package com.redhat.fuse.stock;


import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("stockService")
public class StockServiceImpl implements StockService {
	
	private static ConcurrentHashMap<String, AtomicInteger> warehouse = new ConcurrentHashMap<String, AtomicInteger>();
	
	// This is not concurrent: only one threadh should be working with the same reserverId
	private static HashMap<String, StockReserve> reservations = new HashMap<String, StockReserve>();
	
    static final Logger LOG = LoggerFactory.getLogger(StockServiceImpl.class);
    

    @PostConstruct
    public void init() {
    	
    	// Load stock catalog with "car" and "bike"
    	warehouse.put("car", new AtomicInteger(40));
    	warehouse.put("bike", new AtomicInteger(100));
    	
        LOG.info("The warehouse is initialized with (40) cars and (100) bikes");
    }    
    

/*
 * Useful documentation link: https://camel.apache.org/manual/latest/bean-binding.html
 */

    @Override
 	public String generateReserveId() {

           // Value used for creditID
           UUID uuid = UUID.randomUUID();
           return uuid.toString();
	}

	@Override
	public StockReserve reserveStock(@Header("itemId") String itemId, @Header("reserveId") String reserveId, @Header("quantity") Integer quantity) {
		
		if(!warehouse.containsKey(itemId))
			throw new IllegalStateException("The item " + itemId + " is not in the catalog");
		
		AtomicInteger itemNewAvailableQuantity = warehouse.get(itemId);
		
		if((quantity < 1) || (itemNewAvailableQuantity.get() < quantity))
			throw new IllegalStateException("Invalid quantity or not enough quantity in stock for the item [" + itemId + "]");
		
		if(itemNewAvailableQuantity.addAndGet(-quantity) < 0) {
			// The stock was updated after the previous check: restoring and returning
			itemNewAvailableQuantity.addAndGet(quantity);
			throw new IllegalStateException("No enough quantity for the item " + itemId + " in stock");
			
		};
		
		StockReserve stockReserve = new StockReserve(itemId, reserveId, quantity);
		reservations.put(reserveId, stockReserve);
		
		LOG.info("--- Reserved [{}] elements for item [{}] using reserveId [{}] ---", quantity, itemId, reserveId);
		
		return stockReserve;
	}


	@Override
	public void returnStock( @Header("reserveId") String reserveId) {

		/*  If error occurs during compensation, the Saga service calls the compensation URI again to retry. */
		
		StockReserve stockReserve = reservations.get(reserveId);
		
		if(stockReserve == null) {
			LOG.info("--- reserve [{}] NOT FOUND ---", reserveId);
			//throw new IllegalStateException("ReserveId [" + reserveId + "] not found");
		}
		else {
		
			warehouse.get(stockReserve.getItemId()).addAndGet(stockReserve.getQuantity());
			reservations.remove(reserveId);
			
			LOG.info("--- Returned stock value: [{}] - Reservation [{}] cancelled ---", stockReserve.getQuantity(), reserveId);
		
		}

	}	
	

}

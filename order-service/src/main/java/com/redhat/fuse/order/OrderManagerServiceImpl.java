package com.redhat.fuse.order;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service("orderManagerService")
public class OrderManagerServiceImpl implements OrderManagerService {

    private static AtomicInteger orderIdCounter = new AtomicInteger(1000);
	
    static final Logger LOG = LoggerFactory.getLogger(OrderManagerServiceImpl.class);

    @Override
    public Order newOrder(@Header("itemId") String itemId, @Header("orderId") String orderId, @Header("quantity") Integer quantity) {    
        

    	Order order = new Order(orderId, itemId, quantity);

    	LOG.info("-->>>> orderManagerService: new {} created", order);
    	
    	//return order.getOrderId();
    	return order;
        		
    }

	@Override
	public void cancelOrder(@Header("orderId") String orderId) {
		LOG.info("-->>>> orderManagerService: orderId [{}] CANCELLED", orderId);
		
	}

    @Override
    public void notifyOrder(@Header("orderId") String orderId) {

        LOG.info("Completion task: orderId[{}] notified to customer.", orderId);

    }

	@Override
	public String getNewOrderId() {

		return "ORDER".concat(Integer.toString(orderIdCounter.getAndIncrement()));
		
	}

    

}
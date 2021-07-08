package com.redhat.fuse.stock;

import org.apache.camel.Header;

public interface StockService {

    String generateReserveId();

    StockReserve reserveStock(String itemId, String reserveId, Integer quantity);
    
	void returnStock(String reserveId);

}
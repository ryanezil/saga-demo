package com.redhat.fuse.order;


public interface OrderManagerService {

	public String getNewOrderId();
	
	public Order newOrder(String itemId, String orderId, Integer quantity);
	
	public void cancelOrder(String orderId);

    public void notifyOrder(String orderId);

}
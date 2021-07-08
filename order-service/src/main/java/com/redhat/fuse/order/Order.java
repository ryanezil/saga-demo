package com.redhat.fuse.order;

/**
 * Order entity
 *
 */
public class Order {

    private String orderId;
    private String itemId;
    private Integer quantity;

	public Order() {
	}

	public Order(String orderId, String itemId, Integer quantity) {
		super();
		this.orderId = orderId;
		this.itemId = itemId;
		this.quantity = quantity;
	}



	public String getOrderId() {
		return orderId;
	}



	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}



	public String getItemId() {
		return itemId;
	}



	public void setItemId(String itemId) {
		this.itemId = itemId;
	}




	public Integer getQuantity() {
		return quantity;
	}



	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", itemId=" + itemId + ", quantity=" + quantity + "]";
	}

}
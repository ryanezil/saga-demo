package com.redhat.fuse.stock;

/**
 * Credit entity
 *
 */
public class StockReserve {

	private String itemId;
	private String reserveId;
	private Integer quantity;

    public StockReserve() {
		super();
	}	
	
    public StockReserve(String itemId, String reserveId, Integer quantity) {
		super();
		this.itemId = itemId;
		this.reserveId = reserveId;
		this.quantity = quantity;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getReserveId() {
		return reserveId;
	}

	public void setReserveId(String reserveId) {
		this.reserveId = reserveId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "StockReserve [itemId=" + itemId + ", reserveId=" + reserveId + ", quantity=" + quantity + "]";
	}


}
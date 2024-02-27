package com.thirtythreelabs.systemmodel;

import java.io.Serializable;

public class Warehouse implements Serializable {
	
	private String warehouseId;
	private String warehouseName;
	private int qtyOfHeaders;
	private int qtyOfOrders;
	
	
	public Warehouse(){
		this.warehouseId = "0";
		this.warehouseName = ""; 
		this.qtyOfHeaders = 0;
		this.qtyOfOrders = 0;
		
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public int getQtyOfHeaders() {
		return qtyOfHeaders;
	}

	public void setQtyOfHeaders(int qtyOfHeaders) {
		this.qtyOfHeaders = qtyOfHeaders;
	}

	public int getQtyOfOrders() {
		return qtyOfOrders;
	}

	public void setQtyOfOrders(int qtyOfOrders) {
		this.qtyOfOrders = qtyOfOrders;
	}

}

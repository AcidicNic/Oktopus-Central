package com.thirtythreelabs.systemmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
 
public class Order implements Serializable {
 
	private String orderId;
	private long orderDateMillis;
	private String orderDate;
	private String orderTypeId;
	private String orderTypeName;
	private String orderStatusId;
	private String orderStatusDescription;
	private String orderPriorityId;
	private String orderPriorityName;
	private String orderCustomerId;
	private String orderCustomerName;
	private String orderSalesmanId;
	private String orderSalesmanName;
	private String orderNotes;
	private String orderItemsList;
	
	private int orderTotalLines;
	private int orderLinesPaused;
	private int orderLinesPicked;
	private int orderLinesDiffToPick;
	
	private String orderBillingDate;
	private Boolean orderFutureBilling;
	private Boolean orderFutureBilling2;
	private Boolean orderImportant;
	private String orderRefId;
	
	private String pedHTComId;
	
	private List<Item> itemList;
	
	private boolean showOrder;
	
	private String PedOrdIdEx;
	
	

	public Order() {
	 
		this.orderId = "0";
		this.orderDateMillis = 0;
		this.orderTypeId = "";
		this.orderTypeName = "";
		this.orderStatusId = "";
		this.orderStatusDescription = "";
		this.orderPriorityId = "";
		this.orderPriorityName = "";
		this.orderCustomerId = "";
		this.orderCustomerName = "";
		this.orderSalesmanId = "";
		this.orderSalesmanName = "";
		this.orderNotes = "";
		this.orderItemsList = "";
		
		this.orderTotalLines = 0;
		this.orderLinesPaused = 0;
		this.orderLinesPicked = 0;
		this.orderLinesDiffToPick = 0;
		
		this.itemList = new ArrayList<Item>();
		
		this.showOrder = true;
		
		this.PedOrdIdEx = "";
	}
	
	public Order(
			String orderId, 
			long orderDate, 
			String orderType, 
			int orderTotalLines, 
			String orderSalesmanId, 
			String orderNotes) {
	 
		this.orderId = orderId;
		this.orderDateMillis = orderDate;
		this.orderTypeId = orderType;
		this.orderTotalLines = orderTotalLines;
		this.orderSalesmanId = orderSalesmanId;
		this.orderNotes = orderNotes;
	}
 
	
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public long getOrderDateMillis() {
		return orderDateMillis;
	}

	public void setOrderDateMillis(long orderDateMillis) {
		this.orderDateMillis = orderDateMillis;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(String orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public String getOrderTypeName() {
		return orderTypeName;
	}

	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}

	public String getOrderStatusId() {
		return orderStatusId;
	}

	public void setOrderStatusId(String orderStatusId) {
		this.orderStatusId = orderStatusId;
	}

	public String getOrderStatusDescription() {
		return orderStatusDescription;
	}

	public void setOrderStatusDescription(String orderStatusName) {
		this.orderStatusDescription = orderStatusName;
	}

	public String getOrderPriorityId() {
		return orderPriorityId;
	}

	public void setOrderPriorityId(String orderPriorityId) {
		this.orderPriorityId = orderPriorityId;
	}

	public String getOrderPriorityName() {
		return orderPriorityName;
	}

	public void setOrderPriorityName(String orderPriorityName) {
		this.orderPriorityName = orderPriorityName;
	}

	public String getOrderCustomerId() {
		return orderCustomerId;
	}

	public void setOrderCustomerId(String orderCustomerId) {
		this.orderCustomerId = orderCustomerId;
	}

	public String getOrderCustomerName() {
		return orderCustomerName;
	}

	public void setOrderCustomerName(String orderCustomerName) {
		this.orderCustomerName = orderCustomerName;
	}

	public int getOrderTotalLines() {
		return orderTotalLines;
	}

	public void setOrderTotalLines(int orderTotalLines) {
		this.orderTotalLines = orderTotalLines;
	}

	public String getOrderSalesmanId() {
		return orderSalesmanId;
	}

	public void setOrderSalesmanId(String orderSalesManId) {
		this.orderSalesmanId = orderSalesManId;
	}

	public String getOrderSalesmanName() {
		return orderSalesmanName;
	}

	public void setOrderSalesmanName(String orderSalesManName) {
		this.orderSalesmanName = orderSalesManName;
	}

	public String getOrderNotes() {
		return orderNotes;
	}

	public void setOrderNotes(String orderNotes) {
		this.orderNotes = orderNotes;
	}
	
	public String getOrderItemsList() {
		return orderItemsList;
	}

	public void setOrderItemsList(String orderItemsList) {
		this.orderItemsList = orderItemsList;
	}

	public int getOrderLinesPaused() {
		return orderLinesPaused;
	}

	public void setOrderLinesPaused(int orderLinesPaused) {
		this.orderLinesPaused = orderLinesPaused;
	}

	public int getOrderLinesPicked() {
		return orderLinesPicked;
	}

	public void setOrderLinesPicked(int orderLinesPicked) {
		this.orderLinesPicked = orderLinesPicked;
	}
	
	public int getOrderLinesDiffToPick() {
		return orderLinesDiffToPick;
	}

	public void setOrderLinesDiffToPick(int orderLinesDiffToPick) {
		this.orderLinesDiffToPick = orderLinesDiffToPick;
	}

	public String getOrderBillingDate() {
		return orderBillingDate;
	}

	public void setOrderBillingDate(String orderBillingDate) {
		this.orderBillingDate = orderBillingDate;
	}

	public Boolean getOrderFutureBilling() {
		return orderFutureBilling;
	}

	public void setOrderFutureBilling(Boolean orderFutureBilling) {
		this.orderFutureBilling = orderFutureBilling;
	}
	
	public Boolean getOrderFutureBilling2() {
		return orderFutureBilling2;
	}

	public void setOrderFutureBilling2(Boolean orderFutureBilling2) {
		this.orderFutureBilling2 = orderFutureBilling2;
	}

	public Boolean getOrderImportant() {
		return orderImportant;
	}

	public void setOrderImportant(Boolean orderImportant) {
		this.orderImportant = orderImportant;
	}

	public String getOrderRefId() {
		return orderRefId;
	}

	public void setOrderRefId(String orderRefId) {
		this.orderRefId = orderRefId;
	}
	
	
	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}

	public String toString(){
		return "Order Id: " + this.getOrderId()
				+ " / Date: " + this.getOrderDateMillis();
	}

	public boolean isShowOrder() {
		return showOrder;
	}

	public void setShowOrder(boolean showOrder) {
		this.showOrder = showOrder;
	}

	public String getPedHTComId() {
		return pedHTComId;
	}

	public void setPedHTComId(String pedHTComId) {
		this.pedHTComId = pedHTComId;
	}

	public String getPedOrdIdEx() {
		return PedOrdIdEx;
	}

	public void setPedOrdIdEx(String pedOrdIdEx) {
		PedOrdIdEx = pedOrdIdEx;
	}

	
}
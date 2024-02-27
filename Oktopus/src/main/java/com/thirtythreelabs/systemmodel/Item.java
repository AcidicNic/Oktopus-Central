package com.thirtythreelabs.systemmodel;

import java.io.Serializable;

public class Item implements Serializable{
	
	private String orderId;
	private String itemOrderNumber;
	private String itemLineId;
	private String itemId;
	private String itemDescription;
	private String itemDescriptionHowToRead;
	private String itemHowToRead;
	private String itemFamily;
	private String itemInventory;
	private String itemBrandId;
	private String itemBrandName;
	private String itemBrandNameHowToRead;
	private String itemSupplierId;
	private String itemSupplierName;
	private String itemSupplierNameHowToRead;
	private String itemImage;
	private String itemUnitId;
	private String itemUnitDescription;
	private String itemOrigin;
	private String itemQuantity;
	private String itemStatusId;
	private String itemStatusDescription;
	private String itemQuantityPicked;
	private String itemLastOperatorId;
	private String itemAisle;
	private String itemRow;
	private String itemLevel;
	private Boolean itemIsDiffToPick;
	private String originalItemId;
	
	public Item(){
		this.itemIsDiffToPick = false;
	}
	
	public Item(
			String orderId, 
			String itemLineId, 
			String itemId,
			String itemDescription,
			String itemDescriptionHowToRead,
			String itemHowToRead,
			String itemInventory,
			String itemBrandId,
			String itemBrandName,
			String itemImage,
			String itemUnitId,
			String itemUnitDescription,
			String itemOrigin,
			String itemQuantity,
			String itemStatusId,
			String itemStatusDescription,
			String itemsPicked,
			String itemLastOperatorId,
			String itemAisle,
			String itemRow,
			String itemLevel
			) {

		this.orderId = orderId;
		this.itemLineId = itemLineId;
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.itemHowToRead = itemHowToRead;
		this.itemInventory = itemInventory;
		this.itemBrandId = itemInventory;
		this.itemBrandName = itemBrandName;
		this.itemImage = itemImage;
		this.itemUnitId = itemUnitId;
		this.itemUnitDescription = itemUnitDescription;
		this.itemOrigin = itemOrigin;
		this.itemQuantity = itemQuantity;
		this.itemStatusId = itemStatusId;
		this.itemStatusDescription = itemUnitDescription;
		this.itemQuantityPicked = itemsPicked;
		this.itemLastOperatorId = itemLastOperatorId;
		this.itemAisle = itemAisle;
		this.itemRow = itemRow;
		this.itemLevel = itemLevel;

	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getItemOrderNumber() {
		return itemOrderNumber;
	}

	public void setItemOrderNumber(String itemOrderNumber) {
		this.itemOrderNumber = itemOrderNumber;
	}

	public String getItemLineId() {
		return itemLineId;
	}

	public void setItemLineId(String itemLineId) {
		this.itemLineId = itemLineId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getItemDescriptionHowToRead() {
		return itemDescriptionHowToRead;
	}

	public void setItemDescriptionHowToRead(String itemDescriptionHowToRead) {
		this.itemDescriptionHowToRead = itemDescriptionHowToRead;
	}

	public String getItemHowToRead() {
		return itemHowToRead;
	}

	public void setItemHowToRead(String itemHowToRead) {
		this.itemHowToRead = itemHowToRead;
	}
	
	public String getItemFamily() {
		return itemFamily;
	}

	public void setItemFamily(String itemFamily) {
		this.itemFamily = itemFamily;
	}

	public String getItemInventory() {
		return itemInventory;
	}

	public void setItemInventory(String itemInventory) {
		this.itemInventory = itemInventory;
	}

	public String getItemBrandId() {
		return itemBrandId;
	}

	public void setItemBrandId(String brandId) {
		this.itemBrandId = brandId;
	}

	public String getItemBrandName() {
		return itemBrandName;
	}

	public void setItemBrandName(String brandName) {
		this.itemBrandName = brandName;
	}
	
	public String getItemBrandNameHowToRead() {
		return itemBrandNameHowToRead;
	}

	public void setItemBrandNameHowToRead(String itemBrandNameHowToRead) {
		this.itemBrandNameHowToRead = itemBrandNameHowToRead;
	}

	public String getItemSupplierNameHowToRead() {
		return itemSupplierNameHowToRead;
	}

	public void setItemSupplierNameHowToRead(String itemSupplierNameHowToRead) {
		this.itemSupplierNameHowToRead = itemSupplierNameHowToRead;
	}
	
	public String getItemSupplierId() {
		return itemSupplierId;
	}

	public void setItemSupplierId(String itemSupplierId) {
		this.itemSupplierId = itemSupplierId;
	}

	public String getItemSupplierName() {
		return itemSupplierName;
	}

	public void setItemSupplierName(String itemSupplierName) {
		this.itemSupplierName = itemSupplierName;
	}

	public String getItemImage() {
		return itemImage;
	}

	public void setItemImage(String itemImage) {
		this.itemImage = itemImage;
	}

	public String getItemUnitId() {
		return itemUnitId;
	}

	public void setItemUnitId(String itemUnitId) {
		this.itemUnitId = itemUnitId;
	}

	public String getItemUnitDescription() {
		return itemUnitDescription;
	}

	public void setItemUnitDescription(String itemUnitDescription) {
		this.itemUnitDescription = itemUnitDescription;
	}

	public String getItemOrigin() {
		return itemOrigin;
	}

	public void setItemOrigin(String itemOrigin) {
		this.itemOrigin = itemOrigin;
	}

	public String getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(String itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public String getItemStatusId() {
		return itemStatusId;
	}

	public void setItemStatusId(String itemStatus) {
		this.itemStatusId = itemStatus;
	}

	public String getItemStatusDescription() {
		return itemStatusDescription;
	}

	public void setItemStatusDescription(String itemStatusDescription) {
		this.itemStatusDescription = itemStatusDescription;
	}

	public String getItemQuantityPicked() {
		return itemQuantityPicked;
	}

	public void setItemQuantityPicked(String itemQuantityPicked) {
		this.itemQuantityPicked = itemQuantityPicked;
	}

	public String getItemLastOperatorId() {
		return itemLastOperatorId;
	}

	public void setItemLastOperatorId(String itemLastOperatorId) {
		this.itemLastOperatorId = itemLastOperatorId;
	}

	public String getItemAisle() {
		return itemAisle;
	}

	public void setItemAisle(String itemAisle) {
		this.itemAisle = itemAisle;
	}

	public String getItemRow() {
		return itemRow;
	}

	public void setItemRow(String itemRow) {
		this.itemRow = itemRow;
	}

	public String getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(String itemLevel) {
		this.itemLevel = itemLevel;
	}

	public Boolean getItemIsDiffToPick() {
		return itemIsDiffToPick;
	}

	public void setItemIsDiffToPick(Boolean itemIsDiffToPick) {
		this.itemIsDiffToPick = itemIsDiffToPick;
	}

	public String getOriginalItemId() {
		return originalItemId;
	}

	public void setOriginalItemId(String originalItemId) {
		this.originalItemId = originalItemId;
	}
	
	
	
	

}

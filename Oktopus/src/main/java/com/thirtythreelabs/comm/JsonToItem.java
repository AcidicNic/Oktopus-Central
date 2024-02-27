package com.thirtythreelabs.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.Toast;

import com.thirtythreelabs.oktopus.Oktopus;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.WriteLog;

public class JsonToItem {
	
	private Activity mActivity;
	private WriteLog mLog = new WriteLog();
	
	public JsonToItem(Activity tempmActivity){
		mActivity = tempmActivity;
	}
	
	public List<Item> getItemsFromJson(String json) {
    	List<Item> tempItemsList = new ArrayList<Item>();
        
        try {            
        	
        	//mActivity.setToast(json);
        	//Toast.makeText(mActivity, json, Toast.LENGTH_SHORT).show();
        	mLog.appendLog(json);
        	
        	JSONObject itemsObject = new JSONObject(json);
        	JSONArray tempItems = itemsObject.getJSONArray("Lines");

            for (int i = 0; i < tempItems.length(); i++) {
                JSONObject tempItem = tempItems.getJSONObject(i);
                
                Item mItem = new Item();
                
                mItem.setOrderId("");
                mItem.setItemOrderNumber(String.valueOf(i+1));
                mItem.setItemLineId(tempItem.getString("LineId"));
                String tempItemId = tempItem.getString("ItemId");
                mItem.setItemId(tempItemId);
                mItem.setItemDescription(tempItem.getString("ItemDescription"));
                mItem.setItemDescriptionHowToRead(tempItem.getString("ItemDescriptionHowtoRead"));
                mItem.setItemHowToRead(tempItem.getString("ItemHowToRead"));
                
                char[] chars = tempItemId.toCharArray();
                
                String  tempItemFamily= "";
                if(chars.length > 3){
                	tempItemFamily = tempItemFamily + String.valueOf(chars[0]) + ", ";
                    tempItemFamily = tempItemFamily + String.valueOf(chars[1]) + ", ";
                    tempItemFamily = tempItemFamily + String.valueOf(chars[2]);
                }else{
                	tempItemFamily = "";
                }
                
                mItem.setItemFamily(tempItemFamily);
                
                mItem.setItemInventory(tempItem.getString("ItemInventory").replace(".00", ""));
                mItem.setItemBrandId(tempItem.getString("BrandId"));
                mItem.setItemBrandName(tempItem.getString("BrandName"));
                mItem.setItemSupplierId(tempItem.getString("ItemSupplierId"));
                mItem.setItemSupplierName(tempItem.getString("ItemSupplierName"));
                mItem.setItemImage("http://" + tempItem.getString("ItemImage"));
                mItem.setItemUnitId(tempItem.getString("UnitId"));
                mItem.setItemUnitDescription(tempItem.getString("UnitDescription"));
                mItem.setItemOrigin(tempItem.getString("ItemOrigin"));
                mItem.setItemQuantity(tempItem.getString("ItemQuantity").replace(".00", ""));
                
                
                String tempItemStatusId = tempItem.getString("ItemStatusId");
                if(tempItemStatusId.equalsIgnoreCase("r") || tempItemStatusId.equalsIgnoreCase("f") || tempItemStatusId.equalsIgnoreCase("e")){
                	mItem.setItemStatusId(Config.ITEM_STATUS_READY);
                } else if(tempItemStatusId.equalsIgnoreCase("p")){
                	mItem.setItemStatusId(Config.ITEM_STATUS_PAUSED);
                } else if(tempItemStatusId.equalsIgnoreCase("k")){
                	mItem.setItemStatusId(Config.ITEM_STATUS_PICKED);
                } else if(tempItemStatusId.equalsIgnoreCase("i")){
                	mItem.setItemStatusId(Config.ITEM_STATUS_READY);
                }
                
                
                mItem.setItemStatusDescription(tempItem.getString("ItemStatusDescription"));
                mItem.setItemQuantityPicked(tempItem.getString("ItemQuantityPicked").replace(".00", ""));
                mItem.setItemLastOperatorId(tempItem.getString("ItemLastOperatorId"));
        		mItem.setItemAisle(tempItem.getString("ItemXCoord"));
        		mItem.setItemRow(tempItem.getString("ItemYCoord"));
        		mItem.setItemLevel(tempItem.getString("ItemZCoord"));
        		
        		mItem.setItemBrandNameHowToRead(tempItem.getString("BrandNameHowToRead"));
        		mItem.setItemSupplierNameHowToRead(tempItem.getString("ItemsSupplierNameHowToRead"));
        		String tempItemDifficultToPick = tempItem.getString("ItemDifficultToPick");
        		
        		if(tempItemDifficultToPick.equalsIgnoreCase("y") || tempItemDifficultToPick.equalsIgnoreCase("s")){
        			mItem.setItemIsDiffToPick(true);
        		}else{
        			mItem.setItemIsDiffToPick(false);
        		}
        		
                tempItemsList.add(mItem);
            }
            
        }
        catch (JSONException e) {
        	//Log.e(TAG, "Failed to parse JSON.", e);
        	// mActivity.setToast("Error #3: Failed to parse JSON.");
        	
        }
        
        return tempItemsList;
    }
}

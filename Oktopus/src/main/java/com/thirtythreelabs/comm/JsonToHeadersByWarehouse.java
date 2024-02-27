package com.thirtythreelabs.comm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.thirtythreelabs.systemmodel.Order;
import com.thirtythreelabs.systemmodel.Warehouse;
import com.thirtythreelabs.util.WriteLog;

public class JsonToHeadersByWarehouse {
	
	private Activity mActivity;
	private WriteLog mLog = new WriteLog();
	
	public JsonToHeadersByWarehouse(Activity tempActivity){
		mActivity = tempActivity;
	}
	
	public List<Warehouse> getOrdersFromJson(String json) {
    	List<Warehouse> tempOrderList = new ArrayList<Warehouse>();
        
        try {            
        	mLog.appendLog(json);
        	
        	JSONObject ordersObject = new JSONObject(json);
        	JSONArray tempOrders = ordersObject.getJSONArray("HeadersByWarehouse");
        	
            for (int i = 0; i < tempOrders.length(); i++) {
                JSONObject tempOrder = tempOrders.getJSONObject(i);
                
                Warehouse mWarehouse = new Warehouse();
                
                mWarehouse.setWarehouseId(tempOrder.getString("WareHouseID"));
                mWarehouse.setQtyOfHeaders(tempOrder.getInt("QtyOfHeaders"));
                mWarehouse.setQtyOfOrders(tempOrder.getInt("QtyOfOrders"));
                
	            tempOrderList.add(mWarehouse);
            } 
        }
        catch (JSONException e) {
            Log.e("JsonToOrder", "Failed to parse JSON.", e);
        	
        	// mainActivity.setToast("Error #3: Failed to parse JSON.");
        }
        
        return tempOrderList;
    }
}

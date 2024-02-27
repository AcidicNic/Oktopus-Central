package com.thirtythreelabs.comm;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.thirtythreelabs.oktopus.R;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.systemmodel.Operator;
import com.thirtythreelabs.systemmodel.Warehouse;
import com.thirtythreelabs.util.WriteLog;

public class JsonToOperator {
	
	private Activity mActivity;
	private WriteLog mLog = new WriteLog();
	
	public JsonToOperator(Activity tempActivity){
		mActivity = tempActivity;
	}
	
	@SuppressWarnings("null")
	public Operator getOperatorFromJson(String json) {
    	Operator mOperator = new Operator();
        
        try {            
        	mLog.appendLog(json);
        	
        	List <Warehouse> tempOperatorWarehouses =  new ArrayList<Warehouse>();;
        	JSONObject operatorObject = new JSONObject(json);
        	
        	
    		mOperator.setLoginError(operatorObject.getString("Error"));
    		mOperator.setLoginErrorMessage(operatorObject.getString("ErrorMessage"));
       	
    		mOperator.setOperatorId(operatorObject.getString("OperatorId"));
        	mOperator.setOperatorName(operatorObject.getString("OperatorName").trim());
        	
        	JSONArray tempWareHouses = operatorObject.getJSONArray("OpeWhareouse");

            for (int i = 0; i < tempWareHouses.length(); i++) {
                JSONObject tempItem = tempWareHouses.getJSONObject(i);
                Warehouse tempWarehouseId = new Warehouse();
                tempWarehouseId.setWarehouseId(tempItem.getString("WareHouseID"));
                tempWarehouseId.setWarehouseName(mActivity.getString(R.string.WAREHOUSE) + " " + tempItem.getString("WareHouseID"));
                tempOperatorWarehouses.add(tempWarehouseId);
                
            }
            
            mOperator.setOperatorWarehouses(tempOperatorWarehouses);

        }
        catch (JSONException e) {
            Log.e("JsonToOperator", "Failed to parse JSON.", e);

        }
        
        return mOperator;
    }
}

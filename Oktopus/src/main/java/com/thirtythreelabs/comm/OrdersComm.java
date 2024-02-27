package com.thirtythreelabs.comm;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.thirtythreelabs.oktopus.OrdersActivity;
import com.thirtythreelabs.systemmodel.Order;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.WriteLog;




public class OrdersComm {
	private Activity mActivity;
	private Context mContext;
	private String mLang;
	private boolean mOnline;
	private String mCompanyId;
	
	private JsonToOrder mJsonToOrder;
	
	private WriteLog mLog = new WriteLog();
	private String TAG = "OrdersComm";
	
	public static final String GET_ORDERS_ACTION = "com.thirtythreelabs.oktopus.GET_ORDERS_ACTION"; 
	
	private static final String GET_ORDERS_URI = Config.URL + "getheadersv3/";
	
	
	public OrdersComm (Context tempContext, Activity tempActivity, String tempLang, boolean tempOnline, String tempCompanyId){
		mActivity = tempActivity;
		mContext = tempContext;
		mLang = tempLang;
		mOnline = tempOnline;
		mCompanyId = tempCompanyId;
		
		mJsonToOrder = new JsonToOrder(tempActivity);
	}
	
	
	
	public void getOrders(String pWarehouseId, String pHeaderStatusId) {		
		//Create the search request 
		
		if(mOnline){
			try{			
				
				String url = String.format(GET_ORDERS_URI);
				HttpPost postRequest = new HttpPost(new URI(url));
				
				String mJson;
				JSONObject object = new JSONObject();
		        try {
		            object.put("CompanyId", mCompanyId);
		            object.put("WareHouseId", pWarehouseId);
		            
		            String mDate = (String) android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date());
		            
		            object.put("HeaderDate", mDate);
		            object.put("HeaderStatusId", pHeaderStatusId);
		            
		            mLog.appendLog(object.toString());
		        } catch (Exception ex) {
		        	// setToast("Error: " + ex);
		        }
		        
		        
		        mJson = object.toString();
		        StringEntity entity = new StringEntity(mJson);
		        
		        Log.d(TAG , mJson);
		        
		        postRequest.setEntity(entity);
		        postRequest.setHeader("Content-Type", "application/json");
		        
				RestTask task = new RestTask(mContext, GET_ORDERS_ACTION); 
				
				task.execute(postRequest);
				
			} catch (Exception e) {
				// e.printStackTrace(); 
				// Toast.makeText(this, "Error #1: " + e, Toast.LENGTH_LONG).show();
			}
			
		}else{
			String mJson;
			if(mLang == "es" || mLang == "espa�ol"){
				mJson = "{\"Headers\":[{\"HeaderId\":\"27875\",\"HeaderDate\":\"2013-05-17T00:00:00\",\"HeaderStatusId\":\"F\",\"HeaderStatusDescription\":\"\",\"HeaderPriorityId\":0,\"HeaderPriorityDescription\":\"\",\"DeliveryTypeId\":0,\"DeliveryTypeDescription\":\"MOSTRADOR\",\"SalesmanId\":86,\"SalesmanName\":\"GABRIEL BORGEAUD\",\"CustomerId\":78883,\"CustomerName\":\"ROSTAN LTDA.\",\"HeaderNotes\":\"\",\"TotalLines\":11,\"TotalLinesReadyForPickup\":10,\"WareHouseId\":0},{\"HeaderId\":\"27876\",\"HeaderDate\":\"2013-05-17T00:00:00\",\"HeaderStatusId\":\"S\",\"HeaderStatusDescription\":\"\",\"HeaderPriorityId\":0,\"HeaderPriorityDescription\":\"\",\"DeliveryTypeId\":0,\"DeliveryTypeDescription\":\"MOSTRADOR\",\"SalesmanId\":70,\"SalesmanName\":\"VENTAS WEB\",\"CustomerId\":79863,\"CustomerName\":\"RIBESOL S.A.\",\"HeaderNotes\":\"LEVANTAMOS AHI EN LA MAÑANA\",\"TotalLines\":5,\"TotalLinesReadyForPickup\":5,\"WareHouseId\":0}],\"Error\":\"\",\"ErrorMessage\":\"\"}";
			}else{	
				mJson = "{\"Headers\":[{\"HeaderId\":\"27875\",\"HeaderDate\":\"2013-05-17T00:00:00\",\"HeaderStatusId\":\"F\",\"HeaderStatusDescription\":\"\",\"HeaderPriorityId\":0,\"HeaderPriorityDescription\":\"\",\"DeliveryTypeId\":0,\"DeliveryTypeDescription\":\"MOSTRADOR\",\"SalesmanId\":86,\"SalesmanName\":\"GABRIEL BORGEAUD\",\"CustomerId\":78883,\"CustomerName\":\"ROSTAN LTDA.\",\"HeaderNotes\":\"\",\"TotalLines\":11,\"TotalLinesReadyForPickup\":10,\"WareHouseId\":0},{\"HeaderId\":\"27876\",\"HeaderDate\":\"2013-05-17T00:00:00\",\"HeaderStatusId\":\"S\",\"HeaderStatusDescription\":\"\",\"HeaderPriorityId\":0,\"HeaderPriorityDescription\":\"\",\"DeliveryTypeId\":0,\"DeliveryTypeDescription\":\"MOSTRADOR\",\"SalesmanId\":70,\"SalesmanName\":\"VENTAS WEB\",\"CustomerId\":79863,\"CustomerName\":\"RIBESOL S.A.\",\"HeaderNotes\":\"LEVANTAMOS AHI EN LA MAÑANA\",\"TotalLines\":5,\"TotalLinesReadyForPickup\":5,\"WareHouseId\":0}],\"Error\":\"\",\"ErrorMessage\":\"\"}";
			}
			
			List<Order> tempOrdersList = mJsonToOrder.getOrdersFromJson(mJson);
			
			((OrdersActivity) mActivity).populateOrders(tempOrdersList);
			
		}
	}

}

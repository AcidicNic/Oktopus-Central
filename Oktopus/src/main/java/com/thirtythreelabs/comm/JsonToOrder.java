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

import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.systemmodel.Order;
import com.thirtythreelabs.util.WriteLog;

public class JsonToOrder {
	
	private static final String TAG = null;
	private Activity mActivity;
	private WriteLog mLog = new WriteLog();
	
	public JsonToOrder(Activity tempActivity){
		mActivity = tempActivity;
	}
	
	public List<Order> getOrdersFromJson(String json) {
    	List<Order> tempOrderList = new ArrayList<Order>();
        
        try {            
        	mLog.appendLog(json);
        	
        	JSONObject ordersObject = new JSONObject(json);
        	JSONArray tempOrders = ordersObject.getJSONArray("Headers");
        	
            for (int i = 0; i < tempOrders.length(); i++) {
                JSONObject tempOrder = tempOrders.getJSONObject(i);
                
                Order mOrder = new Order();
                 
                /* mJson = "{\"Headers\": [{\"HeaderId\": \"83346\",\"HeaderDate\": \"2013-01-18\",\"StatusId\": \"M\",\"StatusDescription\": \"\",
                \"PriorityId\": 0,\"PriorityName\": \"HIGH\",\"DeliverTypeId\": 2,\"DeliverTypeName\": \"SHIP\",\"SalesManId\": 83,\"SalesName\": " +
                "\"John\",\"CustomerId\": 76643,\"CustomerName\": \"John M.\",\"HeaderNotes\": \"URGENT\",\"HeaderUltNroLin\": 5}, {\"HeaderId\": " +
                "\"83347\",\"HeaderDate\": \"2013-01-18\",\"StatusId\": \"M\",\"StatusDescription\": \"\",\"PriorityId\": 0,\"PriorityName\": \"MEDIUM\"," +
                "\"DeliverTypeId\": 2,\"DeliverTypeName\": \"PICKUP\",\"SalesManId\": 83,\"SalesName\": \"Greg\",\"CustomerId\": 76643,\"CustomerName\": " +
                "\"Sally M.\",\"HeaderNotes\": \"\",\"HeaderUltNroLin\": 3}]}";
                */
                
                JSONArray tempHeaderListArticles = tempOrders.getJSONObject(i).getJSONArray("HeaderListArticles");
                for (int o = 0; o < tempHeaderListArticles.length(); o++) {
                	JSONObject tempHeaderListArticle = tempHeaderListArticles.getJSONObject(o);
                	
                	
                	String itemId = tempHeaderListArticle.getString("ItemId");
                	String tempFamily = "<font color='red'>" + itemId.substring(0, 3) + "</font> ";
        			String tempItemId = itemId.substring(3);
        			
                	// Log.d(TAG, tempHeaderListArticle.getString("ItemId"));
                	Item tempItem = new Item();
                	tempItem.setOriginalItemId(tempHeaderListArticle.getString("ItemId"));
                	tempItem.setItemFamily(tempFamily);
                	tempItem.setItemId(tempItemId);
                	tempItem.setItemUnitId(tempHeaderListArticle.getString("UnitId"));
                	tempItem.setItemQuantity(tempHeaderListArticle.getString("ItemQuantityPicked"));
                	tempItem.setItemQuantityPicked(tempHeaderListArticle.getString("ItemQuantity"));
                	                	
                	mOrder.getItemList().add(tempItem);
                	
                }
                    
                
                String formattedDate = "";
                try {
					
                	Date tempDate = (Date) new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(tempOrder.getString("HeaderDate"));
                	formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(tempDate.getTime()); 
				
                } catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                mOrder.setOrderDate(formattedDate);
                
                mOrder.setPedOrdIdEx(tempOrder.getString("PedOrdIdEx"));
                
                Boolean tempFutureBilling = false;
                Boolean tempFutureBilling2 = false;
                String billingDateFormattedDate = "";
                
                try {
					
                	Date tempDate = (Date) new SimpleDateFormat("yyyy-MM-dd").parse(tempOrder.getString("HeaderDateFact"));
                	billingDateFormattedDate = new SimpleDateFormat("dd/MM/yyyy").format(tempDate.getTime()); 
                	Date today = new Date();
                	
                	if(today.before(tempDate) && mOrder.getPedOrdIdEx().equals("0")){
                		tempFutureBilling = true;
                	}
                	if(today.before(tempDate) && !mOrder.getPedOrdIdEx().equals("0")){
                		tempFutureBilling2 = true;
                	}
				
                } catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                
                
                if(tempFutureBilling){
                	mOrder.setOrderId(tempOrder.getString("HeaderId") + " (Facturación futura)");
                }else{
                	mOrder.setOrderId(tempOrder.getString("HeaderId"));
                }
                
                mOrder.setOrderBillingDate(billingDateFormattedDate);
                mOrder.setOrderFutureBilling(tempFutureBilling);
                mOrder.setOrderFutureBilling2(tempFutureBilling2);
                mOrder.setOrderDate(formattedDate);
                
                Date orderDate = null;
                long orderDateMilliseconds = 0;
                
                try {
        			orderDate = (Date) new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(tempOrder.getString("HeaderDate"));
        			orderDateMilliseconds = orderDate.getTime();
        			
        		} catch (ParseException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
                
                
                
                mOrder.setOrderDateMillis(orderDateMilliseconds);
                mOrder.setOrderTypeId(tempOrder.getString("DeliveryTypeId"));
                
                mOrder.setOrderStatusId(tempOrder.getString("HeaderStatusId"));
                mOrder.setOrderStatusDescription(tempOrder.getString("HeaderStatusDescription"));
                mOrder.setOrderPriorityId(tempOrder.getString("HeaderPriorityId"));
                mOrder.setOrderPriorityName(tempOrder.getString("HeaderPriorityDescription"));
                mOrder.setOrderCustomerId(tempOrder.getString("CustomerId"));
                mOrder.setOrderCustomerName(tempOrder.getString("CustomerName"));
                
                mOrder.setOrderSalesmanId(tempOrder.getString("SalesmanId"));
                mOrder.setOrderSalesmanName(tempOrder.getString("SalesmanName"));
                mOrder.setOrderNotes(tempOrder.getString("HeaderNotes"));
                
                mOrder.setOrderTotalLines(tempOrder.getInt("TotalLines"));
                mOrder.setOrderLinesDiffToPick(tempOrder.getInt("HeaderItemsDiffToPick"));
                mOrder.setOrderLinesPicked(tempOrder.getInt("TotalLinesPicked"));
                mOrder.setOrderLinesPaused(tempOrder.getInt("TotalLinesPaused"));
                
                if(tempOrder.has("PedHTComId")){
                	mOrder.setPedHTComId(tempOrder.getString("PedHTComId"));
                }
                
                String myString =  tempOrder.getString("HeaderListArticles");
            	
                mOrder.setOrderItemsList(myString);
                
                Boolean tempOrderImportant = false;
                String tempHeaderSolMer = tempOrder.getString("HeaderSolMer");
                
                
                
                if(tempHeaderSolMer.equals("S")){
                	tempOrderImportant = true;
                	mOrder.setOrderTypeName("Enviar a Sucursal A.C.U.");
                	mOrder.setOrderRefId(tempOrder.getString("HeaderRefId"));
                }else{
                	mOrder.setOrderRefId("");
                	mOrder.setOrderTypeName(tempOrder.getString("DeliveryTypeDescription"));
                }
                mOrder.setOrderImportant(tempOrderImportant);

	            tempOrderList.add(mOrder);
            } 
        }
        catch (JSONException e) {
            Log.e("JsonToOrder", "Failed to parse JSON.", e);
        	
        	// mainActivity.setToast("Error #3: Failed to parse JSON.");
        }
        
        return tempOrderList;
    }
}

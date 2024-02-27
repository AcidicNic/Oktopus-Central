package com.thirtythreelabs.oktopus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thirtythreelabs.comm.JsonToItem;
import com.thirtythreelabs.oktopus.R;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.util.Config;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class TestActivity extends Activity {
	private JsonToItem mJsonToItem = new JsonToItem(this);
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
      
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);  
         
          
        
                String serverURL = "http://192.168.0.196:8899/Oktopus/getlinesv2/";
                 
                // Use AsyncTask execute Method To Prevent ANR Problem
                new LongOperation().execute(serverURL);
                
                
               
          
    }
      
      
    // Class with extends AsyncTask class
     
    private class LongOperation  extends AsyncTask<String, Void, Void> {
          
        // Required initialization
         
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(TestActivity.this);
        int sizeData = 0;  
         
        
         
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.
              
            //Start Progress Dialog (Message)
            
            Dialog.setMessage("Please wait..");
            Dialog.show();
             
            
             
        }
  
        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
             
            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;
    
                 // Send data 
                try
                { 
                   
                   // Defined URL  where to send data
                   URL url = new URL(urls[0]);
                      
                  // Send POST data request
        
                  URLConnection conn = url.openConnection(); 
                  conn.setDoOutput(true); 
                  OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
                  wr.write( "" ); 
                  wr.flush(); 
               
                  // Get the server response 
                    
                  reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                  StringBuilder sb = new StringBuilder();
                  String line = null;
                 
                    // Read Server Response
                    while((line = reader.readLine()) != null)
                        {
                               // Append server response in string
                               sb.append(line + "");
                        }
                     
                    // Append Server Response To Content String 
                    
                   Content = sb.toString();
                }
                catch(Exception ex)
                {
                    Error = ex.getMessage();
                }
                finally
                {
                    try
                    {
          
                        reader.close();
                    }
        
                    catch(Exception ex) {}
                }
             
            /*****************************************************/
            return null;
        }
          
        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.
              
            // Close progress dialog
            Dialog.dismiss();
              
            if (Error != null) {
                  
                  
            } else {
               
                // Show Response Json On Screen (activity)
                 
             /****************** Start Parse Response JSON Data *************/
                 
                String OutputData = "";
                JSONObject jsonResponse;
                       
               
                
                
                List<Item> tempItemsList = new ArrayList<Item>();
                
                try {            
                	
                	//mActivity.setToast(json);
                	//Toast.makeText(mActivity, json, Toast.LENGTH_SHORT).show();
                	//mLog.appendLog(json);
                	
                	JSONObject itemsObject = new JSONObject(Content);
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
                    
                    Log.d("TAG", tempItemsList.get(0).getItemBrandName());
                    int c = tempItemsList.size();
                    Log.d("TAG", String.valueOf(c));
                    
                }
                catch (JSONException e) {
                	//Log.e(TAG, "Failed to parse JSON.", e);
                	// mActivity.setToast("Error #3: Failed to parse JSON.");
                	
                }
   
                  
             }
        }
          
    }
     
}
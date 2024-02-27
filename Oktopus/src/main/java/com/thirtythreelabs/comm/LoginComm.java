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

import com.thirtythreelabs.oktopus.OrdersActivity;
import com.thirtythreelabs.systemmodel.Order;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.WriteLog;




public class LoginComm {
	private Activity mActivity;
	private Context mContext;
	private String mLang;
	private boolean mOnline;
	
	private JsonToOperator mJsonToOperator;
	
	private WriteLog mLog = new WriteLog();
	
	public static final String GET_LOGIN_ACTION = "com.thirtythreelabs.oktopus.GET_LOGIN_ACTION"; 
	
	private static final String GET_LOGIN_URI = Config.URL + "loginv1/";
	
	public LoginComm (Context tempContext, Activity tempActivity, String tempLang, boolean tempOnline){
		mActivity = tempActivity;
		mContext = tempContext;
		mLang = tempLang;
		mOnline = tempOnline;
		
		mJsonToOperator = new JsonToOperator(tempActivity);
	}
	
	
	
	public void loginOperator(String pCompanyId, String pOperatorLogin, String pOperatorPassword) {		
		//Create the search request 
		
		if(mOnline){
			try{			
				
				String url = String.format(GET_LOGIN_URI);
				HttpPost postRequest = new HttpPost(new URI(url));
				
				String mJson;
				JSONObject object = new JSONObject();
		        try {
		            object.put("CompanyId", pCompanyId);
		            object.put("OperatorLogin", pOperatorLogin);
		            object.put("OperatorPassword", pOperatorPassword);
		            
		            mLog.appendLog(object.toString());
		        } catch (Exception ex) {
		        	// setToast("Error: " + ex);
		        }
		        
		        
		        mJson = object.toString();
		        StringEntity entity = new StringEntity(mJson);
		        
		        postRequest.setEntity(entity);
		        postRequest.setHeader("Content-Type", "application/json");
		        
				RestTask task = new RestTask(mContext, GET_LOGIN_ACTION); 
				
				task.execute(postRequest);
				
			} catch (Exception e) {
				// e.printStackTrace(); 
				// Toast.makeText(this, "Error #1: " + e, Toast.LENGTH_LONG).show();
			}
			
		}else{

			
		}
	}

}

package com.thirtythreelabs.comm;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;


public class RestTask extends AsyncTask<HttpUriRequest, Void, String> {
	
	public static final String HTTP_RESPONSE = "httpResponse";
	private Context mContext; 
	private HttpClient mClient; 
	private String mAction;
	
	private SharedPreferences mSharedPrefs;

	public RestTask(Context context, String action) { 
		mContext = context;
		mAction = action;
		mClient = new DefaultHttpClient();
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public RestTask(Context context, String action, HttpClient client) { 
		mContext = context;
		mAction = action;
		mClient = client;
	}

	@Override
	protected String doInBackground(HttpUriRequest... params) {
		try{
			HttpUriRequest request = params[0];
			HttpResponse serverResponse = mClient.execute(request);
			BasicResponseHandler handler = new BasicResponseHandler(); 
			String response = handler.handleResponse(serverResponse); 
			return response;
		} catch (Exception e) { 
			// Toast.makeText(mContext, "Error #4: " + e, Toast.LENGTH_SHORT).show();
        	
			// e.printStackTrace(); 
			return null;
		} 
	}
	
	@Override
	protected void onPostExecute(String result) {
		Intent intent = new Intent(mAction); 
		if(mAction.equals(ItemsComm.GET_ITEMS_ACTION)){
			SharedPreferences.Editor editor = mSharedPrefs.edit();
			editor.putString(HTTP_RESPONSE, result);
			editor.commit();
		}else{
			intent.putExtra(HTTP_RESPONSE, result); //Broadcast the completion 
		}

		
		mContext.sendBroadcast(intent);
	} 
}
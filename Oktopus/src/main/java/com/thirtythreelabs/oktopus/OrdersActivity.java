package com.thirtythreelabs.oktopus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;



import com.thirtythreelabs.adapters.OrderAdapter;
import com.thirtythreelabs.bluetooth.AudioStateManager;
import com.thirtythreelabs.bluetooth.BluetoothHeadsetUtils;
import com.thirtythreelabs.comm.JsonToOrder;
import com.thirtythreelabs.comm.OrdersComm;
import com.thirtythreelabs.comm.RestTask;
import com.thirtythreelabs.flowmodel.DataModule;
import com.thirtythreelabs.flowmodel.Flow;
import com.thirtythreelabs.flowmodel.FlowModule;
import com.thirtythreelabs.flowmodel.Module;
import com.thirtythreelabs.flowmodel.Phrase;
import com.thirtythreelabs.systemmodel.Operator;
import com.thirtythreelabs.systemmodel.Order;
import com.thirtythreelabs.ttsstt.Stt;
import com.thirtythreelabs.ttsstt.Tts;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.WriteLog;
import com.thirtythreelabs.util.readXmlResource;


public class OrdersActivity extends Activity implements OnClickListener, Stt.OnSttResults, Tts.OnTtsResults, Bicycle.OnBicycleResults, OnItemClickListener{

	private WriteLog mLog = new WriteLog();
	
	private Stt mStt = new Stt();
	private Tts mTts = new Tts();
	private boolean sttReady = false;
	private boolean ttsReady = false;
	
	private boolean mOnline = true;
	
	
	private OrderAdapter mOrderAdapter;
	private List<Order> mOrdersList;
	private int currentOrder = 0;
	
	
	private OrdersComm mOrdersComm;
	private JsonToOrder mJsonToOrder = new JsonToOrder(this);
	
	private Flow mOktopusFlow;
	private Bicycle mBicycle = new Bicycle();
	private Module mModule;
	private Module mReStartModule;
	private List<Phrase> mReStartSayList;
	
	private Operator mOperator;
	private String mWarehouseId;
	private String mOperatorName;
	
	private String sayAgainString = "";
	
	private ImageButton mPreferencesButton;
	private Button mMuteButton;
	private Button mExitButton;
	private Button mViewClosedOrdersButton;
	
	private SharedPreferences mSharedPrefs;
	
	private boolean mIsSistemMute = false;	
	
	private BluetoothHelper mBluetoothHelper;
	
	private AudioStateManager mAudioStateManager;
	
	
	private ProgressDialog mProgressDialog;
	
	private Dialog mDialog;
	
	private CountDownTimer mCountDownTimer;
	
	// private static WifiLock wifiLock;
	
	private boolean isWaitingForOrders = false;
	
	private final Handler mHandler = new Handler();
	private boolean mIsRefreshing;
	private static final int REFRESH_TIME = 1000*60;
	
	
	@Override
	public void onCreate(Bundle mBundle) {
		super.onCreate(mBundle);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.orders);
				
		ListView mOrdersListView = (ListView) findViewById(R.id.listview);
		mOrderAdapter = new OrderAdapter(this, this);
		mOrdersListView.setAdapter(mOrderAdapter);
        
		mOperator = (Operator)getIntent().getSerializableExtra("operator");
		mWarehouseId = mOperator.getOperatorCurrentWarehoueseId();
		mOperatorName = mOperator.getOperatorName();
		
		mBluetoothHelper = new BluetoothHelper(this);
		
		mStt.startSpeech(this, this);
		mTts.startTts(this, this);
		
		mPreferencesButton =  (ImageButton) findViewById(R.id.preferences);
		mPreferencesButton.setOnClickListener(this);
		
		mMuteButton = (Button) findViewById(R.id.muteButton);
		mMuteButton.setOnClickListener(this);
		
		mExitButton = (Button) findViewById(R.id.exitButton);
		mExitButton.setOnClickListener(this);
		
		mViewClosedOrdersButton = (Button) findViewById(R.id.closedOrders);
		mViewClosedOrdersButton.setOnClickListener(this);
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		mDialog = new Dialog(this);
	    
	    /*
	    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiLock wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "LockTag");
        wifiLock.acquire();
        */
	}
	
	private final Runnable mTimerRunnable = new Runnable() {
		@Override
        public void run() {
			mOrderAdapter.notifyDataSetChanged();
            if(mIsRefreshing){
            	mHandler.postDelayed(this, REFRESH_TIME);
            }
        }
    };
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter(OrdersComm.GET_ORDERS_ACTION)); 
		mBluetoothHelper.start();
		
		Boolean voiceOnOff = mSharedPrefs.getBoolean("voiceOnOff", true);
		
		if(voiceOnOff){
			mMuteButton.setEnabled(true);
		}else{
			mMuteButton.setEnabled(false);
			mute();
		}
		
		if(mProgressDialog != null) {
			mProgressDialog.dismiss(); 
		}
		
		if(mDialog != null) {
			mDialog.dismiss(); 
		}
		
		if(isWaitingForOrders){
			isWaitingForOrders = false;
			mOrdersComm.getOrders(mWarehouseId, Config.ORDER_STATUS_READY);
		}
		
		mTts.resumeTts();
		
		mIsRefreshing = true;
		mHandler.postDelayed(mTimerRunnable, REFRESH_TIME);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if(mCountDownTimer != null){
			isWaitingForOrders = true;
			mCountDownTimer.cancel();
		}else{
			isWaitingForOrders = false;
		}
		
		unregisterReceiver(receiver);
		mBluetoothHelper.stop();
		mTts.pauseTts();
		
		mIsRefreshing = false;
		mHandler.removeCallbacks(mTimerRunnable);
	}
	
	@Override
    public void onDestroy() {        
        super.onDestroy();
        /*
        if(wifiLock.isHeld()){
    		wifiLock.release();
    	}
        */
        mTts.destroyTts();
		mStt.destroyStt();
	}
        
	
	public boolean isBluethoothOn(){
		return mBluetoothHelper.isOnHeadsetSco();
	}

	public void setToast(String myToast){
		mLog.appendLog(myToast);
		Toast.makeText(this, myToast, Toast.LENGTH_SHORT).show();
    }


	@Override
	public void speak(String string) {
		// TODO Auto-generated method stub
		
	}


	public void sttReady() {
		// setToast("sttReady");
		sttReady = true;
		checkSystemReady();
	}


	public void ttsReady() {
		// setToast("ttsReady");
		ttsReady = true;
		checkSystemReady();
		
	}

	public void checkSystemReady(){
		if(ttsReady && sttReady){
			// setToast("System Ready");
			// mOrdersComm.getOrders();
			
			getOktopusFlow();
		}
	}
	
	
	@Override
	public void ttsOnDone(String pUtteranceId) {
		
		if(mIsSistemMute == false){
			mBicycle.processSayCallback(mModule);
		}
	}



	
	
	@Override
	public void sttResults(List<String> pData, float[] pConfidence) {
		mBicycle.processListenToCallback(mModule, pData);
		
	}
	
	
	private BroadcastReceiver receiver = new BroadcastReceiver() { 
		@Override
		public void onReceive(Context context, Intent intent) { 

			String mJson = intent.getStringExtra(RestTask.HTTP_RESPONSE); 
			
			List<Order> tempOrdersList = mJsonToOrder.getOrdersFromJson(mJson);
			
			populateOrders(tempOrdersList);
			
			
            
		} 
	};
	
	
	

	public void populateOrders(List<Order> pOrdersList){
		
		mOrdersList = pOrdersList;
		
		mOrderAdapter.clear();
        
        int tempTotalLines = 0;
        for (Order tempOrder : mOrdersList) {
        	mOrderAdapter.add(tempOrder);
            tempTotalLines++;
        }
        
        TextView mTotalOrders = (TextView) findViewById(R.id.totalPedidos);
        mTotalOrders.setText(String.valueOf(tempTotalLines));
        
        TextView mFloor = (TextView) findViewById(R.id.operatorAndFloor);
        mFloor.setText(getString(R.string.app_name) + " | " + getString(R.string.FLOOR) + " " + mWarehouseId + " | " + mOperatorName);
        
        enableButtons();
        
        if(mProgressDialog != null) {
			mProgressDialog.dismiss(); 
		}
        
        if(tempTotalLines == 0){
        	// mBicycle.processDataCallback((DataModule) mModule, "false");
        	
        	waitForOrders();
        	
        }else{
        	
        	mOrderAdapter.setSelectedItem(currentOrder);
        	mOrderAdapter.setTotalLineas(tempTotalLines);
        	mOrderAdapter.notifyDataSetChanged();
        	
        	mBicycle.processDataCallback((DataModule) mModule, "true");
        }
        
    }
	
	private void waitForOrders(){

		if(mProgressDialog != null) {
			mProgressDialog.dismiss(); 
		}
		
		if(mDialog != null){
			mDialog.dismiss();
		}
		
		mDialog.setContentView(R.layout.wait_dialog);
		mDialog.setTitle(getString(R.string.WAITING_FOR_ORDERS));
		mDialog.setCancelable(false);
		
		
		Button okButton = (Button) mDialog.findViewById(R.id.dialogButtonOK);
		Button cancelButton = (Button) mDialog.findViewById(R.id.dialogButtonCancel);
		Button closedOrdersButton = (Button) mDialog.findViewById(R.id.closedOrdersButton);
		
		final TextView text = (TextView) mDialog.findViewById(R.id.inputText);
		
		mCountDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            	text.setText(getString(R.string.RETRYING_IN) + ": " + String.valueOf((millisUntilFinished/1000)));
            }

            public void onFinish() {
            	mDialog.dismiss();
            	mBicycle.processDataCallback((DataModule) mModule, "false");
            }
            
         }.start();
         
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCountDownTimer.cancel();
	            mDialog.dismiss();
	            mBicycle.processDataCallback((DataModule) mModule, "false");

			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCountDownTimer.cancel();
				mDialog.dismiss();
				gotoChooseWarehouseActivity();
			}
		});
		
		closedOrdersButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCountDownTimer.cancel();
				mDialog.dismiss();
				gotoClosedOrdersActivity();
			}
		});
		
		mDialog.show();

	}
	
	private void getOktopusFlow(){
		
		Serializer mSerializer = new Persister();
		// File source = new File("/sdcard/download/Orders.xml");
		
		//File source = new File(Environment.getExternalStorageDirectory(), "items.xml");

		try {
			
			mOktopusFlow = mSerializer.read(Flow.class, readXmlResource.readXml(this, R.raw.orders));
			
			String mCompanyId = mOktopusFlow.getCompanyId();
			mOrdersComm = new OrdersComm(this, this, null, mOnline, mCompanyId);
			
			mBicycle.startFlow(mOktopusFlow, this);
			
			mBicycle.ride("", true);
					
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setToast("Error serializando flujo");
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void goToSystemAndProcessData(String pAction, Module pModule) {
		mModule = pModule;
		
		if(pAction.equalsIgnoreCase("getorders")){
			disableButtons(); 
			
			CharSequence literal = getString(R.string.DOWNLOADING_ORDERS);
			mProgressDialog = ProgressDialog.show(this, "", literal, true);
			
			mOrdersComm.getOrders(mWarehouseId, Config.ORDER_STATUS_READY);
			 
			 
		} else if(pAction.equalsIgnoreCase("getnextorder")){
			 
			currentOrder++;
			
			if(currentOrder == mOrdersList.size()){
				currentOrder = 0;
				mBicycle.processDataCallback((DataModule) mModule, "false");
				
			}else{
				
				mOrderAdapter.setSelectedItem(currentOrder);
				mOrderAdapter.notifyDataSetChanged();
				// mOrderAdapter.setSelectionFromTop(currentOrder, 0);
				
				mBicycle.processDataCallback((DataModule) mModule, "true");
			}
		}
	}


	
	
	@Override
	public void goToSystemAndProcessSay(Module pModule, List<Phrase> pSayList) {
		mModule = pModule;
		
		
		String tempType = "";
		
		String toSay = "";
		for (int i = 0; i < pSayList.size(); i++){
			
			tempType = pSayList.get(i).getType();
			if(tempType.equalsIgnoreCase("string")){
				toSay = toSay + pSayList.get(i).getLine() + " ";
			}
			
			if(tempType.equalsIgnoreCase("data")){
				String data = pSayList.get(i).getLine();
				
				
				if(data.equalsIgnoreCase("ordernumber")){
					toSay = toSay + mOrdersList.get(currentOrder).getOrderId() + " ";
					
				} else if(data.equalsIgnoreCase("ordertypename")){
					toSay = toSay + mOrdersList.get(currentOrder).getOrderTypeName() + " ";
					
				} else if(data.equalsIgnoreCase("ordercustomername")){
					toSay = toSay + mOrdersList.get(currentOrder).getOrderCustomerName() + " ";
					
				} else if(data.equalsIgnoreCase("ordertotallines")){
					if(mOrdersList.get(currentOrder).getOrderTotalLines() == 1){
						toSay = toSay + getString(R.string.ONE_ITEM) + " ";
					}else{
						toSay = toSay + mOrdersList.get(currentOrder).getOrderTotalLines() + " " + getString(R.string.ITEMS) + " ";
					}
					
				} else if(data.equalsIgnoreCase("ordersalesmanid")){
					toSay = toSay + mOrdersList.get(currentOrder).getOrderSalesmanId() + " ";
					
				} else if(data.equalsIgnoreCase("ordersalesmanname")){
					toSay = toSay + mOrdersList.get(currentOrder).getOrderSalesmanName() + " ";
					
				} else if(data.equalsIgnoreCase("ordernotes")){
					String tempNotes = mOrdersList.get(currentOrder).getOrderNotes();
					if(!tempNotes.equals("")){
						toSay = toSay + tempNotes + " ";
					}else{
						toSay = toSay + getString(R.string.NO_NOTES) + " ";
					}
				}
				
			}
			
			if(tempType.equalsIgnoreCase("method")){
				String method = pSayList.get(i).getLine();
				
				if(method.equalsIgnoreCase("sayagain")){
					toSay = sayAgainString;
				}
				
				if(method.equalsIgnoreCase("help")){
					FlowModule tempFlowModule = (FlowModule) pModule;
					for (int h = 0; h < tempFlowModule.getListenTo().size(); h++){
						// toSay = toSay + "Diga: ";
						toSay = toSay + tempFlowModule.getListenTo().get(h).getInputList().get(0).getLine() + " ";
						toSay = toSay + ", ";
						toSay = toSay + tempFlowModule.getListenTo().get(h).getHelp() + " ";
						toSay = toSay + ". ";
					}
				}
			}
		}
		
		
		
		if(pModule.getType().equalsIgnoreCase("flow") && !tempType.equalsIgnoreCase("silence")){
			mReStartModule = pModule;
			mReStartSayList = pSayList;
		}
		
		sayAgainString = toSay;
		mLog.appendLog(toSay);
		
		Boolean voiceOnOff = mSharedPrefs.getBoolean("voiceOnOff", true);
		
		if(voiceOnOff){
			mTts.speakOut(toSay, toSay);
		}
		
		// disableButtons();
	}


	private void enableButtons() {
		mPreferencesButton.setEnabled(true);
		
		Boolean voiceOnOff = mSharedPrefs.getBoolean("voiceOnOff", true);
		
		if(voiceOnOff){
			mMuteButton.setEnabled(true);
		}else{
			mMuteButton.setEnabled(false);
		}
		
		mExitButton.setEnabled(true);
		mViewClosedOrdersButton.setEnabled(true);
		mOrderAdapter.setButtonsStatus(true);
		mOrderAdapter.notifyDataSetChanged();
		
	}
	
	
	private void disableButtons() {
		mPreferencesButton.setEnabled(false);
		mMuteButton.setEnabled(false);
		mExitButton.setEnabled(false);
		mViewClosedOrdersButton.setEnabled(false);
		mOrderAdapter.setButtonsStatus(false);
		mOrderAdapter.notifyDataSetChanged();
	}


	@Override
	public void goToSystemAndProcessListenTo(Module module) {
		mModule = module;
		mStt.startRecognizeSpeech();
		// enableButtons();
	}


	@Override
	public void goToSystemAndGoToActivity(String pActivityToGo, Module pModule) {
	
		if(pActivityToGo.equalsIgnoreCase("itemsactivity.class")){
			gotoItemsActivity(mOrdersList.get(currentOrder));
		}
		

	}
	
	
	public void gotoItemsActivity(Order tempOrder){
		mute();

		Intent tempIntent = new Intent(this, ItemsActivity.class);

		
		tempIntent.putExtra("operator", mOperator);
		tempIntent.putExtra("order", tempOrder);
		
		startActivity(tempIntent);
	}
	
	

	
	public void gotoLoginActivity(){
		Intent tempIntent = null;

		tempIntent = new Intent(this, LoginActivity.class);
		
		startActivity(tempIntent);
	}
	
	private void gotoChooseWarehouseActivity(){
		Intent intent = new Intent(this, ChooseWarehouseActivity.class);
		
		intent.putExtra("operator", mOperator);

		startActivity(intent);
	}
		
	
	public void gotoClosedOrdersActivity(){
		Intent tempIntent = new Intent(this, ClosedOrdersActivity.class);
		
		tempIntent.putExtra("operator", mOperator);
		
		startActivity(tempIntent);
	}

	@Override
	public void goToSystemAndSetPicked(String pickedNumber) {
		// TODO Auto-generated method stub
		
	}
	
	public void mute(){	
		mIsSistemMute = true;
		
		mMuteButton.setBackgroundResource(R.color.green_button);
		mMuteButton.setText(getResources().getString(R.string.RESUME));
		
		mTts.stopTts();
		mStt.stopStt();

	}
	
	
	public void reStartSystem(){
		mIsSistemMute = false;
		
		mMuteButton.setBackgroundResource(R.color.red_button);
		mMuteButton.setText(getResources().getString(R.string.MUTE));
		
		goToSystemAndProcessSay(mReStartModule, mReStartSayList);
	}
	
	
	
	@Override
	public void onClick(View v) {

		int viewId = v.getId();

		if (viewId == R.id.preferences) {
			startActivity(new Intent(this, Preferences.class));
		} else if (viewId == R.id.muteButton) {
			if (!mIsSistemMute) {
				mute();
			} else {
				reStartSystem();
			}
		} else if (viewId == R.id.exitButton) {
			mute();
			gotoChooseWarehouseActivity();
		} else if (viewId == R.id.closedOrders) {
			mute();
			gotoClosedOrdersActivity();
		}

	}

	



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		String str= mOrdersList.get(position).getOrderId();
        
        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();	
	}


	@Override
	public void goToSystemMethod(String pMethod, Module pModule) {
		// TODO Auto-generated method stub
		
	}
	
	
	private class BluetoothHelper extends BluetoothHeadsetUtils
	{
		public BluetoothHelper(Context context)
	    {
	        super(context);
	    }

	    @Override
	    public void onScoAudioDisconnected()
	    {
	    	setToast("onScoAudioDisconnected");
    	
	    }

	    @Override
	    public void onScoAudioConnected()
	    {           
	    	setToast("onScoAudioConnected");
	    }

	    @Override
	    public void onHeadsetDisconnected()
	    {
	    	setToast("onScoAudioDisconnected");
	    }

	    @Override
	    public void onHeadsetConnected()
	    {
	    	setToast("onHeadsetConnected");
	    }
	}

}

package com.thirtythreelabs.oktopus;

import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thirtythreelabs.adapters.ItemAdapter;
import com.thirtythreelabs.adapters.ItemComparator;
import com.thirtythreelabs.bluetooth.BluetoothHeadsetUtils;
import com.thirtythreelabs.comm.ItemsComm;
import com.thirtythreelabs.comm.JsonToItem;
import com.thirtythreelabs.comm.RestTask;
import com.thirtythreelabs.flowmodel.DataModule;
import com.thirtythreelabs.flowmodel.Flow;
import com.thirtythreelabs.flowmodel.FlowModule;
import com.thirtythreelabs.flowmodel.Module;
import com.thirtythreelabs.flowmodel.Phrase;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.systemmodel.Operator;
import com.thirtythreelabs.systemmodel.Order;
import com.thirtythreelabs.ttsstt.Stt;
import com.thirtythreelabs.ttsstt.Tts;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.WriteLog;
import com.thirtythreelabs.util.readXmlResource;
import com.thirtythreelabs.util.img.ImageLoader;

public class ItemsActivity extends Activity implements OnClickListener, Stt.OnSttResults, Tts.OnTtsResults, Bicycle.OnBicycleResults{

	protected static final String TAG = "ItemsActivity";
	private Stt mStt = new Stt();
	private Tts mTts = new Tts();
	private boolean sttReady = false;
	private boolean ttsReady = false;
	
	private boolean mOnline = true;
	
	private ItemAdapter mItemAdapter;
	private ListView mItemsListView;
	private List<Item> mItemsList;
	private int currentItem = 0;
	private int previousItem = 0;
	
	private ItemsComm mItemsComm;
	private JsonToItem mJsonToItem = new JsonToItem(this);
	
	private Flow mOktopusFlow;
	private Bicycle mBicycle = new Bicycle();
	private Module mModule;
	private Module mReStartModule;
	private List<Phrase> mReStartSayList;
	
	private Operator mOperator;
	private String mOperatorName;
	
	private Order mOrder = new Order();
	private String sayAgainString = "";
	
	
	private String mWarehouseId = "1";
	
	private ImageButton mPreferencesButton;
	private Button mButtonCancelOrder;
	private Button mMuteButton;
	
	private SharedPreferences mSharedPrefs;
	
	private boolean mIsSistemMute = false;
	// private boolean mSistemMuteStateToggle = false;	
	
	private String mLastUtteranceId = "";
	private String mCanceledUtterance = "";
	
	private ProgressDialog mProgressDialog;
	private Dialog mEndOfOrderDialog;
	private Dialog mSetItemStatusErrorDialog;
	
	private WriteLog mLog = new WriteLog();
	
	private BluetoothHeadsetUtils mBluetoothHelper;
	
	public ImageLoader mImageLoader = new ImageLoader(this);

	
	@Override
	public void onCreate(Bundle mBundle) {
		super.onCreate(mBundle);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.items);
		
		
		mItemsListView = (ListView) findViewById(R.id.listview);
		mItemAdapter = new ItemAdapter(this, this);
		mItemsListView.setAdapter(mItemAdapter);
		
		mEndOfOrderDialog = new Dialog(this);
		mSetItemStatusErrorDialog = new Dialog(this);
		
		mItemsListView.setClickable(true);
		mItemsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if(!mItemsList.get(position).getItemStatusId().equals(Config.ITEM_STATUS_PICKED)){
					mItemsList.get(position).setItemStatusId(Config.ITEM_STATUS_READY);
					mItemAdapter.notifyDataSetChanged();
					goToSystemAndChangeItemManually(position);
				}
				
				/*
				String tempAlgo = mItemsList.get(position).getItemId();
				setToast(tempAlgo);
				*/
				
			}
			
		});
		
		
		mOperator = (Operator) getIntent().getSerializableExtra("operator");
		mWarehouseId = mOperator.getOperatorCurrentWarehoueseId();
		mOperatorName = mOperator.getOperatorName();
		
		mOrder = (Order) getIntent().getSerializableExtra("order");
		
		mPreferencesButton =  (ImageButton) findViewById(R.id.preferences);
		mPreferencesButton.setOnClickListener(this);
		
		mMuteButton = (Button) findViewById(R.id.buttonMute);
		mMuteButton.setOnClickListener(this);
		
		mButtonCancelOrder = (Button) findViewById(R.id.buttonCancelOrder);
		mButtonCancelOrder.setOnClickListener(this);
		
		mBluetoothHelper = new BluetoothHelper(this);
		mStt.startSpeech(this, this);
		mTts.startTts(this, this);
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		populateOrderData();
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiverGetItems, new IntentFilter(ItemsComm.GET_ITEMS_ACTION));
		
		registerReceiver(receiverSetItemStatus, new IntentFilter(ItemsComm.SET_ITEM_STATUS_TO_IN_PROGRESS_ACTION)); 
		registerReceiver(receiverSetItemStatus, new IntentFilter(ItemsComm.SET_ITEM_STATUS_TO_PAUSED_ACTION)); 
		registerReceiver(receiverSetItemStatus, new IntentFilter(ItemsComm.SET_ITEM_STATUS_TO_PICKED_ACTION)); 
		
		registerReceiver(receiverSetItemStatus, new IntentFilter(ItemsComm.SET_ITEM_STATUS_TO_IN_PROGRESS_MANUAL_ACTION));
		
		Boolean voiceOnOff = mSharedPrefs.getBoolean("voiceOnOff", true);
		
		if(voiceOnOff){
			mMuteButton.setEnabled(true);
		}else{
			mMuteButton.setEnabled(false);
			mute();
		}
		
		mBluetoothHelper.start();
		mTts.resumeTts();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if(mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_IN_PROGRESS)){
			mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_READY, "", false, mOperator.getOperatorId());
		}
		
		unregisterReceiver(receiverGetItems);
		unregisterReceiver(receiverSetItemStatus);
		mBluetoothHelper.stop();
		mTts.pauseTts();
		mStt.stopStt();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
	}
	
	@Override
    public void onDestroy() {        
        super.onDestroy();
        
        mTts.destroyTts();
		mStt.destroyStt();
	}	
	
	
	private void populateOrderData() {
		((TextView) findViewById(R.id.pedidoId)).setText(mOrder.getOrderId());
		((TextView) findViewById(R.id.salemanTxt)).setText(Html.fromHtml(getString(R.string.SALESMAN) + ": <b>" + mOrder.getOrderSalesmanId() + " - " + mOrder.getOrderSalesmanName().toUpperCase() + "</b>"));
		((TextView) findViewById(R.id.custumerTxt)).setText(Html.fromHtml(getString(R.string.CUSTOMER) + ": <b>" + mOrder.getOrderCustomerName().toUpperCase() + "</b>"));
		((TextView) findViewById(R.id.orderTypeTxt)).setText(Html.fromHtml(getString(R.string.ORDER_TYPE) + ": <b>" + mOrder.getOrderTypeName().toUpperCase() + "</b>"));
		
		int percentage = (int) (((mOrder.getOrderLinesPicked())*100.0f/mOrder.getOrderTotalLines()));
		((TextView) findViewById(R.id.avancePorcentaje)).setText(percentage + "%");
		
		((TextView) findViewById(R.id.avance)).setText(String.valueOf(mOrder.getOrderLinesPicked()) + " " + getString(R.string.OF) + " " + String.valueOf(mOrder.getOrderTotalLines()));
		
		TextView mFloor = (TextView) findViewById(R.id.operatorAndFloor);
        mFloor.setText(getString(R.string.app_name) + " | " + getString(R.string.FLOOR) + " " + mWarehouseId + " | " + mOperatorName);
		
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
			getOktopusFlow();
		}
	}

	
	private void getOktopusFlow(){
		
		Serializer mSerializer = new Persister();
		// File source = new File("/sdcard/download/Items.xml");

		try {
			
			mOktopusFlow = mSerializer.read(Flow.class, readXmlResource.readXml(this, R.raw.items));

			String mCompanyId = mOktopusFlow.getCompanyId();
			mItemsComm = new ItemsComm(this, this, null, mOnline, mCompanyId);
			
			mBicycle.startFlow(mOktopusFlow, this);
			
			mBicycle.ride("", true);
					
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setToast("Error serializando flujo");
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void ttsOnDone(String pUtteranceId) {
		
		// setToast(pUtteranceId);
		Boolean voiceOnOff = mSharedPrefs.getBoolean("voiceOnOff", true);
		

		
		if(mIsSistemMute == false){	
			mBicycle.processSayCallback(mModule);
		}
		
		if(pUtteranceId.toLowerCase().equalsIgnoreCase(mCanceledUtterance)){
			mIsSistemMute = false;
		}
		
		
	}

	@Override
	public void setToast(String myToast) {
		mLog.appendLog(myToast);
		Toast.makeText(this, myToast, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void speak(String string) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sttResults(List<String> data, float[] pConfidence) {
		mBicycle.processListenToCallback(mModule, data);
	}

	@Override
	public void onClick(View v) {

		int viewId = v.getId();

		if (viewId == R.id.preferences) {
			startActivity(new Intent(this, Preferences.class));
		} else if (viewId == R.id.buttonMute) {
			if (!mIsSistemMute) {
				mute();
			} else {
				reStartSystem();
			}
		} else if (viewId == R.id.buttonCancelOrder) {
			gotoOrderActivity();
		}
		
	}
	
	
	private BroadcastReceiver receiverGetItems = new BroadcastReceiver() { 
		@Override
		public void onReceive(Context context, Intent intent) { 

			// String mJson = intent.getStringExtra(RestTask.HTTP_RESPONSE); 
			
			// mLog.appendLog(mJson.toString());
			String mJson = mSharedPrefs.getString(RestTask.HTTP_RESPONSE, "");
			
			
			List<Item> tempItemsList = mJsonToItem.getItemsFromJson(mJson);

			
			mItemsList = tempItemsList;
			
			Collections.sort(mItemsList, new ItemComparator());
			populateItems();
			
			if(mProgressDialog != null) {
				mProgressDialog.dismiss(); 
			}
			
			mBicycle.processDataCallback((DataModule) mModule, "true");
		} 
	};
	
	
	private BroadcastReceiver receiverSetItemStatus = new BroadcastReceiver() { 
		@Override
		public void onReceive(Context context, Intent intent) { 

			String mJson = intent.getStringExtra(RestTask.HTTP_RESPONSE); 
			
			//mLog.appendLog(mJson.toString());
			
			String tempAction = intent.getAction();
			
			JSONObject itemStatusObject;
			String tempError = "Y";
			String tempErrorMessage = "";
			
			Log.d(TAG, mJson);
			
			try {
				itemStatusObject = new JSONObject(mJson);
				
				setToast(itemStatusObject.toString());
				
				tempError = itemStatusObject.getString("Error");
				tempErrorMessage = itemStatusObject.getString("ErrorMessage");
				
				// setToast(tempErrorMessage);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
			if(mProgressDialog != null) {
				mProgressDialog.dismiss(); 
			}

			setItemStatusCallback(tempError, tempErrorMessage, tempAction);

            
		} 
	};
	
	
	public void populateItems(){
		
		mItemAdapter.clear();
        
		int tempTotalLines = 0;
        for (Item tempItem : mItemsList) {
        	mItemAdapter.add(tempItem);
        	tempTotalLines++;
        }
        /*
        for (int e = 0; e < mItemsList.size(); e++){
			if(mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_PICKED)){
				mOrder.setOrderLinesPicked(mOrder.getOrderLinesPicked() +1);
				currentItem++;
			
			}else{
				break;
			}
		}
        */
        populateOrderData();
        
        mItemAdapter.setSelectedItem(currentItem);
        mItemAdapter.setTotalLineas(tempTotalLines);
        mItemAdapter.notifyDataSetChanged();
		
        updateStatusLine();
        
        enableButtons();

    }
	
	
	@Override
	public void goToSystemAndProcessData(String pAction, Module pModule) {	
		mModule = pModule;
		
		if(pAction.equalsIgnoreCase("getitems")){
			disableButtons(); 

			CharSequence literal = getString(R.string.DOWNLOADING_ORDERS);
			mProgressDialog = ProgressDialog.show(this, "", literal, true);
			
			mItemsComm.getItems(mOrder.getOrderId(), mWarehouseId);
		
		} else if(pAction.equalsIgnoreCase("getnextitem")){
			
			if(mEndOfOrderDialog != null) {
				mEndOfOrderDialog.dismiss(); 
			}
		
			currentItem = 0;

			if(mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_PICKED)){
				
				mBicycle.processDataCallback((DataModule) mModule, "false");
			
			} else if (mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
				
				mBicycle.processDataCallback((DataModule) mModule, "false");
				
				/*if(mOnline){
					mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_IN_PROGRESS, "");
				}*/
				
			} else if (mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_READY)){

				if(mOnline){
					CharSequence literal = getString(R.string.PLEASE_WAIT);
					mProgressDialog = ProgressDialog.show(this, "", literal, true);
					
					mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_IN_PROGRESS, "", false, mOperator.getOperatorId());
				}
				
			} else if (mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_IN_PROGRESS)){
				
				//mItemsList.get(currentItem).setItemStatusId(Config.ITEM_STATUS_IN_PROGRESS);
				//mBicycle.processDataCallback((DataModule) mModule, "true");
				
				if(mOnline){
					CharSequence literal = getString(R.string.PLEASE_WAIT);
					mProgressDialog = ProgressDialog.show(this, "", literal, true);
					
					mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_IN_PROGRESS, "", false, mOperator.getOperatorId());
				}
			}

		}else if(pAction.equalsIgnoreCase("getitemmanually")){
			
			if(mEndOfOrderDialog != null) {
				mEndOfOrderDialog.dismiss(); 
			}
		
			// currentItem = 0;
			

			if (mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
				
				mBicycle.processDataCallback((DataModule) mModule, "true");

			} else if (mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_READY)){

				if(mOnline){
					CharSequence literal = getString(R.string.PLEASE_WAIT);
					mProgressDialog = ProgressDialog.show(this, "", literal, true);
					
					mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_IN_PROGRESS, "", false, mOperator.getOperatorId());
				}
				
			} else if (mItemsList.get(currentItem).getItemStatusId().equals(Config.ITEM_STATUS_IN_PROGRESS)){
				
				mItemsList.get(currentItem).setItemStatusId(Config.ITEM_STATUS_IN_PROGRESS);
				mBicycle.processDataCallback((DataModule) mModule, "true");
			}

		} else if(pAction.equalsIgnoreCase("saveitem")){
			
			if(mOnline){
				CharSequence literal = getString(R.string.PLEASE_WAIT);
				mProgressDialog = ProgressDialog.show(this, "", literal, true);
				
				mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_PICKED, mItemsList.get(currentItem).getItemQuantityPicked(), false, mOperator.getOperatorId());
			}
					
			// mBicycle.processDataCallback((DataModule) mModule, "true");
			
			
		} else if(pAction.equalsIgnoreCase("savedefaultitem")){

			mItemsList.get(currentItem).setItemQuantityPicked(mItemsList.get(currentItem).getItemQuantity());
			
			setToast("ItemsPicked: " + mItemsList.get(currentItem).getItemQuantityPicked());
			
			
			if(mOnline){
				CharSequence literal = getString(R.string.PLEASE_WAIT);
				mProgressDialog = ProgressDialog.show(this, "", literal, true);
				
				mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_PICKED, mItemsList.get(currentItem).getItemQuantityPicked(), false, mOperator.getOperatorId());
			}
			
			
			// mBicycle.processDataCallback((DataModule) mModule, "true");
			
			
		} else if(pAction.equalsIgnoreCase("pauseitem")){
			
			CharSequence literal = getString(R.string.PLEASE_WAIT);
			mProgressDialog = ProgressDialog.show(this, "", literal, true);
			
			if(mOnline){
				mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_PAUSED, "", false, mOperator.getOperatorId());
			}
			

			// mBicycle.processDataCallback((DataModule) mModule, "true");
			
			
		} else if(pAction.equalsIgnoreCase("getpauseitems")){
			int pausedItems = 0;
			for (int e = 0; e < mItemsList.size(); e++){
				if(mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
					pausedItems++;
				}
			}
			
			if(pausedItems == 0){
				mBicycle.processDataCallback((DataModule) mModule, "false");		
			}else{
				mBicycle.processDataCallback((DataModule) mModule, "true");
				
				endOfOrderDialog();
			}
		}
	}
	
	
	private void setItemStatusCallback(String pError, String tempErrorMessage, String pAction){
		if(pError.equalsIgnoreCase("n")){
			
			if(pAction.equals(ItemsComm.SET_ITEM_STATUS_TO_IN_PROGRESS_ACTION)){
				
				mItemsList.get(currentItem).setItemStatusId(Config.ITEM_STATUS_IN_PROGRESS);
				mBicycle.processDataCallback((DataModule) mModule, "true");
				
				
				mItemAdapter.setSelectedItem(currentItem);
	    		mItemAdapter.notifyDataSetChanged();
	    		mItemsListView.setSelectionFromTop(currentItem, 0);
	    		
	    		
	        } else if (pAction.equals(ItemsComm.SET_ITEM_STATUS_TO_PAUSED_ACTION)){
	        	
	        	mItemsList.get(currentItem).setItemStatusId(Config.ITEM_STATUS_PAUSED);
	        	
	        	Collections.sort(mItemsList, new ItemComparator());
	        	populateItems();
	        	
	        	mBicycle.processDataCallback((DataModule) mModule, "true");
	        		        	
	        } else if (pAction.equals(ItemsComm.SET_ITEM_STATUS_TO_PICKED_ACTION)){
	        	
	        	mItemsList.get(currentItem).setItemStatusId(Config.ITEM_STATUS_PICKED);
	        	
	        	mOrder.setOrderLinesPicked(mOrder.getOrderLinesPicked() + 1);
				populateOrderData();
				
				Collections.sort(mItemsList, new ItemComparator());
				populateItems();
	        	
				mBicycle.processDataCallback((DataModule) mModule, "true");
				
	        } else if (pAction.equals(ItemsComm.SET_ITEM_STATUS_TO_IN_PROGRESS_MANUAL_ACTION)){
	        
	        	mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(previousItem).getItemLineId(), Config.ITEM_STATUS_READY, "", false, mOperator.getOperatorId());
	        	mItemsList.get(previousItem).setItemStatusId(Config.ITEM_STATUS_READY);
	        	
	        	mItemsList.get(currentItem).setItemStatusId(Config.ITEM_STATUS_IN_PROGRESS);
	        	
	        	mItemAdapter.setSelectedItem(currentItem);
	    		mItemAdapter.notifyDataSetChanged();
	    		mItemsListView.setSelectionFromTop(currentItem, 0);

	        	mBicycle.ride("item", false);
	        }

		}else{
			
			setItemStatusErrorDialog(tempErrorMessage);
			
		}
		
	}

	
	@Override
	public void goToSystemAndProcessSay(Module pModule, List<Phrase> sayList) {
		
		mModule = pModule;
		String tempType = "";
		
		String toSay = "";
		for (int i = 0; i < sayList.size(); i++){
			tempType = sayList.get(i).getType();
			
			if(tempType.equalsIgnoreCase("string")){
				toSay = toSay + sayList.get(i).getLine() + " ";
			}
			
			if(tempType.equalsIgnoreCase("data")){
				String data = sayList.get(i).getLine();
				
				if(data.equalsIgnoreCase("itemhowtoread")){
					toSay = toSay + mItemsList.get(currentItem).getItemHowToRead() + " ";
					
				} else if(data.equalsIgnoreCase("itemhowtoread")){
					toSay = toSay + mItemsList.get(currentItem).getItemHowToRead() + " ";
					
				} else if(data.equalsIgnoreCase("itemfamily")){
					toSay = toSay + mItemsList.get(currentItem).getItemFamily() + " ";
					
				}	else if(data.equalsIgnoreCase("itembrandname")){
					toSay = toSay + mItemsList.get(currentItem).getItemBrandNameHowToRead() + " ";
					
				}else if(data.equalsIgnoreCase("itemsuppliername")){
					toSay = toSay + mItemsList.get(currentItem).getItemSupplierNameHowToRead() + " ";
					
				} else if(data.equalsIgnoreCase("itemdescription")){
					toSay = toSay + mItemsList.get(currentItem).getItemDescriptionHowToRead() + " ";
					
				} else if(data.equalsIgnoreCase("itemlocation")){
					toSay = toSay + getString(R.string.ROW) + ": " + mItemsList.get(currentItem).getItemRow() + ", ";
					toSay = toSay + getString(R.string.AISLE) + ": " + mItemsList.get(currentItem).getItemAisle() + ", ";
					toSay = toSay + getString(R.string.LEVEL) + ": " + mItemsList.get(currentItem).getItemLevel() + ". ";
					
				} else if(data.equalsIgnoreCase("itemrow")){
					toSay = toSay + getString(R.string.ROW) + ": " + mItemsList.get(currentItem).getItemRow() + ". ";
					
				} else if(data.equalsIgnoreCase("itemaisle")){
					toSay = toSay + getString(R.string.AISLE) + ": " + mItemsList.get(currentItem).getItemAisle() + ". ";
					
				} else if(data.equalsIgnoreCase("itemlevel")){
					toSay = toSay + getString(R.string.LEVEL) + ": " + mItemsList.get(currentItem).getItemLevel() + ". ";
					
				} else if(data.equalsIgnoreCase("itemorigin")){
					toSay = toSay + mItemsList.get(currentItem).getItemOrigin() + " ";
					
				} else if(data.equalsIgnoreCase("itemquantity")){
					String tempQuantity = mItemsList.get(currentItem).getItemQuantity();
					
					String tempUnitDescription = mItemsList.get(currentItem).getItemUnitId();
					
					if(tempQuantity.equalsIgnoreCase("1")){
						if( tempUnitDescription.equalsIgnoreCase("0")){
							toSay = toSay + " " + getString(R.string.UNO) + " ";
						}else {
							toSay = toSay + " " + getString(R.string.UN) + " ";
						}
					}else{
						toSay = toSay + tempQuantity + " ";
					}
					
				} else if(data.equalsIgnoreCase("itemunitdescription")){
					String tempUnitDescription = mItemsList.get(currentItem).getItemUnitId();
					if( tempUnitDescription.equalsIgnoreCase("0")){
						toSay = toSay + " ";
					}else{
						if(mItemsList.get(currentItem).getItemQuantity().equalsIgnoreCase("1")){
							if( tempUnitDescription.equalsIgnoreCase("0")){
								toSay = toSay + " " + getString(R.string.UNIT) + " ";
							}else if( tempUnitDescription.equalsIgnoreCase("2")){
								toSay = toSay + " " + getString(R.string.METER) + " ";
							}else if( tempUnitDescription.equalsIgnoreCase("4")){
								toSay = toSay + " " + getString(R.string.KILO) + " ";
							}else if( tempUnitDescription.equalsIgnoreCase("6")){
								toSay = toSay + " " + getString(R.string.HUNDRED) + " ";
							}else if( tempUnitDescription.equalsIgnoreCase("8")){
								toSay = toSay + " juego partido";
							}
						}else{
							if( tempUnitDescription.equalsIgnoreCase("0")){
								toSay = toSay + " " + getString(R.string.UNITS) + " ";
							}else if( tempUnitDescription.equalsIgnoreCase("2")){
								toSay = toSay + " " + getString(R.string.METERS) + " ";
							}else if( tempUnitDescription.equalsIgnoreCase("4")){
								toSay = toSay + " " + getString(R.string.KILOS) + " ";
							}else if( tempUnitDescription.equalsIgnoreCase("6")){
								toSay = toSay + " " + getString(R.string.HUNDREDS) + " ";
							}else if( tempUnitDescription.equalsIgnoreCase("8")){
								toSay = toSay + " juegos partidos ";
							}
						}
					}
					
				} else if(data.equalsIgnoreCase("iteminventory")){
					toSay = toSay + mItemsList.get(currentItem).getItemInventory() + " ";
					
				} else if(data.equalsIgnoreCase("itemspicked")){
					toSay = toSay + mItemsList.get(currentItem).getItemQuantityPicked() + " ";
		
					
				} else if(data.equalsIgnoreCase("ordernumber")){
					toSay = toSay + mOrder.getOrderId() + " ";
					
				} else if(data.equalsIgnoreCase("ordertypename")){
					toSay = toSay + mOrder.getOrderTypeName() + " ";
					
				} else if(data.equalsIgnoreCase("ordercustomername")){
					toSay = toSay + mOrder.getOrderCustomerName() + " ";
					
				} else if(data.equalsIgnoreCase("ordertotallines")){
					if(mOrder.getOrderTotalLines() == 1){
						toSay = toSay + getString(R.string.ONE_ITEM) + " ";
					}else{
						toSay = toSay + String.valueOf(mOrder.getOrderTotalLines()) + " " + getString(R.string.ITEMS) + " ";
					}
					
				} else if(data.equalsIgnoreCase("ordersalesmanid")){
					toSay = toSay + mOrder.getOrderSalesmanId() + " ";
					
				} else if(data.equalsIgnoreCase("ordersalesmanname")){
					toSay = toSay + mOrder.getOrderSalesmanName() + " ";
					
				} else if(data.equalsIgnoreCase("ordernotes")){
					String tempNotes = mOrder.getOrderNotes();
					if(!tempNotes.equals("")){
						toSay = toSay + tempNotes + " ";
					}else{
						toSay = toSay + getString(R.string.NO_NOTES) + " ";
					}
					
				}
			}
			
			if(tempType.equalsIgnoreCase("method")){
				String method = sayList.get(i).getLine();
				
				if(method.equalsIgnoreCase("sayagain")){
					toSay = sayAgainString;
					
				} else if(method.equalsIgnoreCase("help")){
					FlowModule tempFlowModule = (FlowModule) pModule;
					for (int h = 0; h < tempFlowModule.getListenTo().size(); h++){
						// toSay = toSay + "Diga: ";
						toSay = toSay + tempFlowModule.getListenTo().get(h).getInputList().get(0).getLine() + " ";
						toSay = toSay + ", ";
						toSay = toSay + tempFlowModule.getListenTo().get(h).getHelp() + " ";
						toSay = toSay + ". ";
					}
					
				} else if(method.equalsIgnoreCase("gethowmanypauseditems")){
					int pausedItems = 0;
					for (int e = 0; e < mItemsList.size(); e++){
						if(mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
							pausedItems++;
						}
					}
					if(pausedItems == 1){
						toSay = toSay + " " + getString(R.string.ONE_PAUSED_ITEM) + ". ";
					}else{
						toSay = toSay + String.valueOf(pausedItems) + " " + getString(R.string.PAUSED_ITEMS) + ". ";
					}
					
					
				} else if(method.equalsIgnoreCase("gethowmanypickeditems")){
					int pickedItems = 0;
					for (int e = 0; e < mItemsList.size(); e++){
						if(mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PICKED)){
							pickedItems++;
						}
					}
					if(pickedItems == 1){
						toSay = toSay + " " + getString(R.string.ONE_FINISHED_ITEM) + ". ";
					}else{
						toSay = toSay + String.valueOf(pickedItems) + " " + getString(R.string.FINISHED_ITEMS) + ". ";
					}
					
					
				} else if(method.equalsIgnoreCase("gethowmanypendingitems")){
					int pendingItems = 0;
					for (int e = 0; e < mItemsList.size(); e++){
						if(!mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PICKED) && !mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
							pendingItems++;
						}
					}
					if(pendingItems == 1){
						toSay = toSay + " " + getString(R.string.ONE_PENDING_ITEM) + ". ";
					}else{
						toSay = toSay + String.valueOf(pendingItems) + " " + getString(R.string.PENDING_ITEMS) + ". ";
					}
				
				}
			}
			
			if(tempType.equalsIgnoreCase("silence")){
				toSay = "";
			}
		}
		
		if(pModule.getType().equalsIgnoreCase("flow") && !tempType.equalsIgnoreCase("silence")){
			mReStartModule = pModule;
			mReStartSayList = sayList;
		}
		
		
		sayAgainString = toSay;
		mLog.appendLog(toSay);
		mLastUtteranceId = toSay;
		
		Boolean voiceOnOff = mSharedPrefs.getBoolean("voiceOnOff", true);
		
		if(voiceOnOff){
			mTts.speakOut(toSay, toSay);
		} else if (mModule.getType().equalsIgnoreCase("saydo")) {
			mBicycle.processSayCallback(mModule);
		}
		
		
		// mIsSistemMute = false;
		
	}

	@Override
	public void goToSystemAndProcessListenTo(Module pModule) {
		mModule = pModule;
		mStt.startRecognizeSpeech();
		
	}


	
	@Override
	public void goToSystemAndGoToActivity(String pActivityToGo, Module pModule) {
		if(pActivityToGo.equalsIgnoreCase("ordersactivity.class")){
			gotoOrderActivity();
		}
	}
	
	
	public void gotoOrderActivity(){
		mute();
		
		Intent tempIntent = new Intent(this, OrdersActivity.class);
		
		tempIntent.putExtra("operator", mOperator);
		
		startActivity(tempIntent);
	}

	
	@Override
	public void goToSystemAndSetPicked(String itemsPicked) {
		
		mItemsList.get(currentItem).setItemQuantityPicked(itemsPicked);
		
	}
	
	
	public void goToSystemAndPickAll(){
		mCanceledUtterance = mLastUtteranceId;
		
		mute();
		
		if(mTts.isSpeaking()){
			// mSistemMuteStateToggle = true;
		} else {
			mIsSistemMute = false;
		}
		
		
		goToSystemAndSetPicked(mItemsList.get(currentItem).getItemQuantity());
		
		mMuteButton.setBackgroundResource(R.color.red_button);
		mMuteButton.setText(getString(R.string.MUTE));
		
		mBicycle.ride("saveItem", false);
		
	}
	
	
	public void goToSystemAndPauseThisItem(){
		mCanceledUtterance = mLastUtteranceId;
		
		mute();
		
		if(mTts.isSpeaking()){
			//mSistemMuteStateToggle = true;
		} else {
			mIsSistemMute = false;
		}
		
		
		
		mMuteButton.setBackgroundResource(R.color.red_button);
		mMuteButton.setText(getString(R.string.MUTE));
		
		mBicycle.ride("pauseItem", false);
		
	}
	
	
	public void goToSystemAndChangeItemManually(int pCurrentItemId){
		mCanceledUtterance = mLastUtteranceId;
		
		mute();
		
		if(mTts.isSpeaking()){
			//mSistemMuteStateToggle = true;
		} else {
			mIsSistemMute = false;
		}
		
		previousItem = currentItem;
		currentItem = pCurrentItemId;
		
		mMuteButton.setBackgroundResource(R.color.red_button);
		mMuteButton.setText(getString(R.string.MUTE));
		
		mItemsComm.setItemStatus(mOrder.getOrderId(), mItemsList.get(currentItem).getItemLineId(), Config.ITEM_STATUS_IN_PROGRESS, "", true, mOperator.getOperatorId());
		

	}

	
	
	private void enableButtons() {
		mPreferencesButton.setEnabled(true);
		
		Boolean voiceOnOff = mSharedPrefs.getBoolean("voiceOnOff", true);
		
		if(voiceOnOff){
			mMuteButton.setEnabled(true);
		}else{
			mMuteButton.setEnabled(false);
		}
		
		mButtonCancelOrder.setEnabled(true);
	}
	
	private void disableButtons() {
		mPreferencesButton.setEnabled(false);
		mMuteButton.setEnabled(false);
		mButtonCancelOrder.setEnabled(false);
	}
	
	public void mute(){	
		mIsSistemMute = true;
		
		mTts.stopTts();
		mStt.stopStt();
		
		mMuteButton.setBackgroundResource(R.color.green_button);
		mMuteButton.setText(getString(R.string.RESUME));
		
	}
	
	public void setPausedItemsAndGetNextItem(){
		setPausedItemsReady();
		
		mItemAdapter.notifyDataSetChanged();
		
        mBicycle.ride("getNextItem", false);
	}
	
	
	public void reStartSystem(){
		mIsSistemMute = false;
		
		mMuteButton.setBackgroundResource(R.color.red_button);
		mMuteButton.setText(getString(R.string.MUTE));
		
		goToSystemAndProcessSay(mReStartModule, mReStartSayList);
	}

	@Override
	public void goToSystemMethod(String pMethod, Module pModule) {
		if(pMethod.equalsIgnoreCase("mute")){
			mute();
		} else if (pMethod.equalsIgnoreCase("setpauseditemsandgetnextitem")){
			setPausedItemsAndGetNextItem();
		}
	}
	
	
	public void manualImputDialog() {
		mute();
		
		final Dialog mDialog = new Dialog(this);
		mDialog.setContentView(R.layout.manual_input_dialog);
		mDialog.setTitle(getString(R.string.INSERT_QUANTITY) + ": ");
		mDialog.setCancelable(false);
		
		TextView itemIdTxt = (TextView) mDialog.findViewById(R.id.itemIdTxt);
		TextView itemDescriptionTxt = (TextView) mDialog.findViewById(R.id.itemDescription);
		TextView itemCantidadTxt = (TextView) mDialog.findViewById(R.id.itemCantidadTxt);
		TextView itemUnidadTxt = (TextView) mDialog.findViewById(R.id.itemUnidad);
		
		
		String tempFamily = mItemsList.get(currentItem).getItemId().substring(0, 3);
        String tempItemId = mItemsList.get(currentItem).getItemId().substring(3);
        
        String text = "<font color=" + getResources().getColor(R.color.red) + ">" + tempFamily + "</font> <font color=#000000>" + tempItemId + "</font>";
        itemIdTxt.setText(Html.fromHtml(text));
        
        //setColor(itemIdTxt, tempFamily + " " + tempItemId, 0, 3, getResources().getColor(R.color.red));
        
        itemDescriptionTxt.setText(mItemsList.get(currentItem).getItemDescription());
        
        
		String tempItemQuantity = mItemsList.get(currentItem).getItemQuantity();
		itemCantidadTxt.setText(tempItemQuantity);
        
        String tempItemUnitId = mItemsList.get(currentItem).getItemUnitId();
        String tempItemUnitDescription = "";
        boolean tempDecimals = false;
        
        if(tempItemQuantity.equalsIgnoreCase("1")){
        	if(tempItemUnitId.equalsIgnoreCase("0")){
            	tempItemUnitDescription = getString(R.string.UNIT);
            }else if(tempItemUnitId.equalsIgnoreCase("2")){
            	tempItemUnitDescription = getString(R.string.METER);
            	tempDecimals = true;
            }else if(tempItemUnitId.equalsIgnoreCase("4")){
            	tempItemUnitDescription = getString(R.string.KILO);
            	tempDecimals = true;
            }else if(tempItemUnitId.equalsIgnoreCase("5")){
            	tempItemUnitDescription = getString(R.string.HUNDRED);
            }else if(tempItemUnitId.equalsIgnoreCase("8")){
            	tempItemUnitDescription = "JUEGO PARTIDO";
            	tempDecimals = true;
            }
        }else{
        	if(tempItemUnitId.equalsIgnoreCase("0")){
            	tempItemUnitDescription = getString(R.string.UNITS);
            }else if(tempItemUnitId.equalsIgnoreCase("2")){
            	tempItemUnitDescription = getString(R.string.METERS);
            	tempDecimals = true;
            }else if(tempItemUnitId.equalsIgnoreCase("4")){
            	tempItemUnitDescription = getString(R.string.KILOS);
            	tempDecimals = true;
            }else if(tempItemUnitId.equalsIgnoreCase("5")){
            	tempItemUnitDescription = getString(R.string.HUNDREDS);
            }else if(tempItemUnitId.equalsIgnoreCase("8")){
            	tempItemUnitDescription = "JUEGOS PARTIDOS";
            	tempDecimals = true;
            }
        }
        
        itemUnidadTxt.setText(tempItemUnitDescription);
        
        
		// set the custom dialog components - text, image and button
		TextView inputText = (TextView) mDialog.findViewById(R.id.inputText);
		inputText.setText("");
		
		
		final Button okButton = (Button) mDialog.findViewById(R.id.dialogButtonOK);
		Button cancelButton = (Button) mDialog.findViewById(R.id.dialogButtonCancel);
		
		Button ButtonInput1 = (Button) mDialog.findViewById(R.id.ButtonInput1);
		Button ButtonInput2 = (Button) mDialog.findViewById(R.id.ButtonInput2);
		Button ButtonInput3 = (Button) mDialog.findViewById(R.id.ButtonInput3);
		Button ButtonInput4 = (Button) mDialog.findViewById(R.id.ButtonInput4);
		Button ButtonInput5 = (Button) mDialog.findViewById(R.id.ButtonInput5);
		Button ButtonInput6 = (Button) mDialog.findViewById(R.id.ButtonInput6);
		Button ButtonInput7 = (Button) mDialog.findViewById(R.id.ButtonInput7);
		Button ButtonInput8 = (Button) mDialog.findViewById(R.id.ButtonInput8);
		Button ButtonInput9 = (Button) mDialog.findViewById(R.id.ButtonInput9);
		Button ButtonInput0 = (Button) mDialog.findViewById(R.id.ButtonInput0);
		Button ButtonInputComma = (Button) mDialog.findViewById(R.id.ButtonInputComma);
		Button ButtonInputDel = (Button) mDialog.findViewById(R.id.ButtonInputDel);
		
		okButton.setEnabled(false);
		if(!tempDecimals){
			ButtonInputComma.setEnabled(false);
		}
		
		ButtonInput1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "1");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "2");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "3");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "4");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "5");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "6");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "7");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "8");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "9");
				okButton.setEnabled(true);
			}
		});
		
		ButtonInput0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				text.setText(text.getText().toString() + "0");
				okButton.setEnabled(true);
			}
		});
		
		
		
		ButtonInputComma.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				if(!text.getText().toString().contains(".")){
					if(text.getText().toString().length() == 0){
						text.setText(text.getText().toString() + "0.");
					}else{
						text.setText(text.getText().toString() + ".");
					}
					
				}
			}
		});
		
		
		
		ButtonInputDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				TextView text = (TextView)mDialog.findViewById(R.id.inputText);
				
				if(text.getText().toString().length() > 0){
					text.setText(text.getText().toString().substring(0, text.getText().toString().length() - 1));
				}
				
				TextView text2 = (TextView) mDialog.findViewById(R.id.inputText);
				
				if(text2.getText().toString().length() == 0){
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}
				
			}
		});

		
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				TextView edit = (TextView)mDialog.findViewById(R.id.inputText);
	            String itemsPicked = edit.getText().toString();

	            goToSystemAndSetPicked(itemsPicked);

	            mIsSistemMute = false;
	    		
	    		mMuteButton.setBackgroundResource(R.color.red_button);
	    		mMuteButton.setText(getString(R.string.MUTE));
	    		
	    		mBicycle.ride("saveItem", false);
	    		
	            mDialog.dismiss();
	            // goToSystemAndProcessData("saveThisOneDefaultAndGetNextItem", mOktopusFlow);
				
				
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		
		
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.BOTTOM;
		window.setAttributes(wlp);
		 
		mDialog.show();

	}
	
	
	
	
	public void imageDialog() {
		mute();
		
		final Dialog mDialog = new Dialog(this);
		mDialog.setContentView(R.layout.image_dialog);
		mDialog.setTitle(getString(R.string.IMAGE));
		mDialog.setCancelable(false);
		
		
		
		int loader = R.drawable.loading;
        
        
        // Imageview to show
        ImageView image = (ImageView) mDialog.findViewById(R.id.imageView2);
        //String image_url = "http://upload.wikimedia.org/wikipedia/commons/f/f7/Carburador_Solex.JPG";
        String image_url = mItemsList.get(currentItem).getItemImage();
        ImageLoader imgLoader = new ImageLoader(this);

        imgLoader.DisplayImage(image_url, loader, image);
        
		
		TextView itemIdTxt = (TextView) mDialog.findViewById(R.id.itemIdTxt);
		TextView itemDescriptionTxt = (TextView) mDialog.findViewById(R.id.itemDescription);
		TextView itemCantidadTxt = (TextView) mDialog.findViewById(R.id.itemCantidadTxt);
		TextView itemUnidadTxt = (TextView) mDialog.findViewById(R.id.itemUnidad);
		
		
		String tempFamily = mItemsList.get(currentItem).getItemId().substring(0, 3);
        String tempItemId = mItemsList.get(currentItem).getItemId().substring(3);
        
        String text = "<font color=" + getResources().getColor(R.color.red) + ">" + tempFamily + "</font> <font color=#000000>" + tempItemId + "</font>";
        itemIdTxt.setText(Html.fromHtml(text));
        
        //setColor(itemIdTxt, tempFamily + " " + tempItemId, 0, 3, getResources().getColor(R.color.red));
        
        itemDescriptionTxt.setText(mItemsList.get(currentItem).getItemDescription());
        
        
		String tempItemQuantity = mItemsList.get(currentItem).getItemQuantity();
		itemCantidadTxt.setText(tempItemQuantity);
        
        String tempItemUnitId = mItemsList.get(currentItem).getItemUnitId();
        String tempItemUnitDescription = "";
        if(tempItemQuantity.equalsIgnoreCase("1")){
        	if(tempItemUnitId.equalsIgnoreCase("0")){
            	tempItemUnitDescription = getString(R.string.UNIT);
            }else if(tempItemUnitId.equalsIgnoreCase("2")){
            	tempItemUnitDescription = getString(R.string.METER);
            }else if(tempItemUnitId.equalsIgnoreCase("4")){
            	tempItemUnitDescription = getString(R.string.KILO);
            }else if(tempItemUnitId.equalsIgnoreCase("5")){
            	tempItemUnitDescription = getString(R.string.HUNDRED);
            }else if(tempItemUnitId.equalsIgnoreCase("8")){
            	tempItemUnitDescription = "JUEGO PARTIDO";
            }
        }else{
        	if(tempItemUnitId.equalsIgnoreCase("0")){
            	tempItemUnitDescription = getString(R.string.UNITS);
            }else if(tempItemUnitId.equalsIgnoreCase("2")){
            	tempItemUnitDescription = getString(R.string.METERS);
            }else if(tempItemUnitId.equalsIgnoreCase("4")){
            	tempItemUnitDescription = getString(R.string.KILOS);
            }else if(tempItemUnitId.equalsIgnoreCase("5")){
            	tempItemUnitDescription = getString(R.string.HUNDREDS);
            }else if(tempItemUnitId.equalsIgnoreCase("8")){
            	tempItemUnitDescription = "JUEGOS PARTIDOS";
            }
        }
        
        itemUnidadTxt.setText(tempItemUnitDescription);
        
        
		final Button backButton = (Button) mDialog.findViewById(R.id.backButton);
		
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		
		
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.BOTTOM;
		window.setAttributes(wlp);
		 
		mDialog.show();

	}

	@Override
	public boolean isBluethoothOn() {
		// TODO Auto-generated method stub
		return mBluetoothHelper.isOnHeadsetSco();
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
	
	
	
	
	private void endOfOrderDialog(){

		if(mEndOfOrderDialog != null) {
			mEndOfOrderDialog.dismiss(); 
		}
		
		// mEndOfOrderDialog = new Dialog(this);
		mEndOfOrderDialog.setContentView(R.layout.end_of_order_dialog);
		mEndOfOrderDialog.setTitle(getString(R.string.PAUSED_ITEMS) + ":");
		mEndOfOrderDialog.setCancelable(false);

		Button yesButton = (Button) mEndOfOrderDialog.findViewById(R.id.yesButton);
		Button noButton = (Button) mEndOfOrderDialog.findViewById(R.id.noButton);
		TextView text = (TextView) mEndOfOrderDialog.findViewById(R.id.inputText);
		
		int pausedItems = 0;
		for (int e = 0; e < mItemsList.size(); e++){
			if(mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
				pausedItems++;
			}
		}
		
		String tempText;
		if(pausedItems == 1){
			tempText = getString(R.string.ONE_PAUSED_ITEM) + ". " + getString(R.string.RESUME_PAUSED_ITEMS);
		}else{
			tempText = String.valueOf(pausedItems) + " " + getString(R.string.PAUSED_ITEMS) + ". " + getString(R.string.RESUME_PAUSED_ITEMS);
		}
		
		text.setText(tempText);

		yesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	            mEndOfOrderDialog.dismiss();
	            mute();
	            mIsSistemMute = false;
	            setPausedItemsReady();
	            mItemAdapter.notifyDataSetChanged();
	            mBicycle.ride("getNextItem", false);

			}
		});
		
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEndOfOrderDialog.dismiss();
				mute();
				mBicycle.ride("finishWithPausedItems", false);
				mIsSistemMute = false;
			}
		});
		
		
		mEndOfOrderDialog.show();

	}

	private void setItemStatusErrorDialog(String errorText){

		mSetItemStatusErrorDialog.setContentView(R.layout.set_item_status_error_dialog);
		mSetItemStatusErrorDialog.setTitle(getString(R.string.ERROR) + ":");
		mSetItemStatusErrorDialog.setCancelable(false);

		Button continueButton = (Button) mSetItemStatusErrorDialog.findViewById(R.id.continueButton);
		TextView text = (TextView) mSetItemStatusErrorDialog.findViewById(R.id.inputText);
		text.setText(errorText);

		continueButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSetItemStatusErrorDialog.dismiss(); 
				mBicycle.processDataCallback((DataModule) mModule, "false");
			}
		});

		
		mSetItemStatusErrorDialog.show();

	}

	
	protected void setPausedItemsReady() {
		for (int e = 0; e < mItemsList.size(); e++){
			if(mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
				mItemsList.get(e).setItemStatusId(Config.ITEM_STATUS_READY);
			}
		}
		
		updateStatusLine();
	}
	
	protected void updateStatusLine(){
		
		int readyItems = 0;
		int pausedItems = 0;
		int pickedItems = 0;
		
		for (int e = 0; e < mItemsList.size(); e++){
			if(mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_READY) || mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_IN_PROGRESS)){
				readyItems++;
			} else if(mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
				pausedItems++;
			} else if(mItemsList.get(e).getItemStatusId().equals(Config.ITEM_STATUS_PICKED)){
				pickedItems++;
			}
		}
		
		
		float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics()); 
		
		View readyLine = (View) findViewById(R.id.readyLine);
		LinearLayout.LayoutParams paramsReady = new LinearLayout.LayoutParams(0, (int) dp);
		paramsReady.weight = (float) readyItems;
		readyLine.setLayoutParams(paramsReady);
		
		View pausedLine = (View) findViewById(R.id.pauseLine);
		LinearLayout.LayoutParams paramsPaused = new LinearLayout.LayoutParams(0, (int) dp);
		paramsPaused.weight = (float) pausedItems;
		pausedLine.setLayoutParams(paramsPaused);
		
		View pickedLine = (View) findViewById(R.id.pickedLine);
		LinearLayout.LayoutParams paramsPicked = new LinearLayout.LayoutParams(0, (int) dp);
		paramsPicked.weight = (float) pickedItems;
		pickedLine.setLayoutParams(paramsPicked);
		
	}
	
}

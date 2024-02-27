package com.thirtythreelabs.oktopus;

import java.io.File;
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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thirtythreelabs.adapters.ClosedOrderAdapter;
import com.thirtythreelabs.adapters.OrderAdapter;
import com.thirtythreelabs.comm.FutureBillingOrdersComm;
import com.thirtythreelabs.comm.JsonToOrder;
import com.thirtythreelabs.comm.OrdersComm;
import com.thirtythreelabs.comm.RestTask;
import com.thirtythreelabs.flowmodel.Flow;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.systemmodel.Operator;
import com.thirtythreelabs.systemmodel.Order;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.WriteLog;
import com.thirtythreelabs.util.readXmlResource;


public class ClosedOrdersActivity extends Activity implements OnClickListener{

	private WriteLog mLog = new WriteLog();
	
	private boolean mOnline = true;

	private ClosedOrderAdapter mClosedOrderAdapter;
	private List<Order> mOrdersList;

	private OrdersComm mOrdersComm;
	private FutureBillingOrdersComm mFutureBillingOrdersComm;
	
	private JsonToOrder mJsonToOrder = new JsonToOrder(this);
	
	
	
	private Flow mOktopusFlow;
	
	private Operator mOperator;
	private String mWarehouseId;
	private String mOperatorName;
	
		
	private Button mBackButton;
	private Button mFilterButton;
	private Button mGetOrdersButton;
		
	private ProgressDialog mProgressDialog;
	private CountDownTimer mCountDownTimer;
	
	private String mOrderType;
	
	
	private final Handler mHandler = new Handler();
	private boolean mIsRefreshing;
	private static final int REFRESH_TIME = 1000*60;
	
	private String mFilterText;
	private Boolean mFiltering;
	
	
	@Override
	public void onCreate(Bundle mBundle) {
		super.onCreate(mBundle);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.closed_orders);
				
		ListView mOrdersListView = (ListView) findViewById(R.id.listview);
		mClosedOrderAdapter = new ClosedOrderAdapter(this, this);
		mOrdersListView.setAdapter(mClosedOrderAdapter);
        
		mOperator = (Operator)getIntent().getSerializableExtra("operator");
		mWarehouseId = mOperator.getOperatorCurrentWarehoueseId();
		mOperatorName = mOperator.getOperatorName();

		mBackButton = (Button) findViewById(R.id.backButton);
		mBackButton.setOnClickListener(this);
		
		mFilterButton = (Button) findViewById(R.id.filterButton);
		mFilterButton.setOnClickListener(this);
		
		mGetOrdersButton = (Button) findViewById(R.id.getOrdersButton);
		mGetOrdersButton.setOnClickListener(this);
		
		getOktopusFlow();

	    TextView mFloor = (TextView) findViewById(R.id.operatorAndFloor);
	    mFloor.setText(getString(R.string.app_name) + " | " + getString(R.string.FLOOR) + " " + mWarehouseId + " | " + mOperatorName);
	    
	    mFilterText = "";
	    mFiltering = false;
	}
	
	private final Runnable mTimerRunnable = new Runnable() {
		@Override
        public void run() {
			mClosedOrderAdapter.notifyDataSetChanged();
            if(mIsRefreshing){
            	mHandler.postDelayed(this, REFRESH_TIME);
            }
        }
    };
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(todayBillingOrdersReceiver, new IntentFilter(OrdersComm.GET_ORDERS_ACTION));
		registerReceiver(futureBillingOrdersReceiver, new IntentFilter(FutureBillingOrdersComm.GET_HEADERS_NEXT_INVOICE_ACTION)); 
		
		mOrderType = "today";
		mGetOrdersButton.setText(getResources().getString(R.string.FUTURE_BILLING));
		
		mIsRefreshing = true;
		mHandler.postDelayed(mTimerRunnable, REFRESH_TIME);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(todayBillingOrdersReceiver);
		unregisterReceiver(futureBillingOrdersReceiver);
		
		mIsRefreshing = false;
		mHandler.removeCallbacks(mTimerRunnable);
	}
	
	@Override
    public void onDestroy() {        
        super.onDestroy();
	}
        
	
	public void setToast(String myToast){
		mLog.appendLog(myToast);
		Toast.makeText(this, myToast, Toast.LENGTH_SHORT).show();
    }
	
	
	private BroadcastReceiver todayBillingOrdersReceiver = new BroadcastReceiver() { 
		@Override
		public void onReceive(Context context, Intent intent) { 

			String mJson = intent.getStringExtra(RestTask.HTTP_RESPONSE); 
			
			List<Order> tempOrdersList = mJsonToOrder.getOrdersFromJson(mJson);
			
			populateOrders(tempOrdersList);

		} 
	};
	
	private BroadcastReceiver futureBillingOrdersReceiver = new BroadcastReceiver() { 
		@Override
		public void onReceive(Context context, Intent intent) { 

			String mJson = intent.getStringExtra(RestTask.HTTP_RESPONSE); 
			
			List<Order> tempOrdersList = mJsonToOrder.getOrdersFromJson(mJson);
			
			populateOrders(tempOrdersList);

		} 
	};
	
	
	

	public void populateOrders(List<Order> pOrdersList){
		
		mOrdersList = pOrdersList;
		
		mClosedOrderAdapter.clear();
        
        int tempTotalLines = 0;
        for (Order tempOrder : mOrdersList) {

        	boolean show = false;
        	for (Item tempItem: tempOrder.getItemList()) {
        		if(tempItem.getOriginalItemId().toLowerCase().contains(mFilterText)){
        			show = true;
        		}
        	}
        	
        	if(show || mFilterText.equals("")){
        		mClosedOrderAdapter.add(tempOrder);
                tempTotalLines++;
        	}
        	
        }

        
        if(mProgressDialog != null) {
        	mProgressDialog.dismiss(); 
		}
        
        if(tempTotalLines == 0){
        	// mBicycle.processDataCallback((DataModule) mModule, "false");
        	if(mFiltering){
        		setToast("No existen pedidos que coincidan con el filtro.");
        	}else{
        		waitForOrders();
        	}
        	
        	
        }else{
        	
        	mClosedOrderAdapter.setTotalLineas(tempTotalLines);
        	mClosedOrderAdapter.notifyDataSetChanged();
        	enableButtons();
        }
        
    }
	
	private void waitForOrders(){

		if(mProgressDialog != null) {
			mProgressDialog.dismiss(); 
		}
		
		final Dialog mDialog = new Dialog(this);
		mDialog.setContentView(R.layout.closed_orders_wait_dialog);
		mDialog.setTitle(getString(R.string.WAITING_FOR_ORDERS));
		mDialog.setCancelable(false);
			
		Button backButton = (Button) mDialog.findViewById(R.id.backButton);
		Button retryButton = (Button) mDialog.findViewById(R.id.retryButton);
		
		final TextView text = (TextView) mDialog.findViewById(R.id.inputText);

		mCountDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            	text.setText(getString(R.string.RETRYING_IN) + ": " + String.valueOf((millisUntilFinished/1000)));
            }

            public void onFinish() {
            	mDialog.dismiss();
            	
            	if(mOrderType.equalsIgnoreCase("future")){
					getFutureBillingClosedOrders();
				}else{
					getTodayBillingClosedOrders();
				}
            }
            
         }.start();
         
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCountDownTimer.cancel();
	            mDialog.dismiss();
	            gotoOrderActivity();
			}
		});
		
		retryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCountDownTimer.cancel();
				mDialog.dismiss();
				
				if(mOrderType.equalsIgnoreCase("future")){
					getFutureBillingClosedOrders();
				}else{
					getTodayBillingClosedOrders();
				}
			}
		});
		
		
		mDialog.show();

	}
	
	private void getTodayBillingClosedOrders(){
		CharSequence literal = getString(R.string.DOWNLOADING_ORDERS);
		mProgressDialog = ProgressDialog.show(this, "", literal, true);
		
		mOrdersComm.getOrders(mWarehouseId, Config.ORDER_STATUS_PICKED);
	}
	
	private void getFutureBillingClosedOrders(){
		CharSequence literal = getString(R.string.DOWNLOADING_ORDERS);
		mProgressDialog = ProgressDialog.show(this, "", literal, true);
		
		mFutureBillingOrdersComm.getFutureBillingOrders(mWarehouseId);
	}
	
	private void getOktopusFlow(){
		
		Serializer mSerializer = new Persister();
		// File source = new File("/sdcard/download/Orders.xml");

		try {
			
			mOktopusFlow = mSerializer.read(Flow.class, readXmlResource.readXml(this, R.raw.orders));
			
			String mCompanyId = mOktopusFlow.getCompanyId();
			mOrdersComm = new OrdersComm(this, this, null, mOnline, mCompanyId);
			mFutureBillingOrdersComm = new FutureBillingOrdersComm(this, this, null, mOnline, mCompanyId);
					
			getTodayBillingClosedOrders();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setToast("Error serializando flujo");
			e.printStackTrace();
		}
	}
	
	
	
	private void enableButtons() {
		mBackButton.setEnabled(true);
		mFilterButton.setEnabled(true);
		mGetOrdersButton.setEnabled(true);
	}
	
	private void disableButtons() {
		mBackButton.setEnabled(false);
		mFilterButton.setEnabled(false);
		mGetOrdersButton.setEnabled(false);
	}
	
	
	public void gotoItemsActivity(Order tempOrder){
		Intent tempIntent = null;

		tempIntent = new Intent(this, ItemsActivity.class);

		tempIntent.putExtra("orderId", tempOrder.getOrderId());
		tempIntent.putExtra("orderDate", tempOrder.getOrderDateMillis());
		tempIntent.putExtra("orderTypeId", tempOrder.getOrderTypeId());
		tempIntent.putExtra("orderTypeName", tempOrder.getOrderTypeName());
		tempIntent.putExtra("orderStatusDescription", tempOrder.getOrderStatusDescription());
		tempIntent.putExtra("orderPriorityId", tempOrder.getOrderPriorityId());
		tempIntent.putExtra("orderPriorityName", tempOrder.getOrderPriorityName());
		tempIntent.putExtra("orderCustomerId", tempOrder.getOrderCustomerId());
		tempIntent.putExtra("orderCustomerName", tempOrder.getOrderCustomerName());
		tempIntent.putExtra("orderTotalLines", tempOrder.getOrderTotalLines());
		tempIntent.putExtra("orderSalesmanId", tempOrder.getOrderSalesmanId());
		tempIntent.putExtra("orderSalesmanName", tempOrder.getOrderSalesmanName());
		tempIntent.putExtra("orderNotes", tempOrder.getOrderNotes());
		
		tempIntent.putExtra("warehouseId", mWarehouseId);
		
		startActivity(tempIntent);
	}
	

	
	@Override
	public void onClick(View v) {

		int viewId = v.getId();

		if (viewId == R.id.backButton) {
			gotoOrderActivity();

		} else if (viewId == R.id.filterButton) {
			if (mFiltering) {
				clearFilter();
			} else {
				openFilterDialog();
			}

		} else if (viewId == R.id.getOrdersButton) {
			mFiltering = false;
			mFilterText = "";
			mFilterButton.setText("FILTRAR");

			if (mOrderType.equalsIgnoreCase("future")) {

				mOrderType = "today";
				mGetOrdersButton.setText(getResources().getString(R.string.TODAY_BILLING));
				getTodayBillingClosedOrders();
			} else {
				mOrderType = "future";
				mGetOrdersButton.setText(getResources().getString(R.string.FUTURE_BILLING));
				getFutureBillingClosedOrders();
			}
		}
	}
	
	public void openFilterDialog(){
		final Dialog mDialog = new Dialog(this);
		mDialog.setContentView(R.layout.filter_dialog);
		mDialog.setCancelable(false);
		mDialog.setTitle("Ingrese un artículo:");
			
		Button cancelButton = (Button) mDialog.findViewById(R.id.dialogButtonCancel);
		Button okButton = (Button) mDialog.findViewById(R.id.dialogButtonOK);

		final EditText filterText = (EditText) mDialog.findViewById(R.id.etFilterText);
		filterText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		
        cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	            mDialog.dismiss();
			}
		});
		
         okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				filtrarOrders(filterText.getText().toString());
				mDialog.dismiss();
			}
		});
		
		
		mDialog.show();

	}
	
	public void clearFilter(){
		mFiltering = false;
		mFilterText = "";
		populateOrders(mOrdersList);
		mFilterButton.setText("FILTRAR");
	}
	
	public void filtrarOrders(String chars){
		if(!chars.isEmpty()){
			mFiltering = true;
			mFilterButton.setText("LIMPIAR FILTRO");
			mFilterText = chars.toLowerCase();
			populateOrders(mOrdersList);
		}
		
	}
	
	
	public void gotoOrderActivity(){
		Intent tempIntent = new Intent(this, OrdersActivity.class);
		
		tempIntent.putExtra("operator", mOperator);
		
		startActivity(tempIntent);
	}

}

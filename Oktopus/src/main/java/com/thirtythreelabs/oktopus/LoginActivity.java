package com.thirtythreelabs.oktopus;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thirtythreelabs.comm.JsonToOperator;
import com.thirtythreelabs.comm.LoginComm;
import com.thirtythreelabs.comm.OrdersComm;
import com.thirtythreelabs.comm.RestTask;
import com.thirtythreelabs.flowmodel.Flow;
import com.thirtythreelabs.systemmodel.Operator;
import com.thirtythreelabs.systemmodel.Order;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.ConvertWordToNumber;
import com.thirtythreelabs.util.WriteLog;
import com.thirtythreelabs.util.readXmlResource;

public class LoginActivity extends Activity implements OnClickListener {
	
	private WriteLog mLog = new WriteLog();
	
	private Flow mOktopusFlow;
	
	private String mCompanyId = "0";
	private ActionBar mActionBar;
	private Operator mOperator;
	
	private EditText mOperatorNameText;
	private EditText mOperatorPasswordText;
	private Button mLoginButton;
	
	private NfcAdapter mNfcAdapter;
	private ProgressDialog mProgressDialog;
	private LoginComm mLoginComm;
	private JsonToOperator mJsonToOperator;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] mReadTagFilters;
    private boolean mWriteMode = false;
	
    ConvertWordToNumber mConvertWordToNumber = new ConvertWordToNumber();
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.login);

		mOperatorNameText = (EditText)findViewById(R.id.etNombre);
		mOperatorPasswordText = (EditText)findViewById(R.id.etPassword);
		
		mLoginButton = (Button)findViewById(R.id.ButtonEntrar);
		//mLoginButton.setFocusable(true);
		//mLoginButton.setFocusableInTouchMode(true);///add this line
		// mLoginButton.requestFocus();
		mLoginButton.setOnClickListener(this);
		
		mLoginComm = new LoginComm(this, this, null, true);
		mJsonToOperator = new JsonToOperator(this);
		
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String version = pInfo.versionName;
        
		mLog.appendLog("Start. Version: " + version);
		
		getOktopusFlow();
		
		TextView mTestText = (TextView) findViewById(R.id.testText);
		
		if(Config.URL.equals("http://192.168.1.92/smpm/rest/")){
			mTestText.setVisibility(View.GONE);
		}else{
			mTestText.append("  ---  " + Config.URL);
		}
		
	}
	
	

	@Override
    public void onResume() {
        super.onResume();
        registerReceiver(loginReceiver, new IntentFilter(LoginComm.GET_LOGIN_ACTION)); 
    }

    @Override
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(loginReceiver);
    }

	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	
	
	private void getOktopusFlow(){
		
		Serializer mSerializer = new Persister();
		// File source = new File("/sdcard/download/Orders.xml");

		try {
			mOktopusFlow = mSerializer.read(Flow.class, readXmlResource.readXml(this, R.raw.orders));
			
			mCompanyId = mOktopusFlow.getCompanyId();
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setToast("Error serializando flujo");
			e.printStackTrace();
		}
	}
	

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.ButtonEntrar) {
			CharSequence literal = getString(R.string.PLEASE_WAIT);
			mProgressDialog = ProgressDialog.show(this, "", literal, true);
			
			operatorLogin();
		}	
	}
	
	
	
	
	private BroadcastReceiver loginReceiver = new BroadcastReceiver() { 
		@Override
		public void onReceive(Context context, Intent intent) { 

			String mJson = intent.getStringExtra(RestTask.HTTP_RESPONSE); 
			
			if(mJson != null){
				mOperator = mJsonToOperator.getOperatorFromJson(mJson);
				
				if(mProgressDialog != null) {
					mProgressDialog.dismiss(); 
				}
		        
				
				if(mOperator.getLoginError().equals("N")){
					// setToast(mOperator.getOperatorName());
					// startOrdersActivity(mOperator.getOperatorWarehouses().get(0).getWarehouseId());
					
					if(mOperator.getOperatorWarehouses().size() == 1){
						mOperator.setOperatorCurrentWarehoueseId(mOperator.getOperatorWarehouses().get(0).getWarehouseId());
						gotoOrdersActivity();
					}else{
						gotoChooseWarehouseActivity();
					}
					
				}else{
					mOperatorNameText.setText("");
					mOperatorPasswordText.setText("");
					setToast("ERROR: " + mOperator.getLoginErrorMessage());
				}
			}else{
				setToast("No se puede contactar con el servidor.");
			}
			
			
		} 
	};
	
	private void operatorLogin(){
		
		String mOperatorName = mOperatorNameText.getText().toString().toUpperCase();
		
		String mOperatorPassword = mOperatorPasswordText.getText().toString();
		
		mLoginComm.loginOperator(mCompanyId, mOperatorName, mOperatorPassword);
		
		
	}
	
	private void gotoOrdersActivity(){
		Intent intent = new Intent(this, OrdersActivity.class);
		
		intent.putExtra("operator", mOperator);

		startActivity(intent);
	}
	
	
	private void gotoChooseWarehouseActivity(){
		Intent intent = new Intent(this, ChooseWarehouseActivity.class);
		
		intent.putExtra("operator", mOperator);

		startActivity(intent);
	}
	
	
	public void setToast(String myToast){
		mLog.appendLog(myToast);
		Toast.makeText(this, myToast, Toast.LENGTH_SHORT).show();
    }
}
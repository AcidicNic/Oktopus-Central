package com.thirtythreelabs.comm;

import java.net.URI;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.thirtythreelabs.oktopus.ItemsActivity;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.WriteLog;

public class FutureBillingOrdersComm {
	private Activity mActivity;
	private Context mContext;
	private String mLang;
	private boolean mOnline;
	private String mCompanyId;
	private String mWarehouseId;
	
	private JsonToItem mJsonToItem;
	
	private WriteLog mLog = new WriteLog();
	
	public static final String GET_HEADERS_NEXT_INVOICE_ACTION = "com.thirtythreelabs.oktopus.GET_HEADERS_NEXT_INVOICE_ACTION";

	private static final String GET_HEADERS_NEXT_INVOICE_URI = Config.URL + "getheadersnextinvoice/";
	
	public FutureBillingOrdersComm (Context tempContext, Activity tempActivity, String tempLang, boolean tempOnline, String tempCompanyId){
		mActivity = tempActivity;
		mContext = tempContext;
		mLang = tempLang;
		mOnline = tempOnline;
		mCompanyId = tempCompanyId;
		
		mJsonToItem = new JsonToItem(tempActivity);
	}
	
	
	public void getFutureBillingOrders(String pWarehouseId){
		
		if(mOnline){
			try{
				
				String url = String.format(GET_HEADERS_NEXT_INVOICE_URI);
				HttpPost postRequest = new HttpPost(new URI(url));
				
				String mJson;
				JSONObject object = new JSONObject();
		        try {
		        	object.put("CompanyId", mCompanyId);
		        	object.put("WareHouseId", pWarehouseId);
		        	  
		            mLog.appendLog(object.toString());
		            

		        } catch (Exception ex) {
		        	// setToast("Error: " + ex);
		        }
		        
		        mJson = object.toString();
		        StringEntity entity = new StringEntity(mJson);
		        
		        // setToast(mJson);
		        
		        postRequest.setEntity(entity);
		        postRequest.setHeader("Content-Type", "application/json");
		        
		        RestTask task = new RestTask(mContext, GET_HEADERS_NEXT_INVOICE_ACTION); 
				
				task.execute(postRequest);
				
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}else{
			
			String mJson = "";
			
			/*
				if(mLang == "es" || mLang == "español"){
					mJson = "{\"Lines\":[{\"HeaderId\":\"27876\",\"LineId\":3,\"ItemId\":\"CU3AXS039\",\"ItemDescription\":\"CORREA GATES 13X1041\",\"ItemHowToRead\":\"A, X, S, 0, 39\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"913.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"4.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":4,\"ItemId\":\"CU3AXS040\",\"ItemDescription\":\"CORREA GATES 13X1067\",\"ItemHowToRead\":\"A, X, S, 0, 40\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"451.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":8,\"ItemId\":\"CU3AXS058\",\"ItemDescription\":\"CORREA GATES 13X1524\",\"ItemHowToRead\":\"A, X, S, 0, 58\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"216.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":9,\"ItemId\":\"CU3AXS059\",\"ItemDescription\":\"CORREA GATES 13X1549\",\"ItemHowToRead\":\"A, X, S, 0, 59\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"266.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":1,\"ItemId\":\"CU3G6PK1740\",\"ItemDescription\":\"CORR M-V K060685 VW GOL SANTANA 95 ALT DH PG206 1.9DFULL\",\"ItemHowToRead\":\"G, 6, P, K, 17, 40\",\"ItemDescriptionHowtoRead\":\"CORREA MICRO V\",\"ItemInventory\":\"395.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"1.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"}],\"Error\":\"\",\"ErrorMessage\":\"\"}";
				}else{	
					mJson = "{\"Lines\":[{\"HeaderId\":\"27876\",\"LineId\":3,\"ItemId\":\"CU3AXS039\",\"ItemDescription\":\"CORREA GATES 13X1041\",\"ItemHowToRead\":\"A, X, S, 0, 39\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"913.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"4.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":4,\"ItemId\":\"CU3AXS040\",\"ItemDescription\":\"CORREA GATES 13X1067\",\"ItemHowToRead\":\"A, X, S, 0, 40\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"451.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":8,\"ItemId\":\"CU3AXS058\",\"ItemDescription\":\"CORREA GATES 13X1524\",\"ItemHowToRead\":\"A, X, S, 0, 58\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"216.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":9,\"ItemId\":\"CU3AXS059\",\"ItemDescription\":\"CORREA GATES 13X1549\",\"ItemHowToRead\":\"A, X, S, 0, 59\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"266.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":1,\"ItemId\":\"CU3G6PK1740\",\"ItemDescription\":\"CORR M-V K060685 VW GOL SANTANA 95 ALT DH PG206 1.9DFULL\",\"ItemHowToRead\":\"G, 6, P, K, 17, 40\",\"ItemDescriptionHowtoRead\":\"CORREA MICRO V\",\"ItemInventory\":\"395.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"1.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"}],\"Error\":\"\",\"ErrorMessage\":\"\"}";
				}
				
				List<Item> tempItemsList = mJsonToItem.getItemsFromJson(mJson);
				
				((ItemsActivity) mActivity).populateItems(tempItemsList);

			*/
		}
	}
	
}

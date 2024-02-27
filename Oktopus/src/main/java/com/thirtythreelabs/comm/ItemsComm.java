package com.thirtythreelabs.comm;

import java.net.URI;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.thirtythreelabs.oktopus.ItemsActivity;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.WriteLog;

public class ItemsComm {
	private Activity mActivity;
	private Context mContext;
	private String mLang;
	private boolean mOnline;
	private String mCompanyId;
	
	private JsonToItem mJsonToItem;
	
	private WriteLog mLog = new WriteLog();
	
	public static final String GET_ITEMS_ACTION = "com.thirtythreelabs.oktopus.GET_ITEMS_ACTION";
	
	public static final String SET_ITEM_STATUS_TO_IN_PROGRESS_ACTION = "com.thirtythreelabs.oktopus.SET_ITEM_STATUS_TO_IN_PROGRESS_ACTION"; 
	public static final String SET_ITEM_STATUS_TO_PAUSED_ACTION = "com.thirtythreelabs.oktopus.SET_ITEM_STATUS_TO_PAUSED_ACTION";
	public static final String SET_ITEM_STATUS_TO_PICKED_ACTION = "com.thirtythreelabs.oktopus.SET_ITEM_STATUS_TO_PICKED_ACTION";
	public static final String SET_ITEM_STATUS_TO_IN_PROGRESS_MANUAL_ACTION = "com.thirtythreelabs.oktopus.SET_ITEM_STATUS_TO_IN_PROGRESS_MANUAL_ACTION"; 
	
	private static final String GET_ITEMS_URI = Config.URL + "getlinesv2/";
	private static final String SET_ITEMS_STATUS_URI = Config.URL + "setitemstatusv1/";
	
	
	public ItemsComm (Context tempContext, Activity tempActivity, String tempLang, boolean tempOnline, String tempCompanyId){
		mActivity = tempActivity;
		mContext = tempContext;
		mLang = tempLang;
		mOnline = tempOnline;
		mCompanyId = tempCompanyId;
		
		mJsonToItem = new JsonToItem(tempActivity);
	}
	
	
	public void getItems(String orderId, String pWarehouseId){
		
		if(mOnline){
			try{
				
				String url = String.format(GET_ITEMS_URI);
				HttpPost postRequest = new HttpPost(new URI(url));
				
				String mJson;
				JSONObject object = new JSONObject();
		        try {
		        	object.put("CompanyId", mCompanyId);
		            object.put("HeaderId", orderId.replace(" (Facturación futura)", ""));
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
		        
		        RestTask task = new RestTask(mContext, GET_ITEMS_ACTION); 
				
				task.execute(postRequest);
				
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}else{
			
			String mJson = "";
			
			/*
				if(mLang == "es" || mLang == "espa�ol"){
					mJson = "{\"Lines\":[{\"HeaderId\":\"27876\",\"LineId\":3,\"ItemId\":\"CU3AXS039\",\"ItemDescription\":\"CORREA GATES 13X1041\",\"ItemHowToRead\":\"A, X, S, 0, 39\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"913.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"4.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":4,\"ItemId\":\"CU3AXS040\",\"ItemDescription\":\"CORREA GATES 13X1067\",\"ItemHowToRead\":\"A, X, S, 0, 40\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"451.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":8,\"ItemId\":\"CU3AXS058\",\"ItemDescription\":\"CORREA GATES 13X1524\",\"ItemHowToRead\":\"A, X, S, 0, 58\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"216.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":9,\"ItemId\":\"CU3AXS059\",\"ItemDescription\":\"CORREA GATES 13X1549\",\"ItemHowToRead\":\"A, X, S, 0, 59\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"266.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":1,\"ItemId\":\"CU3G6PK1740\",\"ItemDescription\":\"CORR M-V K060685 VW GOL SANTANA 95 ALT DH PG206 1.9DFULL\",\"ItemHowToRead\":\"G, 6, P, K, 17, 40\",\"ItemDescriptionHowtoRead\":\"CORREA MICRO V\",\"ItemInventory\":\"395.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"1.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"}],\"Error\":\"\",\"ErrorMessage\":\"\"}";
				}else{	
					mJson = "{\"Lines\":[{\"HeaderId\":\"27876\",\"LineId\":3,\"ItemId\":\"CU3AXS039\",\"ItemDescription\":\"CORREA GATES 13X1041\",\"ItemHowToRead\":\"A, X, S, 0, 39\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"913.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"4.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":4,\"ItemId\":\"CU3AXS040\",\"ItemDescription\":\"CORREA GATES 13X1067\",\"ItemHowToRead\":\"A, X, S, 0, 40\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"451.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":8,\"ItemId\":\"CU3AXS058\",\"ItemDescription\":\"CORREA GATES 13X1524\",\"ItemHowToRead\":\"A, X, S, 0, 58\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"216.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":9,\"ItemId\":\"CU3AXS059\",\"ItemDescription\":\"CORREA GATES 13X1549\",\"ItemHowToRead\":\"A, X, S, 0, 59\",\"ItemDescriptionHowtoRead\":\"CORREA INDUSTRIAL\",\"ItemInventory\":\"266.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"2.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"},{\"HeaderId\":\"27876\",\"LineId\":1,\"ItemId\":\"CU3G6PK1740\",\"ItemDescription\":\"CORR M-V K060685 VW GOL SANTANA 95 ALT DH PG206 1.9DFULL\",\"ItemHowToRead\":\"G, 6, P, K, 17, 40\",\"ItemDescriptionHowtoRead\":\"CORREA MICRO V\",\"ItemInventory\":\"395.00\",\"BrandId\":\"GATES\",\"BrandName\":\"GATES\",\"ItemImage\":\"\",\"UnitId\":0,\"UnitDescription\":\"UNIDADES\",\"ItemOrigin\":\"BRASIL\",\"ItemQuantity\":\"1.00\",\"ItemStatusId\":\"F\",\"ItemStatusDescription\":\"\",\"ItemLastOperatorId\":0,\"ItemLastOperatorName\":\"\",\"ItemXCoord\":\"\",\"ItemYCoord\":\"\",\"ItemZCoord\":\"\",\"ItemSupplierId\":500094,\"ItemSupplierName\":\"GATES BRASIL\"}],\"Error\":\"\",\"ErrorMessage\":\"\"}";
				}
				
				List<Item> tempItemsList = mJsonToItem.getItemsFromJson(mJson);
				
				((ItemsActivity) mActivity).populateItems(tempItemsList);

			*/
		}
	}
	
	public void setItemStatus(String pOrderId, String pItemId, String pStatusId, String pItemPickedQuantity, Boolean pManually, String pOperatorId){
		
		if(mOnline){
			try{
				
				String url = String.format(SET_ITEMS_STATUS_URI);
				HttpPost postRequest = new HttpPost(new URI(url));
				
				String mJson;
				JSONObject object = new JSONObject();
		        try {
		        	object.put("CompanyId", mCompanyId);
		            object.put("HeaderId", pOrderId.replace(" (Facturación futura)", ""));
		            object.put("LineId", pItemId);
		            if(pStatusId.equals(Config.ITEM_STATUS_READY)){
		            	object.put("StatusId", "F");
		            }else if(pStatusId.equals(Config.ITEM_STATUS_IN_PROGRESS)){
		            	object.put("StatusId", "I");
		            }else{
		            	object.put("StatusId", pStatusId);
		            }
		            object.put("ItemPickedQuantity", pItemPickedQuantity);
		            object.put("OperatorId", pOperatorId);
		            
		            mLog.appendLog(object.toString());
		            

		        } catch (Exception ex) {
		        	// setToast("Error: " + ex);
		        }
		        
		        mJson = object.toString();
		        StringEntity entity = new StringEntity(mJson);
		        
		        // setToast(mJson);
		        
		        postRequest.setEntity(entity);
		        postRequest.setHeader("Content-Type", "application/json");
		        
		        String TempAction = "";
		        
		        if(pManually){
		        	TempAction = SET_ITEM_STATUS_TO_IN_PROGRESS_MANUAL_ACTION;
		        } else if(pStatusId.equals(Config.ITEM_STATUS_IN_PROGRESS)){
		        	TempAction = SET_ITEM_STATUS_TO_IN_PROGRESS_ACTION;
		        }else if (pStatusId.equals(Config.ITEM_STATUS_PAUSED)){
		        	TempAction = SET_ITEM_STATUS_TO_PAUSED_ACTION;
		        }else if (pStatusId.equals(Config.ITEM_STATUS_PICKED)){
		        	TempAction = SET_ITEM_STATUS_TO_PICKED_ACTION;
		        }
				
		        RestTask task = new RestTask(mContext, TempAction); 
				
				task.execute(postRequest);
				
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}else{
			
			String mJson = "";
			
			/*if(mPedidoId.equals("83346")){
				if(mLang == "es" || mLang == "espa�ol"){
					mJson = "{\"Lines\":[{\"LineId\":\"1\",\"ItemId\":\"7252L-ROJO\",\"ItemDescription\":\"PINZA CARGADOR DE BATER�A GRANDE, ROJO\",\"ItemHowToRead\":\"72, 52, L, ROJO\",\"ItemInventory\":\"10\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNIDAD\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"1\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}, {\"LineId\":\"2\",\"ItemId\":\"PCMA408601D\",\"ItemDescription\":\"PREC�MARA MAZDA\",\"ItemHowToRead\":\"PE, C, M, A, 408, 601 D\",\"ItemInventory\":\"2\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNIDAD\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"23\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"},{\"LineId\":\"3\",\"ItemId\":\"F000SH0134\",\"ItemDescription\":\"AUTO ARRANQUE FIAT PALIO\",\"ItemHowToRead\":\"F, TRIPLECERO, S, H, 0, 134\",\"ItemInventory\":\"12\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNIDAD\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"5\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}, {\"LineId\":\"4\",\"ItemId\":\"XX104-INOX\",\"ItemDescription\":\"ABRAZADERA ACERO INOXIDABLE\",\"ItemHowToRead\":\"X, X, 104, INOX\",\"ItemInventory\":\"34\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNIDAD\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"9\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}, {\"LineId\":\"5\",\"ItemId\":\"THM28100A/1\",\"ItemDescription\":\"ALFOMBRA DE GOMA PARA BA�L DE AUTO\",\"ItemHowToRead\":\"T, H, M, 28, 100, A, BARRA 1\",\"ItemInventory\":\"12\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNIDAD\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"14\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}]}";
				}else{	
					mJson = "{\"Lines\":[{\"LineId\":\"1\",\"ItemId\":\"V1020\",\"ItemDescription\":\"Disintegrating Pistol\",\"ItemHowToRead\":\"V, 10, 20\",\"ItemInventory\":\"10\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNITS\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"1\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}, {\"LineId\":\"2\",\"ItemId\":\"AC109\",\"ItemDescription\":\"Female Road Runner Costume\",\"ItemHowToRead\":\"A, C, 109\",\"ItemInventory\":\"2\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNITS\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"2\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"},{\"LineId\":\"3\",\"ItemId\":\"20L\",\"ItemDescription\":\"Hi-Speed Tonic\",\"ItemHowToRead\":\"20, L\",\"ItemInventory\":\"12\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNITS\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"100\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}, {\"LineId\":\"4\",\"ItemId\":\"P 23\",\"ItemDescription\":\"Matches\",\"ItemHowToRead\":\"P, 23\",\"ItemInventory\":\"34\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"2\",\"UnitDescription\":\"BOXES\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"5\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}, {\"LineId\":\"5\",\"ItemId\":\"AXS087\",\"ItemDescription\":\"Earthquake Pills\",\"ItemHowToRead\":\"A, X, S. 0, 87\",\"ItemInventory\":\"12\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNITS\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"28\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}]}";
				}
			}
			
			if(mPedidoId.equals("83347")){
				if(mLang == "es" || mLang == "espa�ol"){
					mJson = "{\"Lines\":[{\"LineId\":\"1\",\"ItemId\":\"EVITA-SOL\",\"ItemDescription\":\"EVITA SOLDADURA 3MM\",\"ItemHowToRead\":\"EVITA SOL\",\"ItemInventory\":\"12\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNIDAD\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"1\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}, {\"LineId\":\"2\",\"ItemId\":\"CFTCORCEL\",\"ItemDescription\":\"FLEXIBLE TRAS FORD CORCEL\",\"ItemHowToRead\":\"C, F, T, CORCEL\",\"ItemInventory\":\"235.50\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"1\",\"UnitDescription\":\"UNIDAD\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"4\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"},{\"LineId\":\"3\",\"ItemId\":\"HZ025X050\",\"ItemDescription\":\"TORNILLO 1/4X1/2\",\"ItemHowToRead\":\"H, Z, 0, 25, POR, 0, 50\",\"ItemInventory\":\"12\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"1\",\"UnitDescription\":\"CAJAS\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"3\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}]}";
				}else{	
					mJson = "{\"Lines\":[{\"LineId\":\"1\",\"ItemId\":\"345RT\",\"ItemDescription\":\"Do-It-Yourself Tornado Kit\",\"ItemHowToRead\":\"345, R, T\",\"ItemInventory\":\"12\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNITS\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"29\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}, {\"LineId\":\"2\",\"ItemId\":\"AK23234\",\"ItemDescription\":\"Invisible Paint\",\"ItemHowToRead\":\"A, K, 23, 234\",\"ItemInventory\":\"235.50\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"1\",\"UnitDescription\":\"GALLONS\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"2.50\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"},{\"LineId\":\"3\",\"ItemId\":\"AXS087\",\"ItemDescription\":\"Earthquake Pills\",\"ItemHowToRead\":\"A, X, S. 0, 87\",\"ItemInventory\":\"12\",\"BrandId\":\"1\",\"BrandName\":\"ACME\",\"ItemImage\":\"\",\"UnitId\":\"0\",\"UnitDescription\":\"UNITS\",\"ItemOrigin\":\"CHINA\",\"ItemQuantity\":\"12\",\"StatusId\":\"0\",\"StatusDescription\":\"ready\",\"ItemLastOperatorId\":\"1\", \"ItemAisle\":\"21\", \"ItemRow\":\"3\", \"ItemLevel\":\"4A\"}]}";
				}
			}*/
			
			
		}
	}

}

package com.thirtythreelabs.adapters;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Second;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thirtythreelabs.oktopus.OrdersActivity;
import com.thirtythreelabs.oktopus.R;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.systemmodel.Order;


public class ClosedOrderAdapter extends ArrayAdapter<Order> {
    private final LayoutInflater mInflater;
    private Context mContext;
    private Activity mActivity;
    
    private Boolean buttonsStatus = false;
    

    private class ViewHolder {
		    	   
		TextView mOrderId;
		TextView mOrderDate;
		TextView mOrderBillingDate;
		TextView mSalesmanName;
		TextView mCustomerName;
		TextView mOrderType;
		TextView mOrderQuantity;
		TextView mOrderNotes;
		TextView mOrderItemsList;
				
	}

    public ClosedOrderAdapter(Context context, Activity activity) {
    	super(context, android.R.layout.simple_list_item_2);
    	this.mActivity = activity;
    	this.mContext = context;   
    	mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	        
    }

    public void setData(List<Order> data) {
        clear();
        if (data != null) {
            for (Order appEntry: data) {
                add(appEntry);
            }
        }
    }
    

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View row = convertView;
        ViewHolder holder = null;
        
        final int pos = position;
        
        if (row == null) {

        	row = mInflater.inflate(R.layout.closed_order, parent, false);

            holder = new ViewHolder();
                        
            holder.mOrderId = (TextView) row.findViewById(R.id.orderId);
            holder.mOrderDate = (TextView) row.findViewById(R.id.orderDate);
            holder.mOrderBillingDate = (TextView) row.findViewById(R.id.orderBillingDate);
            holder.mSalesmanName = (TextView) row.findViewById(R.id.salesmanName);
            holder.mCustomerName = (TextView) row.findViewById(R.id.customerName);
            holder.mOrderType  = (TextView) row.findViewById(R.id.orderType);
            holder.mOrderQuantity = (TextView) row.findViewById(R.id.orderQuantity);
            holder.mOrderNotes = (TextView) row.findViewById(R.id.orderNotes);
            
            
            holder.mOrderItemsList = (TextView) row.findViewById(R.id.orderItemsList);
                 
            row.setTag(holder);
  
        } else {

             holder = (ViewHolder) row.getTag();
        }

        Order order = getItem(position);
        Resources res = getContext().getResources();
        
        Boolean futureBilling = order.getOrderFutureBilling();
        Boolean orderImportant = order.getOrderImportant();
        String orderRefId = order.getOrderRefId();
        
        Boolean isRepo = false;
        if(order.getOrderTypeId().equals("89")){
        	isRepo = true;
        }
        
        if(orderImportant){
        	holder.mOrderId.setText(order.getOrderId() + " (Ref: " + orderRefId + ")");
        }else{
        	holder.mOrderId.setText(order.getOrderId());
        }
        
        Date now = new Date();
		long millisecondsNow = now.getTime();
		
		String result = (String) DateUtils.getRelativeTimeSpanString(order.getOrderDateMillis(), millisecondsNow, 0);
		
		PrettyTime p = new PrettyTime();

		p.setLocale(new Locale("es"));
		List<Duration> durations = p.calculatePreciseDuration(new Date(order.getOrderDateMillis()));
		if(durations.size() > 1){
			durations.remove(durations.size()-1);
		}
		
		String dato = p.format(durations);
		
        holder.mOrderDate.setText(dato + " (" + order.getOrderDate() + ")");
        
		if(futureBilling){
			holder.mOrderBillingDate.setText(Html.fromHtml(res.getString(R.string.BILLING_DATE) + ": <b>" + order.getOrderBillingDate() + "</b>"));
			holder.mOrderBillingDate.setVisibility(View.VISIBLE);
        }else{
        	holder.mOrderBillingDate.setVisibility(View.GONE);
        }
		
		holder.mSalesmanName.setText(Html.fromHtml(res.getString(R.string.SALESMAN) + ": <b>" + order.getOrderSalesmanId() + " - " + order.getOrderSalesmanName().toUpperCase() + "</b>"));
        holder.mCustomerName.setText(Html.fromHtml(res.getString(R.string.CUSTOMER) + ": <b>" + order.getOrderCustomerName().toUpperCase() + "</b>"));
        holder.mOrderType.setText(Html.fromHtml(res.getString(R.string.ORDER_TYPE) + ": <b>" + order.getOrderTypeName().toUpperCase() + "</b>"));
        holder.mOrderQuantity.setText(String.valueOf(order.getOrderLinesPicked()));
        holder.mOrderNotes.setText(order.getOrderNotes());
        
        
        String finalHeader = "";
        for (int e = 0; e < order.getItemList().size(); e++) {
        	
        	Item tempItem = order.getItemList().get(e);
        	
        	String qty = "";
			if(Float.parseFloat(tempItem.getItemQuantityPicked()) == Float.parseFloat(tempItem.getItemQuantity())){
				qty = " (" + tempItem.getItemQuantity().replace(".00", "") + "/" + tempItem.getItemQuantityPicked().replace(".00", "") + ")"; 
			}else if(Float.parseFloat(tempItem.getItemQuantity()) > Float.parseFloat(tempItem.getItemQuantityPicked())){
				qty = "<font color='blue'> (" + tempItem.getItemQuantity().replace(".00", "") + "/" + tempItem.getItemQuantityPicked().replace(".00", "") + ")</font> "; 
			}else{
				qty = "<font color='red'> (" + tempItem.getItemQuantity().replace(".00", "") + "/" + tempItem.getItemQuantityPicked().replace(".00", "") + ")</font> "; 
			}
			
			finalHeader += tempItem.getItemFamily() + tempItem.getItemId() + qty + "<br />";
        }
        
        
		holder.mOrderItemsList.setText(Html.fromHtml(finalHeader));
		
        LinearLayout ActiveItem = (LinearLayout) row;
        
        
    	if (totalLineas == 1){
    		if(futureBilling){
    			ActiveItem.setBackgroundResource(R.drawable.item_picked_unique_special);
    		} else if (orderImportant){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_unique_imp);
			} else if (isRepo){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_unique_repo);
			} else{
    			ActiveItem.setBackgroundResource(R.drawable.item_picked_unique);
    		}
    		
     	} else if (position == 0){
     		if(futureBilling){
    			ActiveItem.setBackgroundResource(R.drawable.item_picked_first_special);
    		} else if (orderImportant){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_first_imp);
			} else if (isRepo){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_first_repo);
			} else{
    			ActiveItem.setBackgroundResource(R.drawable.item_picked_first);
    		}
    		
     	} else if (position == totalLineas-1) {
     		if(futureBilling){
    			ActiveItem.setBackgroundResource(R.drawable.item_picked_last_special);
    		} else if (orderImportant){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_last_imp);
			} else if (isRepo){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_last_repo);
			} else{
    			ActiveItem.setBackgroundResource(R.drawable.item_picked_last);
    		}
     		
    	} else {
    		if(futureBilling){
    			ActiveItem.setBackgroundResource(R.drawable.item_picked_special);
    		} else if (orderImportant){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_imp);
			} else if (isRepo){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_repo);
			} else{
    			ActiveItem.setBackgroundResource(R.drawable.item_picked);
    		}
    		
    	}
    	
        return row;
        
    }
    
    private int totalLineas = 0;

	public void setTotalLineas(int tempTotalLineas) {
		totalLineas = (tempTotalLineas);
	}



}
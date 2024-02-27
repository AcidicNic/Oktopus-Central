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
import com.thirtythreelabs.systemmodel.Order;


public class OrderAdapter extends ArrayAdapter<Order> {
    private final LayoutInflater mInflater;
    private Context mContext;
    private Activity mActivity;
    
    private Boolean buttonsStatus = false;

    private class ViewHolder {
    	
    	Button mTakeThisOrderButton;
		    	   
		TextView mOrderId;
		TextView mOrderDate;
		TextView mOrderBillingDate;
		TextView mSalesmanName;
		TextView mCustomerName;
		TextView mOrderType;
		TextView mOrderQuantity;
		TextView mOrderNotes;
		TextView mOrderProgress;
		
		TextView mOrderTotalItems;
		TextView mItemsDiffToPick;
		TextView mItemsPicked;
		TextView mItemsPaused;

    }

    public OrderAdapter(Context context, Activity activity) {
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

        	row = mInflater.inflate(R.layout.order, parent, false);

            holder = new ViewHolder();
            holder.mTakeThisOrderButton = (Button) row.findViewById(R.id.takeThisOrderButton);
            
            //holder.mTakeThisOrderButton.setFocusable(true);
            //holder.mTakeThisOrderButton.setFocusableInTouchMode(true);///add this line
            //holder.mTakeThisOrderButton.requestFocus();
            
            holder.mOrderId = (TextView) row.findViewById(R.id.orderId);
            holder.mOrderDate = (TextView) row.findViewById(R.id.orderDate);
            holder.mOrderBillingDate = (TextView) row.findViewById(R.id.orderBillingDate);
            holder.mSalesmanName = (TextView) row.findViewById(R.id.salesmanName);
            holder.mCustomerName = (TextView) row.findViewById(R.id.customerName);
            holder.mOrderType  = (TextView) row.findViewById(R.id.orderType);
            holder.mOrderQuantity = (TextView) row.findViewById(R.id.orderQuantity);
            holder.mOrderNotes = (TextView) row.findViewById(R.id.orderNotes);

            holder.mOrderTotalItems  = (TextView) row.findViewById(R.id.orderTotalItems);
            holder.mItemsDiffToPick  = (TextView) row.findViewById(R.id.itemsDiffToPick);
            holder.mItemsPicked  = (TextView) row.findViewById(R.id.itemsPicked);
            holder.mItemsPaused  = (TextView) row.findViewById(R.id.itemsPaused);
           
            row.setTag(holder);
  
        } else {

             holder = (ViewHolder) row.getTag();
        }

        Order order = getItem(position);
        Resources res = getContext().getResources();
        
        Boolean futureBilling = order.getOrderFutureBilling();
        Boolean futureBilling2 = order.getOrderFutureBilling2();
        Boolean orderImportant = order.getOrderImportant();
        String orderRefId = order.getOrderRefId();
        
        Boolean isRepo = false;
        if(order.getOrderTypeId().equals("89")){
        	isRepo = true;
        }
        
        Boolean isNewColor = false;
        if(order.getOrderTypeId().equals("0") && order.getPedHTComId().equals("010")){
        	isNewColor = true;
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
        
        
        holder.mOrderQuantity.setText(String.valueOf(order.getOrderTotalLines() - order.getOrderLinesPicked()));
        holder.mOrderNotes.setText(order.getOrderNotes());
        
        holder.mOrderTotalItems.setText(String.valueOf(res.getString(R.string.OF) + " " + order.getOrderTotalLines()));
        
        if(order.getOrderLinesDiffToPick() > 0){
        	if(order.getOrderLinesDiffToPick() == 1){
	        	holder.mItemsDiffToPick.setText(" " + String.valueOf(order.getOrderLinesDiffToPick()) + " " + res.getString(R.string.QTY_TRICKY_ITEM) + " ");
	        }else{
	        	holder.mItemsDiffToPick.setText(" " + String.valueOf(order.getOrderLinesDiffToPick()) + " " + res.getString(R.string.QTY_TRICKY_ITEMS) + " ");	
	        }
        	holder.mItemsDiffToPick.setVisibility(View.VISIBLE);
        }else{
        	holder.mItemsDiffToPick.setVisibility(View.GONE);
        }
        
        if(order.getOrderLinesPicked() > 0){
	        if(order.getOrderLinesPicked() == 1){
	            holder.mItemsPicked.setText(" " + String.valueOf(order.getOrderLinesPicked()) + " " + res.getString(R.string.QTY_PICKED_ITEM) + " ");        	
	        }else{
	            holder.mItemsPicked.setText(" " + String.valueOf(order.getOrderLinesPicked()) + " " + res.getString(R.string.QTY_PICKED_ITEMS) + " ");
	        }
	        holder.mItemsPicked.setVisibility(View.VISIBLE);
	    }else{
	    	holder.mItemsPicked.setVisibility(View.GONE);
	    }
        
        if(order.getOrderLinesPaused() > 0){
	        if(order.getOrderLinesPaused() == 1){
	            holder.mItemsPaused.setText(" " + String.valueOf(order.getOrderLinesPaused()) + " " + res.getString(R.string.QTY_PAUSED_ITEM) + " ");       	
	        }else{
	            holder.mItemsPaused.setText(" " + String.valueOf(order.getOrderLinesPaused()) + " " + res.getString(R.string.QTY_PAUSED_ITEMS) + " ");
	        }
	        holder.mItemsPaused.setVisibility(View.VISIBLE);
        }else{
        	holder.mItemsPaused.setVisibility(View.GONE);
        }
        
        
        int percentage = (int) (((order.getOrderLinesPicked())*100.0f/order.getOrderTotalLines()));
		// holder.mOrderProgress.setText(percentage + "%");
        
        if(buttonsStatus){
        	holder.mTakeThisOrderButton.setEnabled(true);
        }else{
        	holder.mTakeThisOrderButton.setEnabled(false);
        }
        
        
        LinearLayout ActiveItem = (LinearLayout) row;

        
		if (position == selectedItem) {
			if(futureBilling2){
				ActiveItem.setBackgroundResource(R.drawable.item_selected_fut);
			} else if(futureBilling){
				ActiveItem.setBackgroundResource(R.drawable.item_selected_special);
			} else if (orderImportant){
				ActiveItem.setBackgroundResource(R.drawable.item_selected_imp);
			} else if (isRepo){
				ActiveItem.setBackgroundResource(R.drawable.item_selected_repo);
			} else if (isNewColor){
				ActiveItem.setBackgroundResource(R.drawable.item_selected_new);
			} else {
				ActiveItem.setBackgroundResource(R.drawable.item_selected);
			}
			
			if (totalLineas == 1){
				if(futureBilling2){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_unique_fut);
				} else if(futureBilling){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_unique_special);
				} else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_unique_imp);
				} else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_unique_repo);
				} else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_unique_new);
				} else{
					ActiveItem.setBackgroundResource(R.drawable.item_selected_unique);
				}
        		
        	} else if (position == 0){
        		if(futureBilling2){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_first_fut);
				} else if(futureBilling){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_first_special);
				} else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_first_imp);
				} else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_first_repo);
				} else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_first_new);
				} else {
					ActiveItem.setBackgroundResource(R.drawable.item_selected_first);
				}
        		
        	} else if (position == totalLineas-1) {
        		if(futureBilling2){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_last_fut);
				} else if(futureBilling){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_last_special);
				} else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_last_imp);
				} else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_last_repo);
				} else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_selected_last_new);
				} else {
					ActiveItem.setBackgroundResource(R.drawable.item_selected_last);
				}
        		
        	} 
		} else {
			if(futureBilling2){
				ActiveItem.setBackgroundResource(R.drawable.item_fut);
			} else if(futureBilling){
				ActiveItem.setBackgroundResource(R.drawable.item_special);
			} else if (orderImportant){
				ActiveItem.setBackgroundResource(R.drawable.item_imp);
			} else if (isRepo){
				ActiveItem.setBackgroundResource(R.drawable.item_repo);
			} else if (isNewColor){
				ActiveItem.setBackgroundResource(R.drawable.item_new);
			} else{
				ActiveItem.setBackgroundResource(R.drawable.item);
			}
        	
        	
        	if (totalLineas == 1){
        		if(futureBilling2){
        			ActiveItem.setBackgroundResource(R.drawable.item_unique_fut);
    			} else if(futureBilling){
        			ActiveItem.setBackgroundResource(R.drawable.item_unique_special);
    			} else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_unique_imp);
				} else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_unique_repo);
				} else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_unique_new);
				} else{
    				ActiveItem.setBackgroundResource(R.drawable.item_unique);
    			}
        		
         	} else if (position == 0){
         		if(futureBilling2){
         			ActiveItem.setBackgroundResource(R.drawable.item_first_fut);
    			} else if(futureBilling){
         			ActiveItem.setBackgroundResource(R.drawable.item_first_special);
    			} else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_first_imp);
				} else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_first_repo);
				} else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_first_new);
				} else{
    				ActiveItem.setBackgroundResource(R.drawable.item_first);
    			}
        		
         	} else if (position == totalLineas-1) {
         		if(futureBilling2){
         			ActiveItem.setBackgroundResource(R.drawable.item_last_fut);
    			} else if(futureBilling){
         			ActiveItem.setBackgroundResource(R.drawable.item_last_special);
    			} else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_last_imp);
				} else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_last_repo);
				} else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_last_new);
				} else{
    				ActiveItem.setBackgroundResource(R.drawable.item_last);
    			}
         		
        	} 
		}
        	
		if (order.getOrderStatusId().equalsIgnoreCase("k")) {
			if(futureBilling2){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_fut);
			} else if(futureBilling){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_special);
			} else if (orderImportant){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_imp);
			} else if (isRepo){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_repo);
			} else if (isNewColor){
				ActiveItem.setBackgroundResource(R.drawable.item_picked_new);
			} else{
				ActiveItem.setBackgroundResource(R.drawable.item_picked);
			}
			
        	
        	if (totalLineas == 1){
        		if(futureBilling2){
        			ActiveItem.setBackgroundResource(R.drawable.item_picked_unique_fut);
    			}else if(futureBilling){
        			ActiveItem.setBackgroundResource(R.drawable.item_picked_unique_special);
    			}else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_unique_imp);
				} else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_repo);
				} else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_new);
				}else{
    				ActiveItem.setBackgroundResource(R.drawable.item_picked_unique);
    			}
        		
         	} else if (position == 0){
         		if(futureBilling2){
         			ActiveItem.setBackgroundResource(R.drawable.item_picked_first_fut);
    			} else if(futureBilling){
         			ActiveItem.setBackgroundResource(R.drawable.item_picked_first_special);
    			} else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_first_imp);
				} else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_first_repo);
				} else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_first_new);
				} else{
    				ActiveItem.setBackgroundResource(R.drawable.item_picked_first);
    			}
        		
         	} else if (position == totalLineas-1) {
         		if(futureBilling2){
         			ActiveItem.setBackgroundResource(R.drawable.item_picked_last_fut);
    			}else if(futureBilling){
         			ActiveItem.setBackgroundResource(R.drawable.item_picked_last_special);
    			}else if (orderImportant){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_last_imp);
				}else if (isRepo){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_last_repo);
				}else if (isNewColor){
					ActiveItem.setBackgroundResource(R.drawable.item_picked_last_new);
				}else{
    				ActiveItem.setBackgroundResource(R.drawable.item_picked_last);
    			}
         		
        	}
        	
        	holder.mTakeThisOrderButton.setVisibility(View.GONE);
		}
        
        holder.mTakeThisOrderButton.setOnClickListener( new View.OnClickListener() {  
        	public void onClick(View v) {  
        		Order tempOrder = getItem(pos);
        		((OrdersActivity) mActivity).gotoItemsActivity(tempOrder);
        		
        	}  
        }); 
        
        return row;
        
    }
    
   
    private int selectedItem = 0;
    private int totalLineas = 0;

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

	public void setTotalLineas(int tempTotalLineas) {
		totalLineas = (tempTotalLineas);
	}
	
    public void setButtonsStatus(Boolean status) {
    	buttonsStatus = status;
    }
    
}
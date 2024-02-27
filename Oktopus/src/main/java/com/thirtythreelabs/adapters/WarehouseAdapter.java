package com.thirtythreelabs.adapters;

import java.util.Date;
import java.util.List;

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
import com.thirtythreelabs.systemmodel.Warehouse;


public class WarehouseAdapter extends ArrayAdapter<Warehouse> {
    private final LayoutInflater mInflater;
    private Context mContext;
    private Activity mActivity;
    
    private Boolean buttonsStatus = false;

    private class ViewHolder {
		TextView mWarehouseName;
		TextView mQtyOfHeaders;
		TextView mQtyOfOrders;
    }

    public WarehouseAdapter(Context context, Activity activity) {
    	super(context, android.R.layout.simple_list_item_2);
    	this.mActivity = activity;
    	this.mContext = context;   
    	mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
    }

    public void setData(List<Warehouse> data) {
        clear();
        if (data != null) {
            for (Warehouse appEntry: data) {
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

        	row = mInflater.inflate(R.layout.warehouse, parent, false);

            holder = new ViewHolder();
                       
            holder.mWarehouseName = (TextView) row.findViewById(R.id.warehouseName);
            holder.mQtyOfHeaders = (TextView) row.findViewById(R.id.qtyOfHeaders);
            holder.mQtyOfOrders = (TextView) row.findViewById(R.id.qtyOfOrders);
                       
            row.setTag(holder);
  
        } else {

             holder = (ViewHolder) row.getTag();
        }

        Warehouse item = getItem(position);
        Resources res = getContext().getResources();
        
        holder.mWarehouseName.setText(item.getWarehouseName());
        holder.mQtyOfHeaders.setText(String.valueOf(item.getQtyOfHeaders()));
        holder.mQtyOfOrders.setText(String.valueOf(item.getQtyOfOrders()));
        
        return row;
        
    }    
}
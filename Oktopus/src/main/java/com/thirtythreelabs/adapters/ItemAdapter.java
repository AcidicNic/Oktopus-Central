package com.thirtythreelabs.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thirtythreelabs.oktopus.ItemsActivity;
import com.thirtythreelabs.oktopus.R;
import com.thirtythreelabs.systemmodel.Item;
import com.thirtythreelabs.util.Config;
import com.thirtythreelabs.util.img.ImageLoader;


public class ItemAdapter extends ArrayAdapter<Item> {
    private final LayoutInflater mInflater;
    
    private Context mContext;
    private Activity mActivity;
    public ImageLoader imageLoader; 
    
    private class ViewHolder {		    	   
		TextView mItemLineaTxt;
		TextView mItemIdTxt;
		TextView mItemBrandNameTxt;
		TextView mItemDescriptionTxt;
		TextView mItemLocationTxt;
		TextView mItemOriginTxt;
		TextView mItemCantidadTxt;
		TextView mItemSupplierNameTxt;
		TextView mItemInventoryTxt;
		TextView mItemUnidad;
		TextView mItemDiffToPick;
		
		TextView mCantidadTitle;
		
		Button mImageButton;
		
		ImageView mImageView;
		
		Button mManualImput;
		Button mTotalImput;
		Button mPauseItem;
    }
    
    
    public ItemAdapter(Context context, Activity activity) {
        super(context, android.R.layout.simple_list_item_2);
        this.mActivity = activity;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public void setData(List<Item> data) {
        clear();
        if (data != null) {
            for (Item appEntry : data) {
                add(appEntry);
            }
        }
    }

    
    /**
     * Populate new items in the list.
     */
    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ViewHolder holder = null;
    	View row = convertView;

        if (convertView == null) {
        	
        	
        	row = mInflater.inflate(R.layout.item, parent, false);
            
            holder = new ViewHolder();
            holder.mItemLineaTxt = (TextView) row.findViewById(R.id.itemLineaTxt);
            holder.mItemIdTxt = (TextView) row.findViewById(R.id.itemIdTxt);
            holder.mItemCantidadTxt = (TextView) row.findViewById(R.id.itemCantidadTxt);
            holder.mItemBrandNameTxt = (TextView) row.findViewById(R.id.itemBrandName);
            holder.mItemDescriptionTxt = (TextView) row.findViewById(R.id.itemDescription);
            holder.mItemLocationTxt = (TextView) row.findViewById(R.id.itemLocation);
            holder.mItemOriginTxt = (TextView) row.findViewById(R.id.itemOrigin);
            holder.mItemSupplierNameTxt = (TextView) row.findViewById(R.id.itemSupplierName);
            holder.mItemInventoryTxt = (TextView) row.findViewById(R.id.itemInventory);
            holder.mItemUnidad = (TextView) row.findViewById(R.id.itemUnidad);
            holder.mItemDiffToPick = (TextView) row.findViewById(R.id.itemDiffToPick);
            
            holder.mImageButton = (Button) row.findViewById(R.id.imageButton);
            holder.mManualImput = (Button) row.findViewById(R.id.manualInput2);
            holder.mTotalImput = (Button) row.findViewById(R.id.totalInput2);
            holder.mPauseItem = (Button) row.findViewById(R.id.pauseItemButton);
            
            holder.mCantidadTitle = (TextView) row.findViewById(R.id.cantidadTitle);
            
            
            row.setTag(holder);
            
        } else {
        	holder = (ViewHolder) row.getTag();
        }
        
        
        
        final Item item = getItem(position);
        Resources res = getContext().getResources();

        holder.mItemLineaTxt.setText(item.getItemOrderNumber() + " " + res.getString(R.string.OF) + " " + totalLines);
        
        String tempFamily = item.getItemId().substring(0, 3);
        String tempItemId = item.getItemId().substring(3);

        setColor(holder.mItemIdTxt, tempFamily + " " + tempItemId, 0, 3, res.getColor(R.color.red));
        
        String tempItemQuantity = item.getItemQuantity();
        holder.mItemCantidadTxt.setText(tempItemQuantity);
        
        
        holder.mItemDescriptionTxt.setText(Html.fromHtml(res.getString(R.string.DESCRIPTION) + ": <b>" + item.getItemDescription() + "</b>"));
        holder.mItemLocationTxt.setText(Html.fromHtml(res.getString(R.string.ROW) + ": <b>" + item.getItemRow() + "</b> " + res.getString(R.string.AISLE) + ": <b>" + item.getItemAisle() + "</b> " + res.getString(R.string.LEVEL) + ": <b>" + item.getItemLevel() + "</b>"));
        holder.mItemBrandNameTxt.setText(Html.fromHtml(res.getString(R.string.BRAND) + ": <b>" + item.getItemBrandName() + "</b>"));
        holder.mItemOriginTxt.setText(Html.fromHtml(res.getString(R.string.ORIGIN) + ": <b>" + item.getItemOrigin() + "</b>"));
        holder.mItemSupplierNameTxt.setText(Html.fromHtml(res.getString(R.string.SUPPLIER) + ": <b>" + item.getItemSupplierName() + "</b>"));
        holder.mItemInventoryTxt.setText(Html.fromHtml(res.getString(R.string.INVENTORY) + ": <b>" + item.getItemInventory() + "</b>"));
        
        holder.mTotalImput.setText(res.getString(R.string.GOT_ALL) + " (" + tempItemQuantity + ")");
        holder.mItemDiffToPick.setText(" " + res.getString(R.string.TRICKY) + " ");
        
        holder.mItemLocationTxt.setVisibility(View.GONE);
        
        String tempItemUnitId = item.getItemUnitId();
        String tempItemUnitDescription = "";
        if(tempItemQuantity.equalsIgnoreCase("1")){
        	if(tempItemUnitId.equalsIgnoreCase("0")){
            	tempItemUnitDescription = res.getString(R.string.UNIT);
            }else if(tempItemUnitId.equalsIgnoreCase("2")){
            	tempItemUnitDescription = res.getString(R.string.METER);
            }else if(tempItemUnitId.equalsIgnoreCase("4")){
            	tempItemUnitDescription = res.getString(R.string.KILO);
            }else if(tempItemUnitId.equalsIgnoreCase("5")){
            	tempItemUnitDescription = res.getString(R.string.HUNDRED);
            }else if(tempItemUnitId.equalsIgnoreCase("8")){
            	tempItemUnitDescription = "JUEGO PARTIDO";
            }
        }else{
        	if(tempItemUnitId.equalsIgnoreCase("0")){
            	tempItemUnitDescription = res.getString(R.string.UNITS);
            }else if(tempItemUnitId.equalsIgnoreCase("2")){
            	tempItemUnitDescription = res.getString(R.string.METERS);
            }else if(tempItemUnitId.equalsIgnoreCase("4")){
            	tempItemUnitDescription = res.getString(R.string.KILOS);
            }else if(tempItemUnitId.equalsIgnoreCase("5")){
            	tempItemUnitDescription = res.getString(R.string.HUNDREDS);
            }else if(tempItemUnitId.equalsIgnoreCase("8")){
            	tempItemUnitDescription = "JUEGOS PARTIDOS";
            }
        }
        
        
        holder.mItemUnidad.setText(tempItemUnitDescription);
        
               
        LinearLayout ActiveItem = (LinearLayout) row;
        
       
        holder.mItemLineaTxt.setTextColor(res.getColor(R.color.black));
        holder.mItemIdTxt.setTextColor(res.getColor(R.color.black));
        holder.mItemCantidadTxt.setTextColor(res.getColor(R.color.black));
        holder.mItemDescriptionTxt.setTextColor(res.getColor(R.color.black));
        holder.mCantidadTitle.setTextColor(res.getColor(R.color.black));
    	holder.mItemUnidad.setTextColor(res.getColor(R.color.black));
        
        if (position == selectedItem) {
        	
        	ActiveItem.setBackgroundResource(R.drawable.item_selected);
        	
        	if (totalLines == 1){
        		ActiveItem.setBackgroundResource(R.drawable.item_selected_unique);
        	} else if (position == 0){
        		ActiveItem.setBackgroundResource(R.drawable.item_selected_first);
        	} else if (position == totalLines-1) {
        		ActiveItem.setBackgroundResource(R.drawable.item_selected_last);
        	} 
        	
        	if(item.getItemIsDiffToPick()){
        		holder.mItemDiffToPick.setVisibility(View.VISIBLE);
        	}else{
        		holder.mItemDiffToPick.setVisibility(View.GONE);
        	}
        	
        	holder.mItemBrandNameTxt.setVisibility(View.VISIBLE);
        	holder.mItemOriginTxt.setVisibility(View.VISIBLE);
        	holder.mItemSupplierNameTxt.setVisibility(View.VISIBLE);
        	holder.mItemInventoryTxt.setVisibility(View.VISIBLE);
        	
        	holder.mImageButton.setVisibility(View.VISIBLE);
            holder.mManualImput.setVisibility(View.VISIBLE);
            holder.mTotalImput.setVisibility(View.VISIBLE);
            holder.mPauseItem.setVisibility(View.VISIBLE);
             
        } else {
        	
        	ActiveItem.setBackgroundResource(R.drawable.item);
        	
        	if (totalLines == 1){
        		ActiveItem.setBackgroundResource(R.drawable.item_unique);
         	} else if (position == 0){
        		ActiveItem.setBackgroundResource(R.drawable.item_first);
         	} else if (position == totalLines-1) {
         		ActiveItem.setBackgroundResource(R.drawable.item_last);
        	} 
        	
        	if(item.getItemIsDiffToPick()){
        		holder.mItemDiffToPick.setVisibility(View.VISIBLE);
        	}else{
        		holder.mItemDiffToPick.setVisibility(View.GONE);
        	}
        	holder.mItemBrandNameTxt.setVisibility(View.GONE);
        	holder.mItemOriginTxt.setVisibility(View.GONE);
        	holder.mItemSupplierNameTxt.setVisibility(View.GONE);
        	holder.mItemInventoryTxt.setVisibility(View.GONE);
        	
        	holder.mImageButton.setVisibility(View.GONE);
            holder.mManualImput.setVisibility(View.GONE);
            holder.mTotalImput.setVisibility(View.GONE);
            holder.mPauseItem.setVisibility(View.GONE);
        }
        
        
        
        if(item.getItemStatusId().equals(Config.ITEM_STATUS_PAUSED)){
        	
        	ActiveItem.setBackgroundResource(R.drawable.item_paused);
        	
        	if (totalLines == 1){
         		ActiveItem.setBackgroundResource(R.drawable.item_paused_unique);
         	} else if (position == 0){
        		ActiveItem.setBackgroundResource(R.drawable.item_paused_first);
        	} else if (position == totalLines-1) {
        		ActiveItem.setBackgroundResource(R.drawable.item_paused_last);
        	}
        	
        	if(item.getItemIsDiffToPick()){
        		holder.mItemDiffToPick.setVisibility(View.VISIBLE);
        	}else{
        		holder.mItemDiffToPick.setVisibility(View.GONE);
        	}
        	holder.mItemBrandNameTxt.setVisibility(View.GONE);
        	holder.mItemOriginTxt.setVisibility(View.GONE);
        	holder.mItemSupplierNameTxt.setVisibility(View.GONE);
        	holder.mItemInventoryTxt.setVisibility(View.GONE);
        	
        	holder.mImageButton.setVisibility(View.GONE);
            holder.mManualImput.setVisibility(View.GONE);
            holder.mTotalImput.setVisibility(View.GONE);
            holder.mPauseItem.setVisibility(View.GONE);
        }
        
        if(item.getItemStatusId().equals(Config.ITEM_STATUS_PICKED)){
        	
        	ActiveItem.setBackgroundResource(R.drawable.item_picked);
        	
        	if (totalLines == 1){
        		ActiveItem.setBackgroundResource(R.drawable.item_picked_unique);
        	} else if (position == 0){
        		ActiveItem.setBackgroundResource(R.drawable.item_picked_first);
        	} else if (position == totalLines-1) {
        		ActiveItem.setBackgroundResource(R.drawable.item_picked_last);
        	} 
        	
        	
        	holder.mItemCantidadTxt.setText(item.getItemQuantityPicked() + "/" + item.getItemQuantity());
            
        	holder.mItemLineaTxt.setTextColor(res.getColor(R.color.light_grey));
            holder.mItemIdTxt.setTextColor(res.getColor(R.color.light_grey));
            holder.mItemCantidadTxt.setTextColor(res.getColor(R.color.light_grey));
            holder.mItemDescriptionTxt.setTextColor(res.getColor(R.color.light_grey));
            holder.mCantidadTitle.setTextColor(res.getColor(R.color.light_grey));
        	holder.mItemUnidad.setTextColor(res.getColor(R.color.light_grey));
        	

        	holder.mItemDiffToPick.setVisibility(View.GONE);
        	holder.mItemBrandNameTxt.setVisibility(View.GONE);
        	holder.mItemOriginTxt.setVisibility(View.GONE);
        	holder.mItemSupplierNameTxt.setVisibility(View.GONE);
        	holder.mItemInventoryTxt.setVisibility(View.GONE);
        	
        	holder.mImageButton.setVisibility(View.GONE);
            holder.mManualImput.setVisibility(View.GONE);
            holder.mTotalImput.setVisibility(View.GONE);
			holder.mPauseItem.setVisibility(View.GONE);
     	 
        }
        
        
		
		holder.mImageButton.setOnClickListener( new View.OnClickListener() {  
        	public void onClick(View v) {  
        		((ItemsActivity) mActivity).imageDialog();
        	}  
        }); 
        
        holder.mManualImput.setOnClickListener( new View.OnClickListener() {  
        	public void onClick(View v) {  
        		((ItemsActivity) mActivity).manualImputDialog();
        		
        	}  
        }); 
        
        holder.mTotalImput.setOnClickListener( new View.OnClickListener() {  
        	public void onClick(View v) {  
        		((ItemsActivity) mActivity).goToSystemAndPickAll();
        		
        	}  
        }); 
        
        holder.mPauseItem.setOnClickListener( new View.OnClickListener() {  
        	public void onClick(View v) {  
        		((ItemsActivity) mActivity).goToSystemAndPauseThisItem();
        		
        	}  
        }); 
        
        return row;
    }
    
    private int selectedItem = 0;
    private int totalLines = 0;

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

	public void setTotalLineas(int tempTotalLineas) {
		totalLines = (tempTotalLineas);
	}
	
	private void setColor(TextView view, String fulltext, int start, int finish, int color) {
      view.setText(fulltext, TextView.BufferType.SPANNABLE);
      Spannable str = (Spannable) view.getText();
      str.setSpan(new ForegroundColorSpan(color), start, finish, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
   }

}
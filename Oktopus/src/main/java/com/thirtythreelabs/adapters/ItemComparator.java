package com.thirtythreelabs.adapters;

import java.util.Comparator;

import com.thirtythreelabs.systemmodel.Item;

public class ItemComparator implements Comparator<Item>{

	
	public int compare(Item a, Item b) {
        int dateComparison = b.getItemStatusId().compareTo(a.getItemStatusId());
//        return dateComparison == 0 ? a.getItemLineId().compareTo(b.getItemLineId()) : dateComparison;
        return dateComparison;
   
	}
	
	/*
	
	Collections.sort(foos, new FooComparator());

	 */
}

package com.thirtythreelabs.flowmodel;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Data {
	
	@Element
	public String action;
	
	@ElementList 
	public List<Outcome> outcomeList = new ArrayList<Outcome>();


	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<Outcome> getOutcomeList() {
		return outcomeList;
	}

	public void setOutcomeList(List<Outcome> outcomeList) {
		this.outcomeList = outcomeList;
	}

}

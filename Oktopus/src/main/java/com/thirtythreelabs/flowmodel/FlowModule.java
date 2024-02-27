package com.thirtythreelabs.flowmodel;

import java.util.ArrayList;
import java.util.List;


import org.simpleframework.xml.ElementList;

public class FlowModule extends Module{
	

	
	@ElementList 
	public List<Command> listenTo = new ArrayList<Command>();
	
	@ElementList 
	public List<Phrase> say = new ArrayList<Phrase>();

	public FlowModule(){
		this.type = "flow";
	}
	
	public List<Command> getListenTo() {
		return listenTo;
	}

	public void setListenTo(List<Command> listenTo) {
		this.listenTo = listenTo;
	}

	public List<Phrase> getSay() {
		return say;
	}

	public void setSay(List<Phrase> say) {
		this.say = say;
	}
	
	
	

}

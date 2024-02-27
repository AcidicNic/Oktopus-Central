package com.thirtythreelabs.flowmodel;

import java.util.ArrayList;
import java.util.List;


import org.simpleframework.xml.ElementList;

public class SayDoModule extends Module{
	
	@ElementList 
	public List<Phrase> say = new ArrayList<Phrase>();

	@ElementList 
	public List<Outcome> outcomeList = new ArrayList<Outcome>();
	
	
	public SayDoModule(){
		this.type = "sayDo";
	}

	
	public List<Phrase> getSay() {
		return say;
	}

	public void setSay(List<Phrase> say) {
		this.say = say;
	}


	public List<Outcome> getOutcomeList() {
		return outcomeList;
	}


	public void setOutcomeList(List<Outcome> outcomeList) {
		this.outcomeList = outcomeList;
	}
	
	
	
	
	

}

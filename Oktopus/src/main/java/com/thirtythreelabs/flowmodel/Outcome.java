package com.thirtythreelabs.flowmodel;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Outcome {
	
	@Attribute
	private String result;
	
	@Attribute
	private String type; // activity | module | say | timeOut
	
	@Element (required=false)
	private String gotoWhere;

	@ElementList (required=false)
	public List<Phrase> say = new ArrayList<Phrase>();
	
	
	public Outcome(){
		this.result = "true";
		this.type = "";
		this.gotoWhere = "";
		
	}
	
	
	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public String getGotoWhere() {
		return gotoWhere;
	}

	public void setGotoWhere(String gotoWhere) {
		this.gotoWhere = gotoWhere;
	}

	public String getModType() {
		return type;
	}

	public void setModType(String type) {
		this.type = type;
	}
	
	public List<Phrase> getSay() {
		return say;
	}


	public void setSay(List<Phrase> say) {
		this.say = say;
	}
}

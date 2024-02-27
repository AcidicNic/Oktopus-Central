package com.thirtythreelabs.flowmodel;

import org.simpleframework.xml.Attribute;


public class Module {
	
	@Attribute
	private String name;
	
	@Attribute
	private String start;
	
	@Attribute
	protected String type; // data | flow | sayDo

	@Attribute
	private String timeOut; // milisecs
	
	
	public Module(){
		this.name = "";
		this.start = "false";
		this.timeOut = "0";
	}
	
	public Module(String name, String start){
		this.name = name;
		this.start = start;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}

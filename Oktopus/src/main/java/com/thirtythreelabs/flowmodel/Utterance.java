package com.thirtythreelabs.flowmodel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public class Utterance {
	@Text
	String line;
	
	@Attribute (required=false)
	String type;
	
	
	public Utterance(){
		this.line = "";
		this.type = "";
	}
	
	
	public String getLine() {
		return line;
	}


	public void setLine(String line) {
		this.line = line;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}
	

}

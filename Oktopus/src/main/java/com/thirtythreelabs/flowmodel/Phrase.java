package com.thirtythreelabs.flowmodel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public class Phrase {

	
	@Attribute
	private String type; // string | data | method
	
	@Attribute
	private String speed; // 0.0 - 2.0
	
	@Attribute
	private String pitch; // 0.0 - 2.0
	
	@Attribute
	private String pauseBefore; // milliseconds
	
	@Attribute
	private String pauseAfter; // milliseconds
	
	@Text
	private String line; // string to say
	
	public Phrase(){
		this.type = "string";
		this.speed = "1.0";
		this.pitch = "1.0";
		this.pauseBefore = "0";
		this.line = "";
		this.pauseAfter = "0";
		
	}
	
	
	public String getPauseBefore() {
		return pauseBefore;
	}


	public void setPauseBefore(String pauseBefore) {
		this.pauseBefore = pauseBefore;
	}


	public String getPauseAfter() {
		return pauseAfter;
	}


	public void setPauseAfter(String pauseAfter) {
		this.pauseAfter = pauseAfter;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getSpeed() {
		return speed;
	}


	public void setSpeed(String speed) {
		this.speed = speed;
	}


	public String getLine() {
		return line;
	}


	public void setLine(String line) {
		this.line = line;
	}


	

	
	
}

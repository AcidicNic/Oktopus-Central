package com.thirtythreelabs.flowmodel;


import org.simpleframework.xml.Element;

public class DataModule extends Module{
	

	
	@Element
	private Data data;
	
	public DataModule(){
		this.type = "data";
	}
	
	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
	
	
}

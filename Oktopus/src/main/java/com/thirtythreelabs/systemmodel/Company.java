package com.thirtythreelabs.systemmodel;

import java.io.Serializable;

public class Company implements Serializable{
	
	private String companyId;
	private String companyName;

	
	public Company(){
		this.companyId = "0";
		this.companyName = "";
	}


	public String getCompanyId() {
		return companyId;
	}


	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	

}

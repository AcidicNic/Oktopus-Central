package com.thirtythreelabs.systemmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Operator implements Serializable {
	
	private String operatorId;
	private String operatorName;
	private String operatorCurrentWarehoueseId;
	private String operatorLastWarehoueseId;
	private String loginError;
	private String loginErrorMessage;
	private List<Warehouse> operatorWarehouses;

	
	public Operator(){
		this.operatorId = "0";
		this.operatorName = "";
		this.operatorCurrentWarehoueseId = "0";
		this.operatorLastWarehoueseId = "0";
		this.operatorWarehouses = new ArrayList<Warehouse>();
	}


	public String getOperatorId() {
		return operatorId;
	}


	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}


	public String getOperatorName() {
		return operatorName;
	}


	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}


	public String getOperatorCurrentWarehoueseId() {
		return operatorCurrentWarehoueseId;
	}


	public void setOperatorCurrentWarehoueseId(String operatorCurrentWarehoueseId) {
		this.operatorCurrentWarehoueseId = operatorCurrentWarehoueseId;
	}


	public List<Warehouse> getOperatorWarehouses() {
		return operatorWarehouses;
	}

	
	public void setOperatorWarehouses(List<Warehouse> operatorWarehouses) {
		this.operatorWarehouses = operatorWarehouses;
	}


	public String getLoginError() {
		return loginError;
	}


	public void setLoginError(String loginError) {
		this.loginError = loginError;
	}


	public String getLoginErrorMessage() {
		return loginErrorMessage;
	}


	public void setLoginErrorMessage(String loginErrorMessage) {
		this.loginErrorMessage = loginErrorMessage;
	}


	public String getOperatorLastWarehoueseId() {
		return operatorLastWarehoueseId;
	}


	public void setOperatorLastWarehoueseId(String operatorLastWarehoueseId) {
		this.operatorLastWarehoueseId = operatorLastWarehoueseId;
	}
	

}

package com.kemplerEnergy.unfinished;

import java.util.Set;
import java.util.HashSet;

import com.kemplerEnergy.model.CounterParty;


public class Vendor extends CounterParty {
	
	private int expenseAccnt;
	private String bussinessType;
	private String holdStatus;
	
	private Set transportTypes = new HashSet();

	public int getExpenseAccnt() {
		return expenseAccnt;
	}

	public void setExpenseAccnt(int expenseAccnt) {
		this.expenseAccnt = expenseAccnt;
	}

	public String getBussinessType() {
		return bussinessType;
	}

	public void setBussinessType(String bussinessType) {
		this.bussinessType = bussinessType;
	}

	public String getHoldStatus() {
		return holdStatus;
	}

	public void setHoldStatus(String holdStatus) {
		this.holdStatus = holdStatus;
	}

	public Set getTransportTypes() {
		return transportTypes;
	}

	public void setTransportTypes(Set transportTypes) {
		this.transportTypes = transportTypes;
	}
	
	
}

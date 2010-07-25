package com.kemplerEnergy.unfinished;

import com.kemplerEnergy.model.CounterParty;




public class Customer extends CounterParty {
	
	private int DUNNo;
	private int storeFinancial;
	private int incomeAccnt;
	private int creditLimit;
	public int getDUNNo() {
		return DUNNo;
	}
	public void setDUNNo(int no) {
		DUNNo = no;
	}
	public int getStoreFinancial() {
		return storeFinancial;
	}
	public void setStoreFinancial(int storeFinancial) {
		this.storeFinancial = storeFinancial;
	}
	public int getIncomeAccnt() {
		return incomeAccnt;
	}
	public void setIncomeAccnt(int incomeAccnt) {
		this.incomeAccnt = incomeAccnt;
	}
	public int getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(int creditLimit) {
		this.creditLimit = creditLimit;
	}
	
}

package com.kemplerEnergy.model.rins;

import com.google.gwt.user.client.Window;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.BaseObject;

public class BOLInfo extends BaseObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7858619918788740765L;
	
	private String bolNumber = "";
	private int quantity;
	private String loadNumber = "";
	
	// foreign key to its owner
	private Invoice invoice;
	
	
	public BOLInfo(){}
	
	public String getBolNumber() {
		return bolNumber;
	}
	public void setBolNumber(String bolNumber) {
		this.bolNumber = bolNumber;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getLoadNumber() {
		return loadNumber;
	}
	public void setLoadNumber(String loadNumber) {
		this.loadNumber = loadNumber;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	/**
	 * bolNumber = "DefaultBOL";</br> 
	 * quantity = 9999999; </br> 
	 * loadNumber = "Default Load Number";</br> 
	 */
	public void setDefault() {
		bolNumber = "DefaultBOL";
		quantity = 9999999;
		loadNumber = "Default Load Number";
	}

	public void verifyBOLEntry(){
		if (bolNumber.trim().isEmpty()){
			throw new RINException("BOL Number cannot be empty!");
		}
		if (quantity <= 0){
			throw new RINException("Quantity cannot be empty or negative!");
		}
	}
	
	public String toString() {
		return bolNumber;
	}
}

package com.kemplerEnergy.model.rins;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.kemplerEnergy.model.EPAPartner;

public class RINInvoiceSearch implements IsSerializable{
	private String invoiceType = "";
	private String invoiceNumber = "";
	private EPAPartner epaPartner;
	private String fromDate ="";
	private String toDate = "";
	private int version = 0;
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public EPAPartner getEpaPartner() {
		return epaPartner;
	}
	public void setEpaPartner(EPAPartner epaPartner) {
		this.epaPartner = epaPartner;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}	
}

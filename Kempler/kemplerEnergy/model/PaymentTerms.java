package com.kemplerEnergy.model;

public class PaymentTerms {

	private int termDays;
	private ShipMode deliveryMethod;
	public int getTermDays() {
		return termDays;
	}
	public void setTermDays(int termDays) {
		this.termDays = termDays;
	}
	public ShipMode getDeliveryMethod() {
		return deliveryMethod;
	}
	public void setDeliveryMethod(ShipMode deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}
	
}

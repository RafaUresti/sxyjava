package com.kemplerEnergy.model;

public class Broker extends CounterParty {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3059145581848565406L;

	// GL balance with broker, record commission we need to pay for each broker
	private int balance; 
	
	public Broker() {
//		if (type == null || !type.equalsIgnoreCase("BROKER"))
//			type = "BROKER";
		setDefault();
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public void setDefault() {
		name = "Dummy Broker";
		fullName = "Fully Dummy Broker";
		balance = 0;
	}
	
	public static void main(String[] args) {
		new Broker().save();
	}
}

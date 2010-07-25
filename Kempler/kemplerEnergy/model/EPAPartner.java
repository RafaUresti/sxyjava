package com.kemplerEnergy.model;

public class EPAPartner extends CounterParty {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2433069881063795744L;
	
	private char[] epaNo;
	private String email;
	private String contact;

	public EPAPartner() {
		epaNo = new char[4];
	}

	public char[] getEpaNo() {
		return epaNo;
	}

	public void setEpaNo(char[] epaNo) {
		this.epaNo = epaNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

}

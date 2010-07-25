package com.kemplerEnergy.model;

import java.util.ArrayList;

import com.kemplerEnergy.model.rins.Invoice;

public class Outbox extends BaseObject {

	private String subject;
	private String context;
	private String attachements;
	private String addresses;
	
	
	public Outbox() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getContext() {
		return context;
	}


	public void setContext(String context) {
		this.context = context;
	}


	public String getAttachements() {
		return attachements;
	}


	public void setAttachements(String attachements) {
		this.attachements = attachements;
	}


	public String getAddresses() {
		return addresses;
	}


	public void setAddresses(String addresses) {
		this.addresses = addresses;
	}

	public ArrayList<String> returnRecipients() {
		ArrayList<String> recipients = new ArrayList<String>();
		
		return recipients;
	}
	public void setDefault() {
		// TODO Auto-generated method stub

	}

}

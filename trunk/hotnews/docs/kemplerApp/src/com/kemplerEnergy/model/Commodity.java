package com.kemplerEnergy.model;

public class Commodity extends BaseObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8803860493713886155L;
	
	private String name;
	
	public Commodity() {
		// TODO Auto-generated constructor stub
	}

	public Commodity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefault() {
		name = "Fake Commodity";
	}
	
	public static void main(String[] args) {
		new Commodity().save();
	}
}

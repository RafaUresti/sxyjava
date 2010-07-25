package com.kemplerEnergy.model;

public class MarketZone extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5985759818910851319L;

	private String name;

	public MarketZone() {}
	public MarketZone(String area) {
		name = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setDefault() {
		name = "Fake area";
	}
	
	public static void main(String[] args) {
		new MarketZone().save();
	}
}

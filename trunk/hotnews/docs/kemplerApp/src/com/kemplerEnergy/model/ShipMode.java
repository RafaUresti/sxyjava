package com.kemplerEnergy.model;

public class ShipMode extends BaseObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3472638709003361549L;
	
	private String mode;
	
	public ShipMode() {}
	
	public ShipMode(String mode) {
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setDefault() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

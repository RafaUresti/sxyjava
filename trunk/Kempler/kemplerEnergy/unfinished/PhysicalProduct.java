package com.kemplerEnergy.unfinished;

import com.kemplerEnergy.model.ShipMode;

public class PhysicalProduct extends Product {

	private String shipUnit;
	private int quantity;
	private String Region;
	
	private ShipMode shipMethod;

	public String getShipUnit() {
		return shipUnit;
	}

	public void setShipUnit(String shipUnit) {
		this.shipUnit = shipUnit;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getRegion() {
		return Region;
	}

	public void setRegion(String region) {
		Region = region;
	}

	public ShipMode getShipMethod() {
		return shipMethod;
	}

	public void setShipMethod(ShipMode shipMethod) {
		this.shipMethod = shipMethod;
	}
	
}

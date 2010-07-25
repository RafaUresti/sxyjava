package com.kemplerEnergy.unfinished;

import com.kemplerEnergy.model.ShipMode;

public class PhysicalTicket extends Ticket {

	private boolean ratable;
	private String FOBPoint;
	
	private ShipMode defaultShipMode;

	public boolean isRatable() {
		return ratable;
	}

	public void setRatable(boolean ratable) {
		this.ratable = ratable;
	}

	public String getFOBPoint() {
		return FOBPoint;
	}

	public void setFOBPoint(String point) {
		FOBPoint = point;
	}

	public ShipMode getDefaultShipMode() {
		return defaultShipMode;
	}

	public void setDefaultShipMode(ShipMode defaultShipMode) {
		this.defaultShipMode = defaultShipMode;
	}
	
}

package com.kemplerEnergy.position;

public class HeaderNotDefinedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3291711167750098473L;

	public HeaderNotDefinedException () {}
	
	public HeaderNotDefinedException (String msg) {
		super(msg);
	}
	
	public HeaderNotDefinedException (Throwable obj) {
		super(obj);
	}
}

package com.kemplerEnergy.exception;

import java.io.Serializable;

public class RINDuplicateRetire extends RuntimeException implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6971794624453548084L;
	
	private String message;

	public RINDuplicateRetire(String message) {
		super();
		this.message = message;
	}

	public RINDuplicateRetire() {}

	public String getMessage() {
		return message;
	}
}

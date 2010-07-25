package com.kemplerEnergy.exception;


public class RINInsufficientException extends RINException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1290444028211438683L;
	private String message;
	
	public RINInsufficientException(String message) {
		super(message);
		this.message = message;
	}

	public RINInsufficientException() {}

	public String getMessage() {
		return message;
	}


}

package com.kemplerEnergy.exception;


import com.google.gwt.user.client.rpc.IsSerializable;

public class AuthorizationException extends RuntimeException implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3063631334327652146L;
	/**
	 * 
	 */
	
	private String message;

	public AuthorizationException() {
	}

	public AuthorizationException(String message) {
		super(message);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}


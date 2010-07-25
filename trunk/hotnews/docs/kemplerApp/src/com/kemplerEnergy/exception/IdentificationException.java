package com.kemplerEnergy.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IdentificationException extends RuntimeException implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1185519497274131309L;
	
	private String message;

	public IdentificationException() {
	}

	public IdentificationException(String message) {
		super(message);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

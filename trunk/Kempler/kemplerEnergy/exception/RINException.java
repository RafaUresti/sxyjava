package com.kemplerEnergy.exception;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;

public class RINException extends RuntimeException implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8695886456563104902L;
	
	private String message;

	public RINException() {
	}

	public RINException(String message) {
		super(message);
		this.message = message;
		GWT.log(message, this);
	}

	public String getMessage() {
		return message;
	}
}


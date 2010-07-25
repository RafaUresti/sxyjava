package com.kemplerEnergy.exception;

import java.io.Serializable;

public class RINConlfictStateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6304295306238248757L;
	private String message;

	public RINConlfictStateException() {
	}

	public RINConlfictStateException(String message) {
		super(message);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

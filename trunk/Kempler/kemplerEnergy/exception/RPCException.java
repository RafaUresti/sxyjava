package com.kemplerEnergy.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RPCException extends RuntimeException implements IsSerializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1991916201643391853L;
	
	private String message;

	public RPCException() {
	}

	public RPCException(String message) {
		super(message);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

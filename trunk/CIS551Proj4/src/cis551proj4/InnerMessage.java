package cis551proj4;

import java.security.Key;
import java.security.Signature;

public class InnerMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3822051791901118932L;

	private long nonce;
	private String AccountNumber;
	private Key sessionkey;
	private String command;
	private byte[] sign;
	
	public InnerMessage(Key key){
		this.sessionkey = key;
	}
	
	public InnerMessage(String AN, long nonce){
		this.nonce = nonce;
		this.AccountNumber = AN;
	}
	
	public InnerMessage(String AN, long nonce, Key sKey){
		this.nonce = nonce;
		this.AccountNumber = AN;
		this.sessionkey = sKey;
	}

	public InnerMessage(String command){
		this.command = command;
	}
	
	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public String getAccountNumber() {
		return AccountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		AccountNumber = accountNumber;
	}

	public Key getSessionkey() {
		return sessionkey;
	}

	public void setSessionkey(Key sessionkey) {
		this.sessionkey = sessionkey;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public byte[] getSign() {
		return sign;
	}

	public void setSign(byte[] sign) {
		this.sign = sign;
	}
}

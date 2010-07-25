package cis551proj4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.SignatureException;

public class UniversalMessage implements Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6586664718546631286L;
	private byte[] data;
	private byte[] kSession;
	private byte[] encryptedSignature;
	private String ATMNumber;
	
	public UniversalMessage(){
	}
	public UniversalMessage (String n){
		this.ATMNumber = n;
	}

	public UniversalMessage (byte[] data){
		this.data = data;
	}
	
	public UniversalMessage (String n, byte[] data){
		this.ATMNumber = n;
		this.data = data;
	}
	
	public UniversalMessage (byte[] data, byte[] sKey){
		this.data = data;
		this.kSession = sKey;
	}
	
	public byte[] getSignature() {
		return encryptedSignature;
	}

	public void setSignature(byte[] signature) {
		this.encryptedSignature = signature;
	}
	
	public byte[] getData(){
		return data;
	}

	public byte[] getKSession(){
		return this.kSession;
	}
	
	public String getATMNumber() {
		return this.ATMNumber;
	}
	
	public void setKSession(byte[] session) {
		kSession = session;
	}
}

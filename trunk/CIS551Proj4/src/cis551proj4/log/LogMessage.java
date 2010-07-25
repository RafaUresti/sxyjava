package cis551proj4.log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import cis551proj4.TransactionMessage;

public class LogMessage implements Serializable {
	private static final long serialVersionUID = 1952326176186799751L;
	public byte[] data;
	public byte[] signature;
	public String info;
	public int type;
	
	public LogMessage(){}
	
	public LogMessage(Exception e){
		type = 3;
		info = e.getMessage();
	}
	
	public String toString(){
		
		TransactionMessage message = getMessage();
		String messageString = "Null Message";
		if(message != null){
			messageString = message.toString();
		}
		
		switch(type){
		case 0:
			return "Authorization: " + info;
		case 1:
			return "Receiving Transaction: " 
				+ messageString
				+ ", SIGNATURE[" + getHexSignature() + "]";
		case 2:
			return "Sending Transaction: " 
				+ messageString
				+ ", SIGNATURE[" + getHexSignature() + "]";
		case 3:
			return "Exception: " + info;
		default:
			return "Unknown Entry Type";
		}
	}

	private String getHexSignature() {
		
		if(signature == null) return null;
		
		StringBuilder result = new StringBuilder();
		for (int i=0; i < signature.length; i++) {
			result.append(
					Integer.toString( ( signature[i] & 0xff ) + 0x100, 16).substring( 1 ));
		}
		return result.toString();
	}
	
	private TransactionMessage getMessage(){
		if(data == null) return null;
		try{
			ObjectInputStream reader = new ObjectInputStream(new ByteArrayInputStream(data));
			return (TransactionMessage)reader.readObject();
		}catch(IOException e){
			return null;
		}catch(ClassNotFoundException e){
			return null;
		}
	}

}

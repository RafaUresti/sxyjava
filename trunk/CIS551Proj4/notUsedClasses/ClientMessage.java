package cis551proj4;
import java.io.Serializable;


public class ClientMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5557841930886627511L;
	private String atmID;
	private byte[] encryptedData;
	
	public ClientMessage(String ID, byte[] Data){
		this.atmID = ID;
		this.encryptedData = Data;
	}
	
	public String getATMID(){
		return atmID;
	}
	
	public byte[] getData(){
		return encryptedData;
	}
}

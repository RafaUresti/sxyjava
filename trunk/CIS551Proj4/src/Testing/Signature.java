package Testing;

import java.sql.Timestamp;

import cis551proj4.Message;

public class Signature implements Message{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2500170755547306853L;
	public Signature (String accountNumber, String atmNumber, String command, long timestamp){
		this.accountNumber = accountNumber;
		this.atmNumber = atmNumber;
		this.command = command;
		this.timestamp = timestamp;
	}
	String accountNumber;
	String atmNumber;
	String command;
	long timestamp;
}

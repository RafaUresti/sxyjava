package edu.upenn.cis505.g48.emailTotalOrdering;

import java.io.Serializable;
import java.util.Arrays;

public class EmailMessage implements Serializable, Comparable<EmailMessage>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4655745620145411946L;
	
	long messageTime = 0;
	boolean deliverable = false;
	String senderIp = "";
	int senderClusterPort = 0;
	String user = "";
	String domain = "";
	private byte[] email = null;
	
	public EmailMessage(long messageTime, String senderIp, int senderClusterPort){
		this.messageTime = messageTime;
		this.senderIp = senderIp;
		this.senderClusterPort = senderClusterPort;
	}
	
	byte[] getEmail(){
			return email;
	}
	
	void attachEmail(byte[] email, String user, String domain){
		this.email = email;
		this.user = user;
		this.domain = domain;
	}
	
	/**
	 * Messages with lower messageTime will be put first.
	 * If equal messageTime, smaller IP gets first.
	 * If equal IP, smaller port gets first.
	 */
	public int compareTo(EmailMessage message) {
		long timeDiff = this.messageTime - message.messageTime;
		if (timeDiff < 0){
			return -1;
		} else if (timeDiff > 0){
			return 1;
		} else if (senderIp.compareTo(message.senderIp) != 0){
			return senderIp.compareTo(senderIp);
		} else if (this.senderClusterPort - message.senderClusterPort != 0){
			return this.senderClusterPort - message.senderClusterPort;
		} else if (Arrays.equals(this.email, message.email)){
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * If the serverIp and smtpPort and the email content
	 * are all the same, the two messages are considered
	 * the same.
	 */
	public boolean equals(Object obj){
		EmailMessage otherMessage;
		if (!(obj instanceof EmailMessage)){
			return false;
		} else {
			otherMessage = (EmailMessage)obj;
			if (this.senderIp.equals(otherMessage.senderIp) ||
					this.senderClusterPort != otherMessage.senderClusterPort){
				return false;
			}
			return Arrays.equals(this.email, otherMessage.email);
		}
		
	}
}

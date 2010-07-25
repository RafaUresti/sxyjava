package edu.upenn.cis505.g48.smtp;
import java.io.*;
import java.net.*;
public class SMTPClient {
	private Socket socket;
	private String smtpAddress;
	private int smtpPort;
	
	
	public void setParameter(String address, int port){
		this.smtpAddress = address;
		this.smtpPort = port;
	}

	public void handleRequests(){
		//TODO handle user requests in command line format
		SMTP_User_Client obj = new SMTP_User_Client();
		obj.control(smtpAddress, smtpPort);		
	}
}

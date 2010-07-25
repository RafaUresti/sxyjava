package edu.upenn.cis505.g48.smtp;

import java.net.*;
import java.io.*;

import edu.upenn.cis505.g48.model.ServerData;

public class SMTP_Server implements Runnable {
	String cluster_name = "";
	ServerSocket serverSock = null;
	
	public SMTP_Server (int the_port) {
		int smtp_port = the_port;
		try {
			serverSock = new ServerSocket(smtp_port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public SMTP_Server (ServerSocket sock){
		this.serverSock = sock;
	}
	public void run(){
		System.out.printf("** SMTP server: start\n");			
		while (true) {
			try {
				Socket sock = serverSock.accept();
				
				System.out.printf("** SMTP get request\n");
				
				SMTP_Client_Handler client_handler = 
					new SMTP_Client_Handler(sock, this);
				Thread client_handler_thread = new Thread(client_handler, "SMTP_Client_Handler");
				
				client_handler_thread.start();
				
			} catch (IOException e) { e.printStackTrace();}
		}
	}
}

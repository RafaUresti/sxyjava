package edu.upenn.cis505.g48.pop3;


import java.net.*;
import java.io.*;


import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.ServerData;

public class Pop3Server implements Runnable {
	int port = 6230;
	public boolean shut = false;
	ServerSocket serverSock = null;
	
	public Pop3Server (int the_port) {
		port = the_port;
		try {
			serverSock = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Pop3Server (ServerSocket servSock) {
		serverSock = servSock;
	}
	public void run(){
		System.out.printf("Pop3 listening...\n");
		while (true) {
			try {
				Socket sock = serverSock.accept();
				
				System.out.println("** Pop3 server begin new handler.");
				
				Pop3Handler handler = new Pop3Handler(sock);
				Thread handler_thread = new Thread(handler);
				handler_thread.start();
				
				System.out.println("** Pop3 server handler thread started.");
				
			} catch (IOException e) { e.printStackTrace();}
		}
	}
	
}

package edu.upenn.cis505.g48.pop3;

import edu.upenn.cis505.g48.model.ServerData;


public class POP3_Entry {
	static public void main(String[] strs) {
		Pop3Server ls = new Pop3Server(6230);
		Thread pop3_server_thread = new Thread(ls);
		// ls.run();
		pop3_server_thread.start();
		
		System.out.printf("already started\n");
		while(true);
	}
}

package edu.upenn.cis505.g48.smtp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;

import edu.upenn.cis505.g48.model.Email;
import edu.upenn.cis505.g48.model.ServerData;

public class SMTP_Server_Entry {
	static public void main(String[] strs) {
		SMTP_Server ls = new SMTP_Server(-1);
		// ls.run();
		Thread smtp_server_thread = new Thread(ls);
		smtp_server_thread.start();
		while(true){
			String comm = scan_a_comm();
		}
	}

	static int scan_a_int() {
		System.out.println("Input a dubeg world number:");
		Scanner scan = new Scanner(System.in);
		String command = scan.nextLine();
		return Integer.parseInt(command);
	}
	
	static String scan_a_comm() {
		System.out.println("Input command for central console:");
		Scanner scan = new Scanner(System.in);
		String command = scan.nextLine();
		return command;
	}
}
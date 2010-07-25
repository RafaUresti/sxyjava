package edu.upenn.cis505.g48;

import java.net.UnknownHostException;
import java.util.Scanner;

import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.ServerData;
import edu.upenn.cis505.g48.pop3.Pop3Server;
import edu.upenn.cis505.g48.smtp.*;

public class Server {
	public static void main(String[] a) {
		if (a.length == 5) {
			try {
				Integrated_Server is = new Integrated_Server(a[1], a[3], Integer.parseInt(a[4]));
				is.start_server();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		else if (a.length == 9) {
			try {
				Integrated_Server is = new Integrated_Server(a[1], a[7], Integer.parseInt(a[8]));
				is.set_ports(Integer.parseInt(a[3]), Integer.parseInt(a[4]), Integer.parseInt(a[5]));
				is.start_server();
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
		} else {
			System.out.println("Params ERROR!");
			return;
		}
	}
}

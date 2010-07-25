package edu.upenn.cis505.g48.emailTotalOrdering;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.smtp.SMTP_Tools;

public class HandleDataRequest {

	public static void handle(ObjectOutputStream oos, Integrated_Server inte_server) {
		try {
			oos.writeObject(inte_server.server_data);
			
			// TODO : del debug here
			System.out.println("The data before transfer: ");
			new SMTP_Tools().disp_server_data(inte_server.server_data);
			
			oos.flush();
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}

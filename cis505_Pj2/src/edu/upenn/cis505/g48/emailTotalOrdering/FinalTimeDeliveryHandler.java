package edu.upenn.cis505.g48.emailTotalOrdering;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FinalTimeDeliveryHandler extends Thread {

	ObjectOutputStream oos;
	EmailMessage message;
	public FinalTimeDeliveryHandler(ObjectOutputStream oos, EmailMessage message){
		this.oos = oos;
		this.message = message;
	}

	public void run(){
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

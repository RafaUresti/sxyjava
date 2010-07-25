package edu.upenn.cis505.g48.pop3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import edu.upenn.cis505.g48.model.Email;

public class GroupEmailDeletionHandler{

	ArrayList<Email> deletedEmail;
	String destIp;
	int destPort;
	String emailAddress;
	public GroupEmailDeletionHandler(ArrayList<Email> deletedEmail, String emailAddress, String destIp, int destPort){
		this.deletedEmail = deletedEmail;
		this.destIp = destIp;
		this.destPort = destPort;
		this.emailAddress = emailAddress;
	}
	
	public void run(){
		Socket sock;
		try {
			sock = new Socket(destIp, destPort);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeUTF("DELETE_EMAIL");
			oos.flush();
			oos.writeUTF(emailAddress);
			oos.writeObject(deletedEmail);
			oos.flush();
			// oos.close();
			// sock.close();
		} catch (UnknownHostException e) {
			System.out.println("Cannot connect to " + destIp + ":" + destPort);
		} catch (IOException e) {
			System.out.println("Cannot connect to " + destIp + ":" + destPort);
		}
	}
}

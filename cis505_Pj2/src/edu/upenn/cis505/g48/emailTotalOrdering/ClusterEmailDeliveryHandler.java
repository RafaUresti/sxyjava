package edu.upenn.cis505.g48.emailTotalOrdering;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClusterEmailDeliveryHandler extends Thread{

	Socket sock;
	private EmailMessage message;
	long replyTime = -1;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	public ClusterEmailDeliveryHandler(Socket sock, EmailMessage message){
		this.sock = sock;
		this.message = message;
	}
	public void run(){
		try {
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			oos.writeUTF("INCOMING_EMAIL");
			oos.writeObject(message);
			oos.flush();
			EmailMessage replyMessage = (EmailMessage)ois.readObject();
			replyTime = replyMessage.messageTime;			
		} catch (UnknownHostException e) {
			System.out.println("Failed to connect to " + 
					sock.getInetAddress().getHostAddress()+":"+sock.getPort());
			System.out.println("The host might be down");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to connect to " + 
					sock.getRemoteSocketAddress()+":"+sock.getPort());
			System.out.println("The host might be down");
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	}
}

package edu.upenn.cis505.g48.sync_msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;

public class Sync_Sender implements Runnable{
	boolean DEBUG = false;
	
	String comm;
	Sync_Msg msg;
	InetSocketAddress addr;
	
	public void set_params(String comm, Sync_Msg msg, InetSocketAddress addr) {
		this.comm = comm;
		this.msg = msg;
		this.addr = addr;
	}
	
	void send_to_one(String comm, Sync_Msg msg, InetSocketAddress addr) {
		Socket sock = new Socket();
		try {
			// idle a bit, to reduce threads
//			try {
//				Thread.sleep(Common_Settings.sync_sender_delay);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			if (DEBUG)
				System.out.printf("** SynM_Sender: send msg to (%s, %d)\n",
						addr.getHostName(), addr.getPort());
			
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
			sock.connect(addr, Common_Settings.socket_connect_timeout);

			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

			oos.writeUTF(comm);
			// oos.flush();
			
			oos.writeObject(msg);
			oos.flush();
			
			// String response = ois.readUTF();
			// sock.close();
		} catch (SocketException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
	public static void main(String[] args) {

	}
	@Override
	public void run() {
		send_to_one(this.comm, this.msg, this.addr);
	}

}

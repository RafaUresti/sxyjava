package edu.upenn.cis505.g48.extended_smtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Add_To_Registry_Server {
	String host;
	int port;
	OutputStream out_stream;
	InputStream in_stream;
	PrintWriter pw_out;
	Socket sock;
	ObjectOutputStream oos_out;
	ObjectInputStream oos_in;
	
	String param_host;
	int param_pop_port;
	int param_smtp_port;
	String param_group;
	int param_group_port;

	static public void main(String args[]) {
		Add_To_Registry_Server obj = new Add_To_Registry_Server();
		obj.set_params("C new", 81, 81, "azc", 81);
		try {
			obj.add();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void set_params(String host, int pop_port, int smtp_port, String group, int group_port) {
		param_host = host;
		param_pop_port = pop_port;
		param_smtp_port = smtp_port;
		param_group = group;
		param_group_port = group_port;
	}
	
	public void add() throws UnknownHostException, IOException {
//		host = "nems.seas.upenn.edu";
//		port = 4242;
		
		host = "localhost";
		port = 54333;
		
		sock = new Socket(host, port);
		oos_out = new ObjectOutputStream(sock.getOutputStream());
		oos_in = new ObjectInputStream(sock.getInputStream());
		
		String heading_msg = oos_in.readUTF();
		System.out.println(heading_msg);
		
		/**
			host = in.readUTF();
			pop_port = in.readInt();
			smtp_port = in.readInt();
			group = in.readUTF();
			group_port = in.readInt();
		*/
		
		oos_out.writeUTF("ADD");
		oos_out.writeUTF(param_host);
		oos_out.writeInt(param_pop_port);
		oos_out.writeInt(param_smtp_port);
		oos_out.writeUTF(param_group);
		oos_out.writeInt(param_group_port);
		oos_out.flush();
		
		String stra = oos_in.readUTF();
		System.out.println(stra);
		
		sock.close();
	}
}

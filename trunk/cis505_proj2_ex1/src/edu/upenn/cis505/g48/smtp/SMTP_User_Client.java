package edu.upenn.cis505.g48.smtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class SMTP_User_Client {
	boolean DEBUG = false;

	final int socket_read_timeout = 8000;
	final int socket_connect_timeout = 1000;
	final int buffer_size = 1024;
	String host;
	int port;
	OutputStream out_stream;
	InputStream in_stream;
	PrintWriter pw_out;
	Socket sock;

	String smtp_host = "";
	int smtp_port = -1;

	static public void main(String[] args) {
		SMTP_User_Client obj = new SMTP_User_Client();
		// obj.control("smtp.163.com", 25);

		System.out.printf("** Welcome to SMTP client, entry [host] [port]\n");
		Scanner scan = new Scanner(System.in);
		String comm = scan.nextLine();
		String tokens[] = comm.split(" ");

		obj.control(tokens[0], Integer.parseInt(tokens[1]));
		// obj.control("localhost", 25);
	}

	public void control(String host, int port) {
		do_connect(host, port);
		System.out.printf("reply: [%s]\n", recv());
		Scanner scan = new Scanner(System.in);		
		while (true){
			String comm = scan.nextLine();

			String tokens[] = comm.split(" ");
			if (tokens[0].equals("~")) {
				if (tokens.length == 1)
					System.out.printf("recv once: [%s]\n", recv());
				else {
					String to_send = comm.substring(2, comm.length());
					System.out.printf("send without recv(): %s\n", to_send);

					send(to_send);
				}
			} else {
				send(comm);
				System.out.printf("got reply: [%s]\n", recv());
			}
		}		
	}

	public void do_connect(String the_host, int the_port) {
		host = the_host;
		port = the_port;
		try {
			core_connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void core_connect() throws IOException {
		sock = new Socket();
		sock.setSoTimeout(socket_read_timeout);
		InetSocketAddress addr = new InetSocketAddress(host, port);
		sock.connect(addr, socket_connect_timeout);
		out_stream = sock.getOutputStream();
		in_stream = sock.getInputStream();
		pw_out = new PrintWriter(out_stream);
	}

	public void send(String comm) {
		pw_out.printf("%s\n", comm);
		pw_out.flush();
	}

	public String recv() {
		byte[] buf = new byte[buffer_size];
		String rtn = null;
		try {
			int len = in_stream.read(buf);
			if (len != -1)
				rtn = new String(buf, 0, len);
		} catch (IOException e) {
			rtn = null;
			System.out.printf("no reply from remote host.\n");
			// e.printStackTrace();
		}
		return rtn;
	}
	public void close() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

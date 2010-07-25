package edu.upenn.cis505.g48.smtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SMTP_Message_Sender {
	String remote_host = "";
	int remote_port = -1;
	String mail_from;
	String rcpt_to;
	String msg_body;
	Socket sock = null;
	OutputStream out;
	InputStream in;
	PrintWriter pw_out;
	
	public void set_params(String host, int port, String from, String to, String msg) {
		remote_host = host;
		remote_port = port;
		mail_from = from;
		rcpt_to = to;
		msg_body = msg;
	}
	
	boolean connect() {
		if (remote_host.equals(""))
			return false;
		try {
			sock = new Socket(remote_host, remote_port);
			if (sock != null) {
				out = sock.getOutputStream();
				in = sock.getInputStream();
				pw_out = new PrintWriter(out);
				return true;
			}
			else return false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public int relay_message() {
		if (remote_host.equals(""))
			return -1;
		if (! connect())
			return -1;
		
		pw_out.printf("EHLO smtp\n");
		pw_out.flush();
		print_response();
		auth_login();
		pw_out.printf("MAIL FROM: <%s>\n", mail_from);
		pw_out.flush();
		print_response();
		
		pw_out.printf("RCPT TO: <%s>\n", rcpt_to);
		pw_out.flush();
		print_response();
		
		pw_out.printf("DATA\n");
		pw_out.flush();
		print_response();
		
		pw_out.printf("%s\n", msg_body);
		pw_out.flush();
		print_response();
		
		pw_out.printf("\r\n.\r\n");
		pw_out.flush();
		print_response();
		
		pw_out.printf("QUIT\n");		
		pw_out.flush();
		print_response();
		
		return -1;
	}
	void auth_login() {
		String user = "cGFwYXlhOTQ=";
		String psw = "c3VpZHluZXI=";
		pw_out.printf("AUTH LOGIN\n");
		pw_out.flush();
		print_response();
		pw_out.printf("%s\n", user);
		pw_out.flush();
		print_response();
		pw_out.printf("%s\n", psw);
		pw_out.flush();
		print_response();
	}
	void print_response() {
		byte[] buf = new byte[1024];
		// while (true) {
			try {
				int len = in.read(buf);
				String str = new String(buf, 0, len);
				System.out.println(str);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		// }
	}
}

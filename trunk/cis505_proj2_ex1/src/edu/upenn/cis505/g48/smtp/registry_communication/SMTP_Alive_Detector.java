package edu.upenn.cis505.g48.smtp.registry_communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;

public class SMTP_Alive_Detector {
	int socket_read_timeout = 2000;
	int socket_connect_timeout = 2000;
	
	OutputStream out_stream;
	InputStream in_stream;
	PrintWriter pw_out;

	public boolean detect(String host, int port) {
		Socket sock = new Socket();
		try {
			sock.setSoTimeout(socket_read_timeout);
		} catch (SocketException e) {
//			e.printStackTrace();
			return false;
		}
		InetSocketAddress addr = new InetSocketAddress(host, port);
		try {
			sock.connect(addr, socket_connect_timeout);
		} catch (IOException e) {
//			e.printStackTrace();
			return false;
		}
		try {
			out_stream = sock.getOutputStream();
			in_stream = sock.getInputStream();
		} catch (IOException e) {
//			e.printStackTrace();
			return false;
		}
		pw_out = new PrintWriter(out_stream);
		
		// till here, connect OK
		String greeting = recv();
		if (greeting == null)
			return false;
		else {
			send("QUIT");
			try {
				sock.close();
			} catch (IOException e) {
//				e.printStackTrace();
			}
			return true;
		}
	}
	
	public void send(String comm) {
		pw_out.printf("%s\r\n", comm);
		pw_out.flush();
	}
	
	public String recv() {
		byte[] buf = new byte[Common_Settings.buffer_size];
		String rtn = null;
		try {
			int len = in_stream.read(buf);
			if (len != -1)
				rtn = new String(buf, 0, len);
		} catch (IOException e) {
			// remote host response error
			rtn = null;
		}
		return rtn;
	}
}

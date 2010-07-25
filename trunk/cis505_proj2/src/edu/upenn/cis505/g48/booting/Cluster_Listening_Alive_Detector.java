package edu.upenn.cis505.g48.booting;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;

public class Cluster_Listening_Alive_Detector {
	OutputStream out_stream;
	InputStream in_stream;

	public boolean detect(String host, int port) {
		Socket sock = new Socket();
		try {
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
		} catch (SocketException e) {
//			e.printStackTrace();
			return false;
		}
		InetSocketAddress addr = new InetSocketAddress(host, port);
		try {
			sock.connect(addr, Common_Settings.socket_connect_timeout);
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
		
		// till here, connect OK
		try {
			ObjectOutputStream oos = new ObjectOutputStream(out_stream);
			oos.writeUTF("HELLO");
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}

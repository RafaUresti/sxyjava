package edu.upenn.cis505.g48.smtp.poll_other_cluster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;

public class Cluster_Poller implements Runnable{
	boolean DEBUG = true;
	
	OutputStream out_stream;
	InputStream in_stream;
	PrintWriter pw_out;

	String domain = "";
	String user = "";
	String host = "";
	int port = -1;

	public int result = -100;
	
	/** methods begin */
	public void set_params(String domain, String user, String host, int port) {
		this.domain = domain;
		this.user = user;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * -1: cannot connect
	 * -2: no welcome msg from remote server
	 * -3: no result from remote server regarding the request
	 * -4: return code un-recognizable
	 * */
	int poll_for_user_domain(String domain, String user, String host, int port) {
		Socket socket = new Socket();
		try {
			socket.setSoTimeout(Common_Settings.socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(host, port);
			
//			if (DEBUG) {
//				System.out.printf("** Poller: try to poll %s-%d\n", 
//						addr.getAddress(), addr.getPort());
//			}
			
			socket.connect(addr, Common_Settings.socket_connect_timeout);
			out_stream = socket.getOutputStream();
			in_stream = socket.getInputStream();
			pw_out = new PrintWriter(out_stream);

			// recv the welcome msg from smtp server
			String result = recv();
			if (result == null) {
				send("QUIT");
				socket.close();
				return -2;
			}

			// send LVRF command
			send("HELO g48");
			String result1 = recv();
			
			String request = String.format("LVRF %s@%s", user, domain);
			send(request);

			// recv result
			result = recv();
			
			if (DEBUG)
				System.out.printf("\n** Poller: result for [LVRF %s@%s]: (%s)(%s), of (%s, %d)\n\n",
						user, domain,
						result1, result,
						addr.getAddress(), addr.getPort());
			
			if (result == null) {
				send("QUIT");
				socket.close();
				return -3;
			}

			// parse result code
			int result_code = get_first_3_digist(result);
			if (result_code == -1) {
				send("QUIT");
				socket.close();
				return -4;
			}

			// return result code
			send("QUIT");
			socket.close();
			return result_code;
			
		} catch (IOException e) {
			// remote host response error
			e.printStackTrace();
			return -1;
		}
	}

	int get_first_3_digist(String str) {
		str = str.substring(0, 3);
		try {
			int i = Integer.parseInt(str);
			return i;
		} catch (NumberFormatException e){
			return -1;
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
			// System.out.printf("no reply from remote host.\n");
			// e.printStackTrace();
		}
		return rtn;
	}

	
	public void run() {
		result = poll_for_user_domain(domain, user, host, port);
	}
	
	public static void main(String[] args) {

	}
}

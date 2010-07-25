package edu.upenn.cis505.g48.smtp;
import java.net.*;
import java.io.*;

import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.ServerData;

public class SMTP_Client_Handler implements Runnable{
	int my_pulse_count = 0;
	static int my_total_count = 0;
	
	boolean DEBUG = true; 
	
	SMTP_Server super_server;
	
	Socket sock = null;
	SMTP_Service_Provider smtp_service_provider = null;
	ServerData the_server_data = null;
	boolean init_session = true;

	
	public void run() {
		do_service();
	}

	public void init_smtp(PrintWriter out_print) {
		// init smtp service provider
		smtp_service_provider = new SMTP_Service_Provider();
		smtp_service_provider.init(out_print, super_server);
	}
	
	public SMTP_Client_Handler(Socket the_sock, SMTP_Server upper_server) {
		// set the socket
		sock = the_sock;
		// set the super server
		super_server = upper_server;
	}

	void do_service(){
		try {
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			PrintWriter out_print_writer = new PrintWriter(out, true);
			InetAddress inet_addr = sock.getInetAddress();
			
			init_smtp(out_print_writer);
			smtp_service_provider.sock = sock;
			
			if (init_session == true) {
				out_print_writer.println("220 Hello from g48's smtp server :P");
				out_print_writer.flush();
				init_session = false;
			}

			String comm_read = "";
			while (true) {
				byte[] buf = new byte[1024];
				int len = in.read(buf);
				
				// XXX: not sure about this use here
				if (len == -1) {
					if (DEBUG)
						System.err.printf("** SMTP handler pulse [%d][%d][%d]\n",
								Thread.currentThread().getId(),
								this.my_pulse_count++,
								SMTP_Client_Handler.my_total_count++);
					break;
					// continue;
				}

				String received_str = new String(buf, 0, len);
				if (DEBUG) {
					String server_id = Integrated_Server.global_inte_server.cluster_name + "-" +
						Integrated_Server.global_inte_server.cluster_port;
					
					if (received_str.length() < 30)
						System.out.printf("[%s]SMTP server get request: [%s]\n", server_id, received_str);
				}

				comm_read += received_str;
				
				int sub = 0;
				if (comm_read.endsWith("\r\n")) sub = 2;
				else if (comm_read.endsWith("\r")) sub = 1;
				else if (comm_read.endsWith("\n")) sub = 1;
				if (sub > 0) {
					int result = smtp_service_provider.rcv_command(comm_read);
					if (result == -1) {
						// get command QUIT
						// close this service thread with current socket
						if (DEBUG)
							System.out.printf("receive QUIT, close this service thread\n");
						sock.close();
						break;
					}
					out_print_writer.flush();
					comm_read = "";
				}
			}
		} catch (IOException e) {
			// System.out.println("-- SMTP_Client_Handler: exception");
			// e.printStackTrace();
		}
	}
}

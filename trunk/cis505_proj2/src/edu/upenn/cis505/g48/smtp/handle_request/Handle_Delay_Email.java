package edu.upenn.cis505.g48.smtp.handle_request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.smtp.SMTP_Service_Provider;

public class Handle_Delay_Email {
	boolean DEBUG = true;

	String mail_from;
	String rcpt_to;
	String msg_body;
	Socket sock = null;

	OutputStream out;
	InputStream in;
	PrintWriter pw_out;

	public void set_params(String from, String to, String msg) {
		mail_from = from;
		rcpt_to = to;
		msg_body = msg;
	}
	
	String get_cluster_for_domain(String rcpt_id) {
		String tokens[] = rcpt_id.split("@");
		String domain_name = tokens[1];
		SMTP_Service_Provider ssp = new SMTP_Service_Provider();
		
		if (DEBUG)
			System.out.printf("** Delay_Email: begin DVRF for %s\n", rcpt_id);
		String response = ssp.command_DVRF("DVRF " + domain_name);
		if (DEBUG)
			System.out.printf("** Delay_Email: end DVRF resp: %s\n", response);
		
		if (response.startsWith("251")) {
			String tokens2[] = response.split(" ");
			return tokens2[1];
		} else {
			return null;
		}
	}
	
	public boolean work() {
		String remote_cluser = get_cluster_for_domain(this.rcpt_to);
		if (remote_cluser == null) {
			if (DEBUG)
				System.out.printf("** Delay_Email: DVRF fail, cannot locate server for domain\n");
			return false;
		}
		
		InetSocketAddress addr = 
			Integrated_Server.global_inte_server.registry_commu_manager.
			get_smtp_addr_SET_for_cluster(remote_cluser);
		
		if (addr != null) {
			if (DEBUG)
				System.out.printf("** Delay_Email: try CONNECT %s %s\n", 
						addr.getHostName(), addr.getPort());
			
			if (try_connect(addr)) {
				return relay_message();
			} else {
				if (DEBUG)
					System.out.printf("** Delay_Email: fail SEND or CONNECT %s %s\n", 
							addr.getHostName(), addr.getPort());
				return false;
			}
		}
		else 
			return false;
	}

	boolean try_connect(InetSocketAddress addr) {
		try {
			sock = new Socket();
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
			sock.connect(addr, Common_Settings.socket_connect_timeout);
			out = sock.getOutputStream();
			in = sock.getInputStream();
			pw_out = new PrintWriter(out);

			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean relay_message() {
		
		// recv the greeting from SMTP server
		print_response();
		
		pw_out.printf("HELO smtp\n");
		pw_out.flush();
		System.out.printf("I say: [HELO smtp]\n");
		print_response();
		
		
		pw_out.printf("MAIL FROM: <%s>\n", mail_from);
		pw_out.flush();
		System.out.printf("I say: [MAIL FROM: <%s>]\n", mail_from);
		print_response();
		

		pw_out.printf("RCPT TO: <%s>\n", rcpt_to);
		pw_out.flush();
		System.out.printf("I say: [RCPT TO: <%s>]\n", rcpt_to);
		print_response();
		

		pw_out.printf("DATA\n");
		pw_out.flush();
		System.out.printf("I say: [DATA]\n");
		print_response();

		pw_out.printf("%s\n", msg_body);
		pw_out.flush();
		System.out.printf("I say: [%s]\n", msg_body);
		// no response

		pw_out.printf("\r\n.\r\n");
		pw_out.flush();
		System.out.printf("I say: [\\r\\n.\\r\\n]\n");
		print_response();

		pw_out.printf("QUIT\n");		
		pw_out.flush();
		System.out.printf("I say: [QUIT]\n");
		print_response();

		return true;
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
	
	boolean print_response() {
		byte[] buf = new byte[Common_Settings.buffer_size];
		try {
			int len = in.read(buf);
			if (len == -1)
				return false;
			String str = new String(buf, 0, len);
			System.out.println(str);
			return true;
		} catch (IOException e) {
			return false;
//			e.printStackTrace();
		}
	}
}

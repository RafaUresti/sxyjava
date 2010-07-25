package edu.upenn.cis505.g48.smtp.handle_request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import edu.upenn.cis505.g48.cluster_internal_communication.Multicast_NEWU_INFO;
import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.smtp.SMTP_Service_Provider;
import edu.upenn.cis505.g48.smtp.SMTP_Tools;

public class Handle_NEWU {
	public  SMTP_Service_Provider smtp_service = null;

	OutputStream out_stream;
	InputStream in_stream;
	PrintWriter pw_out;
	Socket sock;
	
	public Handle_NEWU(SMTP_Service_Provider smtp) {
		this.smtp_service = smtp;
	}
	
	public String handle(String user, String domain, String psw) {
		Handle_Create_Domain create_domain = new Handle_Create_Domain(smtp_service);
		String response = create_domain.handle(domain);
		
		if (response.startsWith("551")) {
			// domain exists locally
			
			// XXX: change for new interface, check in further if it works
			if (Integrated_Server.global_inte_server.server_data.has_user_at_domain(user, domain)) {
			// if (tool.has_user(smtp_service.server_data, domain, user)) {
				
				return "550 FAILURE";
			} else {
				// XXX: create user locally
				Integrated_Server.global_inte_server.server_data.
					create_user_to_domain(user, domain, psw);
				// TODO: multicast
				Multicast_NEWU_INFO multi = new Multicast_NEWU_INFO();
				multi.set_params(user, domain, psw);
				new Thread(multi).start();
				
				return "250 SUCCESS new user locally";
			}
		}
		else if (response.startsWith("550")) {
			String[] tokens = response.split(" ");
			InetSocketAddress addr = 
				Integrated_Server.global_inte_server.registry_commu_manager.
					get_smtp_addr_for_cluster(tokens[1]);
			
			if (addr == null) {
				return "554 FAILURE";
			} else {
				core_connect(addr);
				String greeting = conn_recv();
				
				// TODO: added new FISH
				conn_send("HELO g48");
				String r2 = conn_recv();
				
				conn_send(String.format("NEWU %s@%s %s", user, domain, psw));
				String resp = conn_recv();
				resp += " (get relay msg)";
				
				//let the remote handler down
				conn_send("QUIT");
				return resp;
			}
		}
		else if (response.startsWith("250")) {
			// XXX: create user locally
			Integrated_Server.global_inte_server.server_data.
			create_user_to_domain(user, domain, psw);
			// TODO: multicast
			Multicast_NEWU_INFO multi = new Multicast_NEWU_INFO();
			multi.set_params(user, domain, psw);
			new Thread(multi).start();
			
			return "250 SUCCESS";
		}
		else {
			return "554 FAILURE";
		}
	}
	// standard connect methods
	void core_connect(InetSocketAddress addr) {
		sock = new Socket();
		try {
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
			// InetSocketAddress addr = new InetSocketAddress(host, port);
			sock.connect(addr, Common_Settings.socket_connect_timeout);
			out_stream = sock.getOutputStream();
			in_stream = sock.getInputStream();
			pw_out = new PrintWriter(out_stream);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void conn_send(String comm) {
		pw_out.printf("%s\r\n", comm);
		pw_out.flush();
	}

	public String conn_recv() {
		byte[] buf = new byte[Common_Settings.buffer_size];
		String rtn = null;
		try {
			int len = in_stream.read(buf);
			if (len != -1)
				rtn = new String(buf, 0, len);
		} catch (IOException e) {
			rtn = null;
			// System.out.printf("no reply from remote host.\n");
			// e.printStackTrace();
		}
		return rtn;
	}
	
	void conn_close(){
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {

	}
}

package edu.upenn.cis505.g48.smtp.handle_request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

import edu.upenn.cis505.g48.cluster_internal_communication.Multicast_NEWU_INFO;
import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.smtp.SMTP_Service_Provider;
import edu.upenn.cis505.g48.smtp.SMTP_Tools;
import edu.upenn.cis505.registry.UpdateEntry;

public class Handle_Create_Domain {
	boolean DEBUG = true;
	public  SMTP_Service_Provider smtp_service = null;

	OutputStream out_stream;
	InputStream in_stream;
	PrintWriter pw_out;
	Socket sock;


	/** methods begin */

	public Handle_Create_Domain(SMTP_Service_Provider smtp) {
		this.smtp_service = smtp;
	}

	/**
	 * 250: success created locally
	 * 551: domain exists locally
	 * 550 cluster_name: domain exists in other cluster
	 * 552: strange error
	 * */
	public String handle(String domain_name) {
		// XXX: change for new interface, check in further if works
		if (Integrated_Server.global_inte_server.server_data.has_domain(domain_name)) {
		// if (tool.has_domain(smtp_service.server_data, domain_name)) {
			return "551";
		} else {
			String response = smtp_service.command_DVRF("DVRF " + domain_name);
			if (response.startsWith("251")) {
				String[] tokens = response.split(" "); 
				return "550" + " " + tokens[1];
			}
			if (response.startsWith("550")) {
				// resd all and if suc create domain locally
				if (resd_all(domain_name)) {
					if (DEBUG) {
						String server_id = Integrated_Server.global_inte_server.cluster_name + "-" +
						Integrated_Server.global_inte_server.cluster_port;
						
						System.out.printf("(%s): handle create_domain: succ\n", server_id);
					}
					//XXX: create domain and postmaster@domain locally
					Integrated_Server.global_inte_server.server_data.
						create_domain_then_create_user("postmaster", domain_name, Common_Settings.defaut_user_psw);
					//TODO: multicast
					
					Multicast_NEWU_INFO multi = new Multicast_NEWU_INFO();
					multi.set_params("postmaster", domain_name, Common_Settings.defaut_user_psw);
					new Thread(multi).start();
					
					return "250";
				}
				else return "552"; 
			}
			return "552";
		}
	}

	boolean resd_all(String domain_name) {
		HashSet <UpdateEntry> other_cluster_gate_set = 
			Integrated_Server.global_inte_server.registry_commu_manager.get_other_cluster_gate_server();
		
		for (UpdateEntry ue : other_cluster_gate_set) {
			core_connect(ue.host, ue.smtp_port);
			
			String greeting = conn_recv();
			conn_send("RESD " + domain_name);
			String response = conn_recv();
			
			conn_send("QUIT");
			conn_close();
			
			//XXX: not so sure about why
			if (response == null)
				return false;
			if (! response.startsWith("250"))
				return false;
		}
		return true;
	}

	// standard connect methods
	void core_connect(String host, int port) {
		sock = new Socket();
		try {
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(host, port);
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
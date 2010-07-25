package edu.upenn.cis505.g48.smtp.handle_request;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.smtp.SMTP_Tools;
import edu.upenn.cis505.registry.UpdateEntry;


public class Handle_RESD {
	Integrated_Server inte_server = null;

	public void handle(String comm, PrintWriter out_print_writer,
			Integrated_Server main_server) {
		inte_server = main_server;

		String tokens[] = comm.split(" ");
		SMTP_Tools smtp_tool = new SMTP_Tools();
		
		// boolean in_local = smtp_tool.has_domain(this.inte_server.server_data, tokens[1]);
		// XXX: changed for new server data interface
		boolean in_local = this.inte_server.server_data.has_domain(tokens[1]);

		if (in_local) {
			out_print_writer.println("553 FAILURE, domain already exists");
		} else
			if (resd_in_other_server(tokens[1])) {
				out_print_writer.println("550 FAILURE, domain already reserved in this cluster");
			} else {
				inte_server.resd_manager.add_item(tokens[1]);
				out_print_writer.println("250 SUCCESS");
			}
	}

	boolean resd_in_other_server(String domain_name) {
		HashSet <UpdateEntry> server_set = inte_server.registry_commu_manager.get_alive_my_cluster_server();
		
		for (UpdateEntry ue : server_set) {
			if (poll_one_server(ue.host, ue.group_port, domain_name))
				return true;
		}
		return false;
	}

	boolean poll_one_server(String cluster_host, int cluster_port, String name) {
		Socket sock = new Socket();
		try {
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(cluster_host, cluster_port);
			sock.connect(addr, Common_Settings.socket_connect_timeout);

			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

			oos.writeUTF("IS_RESD " + name);
			oos.flush();
			String response = ois.readUTF();

			sock.close();

			if (response.startsWith("YES"))
				return true;
			if (response.startsWith("No"))
				return false;
			return false;
		} catch (SocketException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {

	}
}

package edu.upenn.cis505.g48.cluster_internal_communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.registry.UpdateEntry;

public class Multicast_NEWU_INFO implements Runnable{
	boolean DEBUG = true;
	String the_user = null;
	String the_domain = null;
	String the_psw = null;
	
	public void set_params(String user, String domain, String psw) {
		the_user = user;
		the_domain = domain;
		the_psw = psw;	
	}
	
	void multicast(String user, String domain, String psw) {
		HashSet <UpdateEntry> set = Integrated_Server.global_inte_server.
			registry_commu_manager.get_alive_my_cluster_server();
		for (UpdateEntry ue: set) {
			send_to_one(String.format("TRANS_NEWU_INFO %s %s %s", user, domain, psw),
					ue.host, ue.group_port);
		}
	}
	void send_to_one(String comm, String cluster_host, int cluster_port) {
		if (DEBUG)
			System.out.printf("** Multicast: send to %s %d comm: [%s]\n",
					cluster_host, cluster_port, comm);
		
		Socket sock = new Socket();
		try {
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(cluster_host, cluster_port);
			sock.connect(addr, Common_Settings.socket_connect_timeout);

			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

			oos.writeUTF(comm);
			oos.flush();
			// String response = ois.readUTF();
			sock.close();
		} catch (SocketException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
	public static void main(String[] args) {

	}
	public void run() {
		multicast(this.the_user, this.the_domain, this.the_psw);
	}

}

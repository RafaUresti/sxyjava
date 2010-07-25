package edu.upenn.cis505.g48.booting;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.ServerData;
import edu.upenn.cis505.g48.smtp.SMTP_Tools;
import edu.upenn.cis505.registry.UpdateEntry;

public class Transfer_Data_When_Boot {
	boolean DEBUG = true;
	
	public boolean work(HashSet <UpdateEntry> set) {
		if (set.size() == 0) {
			if (DEBUG)
				System.out.printf("** Boot data transfer: param server set is 0\n");
			return false;
		}
		// HashSet <UpdateEntry> set = remove_my_server(server_set);
		Cluster_Listening_Alive_Detector detector = new Cluster_Listening_Alive_Detector();
		for (UpdateEntry ue : set) {
			if (detector.detect(ue.host, ue.group_port)) {
				if (DEBUG)
					System.out.printf("** Boot data transfer: before get data\n");
				ServerData got_server_data = get_transfer_data(ue.host, ue.group_port);
				if (DEBUG)
					System.out.printf("** Boot data transfer: after get data\n");
				if (got_server_data != null) {
					
					if (DEBUG)
						System.out.printf("** Boot data transfer: got non-null result\n");
					
					Integrated_Server.global_inte_server.server_data = got_server_data;
					
					// TODO : del debug here
					System.out.println("The data got here: ");
					new SMTP_Tools().disp_server_data(Integrated_Server.global_inte_server.server_data);
					
					return true;
				}
			}
		}
		return false;
	}
	
	ServerData get_transfer_data(String host, int port) {
		Socket sock = new Socket();
		try {
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
		} catch (SocketException e) {
//			e.printStackTrace();
			return null;
		}
		InetSocketAddress addr = new InetSocketAddress(host, port);
		try {
			sock.connect(addr, Common_Settings.socket_connect_timeout);
		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			oos.writeUTF("DATA_REQUEST");
			oos.flush();
			
			ServerData got_server_data;
			try {
				got_server_data = (ServerData) ois.readObject();
				
				return got_server_data;
			} catch (ClassNotFoundException e) {
				// e.printStackTrace();
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
	}

}

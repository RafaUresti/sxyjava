package edu.upenn.cis505.g48.sync_msg;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.registry.UpdateEntry;

public class Sync_Tools {
	boolean DEBUG = false;
	
	int cluster_port = -1;
	Sync_Worker super_worker = null;
	InetSocketAddress my_addr = null;
	
	public void dump_undeliver_list() {
		for (Sync_Msg sm: super_worker.un_deliver_list) {
			System.out.printf("-> %d, %s\n", sm.tm, sm.msg_id);
		}
	}

	public String make_msg_id(InetSocketAddress my_addr, int count) {
		//[127.0.0.1+45112+1998]
		return String.format("%s+%d+%d", 
				my_addr.getHostName(), my_addr.getPort(), count);
	}
	
	public void disp_addr_set(HashSet <InetSocketAddress> set) {
		System.out.printf("** Disp Addr Set:\n");
		for (InetSocketAddress addr : set) {
			System.out.printf("addr: (%s, %d)\n", addr.getHostName(), addr.getPort());
		}
	}

	public HashSet <InetSocketAddress> get_my_cluster_server_addr_set() {
		HashSet <UpdateEntry> my_cluster_server_set = 
			// Integrated_Server.global_inte_server.registry_commu_manager.get_my_cluster_server();
			Integrated_Server.global_inte_server.registry_commu_manager.get_alive_my_cluster_server_with_myself();
		
		HashSet <InetSocketAddress> addr_set = new HashSet <InetSocketAddress> ();

		for (UpdateEntry ue: my_cluster_server_set) {
			addr_set.add(new InetSocketAddress(ue.host, ue.group_port));
		}
		return addr_set;
	}

	boolean is_my_addr(InetSocketAddress addr) {
		return false;
//		if (addr.getAddress().getHostAddress().equals(my_addr.getAddress().getHostAddress()) && addr.getPort() == this.cluster_port)
//			return true;
//		else
//			return false;
	}
	
	UNACK_Pool_Item get_from_unack_pool(HashSet <UNACK_Pool_Item> set, String msg_id) {
		for (UNACK_Pool_Item item: set) {
			if (item.msg_id.equals(msg_id))
				return item;
		}
		return null;
	}
	
	Sync_Msg get_from_undeliver_list(ArrayList <Sync_Msg> list, String msg_id) {
		for (Sync_Msg msg : list) {
			if (msg.msg_id.equals(msg_id))
				return msg;
		}
		return null;
	}

	public void multicast(String comm, Sync_Msg msg, HashSet <InetSocketAddress> addr_set) {
		for (InetSocketAddress addr : addr_set) {
			if (! is_my_addr(addr)) {
				Sync_Sender sender = new Sync_Sender();
				sender.set_params(comm, msg, addr);
				Thread sender_thread = new Thread(sender, "Msg Sender Step 1");
				sender_thread.start();
			}
			else {
				// addr for my server
				if (DEBUG)
					System.out.printf("** Sync_Msg.Tool: %s to local addr (%s, %d)",
							msg.msg_id, addr.getAddress().getHostAddress(), addr.getPort());
				super_worker.add_to_msg_queue(msg);
			}
		}
	}

	public void send_one(String comm, Sync_Msg msg) {
		if (! is_my_addr(msg.recv_addr)) {
//			if (DEBUG) {
//				System.out.printf("** SynM.Tool.Send_one: to (%s, %d)\n",
//						msg.recv_addr.getHostName(), msg.recv_addr.getPort());
//			}
			
			Sync_Sender sender = new Sync_Sender();
			sender.set_params(comm, msg, msg.recv_addr);
			Thread sender_thread = new Thread(sender, "Msg Sender Step 2");
			sender_thread.start();
		}
		else {
//			if (DEBUG) {
//				System.out.printf("** SynM.Tool.Send_one: to local\n");
//			}			
			// addr for my server
			super_worker.add_to_msg_queue(msg);
		}
	}



	public static void main(String[] args) {
		ArrayList <Sync_Msg> list = new ArrayList <Sync_Msg>();
		Sync_Msg m1 = new Sync_Msg();
		m1.tm = 1;
		m1.msg_id = "c";
		
		Sync_Msg m2 = new Sync_Msg();
		m2.tm = 1;
		m2.msg_id = "b";
		
		Sync_Msg m3 = new Sync_Msg();
		m3.tm = 2;
		m3.msg_id = "d";
		
		Sync_Msg m4 = new Sync_Msg();
		m4.tm = 3;
		m4.msg_id = "c";
		
		list.add(m1);
		list.add(m2);
		list.add(m3);
		list.add(m4);
		
		Collections.sort(list, new Sync_Msg_Comparator());
		for (Sync_Msg sm: list) {
			System.out.printf("%d - %s\n", sm.tm, sm.msg_id);
		}
		
	}

}

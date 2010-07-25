package edu.upenn.cis505.g48.sync_msg;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import edu.upenn.cis505.g48.integrated_server.Integrated_Server;

// TODO: make sure about my server addr

public class Sync_Worker implements Runnable{
	boolean DEBUG = false;

	int msg_count = 0;
	public int lc_P = 0;
	public int lc_A = 0;
	HashSet <Sync_Msg> msg_queue = new HashSet <Sync_Msg> ();
	ArrayList <Sync_Msg> un_deliver_list = new ArrayList <Sync_Msg> ();
	HashSet <UNACK_Pool_Item> un_ack_pool = new HashSet <UNACK_Pool_Item> ();

	int cluster_port;
	InetSocketAddress my_addr = null;
	public Sync_Tools tool = new Sync_Tools();

	public Sync_Worker(int cluster_port) {
		this.cluster_port = cluster_port;
		tool.cluster_port = this.cluster_port;
		tool.super_worker = this;
		try {
			my_addr = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(),
					this.cluster_port);
			tool.my_addr = this.my_addr;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void add_to_msg_queue(Sync_Msg msg) {
		synchronized (msg_queue) {
			msg_queue.add(msg);
		}
	}

	Sync_Msg get_from_msg_queue() {
		synchronized (msg_queue) {
			if (msg_queue.size() == 0)
				return null;
			else {
				Iterator <Sync_Msg> ite = msg_queue.iterator();
				Sync_Msg rtn = ite.next();
				msg_queue.remove(rtn);
				return rtn;
			}
		}
	}

	public void accept_for_deliver(byte[] data, String user, String domain) {
		
			if (DEBUG)
				System.out.printf("** SynM-0: accept msg for deliver\n");

			UNACK_Pool_Item unack_pool_item = new UNACK_Pool_Item();
			unack_pool_item.msg_id = tool.make_msg_id(my_addr, ++ this.msg_count);
			unack_pool_item.tm = 0;
			unack_pool_item.unack_server_pool = tool.get_my_cluster_server_addr_set();

			if (DEBUG) {
				System.out.printf("** SynM-0: put to UN-ACK pool, addrs to ack:\n");
				tool.disp_addr_set(unack_pool_item.unack_server_pool);
			}

			this.un_ack_pool.add(unack_pool_item);

			Sync_Msg msg_to_cast = new Sync_Msg();
			// TODO: if need to copy
			// msg_to_cast.data = data;

			// TODO: temp debug, give msg_id to data
			msg_to_cast.data = data;
			
			msg_to_cast.user = user;
			msg_to_cast.domain = domain;
			msg_to_cast.msg_id = unack_pool_item.msg_id;
			msg_to_cast.send_addr = this.my_addr;
			msg_to_cast.step = 1;

			if (DEBUG) {
				System.out.printf("** SynM-0: multicast step-1:\n");
			}
			tool.multicast("SYNC_MSG", msg_to_cast, tool.get_my_cluster_server_addr_set());
		
	}

	void process_step_1(Sync_Msg msg) {
		if (DEBUG) {
			System.out.printf("** SynM-1: get step-1 msg from (%s, %d), add to UN-Deliver list\n",
					msg.send_addr.getHostName(), msg.send_addr.getPort());
			System.out.printf("** SynM-1: current P: %d, A: %d, new P: %d\n",
					lc_P, lc_A, Math.max(lc_P, lc_A) + 1);
		}

		lc_P = Math.max(lc_P, lc_A) + 1;
		
		msg.ready_deliver = false;
		//TODO: added rule
		msg.tm = lc_P;
		un_deliver_list.add(msg);

		Sync_Msg to_send = new Sync_Msg();
		to_send.tm = lc_P;
		to_send.send_addr = this.my_addr;
		to_send.recv_addr = msg.send_addr;
		to_send.step = 2;
		to_send.msg_id = msg.msg_id;

		if (DEBUG) {
			System.out.printf("** SynM-1: send back step-2 with tm: %d\n",
					to_send.tm);
		}

		tool.send_one("SYNC_MSG", to_send);
	}

	void process_step_2(Sync_Msg msg) {
		if (DEBUG) {
			System.out.printf("** SynM-2: recv [* step-2 *] from (%s, %d), msg_id: %s, tm: %d\n",
					msg.send_addr.getHostName(), msg.send_addr.getPort(),
					msg.msg_id, msg.tm);
		}

		UNACK_Pool_Item item = tool.get_from_unack_pool(un_ack_pool, msg.msg_id);
		if (item != null) {
			if (DEBUG) {
				System.out.printf("** SynM-2: find unack-item for %s, tm: %d\n",
						msg.msg_id, msg.tm);
			}
			if (msg.tm > item.tm) {
				if (DEBUG) {
					System.out.printf("** SynM-2: update unack-item tm from %d to %d\n",
							item.tm, msg.tm);
				}
				item.tm = msg.tm;
			}
			item.unack_server_pool.remove(msg.send_addr);
			if (item.unack_server_pool.size() == 0) {
				un_ack_pool.remove(item);

				Sync_Msg to_multicast = new Sync_Msg();
				to_multicast.tm = item.tm;
				to_multicast.msg_id = item.msg_id;
				to_multicast.step = 3;

				if (DEBUG) {
					System.out.printf("** SynM-2: got all ACKs for %s, multicast step-3 with tm: %d\n",
							to_multicast.msg_id, to_multicast.tm);
				}

				tool.multicast("SYNC_MSG", to_multicast, tool.get_my_cluster_server_addr_set());
			}
		}
	}

	@SuppressWarnings("unchecked")
	void process_step_3(Sync_Msg msg) {
		if (DEBUG) {
			System.out.printf("** SynM-3: recv [* step-3 *] for %s with tm: %d. update A from %d to %d\n",
					msg.msg_id, msg.tm, this.lc_A, Math.max(this.lc_A, msg.tm));
		}

		//XXX: update logic clock A
		this.lc_A = Math.max(this.lc_A, msg.tm);

		Sync_Msg item = tool.get_from_undeliver_list(this.un_deliver_list, msg.msg_id);
		if (item != null) {

			if (DEBUG) {
				System.out.printf("** SynM-3: find un-delive item for %s, update tm from %d to %d, READY to deliver\n",
						item.msg_id, item.tm, Math.max(item.tm, msg.tm));
			}

			// item.tm = Math.max(item.tm, msg.tm);
			item.tm = msg.tm;
			
			item.ready_deliver = true;
			// TODO: re-sort
			Collections.sort(this.un_deliver_list, new Sync_Msg_Comparator());
			
			if (DEBUG) {
				Sync_Msg top = this.un_deliver_list.get(0);
				System.out.printf("** SynM-3: after re-sort, top msg is (%s), stat: (%s), tm: (%d)\n",
						top.msg_id, top.ready_deliver, top.tm);
				
				int c = 0;
				for (Sync_Msg sm : un_deliver_list) {
					System.out.printf("[%d]--> tm: %d, stat: %s, id: %s\n", c++, sm.tm, sm.ready_deliver,
							sm.msg_id);
				}
			}
			
			int deliver_count = 0;
			for (int i=0; i<this.un_deliver_list.size(); i++) {
				Sync_Msg the_msg = this.un_deliver_list.get(i);
				if (the_msg.ready_deliver) {
					deliver_count ++;
					
					Integrated_Server.global_inte_server.server_data.add_email_to_user_at_domain(the_msg.data, 
							the_msg.user, the_msg.domain);
					
					
					System.out.printf("** SynM-3: <<DELIVER>> tm: %d, (%s, %d) (%s) count: %d to Application\n",
							the_msg.tm,
							Integrated_Server.global_inte_server.local_host, 
							Integrated_Server.global_inte_server.cluster_port,
							the_msg.msg_id, deliver_count);
					/**
					if (DEBUG) {
						System.out.printf("** SynM-3: <<DELIVER>> (%s, %s) (%s) to Application\n",
								Integrated_Server.global_inte_server.local_host, 
								Integrated_Server.global_inte_server.cluster_port,
								the_msg.msg_id);
					}	
					*/
				}
				else
					break;
			}
			
			for (int i=0; i<deliver_count; i++) {
				this.un_deliver_list.remove(0);
			}
			
			/**
			Iterator <Sync_Msg> ite = this.un_deliver_list.iterator();
			Sync_Msg top_msg = ite.next();

			if (DEBUG) {
				System.out.printf("** SynM-3: top item: %s, stat: %s\n",
						top_msg.msg_id, top_msg.ready_deliver);
			}

			if (top_msg.ready_deliver) {
				this.un_deliver_list.remove(top_msg);
				// TODO: devliver to application
				
				Integrated_Server.global_inte_server.server_data.add_email_to_user_at_domain(top_msg.data, 
						top_msg.user, top_msg.domain);

				if (DEBUG) {
					System.out.printf("** SynM-3: <<DELIVER>> (%s, %s) (%s) to Application\n",
							Integrated_Server.global_inte_server.local_host, 
							Integrated_Server.global_inte_server.cluster_port,
							top_msg.msg_id);
				}
			}
			*/
		}
	}

	void main_work() {
		while(true) {
			Sync_Msg a_msg = null;
			while((a_msg = get_from_msg_queue()) == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}			
			}

			if (a_msg.step == 1)
				process_step_1(a_msg);
			if (a_msg.step == 2)
				process_step_2(a_msg);
			if (a_msg.step == 3)
				process_step_3(a_msg);
		}
	}

	public static void main(String[] args) {

	}

	public void run() {
		main_work();
	}

}

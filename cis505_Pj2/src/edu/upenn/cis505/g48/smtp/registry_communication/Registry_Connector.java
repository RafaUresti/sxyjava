package edu.upenn.cis505.g48.smtp.registry_communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.TimerTask;

import edu.upenn.cis505.g48.booting.Transfer_Data_When_Boot;
import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.ServerData;
import edu.upenn.cis505.g48.utility.Utility;
import edu.upenn.cis505.registry.UpdateEntry;

public class Registry_Connector extends TimerTask {
	static public boolean DEBUG = false;
	static final String server_reset_label = "RESET";

	public Registry_Communication_Manager manager = null;

	String host;
	int port;

	String param_host;
	int param_pop_port;
	int param_smtp_port;
	String param_group;
	int param_group_port;

	/* methods begin */
	public void add() {
		Socket sock = new Socket();
		try {
			sock.setSoTimeout(Common_Settings.socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(host, port);
			sock.connect(addr, Common_Settings.socket_connect_timeout);

			ObjectOutputStream oos_out = new ObjectOutputStream(sock.getOutputStream());
			ObjectInputStream ois_in = new ObjectInputStream(sock.getInputStream());

			String heading_msg = ois_in.readUTF();

			// if the server in WARM UP period
			if (heading_msg.startsWith(Registry_Connector.server_reset_label)) {
				if (DEBUG)
					System.out.println("** Connector: registry server in warm up, reset timestamp");	
				reset_timestamp();
			}

			if (DEBUG)
				System.out.println(heading_msg);

			oos_out.writeUTF("ADD");
			oos_out.writeUTF(param_host);
			oos_out.writeInt(param_pop_port);
			oos_out.writeInt(param_smtp_port);
			oos_out.writeUTF(param_group);
			oos_out.writeInt(param_group_port);
			oos_out.flush();

			String stra = ois_in.readUTF();
			if (DEBUG)
				System.out.println(stra);

			sock.close();
		} catch (SocketException e) {
			if (DEBUG)
				System.out.printf("** Registry Connector: add(): SocketException\n");
			// e.printStackTrace();
		} catch (IOException e) {
			if (DEBUG)
				System.out.printf("** Registry Connector: add(): IOException\n");
			// e.printStackTrace();
		}

	}

	public Object[] get_update() {
		String registy_host = host;
		int registry_port = port;

		Socket socket = new Socket();
		try {
			socket.setSoTimeout(Common_Settings.socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(registy_host, registry_port);
			socket.connect(addr, Common_Settings.socket_connect_timeout);

			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());					
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

			// reading server heading msg
			String heading_msg = inputStream.readUTF();
			if (DEBUG)
				System.out.println(heading_msg);

			// send command: UPDATE
			outputStream.writeUTF("update");//Request for a server
			outputStream.writeInt(this.manager.update_timestamp);
			outputStream.flush();

			Object[] serverUpdateEntries = (Object [])inputStream.readObject();
			this.manager.update_timestamp = inputStream.readInt();
			String response = (String)inputStream.readUTF();

			if (DEBUG)
				System.out.printf("response: %s, num of entries: %d-%d\n\n", response,
						this.manager.update_timestamp, serverUpdateEntries.length);

			//		Get the server information
			//					if (DEBUG) {
			//						for (int i=0; i<serverUpdateEntries.length; i++) {
			//							UpdateEntry entry = (UpdateEntry)serverUpdateEntries[i];
			//							System.out.printf("smtp: %s - %d\n", entry.host, entry.smtp_port);
			//							System.out.printf("pop3: %s - %d\n", entry.host, entry.pop_port);
			//							System.out.printf("group: %s - %d\n", entry.group, entry.group_port);
			//							System.out.println();
			//						}
			//					}

			outputStream.close();
			inputStream.close();
			socket.close();
			return serverUpdateEntries;
		} catch (SocketException e) {
			if (DEBUG)
				System.out.printf("** Registry Connector: update(): SocketException\n");
			// e.printStackTrace();
		} catch (IOException e) {
			if (DEBUG)
				System.out.printf("** Registry Connector: update(): IOException\n");
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if (DEBUG)
				System.out.printf("** Registry Connector: update(): ClassNotFoundException\n");
			// e.printStackTrace();
		}
		return null;
	}

	void work() {
		if (DEBUG) {
			System.out.printf("**> Registry Connector: my current entries:\n");
			if (this.manager.entry_set != null)
				for (UpdateEntry ue: this.manager.entry_set) {
					System.out.printf("--> item: name: %s, host: %s, smtp port: %d\n",
							ue.group, ue.host, ue.smtp_port);
				}
		}
		
		if (Integrated_Server.global_inte_server.register_self_done == false) {
			if (DEBUG)
				System.out.printf("** Registry Connector: register myself\n");
			add();
			if (DEBUG)
				System.out.printf("** Registry Connector: register done\n");
			
			Integrated_Server.global_inte_server.register_self_done = true;
		}
		
		Object[] objs = get_update();
		add_return_entries(objs);

		if (objs != null && objs.length > 0) {
			if (DEBUG)
				System.out.printf("** Registry Connector: add %d new entries\n", objs.length);
		
		// TODO: get transfered data from other server in my cluster
			if (Integrated_Server.global_inte_server.retrive_data_done == false) {
				//XXX: each server only enter this block once in its life
				ServerData serverData = Integrated_Server.global_inte_server.server_data;
				System.out.println("** Server: BEGIN to get data from other server in my cluster\n");
				
				Transfer_Data_When_Boot handle_boot = new Transfer_Data_When_Boot();
				
				boolean suc = handle_boot.work(Integrated_Server.
						global_inte_server.registry_commu_manager.get_alive_my_cluster_server());
				
				if (suc){
					Integrated_Server.global_inte_server.server_data.dump_all_domains();
					Integrated_Server.global_inte_server.server_data.forceDataToDisk();
					
					System.out.println("** Server: got data from other server in my cluster");
				}
				else {
					// TODO:
					String serverDataFileName = serverData.get_filename();
					ServerData persistentSD;
					try {
						persistentSD = (ServerData)Utility.read_from(serverDataFileName);
						
						persistentSD.dump_all_domains();
						
						
						Integrated_Server.global_inte_server.server_data = persistentSD;
						
						System.out.println("Retrieved data from persistent storage");
					} catch (IOException e) {
						//TODO call initialize server data;
						Integrated_Server.global_inte_server.server_data.create_domain(
								this.param_group);
						Integrated_Server.global_inte_server.server_data.create_user_to_domain(
								"postmaster", this.param_group, Common_Settings.defaut_user_psw);
						
						System.out.println("Persistent Server Data is not available, get pre-set data.");
						
					}
				}
				
				Integrated_Server.global_inte_server.retrive_data_done = true;
			}
		}
	}
	
	void del_entry_if_exist(String host, int smtp_port, int pop3_port, String group, int group_port) {
		HashSet <UpdateEntry> waste = new HashSet <UpdateEntry>(); 
		for (UpdateEntry ue : this.manager.entry_set) {
			if (ue.host.equals(host) &&
					ue.smtp_port == smtp_port &&
					ue.pop_port == pop3_port &&
					ue.group.equals(group) &&
					ue.group_port == group_port) {
				
				waste.add(ue);
			}
		}
		for (UpdateEntry ue : waste) {
			if (this.manager.entry_set.contains(ue))
				this.manager.entry_set.remove(ue);
		}
	}

	void add_return_entries(Object[] objs) {
		if (objs == null)
			return;
		for (int i=0; i<objs.length; i++) {
			UpdateEntry ue = (UpdateEntry) objs[i];
			
			if (ue.action == UpdateEntry.ADD_HOST) {
				this.manager.entry_set.add((UpdateEntry) objs[i]);
			}
			if (ue.action == UpdateEntry.REMOVE_HOST) {
				del_entry_if_exist(ue.host, ue.smtp_port, ue.pop_port, ue.group, ue.group_port);
			}
		}
	}

	public void set_params(String host, int port, Registry_Communication_Manager manager) {
		this.manager = manager;
		this.host = host;
		this.port = port;

		this.param_host = this.manager.param_host;
		this.param_pop_port = this.manager.param_pop_port;
		this.param_smtp_port = this.manager.param_smtp_port;
		this.param_group = this.manager.param_group;
		this.param_group_port = this.manager.param_group_port;		
	}

	void reset_timestamp() {
		this.manager.update_timestamp = -1;
	}

	public static void main(String[] args) {
	}

	@Override
	public void run() {
		work();
	}
}

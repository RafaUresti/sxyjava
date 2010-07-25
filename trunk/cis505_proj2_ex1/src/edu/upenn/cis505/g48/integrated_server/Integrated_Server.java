package edu.upenn.cis505.g48.integrated_server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import edu.upenn.cis505.g48.cluster_internal_communication.Cluster_Internal_Listening_Server;
import edu.upenn.cis505.g48.emailTotalOrdering.EmailSyncManager;
import edu.upenn.cis505.g48.model.Email;
import edu.upenn.cis505.g48.model.ServerData;
import edu.upenn.cis505.g48.pop3.Pop3Server;
import edu.upenn.cis505.g48.smtp.SMTP_Server;
import edu.upenn.cis505.g48.smtp.registry_communication.Get_Alive_Gate_Server_List;
import edu.upenn.cis505.g48.smtp.registry_communication.Registry_Communication_Manager;
import edu.upenn.cis505.g48.smtp.registry_communication.Registry_Connector;
import edu.upenn.cis505.g48.smtp.reserve_domain.Reserve_Domain_Manager;
import edu.upenn.cis505.g48.sync_msg.Sync_Worker;
import edu.upenn.cis505.registry.UpdateEntry;

public class Integrated_Server {
	// TODO this reference is always the ONLY inte server
	// given val in constructor
	public static Integrated_Server global_inte_server = null;

	public SMTP_Server smtp_server = null;
	public Pop3Server pop3_server = null;

	public Cluster_Internal_Listening_Server cluster_server = null;
	public Registry_Communication_Manager registry_commu_manager = null;
	public Reserve_Domain_Manager resd_manager = null;
	public EmailSyncManager emailSyncManager;
	public ServerData server_data = null;

	public Sync_Worker sync_manager = null;
	public Thread sync_manager_thread = null;

	public String cluster_name = "";
	public String local_host = "";
	public int smtp_port = -1;
	public int pop3_port = -1;
	public int cluster_port = -1;

	public String reg_host = "";
	public int reg_port = -1;

	public boolean retrive_data_done = false;
	public boolean register_self_done = false;

	public Integrated_Server(String cluster_name, String reg_host, int reg_port) throws UnknownHostException {
		this.cluster_name = cluster_name;
		this.reg_host = reg_host;
		this.reg_port = reg_port;
		
		Integrated_Server.global_inte_server = this;
		InetAddress local = InetAddress.getLocalHost();
		local_host = local.getHostAddress();
		server_data = new ServerData();
	}
	
	public void set_ports(int a, int b, int c){
		this.pop3_port = a;
		this.smtp_port = b;
		this.cluster_port = c;
	}
	
	public void start_server() throws UnknownHostException {
		try {
			emailSyncManager = new EmailSyncManager(global_inte_server);
		} catch (UnknownHostException e) {
			System.out.println("Failed to obtain local IP. Server aborted");
			return;
		}

		// cluster listening server
		if (cluster_port > 0){
			cluster_server = new Cluster_Internal_Listening_Server(cluster_port, global_inte_server);
		} else {
			try {
				ServerSocket serv_sock = new ServerSocket(0);
				cluster_port = serv_sock.getLocalPort();
				cluster_server = new Cluster_Internal_Listening_Server(serv_sock, global_inte_server);
			} catch (IOException e) {
				System.out.println("Failed to creater cluster server socket");
				e.printStackTrace();
			}
		}
		if (pop3_port > 0){
			this.pop3_server = new Pop3Server(pop3_port);
		} else {
			try {
				ServerSocket serv_sock = new ServerSocket(0);
				pop3_port = serv_sock.getLocalPort();
				pop3_server = new Pop3Server(serv_sock);
			} catch (IOException e) {
				System.out.println("Failed to creater pop server socket");
				e.printStackTrace();
			}
		}
		
		if (smtp_port > 0){
			this.smtp_server = new SMTP_Server(smtp_port);
		} else {
			try {
				ServerSocket serv_sock = new ServerSocket(0);
				smtp_port = serv_sock.getLocalPort();
				smtp_server = new SMTP_Server(serv_sock);
			} catch (IOException e) {
				System.out.println("Failed to creater smtp server socket");
				e.printStackTrace();
			}
		}
		
		System.out.printf("Ports: smtp: %d, pop3 %d, cluster %d\n",
				this.smtp_port, this.pop3_port, this.cluster_port);
		
		Thread cluster_server_thread = new Thread(cluster_server, "Cluster_Listening");
		cluster_server_thread.start();

		// RESD manager
		resd_manager = new Reserve_Domain_Manager();

		// SMTP
		Thread smtp_server_thread = new Thread(smtp_server, "SMTP_Server");
		smtp_server_thread.start();

		// pop3
		Thread pop3_server_thread = new Thread(pop3_server, "POP3_Server");
		pop3_server_thread.start();

		// registry commu server
		// XXX: my registry server
		registry_commu_manager = new Registry_Communication_Manager(reg_host, reg_port);
		registry_commu_manager.set_server_info(local_host, pop3_port, smtp_port, cluster_name, cluster_port);
		Thread registry_commu_manager_thread = new Thread(registry_commu_manager, "Registry_Communication_Server");
		registry_commu_manager_thread.start();

		// Sync_Msg manager
		sync_manager = new Sync_Worker(this.cluster_port);
		sync_manager_thread = new Thread(sync_manager);
		sync_manager_thread.start();
		
		
		// command line
		get_comm();
	}

	void get_comm() {
		Scanner scan = new Scanner(System.in);
		while (true) {
			// System.err.println("Input GENERAL command");
			String comm = scan.nextLine();

			String tokens[] = comm.split(" ");


			if (tokens[0].equals("alive")) {
				Get_Alive_Gate_Server_List alive_getter = new Get_Alive_Gate_Server_List();
				HashSet <UpdateEntry> alive_set = 
					alive_getter.get_alive_gate_server_list(registry_commu_manager.entry_set);
				alive_getter.show_entry_set(alive_set);
			}

			if (tokens[0].equals("reg")) {
				if (tokens[1].equals("on"))
					Registry_Connector.DEBUG = true;
				if (tokens[1].equals("off"))
					Registry_Connector.DEBUG = false;
			}


			if (tokens[0].equals("synm")) {
				int num = Integer.parseInt(tokens[1]);
				int level = Integer.parseInt(tokens[2]);
				// int period = Integer.parseInt(tokens[2]);
				// int lc = Integer.parseInt(tokens[3]);
				// sync_manager.lc_A = sync_manager.lc_P = lc;
				
				for (int i=0; i < num; i++) {
					byte[] msg = new byte[i+level];
					sync_manager.accept_for_deliver(msg, "rafi", "g481");
				}
			}

			if (tokens[0].equals("dump")) {
				// sync_manager.tool.dump_undeliver_list();
				this.server_data.dump_user_email("upenn", "PA.com");
			}

			if (tokens[0].equals("dell")) {
				this.server_data.userEmail.put("upenn@PA.com", new ArrayList<Email> ());
			}
			
			if (tokens[0].equals("port")) {
				System.out.printf("Ports: smtp: %d, pop3 %d, cluster %d\n",
						this.smtp_port, this.pop3_port, this.cluster_port);
			}
		}
	}

//	void init() {
//		smtp_port = 25;
//		pop3_port = 11024;
//		cluster_port = 40001;
//
//		reg_host = "nems.seas.upenn.edu";
//		reg_port = 4242;
//		server_data.setFileName();
//
//	}
}
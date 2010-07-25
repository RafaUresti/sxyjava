package edu.upenn.cis505.g48.pop3;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.Email;
import edu.upenn.cis505.g48.model.ServerData;
import edu.upenn.cis505.g48.smtp.SMTP_Service_Provider;
import edu.upenn.cis505.registry.UpdateEntry;

public class Pop3 {
	final int buffer_size = 1024;

	private String current_user = "";
	private int user_state = 0; // 0 no such user, 1 at current domain, 2 at other domain

	PrintWriter out;
	OutputStream out_stream;
	InputStream in_stream;
	OutputStream to_out_stream;
	InputStream to_in_stream;
	PrintWriter pw_out;
	Socket to_socket;
	boolean to_connecting = false;


	int pop3_state = 0; // 0 at authorization state, 1 at waiting password state, 2 at transaction state
	String connected_data = "";

	public void init(PrintWriter the_out) {
		out = the_out;
	}

	public int rcv_command(String comm) {
		String get_back = new String("");
		if (( user_state == 2)&& ( pop3_state == 2)) {		//relay and logged in
			if ((comm.toUpperCase().startsWith("LIST"))||(comm.toUpperCase().startsWith("UIDL"))||
					(comm.toUpperCase().startsWith("TOP"))||(comm.toUpperCase().startsWith("RETR"))) {
				send(comm);
//				while (!((get_back.endsWith(".\r"))||((get_back.endsWith(".\r\n")) {
				while (!((get_back.startsWith(".\r"))||(get_back.contains("\n.\r")))){
					get_back = recv();
					System.out.println("relay get: " + get_back);
					out.printf(get_back);	
				}
				return 0;
			}
			send(comm);
			get_back = recv();
			System.out.println("relay get: " + get_back);
			out.println(get_back);
			if (comm.toUpperCase().startsWith("QUIT")) {
				return command_QUIT();
			}	
			
			return 0;
		}
		if (comm.toUpperCase().startsWith("USER")) {
			command_USER(comm);
		}
		else if (comm.toUpperCase().startsWith("PASS")) {
			command_PASS(comm);
		}		
		else if (comm.toUpperCase().startsWith("NOOP")) {
			command_NOOP();
		} 
		else if (comm.toUpperCase().startsWith("STAT")) {
			command_STAT();
		} 
		else if (comm.toUpperCase().startsWith("LIST")) {
			command_LIST( comm);
		} 
		else if (comm.toUpperCase().startsWith("RETR")) {
			command_RETR( comm );
		} 
		else if (comm.toUpperCase().startsWith("DELE")) {
			command_DELE( comm );
		} 
		else if (comm.toUpperCase().startsWith("RSET")) {
			command_RSET();
		} 
		else if (comm.toUpperCase().startsWith("QUIT")) {
			return command_QUIT();
		}	
		else if (comm.toUpperCase().startsWith("TOP")) {
			command_TOP(comm);
		}	
		else if (comm.toUpperCase().startsWith("UIDL")) {
			command_UIDL(comm);
		}		
		else {
			out.println("-ERR invalid command");
			System.out.println("-ERR invalid command");			
		}
		
		return 0;
	}

	public void command_USER( String comm) {
		try {
			String token[] = comm.split("[' ']+");
			String input_name = token[1];
			
			if (Integrated_Server.global_inte_server.server_data.has_ID(input_name)) {
				user_state = 1;
			}
			// the user name is in current domain
			if (user_state == 1) {
				out.println("+OK valid username");
				System.out.println("+OK valid username---- local user");
				pop3_state = 1;
				current_user = input_name;
			} else {
				System.out.println("Trying to check if relay");
				String tokens2[] = input_name.split("@");
				String domain = new String();
				try {
					domain = tokens2[1];
				}
				catch (Exception e) {
					System.err.println("no @");
				}
				SMTP_Service_Provider smtp_service = new SMTP_Service_Provider();
				String response = smtp_service.command_DVRF("DVRF "+ domain);
				System.out.println("DVery "+ domain + " and get result: "+ response);
				
				
				String res_tokens[] = response.split(" ");
				if (res_tokens[0].equals("251")) {
					String cluster_name = res_tokens[1];
					System.out.println("Cluster return "+ cluster_name);
					InetSocketAddress to_socket_add = 
						Integrated_Server.global_inte_server.registry_commu_manager.
						get_pop3_addr_for_cluster(cluster_name);
					
					to_socket = new Socket();
					to_socket.setSoTimeout(Common_Settings.socket_read_timeout);
					try {
						to_socket.connect(to_socket_add, Common_Settings.socket_connect_timeout);
					}
					catch ( IOException o) {
						System.out.println("Relay server cannot connect");
						return ;
					}
					to_connecting = true;

					to_out_stream = to_socket.getOutputStream();
					to_in_stream = to_socket.getInputStream();
					pw_out = new PrintWriter(to_out_stream);
					String check_msg = recv();
					System.out.println("Relay: recv "+ check_msg + "end of relay reply.");
					send(comm);
					System.out.println("Relay: send "+ comm);
					check_msg = recv();
					System.out.println("Relay: recv "+ check_msg + "end of relay reply.");
					String check_tok[] = check_msg.split("\r\n");
					String real_return = check_tok[check_tok.length-1];
					System.out.println("Real return: "+ real_return);
					if ( real_return.startsWith("+OK")) {
						out.println("+OK valid username");
						System.out.println("+OK valid username");
						pop3_state = 1;
						current_user = input_name;
						user_state = 2;
					}
				}							
				// check other domain
				//user_state = 2;
			}		
			if (user_state == 0) {
				out.println("-ERR no such user");
				System.out.println("-ERR no such user");
			}
		}
		catch ( Exception o) {
			System.out.println("Socket timeout error");
			o.printStackTrace();
		}
	}
	public void command_PASS( String comm) {
		try {
			if ( pop3_state != 1 ) {
				System.out.println("-ERR you are not supposed to enter password here");
				out.println("-ERR you are not supposed to enter password here");
				return;
			}
			if (user_state == 2) { 		// if it's relayed
				send(comm);
				String check_pw = recv();
				if (check_pw.startsWith("+OK")) {
					System.out.println(check_pw);
					out.println(check_pw);
					pop3_state = 2;		
					return;
				}
				else {
					pop3_state = 0;
					user_state = 0;
					current_user = "";
					System.out.println(check_pw);
					out.println(check_pw);					
					return ;
				}
			}					// if it's on the local server
			String token[] = comm.split("[' ']+", 2);
			String input_pass = token[1];
			String real_pass = Integrated_Server.global_inte_server.server_data.get_psw_for_user_at_domain(current_user);
			if ( input_pass.equals(real_pass)) {
				System.out.println("+OK you are authorized");
				out.println("+OK you are authorized");
				pop3_state = 2;			
			}
			else {
				pop3_state = 0;
				user_state = 0;
				current_user = "";
				System.out.println("-ERR invalid password");
				out.println("-ERR invalid password");
			}
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
	}	
	public void command_NOOP() {
		try {
			if (pop3_state ==2 ){
				System.out.println("+OK noob");
				out.println("+OK noob");}
			else {
				System.out.println("-ERR not authorized yet");
				out.println("-ERR not authorized yet");}
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
	}
	public void command_STAT() {
		try {
			if ( pop3_state != 2 ) {
				System.out.println("-ERR not authorized yet");
				out.println("-ERR not authorized yet");
				return;
			}		                           
			ArrayList <Email> emails = (ArrayList<Email>) Integrated_Server.global_inte_server.server_data.get_email_list_from_user_at_domain(current_user);
			if (emails == null) {
				System.out.println("+OK "+ 0 + " " + 0);
				out.println("+OK "+ 0 + " " + 0);			
				return ;
			}
			int num = 0;
			int space = 0;
			for  (Email e_tmp : emails) {
				if (!e_tmp.if_marked()) {
					num++;
					space += e_tmp.getData().length;
				}
			}
			System.out.println("+OK "+ num + " " + space);
			out.println("+OK "+ num + " " + space);
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
	}
	public void command_LIST(String comm) {
		try {
			if ( pop3_state != 2 ) {
				System.out.println("-ERR not authorized yet");
				out.println("-ERR not authorized yet");
				return;
			}                  
			ArrayList <Email> emails = (ArrayList<Email>) Integrated_Server.global_inte_server.server_data.get_email_list_from_user_at_domain(current_user);
			String token[] = comm.split("[' ']+");
			if (token.length == 1 ) { // list all the emails
				int num = 0;
				int space = 0;
				for  (Email e_tmp : emails) {
					if (!e_tmp.if_marked()) {
						num++;
						space += e_tmp.getData().length;
					}
				}
				System.out.println("+OK "+ num + " messages (" + space + " octets)");
				out.println("+OK "+ num + " messages (" + space + " octets)");
				int i = 0;
//				if (emails == null) {
//					System.out.println(".");
//					out.println(".");		
//					return ;
//				}
				for  (Email e_tmp : emails) {
					if (!e_tmp.if_marked()) {
						i++;
						System.out.println(i + " " + e_tmp.getData().length);
						out.println(i + " " + e_tmp.getData().length);					
					}
				}
				System.out.println(".");
				out.println(".");
			}
			else {		// list the n-th email information
				boolean exist=false;
				int num_email;
				try {
					num_email = Integer.parseInt(token[1]);
				}
				catch ( Exception e ) {
					System.out.println("Invalid argument.");
					out.println("Invalid argument.");
					return ;
				}
				int num = 0;
				if (emails == null) {
					System.out.println("-ERR no such message");
					out.println("-ERR no such message");
					return ;
				}
				for  (Email e_tmp : emails) {
					if (!e_tmp.if_marked()) {
						num++;
						if (num == num_email ) {
							System.out.println("+OK "+ num_email + " " + e_tmp.getData().length);
							out.println("+OK "+num_email + " " + e_tmp.getData().length);
							exist = true;
							break;
						}
					}
				}
				if (!exist) {
					System.out.println("-ERR no such message");
					out.println("-ERR no such message");
				}
			}		
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
	}
	public void command_RETR(String comm) {
		try {
			if ( pop3_state != 2 ) {
				System.out.println("-ERR not authorized yet");
				out.println("-ERR not authorized yet");
				return;
			}                  
			ArrayList <Email> emails = (ArrayList<Email>) Integrated_Server.global_inte_server.server_data.
				get_email_list_from_user_at_domain(current_user);
			String token[] = comm.split("[' ']+");
			// retr the n-th email information
			boolean exist=false;
			int num_email;
			try {
				num_email = Integer.parseInt(token[1]);
			}
			catch ( Exception e ) {
				System.out.println("Invalid argument.");
				out.println("Invalid argument.");
				return ;
			}
			int num = 0;
			if (emails == null) {
				System.out.println("-ERR no such message");
				out.println("-ERR no such message");
				return ;
			}
			for  (Email e_tmp : emails) {	
				if (!e_tmp.if_marked()) {
					num++;
					if (num == num_email ) {
						System.out.println("+OK " + e_tmp.getData().length + " octets");
						out.println("+OK " + e_tmp.getData().length + " octets");
						int i;
						String lines[] = new String(e_tmp.getData()).split("[\r|\n]+");
						for ( i = 0; i < lines.length; i++) {
							if (lines[i].startsWith(".")) {
								lines[i] = "."+lines[i];
							}
							out.println(lines[i]);
							System.out.println(""+ lines[i]);
						}
						System.out.println(".");
						out.println(".");
						exist = true;
						break;
					}
				}
			}
			if (!exist) {
				System.out.println("-ERR no such message");
				out.println("-ERR no such message");
			}
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
	}
	public void command_DELE(String comm) {
		try {
			if ( pop3_state != 2 ) {
				System.out.println("-ERR not authorized yet");
				out.println("-ERR not authorized yet");
				return;
			}                  
			ArrayList <Email> emails = (ArrayList<Email>) Integrated_Server.global_inte_server.server_data.
				get_email_list_from_user_at_domain(current_user);
			String token[] = comm.split("[' ']+");
			// retr the n-th email information
			boolean exist=false;
			int num_email;
			try {
				num_email = Integer.parseInt(token[1]);
			}
			catch ( Exception e ) {
				System.out.println("Invalid argument.");
				out.println("Invalid argument.");
				return ;
			}
			int num = 0;
			if (emails == null) {
				System.out.println("-ERR no such message");
				out.println("-ERR no such message");
				return ;
			}
			for  (Email e_tmp : emails) {
				if (!e_tmp.if_marked()) {
					num++;
					if (num == num_email ) {
						Integrated_Server.global_inte_server.server_data.
							mark_email_by_ID(current_user, e_tmp.hashCode());
						exist = true;
						System.out.println("+OK message " + num+ " deleted");
						out.println("+OK message " + num+ " deleted");
						break;
					}
				}
			}
			if (!exist) {
				System.out.println("-ERR no such message");
				out.println("-ERR no such message");
			}	
		}
		catch ( Exception e) {
			e.printStackTrace();			
		}
	}
	public void command_RSET() {
		try {
			if ( pop3_state != 2 ) {
				System.out.println("-ERR not authorized yet");
				out.println("-ERR not authorized yet");
				return;
			}        
			Integrated_Server.global_inte_server.server_data.reset(current_user);
			System.out.println("+OK");
			out.println("+OK");
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
	}
	public int command_QUIT() {
		try {
			if ( to_connecting ) { 
				try {
					send("QUIT");
					to_socket.close();
					to_connecting = false;
				}
				catch ( Exception e) {
					e.printStackTrace();
				}
			}
			if ( pop3_state != 2 ) {
				System.out.println("+OK user quit");
				out.println("+OK user quit");
				return -1;
			}
			ArrayList<Email> deletedEmails = Integrated_Server.
				global_inte_server.server_data.update(current_user);
			System.out.println("+OK user quit");
			out.println("+OK user quit");
			
//			if (deletedEmails==null || deletedEmails.size() == 0) {
//				return -1;
//			}
			
			Integrated_Server int_serv = Integrated_Server.global_inte_server;
			HashSet<UpdateEntry> clusterServerEntries = 
				int_serv.registry_commu_manager.get_alive_my_cluster_server();
			String localHost = int_serv.local_host;
			int clusterPort = int_serv.cluster_port;
			
			ArrayList<GroupEmailDeletionHandler> emailDeletionThreads = 
				new ArrayList<GroupEmailDeletionHandler>();
			
			for (UpdateEntry entry : clusterServerEntries){
				if(entry.host.equals(localHost) && entry.group_port == clusterPort){
					continue;
				}
				
				GroupEmailDeletionHandler deletionThread = 
					new GroupEmailDeletionHandler(deletedEmails, current_user, entry.host, entry.group_port);
				
				emailDeletionThreads.add(deletionThread);
				// deletionThread.start();
				deletionThread.run();
			}
			
//			for (GroupEmailDeletionHandler deletionHandler: emailDeletionThreads){
//				deletionHandler.join();
//			}
			
			System.out.println("Email Deletion Synced in cluster");
			return -1;
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		return -1;
	}	
	public void command_TOP(String comm){
		try {
			if ( pop3_state != 2 ) {
				System.out.println("-ERR not authorized yet");
				out.println("-ERR not authorized yet");
				return;
			}                  
			ArrayList <Email> emails = (ArrayList<Email>) Integrated_Server.global_inte_server.server_data.
				get_email_list_from_user_at_domain(current_user);
			String token[] = comm.split("[' ']+");
			boolean exist = false;
			// retr the n-th email information
			int num_line;
			int num_email;
			try {
				num_email = Integer.parseInt(token[1]);
				num_line = Integer.parseInt(token[2]);
			}
			catch ( Exception e ) {
				System.out.println("Invalid argument.");
				out.println("Invalid argument.");
				return ;
			}
			int num = 0;
			if (emails == null) {
				System.out.println("-ERR no such message");
				out.println("-ERR no such message");
				return ;
			}
			for  (Email e_tmp : emails) {
				if (!e_tmp.if_marked()) {
					num++;
					if (num == num_email ) {
						System.out.println("+OK");
						out.println("+OK");
						exist = true;
						String lines[] = new String(e_tmp.getData()).split("\r\n");
						int total_i = 0;
						while (true) {
							if ( total_i >= lines.length ) break;
							if (lines[total_i].startsWith(".")) {
								lines[total_i] = "."+lines[total_i];
							}
							System.out.println(lines[total_i]);
							out.println(lines[total_i]);
							if (lines[total_i].length() == 0) {
								total_i++;
								for (int i = 0; i < num_line; i++){
									if ( total_i >= lines.length ) break;
									if (lines[total_i].startsWith(".")) {
										lines[total_i] = "."+lines[total_i];
									}
									System.out.println(lines[total_i]);
									out.println(lines[total_i]);
									total_i++;
								}
								break;
							}
							total_i++;
						}
						System.out.println(".");
						out.println(".");
						break;
					}
				}
			}
			if (!exist) {
				System.out.println("-ERR no such message");
				out.println("-ERR no such message");
			}	
		}
		catch ( Exception e) {
			e.printStackTrace();
		}	
	}
	public void command_UIDL(String comm) {
		try {
			if ( pop3_state != 2 ) {
				System.out.println("-ERR not authorized yet");
				out.println("-ERR not authorized yet");
				return;
			}                  
			ArrayList <Email> emails = (ArrayList<Email>) Integrated_Server.global_inte_server.server_data.
				get_email_list_from_user_at_domain(current_user);
			String token[] = comm.split("[' ']+");
			if (token.length == 1 ) { // list all the emails			
				System.out.println("+OK");
				out.println("+OK");
				int i = 0;
				if (emails == null) {
					System.out.println(".");
					out.println(".");
					return ;
				}
				for  (Email e_tmp : emails) {
					if (!e_tmp.if_marked()) {
						i++;
						String a = new String(e_tmp.getData());
						System.out.println(i + " " + a.hashCode());
						out.println(i + " " + a.hashCode());					
					}
				}
				System.out.println(".");
				out.println(".");
			}
			else {		// list the n-th email information
				boolean exist=false;
				int num_email;
				try {
					num_email = Integer.parseInt(token[1]);
				}
				catch ( Exception e ) {
					System.out.println("Invalid argument.");
					out.println("Invalid argument.");
					return ;
				}
				int num = 0;
				if (emails == null) {
					System.out.println("-ERR no such message");
					out.println("-ERR no such message");
					return ;
				}
				for  (Email e_tmp : emails) {
					if (!e_tmp.if_marked()) {
						num++;
						if (num == num_email ) {
							String a = new String(e_tmp.getData());
							System.out.println("+OK "+ num_email + " " + a.hashCode());
							out.println("+OK "+num_email + " " + a.hashCode());
							exist = true;
							break;
						}
					}
				}
				if (!exist) {
					System.out.println("-ERR no such message");
					out.println("-ERR no such message");
				}
			}		
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String comm) {
		pw_out.printf("%s\r\n", comm);
		pw_out.flush();
	}

	public String recv() {
		byte[] buf = new byte[buffer_size];
		String rtn = null;
		try {
			int len = to_in_stream.read(buf);
			if (len != -1)
				rtn = new String(buf, 0, len);
		} catch (IOException e) {
			// remote host response error
			System.out.println("Relay server cannot connect");
			rtn = null;
			// System.out.printf("no reply from remote host.\n");
			// e.printStackTrace();
		}
		return rtn;
	}

}
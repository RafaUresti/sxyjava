package edu.upenn.cis505.g48.smtp;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.ServerData;
import edu.upenn.cis505.g48.smtp.handle_request.Handle_Delay_Email;
import edu.upenn.cis505.g48.smtp.handle_request.Handle_NEWU;
import edu.upenn.cis505.g48.smtp.handle_request.Handle_RESD;
import edu.upenn.cis505.g48.smtp.poll_other_cluster.Poll_Other_Cluster;
import edu.upenn.cis505.registry.UpdateEntry;

public class SMTP_Service_Provider {
	boolean DEBUG = true;
	boolean OUT_LOG = true;

	SMTP_Server super_server;
	SMTP_Tools tools = new SMTP_Tools();

	String mailFrom = "";
	String mail_from_user;
	String mail_from_domain;
	String rcptTo = "NONE";
	String rcpt_to_user;
	String rcpt_to_domain;

	PrintWriter out_print_writer;

	boolean is_in_DATA = false;
	String connected_data = "";

	Socket sock;

	public void init(PrintWriter the_out, SMTP_Server upper_server) {
		super_server = upper_server;
		out_print_writer = the_out;
	}

	/**
	 * the SMTP command is given, only need to flush response
	 * to PrintWriter out, which is given in init()
	 * */
	public int rcv_command(String comm) {
		if (is_in_DATA) {
			command_DATA(comm);
			if (!is_in_DATA) {
				//				 if (OUT_LOG)
				//				 	System.out.println("250 Message accepted for delivery");

				out_print_writer.println("250 Message accepted for delivery");
				command_DATA_after(connected_data.getBytes());
				connected_data = "";
			}
		} else {
			if (comm.toUpperCase().startsWith("HELO")) {			
				command_HELO();
				return 0;
			}

			if (comm.toUpperCase().startsWith("EHLO")) {
				command_EHLO();
				return 0;
			} 

			if (comm.toUpperCase().startsWith("MAIL")) {
				command_MAIL(tools.get_addr(comm));
				return 0;
			}

			if (comm.toUpperCase().startsWith("RCPT")) {
				command_RCPT(tools.get_addr(comm));
				return 0;
			}

			if (comm.toUpperCase().startsWith("DATA") && !is_in_DATA) {
				command_DATA(comm);
				return 0;
			}

			if (comm.toUpperCase().startsWith("NOOP")) {
				command_NOOP();
				return 0;
			}

			if (comm.toUpperCase().startsWith("VRFY")) {
				comm = comm.replace('\r', ' ');
				comm = comm.replace('\n', ' ');
				String response = command_VRFY(comm);
				out_print_writer.println(response);

				if (DEBUG)
					System.out.printf(">> response: %s\n", response);
				return 0;
			}

			if (comm.toUpperCase().startsWith("QUIT")) {
				return command_QUIT();
				
			}

			// extended SMTP commands
			if (comm.toUpperCase().startsWith("LVRF")) {
				comm = comm.replace('\r', ' ');
				comm = comm.replace('\n', ' ');

				String response = command_LVRF(comm);
				out_print_writer.println(response);

				if (DEBUG)
					System.out.printf(">> response: %s\n", response);
				return 0;
			}

			if (comm.toUpperCase().startsWith("DVRF")) {
				comm = comm.replace('\r', ' ');
				comm = comm.replace('\n', ' ');

				String response = command_DVRF(comm);
				out_print_writer.println(response);

				if (DEBUG)
					System.out.printf(">> response: %s\n", response);
				return 0;
			}

			if (comm.toUpperCase().startsWith("NEWU")) {
				String tokens[] = comm.split(" ");

				if (tokens.length < 3) {
					out_print_writer.println("599 PARAM ERROR");
				} else {
					
					String tokens2[] = tokens[1].split("@");
					if (tokens2.length < 2)
						out_print_writer.println("599 PARAM ERROR");
					else {
						Handle_NEWU handle_newu = new Handle_NEWU(this);

						String resp = handle_newu.handle(tokens2[0], tokens2[1], 
								tokens[2].replaceAll("[\r|\n]", ""));

						out_print_writer.println(resp);
						if (DEBUG)
							System.out.printf(">> response: %s\n", resp);
					}
				}
				return 0;
			}

			if (comm.toUpperCase().startsWith("RESD")) {
				new Handle_RESD().handle(comm, out_print_writer, Integrated_Server.global_inte_server);
				return 0;
			}
			
			// unimplemented command
			out_print_writer.println("502 UNimplement by g48");
		}
		out_print_writer.flush();
		return 0;
	}

	public void command_HELO() {
		out_print_writer.println("250 HELO you too!");
	}

	public void command_EHLO() {
		out_print_writer.println("250 EHLO you too!");
	}

	public void command_MAIL(String addr) {
		if (addr == null) {
			out_print_writer.println("599 PARAM ERROR");
			return;
		}

		mailFrom = addr;
		String tokens[] = mailFrom.split("@");
		this.mail_from_user = tokens[0];
		if (tokens.length < 2)
			this.mail_from_domain = "";
		else
			this.mail_from_domain = tokens[1];

		String response = String.format("250 MAIL OK: user: %s, domain: %s", mail_from_user, mail_from_domain);
		out_print_writer.println(response);
	}

	public void command_RCPT(String addr) {
		if (addr == null) {
			out_print_writer.println("599 PARAM ERROR");
			return;
		}

		rcptTo = addr;
		String tokens[] = rcptTo.split("@");
		if (tokens.length < 2)
			return;

		this.rcpt_to_user = tokens[0];
		this.rcpt_to_domain = tokens[1];

		String response = String.format("250 RCPT OK: user: %s, domain: %s", rcpt_to_user, rcpt_to_domain);
		out_print_writer.println(response);
	}

	public void command_DATA(String comm) {
		if (!is_in_DATA) {
			is_in_DATA = true;
			out_print_writer.println("354 please input email body");
		} else if (comm.equals(".\r")
				|| comm.equals(".\n")
				|| comm.equals("\n.\n")
				|| comm.equals(".\r\n")
				|| comm.contains("\r\n.\r\n")) {
			is_in_DATA = false;
			// connected_data = connected_data + comm;
		} else
			connected_data = connected_data + comm;
	}


	public void command_DATA_after(byte[] data) {
		// XXX: has big change, check if works in further
		boolean for_this = Integrated_Server.global_inte_server.server_data.
		has_user_at_domain(rcpt_to_user, rcpt_to_domain);

		// the data's target domain is handled here
		if (for_this == true) {

			//			// add header
			//			String str1 = sock.getInetAddress().getHostName();
			//			String str2 = sock.getLocalAddress().getHostName();
			//
			//			String header = tools.get_header(str1, str2, this.mailFrom, this.rcptTo, tools.get_std_time());
			//			byte[] b_header = header.getBytes();
			//			byte[] n_data = new byte[b_header.length + data.length];
			//			for (int i=0; i<b_header.length; i++)
			//				n_data[i] = b_header[i];
			//			for (int i=b_header.length; i<b_header.length + data.length; i++)
			//				n_data[i] = data[i - b_header.length];
			// XXX: changed adding header func, check later if works

			byte[] n_data = tools.put_header_to_email_bytes(sock, mailFrom, rcptTo, data);
			// make msg
			// String tstr = new String(n_data, 0, n_data.length);

			/** XXX: Add a new email to local storage 
			 * The only add email operation at the server side
			 * Synchronized operation from ServerData interface
			 * */

//			 old one, not synchronized
//						Integrated_Server.global_inte_server.server_data.
//							add_email_to_user_at_domain(n_data, this.rcpt_to_user, this.rcpt_to_domain);

//			Integrated_Server.global_inte_server.emailSyncManager.bufferSmtpEmail
//				(n_data, this.rcpt_to_user, this.rcpt_to_domain);

			Integrated_Server.global_inte_server.sync_manager.accept_for_deliver(n_data, 
					this.rcpt_to_user, this.rcpt_to_domain);
			
			if (DEBUG) {
				System.out.printf(String.format("%s@%s: email given total ordering manager\n", 
						rcpt_to_user, rcpt_to_domain));

				// TODO: display info, need to implement if further needed
				// tools.display_info(server_data, rcpt_to_user, rcpt_to_domain);
			}
		} else {
			//			// add header
			//			String str1 = sock.getInetAddress().getHostName();
			//			String str2 = sock.getLocalAddress().getHostName();
			//
			//			String header = tools.get_header(str1, str2, this.mailFrom, this.rcptTo, tools.get_std_time());
			//			byte[] b_header = header.getBytes();
			//			byte[] n_data = new byte[b_header.length + data.length];
			//			for (int i=0; i<b_header.length; i++)
			//				n_data[i] = b_header[i];
			//			for (int i=b_header.length; i<b_header.length + data.length; i++)
			//				n_data[i] = data[i - b_header.length];
			// XXX: changed adding header func, check later if works

			byte[] n_data = tools.put_header_to_email_bytes(sock, mailFrom, rcptTo, data);
			// make msg
			String msg = new String(n_data, 0, n_data.length);

			// TODO: need to delay this msg to other cluster who holds the domain and user
			Handle_Delay_Email delay_email = new Handle_Delay_Email();
			delay_email.set_params(mailFrom, rcptTo, msg);
			boolean suc = delay_email.work();
			if (DEBUG)
				if (suc)
					System.out.printf("** Success send email\n");
				else
					System.out.printf("** Fail send email\n");

			/*
			SMTP_Message_Sender srm = new SMTP_Message_Sender();
			msg = msg.replace("From: \"fym\" <my@localhost>", "From: \"Gates Bill\" <bill.gates@microsoft.com>");

			srm.set_params("smtp.163.com", 25, "papaya94@163.com", "nkufym@gmail.com", msg);
			int result = srm.relay_message();
			System.out.println(result);
			 */
		}
	}

	public void command_RSET() {
		rcptTo = "";
		mailFrom = "";
		out_print_writer.println("250 OK, reset done");
	}

	public void command_NOOP() {
		out_print_writer.println("250 OK, but don't always NOOP!");
	}

	public String command_VRFY(String arg) {
		String tokens[] = tools.parse_user_domain_from_comm(arg);
		if (tokens == null) {
			return "509 PARAM ERROR";
		}
		String user = tokens[0];
		String domain = tokens[1];

		String local_cluster_name = Integrated_Server.global_inte_server.cluster_name;

		// check local
		int result = tools.has_user_at_domain(Integrated_Server.global_inte_server.server_data, 
				domain, user);

		if (result == 0) {
			return String.format("550 %s", local_cluster_name);
		}
		else if (result == 1) {
			return String.format("250 %s", local_cluster_name);
		}
		else if (result == -1) {
			// check other groups
			Poll_Other_Cluster poc = new Poll_Other_Cluster();

			HashSet <UpdateEntry> other_cluster_gate_set = 
				Integrated_Server.global_inte_server.registry_commu_manager.get_other_cluster_gate_server();

			poc.poll_user_domain_in_cluster_set(other_cluster_gate_set, domain, user);

			//results
			if (poc.poll_result == 11) {
				return String.format("250 %s", poc.poll_cluster_name_result);
			}
			else if (poc.poll_result == 10) {
				return String.format("550 %s", poc.poll_cluster_name_result);
			}
			else if (poc.poll_result == 00) {
				return "551 FAILURE, no user, no domain";
			}
			else if (poc.poll_result == -1 || poc.poll_result == -2) {
				return "554 FAILURE, registry server error";
			}
		}
		return "551";
	}

	public int command_QUIT() {
		return -1;
	}

	// extended SMTP commands
	public String command_LVRF(String arg) {
		String tokens[] = tools.parse_user_domain_from_comm(arg);
		if (tokens == null)
			return "509 PARAM ERROR";

		String user = tokens[0];
		String domain = tokens[1];

		int result = tools.has_user_at_domain(Integrated_Server.global_inte_server.server_data, 
				domain, user);
		if (result == -1)
			return "551 FAILURE: domain not local here";
		if (result == 0)
			return "550 FAILURE: I have the domain but no user";
		if (result == 1)
			return "250 SUCCESS";

		return "554";
	}

	public String command_DVRF(String arg) {
		String tokens1[] = arg.split("[' ']+");
		if (tokens1.length != 2) {
			return "599 PARAM ERROR";
		}

		String domain = tokens1[1];

		if (Integrated_Server.global_inte_server.server_data.has_domain(domain)) {
			return "250 local domain";
		}

		Poll_Other_Cluster poc = new Poll_Other_Cluster();

		HashSet <UpdateEntry> other_cluster_gate_set = 
			Integrated_Server.global_inte_server.registry_commu_manager.get_other_cluster_gate_server();

		poc.poll_user_domain_in_cluster_set(other_cluster_gate_set, domain, "");

		if (poc.poll_result == 11 || poc.poll_result == 10) {
			String rtn = String.format("251 %s", poc.poll_cluster_name_result);
			return rtn;
		}
		else if (poc.poll_result == 00) {
			return "550 no domain";
		}
		else if (poc.poll_result == -1 || poc.poll_result == -2){
			return "554 registry server error";
		}
		return "554";
	}
}

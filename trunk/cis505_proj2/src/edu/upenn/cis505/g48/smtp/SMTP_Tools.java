package edu.upenn.cis505.g48.smtp;

import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.upenn.cis505.g48.model.ServerData;

public class SMTP_Tools {
	public void disp_server_data(ServerData data) {
		System.out.printf("** Disp server data:\n");
		Set<String> domain_set = data.domainMap.keySet();
		
		for (String domain: domain_set) {
			System.out.printf("** domain [%s]: ", domain);
			HashSet <String> user_set = data.domainMap.get(domain);
			for (String user : user_set) {
				System.out.printf("[%s] ", user);
			}
			System.out.println();
		}
	}
	
	public String get_std_time() {
		//Wed Apr 08 00:36:15 EDT 2009
		//Tue, 7 Apr 2009 21:07:17 -0700 (PDT)
		Date date = new Date();
		String d = date.toString();
		String t[] = d.split(" ");
		String rtn = String.format("%s, %s %s %s %s -0400 (%s)", 
				t[0], t[2], t[1], t[5], t[3], t[4]);
		return rtn;
	}
	
	public byte[] put_header_to_email_bytes(Socket sock, String from, String to, byte[] data) {
		String str1 = sock.getInetAddress().getHostName();
		String str2 = sock.getLocalAddress().getHostName();

		String header = get_header(str1, str2, from, to, get_std_time());
		byte[] b_header = header.getBytes();
		byte[] n_data = new byte[b_header.length + data.length];
		for (int i=0; i<b_header.length; i++)
			n_data[i] = b_header[i];
		for (int i=b_header.length; i<b_header.length + data.length; i++)
			n_data[i] = data[i - b_header.length];
		
		return n_data;
	}
	
	public String get_header(String from_ip, String my_ip, 
			String from_addr, String to_addr, String cur_time) {
		String rtn = "";
		
		String delivered_to = String.format("Delivered-To: %s\r\n", to_addr);
		String return_path = String.format("Return-Path: <%s>\r\n", from_addr);
		String received = String.format("Received: from %s\r\n        by %s;\r\n        %s\r\n",
				from_ip, my_ip, cur_time);
		String date = String.format("Date: %s\r\n", cur_time);
		
		// XXX need to clarify header format:
		rtn = delivered_to + received + return_path + date + "\r\n";
		// rtn = delivered_to + received + return_path + date; 
		
		return rtn;
	}
	
	/**
	 * display users and emails
	 * */
//	public void display_info(ServerData server_data, String user, String domain) {
//		ArrayList<Email> user_email = 
//			server_data.getUserEmail().get(user + "@" + domain);
//		
//		System.out.printf("emails for %s@%s: %d\n", 
//				user, domain, user_email.size());
//	}
	/**
	 * if server_data has the domain
	 * */
//	public boolean has_domain(ServerData server_data, String domain) {
//		Set <String> domain_set = server_data.getDomainMap().keySet();
//		if (domain_set.contains(domain))
//			return true;
//		else
//			return false;
//	}

	/**
	 * call this only when known that domain existed
	 * */
//	public boolean has_user(ServerData server_data, String domain, String user) {
//		HashMap<String, HashSet<String>> domain_map = server_data.getDomainMap();
//		HashSet<String> user_set = domain_map.get(domain);
//		if (user_set != null) {
//			if (user_set.contains(user)) return true;
//			else return false;
//		}
//		else return false;
//	}
	
	/**
	 * -1: no user, no domain
	 *  0: no user, has domain
	 *  1: has user, has domain
	 * */
	public int has_user_at_domain(ServerData server_data, String domain, String user) {
		if (! server_data.has_domain(domain))
			return -1;
		else
			if (! server_data.has_user_at_domain(user, domain))
				return 0;
			else
				return 1;
	}
	
	/**
	 * parse the user and domain from "COMM user@domain"
	 * return String[2] for user and domain,
	 * null for any format un-match
	 * */
	public String[] parse_user_domain_from_comm(String arg) {
		String tokens1[] = arg.split("[' ']+");
		if (tokens1.length != 2)
			return null;
		String tokens2[] = tokens1[1].split("@");
		if (tokens2.length != 2)
			return null;
		return tokens2;
	}

//	public boolean add_email(String domain, String user, byte[] data, ServerData server_data) {
//		ArrayList<Email> email_set = server_data.getUserEmail().get(user + "@" + domain);
//		if (email_set == null)
//			return false;
//		Email an_email = new Email();
//		
//		// copy data to email's obj
//		an_email.data = new byte[data.length];
//		for (int i=0; i<data.length; i++)
//			an_email.data[i] = data[i];
//		
//		// further need to deal with time stamp
//		Timestamp ts = new Timestamp(System.currentTimeMillis());
//		an_email.setTimeStamp(ts);
//		
//		// add the email to storage
//		email_set.add(an_email);
//		return true;
//	}

	public String get_addr(String comm) {
		int i1 = comm.indexOf("<")+1;
		int i2 = comm.indexOf(">");
		if (i1<0 || i2<0) return null;
		else
			return comm.substring(comm.indexOf("<")+1, comm.indexOf(">"));
	}
}

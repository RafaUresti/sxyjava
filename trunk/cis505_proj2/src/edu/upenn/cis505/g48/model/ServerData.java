	package edu.upenn.cis505.g48.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.utility.Utility;


public class ServerData implements Serializable {
	private static final long serialVersionUID = 4242701017139088191L;
	public HashMap <String, HashSet<String>> domainMap = new HashMap <String, HashSet<String>> ();
	public HashMap <String, ArrayList<Email>> userEmail = new HashMap <String, ArrayList<Email>> ();
	public HashMap <String, String> userPass = new HashMap <String, String> (); // user@domain, password
	public boolean persistent = false;

	public String get_filename() {
		String localIp = Integrated_Server.global_inte_server.local_host;
		String fileName = localIp.replaceAll("\\.", "_")+ "_" + Integrated_Server.global_inte_server.cluster_port;
		return fileName;
	}
	
	public synchronized void  forceDataToDisk(){
		if (!persistent) return;
		try {
			String fileName = get_filename();
			Utility.write_to(this, fileName);
			System.out.println("Data forced to disk");
		} catch (IOException e) {
			System.out.println("Failed to store to disk");
		}
	}
	
	public void dump_all_domains() {
		Set <String> set =   domainMap.keySet();
		for (String s: set) {
			System.out.printf("** domain ele: %s\n", s);
		}
	}


	public void dump_user_email(String user, String domain) {
		ArrayList<Email> list = userEmail.get(user + "@" + domain);
		int count = 0;
		for (Email e: list) {
			System.out.printf("[%d]--> %s\n", count++, new String(e.data, 0, e.data.length));
		}
	}

	public void set_domainMap(HashMap <String, HashSet<String>> n_domainMap) {
		domainMap = n_domainMap;
	}
	
	public void set_userEmail(HashMap <String, ArrayList<Email>> n_userEmail) {
		userEmail = n_userEmail;
	}
	
	public void set_userPass(HashMap <String, String> n_userPass) {
		userPass = n_userPass;
	}

	/** server data operation interface */
	public boolean create_domain(String domain_name) {
		synchronized(this) {
			HashSet<String> user_set = domainMap.get(domain_name);
			if (user_set != null)
				return false;
			else {
				user_set = new HashSet<String>();
				domainMap.put(domain_name, user_set);
				forceDataToDisk();
				return true;
			}
		}
	}

	public boolean create_user_to_domain(String user_name, String domain_name, String psw) {
		synchronized(this) {
			HashSet<String> user_set = domainMap.get(domain_name);
			if (user_set == null)
				return false;
			else {
				if (user_set.contains(user_name))
					return false;
				user_set.add(user_name);

				ArrayList <Email> email_set = new ArrayList<Email>();
				this.userEmail.put(user_name + "@" + domain_name, email_set);

				userPass.put(user_name + "@" + domain_name, psw);
				forceDataToDisk();
				return true;
			}
		}
	}

	public boolean create_domain_then_create_user(String user_name, String domain_name, String psw) {
		synchronized(this) {
			HashSet<String> user_set = domainMap.get(domain_name);
			if (user_set != null)
				return false;
			else {
				user_set = new HashSet<String>();
				user_set.add(user_name);				
				domainMap.put(domain_name, user_set);

				ArrayList <Email> email_set = new ArrayList<Email>();
				this.userEmail.put(user_name + "@" + domain_name, email_set);

				userPass.put(user_name + "@" + domain_name, psw);
				
				forceDataToDisk();
				return true;
			}
		}
	}

	public boolean has_domain(String domain_name) {
		synchronized(this) {
			Set <String> domain_set = domainMap.keySet();
			return domain_set.contains(domain_name);
		}
	}

	public boolean has_user_at_domain(String user_name, String domain_name) {
		synchronized(this) {
			HashSet<String> user_set = domainMap.get(domain_name);
			if (user_set == null) {
				return false;
			}
			else {
				return user_set.contains(user_name);
			}
		}
	}

	public boolean has_ID(String user_ID) {
		synchronized(this) {
			Set<String> user_set =  userPass.keySet();
			if (user_set == null) {
				return false;
			}
			else {
				return user_set.contains(user_ID);
			}
		}
	}

	public boolean add_email_to_user_at_domain(byte[] data, String user, String domain) {
		synchronized(this) {
			ArrayList<Email> email_set = userEmail.get(user + "@" + domain);
			if (email_set == null)
				return false;
			else {
				Email email = new Email();
				email.copy_data(data);
				email_set.add(email);
				forceDataToDisk();
				return true;
			}
		}
	}

	public ArrayList<Email> get_email_list_from_user_at_domain(String user_domain) {
		synchronized (this){
			ArrayList<Email> email_set = userEmail.get(user_domain);
			if (email_set == null)
				return null;
			else {
				ArrayList<Email> rtn_set = new ArrayList<Email> ();
				for (Email e: email_set)
					rtn_set.add(e);
				return rtn_set;
			}
		}
	}
	public boolean del_emails(String emailAddress, ArrayList<Email> deletedEmails){
		synchronized (this){
			boolean success = true;
			ArrayList<Email> currentUserEmails = 
				this.userEmail.get(emailAddress);
			for (Email email: deletedEmails){
				if (!currentUserEmails.remove(email))
					success = false;
			}
			
			if (success) {
				forceDataToDisk();
			}
				
			return success;
		}
	}

	public boolean mark_email_by_ID(String userID, int hash_code) {
		synchronized (this){
			ArrayList<Email> email_set = userEmail.get(userID);
			for (Email e : email_set) {
				if (e.hashCode() == hash_code) {
					e.mark_it();
					return true;
				}
			}
			return false;
		}
	}

	public boolean reset(String userID) {
		synchronized (this){
			ArrayList<Email> email_set = userEmail.get(userID);
			for (Email e : email_set) {
				e.unmark_it();
				return true;
			}
		}
		return false;
	}


	public String get_psw_for_user_and_domain(String user, String domain) {
		synchronized (this){
			return userPass.get(user + "@" + domain);
		}
	}

	public String get_psw_for_user_at_domain(String user_domain) {
		synchronized (this){
			return userPass.get(user_domain);
		}
	}
	public ArrayList<Email> update(String userID) {
		synchronized (this){
			ArrayList<Email> email_set = userEmail.get(userID);
			ArrayList<Email> deletedEmails = new ArrayList<Email>();

			if (email_set == null)
				return null;
			else {
				for (Email e : email_set) {
					if (e.if_marked()) {
						// email_set.remove(e);
						deletedEmails.add(e);
					}
				}

				for (Email e: deletedEmails)
					email_set.remove(e);

				return deletedEmails;
			}

		}
	}

	/** END: server data operation interface */

}
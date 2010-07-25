package edu.upenn.cis505.g48.emailTotalOrdering;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.Email;
import edu.upenn.cis505.registry.UpdateEntry;

public class EmailSyncManager {
	//Stores <msg_id, Set of <server_port>>
	TreeSet<EmailMessage> emailBuffer = new TreeSet<EmailMessage>();
	LogicalClock clock;
	String localIp;
	int clusterPort;
	private Integrated_Server int_server;
	public EmailSyncManager(Integrated_Server int_server) throws UnknownHostException{
		this.int_server = int_server;
		clock = new LogicalClock();
		localIp = int_server.local_host;
		clusterPort = int_server.cluster_port;
	}

	/**
	 * Store email received by the smtp server and deliver to all
	 * other cluster servers.
	 * @param email
	 * @param user
	 * @param domain
	 */

	@SuppressWarnings("unchecked")
	public void syncEmailDeletion(ObjectInputStream ois){
		ArrayList<Email> deletedEmails;
		try {
			String emailAddress = ois.readUTF();
			deletedEmails = (ArrayList<Email>)ois.readObject();
			boolean success = int_server.server_data.del_emails(emailAddress, deletedEmails);
			if (success){
				System.out.println("Sync Email" + ": Email deleted");
			} else {
				System.out.println("Sync Email: " + "Failed to delete Email");
			}
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}

	}
}

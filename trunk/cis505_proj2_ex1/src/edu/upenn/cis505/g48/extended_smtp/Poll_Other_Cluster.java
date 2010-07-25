package edu.upenn.cis505.g48.extended_smtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;

import edu.upenn.cis505.registry.UpdateEntry;

public class Poll_Other_Cluster {
	boolean DEBUG = true;

	final int socket_read_timeout = 1000;
	final int socket_connect_timeout = 500;
	final int buffer_size = 1024;
	private Socket socket;
	Object[] serverUpdateEntries;
	Get_Info_From_Registry gifr = new Get_Info_From_Registry();
	OutputStream out_stream;
	InputStream in_stream;
	PrintWriter pw_out;

	/**
	 * 00: no domain, no user
	 * 10: has domain, no user
	 * 11: has domain, has user
	 * -1: registry error
	 * -2: no registry entry
	 * -100: init value
	 * */
	public int poll_result = -100;
	public String poll_cluster_name_result = "";
	HashSet <UpdateEntry> cluster_set = new HashSet <UpdateEntry> (); 

	/** methods begin */
	int get_registry_info() {
		serverUpdateEntries = gifr.get();
		if (serverUpdateEntries == null) return -1;
		else return 1;
	}

	public void poll_user_domain_in_cluster_set(String domain, String user) {
		if (user.equals(""))
			user = "FAKE_USER";

		// init
		poll_result = -100;

		if (cluster_set.size() == 0) {
			poll_result = -2;
			return;
		}
		for (UpdateEntry ue : cluster_set) {
			String remote_smtp_host = ue.host;
			int remote_smtp_port = ue.smtp_port;

			int result = poll_user_domain(domain, user, remote_smtp_host, remote_smtp_port);
			if (result == 250) {
				poll_result = 11;
				poll_cluster_name_result = ue.group;
			}
			if (result == 550) {
				poll_result = 10;
				poll_cluster_name_result = ue.group;
			}
			if (result == 551) {
				// none
			}
		}
		if (poll_result == -100) {
			poll_result = 00;
		}
	}

	public void poll_all_user_domain(String domain, String user) {
		if (user.equals(""))
			user = "FAKE_USER";

		// init
		poll_result = -100;

		if (get_registry_info() == -1) {
			poll_result = -1;
		} else {
			if (serverUpdateEntries.length == 0) {
				poll_result = -2;
			} else {
				for (int i=0; i<serverUpdateEntries.length; i++) {
					String remote_smtp_host = ((UpdateEntry) serverUpdateEntries[i]).host;
					int remote_smtp_port = ((UpdateEntry) serverUpdateEntries[i]).smtp_port;

					if (DEBUG) {
						System.out.printf("Now polling [%s: %d] for [%s@%s]\n",
								remote_smtp_host, remote_smtp_port, user, domain);
					}

					int result = poll_user_domain(domain, user, remote_smtp_host, remote_smtp_port);
					if (result == 250) {
						poll_result = 11;
						poll_cluster_name_result = ((UpdateEntry) serverUpdateEntries[i]).group;
					}
					if (result == 550) {
						poll_result = 10;
						poll_cluster_name_result = ((UpdateEntry) serverUpdateEntries[i]).group;
					}
					if (result == 551) {
						// none
					}
				}
				if (poll_result == -100) {
					poll_result = 00;
				}
			}
		}
	}

	/**
	 * -1: cannot connect
	 * -2: no welcome msg from remote server
	 * -3: no result from remote server regarding the request
	 * -4: return code un-recognizable
	 * 
	 * 3 digits: return code
	 * */
	int poll_user_domain(String domain, String user, String host, int port) {
		socket = new Socket();
		try {
			socket.setSoTimeout(socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(host, port);
			socket.connect(addr, socket_connect_timeout);
			out_stream = socket.getOutputStream();
			in_stream = socket.getInputStream();
			pw_out = new PrintWriter(out_stream);

			// recv the welcome msg from smtp server
			String result = recv();
			if (result == null)
				return -2;

			// send LVRF command
			String request = String.format("LVRF %s@%s", user, domain);
			send(request);

			// recv result
			result = recv();
			send("QUIT");
			if (result == null)
				return -3;

			// parse result code
			int result_code = get_first_3_digist(result);
			if (result_code == -1)
				return -4;

			// return result code
			return result_code;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
		}
	}

	int get_first_3_digist(String str) {
		str = str.substring(0, 3);
		try {
			int i = Integer.parseInt(str);
			return i;
		} catch (NumberFormatException e){
			return -1;
		}
	}

	public void send(String comm) {
		pw_out.printf("%s\n", comm);
		pw_out.flush();
	}

	public String recv() {
		byte[] buf = new byte[buffer_size];
		String rtn = null;
		try {
			int len = in_stream.read(buf);
			if (len != -1)
				rtn = new String(buf, 0, len);
		} catch (IOException e) {
			rtn = null;
			// System.out.printf("no reply from remote host.\n");
			// e.printStackTrace();
		}
		return rtn;
	}
}

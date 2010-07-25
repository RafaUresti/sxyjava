package edu.upenn.cis505.g48.cluster_internal_communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;

public class Handle_IS_RESD {
	boolean DEBUG = true;
	ObjectOutputStream oos_out = null;
	ObjectInputStream ois_in = null;
	Integrated_Server inte_server = null;

	public void handle(String comm, ObjectOutputStream out, ObjectInputStream in, Integrated_Server main_server) {
		this.inte_server = main_server;
		String tokens[] = comm.split(" ");
		boolean is_resd = inte_server.resd_manager.check_item(tokens[1]);
		if (DEBUG) {
			String server_id = Integrated_Server.global_inte_server.cluster_name + "-" +
			Integrated_Server.global_inte_server.cluster_port;
			
			System.out.printf("{%s} handle IS_RESD: [%s]-[%s]\n", server_id,
					comm, is_resd);
		}
		
		try {
			if (is_resd) {
				out.writeUTF("YES");
			} else {
				out.writeUTF("NO");
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package edu.upenn.cis505.g48.cluster_internal_communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;

import edu.upenn.cis505.g48.emailTotalOrdering.HandleDataRequest;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.sync_msg.Handle_SYNC_MSG;
import edu.upenn.cis505.g48.sync_msg.Sync_Msg;

public class Cluster_Internal_Request_Handler implements Runnable{
	Integrated_Server inte_server = null;
	Socket sock = null;

	boolean DEBUG = false;

	public void set_params(Integrated_Server i_server, Socket s_sock) {
		inte_server = i_server;
		sock = s_sock;
	}

	/**
	 * <pre>
	 * Handle commands received by the cluster port.
	 * IS_RESD:
	 * DATA_REQUEST: New cluster member asking for data
	 * INCOMING_EMAIL: When this 
	 * ACK <i>MSG_ID</i>: ACK from other servers of the cluster for receiving a msg with MSG_ID
	 * 	  When ACKs received from all other cluster members except for the sender, deliver the
	 *    email to local database.
	 * </pre>
	 */
	void begin_handle() {
		try {
			ObjectOutputStream oos_out = new ObjectOutputStream(sock.getOutputStream());
			ObjectInputStream ois_in = new ObjectInputStream(sock.getInputStream());

			String comm = ois_in.readUTF();

			if (DEBUG) {
				String server_id = Integrated_Server.global_inte_server.cluster_name + "-" +
				Integrated_Server.global_inte_server.cluster_port;

				System.out.printf("<%s> cluster_chanel get request [%s]\n", server_id, comm);
			}


			if (comm.startsWith("IS_RESD")) {
				new Handle_IS_RESD().handle(comm, oos_out, ois_in, inte_server);
			} else if (comm.startsWith("DATA_REQUEST")){
				HandleDataRequest.handle(oos_out, inte_server);
			} else if (comm.startsWith("DELETE_EMAIL")){
				inte_server.emailSyncManager.syncEmailDeletion(ois_in);
			} else if (comm.startsWith("TRANS_NEWU_INFO")){
				// added by yeming
				// [TRANS_NEWU_INFO user domain psw]
				new Handle_TRANS_NEWU_INFO().handle(comm);
			} else if (comm.startsWith("SYNC_MSG")){
				Handle_SYNC_MSG handle = new Handle_SYNC_MSG();
				Sync_Msg msg = handle.handle(comm, oos_out, ois_in);
				if (msg != null) {
					inte_server.sync_manager.add_to_msg_queue(msg);
					inte_server.sync_manager_thread.interrupt();
				}
			}

			// sock.close();
		} catch (IOException e) {
			// System.out.println("-- Cluster_Internal_Request_Handler: exception");
			// e.printStackTrace();
		}
	}

	public void run() {
		begin_handle();
	}
}

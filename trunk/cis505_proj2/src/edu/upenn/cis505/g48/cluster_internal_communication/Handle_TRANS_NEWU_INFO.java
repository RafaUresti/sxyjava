package edu.upenn.cis505.g48.cluster_internal_communication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.g48.model.ServerData;

public class Handle_TRANS_NEWU_INFO {
	boolean DEBUG = true;

	public boolean handle(String comm) {
		// [TRANS_NEWU_INFO user domain psw]
		ServerData server_data = Integrated_Server.global_inte_server.server_data;
		String tokens[] = comm.split(" ");

		if (! server_data.create_domain_then_create_user(tokens[1], tokens[2], tokens[3])) {
			// has domain already
			if (server_data.create_user_to_domain(tokens[1], tokens[2], tokens[3])) {

			} else {
				if (DEBUG)
					System.out.printf("** TRANS_NEWU_INFO: %s@%s - %s create FAILED\n",
							tokens[1], tokens[2], tokens[3]);
				return false;
			}
		}
		if (DEBUG)
			System.out.printf("** TRANS_NEWU_INFO: %s@%s - %s created\n",
					tokens[1], tokens[2], tokens[3]);
		return true;
	}

	public static void main(String[] args) {
	}

}

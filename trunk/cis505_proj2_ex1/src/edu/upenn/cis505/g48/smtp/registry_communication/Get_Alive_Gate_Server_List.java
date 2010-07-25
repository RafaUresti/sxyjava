package edu.upenn.cis505.g48.smtp.registry_communication;

import java.util.HashSet;

import edu.upenn.cis505.registry.UpdateEntry;

public class Get_Alive_Gate_Server_List {
	boolean DEBUG = true;

	public HashSet <UpdateEntry> get_alive_gate_server_list(HashSet <UpdateEntry> entry_set) {
		HashSet <UpdateEntry> rtn_set = new HashSet <UpdateEntry> ();
		HashSet <UpdateEntry> waste_set = new HashSet <UpdateEntry> ();

		HashSet <String> cluster_got_set = new HashSet <String> ();

		SMTP_Alive_Detector detector = new SMTP_Alive_Detector();
		for (UpdateEntry ue : entry_set) {
			if (DEBUG)
				System.out.printf("** Alive Gate getter: try [%s]'s host: %d...\n",
						ue.group, ue.smtp_port);
			if (! cluster_got_set.contains(ue.group)) {
				if (detector.detect(ue.host, ue.smtp_port)) {
					rtn_set.add(ue);
					cluster_got_set.add(ue.group);

					if (DEBUG)
						System.out.printf("** Alive Gate getter: [%s]'s host: %d ALIVE, got\n",
								ue.group, ue.smtp_port);
				} else {
					waste_set.add(ue);

					if (DEBUG)
						System.out.printf("** Alive Gate getter: [%s]'s host: %d DEAD, put to waste\n",
								ue.group, ue.smtp_port);
				}
			}
		}
		// remove those down servers in entry_set
//		for (UpdateEntry ue : waste_set) {
//			entry_set.remove(ue);
//		}
		return rtn_set;
	}

	public void show_entry_set(HashSet <UpdateEntry> set) {
		System.out.printf("** Entry Set Shower: elements in set:\n");
		for (UpdateEntry ue : set) {
			System.out.printf("** ele: cluster[%s] smtp_port[%d] pop3_port[%d]\n",
					ue.group, ue.smtp_port, ue.pop_port);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

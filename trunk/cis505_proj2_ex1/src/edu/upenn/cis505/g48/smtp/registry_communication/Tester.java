package edu.upenn.cis505.g48.smtp.registry_communication;

import java.util.HashSet;
import java.util.Scanner;

import edu.upenn.cis505.registry.UpdateEntry;

public class Tester {
	public static void main(String[] args) {
		// Registry_Communication_Manager rcm = new Registry_Communication_Manager("nems.seas.upenn.edu", 4242);
		Registry_Communication_Manager rcm = new Registry_Communication_Manager("localhost", 54333);
		
		rcm.set_server_info("localhost", 81, 81, "Z", 81);
		rcm.run();
		
		Scanner scan = new Scanner(System.in);
		while (true) {
			String comm = scan.nextLine();
			if (comm.equals("a"))
				display_entry_set(rcm.get_my_cluster_server());
			if (comm.equals("b"))
				display_entry_set(rcm.get_other_cluster_gate_server());
		}
	}
	
	static void display_entry_set(HashSet <UpdateEntry> set) {
		System.out.printf("display:\n");
		for (UpdateEntry ue : set) {
			System.out.printf("\n[%s %d] [%s %d %d]\n", ue.group, ue.group_port,
					ue.host, ue.smtp_port, ue.pop_port);
		}
	}
}

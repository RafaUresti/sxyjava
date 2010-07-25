package edu.upenn.cis505.g48.smtp.reserve_domain;


import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;

public class Reserve_Domain_Manager {
	boolean DEBUG = false;
	
	HashSet <String> resd_set = new HashSet <String> ();
	Timer timer = new Timer("RESD Manager Timer");

	public void add_item(String domain_name) {
		resd_set.add(domain_name);
		Reserve_Counter counter = new Reserve_Counter(domain_name);
		timer.schedule(counter, Common_Settings.reserve_time * 1000);
	}
	
	public boolean check_item(String domain_name) {
		if (resd_set.contains(domain_name)) 
			return true;
		else return false;
	}
	
	class Reserve_Counter extends TimerTask {
		String domain_name;
		Reserve_Counter(String name) {
			this.domain_name = name;
		}
		public void run() {
			resd_set.remove(this.domain_name);
			if (DEBUG)
				System.out.printf("time out, remove [%s]\n", this.domain_name);
		}
	}
	
	public static void main(String[] args) {

	}
}

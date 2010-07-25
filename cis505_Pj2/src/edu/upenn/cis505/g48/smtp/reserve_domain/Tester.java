package edu.upenn.cis505.g48.smtp.reserve_domain;

import java.util.Scanner;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;


public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Tester().go();
	}
	
	void go(){
		Reserve_Domain_Manager rdm = new Reserve_Domain_Manager();
		Common_Settings.reserve_time = 3;
		rdm.add_item("A");
		Common_Settings.reserve_time = 5;
		rdm.add_item("B");
		Common_Settings.reserve_time = 7;
		rdm.add_item("C");
		
		Scanner scan = new Scanner(System.in);
		while (true) {
			String com = scan.nextLine();
			System.out.println(rdm.check_item(com));
		}
	}

}

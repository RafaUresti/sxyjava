package edu.upenn.cis505.g48.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.upenn.cis505.g48.model.Email;
import edu.upenn.cis505.g48.model.ServerData;

public class PersistentServerDataChecker {

	public static void main(String[] args) throws IOException{
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		ServerData sd ;
		String fileName;
		while(true){
			System.out.println("IP Cluster_port");
			System.out.println("158.130.93.93 45100?");
			String[] inputs = br.readLine().split(" ");
			if (inputs.length != 2) continue;
			else {
				fileName = inputs[0].replaceAll("\\.", "_") + "_" + inputs[1];
				System.out.println(fileName);
				sd= (ServerData)Utility.read_from(fileName);
				if (sd == null) {
					System.out.println("File doesn't exist!");
					continue;
				}
				break;
			}
		}
		while (true){
			try {
				System.out.println("Domains/Users/Emails/Quit");
				String choice = br.readLine();
				sd= (ServerData)Utility.read_from(fileName);
				if (choice.equalsIgnoreCase("Domains")){
					for (String domain : sd.domainMap.keySet()){
						System.out.println(domain);
					}
				}
				if (choice.equalsIgnoreCase("Users")){
					for (String user : sd.userEmail.keySet()){
						System.out.println(user);
					}
				}
				if (choice.equalsIgnoreCase("Emails")){
					for (String user : sd.userEmail.keySet()){
						System.out.println("-----User " + user + "'s emails:---------");
						for (Email email : sd.userEmail.get(user)){
							System.out.println("         " + new String(email.data));
						}
						System.out.println("------------------------------------------");
					}
				}
				if (choice.equalsIgnoreCase("Quit")) break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


}

package edu.upenn.cis505.g48.booting;

import edu.upenn.cis505.g48.model.ServerData;

public class Set_Init_Data {
	public void work(ServerData server_data, String cluster_name) {
		server_data.create_domain(cluster_name);
		server_data.create_user_to_domain("postmaster", cluster_name, "a");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

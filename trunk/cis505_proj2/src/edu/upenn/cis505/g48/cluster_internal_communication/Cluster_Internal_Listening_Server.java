package edu.upenn.cis505.g48.cluster_internal_communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;

public class Cluster_Internal_Listening_Server implements Runnable{
	public boolean DEBUG = true;
	String cluster_name = "";	
	int listening_port = -1;

	ServerSocket serverSock = null;
	Integrated_Server inte_server = null;


	/* methods begin **/
	public Cluster_Internal_Listening_Server (int port, Integrated_Server i_server) {
		listening_port = port;
		inte_server = i_server;
		try {
			serverSock = new ServerSocket(listening_port);
		} catch (IOException e) {
			System.out.println("Failed to create Cluster server socket");
			e.printStackTrace();
		}
	}

	public Cluster_Internal_Listening_Server (ServerSocket serverSock, Integrated_Server i_server) {
		inte_server = i_server;
		this.serverSock = serverSock;
	}

	void work() {
		try {
			if (DEBUG)
				System.out.printf("** Cluster in-commu server starts...\n");			
			while (true) {
				Socket sock = serverSock.accept();

				Cluster_Internal_Request_Handler request_handler = new Cluster_Internal_Request_Handler();
				request_handler.set_params(inte_server, sock);
				
				Thread request_handler_thread = new Thread(request_handler);
				request_handler_thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		work();
	}
	
	public static void main(String[] args) {
	}

}

package edu.upenn.cis505.g48.smtp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.upenn.cis505.registry.*;
import edu.upenn.cis505.Util;

public class SMTP_Registy_Test {

	private String pop_server;
	private int pop_server_port;
	private String smtp_server;
	private int smtp_server_port;
	private Socket socket;

	public static void main(String[] args){
		System.setProperty("line.separator",
				new String(new byte[] { 0xD, 0xA }));
		new SMTP_Registy_Test().run(args);
	}

	private void run(String[] args){
		//Parse args and get pop and smtp server information
		if (args.length == 3){//reg server
			if (!args[0].equalsIgnoreCase("--reg_server")){
				System.out.println("Wrong arguments!");
				return;
			} else {
				String reg_server = args[1];
				int reg_server_port = Util.parsePortNumber(args[2]);
				if (reg_server_port < 0){
					return;
				}
				try {
					socket = new Socket(reg_server, reg_server_port);
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());					
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
					
					byte[] message = new byte[1000];
					int len = inputStream.read(message);
					System.out.println(new String(message,0,len));
					
					outputStream.writeUTF("random");//Request for a server
					outputStream.writeInt(-1);
					outputStream.flush();
					Object[] serverUpdateEntries = (Object [])inputStream.readObject();
					//make sure the response is right
					int entrySize = (int)inputStream.readInt();
					if (entrySize != 1){
						System.out.println("Registery server returned " + entrySize + " server entries");
						// return;
					}
					String response = (String)inputStream.readUTF();
					if (!"DONE".equalsIgnoreCase(response)){
						System.out.println("Registery server didn't end with DONE");
						return;
					}
					//Get the server information
					for (int i=0; i<entrySize; i++) {
						UpdateEntry entry = (UpdateEntry)serverUpdateEntries[i];
						pop_server = smtp_server = entry.host;
						pop_server_port = entry.pop_port;
						smtp_server_port = entry.smtp_port;
						System.out.println("pop_server "+ pop_server + " "+ pop_server_port);
						System.out.println("smtp_server "+ smtp_server + " "+ smtp_server_port);
						System.out.printf("group: %s - %d\n", entry.group, entry.group_port);
						System.out.println();
					}
					outputStream.close();
					inputStream.close();
					socket.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} else if (args.length == 6){//pop and smtp server
			if(!args[0].equalsIgnoreCase("--pop_server")){
				System.out.println("Wrong arguments!");
				return;
			} else {
				pop_server = args[1];
				pop_server_port = Util.parsePortNumber(args[2]);
				if (pop_server_port < 0){
					return;
				}
			} 
			if (!args[3].equalsIgnoreCase("smtp_server")){
				System.out.println("Wrong arguments!");
				return;
			} else {
				smtp_server = args[4];
				smtp_server_port = Util.parsePortNumber(args[5]);
				if (smtp_server_port < 0){
					return;
				}
			}
		} else {
			System.out.println("Wrong arguments!");
			return;
		}
	}
}

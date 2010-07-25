package edu.upenn.cis505.g48.smtp.poll_other_cluster;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.upenn.cis505.registry.UpdateEntry;


public class Get_Info_From_Registry {
	boolean DEBUG = true;

	final int socket_read_timeout = 2000;
	final int socket_connect_timeout = 2000;
	
	public String reg_host = "";
	public int reg_port = -1;
	
	private Socket socket;
	
	public int last_update_timestamp = -1;

	public static void main(String[] args){
		System.setProperty("line.separator", new String(new byte[] { 0xD, 0xA }));
		Get_Info_From_Registry obj = new Get_Info_From_Registry();
		
		obj.reg_host = "nems.seas.upenn.edu";
		obj.reg_port = 4242;
//		
//		obj.reg_host = "localhost";
//		obj.reg_port = 53647;
		
		// obj.get_random();
		obj.get_update(200);
	}
	
	public Object[] get_update(int times_tamp) {
		
		String registy_host = reg_host;
		int registry_port = reg_port;
		
		try {
			socket = new Socket();
			socket.setSoTimeout(socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(registy_host, registry_port);
			socket.connect(addr, socket_connect_timeout);

			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());					
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

			// reading server heading msg
			String heading_msg = inputStream.readUTF();
			System.out.println(heading_msg);
			
			// send command: UPDATE
			outputStream.writeUTF("update");//Request for a server
			outputStream.writeInt(times_tamp);
			outputStream.flush();

			Object[] serverUpdateEntries = (Object [])inputStream.readObject();
			this.last_update_timestamp = inputStream.readInt();
			String response = (String)inputStream.readUTF();
			
			if (DEBUG)
				System.out.printf("response: %s, num of entries: %d-%d\n\n", response, last_update_timestamp, serverUpdateEntries.length);

			//Get the server information
			if (DEBUG) {
				for (int i=0; i<serverUpdateEntries.length; i++) {
					UpdateEntry entry = (UpdateEntry)serverUpdateEntries[i];
					System.out.printf("smtp: %s - %d\n", entry.host, entry.smtp_port);
					System.out.printf("pop3: %s - %d\n", entry.host, entry.pop_port);
					System.out.printf("group: %s - %d\n", entry.group, entry.group_port);
					System.out.println();
				}
			}
			outputStream.close();
			inputStream.close();
			socket.close();

			return serverUpdateEntries;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object[] get_random(){
		System.setProperty("line.separator", new String(new byte[] { 0xD, 0xA }));
		String registy_host = reg_host;
		int registry_port = reg_port;

		try {
			socket = new Socket();
			socket.setSoTimeout(socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(registy_host, registry_port);
			socket.connect(addr, socket_connect_timeout);

			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());					
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			
			String heading_msg = inputStream.readUTF();
			System.out.println(heading_msg);
			
			outputStream.writeUTF("random");//Request for a server
			outputStream.writeInt(-1);
			outputStream.flush();

			Object[] serverUpdateEntries = (Object [])inputStream.readObject();
			int entrySize = (int)inputStream.readInt();
			String response = (String)inputStream.readUTF();
			
			//make sure the response is right
			if (!"DONE".equalsIgnoreCase(response)){
				if (DEBUG)
					System.out.println("Registery server didn't end with DONE");
				return null;
			}
			if (DEBUG)
				System.out.printf("response: %s, num of entries: %d-%d\n\n", response, entrySize, serverUpdateEntries.length);

			//Get the server information
			if (DEBUG) {
				for (int i=0; i<entrySize; i++) {
					UpdateEntry entry = (UpdateEntry)serverUpdateEntries[i];
					System.out.printf("smtp: %s - %d\n", entry.host, entry.smtp_port);
					System.out.printf("pop3: %s - %d\n", entry.host, entry.pop_port);
					System.out.printf("group: %s - %d\n", entry.group, entry.group_port);
					System.out.println();
				}
			}
			outputStream.close();
			inputStream.close();
			socket.close();

			return serverUpdateEntries;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}

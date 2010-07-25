package edu.upenn.cis505.g48.extended_smtp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.upenn.cis505.registry.UpdateEntry;


public class Get_Info_From_Registry {
	boolean DEBUG = true;

	final int socket_read_timeout = 1000;
	final int socket_connect_timeout = 1000;	
	private Socket socket;

	public static void main(String[] args){
		new Get_Info_From_Registry().get();
	}

	public Object[] get(){
		System.setProperty("line.separator", new String(new byte[] { 0xD, 0xA }));
		String registy_host = "nems.seas.upenn.edu";
		int registry_port = 4242;

		try {
			socket = new Socket();
			socket.setSoTimeout(socket_read_timeout);
			InetSocketAddress addr = new InetSocketAddress(registy_host, registry_port);
			socket.connect(addr, socket_connect_timeout);

			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());					
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

			byte[] message = new byte[1024];
			int len = inputStream.read(message);
			System.out.println(new String(message,0,len));

			outputStream.writeUTF("random");//Request for a server
			outputStream.writeInt(-1);
			outputStream.flush();

			Object[] serverUpdateEntries = (Object [])inputStream.readObject();
			//make sure the response is right
			int entrySize = (int)inputStream.readInt();
			String response = (String)inputStream.readUTF();
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

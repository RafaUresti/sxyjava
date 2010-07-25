package edu.upenn.cis505.g48;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import edu.upenn.cis505.g48.model.Email;
import edu.upenn.cis505.g48.pop3.Pop3Client;
import edu.upenn.cis505.g48.smtp.SMTPClient;
import edu.upenn.cis505.g48.smtp.SMTP_User_Client;
import edu.upenn.cis505.registry.*;
import edu.upenn.cis505.Util;

public class Client {

	private String pop_server;
	private int pop_server_port;
	private String smtp_server;
	private int smtp_server_port;
	//	private Socket pop_socket;
	//	private Socket smtp_socket;
	private Pop3Client pop3Client;
	private SMTP_User_Client smtpClient;

	/**
	 * To run the mail client with arguments specifying either
	 * "--reg_server address port_number"
	 * or
	 * "--pop_server address port_number" and
	 * "--smtp_server address port_number".
	 * If specified reg_server, the program will obtain from
	 * registry server a random pop and smtp_server with respective
	 * ports
	 * @param args The server
	 */
	public static void main(String[] args){
		System.setProperty("line.separator",
				new String(new byte[] { 0xD, 0xA }));
		new Client().run(args);
	}

	private void run(String[] args){
		if (!parse(args)) return;
		processUserinput();
	}

	private void processUserinput() {

		Scanner scanner = new Scanner(System.in);
		String email;
		String password;

		System.out.println("Welcome to cis505g48 email client!");
		while (true){
			System.out.print("Press \"1\" to check emails, \"2\" to send emails, \"3\" to quit:");
			String input;
			input = scanner.nextLine();
			if (input.length() == 0){
				continue;
			}
			if (input.startsWith("1")){
				while(true){
					System.out.println("Please enter your email address:");
					email = scanner.nextLine(); //FIXME
					System.out.println("Please enter your password:");
					password = scanner.nextLine(); //FIXME
					try{
						if (verify(email, password, pop_server, pop_server_port)){
							break;
						}
					} catch (UnknownHostException e){
						e.printStackTrace();
						System.out.println("The IP address of the host could not be determined");
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Server error!");
					}
				}
				pop3Client = new Pop3Client();
				pop3Client.setParameter(pop_server, pop_server_port, email, password);
				try{
					pop3Client.handleRequests();
				} catch (IOException e){
					System.err.print("\n\nPop 3 server error!!\n\n");
					continue;
				} 
			}
			else if (input.startsWith("2")){
				smtpClient = new SMTP_User_Client();
				smtpClient.control(smtp_server, smtp_server_port);
			} else if (input.startsWith("3")){
				System.out.println("Thank you for using the system. Good Bye!");
				break;
			}
		}
	}

	/**
	 * Verifies email address and password on the pop3 server
	 * @param email
	 * @param password
	 * @param pop_server
	 * @param pop_server_port
	 * @return <code>true</code> if successful
	 * @throws UnknownHostException If connection to the pop3 server fails
	 * @throws IOException
	 */
	public static boolean verify(String email, String password, String pop_server, int pop_server_port)
	throws UnknownHostException, IOException {
		Socket pop3Socket = new Socket(pop_server, pop_server_port);
		DataInputStream is = new DataInputStream(pop3Socket.getInputStream());
		DataOutputStream os = new DataOutputStream(pop3Socket.getOutputStream());
		String message = authorizePop3(email, password, is, os);
		if (message.startsWith("+OK")){
			os.writeBytes("QUIT\r\n");
			is.close();
			os.close();
			pop3Socket.close();
			return true;
		}
		else {
			os.writeBytes("QUIT\r\n");
			is.close();
			os.close();
			pop3Socket.close();
			return false;
		}
	}

	/**
	 * Helper method to authorize email and password using
	 * data input/output stream
	 * @param email 
	 * @param password
	 * @param is
	 * @param os
	 * @return The final response from the server. +OK is success.
	 * @throws IOException
	 */
	public static String authorizePop3(String email, String password,
			DataInputStream is, DataOutputStream os) throws IOException {
		byte[] b = new byte[1024];
		int len = is.read(b);
		System.out.println(new String(b, 0, len));//greeting message
		os.writeBytes("USER " + email+"\r\n");
		os.flush();
		len = is.read(b);
		System.out.println(new String(b, 0, len));
		os.writeBytes("PASS " + password+"\r\n");
		os.flush();
		len = is.read(b);
		String message = new String(b, 0, len);
		System.out.println(message);
		return message;
	}

	/**
	 * Parse the server arguments and obtain the pop3 and smtp server and ports 
	 * @param args The arguments specifying arguments
	 * @return <code>true</code> if args parsed successfully
	 */
	private boolean parse(String[] args){
		//Parse args and get pop and smtp server information

		if (args.length == 3){//reg server
			if (!args[0].equalsIgnoreCase("--reg_server")){
				System.out.println("Wrong arguments!");
				return false;
			} else {
				String reg_server = args[1];;
				int reg_server_port = Util.parsePortNumber(args[2]);
				if (reg_server_port < 0){
					return false;
				}
				Socket registrySocket;
				try {
					registrySocket = new Socket(reg_server, reg_server_port);
					ObjectOutputStream outputStream = new ObjectOutputStream(registrySocket.getOutputStream());
					ObjectInputStream inputStream = new ObjectInputStream(registrySocket.getInputStream());
					System.out.println(inputStream.readUTF());
					outputStream.writeUTF("random");//Request for a server
					outputStream.writeInt(1);
					outputStream.flush();
					Object[] serverUpdateEntries = (Object [])inputStream.readObject();
					//make sure the response is right
					int entrySize = (int)inputStream.readInt();
					if (entrySize != 1){
						System.out.println("Registery server returned " + entrySize + " server entries");
						return false;
					}
					String response = (String)inputStream.readUTF();
					if (!"DONE".equalsIgnoreCase(response)){
						System.out.println("Registery server didn't end with DONE");
						return false;
					}
					//Get the server information
					UpdateEntry entry = (UpdateEntry)serverUpdateEntries[0];
					pop_server = smtp_server = entry.host;
					pop_server_port = entry.pop_port;
					smtp_server_port = entry.smtp_port;
					System.out.println("pop_server "+ pop_server + " "+ pop_server_port);
					System.out.println("smtp_server "+ smtp_server + " "+ smtp_server_port);
					outputStream.close();
					inputStream.close();
					registrySocket.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return false;
				}
			}
		} else if (args.length == 6){//pop and smtp server

			if(!args[0].equalsIgnoreCase("--pop_server")){
				System.out.println("Wrong arguments!");
				return false;
			} else {
				pop_server = args[1];
				pop_server_port = Util.parsePortNumber(args[2]);
				if (pop_server_port < 0){
					return false;
				}
			} 
			if (!args[3].equalsIgnoreCase("--smtp_server")){
				System.out.println("Wrong arguments!");
				return false;
			} else {
				smtp_server = args[4];
				smtp_server_port = Util.parsePortNumber(args[5]);
				if (smtp_server_port < 0){
					return false;
				}
			}
		} else {
			System.out.println("Wrong arguments!");
			return false;
		}
		System.out.println("Pop server: " + pop_server + " " + pop_server_port);
		System.out.println("SMTP server: " + smtp_server + " " + smtp_server_port);
		return true;
	}
}

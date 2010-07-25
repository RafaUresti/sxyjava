package edu.upenn.cis505.g48.pop3;

import java.util.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.upenn.cis505.g48.Client;
import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.model.Email;

public class Pop3Client {
	public static final String TERMINATE = "\r\n.\r\n";
	private Socket socket;
	private String pop3Address;
	private int pop3Port;
	private String email;
	private String password;
	private TreeSet<Email> mailBox;
	private StringBuilder sessionResponse;
	public Pop3Client(){
		System.out.printf("Get in Pop3 new client\n");
		mailBox = new TreeSet<Email>();
		System.out.printf("Get in Pop3 new client done\n");
	}
	
	public static void main(String[] str) {
		System.out.printf("Get in Pop3...\n");
		
		Pop3Client obj = new Pop3Client();
		System.out.printf("Get in Pop3... we have an object \n");
		
		// obj.setParameter(str[0], Integer.parseInt(str[1]), str[2], str[3]);
		
		// added by yeming
		Scanner scan = new Scanner(System.in);
		String comm = scan.nextLine();
		String tokens[] = comm.split(" ");
		obj.setParameter(tokens[0], Integer.parseInt(tokens[1]), tokens[2], tokens[3]);
		//~
		
		
		System.out.printf("Get in Pop3.Parameters seted\n");
		try {
			System.out.printf("trying to get in request...\n");
			obj.handleRequests();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setParameter(String address, int port, String email, String password){
		pop3Address = address;
		pop3Port = port;
		this.email = email;
		this.password = password;
	}


	public void handleRequests() throws UnknownHostException, IOException{
		System.out.println("in handlerequests: i am int!");
		socket = new Socket(pop3Address, pop3Port);
		System.out.println("in handlerequests: "+ pop3Address + "  " + pop3Port);
		DataOutputStream os= new DataOutputStream(socket.getOutputStream());
		DataInputStream is = new DataInputStream(socket.getInputStream());
		System.out.println("in handlerequests: in and out stream set");
		String authMessage = Client.authorizePop3(email, password, is, os);
		System.out.println("in handlerequests: "+ authMessage);
		if (!authMessage.startsWith("+OK")){
			throw new IOException();
		}
		System.out.println("Pop3 response : " + authMessage);
		Scanner scanner = new Scanner(System.in);
		while(true){
			System.out.println("Please use the following standard POP commands:");
			System.out.println("STAT; LIST [msg]; RETR msg; DELE msg; " +
			"NOOP; TOP msg n; UIDL [msg]; RSET; QUIT");
			String input = scanner.nextLine();
			os.writeBytes(input + "\r\n");
			os.flush();
			sessionResponse = new StringBuilder();
			String response = readIs(is);
			System.out.print(response);
			sessionResponse.append(response);
			if (input.toUpperCase().startsWith("QUIT") && 
					response.startsWith("+OK")){
				break;
			} 
			else if(response.startsWith("-ERR")){
				continue;
			}
			else if(input.toUpperCase().startsWith("STAT")){/*do nothing*/}
			else if(input.toUpperCase().startsWith("LIST") && 
					response.startsWith("+OK") &&
					input.split("[' ']+").length == 1 &&
					!response.endsWith(TERMINATE)){//LIST with args, +OK response with more to go
				printRspTillEnd(is);
			}
			else if(input.toUpperCase().startsWith("RETR") && 
					response.startsWith("+OK")){//RETR with +OK response
				downloadEmail(is);
			}
			else if(input.toUpperCase().startsWith("DELE")){}
			else if(input.toUpperCase().startsWith("NOOP")){}
			else if(input.toUpperCase().startsWith("RSET")){}
			else if(input.toUpperCase().startsWith("TOP") &&
					response.startsWith("+OK")){
				downloadEmail(is);
			}
			else if(input.toUpperCase().startsWith("UIDL") 
					&& input.split("[' ']+").length == 1
					&& !response.endsWith(TERMINATE)){//UIDL without arguments
				printRspTillEnd(is);
			}
		}
		is.close();
		os.close();
		socket.close();
	}



	private void printRspTillEnd(DataInputStream is) throws IOException{
		byte[] bContents = new byte[Common_Settings.MAX_CONTENT_SIZE];
		int len;
		do {//printing each hashed message
			len = is.read(bContents);
			String message = new String(bContents, 0, len);
			sessionResponse.append(message);
			System.out.print(message);
		}while (!sessionResponse.toString().endsWith(TERMINATE));
		System.out.println();
	}

	/**
	 * Read a byte stream from input stream
	 * @param is
	 * @return The String representation of the byte array input.
	 * @throws IOException
	 */
	private String readIs(DataInputStream is) throws IOException {
		byte[] bContent = new byte[Common_Settings.MAX_CONTENT_SIZE];
		int len = is.read(bContent);
		return new String(bContent, 0, len);
	}
	
	/**
	 * Used after sending RETR and TOP commands to receive and store email 
	 * @param is
	 * @param bContent The first response after the command
	 * @throws IOException
	 */
	private void downloadEmail(DataInputStream is)
	throws IOException {
		String mail;
		if (sessionResponse.toString().endsWith(TERMINATE)){//Email is contained in sessionResponse
			mail = chopFirstLine(sessionResponse);
			// mailBox.add(new Email(mail));
		} else {//Email hasn't been fully retrieved
			StringBuilder mailBuilder = new StringBuilder();
			String mailHead = chopFirstLine(sessionResponse);
			mailBuilder.append(mailHead);
			do{
				mailBuilder.append(readIs(is));
			} while (!mailBuilder.toString().endsWith(TERMINATE));
			
			mail = eliminateAppendedDot(mailBuilder).toString();
		}
		String content = new String(mail);
		System.out.println(content);
	}
	
	
	private StringBuilder eliminateAppendedDot(StringBuilder mailBuilder) {
		if (mailBuilder.length() < 6) return mailBuilder;
		String[] lines = mailBuilder.toString().split("[\r\n]", -1);
		int l = lines.length;
		StringBuilder s = new StringBuilder();
		for ( int i = 0; i < l; i ++) {
			if ( lines[i].startsWith("..")) {
				lines[i] = new String( lines[i].substring(1));
			}
			s.append(lines[i]+"\r\n");
		}
		return s;
	}
	/**
	 * Get the data stream with the first line removed.
	 * The end of first line is marked with \r\n.
	 * @param content
	 * @return The stream without the first line. Size equals to the data.
	 */
	private String chopFirstLine(StringBuilder content) {
		int len = content.length();
		int endLineIndex = 0;
		for (int i = 0; i < len; i ++){
			if (i > 0 && content.charAt(i - 1) == '\r' && content.charAt(i) == '\n'){
				endLineIndex = i;
				break;
			}
		}
		if (len == endLineIndex + 1){
			return "";
		}
		return content.substring(endLineIndex + 1);
	}

}
package edu.upenn.cis505.g48.pop3;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.io.*;
import java.sql.*;

import edu.upenn.cis505.g48.model.Email;
import edu.upenn.cis505.g48.model.ServerData;

public class Pop3Handler implements Runnable {
	int my_pulse_count = 0;
	
	Socket sock = null;
	Pop3 pop3 = new Pop3();
	private HashMap<String, HashSet<String>> abc;
	private HashMap<String, TreeSet<Email>> user_email = new HashMap<String, TreeSet<Email>> ();
	private HashMap<String, String> user_pass = new HashMap<String, String> ();
	
	public Pop3Handler(Socket the_sock) {
		
		sock = the_sock;		
		/*
		abc = new HashMap<String, HashSet<String>>();
		String[] ss = {"lzf1","rafi2","rafi3"};
		create_entries("a.b", ss);
//		String[] ss2 = {"xiao1","xiao2","xiao3"};
//		create_entries("hixiaoyi", ss2 );
		the_server_data.set_domainMap(abc);
		Email t_email = new Email();
		Email t_email2 = new Email();
		byte[] da = new String("email1\r\\r\nhi here\r\nhey\r\n").getBytes();
		byte[] da2 = new String("email2\r\\r\nhi here\r\nhey\r\n1 adf asf").getBytes();
		t_email.setData(da);
		t_email2.setData(da2);
		Timestamp ts1 = new Timestamp(System.currentTimeMillis());
		Timestamp ts2 = new Timestamp(System.currentTimeMillis());
		t_email.setTimeStamp(ts1);
		t_email2.setTimeStamp(ts2);
		
		TreeSet<Email> em_set = new TreeSet<Email>();
		System.out.println("debug+++"+ em_set.size());
		System.out.println(em_set.add(t_email));
		System.out.println("debug---"+ em_set.size());
		System.out.println(em_set.add(t_email2));
		System.out.println("debug==="+ em_set.size());
		user_email.put("lzf1@a.b", em_set);
		the_server_data.set_userEmail(user_email);
		user_pass.put("lzf1@a.b","rafi1");
		the_server_data.set_userPass(user_pass);
		*/
	}

	private void create_entries(String domain_name, String[] users) {
		String[] names = users;
		List<String> list = Arrays.asList(names);
		HashSet<String> nameSet = new HashSet<String>(list);
		abc.put(domain_name, nameSet);
	}

	public void do_service(){
		try {
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			PrintWriter out_print = new PrintWriter(out, true);
			pop3.init(out_print);

			out_print.println("+OK POP3 server ready");
			out_print.flush();
			System.out.println("+OK POP3 server ready");
			
			String cc="";
			while (true) {
				if (!sock.isConnected()) {
					sock.close();
					break;
				}
				byte[] buf = new byte[1024];
				int len = in.read(buf);
				if ( len == -1 ) {
					System.err.printf("** POP3 pulse: [%d] [%d]\n",
							Thread.currentThread().getId(),
							this.my_pulse_count++);
					break;
					// continue;
				}
				String content = new String(buf, 0, len);
				System.out.printf("get command(%d): [%s]\n", len, content);
				cc += content;
				int sub = 0;
				if (cc.endsWith("\r\n")) sub = 2;
//				else if (cc.endsWith("\r")) 
//					sub = 1;
//				else if (cc.endsWith("\n")) 
//					sub = 1;
				if (sub > 0) {
					cc = cc.substring(0, cc.length() - sub);

					int result = pop3.rcv_command(cc);
					if (result == -1) {
						// command QUIT
						System.out.printf("receive QUIT\n");
						sock.close();
						break;
					}
					/*
					out_print.printf("a echo: <%s>\n", cc);
					System.out.printf("[%s]\n", cc);
					 */
					out_print.flush();
					cc = "";
				}
			}
		} catch (IOException e) {
		  System.out.println("-- Pop3Handler: exception");
			// e.printStackTrace();
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		do_service();
	}
}

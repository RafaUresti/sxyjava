package edu.upenn.cis505.registry;

/////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2009 The University of Pennylvania
// Permission to use, copy, modify, and distribute this software and
// its documentation for any purpose, without fee, and without a
// written agreement is hereby granted, provided that the above copyright 
// notice and this paragraph and the following two paragraphs appear in
// all copies. 
//
// IN NO EVENT SHALL THE UNIVERSITY OF PENNSYLVANIA BE LIABLE TO ANY PARTY FOR
// DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
// LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
// EVEN IF THE UNIVERSITY OF PENNSYLVANIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. 
//
// THE UNIVERSITY OF PENNSYLVANIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
// AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON
// AN "AS IS" BASIS, AND THE UNIVERSITY OF PENNSYLVANIA HAS NO OBLIGATIONS TO
// PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. 
//////////////////////////////////////////////////////////////////////////////
//
// Author: Rafi Rubin
//
//////////////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class RegTest {

	public static List<String> commands = Arrays.asList(new String[] { "a",
			"r", "u", "q", "n", "e" });

	public static int readInt(String prompt, BufferedReader in,
			PrintStream out, int min, int max) throws IOException {
		int i = 0;
		boolean read = false;
		do {
			out.print(prompt);
			out.flush();
			String line = in.readLine();
			try {
				i = Integer.parseInt(line);
				if (i >= min && i <= max)
					read = true;
				else
					throw new NumberFormatException("out of range");

			} catch (NumberFormatException nfe) {
				out.println("Expected integer from " + min + " to " + max);
			}
		} while (!read);
		return i;
	}

	public static String readString(String prompt, BufferedReader in,
			PrintStream out, int minlen) throws IOException {
		String line = "";
		do {
			out.print(prompt);
			out.flush();
			line = in.readLine();
			if (line.length() < minlen) {
				out.println("Expecting a string of a least " + minlen
						+ " characters");
			}
		} while (line.length() < minlen);
		return line;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("line.separator",
				new String(new byte[] { 0xD, 0xA }));
		String host = args[0];
		int port = Integer.parseInt(args[1]);

		Socket reg = new Socket(host, port);

		/*
		 * note order is CRITICAL here. If you create them in the wrong order
		 * your program will block
		 */
		ObjectOutputStream oos = new ObjectOutputStream(reg.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(reg.getInputStream());

		String state;
		boolean run = true;
		BufferedReader cin = new BufferedReader(
				new InputStreamReader(System.in));
		PrintStream cout = System.out;
		while (run) {
			state = ois.readUTF();
			cout.println("State " + state);
			String hostname=state.split(" ")[1];
			System.out.println("host name is: "+hostname);
			run = command(ois, oos, cin, cout);
		}

		try {
			ois.close();
			oos.close();

			reg.close();
		} catch (Exception e) {
		}
	}

	private static boolean command(ObjectInputStream ois,
			ObjectOutputStream oos, BufferedReader cin, PrintStream cout)
			throws IOException, ClassNotFoundException {

		String line = "";

		String host;
		String group;
		int pop_port;
		int smtp_port;
		int group_port;

		while (!commands.contains(line)) {
			cout.println("Commands are a,r,e,u,n,q.");
			cout.flush();
			line = cin.readLine();
		}

		if (line.equals("q")) {
			oos.writeUTF("QUIT");
			return false;
		} else if (line.equals("a") || line.equals("r") || line.equals("e")) {
			host = readString("host: ", cin, cout, 1);
			group = readString("group: ", cin, cout, 1);

			pop_port = readInt("pop port: ", cin, cout, 0, 65535);
			smtp_port = readInt("smtp port: ", cin, cout, 0, 65535);
			group_port = readInt("group port: ", cin, cout, 0, 65535);

			if (line.equals("a"))
				oos.writeUTF("ADD");
			else if (line.equals("r"))
				oos.writeUTF("REMOVE");

			if (line.equals("e")) {
				oos.writeUTF("ADD_ENTRY");
				oos.flush();
				String prompt = "";
				String prompt2 = "";
				for (int i = 0; i < UpdateEntry.ACTION_STRINGS.length; i++) {
					prompt += "\t" + i + ": " + UpdateEntry.ACTION_STRINGS[i]
							+ "\n";
					prompt2 += i;
				}
				prompt = "\n" + prompt + "Please select an action [" + prompt2
						+ "] ";
				int act = readInt(prompt, cin, cout, 0,
						UpdateEntry.ACTION_STRINGS.length - 1);
				oos.writeObject(new UpdateEntry(act, host, pop_port, smtp_port,
						group, group_port));
			} else {
				oos.writeUTF(host);
				oos.writeInt(pop_port);
				oos.writeInt(smtp_port);
				oos.writeUTF(group);
				oos.writeInt(group_port);
			}
			oos.flush();
			cout.println(ois.readUTF());
			cout.flush();
		} else if (line.equals("n") || line.equals("u")) {
			if (line.equals("n"))
				oos.writeUTF("random");
			else
				oos.writeUTF("update");
			oos.flush();
			int n = readInt("int: ", cin, cout, Integer.MIN_VALUE,
					Integer.MAX_VALUE);
			oos.writeInt(n);
			oos.flush();
			Object[] nodes = (Object[]) ois.readObject();
			cout.println("nodes:");
			for (Object o : nodes) {
				cout.println("\t" + o);
			}
			cout.println("number send by server: " + ois.readInt());
			cout.println("this should say done: " + ois.readUTF());
		}
		return true;

	}
}

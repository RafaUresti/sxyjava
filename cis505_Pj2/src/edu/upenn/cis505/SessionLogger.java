package edu.upenn.cis505;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SessionLogger implements Runnable {

	private Socket a;

	private Socket b;

	private boolean state = true;

	private Collection<OutputStream> copy_from_a = null;
	private Collection<OutputStream> copy_from_b = null;

	public SessionLogger(Socket a, Socket b) {
		this.a = a;
		this.b = b;
		new Thread(this).start();
	}

	public SessionLogger(Socket a, Socket b, Collection<OutputStream> from_a,
			Collection<OutputStream> from_b) {
		this.a = a;
		this.b = b;
		this.copy_from_a = from_a;
		this.copy_from_b = from_b;

		new Thread(this).start();
	}

	private void spin(InputStream in, OutputStream out,
			Collection<OutputStream> extra_outs) throws IOException {
		byte[] buf = new byte[4096];
		int count = 0;
		OutputStream[] outs = extra_outs.toArray(new OutputStream[extra_outs
				.size() + 1]);
		outs[outs.length - 1] = out;

		try {
			while ((count = in.read(buf)) >= 0) {
				if (count > 0) {
					for (OutputStream ow : outs)
						ow.write(buf, 0, count);
					if (in.available() <= 0)
						for (OutputStream ow : outs)
							ow.flush();
				}
			}

		} catch (IOException ioe) {
			try {
				in.close();
			} catch (IOException e) {
			}
			try {
				out.close();
				for (OutputStream ow : outs)
					ow.flush();

			} catch (IOException e) {
			}
			throw ioe;
		}

	}

	private void spin(InputStream i, OutputStream o) throws IOException {
		byte[] buf = new byte[4096];
		int count = 0;

		try {
			while ((count = i.read(buf)) >= 0) {
				if (count > 0) {
					o.write(buf, 0, count);
					if (i.available() <= 0)
						o.flush();
				}
			}

		} catch (IOException ioe) {
			try {
				i.close();
			} catch (IOException e) {
			}
			try {
				o.close();
			} catch (IOException e) {
			}
			throw ioe;
		}

	}

	public void run() {
		if (state) {
			try {
				state = false;
				new Thread(this).start();
				if (copy_from_b != null && copy_from_b.size() > 0)
					spin(b.getInputStream(), a.getOutputStream(), copy_from_b);
				else
					spin(b.getInputStream(), a.getOutputStream());
			} catch (IOException e) {
				// e.printStackTrace();
			}
		} else {
			try {
				if (copy_from_a != null && copy_from_a.size() > 0)
					spin(a.getInputStream(), b.getOutputStream(), copy_from_a);
				else
					spin(a.getInputStream(), b.getOutputStream());
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}

		try {
			a.close();
		} catch (IOException e1) {
		}

		try {
			b.close();
		} catch (IOException e1) {
		}
		// System.out.println("splicer done");
	}

	public static void main(String[] args) throws Exception {

		String server = "localhost";
		int port = 0;
		int listen = 0;

		HashMap<String, Object> arg_map = new HashMap<String, Object>();
		HashMap<String, String> arg_help = new HashMap<String, String>();

		arg_map.put("-h", 0);
		arg_map.put("--help", "-h");

		arg_map.put("-s", 1);
		arg_map.put("--server", "-s");
		arg_help.put("-s", "Target server");

		arg_map.put("-p", 1);
		arg_map.put("--port", "-p");
		arg_help.put("-p", "Target port on the target server");

		arg_map.put("-l", 1);
		arg_map.put("--listen", "-l");
		arg_map.put("--listen_port", "-l");
		arg_help
				.put(
						"-l",
						"Port to listen for incoming connections\n\t(0 or leave this unset to automatically select one for you");

		arg_map.put("--client_echo", 1);
		arg_help
				.put("--client_echo",
						"Comma separated list of files to dump the stream from the client");
		arg_map.put("--server_echo", 1);
		arg_help
				.put("--server_echo",
						"Comma separated list of files to dump the stream from the server");
		arg_map.put("--echo", 1);
		arg_help.put("--echo",
				"Comma separated list of files to dump all traffic");

		arg_map.put("--no_cli_stdout", 0);
		arg_map.put("--no_srv_stdout", 0);
		arg_map.put("--no_stdout", 0);
		arg_help.put("--no_cli_stdout", "don't show traffic from the client");
		arg_help.put("--no_srv_stdout", "don't show traffic from the server");
		arg_help.put("--no_stdout", "don't show traffic from either");

		ArgParse arg = new ArgParse(arg_map, args, null, arg_help);

		if (arg.containsKey("-h") || arg.rest.size() > 0) {
			arg.help();
			if (arg.rest.size() > 0) {
				System.out.print("bad args:");
				for (String s : arg.rest)
					System.out.print(" " + s);
				System.out.println();
				System.exit(1);
			}
			System.exit(0);
		}

		if (arg.containsKey("-s"))
			server = arg.get("-s").toString();
		if (arg.containsKey("-p"))
			port = new Integer(arg.get("-p").toString());
		if (arg.containsKey("-l"))
			listen = new Integer(arg.get("-l").toString());

		Set<OutputStream> client_echo = new HashSet<OutputStream>();
		Set<OutputStream> server_echo = new HashSet<OutputStream>();

		if (!(arg.containsKey("--no_cli_stdout") || arg
				.containsKey("--no_stdout")))
			client_echo.add(System.out);
		if (!(arg.containsKey("--no_srv_stdout") || arg
				.containsKey("--no_stdout")))
			server_echo.add(System.out);
		Map<String, OutputStream> all_outs = new HashMap<String, OutputStream>();
		Set<String> client_echo_set = new HashSet<String>();
		Set<String> server_echo_set = new HashSet<String>();
		if (arg.containsKey("--echo"))
			for (String fname : arg.get("--echo").toString().split(",")) {
				client_echo_set.add(fname);
				server_echo_set.add(fname);
			}
		if (arg.containsKey("--client_echo"))
			for (String fname : arg.get("--client_echo").toString().split(",")) {
				client_echo_set.add(fname);
			}
		if (arg.containsKey("--server_echo"))
			for (String fname : arg.get("--server_echo").toString().split(",")) {
				server_echo_set.add(fname);
			}

		for (String fname : client_echo_set) {
			if (!all_outs.containsKey(fname))
				all_outs.put(fname, new java.io.FileOutputStream(fname));
			client_echo.add(all_outs.get(fname));
			System.out.println("adding client echo: " + fname);
		}
		for (String fname : server_echo_set) {
			if (!all_outs.containsKey(fname))
				all_outs.put(fname, new java.io.FileOutputStream(fname));
			server_echo.add(all_outs.get(fname));
			System.out.println("adding server echo: " + fname);
		}

		System.out.println("will connect incoming clients to: " + server + ":"
				+ port);
		ServerSocket listen_socket = new ServerSocket(listen);
		System.out
				.println("listening on port: " + listen_socket.getLocalPort());
		while (true) {
			Socket client_sock = listen_socket.accept();
			Socket server_sock = new Socket(server, port);
			new SessionLogger(client_sock, server_sock, client_echo,
					server_echo);
		}
	}
}

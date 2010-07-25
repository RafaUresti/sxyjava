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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

	Registry parent = null;
	Socket client = null;
	InetAddress remote_host = null;

	public ClientHandler(Registry registry, Socket cli) {
		parent = registry;
		client = cli;
		remote_host = cli.getInetAddress();
	}

	private void addNode(ObjectInputStream in, ObjectOutputStream out)
			throws IOException {
		UpdateEntry ue = new UpdateEntry(UpdateEntry.ADD_HOST, in);
		
		/**TODO when test, modify this line*/
		if (ue.check() && parent.add(ue)) {
		// if (parent.add(ue)) {
			
			out.writeUTF("OK");
		} else
			out.writeUTF("FAILED");
		out.flush();
	}

	private void addEntry(ObjectInputStream in, ObjectOutputStream out,
			boolean warmup) throws IOException {
		UpdateEntry ue;
		boolean succ = false;
		try {
			ue = (UpdateEntry) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			out.writeUTF("FAILED");
			out.flush();
			out.close();
			in.close();
			return;
		}
		// System.out.println("got entry: " + ue);
		/* note this will reject removes that occur during warmup */
		if ((ue.action == UpdateEntry.ADD_HOST && ue.check())
				|| (!warmup && ue.action == UpdateEntry.REMOVE_HOST)) {
			succ = parent.add(ue);
		}

		if (succ)
			out.writeUTF("OK");
		else
			out.writeUTF("FAILED");
		out.flush();
	}

	private void removeNode(ObjectInputStream in, ObjectOutputStream out)
			throws IOException {
		UpdateEntry ue = new UpdateEntry(UpdateEntry.REMOVE_HOST, in);
		if (parent.add(ue))
			out.writeUTF("OK");
		else
			out.writeUTF("FAILED");
		out.flush();
	}

	private void update(ObjectInputStream in, ObjectOutputStream out)
			throws IOException {
		int t = in.readInt();
		if (t < 0)
			t = 0;
		int n = parent.size();
		if (t >= n)
			t = n;
		out.writeObject(parent.subList(t, n).toArray());
		out.writeInt(n);
		out.writeUTF("DONE");
		out.flush();
	}

	private void getNNodes(ObjectInputStream in, ObjectOutputStream out)
			throws IOException {
		int n = in.readInt();
		// System.out.println("got N=" + n);
		List<UpdateEntry> lue = parent.getNNodes(n);
		out.writeObject(lue.toArray());
		/*
		 * not really necessary but send the number of elements anyway The real
		 * reason for this is to keep the behavior syntax the same as update
		 */
		out.writeInt(lue.size());
		out.writeUTF("DONE");
		out.flush();
	}

	public void handleClient(ObjectInputStream in, ObjectOutputStream out)
			throws IOException {
		String line;
		if (parent.warming_up) {
			out.writeUTF("RESET " + remote_host.getHostName());
			out.flush();
			line = in.readUTF();

			/*
			 * since this is a warm up period, we'll let nodes add themselves,
			 * but nothing else
			 */
			if (line.equals("ADD") || line.equalsIgnoreCase("add_entry")) {
				if (line.equals("ADD"))
					addNode(in, out);
				else
					addEntry(in, out, true);
				out.writeUTF("READY " + remote_host.getHostName());
				out.flush();
				line = in.readUTF();
			}
			try {
				if (parent.warming_up) // verify its still asleep
					synchronized (parent) {
						/*
						 * since the time field has been updated before the
						 * notify all is called. The notify and parent.wait
						 * should never be swapped The synchronization is a
						 * further guarantee. But that being said there should
						 * be no harm in letting that timeout stay.
						 */
						if (parent.warming_up) // check again
							parent.wait(Registry.warmup);
					} // just on the off chance we miss the
				// notifyAll, wake up anyway

				assert !parent.warming_up; // doesn't hurt to add an extra
				// check
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			out.writeUTF("READY " + remote_host.getHostName());
			out.flush();
			// System.out.println("hi");
			line = in.readUTF();
		}
		while (client.isConnected() && !line.equalsIgnoreCase("QUIT")) {
			System.out.println("Command: " + line);
			if (line.equalsIgnoreCase("UPDATE")) {
				update(in, out);
			} else if (line.equalsIgnoreCase("ADD")) {
				addNode(in, out);
			} else if (line.equalsIgnoreCase("REMOVE")) {
				removeNode(in, out);
			} else if (line.equalsIgnoreCase("add_entry")) {
				addEntry(in, out, false);
			} else if (line.equalsIgnoreCase("random")) {
				getNNodes(in, out);
			} else {
				System.err.println("got invalid command");
			}
			out.writeUTF("READY " + remote_host.getHostName());
			out.flush();
			line = in.readUTF();
		}
		client.close();
	}

	public void run() {
		try {
			handleClient(new ObjectInputStream(client.getInputStream()),
					new ObjectOutputStream(client.getOutputStream()));

		} catch (java.io.EOFException eofe) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		try { /* ensure it closes if at all possible */
			client.close();
		} catch (IOException e) {
		}
	}

}

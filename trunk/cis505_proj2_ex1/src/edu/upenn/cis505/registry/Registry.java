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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Registry extends Vector<UpdateEntry> implements Runnable {
	private static final long serialVersionUID = 7549412619612524370L;

	private Set<UpdateEntry> nodes = new HashSet<UpdateEntry>();
	private List<UpdateEntry> nodes_list = null;
	private boolean nodes_arr_dirty = true;
	private boolean locked=false;

	public boolean warming_up = true;

	/** warm up period */
	public static int warmup = 1000;
	static {
		if (System.getProperty("DEBUG") != null)
			warmup = 1000;
	}

	public synchronized boolean add(UpdateEntry entry) {
		if(locked)
			return false;
		/* first update the nodes set */
		boolean contained = nodes.contains(entry);
		if (entry.action == UpdateEntry.ADD_HOST) {
			if (!contained) {
				nodes.add(entry);
				nodes_arr_dirty = true;
			} else
				return false;
		} else if (entry.action == UpdateEntry.REMOVE_HOST) {
			if (contained) {
				nodes.remove(entry);
				nodes_arr_dirty = true;
			} else
				return false;
		}

		/* normal vector add */
		return super.add(entry);
	}

	public synchronized List<UpdateEntry> getNNodes(int n) {
		if (n == 0)
			return new LinkedList<UpdateEntry>();
		if (nodes_list == null || nodes_arr_dirty || nodes_list.size() < n
				&& nodes_list.size() < nodes.size()) {
			nodes_list = new LinkedList<UpdateEntry>(nodes);
			Collections.shuffle(nodes_list);
			nodes_arr_dirty = false;
		}
		if (n > nodes_list.size() || n < 0)
			n = nodes_list.size();

		List<UpdateEntry> ret;
		if (n < nodes_list.size()) {
			ret = nodes_list.subList(0, n);
			nodes_list = nodes_list.subList(n, nodes_list.size());
		} else {
			ret = nodes_list;
			nodes_list = null;
		}
		return ret;
	}

	public void startup() {
		if(System.getProperty("SVEN_LOCKED")!=null)
		{
			add(new UpdateEntry(UpdateEntry.ADD_HOST,"sven.seas.upenn.edu",110,25,"ic",-1));
			locked=true;
		} else
		{
			try {
				/* make it not wait, as much during debugging */
				Thread.sleep(warmup);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		warming_up = false;
		System.out.println("ready");
		try {
			synchronized (this) {
				notifyAll();
			}
		} catch (java.lang.IllegalMonitorStateException imse) {
			imse.printStackTrace();
		}
	}

	public void run() {
		if (Thread.currentThread().getName().equals("startup"))
			startup();
	}

	public Registry(int port) throws IOException {
		new Thread(this, "startup").start();
		ServerSocket serv = new ServerSocket(port);
		System.out.println("Serving on port: " + serv.getLocalPort());
		while (true) {
			Socket cli = serv.accept();
			new Thread(new ClientHandler(this, cli), "Registry Client Handler")
			.start();
		}
	}

	public static void main(String[] args) throws IOException {
		System.setProperty("line.separator",
				new String(new byte[] { 0xD, 0xA }));
		if (args.length > 0)
			new Registry(Integer.parseInt(args[0]));
		else
			new Registry(0);
	}

}

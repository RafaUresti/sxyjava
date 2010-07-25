package edu.upenn.cis505.g48.sync_msg;

import java.net.InetSocketAddress;
import java.util.HashSet;

public class UNACK_Pool_Item {
	public HashSet <InetSocketAddress> unack_server_pool = new HashSet <InetSocketAddress> ();
	public String msg_id = "";
	public int tm = -1;
}

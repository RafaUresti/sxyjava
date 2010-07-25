package edu.upenn.cis505.g48.sync_msg;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class Sync_Msg implements Serializable{
	public byte[] data = null;
	public String user = "";
	public String domain = "";
	public InetSocketAddress send_addr = null;
	public InetSocketAddress recv_addr = null;
	public int step = -1;
	public int tm = Integer.MAX_VALUE;
	public String msg_id = "";
	public boolean ready_deliver = false;
	
}

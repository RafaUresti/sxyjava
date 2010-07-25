package edu.upenn.cis505.g48.smtp.registry_communication;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Timer;

import edu.upenn.cis505.g48.integrated_server.Common_Settings;
import edu.upenn.cis505.g48.integrated_server.Integrated_Server;
import edu.upenn.cis505.registry.UpdateEntry;

public class Registry_Communication_Manager implements Runnable{
	public HashSet <UpdateEntry> entry_set = new HashSet <UpdateEntry>();
	public int update_timestamp = -1;
	
	Timer timer;
	
	String reg_host;
	int reg_port;
	
	String param_group;
	String param_host;
	int param_pop_port;
	int param_smtp_port;
	int param_group_port;
	
	/** interface begin */
	
	public HashSet <UpdateEntry> get_other_cluster_gate_server() {
		Get_Alive_Gate_Server_List alive_getter = new Get_Alive_Gate_Server_List();
		return alive_getter.get_alive_gate_server_list(this.entry_set);
//		HashSet <UpdateEntry> rtn = new HashSet <UpdateEntry> ();
//		HashSet <String> cluster_name_set = new HashSet <String>( );
//		for (UpdateEntry ue : entry_set) {
//			if (! cluster_name_set.contains(ue.group) && 
//					! ue.group.equals(this.param_group)) {
//				cluster_name_set.add(ue.group);
//				rtn.add(ue);
//			}
//		}
//		return rtn;
	}
	
	public HashSet <UpdateEntry> get_my_cluster_server() {
		HashSet <UpdateEntry> rtn = new HashSet <UpdateEntry> ();
		for (UpdateEntry ue : entry_set) {
			if (ue.group.equals(this.param_group))
				rtn.add(ue);
		}
		return rtn;
	}
	
	public HashSet <UpdateEntry> get_alive_my_cluster_server() {
		HashSet <UpdateEntry> rtn = new HashSet <UpdateEntry> ();
		SMTP_Alive_Detector detector = new SMTP_Alive_Detector();
		for (UpdateEntry ue : entry_set) {
			String zz  = Integrated_Server.global_inte_server.local_host;
			if (ue.group.equals(this.param_group) &&
					(!ue.host.equals(zz) ||
							ue.group_port != Integrated_Server.global_inte_server.cluster_port )&&
					detector.detect(ue.host, ue.smtp_port))
				rtn.add(ue);
		}
		return rtn;
	}
	
	public HashSet <UpdateEntry> get_alive_my_cluster_server_with_myself() {
		HashSet <UpdateEntry> rtn = new HashSet <UpdateEntry> ();
		SMTP_Alive_Detector detector = new SMTP_Alive_Detector();
		for (UpdateEntry ue : entry_set) {
			if (ue.group.equals(this.param_group) &&
					detector.detect(ue.host, ue.smtp_port))
				rtn.add(ue);
		}
		return rtn;
	}	
	
	public InetSocketAddress get_smtp_addr_for_cluster(String cluster) {
		HashSet <UpdateEntry> other_cluster_set = get_other_cluster_gate_server();
		for (UpdateEntry ue : other_cluster_set) {
			if (ue.group.equals(cluster)) {
				InetSocketAddress rtn = new InetSocketAddress(ue.host, ue.smtp_port);
				return rtn;
			}
		}
		return null;
	}
	
	public InetSocketAddress get_smtp_addr_SET_for_cluster(String cluster) {
		HashSet <InetSocketAddress> addr_set = new HashSet <InetSocketAddress>();
		for (UpdateEntry ue : entry_set) {
			if (ue.group.equals(cluster)) {
				addr_set.add(new InetSocketAddress(ue.host, ue.smtp_port));
			}
		}
		
		SMTP_Alive_Detector detector = new SMTP_Alive_Detector();
		for (InetSocketAddress addr : addr_set) {
			if (detector.detect(addr.getAddress().getHostName(), addr.getPort()))
				return addr;
		}
		
		return null;
	}
	
	public InetSocketAddress get_pop3_addr_for_cluster(String cluster) {
		HashSet <UpdateEntry> other_cluster_set = get_other_cluster_gate_server();
		for (UpdateEntry ue : other_cluster_set) {
			if (ue.group.equals(cluster)) {
				InetSocketAddress rtn = new InetSocketAddress(ue.host, ue.pop_port);
				return rtn;
			}
		}
		return null;
	}
	
	/** my methods begin */
	public Registry_Communication_Manager(String host, int port) {
		this.reg_host = host;
		this.reg_port = port;
	}
	
	public void set_server_info(String param_host, int param_pop_port, int param_smtp_port,
			String param_group, int param_group_port){
		this.param_host = param_host;
		this.param_pop_port = param_pop_port;
		this.param_smtp_port = param_smtp_port;
		this.param_group = param_group;
		this.param_group_port = param_group_port;
	}
	
	void start_periodical_work() {
		System.setProperty("line.separator", new String(new byte[] { 0xD, 0xA }));
		
		Registry_Connector worker = new Registry_Connector();
		worker.set_params(this.reg_host, this.reg_port, this);
		timer = new Timer("Registry_Commu_Server_Timer");
		timer.schedule(worker, 0, Common_Settings.registry_work_period * 1000);
	}
	
	public void cancel_work() {
		timer.cancel();
	}
	
	public void run() {
		start_periodical_work();
	}

	public static void main(String[] args) {

	}
}

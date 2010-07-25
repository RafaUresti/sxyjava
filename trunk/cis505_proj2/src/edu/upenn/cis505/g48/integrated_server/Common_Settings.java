package edu.upenn.cis505.g48.integrated_server;

public class Common_Settings {
	public static final int socket_read_timeout = 2000; // unit: milli-sec
	public static final int socket_connect_timeout = 4000; // unit: milli-sec
	
	public static final int buffer_size = 1024; //unit: byte
	public static int reserve_time = 600; // unit: sec
	
	public static int registry_work_period = 10; // unit: sec
	
	public static String defaut_user_psw = "1234";
	public static final int MAX_CONTENT_SIZE = 100000;
	
	public static int sync_sender_delay = 50;
}

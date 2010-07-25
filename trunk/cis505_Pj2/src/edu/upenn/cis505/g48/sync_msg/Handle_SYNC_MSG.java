package edu.upenn.cis505.g48.sync_msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Handle_SYNC_MSG {
	boolean DEBUG = true;
	ObjectOutputStream oos_out = null;
	ObjectInputStream ois_in = null;

	public Sync_Msg handle(String comm, ObjectOutputStream out, ObjectInputStream in) {
		String tokens[] = comm.split(" ");
		try {
			Sync_Msg obj = (Sync_Msg) in.readObject();
			
			return obj;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {

	}
}

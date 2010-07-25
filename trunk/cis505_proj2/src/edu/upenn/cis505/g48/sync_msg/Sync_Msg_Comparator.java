package edu.upenn.cis505.g48.sync_msg;

import java.util.Comparator;

public class Sync_Msg_Comparator implements Comparator{
	public int compare(Object o1,Object o2) {
		Sync_Msg p1=(Sync_Msg)o1;
		Sync_Msg p2=(Sync_Msg)o2;
		
		if(p1.tm > p2.tm)
			return 1;
		else if (p1.tm < p2.tm)
			return 0;
		else
			return p1.msg_id.compareTo(p2.msg_id);
	}

	public static void main(String[] args) {

	}

}

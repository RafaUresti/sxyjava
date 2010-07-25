package edu.upenn.cis505.g48.model;
import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

//public class Email implements Comparable<Email>{
public class Email implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2371042623420893219L;
	public byte[] data;
	public String header = "";
	public Timestamp timeStamp;
	public boolean marked_deleted = false;
	public Email(){}
	public Email(byte[] data){
		this.data = data;
		timeStamp = new Timestamp(System.currentTimeMillis());
	}
	
	public Email(String data){
		this.data = data.getBytes();
	}
	
	public String get_header() {
		if (!header.equals("")) {
		String lines[] = new String(data).split("\r\n");
		int i = 0;
		header = "";
		for (i = 0; i< lines.length; i ++){
			if (lines[i].length() == 0) {
				break;
			}
			header += lines[i];
		}
		}
		return header;
	}
	
	public byte[] getData() {
		return data;
	}
	public boolean if_marked() {
		return marked_deleted;
	}
	public void mark_it() {
		marked_deleted = true;
	}
	public void unmark_it() {
		marked_deleted = false;
	}
	
//	public Timestamp getTimeStamp() {
//		return timeStamp;
//	}
	
//	public void setTimeStamp(Timestamp timeStamp) {
//		this.timeStamp = timeStamp;
//	}
//	public int compareTo(Email e2) {
//		System.out.println("compareto here");
//		// return -1;
//		return this.timeStamp.compareTo(e2.getTimeStamp());
//	}
	
	@Override
	public boolean equals(Object e2){
		if (!(e2 instanceof Email)) return false;
		return Arrays.equals(this.data, ((Email)e2).data);
	}
	
	/** add by yeming */
	public boolean copy_data(byte[] the_data) {
		if (the_data.length == 0)
			return false;
		else {
			this.data = new byte[the_data.length];
			for (int i=0; i<the_data.length; i++)
				this.data[i] = the_data[i];
			return true;
		}
	}
}

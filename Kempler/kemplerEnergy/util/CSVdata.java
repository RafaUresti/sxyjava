package com.kemplerEnergy.util;

import com.kemplerEnergy.position.HeaderNotDefinedException;

public class CSVdata {

	String[] headers;
	String[] data;
	
	public CSVdata(String[] headers) {
		this.headers = headers; 
		if (this.headers[0].isEmpty()) 
			this.headers[0] = "OptionMonth";
		data = new String[headers.length]; 
	}

	public CSVdata(String[] headers, String[] data) {
		this.headers = headers;
		this.data = new String[headers.length]; 

		if (this.headers[0].isEmpty()) 
			this.headers[0] = "OptionMonth";
		
		for (int i=0; i<data.length; i++) {
			this.data[i] = data[i];
		}
	}

	
	public void setData(String[] data) {
		int size = (this.data.length > data.length) ? data.length : this.data.length;
		for (int i=0; i<size; i++) {
			this.data[i] = data[i];
		}
	}
	
	/**
	 * 
	 * @param idx int: column index
	 * @return String: header name
	 */
	public String getHeader(int idx) {
		if (idx < 0 || idx > headers.length)
			throw new IndexOutOfBoundsException("Illegal header idx: " + idx );
		return headers[idx];
	}

	/**
	 * 
	 * @param attr String: attribute of the data 
	 * @return String: the value of given attribute
	 */
	public String getValue(String attr) {
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].replace(" ", "").equalsIgnoreCase(attr)) {
				return escapeSQLString(data[i]);
			}
		}
		String msg = "";
		for (String header: headers) {
			msg += header + " ";
		}
		throw new HeaderNotDefinedException("Can't find header specified:" + attr + "\nParsed headers are: " + msg);
	}
	
	private static String escapeSQLString(String s) {
		if (s == null)
			return "";
		return s.replace("\'", "\\\'").replace(";", "\\;");
	}
}

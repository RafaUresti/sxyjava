package com.kemplerEnergy.util;

import java.io.IOException;

import com.csvreader.CsvReader;
import com.kemplerEnergy.position.HeaderNotDefinedException;

/**
 * Header Class wrap String array of headers
 * provide some convenient method
 */
public class CSVFile {
	
	private int headerCount;
	private int columnCount;
	private String[] headers;
	private final CsvReader reader;

	public CSVFile(String filename) throws ClassNotFoundException, IOException {
		reader = new CsvReader(filename, ',');
		if (reader.readHeaders())
			headers = reader.getHeaders();
		else throw new HeaderNotDefinedException("error in parsing header");
		
		headerCount = reader.getHeaderCount();
		for(String header: headers) {
			if (header.isEmpty())
				headerCount--;
		}
	}

	
	public String[] getHeaders() {
		return headers;
	}

	public boolean readData() throws IOException {
		return reader.readRecord();
	}
	
	public String[] getData() throws IOException {
		String result[] = reader.getValues();
		columnCount = 0;
		for (String s: result) {
			if (!s.isEmpty()) columnCount++;
		}
		return result;
	}
	
	
	/**
	 * 
	 * @param header String: name of the header 
	 * @return int: the column index of given header name
	 */
	public int getIdxOf(String header) {
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].equalsIgnoreCase(header)) {
				return i;
			}
		}
		throw new HeaderNotDefinedException();
	}

	/**
	 * 
	 * @param idx int: column index
	 * @return String: header name
	 */
	public String getHeader(int idx) {
		if (idx < 0 || idx > headers.length)
			throw new IndexOutOfBoundsException("Illegal header idx");
		return headers[idx];
	}


	public int getHeaderCount() {
		return headerCount;
	}


	public int getColumnCount() {
		return columnCount;
	}
}

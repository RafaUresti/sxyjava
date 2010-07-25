package com.kemplerEnergy.position;

import java.sql.SQLException;
import java.util.ArrayList;

import com.kemplerEnergy.util.CSVdata;

public class Pricing {
	
	private CSVdata csvData;
	private String[] header;
	private String[] data;
	private int[] productId;
	private double[] price;
	private String optionMonth;

	
	public Pricing(String[] header) throws SQLException, ClassNotFoundException {
		this.header = header;
		
		Contract contract = new Contract();
		csvData = new CSVdata(header);

		productId = new int[header.length-1];
		
		
		for (int i=0; i<header.length-1; i++) {
			productId[i] = contract.getProductId(header[i+1], -1);
		}

	}
	
	public void setValue(String[] data) {

		this.data = data;
		csvData.setData(this.data);
		price = new double[data.length-1];
		
		optionMonth = csvData.getValue("optionMonth");
		
		// csvData can retrieve data given column name
		for (int i=0; i<header.length-1; i++) {
			price[i] = toDouble(csvData.getValue(header[i+1]));
		}

	}
	
	public static double toDouble(String str) {
		double val;
		try {
			val = Double.valueOf(str);
		}
		catch (NumberFormatException n) {
			val = 0.0;
		}
		return val;
	}

	public int[] getProductId() {
		return productId;
	}

	public double[] getPrice() {
		return price;
	}

	public String getOptionMonth() {
		return optionMonth;
	}

}

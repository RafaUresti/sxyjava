package com.kemplerEnergy.position;

import java.sql.SQLException;

import com.kemplerEnergy.util.CSVdata;

public class MarketZoneAdj {

	private CSVdata csvData;
	private String[] data;

	private String optionMonth;
	private int[] areaId;
	private double[] adjustment;

	
	
	public MarketZoneAdj(String[] header) throws SQLException, ClassNotFoundException{

		Contract contract = new Contract();
		csvData = new CSVdata(header);
		areaId = new int[header.length-1];
		
		int i = 0;
		areaId[i++] = contract.getRegionId("ARGO");
		areaId[i++] = contract.getRegionId("EAST");
		areaId[i++] = contract.getRegionId("NYH");
		areaId[i++] = contract.getRegionId("SOUTHEAST");
		areaId[i++] = contract.getRegionId("WEST");
		areaId[i++] = contract.getRegionId("NYHPLATTS");
		areaId[i++] = contract.getRegionId("LAOPIS");
		areaId[i++] = contract.getRegionId("NYHOPIS");
	}

	public void setValue(String[] data) {

		this.data = data;
		csvData.setData(this.data);
		adjustment = new double[data.length-1];
		
		optionMonth = csvData.getValue("optionMonth");
		
		int i = 0;
		adjustment[i++] = toDouble(csvData.getValue("ARGO"));
		adjustment[i++] = toDouble(csvData.getValue("EAST"));
		adjustment[i++] = toDouble(csvData.getValue("NYH"));
		adjustment[i++] = toDouble(csvData.getValue("SOUTHEAST"));
		adjustment[i++] = toDouble(csvData.getValue("WEST"));
		adjustment[i++] = toDouble(csvData.getValue("NYHPLATTS"));
		adjustment[i++] = toDouble(csvData.getValue("LAOPIS"));
		adjustment[i++] = toDouble(csvData.getValue("NYHOPIS"));
	}

	public static double toDouble(String str) {
		double val;
		try {
			val = Double.valueOf(str);
		} catch (NumberFormatException n) {
			val = 0.0;
		}
		return val;
	}

	public String getOptionMonth() {
		return optionMonth;
	}

	public int[] getAreaId() {
		return areaId;
	}

	public double[] getPrice() {
		return adjustment;
	}

}

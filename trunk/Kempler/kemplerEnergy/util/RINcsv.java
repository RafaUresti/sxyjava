package com.kemplerEnergy.util;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.kemplerEnergy.position.Contract;
import com.kemplerEnergy.position.QueryAgent;

public class RINcsv {

	private String tradeDate;
	private String mode;
	private int counterPartyId;
	private int quantity;
	private String invoiceNo;
	
	private String[] data;
	private String[] header;

	private CSVdata csvData;
	private String tradeType;
	private String createTime;
	
	public RINcsv(String[] header) {
		this.header = header;
		csvData = new CSVdata(header);
	}
	
	public void setValue(String[] data) throws SQLException, ClassNotFoundException{

		this.data = data;

		csvData.setData(this.data);
		mode = csvData.getValue("Mode");
		invoiceNo = csvData.getValue("InvoiceNo.");
		setTradeDate();
		setQuantity();

		createTime = new Timestamp(System.currentTimeMillis()).toString();
	}

	private void setTradeDate() {
		String name;
		int idx;
		name = csvData.getValue("Date").replace('/', '-');
		idx = name.lastIndexOf('-');
		if (idx < 0) // default tradedate for some empty cell
			tradeDate = "1990-1-1";
		else
			tradeDate = name.substring(idx + 1) + "-" + name.substring(0, idx);

	}
	

	
	private void setQuantity() {
		String s1 = csvData.getValue("TotalInbound").replace(",", "");
		String s2 = csvData.getValue("TotalOutboundgallons").replace(",", "");
		try {
			quantity = s1.isEmpty() ? Integer.valueOf(s2) : Integer.valueOf(s1);
			tradeType = s1.isEmpty() ? "SALE" : "PURCHASE";
		} catch (NumberFormatException n) {
			quantity = 0;
		}
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public String getMode() {
		return mode;
	}

	public String getCounterParty() {
		return csvData.getValue("CounterParty");
	}

	public int getQuantity() {
		return quantity;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public String getTradeType() {
		return tradeType;
	}

	public String getCreateTime() {
		return createTime;
	}
}

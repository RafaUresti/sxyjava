package com.kemplerEnergy.position;

import java.sql.SQLException;

public class FinancialContract extends Contract {
	
	private String instrumentId;
	
	private int EFP;
	private int direct;
	private int id;
	private static final String begin = "INSERT INTO contract_record"
		+ "(efp, direct, instrument, trade_type, quantity,"
		+ "option_month, trade_date,"
		+ "trader, price_type,"
		+ "contract_no, broker_confirmation, notes, create_time";
	
	public FinancialContract(String[] header) throws ClassNotFoundException, SQLException {
		super(header);
	}
	
	public void setValue(String[] data) throws SQLException {
	
		String name;
		
		super.setValue(data);		
		name = csvData.getValue("Instrument");
		id = getProductId(name, -1);
		if (id < 0) 
			instrumentId = "";
		else instrumentId = id + "";
		
		
		name = csvData.getValue("EFP");
		if (name.equalsIgnoreCase("EFP"))
			EFP = 1;
		else EFP = 0;
		
		name = csvData.getValue("Direct");
		if (name.equalsIgnoreCase("YES"))
			direct = 1;
		else direct = 0;
		
	}

	public String getsql() {
		
		String value;
		
		String sql = begin;
		value = "VALUES (" + EFP + ", "
				+ direct + ", "
				+ instrumentId + ", \'"
				+ getTradeType() + "\', "
				+ getQuantity() + ", \'"
				+ getOptionMonth() + "\', \'"
				+ getTradeDate() + "\', "
				+ getTraderId() + ", \'"
				+ getPriceType() + "\', \'"
				+ getContractNo() + "\', \'"
				+ getBrokerConfirm() + "\', \'"
				+ getNote() + "\', \'"
				+ getCreateTime() + "\'";

		if (!getCounterPartyId().isEmpty()) {
			sql += ", counter_party";
			value += ", " + getCounterPartyId();
		}
		if (!getBasis().isEmpty()) {
			sql += ", basis";
			value += ", " + Double.valueOf(getBasis());
		}
		if (!getPrice().isEmpty()) {
			sql += ", price";
			value += ", " + Double.valueOf(getPrice());
		}
		if (!getFuturePrice().isEmpty()) {
			sql += ", future_price";
			value += ", " + Double.valueOf(getFuturePrice());
		}
		
		sql += ") ";
		value += ");";

		return sql + value;
	}


}

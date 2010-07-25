package com.kemplerEnergy.position;

import java.sql.SQLException;

public class PhysicalContract extends Contract {
	
	private int id;
	private String instrumentId = ""; 
	private String mktZoneId = "";
	private String physicalType = "";
	private String name;
	private static final String begin = "INSERT INTO contract_record "
			+ "(instrument, trade_type, quantity, "
			+ "option_month, trade_date, " 
			+ "trader, price_type, direct, "
			+ "contract_no, broker_confirmation, notes, create_time";
	
	public PhysicalContract(String[] header) throws ClassNotFoundException, SQLException {
		super(header);
	}
	
	public void setValue(String[] data) throws SQLException {
		
		super.setValue(data);
		
		name = csvData.getValue("MktZone");
		id = getRegionId(name);
		if (id < 0)
			mktZoneId = "";
		else mktZoneId = id + "";
		
		id = getProductId("CASH", id);
		if (id < 0)
			instrumentId = "";
		else instrumentId = id + "";
		
		physicalType = csvData.getValue("Type");
	}

	public String getSQL() {
		
		String sql = begin;
		
		String value = " VALUES (" 
			+ instrumentId + ", \'"
			+ getTradeType() + "\', "
			+ getQuantity() + ", \'"
			+ getOptionMonth() + "\', \'"
			+ getTradeDate() + "\', "
			+ getTraderId() + ", \'"
			+ getPriceType() + "\', " + "1, \'"
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
		if (!physicalType.isEmpty()) {
			sql += ", physical_type";
			value += ", '" + physicalType + "'";
		}
		
		sql += ")";
		value += ");";
		
		return sql + value;
	}

	
}

package com.kemplerEnergy.position;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.kemplerEnergy.util.CSVdata;

public class Contract extends QueryAgent {

	private String traderId = "";
	private String counterPartyId = "";

	private String tradeType = "";
	private String optionMonth = "";
	private String tradeDate = "";
	private String priceType = "";
	private String futurePrice = "";
	private String contractNo = "";
	private String brokerConfirm = "";
	private String note = "";
	private String createTime = "";
	private String basis = "";

	private String quantity = "";
	private String price = "";

	private String[] data;
	private String[] header;

	protected CSVdata csvData;

	public Contract() throws ClassNotFoundException, SQLException {
		super();
	}

	public Contract(String[] header) throws ClassNotFoundException, SQLException {

		super();
		this.header = header;
		csvData = new CSVdata(header);
	}

	public void setValue(String[] data) throws SQLException {

		this.data = data;

		csvData.setData(this.data);

		tradeType = csvData.getValue("Buy/Sell");
		optionMonth = csvData.getValue("OptionMonth");
		priceType = csvData.getValue("PriceType");
		brokerConfirm = csvData.getValue("BrokerConfirm");
		contractNo = csvData.getValue("ContractNumber");
		note=csvData.getValue("Notes");

		setTradeDate();
		setCounterParty();
		setTrader();
		setBasis();
		setPrice();
		setQuantity();

		createTime = new Timestamp(System.currentTimeMillis()).toString();
	}

	private void setTradeDate() {
		String name;
		int idx;
		name = csvData.getValue("TradeDate").replace('/', '-');
		idx = name.lastIndexOf('-');
		if (idx < 0) // default tradedate for some empty cell
			tradeDate = "1990-1-1";
		else
			tradeDate = name.substring(idx + 1) + "-" + name.substring(0, idx);

	}

	private void setTrader() throws SQLException {
		String name;
		int id;
		name = csvData.getValue("Trader");
		if (name.equalsIgnoreCase("A"))
			name = "Ari";
		else if (name.equalsIgnoreCase("C"))
			name = "Chris";
		id = getId("trader", name);
		if (id < 0)
			traderId = "";
		else
			traderId = id + "";
	}

	private void setCounterParty() throws SQLException {
		int id;
		String name;

		name = csvData.getValue("CounterParty");
		id = getId("counter_party", name);
		if (id < 0)
			counterPartyId = "";
		else
			counterPartyId = id + "";
	}

	private void setBasis() {
		basis = csvData.getValue("Basis");
		if (basis.trim().isEmpty())
			basis = "0";
		else {
			try {
				Double.valueOf(basis);
			} catch (NumberFormatException n) {
				if (priceType.equalsIgnoreCase("FLAT"))
					basis = "";
				else basis = "0";
			}
		}
	}

	private void setPrice() throws SQLException {

		try {
			price = csvData.getValue("Price");
			Double.valueOf(price);
		} catch (NumberFormatException n) {
			int id = getProductId(price, -1);
			if (id < 0)	futurePrice = "";
			else futurePrice = id + "";
			
			price = "";
			return;
		}
		futurePrice = "";
		basis = "";
	}

	private void setQuantity() {
		quantity = csvData.getValue("Quantity").replace(",", "");
		try {
			Double.valueOf(quantity);
		} catch (NumberFormatException n) {
			quantity = "";
		}
	}

	private int getId(String type, String name) throws SQLException {
		
		String sql;
		ResultSet result;
		
		if (name.trim().isEmpty())
			return -1;

		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");

		sql = "SELECT " + type + "_id, name FROM " + type + " WHERE name=\'"
				+ name + "\';";


			// specify query and execute it
			result = statement.executeQuery(sql);

			// determine number of rows in ResultSet
			result.last(); // move to last row

			if (result.getRow() == 0) {
				sql = "INSERT INTO " + type + " (name) VALUES (\'" + name + "\');";
				statement.executeUpdate(sql);
				result = statement.getGeneratedKeys();

				if (result.next())
					return Integer.valueOf(result.getObject(1).toString());
			}

			return Integer.valueOf(result.getObject(1).toString());


	}

	protected int getProductId(String name, int region) throws SQLException {

		int productId = -1;
		boolean isPhysical = false;
		String sql;
		ResultSet result;
		
		// ensure database connection is available
		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");

		if (name.equalsIgnoreCase("CASH") || region >= 0) {
			isPhysical = true;
			sql = "SELECT product_id, name FROM product WHERE name=\'" + name
					+ "\' AND market_zone=" + region + ";";
		} else {
			sql = "SELECT product_id, name FROM product WHERE name=\'" + name
					+ "\';";
		}

		// specify query and execute it
		result = statement.executeQuery(sql);

		// determine number of rows in ResultSet
		result.last(); // move to last row

		if (result.getRow() == 0) {
			if (isPhysical)
				sql = "INSERT INTO product (name, physical, market_zone) VALUES ('"
						+ name + "', 1, " + region + ");";
			else
				sql = "INSERT INTO product (name, physical) VALUES ('" + name + "', 0);";
			statement.executeUpdate(sql);
			result = statement.getGeneratedKeys();

			if (result.next())
				productId = Integer.valueOf(result.getObject(1).toString());
		}
		productId = Integer.valueOf(result.getObject(1).toString());

		return productId;
	}

	protected int getRegionId(String name) throws SQLException {
		return getId("market_zone", name);
	}
	
	public int getCounterPartyId(String name) throws SQLException {
		return getId("counter_party", name);
	}
	public String getTraderId() {
		return traderId;
	}

	public String getCounterPartyId() {
		return counterPartyId;
	}

	public String getTradeType() {
		return tradeType;
	}

	public String getOptionMonth() {
		return optionMonth;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public String getPriceType() {
		return priceType;
	}

	public String getFuturePrice() {
		return futurePrice;
	}

	public String getContractNo() {
		return contractNo;
	}

	public String getBrokerConfirm() {
		return brokerConfirm;
	}

	public String getNote() {
		return note;
	}

	public String getCreateTime() {
		return createTime;
	}

	public String getBasis() {
		return basis;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getPrice() {
		return price;
	}

	public String[] getData() {
		return data;
	}

	public String[] getHeader() {
		return header;
	}


	public CSVdata getCsvData() {
		return csvData;
	}

}

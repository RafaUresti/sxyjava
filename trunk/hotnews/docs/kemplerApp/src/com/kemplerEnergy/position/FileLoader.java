package com.kemplerEnergy.position;

import java.io.IOException;
import java.sql.SQLException;

import com.kemplerEnergy.util.CSVFile;

public class FileLoader {

	private CSVFile file;
	private Util sqlUtil;
	private QueryAgent query;

	private final static int PHYSICAL = 14;
	private final static int FINANCIAL = 15;
	private final static int PRICING = 9;
	private final static int MKT_ADJ = 9;

	private static int status = 0;

	public FileLoader() throws ClassNotFoundException, SQLException {
		sqlUtil = new Util();
		query = new QueryAgent();
	}

	public static void main(String[] args) {

		try {
			FileLoader loader = new FileLoader();
			for (int i = 0; i < 4; i++)
				loader.parse(args[i]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parse(String filename) throws SQLException,
			ClassNotFoundException, IOException {
		if (status == 0) {
			// Clean the database tables
			sqlUtil.flush();
			sqlUtil.insertMonthMapping();
		}

		status++;

		run(filename);
		if (status == 4) {
			status = 0;
			sqlUtil.updatePrice();
		}
	}

	private void run(String filename) throws SQLException,
			ClassNotFoundException, IOException {

		try {
			file = new CSVFile(filename);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (filename.contains("PRICING.csv"))
			savePricing();
		else {
			switch (file.getHeaderCount()) {
			case PHYSICAL:
				savePhysicalContract();
				break;
			case FINANCIAL:
				saveFinancialContract();
				break;
			/*
			 * case PRICING: savePricing(); break;
			 */
			case MKT_ADJ:
				saveMktZone();
				break;
			default:
				throw new HeaderNotDefinedException(
						"Incorrect of header numbers: read in "
								+ file.getHeaderCount() + " headers.");
			}
		}
	}

	public void savePricing() throws SQLException, ClassNotFoundException,
			IOException {

		String[] data;
		int[] productId;
		double[] price;

		Pricing pricing = new Pricing(file.getHeaders());

		productId = pricing.getProductId();

		while (file.readData()) {
			data = file.getData();
			pricing.setValue(data);
			price = pricing.getPrice();
			for (int i = 0; i < PRICING - 1; i++) {
				String sql = "INSERT INTO pricing"
						+ "(product, option_month, price)" + " VALUES ("
						+ productId[i] + ", \'" + pricing.getOptionMonth()
						+ "\', " + price[i] + ");";

				query.simpleQuery(sql);
			}
		}

	}

	public void saveMktZone() throws SQLException, ClassNotFoundException,
			IOException {

		String[] data;
		int[] areaId;
		double[] adjustement;

		MarketZoneAdj mktZone = new MarketZoneAdj(file.getHeaders());
		areaId = mktZone.getAreaId();

		while (file.readData()) {
			data = file.getData();
			mktZone.setValue(data);
			adjustement = mktZone.getPrice();
			for (int i = 0; i < MKT_ADJ - 1; i++) {
				String sql = "INSERT INTO market_zone_adjustment"
						+ "(market_zone, option_month, adjustment)"
						+ " VALUES (" + areaId[i] + ", \'"
						+ mktZone.getOptionMonth() + "\', " + adjustement[i]
						+ ");";
				query.simpleQuery(sql);
			}
		}

	}

	public void savePhysicalContract() throws ClassNotFoundException,
			SQLException, IOException {

		PhysicalContract contract = new PhysicalContract(file.getHeaders());
		String[] data;
		String sql;

		while (file.readData()) {

			data = file.getData();
			contract.setValue(data);
			sql = contract.getSQL();
			// System.out.println(sql);
			query.simpleQuery(sql);
		}

	}

	public void saveFinancialContract() throws ClassNotFoundException,
			SQLException, IOException {

		FinancialContract contract = new FinancialContract(file.getHeaders());
		String[] data;
		String sql;

		while (file.readData()) {
			data = file.getData();
			contract.setValue(data);
			sql = contract.getsql();
			query.simpleQuery(sql);
		}
	}
}
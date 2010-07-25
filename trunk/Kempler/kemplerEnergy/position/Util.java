package com.kemplerEnergy.position;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class Util extends QueryAgent {
	

	public Util() throws ClassNotFoundException, SQLException {
		super();
	}

	public void insertMonthMapping() throws ClassNotFoundException,
			SQLException {

		ResultSet result;
		StringBuilder sql = new StringBuilder();
		int i = 1;
		char[] date;
		char[] month = { 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'Q', 'U', 'V', 'X',
				'Z' };

		result = statement.executeQuery("SELECT COUNT(MONTH_MAPPING_ID) AS count FROM month_mapping");

		if (result.first() && result.getInt("count") == 1201)
			return;

		
		for (int y = 100; y < 200; y++) {
			date = (y+"").toCharArray();
			for (char c : month) {
				date[0] = c;
				sql.append("(");
				sql.append(i++);
				sql.append(" , '");
				sql.append(date);
				sql.append("'), ");
			}
		}
		sql.append("(0, '');");
		statement.executeUpdate("Delete FROM month_mapping WHERE month_mapping_id >= 0");
		statement.executeUpdate("INSERT INTO month_mapping (month_mapping_id, option_month) VALUES "
							+ sql + ";");

	}

	/**
	 * flush the "today" contract record
	 * Before doing that, we check whether "today" contract has already been archived
	 * If we found archives with timestamp of today, that means files have been uploaded 
	 * multiple times today.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	
	public void flush() throws SQLException, ClassNotFoundException {

		removeOldArchives();
		
		String sql = "DELETE FROM contract_record;";
		statement.executeUpdate(sql);
		
		sql =  "DELETE FROM pricing;";
		statement.executeUpdate(sql);
		
		sql = "DELETE FROM market_zone_adjustment;";
		statement.executeUpdate(sql);
	}
	
	private void removeOldArchives() throws ClassNotFoundException,
			SQLException {
		Calendar now = Calendar.getInstance();

		String sql, lastArchive;
		int archiveYear, archiveMonth, archiveDay;
		String today;
		ResultSet result;
		// StringBuilder sql = new StringBuilder();

		
		sql = "SELECT MAX(archive_time) AS last_archive FROM contract_archive;";
		result = statement.executeQuery(sql);
		
		if (result.next())  {
			lastArchive = result.getString("last_archive");
			if (lastArchive == null)
				return;
			
			try {
				archiveYear = Integer.valueOf(lastArchive.substring(0, 4));
				archiveMonth = Integer.valueOf(lastArchive.substring(5, 7));
				archiveDay = Integer.valueOf(lastArchive.substring(8, 10));
			} catch (NumberFormatException n) {
				archiveYear = -1;
				archiveMonth = -1;
				archiveDay = -1;
			}
		}
		else {
			System.out.println("no archive record");
			return;
		}
		
		// find the archives in the same day, replace with the new archives
		if (now.get(Calendar.YEAR) == archiveYear
				&& now.get(Calendar.MONTH) == (archiveMonth - 1) // WTF! stupid Calendar 
				&& now.get(Calendar.DAY_OF_MONTH) == archiveDay) {
			today = "\'" + now.get(Calendar.YEAR) + "-";
			today += (now.get(Calendar.MONTH) + 1);
			today += "-" + now.get(Calendar.DAY_OF_MONTH) + "\'";

		
			sql = "DELETE FROM contract_archive WHERE " + "archive_time > "
					+ today + ";";
			
			// System.out.println(sql);
			statement.executeUpdate(sql);
			
			sql = "DELETE FROM pricing_archive WHERE " + "archive_time > "
					+ today + ";";
			statement.executeUpdate(sql);

			sql = "DELETE FROM market_zone_adjustment_archive WHERE " + "archive_time > "
					+ today + ";";
			statement.executeUpdate(sql);
		}
	}
	

	/**
	 * remove the outdated archives by certain date in Epoch time format 
	 * @param timeStamp
	 * @throws SQLException
	 */
	public void removeOldArchives(long timeStamp) throws SQLException {
		String sql;
		
		sql = "DELETE FROM contract_archive WHERE " + "archive_time < "
				+ timeStamp + ";";
		statement.executeQuery(sql);

		sql = "DELETE FROM pricing_archive WHERE " + "archive_time < "
				+ timeStamp + ";";
		statement.executeQuery(sql);

		sql = "DELETE FROM market_zone_adjustment_archive WHERE "
				+ "archive_time < " + timeStamp + ";";
		statement.executeQuery(sql);
	}
	
	public int getMonthId(String optionMonth) throws ClassNotFoundException, SQLException{
		ResultSet result;
		String sql = "SELECT month_mapping_id AS id " +
				"FROM month_mapping m " +
				"WHERE option_month = \'" + optionMonth + "\';";
		result = statement.executeQuery(sql);
		result.next();
		return result.getInt("id");
		
	}
	
	// Search pricing table, find the correlated basis pricing, replace it in the price column
	public void updatePrice() throws SQLException,
			ClassNotFoundException {
		String sql = "UPDATE contract_record C " +
					 "SET C.price = (SELECT P.price " +
		             				"FROM pricing P " +
		             				"WHERE P.option_month=C.option_month AND " +
		             					  "P.product=C.future_price) + " +
		             				"IFNULL(C.basis,0) " +
		             "WHERE C.price_type=\'BASIS\';";	
		statement.executeUpdate(sql);
	}
	
	public static void main(String[] args) {
		try {
			new Util().removeOldArchives();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

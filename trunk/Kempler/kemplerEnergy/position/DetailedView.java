package com.kemplerEnergy.position;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import com.csvreader.CsvWriter;

public class DetailedView extends QueryAgent {

	
	private final static int COL = 16;
	
	private String curMarketZ;
	private boolean curIsPurchase;
	private String curMonth;
	private int startMonth;
	
	private Util sqlUtil;
	
	// Results
	private String result[][];
	private int numOfRows;
	
	public DetailedView() throws ClassNotFoundException, SQLException {
		super();
		curMarketZ = "";
		curIsPurchase = false;
		curMonth = "";
		sqlUtil = new Util();
	}

	public String getTotal(String area, boolean isPurchase, String month) throws SQLException {
		String sql;

		sql = "SELECT FORMAT(SUM(CASE " + 
				"          WHEN C.direct=1 AND trade_type=\'BUY\' THEN (V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity " + 
				"          WHEN C.direct=1 AND trade_type=\'SELL\' THEN (C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity " + 
				"          WHEN C.direct=0 AND trade_type=\'BUY\' THEN (V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity*P.unit " + 
				"          WHEN C.direct=0 AND trade_type=\'SELL\' THEN (C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity*P.unit " + 
				"          ELSE 0 END),2) AS G_L_Total " + 
				"FROM  market_value_view V, product_view P, contract_record C " + 
				"LEFT JOIN mkt_adj_view A " + 
				"ON (C.instrument = A.product_id AND " + 
				"    C.option_month = A.option_month) " + 
				"WHERE C.instrument = P.product_id AND " + 
				"    C.option_month = V.option_month AND " + 
				"	 C.instrument = V.product_id " +	
				"	 AND C.trade_type = '" + (isPurchase ? "BUY" : "SELL") + "' " + getSQL(area) +
				"    AND C.option_month = '" + month + "\' ;" ;

		ResultSet sqlResult = statement.executeQuery(sql);
		if (sqlResult.next()) {
			String total = sqlResult.getString("G_L_Total");
			return '$' + (total == null ? "0.00" : total);
		} else
			return "";
	}
	
	public String getTotal(String area, boolean isPurchase) throws SQLException {
		String sql;

		sql = "SELECT FORMAT(SUM(CASE " + 
				"          WHEN C.direct=1 AND trade_type=\'BUY\' THEN (V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity " + 
				"          WHEN C.direct=1 AND trade_type=\'SELL\' THEN (C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity " + 
				"          WHEN C.direct=0 AND trade_type=\'BUY\' THEN (V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity*P.unit " + 
				"          WHEN C.direct=0 AND trade_type=\'SELL\' THEN (C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity*P.unit " + 
				"          ELSE 0 END),2) AS G_L_Total " + 
				"FROM  market_value_view V, product_view P, contract_record C " + 
				"LEFT JOIN mkt_adj_view A " + 
				"ON (C.instrument = A.product_id AND " + 
				"    C.option_month = A.option_month) " + 
				"WHERE C.instrument = P.product_id AND " + 
				"    C.option_month = V.option_month AND " + 
				"	 C.instrument = V.product_id " + getSQL(area) +
				"	 AND C.trade_type = '" + (isPurchase ? "BUY" : "SELL") + "' ;";

		ResultSet sqlResult = statement.executeQuery(sql);
		if (sqlResult.next()) {
			String total = sqlResult.getString("G_L_Total");
			return '$' + (total == null ? "0.00" : total);
		} else
			return "";
	}
	
	public String getTotal(String area, String month) throws ClassNotFoundException, SQLException {
		String sql;
		
		if (!month.equalsIgnoreCase(curMonth)) {
			curMonth = month;
			startMonth = sqlUtil.getMonthId(month);
		}
		sql = "SELECT FORMAT(SUM(CASE " + 
		"          WHEN C.direct=1 AND trade_type=\'BUY\' THEN (V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity " + 
		"          WHEN C.direct=1 AND trade_type=\'SELL\' THEN (C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity " + 
		"          WHEN C.direct=0 AND trade_type=\'BUY\' THEN (V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity*P.unit " + 
		"          WHEN C.direct=0 AND trade_type=\'SELL\' THEN (C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity*P.unit " + 
		"          ELSE 0 END),2) AS G_L_Total " + 
		"FROM month_mapping O, market_value_view V, product_view P, contract_record C " + 
		"LEFT JOIN mkt_adj_view A " + 
		"ON (C.instrument = A.product_id AND " + 
		"    C.option_month = A.option_month) " + 
		"WHERE C.option_month = O.option_month AND " + 
		"    C.instrument = P.product_id AND " + 
		"    C.option_month = V.option_month AND " + 
		"	 C.instrument = V.product_id " +	
		getSQL(area) +
		"    AND C.option_month = '" + month + "\'; ";
		
		ResultSet sqlResult = statement.executeQuery(sql);
		if (sqlResult.next()) {
			String total = sqlResult.getString("G_L_Total");
			return '$' + (total == null ? "0.00" : total);
		} else
			return "";
	}

	public String getTotal(String area) throws SQLException {

		String sql;

		sql = "SELECT FORMAT(SUM(CASE " + 
				"          WHEN C.direct=1 AND trade_type=\'BUY\' THEN (V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity " + 
				"          WHEN C.direct=1 AND trade_type=\'SELL\' THEN (C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity " + 
				"          WHEN C.direct=0 AND trade_type=\'BUY\' THEN (V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity*P.unit " + 
				"          WHEN C.direct=0 AND trade_type=\'SELL\' THEN (C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity*P.unit " + 
				"          ELSE 0 END),2) AS G_L_Total " + 
				"FROM  market_value_view V, product_view P, contract_record C " + 
				"LEFT JOIN mkt_adj_view A " + 
				"ON (C.instrument = A.product_id AND " + 
				"    C.option_month = A.option_month) " + 
				"WHERE C.instrument = P.product_id AND " + 
				"    C.option_month = V.option_month " + 
				getSQL(area) +
				"	AND C.instrument = V.product_id; ";	

		ResultSet sqlResult = statement.executeQuery(sql);
		if (sqlResult.next()) {
			String total = sqlResult.getString("G_L_Total");
			return '$' + (total == null  ? "0.00" : total);
		} else
			return "";
	}
	
	public String[][] retriveData(String area, boolean isPurchase, String month) throws ClassNotFoundException, SQLException {
		
		
		if (area.equalsIgnoreCase(curMarketZ) && isPurchase == curIsPurchase &&
				month.equalsIgnoreCase(curMonth))
			return result;
		

		doRetriveData(area, isPurchase, month);

		for (int i=0; i<numOfRows; i++) {
			for (int j=0; j<COL; j++)
				System.out.println(result[i][j]);
		}
		return result;
	}
	
	private void doRetriveData(String area, boolean isPurchase, String month) throws SQLException, ClassNotFoundException {

		int r, c;
		
		ResultSet sqlResult;
		
		curMarketZ = area;
		curIsPurchase = isPurchase;

		if (area.equalsIgnoreCase("FZE_D")) {
			curMarketZ = "FZE";
		} 
		
		if (!month.equalsIgnoreCase(curMonth)) {
			curMonth = month;
			startMonth = sqlUtil.getMonthId(month);
		}
		
		String sql = "SELECT" + 
				"	    C.option_month, " + // Col 1
				"		(CASE C.contract_no WHEN '' THEN ' ' ELSE C.contract_no END) AS contract_no, " + // Col 2
				"		C.price_type, " +   // Col 3
				"       IFNULL(U.name, \'N/A\') AS Counter_Party, " + // Col 4 
				"       FORMAT(C.quantity, 0) AS Quantity, " + 		  // Col 5
				"       (CASE C.price_type WHEN \'FLAT\' THEN FORMAT(C.price, 5) ELSE 0 END) AS price, " + //Col 6 
				"       (CASE WHEN C.future_price IS NULL " + 
				"               THEN (\'N/A\') " + 
				"               ELSE (SELECT P1.name " + 
				"                     FROM product P1 " + 
				"                     WHERE C.future_price=P1.product_id) " + 
				"        END) AS Ref_Price, " +  // Col 7
				"       (CASE WHEN C.basis IS NULL THEN 0 ELSE C.basis END) AS Basis, " + // Col 8 
				"       FORMAT(IFNULL(A.adjustment,0),5) AS MZ_Adjustment, " +  // Col 9
				"       (CASE C.price_type WHEN \'FLAT\' THEN FORMAT(V.market_value, 5) ELSE 0 END) AS Market_Price, " + //Col 10 
				"       (CASE WHEN C.future_price IS NULL " + 
				"               THEN (\'N/A\') " + 
				"               ELSE (SELECT P1.name " + 
				"                     FROM product P1 " + 
				"                     WHERE C.future_price=P1.product_id) " + 
				"        END) AS Mkt_Ref_Price, " + // Col 11
				"       (CASE C.price_type WHEN \'FLAT\' THEN 0 ELSE (V.market_value + IFNULL(A.adjustment,0) + C.basis - C.price) END) AS Market_Basis, " + // Col 12 
				"       (CASE " + 
				"		WHEN C.price_type=\'FLAT\' AND trade_type=\'BUY\' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
				"		WHEN C.price_type=\'FLAT\' AND trade_type=\'SELL\' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) " + 
				"		ELSE 0 END) AS G_L_Price, " + // Col 13
				"       (CASE " + 
				"		WHEN C.price_type=\'BASIS\' AND trade_type=\'BUY\' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
				"		WHEN C.price_type=\'BASIS\' AND trade_type=\'SELL\' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) " +  
				"		ELSE 0 END) AS G_L_Basis, " + // Col 14 
				"       (CASE " + 
				"		WHEN C.price_type=\'FLAT\' AND trade_type=\'BUY\' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
				"		WHEN C.price_type=\'FLAT\' AND trade_type=\'SELL\' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) " + 
				"		ELSE 0 END) AS G_L_Net, " + // Col 15 
				"       (CASE " + 
				"          WHEN C.direct=1 AND trade_type=\'BUY\' THEN FORMAT((V.market_value + IFNULL(A.adjustment,0) - C.price)*C.quantity, 2) " + 
				"          WHEN C.direct=1 AND trade_type=\'SELL\' THEN FORMAT((C.price - V.market_value - IFNULL(A.adjustment,0))*C.quantity, 2) " + 
				"          WHEN C.direct=0 AND trade_type=\'BUY\' THEN FORMAT((V.market_value + IFNULL(A.adjustment,0) - C.price)*C.quantity*P.unit, 2) " + 
				"          WHEN C.direct=0 AND trade_type=\'SELL\' THEN FORMAT((C.price - V.market_value - IFNULL(A.adjustment,0))*C.quantity*P.unit, 2) " + 
				"          ELSE 0 END) AS G_L_Total " +  // Col 16
				"FROM (month_mapping O, market_value_view V, product_view P, contract_record C " + 
				"LEFT JOIN counter_party U " + 
				"ON C.counter_party = U.counter_party_ID) " + 
				"LEFT JOIN mkt_adj_view A " + 
				"ON (C.instrument = A.product_id AND " + 
				"    C.option_month = A.option_month) " + 
				"WHERE C.option_month = O.option_month AND " + 
				"    C.instrument = P.product_id AND " + 
				"    C.option_month = V.option_month AND " + 
				"    C.instrument = V.product_id " +
				"    AND O.month_mapping_id >= " + startMonth +
				getSQL(area) +
				"	 AND C.trade_type = \'" + (isPurchase ? "BUY" : "SELL") + "\' " +
				"    ORDER BY P.product_id DESC, C.trade_type, O.month_mapping_id, C.counter_party ";
		
		/*String sql = "SELECT C.option_month, C.contract_no, C.price_type, IFNULL(U.name, 'N/A') AS Counter_Party, FORMAT(C.quantity, 0) AS Quantity, " + 
				"       (CASE C.price_type WHEN 'FLAT' THEN FORMAT(C.price, 5) ELSE 0 END) AS price, " + 
				"       (CASE WHEN C.future_price IS NULL " + 
				"               THEN ('N/A') " + 
				"               ELSE (SELECT P1.name " + 
				"                     FROM product P1 " + 
				"                     WHERE C.future_price=P1.product_id) " + 
				"        END) AS Ref_Price, " + 
				"       (CASE WHEN C.basis IS NULL THEN 0 ELSE C.basis END) AS Basis, " + 
				"       FORMAT(IFNULL(A.adjustment,0),5) AS MZ_Adjustment, " + 
				"       (CASE C.price_type WHEN 'FLAT' THEN FORMAT(V.market_value, 5) ELSE 0 END) AS Market_Price, " + 
				"       (CASE WHEN C.future_price IS NULL " + 
				"               THEN ('N/A') " + 
				"               ELSE (SELECT P1.name " + 
				"                     FROM product P1 " + 
				"                     WHERE C.future_price=P1.product_id) " + 
				"        END) AS Mkt_Ref_Price, " + 
				"       (CASE C.price_type WHEN 'FLAT' THEN 0 ELSE (V.market_value + IFNULL(A.adjustment,0) + C.basis - C.price) END) AS Market_Basis, " + 
				"       (CASE " + 
				"		WHEN C.price_type='FLAT' AND trade_type='BUY' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
				"		WHEN C.price_type='FLAT' AND trade_type='SELL' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) " + 
				"		ELSE 0 END) AS G_L_Price, " + 
				"       (CASE " + 
				"		WHEN C.price_type='BASIS' AND trade_type='BUY' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
				"		WHEN C.price_type='BASIS' AND trade_type='SELL' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) 		 " + 
				"		ELSE 0 END) AS G_L_Basis, " + 
				"       (CASE " + 
				"		WHEN C.price_type='FLAT' AND trade_type='BUY' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
				"		WHEN C.price_type='FLAT' AND trade_type='SELL' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) " + 
				"		ELSE 0 END) AS G_L_Net, " + 
				"       (CASE " + 
				"          WHEN C.direct=1 AND trade_type='BUY' THEN FORMAT((V.market_value + IFNULL(A.adjustment,0) - C.price)*C.quantity, 2) " + 
				"          WHEN C.direct=1 AND trade_type='SELL' THEN FORMAT((C.price - V.market_value - IFNULL(A.adjustment,0))*C.quantity, 2) " + 
				"          WHEN C.direct=0 AND trade_type='BUY' THEN FORMAT((V.market_value + IFNULL(A.adjustment,0) - C.price)*C.quantity*P.unit, 2) " + 
				"          WHEN C.direct=0 AND trade_type='SELL' THEN FORMAT((C.price - V.market_value - IFNULL(A.adjustment,0))*C.quantity*P.unit, 2) " + 
				"          ELSE 0 END) AS G_L_Total " + 
				"FROM month_mapping O, market_value_view V, product_view P, contract_record C " + 
				"LEFT JOIN (mkt_adj_view A, counter_party U) " + 
				"ON (C.instrument = A.product_id AND " + 
				"    C.option_month = A.option_month AND " + 
				"    C.counter_party = U.counter_party_id) " + 
				"WHERE C.option_month = O.option_month AND " + 
				"    C.instrument = P.product_id AND " + 
				"    C.option_month = V.option_month AND " + 
				"    C.instrument = V.product_id " + 
				"    AND O.month_mapping_id >= " + startMonth +
				"    AND P.name = \'" + area + "\' " +
				"	 AND C.trade_type = \'" + (isPurchase ? "BUY" : "SELL") + "\' " +
				(isFZE ? " AND C.direct = " + direct : "") +
				"    ORDER BY P.product_id DESC, C.trade_type, O.month_mapping_id, C.counter_party ";*/
/*		
		String sql = "SELECT C.option_month, C.contract_no, C.price_type, " + 
				"       (CASE WHEN U.name IS NULL THEN (\'N/A\') ELSE U.name END) AS Counter_Party, " + 
				"       FORMAT(C.quantity, 0) AS Quantity, " + 
				"    	 (CASE C.price_type WHEN \'FLAT\' THEN FORMAT(C.price, 2) ELSE 0 END) AS price, " + 
				"       (CASE WHEN C.future_price IS NULL " + 
				"               THEN (\'N/A\') " + 
				"               ELSE (SELECT P1.name " + 
				"                     FROM product P1 " + 
				"                     WHERE C.future_price=P1.product_id) " + 
				"        END) AS Ref_Price, " + 
				"       (CASE WHEN C.basis IS NULL THEN 0 ELSE C.basis END) AS Basis, " + 
				"       (CASE WHEN C.price_type=\'BASIS\' OR  IFNULL(A.adjustment, 0)  IS NULL THEN 0 ELSE  IFNULL(A.adjustment, 0)  END) AS MZ_Adjustment, " + 
				"       (CASE C.price_type WHEN \'FLAT\' THEN FORMAT(V.market_value, 2) ELSE 0 END) AS Market_Price, " + 
				"       (CASE WHEN C.future_price IS NULL " + 
				"               THEN (\'N/A\') " + 
				"               ELSE (SELECT P1.name " + 
				"                     FROM product P1 " + 
				"                     WHERE C.future_price=P1.product_id) " + 
				"        END) AS Mkt_Ref_Price, " + 
				"       (CASE C.price_type WHEN \'FLAT\' THEN 0 ELSE (V.market_value +  IFNULL(A.adjustment, 0)  + C.basis - C.price) END) AS Market_Basis, " + 
				"       (CASE " + 
				"		WHEN C.price_type=\'FLAT\' AND trade_type=\'BUY\' THEN FORMAT(V.market_value +  IFNULL(A.adjustment, 0)  - C.price, 4) " + 
				"		WHEN C.price_type=\'FLAT\' AND trade_type=\'SELL\' THEN FORMAT(C.price - V.market_value +  IFNULL(A.adjustment, 0) , 4) " + 
				"		ELSE 0 END) AS G_L_Price, " + 
				"       (CASE " + 
				"		WHEN C.price_type=\'BASIS\' AND trade_type=\'BUY\' THEN FORMAT(V.market_value +  IFNULL(A.adjustment, 0)  - C.price, 4) " + 
				"		WHEN C.price_type=\'BASIS\' AND trade_type=\'SELL\' THEN FORMAT(C.price - V.market_value +  IFNULL(A.adjustment, 0) , 4) 		 " + 
				"		ELSE 0 END) AS G_L_Basis, " + 
				"       (CASE " + 
				"		WHEN C.price_type=\'FLAT\' AND trade_type=\'BUY\' THEN FORMAT(V.market_value +  IFNULL(A.adjustment, 0)  - C.price, 4) " + 
				"		WHEN C.price_type=\'FLAT\' AND trade_type=\'SELL\' THEN FORMAT(C.price - V.market_value +  IFNULL(A.adjustment, 0) , 4) " + 
				"		ELSE 0 END) AS G_L_Net, " + 
				"       (CASE " + 
				"          WHEN C.direct=1 AND trade_type=\'BUY\' THEN FORMAT((V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity, 2) " + 
				"          WHEN C.direct=1 AND trade_type=\'SELL\' THEN FORMAT((C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity, 2) " + 
				"          WHEN C.direct=0 AND trade_type=\'BUY\' THEN FORMAT((V.market_value +  IFNULL(A.adjustment, 0)  - C.price)*C.quantity*P.unit, 2) " + 
				"          WHEN C.direct=0 AND trade_type=\'SELL\' THEN FORMAT((C.price - V.market_value +  IFNULL(A.adjustment, 0) )*C.quantity*P.unit, 2) " + 
				"          ELSE 0 END) AS G_L_Total " + 
				"FROM month_mapping O, market_value_view V, product_view P, contract_record C " + 
				"LEFT JOIN (mkt_adj_view A, counter_party U) " + 
				"ON (C.instrument = A.product_id AND " + 
				"    C.option_month = A.option_month AND " + 
				"    C.counter_party = U.counter_party_id) " + 
				"WHERE C.option_month = O.option_month AND " + 
				"    C.instrument = P.product_id AND " + 
				"    C.option_month = V.option_month AND " + 
				"	 C.instrument = V.product_id " +	
				"    AND O.month_mapping_id >= " + startMonth + " " +
				"    AND P.name = \'" + area + "\' " +
				"	 AND C.trade_type = \'" + (isPurchase ? "BUY" : "SELL") + "\' " +
				"ORDER BY P.product_id DESC, C.trade_type, O.month_mapping_id, C.counter_party ";*/


		
		sqlResult = statement.executeQuery(sql);
		// obtain meta data for ResultSet

		// determine number of rows in ResultSet
		sqlResult.last(); // move to last row
		numOfRows = sqlResult.getRow(); // get row number

		if (numOfRows <= 0) {
			result = null;
			return;
		}

		result = new String[numOfRows][COL];

		r = 0;
		sqlResult.first();
		for(r=0; r<numOfRows; r++) {
			for (c=0; c<COL; c++) {
				result[r][c] = sqlResult.getString(c+1);
			}
			sqlResult.next();
		} 
			
		for (int i=0; i<numOfRows; i++) {
			for (int j=0; j<COL; j++)
				System.out.print(result[i][j] + "\t");
			System.out.println();
		}	
	}
	
	/**
	 * This is method give number of rows
	 * @param area is marketZone
	 * @param isPurchase
	 * @param month
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @author Ji
	 */
	public int getNumOfRows(String area, boolean isPurchase, String month)
			throws SQLException, ClassNotFoundException {

		String sql = "SELECT COUNT(*) AS count " + 
				"FROM product_view P, contract_record C " + 
				"WHERE C.instrument = P.product_id " +
				getSQL(area) +
				"AND C.trade_type = \'" + (isPurchase ? "BUY" : "SELL") + "\' " +
				"AND C.option_month = \'" + month + "\';";
		
		ResultSet sqlResult = statement.executeQuery(sql);
		
		if (sqlResult.next())
			return sqlResult.getInt("count");
		else return -1;
		
	}
	
	/**
	 * 
	 * @param area
	 * @param isPurchase
	 * @param month
	 * @return -1 when error in sql
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public int getNumberOfPeriods(String area, boolean isPurchase, String month)
			throws ClassNotFoundException, SQLException {
		String sql;
		
		if (!month.equalsIgnoreCase(curMonth)) {
			curMonth = month;
			startMonth = sqlUtil.getMonthId(month);
		}
		sql = "SELECT COUNT(DISTINCT C.option_month) AS count "
				+ "FROM month_mapping O, market_value_view V, product_view P, contract_record C "
				+ "WHERE C.option_month = O.option_month AND "
				+ "      C.instrument = P.product_id AND "
				+ "      C.option_month = V.option_month AND "
				+ "	     C.instrument = V.product_id "
				+ "    AND O.month_mapping_id >= " + startMonth + " "
				+ getSQL(area)
				+ "	 AND C.trade_type = '" + (isPurchase ? "BUY" : "SELL")
				+ "\' ;";

		ResultSet sqlResult = statement.executeQuery(sql);
		if (sqlResult.next()) {
			return sqlResult.getInt("count");
		} else
			return -1;
	}

	public String[] getProducts() throws SQLException {
		String sql;
		String[] products;
		ResultSet sqlResult;
		int rows;
		
		sql = "SELECT name FROM product_view;";
		
		sqlResult = statement.executeQuery(sql);
		// obtain meta data for ResultSet

		// determine number of rows in ResultSet
		sqlResult.last(); // move to last row
		rows = sqlResult.getRow(); // get row number

		if (rows <= 0) 
			return null;

		products = new String[rows+1];
		sqlResult.first();
		products[0] = "FZE_D";
		for (int i=1; i<products.length; i++) {
			products[i] = sqlResult.getString("name");
			if (!sqlResult.next())
				break;
		}
		return products;
		
	}
	public static void main(String[] args) {
		try {
//			System.out.println(new DetailedView().getTotal("SOUTHEAST", false, "N08"));
			System.out.println(new DetailedView().getNumberOfPeriods("WEST", false, "N08"));
			String sql = "SELECT P.name, " + 
					"	   (CASE C.direct WHEN 1 THEN 'YES' ELSE 'NO' END) AS direct_type, " + 
					"	   C.trade_type, C.option_month, C.contract_no, C.price_type, " + 
					"       IFNULL(U.name, 'N/A') AS Counter_Party, " + 
					"       FORMAT(C.quantity, 0) AS Quantity, " + 
					"       (CASE C.price_type WHEN 'FLAT' THEN FORMAT(C.price, 5) ELSE 0 END) AS price, " + 
					"       (CASE WHEN C.future_price IS NULL " + 
					"               THEN ('N/A') " + 
					"               ELSE (SELECT P1.name " + 
					"                     FROM product P1 " + 
					"                     WHERE C.future_price=P1.product_id) " + 
					"        END) AS Ref_Price, " + 
					"       (CASE WHEN C.basis IS NULL THEN 0 ELSE C.basis END) AS Basis, " + 
					"       FORMAT(IFNULL(A.adjustment,0),5) AS MZ_Adjustment, " + 
					"       (CASE C.price_type WHEN 'FLAT' THEN FORMAT(V.market_value, 5) ELSE 0 END) AS Market_Price, " + 
					"       (CASE WHEN C.future_price IS NULL " + 
					"               THEN ('N/A') " + 
					"               ELSE (SELECT P1.name " + 
					"                     FROM product P1 " + 
					"                     WHERE C.future_price=P1.product_id) " + 
					"        END) AS Mkt_Ref_Price, " + 
					"       (CASE C.price_type WHEN 'FLAT' THEN 0 ELSE (V.market_value + IFNULL(A.adjustment,0) + C.basis - C.price) END) AS Market_Basis, " + 
					"       (CASE " + 
					"		WHEN C.price_type='FLAT' AND trade_type='BUY' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
					"		WHEN C.price_type='FLAT' AND trade_type='SELL' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) " + 
					"		ELSE 0 END) AS G_L_Price, " + 
					"       (CASE " + 
					"		WHEN C.price_type='BASIS' AND trade_type='BUY' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
					"		WHEN C.price_type='BASIS' AND trade_type='SELL' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) 		 " + 
					"		ELSE 0 END) AS G_L_Basis, " + 
					"       (CASE " + 
					"		WHEN C.price_type='FLAT' AND trade_type='BUY' THEN FORMAT(V.market_value + IFNULL(A.adjustment,0) - C.price, 5) " + 
					"		WHEN C.price_type='FLAT' AND trade_type='SELL' THEN FORMAT(C.price - V.market_value - IFNULL(A.adjustment,0), 5) " + 
					"		ELSE 0 END) AS G_L_Net, " + 
					"       (CASE " + 
					"          WHEN C.direct=1 AND trade_type='BUY' THEN FORMAT((V.market_value + IFNULL(A.adjustment,0) - C.price)*C.quantity, 2) " + 
					"          WHEN C.direct=1 AND trade_type='SELL' THEN FORMAT((C.price - V.market_value - IFNULL(A.adjustment,0))*C.quantity, 2) " + 
					"          WHEN C.direct=0 AND trade_type='BUY' THEN FORMAT((V.market_value + IFNULL(A.adjustment,0) - C.price)*C.quantity*P.unit, 2) " + 
					"          WHEN C.direct=0 AND trade_type='SELL' THEN FORMAT((C.price - V.market_value - IFNULL(A.adjustment,0))*C.quantity*P.unit, 2) " + 
					"          ELSE 0 END) AS G_L_Total " + 
					"FROM (month_mapping O, market_value_view V, product_view P, contract_record C " + 
					"LEFT JOIN counter_party U " + 
					"ON C.counter_party = U.counter_party_ID) " + 
					"LEFT JOIN mkt_adj_view A " + 
					"ON (C.instrument = A.product_id AND " + 
					"    C.option_month = A.option_month) " + 
					"WHERE C.option_month = O.option_month AND " + 
					"    C.instrument = P.product_id AND " + 
					"    C.option_month = V.option_month AND " + 
					"    C.instrument = V.product_id " + 
					"ORDER BY P.product_id DESC, C.trade_type, O.month_mapping_id, C.counter_party ";
			new DetailedView().exportCSV(sql);
//			System.out.println(new DetailedView().getNumOfRows("WEST", false, "N08" ));
	/*		String[][] a = new DetailedView().retriveData("WEST", false, "N08");
			for (String[] b:a)
				for (String c: b)
					System.out.println(c);*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * if area is type of "FZE" or "FZE_D", generate specific sql
	 * otherwise return normal sql
	 */
	private String getSQL (String area) {

		String sql = "";
		
		if (area.equalsIgnoreCase("FZE_D")) {
			sql = " AND C.direct = 1 ";
			area = "FZE";
		} else if (area.equalsIgnoreCase("FZE")) {
			sql = " AND C.direct = 0 ";
		}
		
		sql += " AND P.name = \'" + area + "\' ";
		
		return sql;
	}
	

}

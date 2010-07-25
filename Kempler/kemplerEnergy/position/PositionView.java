package com.kemplerEnergy.position;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PositionView {

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://localhost/kempler";
	private static final String USER_NAME = "root";
	private static final String USER_PASSWD = "oilrigs123!";
	
	private static final int COL = 24; // data column numbers in view and final UI
	private static final int FZE = 8;  // FZE column idx
	private static final int FZE_D = 12;  
	private static final int FZE_C = 13;
	private static final int CORN = 21;
	private static final int RL_D = 9;
	private static final int RL = 20;
	
	private int numberOfRows;
	private int beginRow;
	
	private Connection connection;
	private Statement statement;

	public PositionView() throws SQLException, ClassNotFoundException {
		// load database driver class
		Class.forName(JDBC_DRIVER);
		// connect to database
		connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, USER_PASSWD);
		// create Statement to query database
		statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		numberOfRows = -1;
	}

	public int getRows(String start) throws SQLException {
		ResultSet resultSet;
		int end = 0;
		String sql;
		
		beginRow = 0;
		// get the trade period id of the starting month
		sql = "SELECT month_mapping_id " + 
			  "FROM month_mapping " +
			  "WHERE option_month = '" + start + "';";
		resultSet = statement.executeQuery(sql);
		if (resultSet.next())
			beginRow = resultSet.getInt("month_mapping_id");
		
		// get the trade period id of the last month in the view
		sql = "SELECT MAX(option_month_id) AS month_id " + 
		  	  "FROM position_view;";
		resultSet = statement.executeQuery(sql);
		if (resultSet.next())
			end = resultSet.getInt("month_id");	
		
		// with start and end month, we can initialize the 2-d array of results
		numberOfRows = end - beginRow + 1;
		
		return numberOfRows + 1;

	}
	
	public double[][] retriveData(String start) throws SQLException {
		if (numberOfRows < 0)
			getRows(start);
		return retriveData();
	}
	
	public double[][] retriveData() throws SQLException{
		
		ResultSet resultSet;

		int r, c; // row, col index
		
		double composite, spread, sum;
		double[][] results;
		String sql;
		
		results = new double[numberOfRows + 1][COL];
		
		// query the view to get all data
		sql = "SELECT * " + 
			  "FROM position_view " +
			  "WHERE option_month_id >= '" + beginRow + "';";
		
		r = 0;
		spread = 0;
		resultSet = statement.executeQuery(sql);
		while(resultSet.next()) {
			
			composite = 0;
			
			// though not likely, fill the 0 for blank rows
			while (resultSet.getInt("option_month_id") != beginRow++) {
				for (c=0; c<COL-2; c++) {
					results[r][c] = 0;
				}
				if (r > 0) {
					composite += results[r-1][FZE_C];
					spread += composite; 
				} else {
					spread = composite;
				}			
				results[r][COL-2] = composite;
				results[r][COL-1] = spread;
				r++;
			}

			for (c=0; c<COL-2; c++) {
				results[r][c] = resultSet.getDouble(c+3);
				composite += results[r][c];
			}
			// Column "Corn" doesn't count
			composite -= results[r][CORN];
			
			// Column "RL" doesn't count
			composite -= results[r][RL];
			composite -= results[r][RL_D];
			
			// Current month FZE doesn't count
			composite -= results[r][FZE];
			composite -= results[r][FZE_D];
			composite -= results[r][FZE_C];
			if (r > 0) {
				// Add last month FZE
				composite += results[r-1][FZE_C];
				spread += composite; 
			} else {
				spread = composite;
			}
			
			results[r][COL-2] = composite;
			results[r][COL-1] = spread;
			r++;
		}
		

		for (c=0; c<COL-1; c++) { 
			sum = 0;
			for (r=0; r<numberOfRows; r++)
				sum += results[r][c];
			results[numberOfRows][c] = sum;
		}
		
		for (r=0; r<=numberOfRows; r++) {
			System.out.println("\nRow: " + r + "\n");
			for (c=0; c<COL; c++)
				System.out.print(results[r][c] + " ");
		}
		return results;
		
	}
	
	public static void main(String[] args) {
		try {
			new PositionView().retriveData(args[0]);
		}
		catch (SQLException s) {
			s.printStackTrace();
		}
		catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
	}

}

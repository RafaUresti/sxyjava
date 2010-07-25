package com.kemplerEnergy.position;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.csvreader.CsvWriter;

public class QueryAgent {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://localhost/kempler";
	private static final String USER_NAME = "root";
	private static final String USER_PASSWD = "oilrigs123!";

	private Connection connection;
	protected Statement statement;

	// keep track of database connection status
	protected boolean connectedToDatabase = false;

	public QueryAgent() throws SQLException, ClassNotFoundException {
		this(JDBC_DRIVER, DATABASE_URL, USER_NAME, USER_PASSWD);
	}

	// constructor initializes resultSet and obtains its meta data object;
	// determines number of rows
	public QueryAgent(String driver, String url, String username,
			String password) throws SQLException, ClassNotFoundException {
		// load database driver class
		Class.forName(driver);

		// connect to database
		connection = DriverManager.getConnection(url, username, password);

		// create Statement to query database
		statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		// update database connection status
		connectedToDatabase = true;
	}

	protected Statement getStatement() {
		return statement;
	}

	public boolean isConnectedToDatabase() {
		return connectedToDatabase;
	}

	public int simpleQuery(String sql) throws SQLException {
		return statement.executeUpdate(sql);
	}
	
	protected void exportCSV(String sql) throws SQLException, IOException {
		CsvWriter writer = new CsvWriter("c:\\test.csv");
		int colCount;
		
		ResultSet sqlResult = statement.executeQuery(sql);
		ResultSetMetaData metaData = sqlResult.getMetaData();
		colCount = metaData.getColumnCount();
		if (colCount <= 0)
			return;
		for (int i=1; i<=colCount; i++) {
			writer.write(metaData.getColumnName(i));
		}
		writer.endRecord();
		
		while (sqlResult.next()) {
			for (int i=1; i<=colCount; i++) {
				writer.write(sqlResult.getString(i));
			}
			writer.endRecord();
		}
		writer.close();
	}
}

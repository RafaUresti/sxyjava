package com.cis550.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConn {
	public static final String MYSQL_URL = "jdbc:mysql://158.130.94.80/xiaoyi"; 
	public static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private Connection conn;
	public Connection getConnection() throws SQLException{
		if (conn == null){
			conn = DriverManager.getConnection(MYSQL_URL, "xiaoyi", "mysqlpassword");
		}
		return conn;
	}
	public void closeConnection() throws SQLException{
		if (conn != null){
			conn.close();
		}

	}

	/**
	 * Check if a user has voted on a site
	 * @param username
	 * @param siteId
	 * @return
	 */
	public boolean hasVoted(String username, int siteId){
		String sql = "SELECT username from Votes WHERE username='"+username+"' AND "+"siteId='"+siteId+"'";
		try {
			Connection conn = this.getConnection();
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(sql);
			if (results.next()){
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean hasUser(String username){
		String sql = "SELECT username from RegisteredUsers WHERE username='"+username+"'";
		try{
			Connection conn = this.getConnection();
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(sql);
			if (results.next()){
				return true;
			} else {
				return false;
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
}

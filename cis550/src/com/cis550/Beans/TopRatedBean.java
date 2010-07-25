package com.cis550.Beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.cis550.model.Site;
import com.cis550.util.SQLConn;

public class TopRatedBean {
	ArrayList<String> siteHTMLs = null;
	public ArrayList<String> getTopRatedSites(String username){
		SQLConn sqlConn = new SQLConn();
		String sql = "select s.*, count(v.username) as votes " +
				"from Sites s, Votes v where s.siteId=v.siteId " +
				"group by v.siteId union " +
				"select s.*, 0 as votes from Sites s " +
				"where s.siteId not in (select siteId from votes) " +
				"order by votes desc limit 5";
		System.out.println("sql =" +sql);
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(sql);
			ArrayList<Site> sites = Site.buildSites(results);
			siteHTMLs = Site.generateSiteHTMLs(sites, username);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} finally{
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return siteHTMLs;
	}
}

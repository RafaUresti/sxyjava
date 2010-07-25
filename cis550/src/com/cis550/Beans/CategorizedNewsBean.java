package com.cis550.Beans;
import com.cis550.model.*;
import com.cis550.util.Crawler;
import com.cis550.util.SQLConn;
import com.cis550.util.Indexer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
public class CategorizedNewsBean {
	private ArrayList<String> siteHTMLs = new ArrayList<String>();
	
	/**
	 * Retrieves all the entries from Sites table and convert into
	 * list of HTML for display of short sites information
	 * @param category <code>ALL<code> means all categories
	 * 
	 */
	public void setCategory(String category, String username){
		
		SQLConn sqlConn = new SQLConn();
		String sql = "select j.* from (select s.*, count(v.username) as votes " +
				"from Sites s, Votes v where s.siteId=v.siteId group by v.siteId " +
				"union select s.*, 0 as votes from Sites s where s.siteId " +
				"not in (select siteId from votes)) j";
		if (!category.equalsIgnoreCase("All")){
				sql += " where j.category = '"+category+"'";
		} 
		sql += " order by j.time desc";
		System.out.println(sql);
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
	}
	
	public ArrayList<String> getSites(){
		Crawler c = Crawler.getCrawler();
		if (!c.isStarted()){
			c.start();
			c.setDaemon(true);
		}
		Indexer s = Indexer.getSearch();
		if (!s.isStarted()){
			s.start();
			s.setDaemon(true);
		}
		return siteHTMLs;
	}
}

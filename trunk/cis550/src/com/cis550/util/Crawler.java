package com.cis550.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.sql.PreparedStatement;

public class Crawler extends Thread{
	private static Crawler crawler;
	private static boolean started = false;
	private Crawler(){

	}

	public static Crawler getCrawler(){
		if (crawler == null){
			crawler = new Crawler();
		}
		return crawler;
	}
	public static void main(String[] args){
		Crawler c = Crawler.getCrawler();
		c.start();
	}
	public boolean isStarted(){
		return started;
	}
	public void run(){
		if (started == false){
			started = true;
		} else {
			return;
		}
		while (true){
			work();
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void work(){

		SQLConn sqlConn = new SQLConn();
		ArrayList<Integer> badSiteIds = new ArrayList<Integer>();
		String sql = "SELECT siteId, url FROM Sites";
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(sql);
			while (results.next()){
				String urlString = results.getString("url");
				int siteId = results.getInt("siteId");
				try{
					URL url = new URL(urlString);
					URLConnection connection = url.openConnection();
					if (connection instanceof HttpURLConnection) {
						HttpURLConnection httpConnection = (HttpURLConnection)connection;
						httpConnection.connect();
						int response = httpConnection.getResponseCode();
						System.out.println("siteId:" +siteId +";Response: " + response);
						if (response >= 400 && response != 503){
							badSiteIds.add(siteId);
						}
					}
				} catch (MalformedURLException e) {
					badSiteIds.add(siteId);
				} catch (UnknownHostException e){
					badSiteIds.add(siteId);
				}
			}
			if (badSiteIds.size()>0){
				String deleteBadUrlSql = "DELETE FROM Sites WHERE siteId=?";
				PreparedStatement deleteStatement = conn.prepareStatement(deleteBadUrlSql); 
				for (int badSiteId: badSiteIds){
					deleteStatement.setInt(1, badSiteId);
					deleteStatement.executeUpdate();
					System.out.println("Bad siteIds removed:"+badSiteId);
				}
				deleteStatement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally{
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

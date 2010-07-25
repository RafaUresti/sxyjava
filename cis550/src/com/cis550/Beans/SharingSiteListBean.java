package com.cis550.Beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.cis550.model.RegisteredUser;
import com.cis550.model.Site;
import com.cis550.util.SQLConn;

public class SharingSiteListBean {
private String accountHTML;
private ArrayList<String> sharingHTMLs;
    public void setUsername(String username){
	SQLConn sqlConn = new SQLConn();
	String userSql = "SELECT username, name, birthyear, city, state, profession FROM RegisteredUsers" +
	" WHERE username='"+username+"'";
	String sharingSql = "SELECT * from Sites WHERE username='"+username+"'";
	System.out.println("userSql =" +userSql);
	System.out.println("sharingSql =" +sharingSql);

	try {
		Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
		Connection conn = sqlConn.getConnection();
		Statement statement = conn.createStatement();
		ResultSet results = statement.executeQuery(userSql);
		RegisteredUser user;
		ArrayList<RegisteredUser> userList = RegisteredUser.buildUsers(results);
		if (userList.size() == 1){
			user = userList.get(0);
		} else {
			accountHTML = "No user information found for username:" + username;
			return;
		}
		accountHTML = user.generateHTML();
		results = statement.executeQuery(sharingSql);
		ArrayList<Site> sites = Site.buildSites(results);
		sharingHTMLs = Site.generateSiteHTMLs(sites, username);
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

public String generateAccountHTML(){
	String html = "<big>Here is your account information</big>" + "<br/>";
	if (sharingHTMLs.size() > 0){
		html += accountHTML + "<big>"+"These are your shared URLs:"+"</big>";
		for (String sharingHTML: sharingHTMLs){
			html += "<br/>";
			html += sharingHTML;
		}
	} else {
		html += "You have not shared any URL";
	}
	return html;
}
}


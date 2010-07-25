package com.cis550.Beans;
import com.cis550.model.*;
import com.cis550.util.SQLConn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * JavaBean class used in the JSP page for each story.
 * Need to call setStory first.
 * @author Xiaoyi Sheng
 *
 */
public class StoryBean {
	private Story story;
	private String storyHTML;
	public void setStory(String siteId, String username){
		final String WHERE_siteId = " WHERE siteId = '"+siteId+"'";
		SQLConn sqlConn = new SQLConn();
		String siteSql = "SELECT siteId, url, title, time, content, category, username FROM Sites";
		siteSql += WHERE_siteId;
		String commentsSql = "SELECT username, siteId, content, time FROM Comments";
		commentsSql += WHERE_siteId;
		commentsSql += " order by time desc";
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			Connection conn = sqlConn.getConnection();
			Statement statement1 = conn.createStatement();
			Statement statement2 = conn.createStatement();
			ResultSet siteResults = statement1.executeQuery(siteSql);
			ResultSet commentResults = statement2.executeQuery(commentsSql);
			ArrayList<Site> sites = Site.buildSites(siteResults);
			ArrayList<Comment> comments = Comment.buildComments(commentResults);
			if (sites != null && sites.size() == 1){
				story = new Story(sites.get(0), comments);
				storyHTML = story.generateHTML(username);
			}
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
	public String getStoryHTML(){
		return storyHTML;
	}
}

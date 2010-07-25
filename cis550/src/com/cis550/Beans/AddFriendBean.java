package com.cis550.Beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.cis550.util.SQLConn;

public class AddFriendBean {

	private String friendsHTMLs;
	private ArrayList<String> friends;
	public void setUsername(String username){
		friends = new ArrayList<String>();
		SQLConn sqlConn = new SQLConn();
		String friendSql1 = "SELECT user2 FROM Friendships WHERE user1 = '"+username+"'";
		String friendSql2 = "SELECT user1 FROM Friendships WHERE user2='"+username+"'";
		System.out.println("userSql =" +friendSql1);
		System.out.println("sharingSql =" +friendSql2);

		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(friendSql1);
			while (results.next()){
				friends.add(results.getString("user2"));
			}
			results = statement.executeQuery(friendSql2);
			while (results.next()){
				friends.add(results.getString("user1"));
			}
			if (friends.size() == 0){
				friendsHTMLs = "You have no friends";
			} else {
				friendsHTMLs = "<h1>Following is your friend list:</h1>";
				for (String friend:friends){
					friendsHTMLs += "<br/>";
					friendsHTMLs += friend;
				}
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
	
	public String generateFriendHTMLs(){
		return friendsHTMLs;
	}
}

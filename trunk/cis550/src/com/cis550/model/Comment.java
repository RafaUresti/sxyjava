package com.cis550.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Comment {
	private String username;
	private int siteId;
	private String content;
	private Timestamp time;

	public Comment(String username, int siteId, String content, Timestamp time) {
		this.username = username;
		this.siteId = siteId;
		this.content = content;
		this.time = time;
	}

	public String generateHTML() {
		String commentHTML = "";
		commentHTML += "<table width=\"500\" border=\"1\">" + "<tr>"
				+ "<td height=\"100\">" + this.content + " </td>"
				+ "</tr> <tr>" + "<td><table width=\"500\" border=\"1\">"
				+ "<tr>" + "<td width=\"242\">Author: " + this.username + "</td>"
				+ "<td width=\"242\">" + this.time + "</td>"
				+ "</tr> </table></td> </tr></table>";

		;
		return commentHTML;// TODO
	}

	/**
	 * Convert a result set of Comment tuples into an ArrayList of Comment
	 * objects.
	 * 
	 * @param results
	 * @return
	 */
	public static ArrayList<Comment> buildComments(ResultSet results) {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		try {
			while (results.next()) {
				String username = results.getString("username");
				int siteId = results.getInt("siteId");
				String content = results.getString("content");
				Timestamp time = results.getTimestamp("time");
				comments.add(new Comment(username, siteId, content, time));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return comments;
	}

	public String getUsername() {
		return username;
	}

	public int getSiteId() {
		return siteId;
	}

	public String getContent() {
		return content;
	}

	public Timestamp getTime() {
		return time;
	}
}

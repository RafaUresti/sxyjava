package com.cis550.model;
import java.sql.*;
import java.util.ArrayList;

import com.cis550.util.SQLConn;

public class Site {
	private int siteId = -1;
	private String url;
	private String title;
	private Timestamp time;
	private int votes;
	private String content;
	private String category;
	private String username;

	public Site(int siteId, String url, String title, Timestamp time, String content, String category, String username){
		this.siteId = siteId;
		this.url = url;
		this.title = title;
		this.time = time;
		SQLConn sqlConn = new SQLConn();
		Connection conn;
		try {
			String sql = "SELECT COUNT(*) as votes FROM Votes WHERE siteId="+siteId;
			conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(sql);
			if (results.next()){
				votes = results.getInt("votes");
			} else {
				votes = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.content = content;
		this.category = category;
		this.username = username;
	}

	/**
	 * Generate the HTML representative of this site object.
	 * @param more Whether to include the "More" option 
	 * (displayed in site list view)
	 * @return The String representative of the site
	 */
	public String generateHTML(boolean more, String username){
		String siteHTML = "";
		SQLConn conn = new SQLConn();
		siteHTML += "Posted by "+this.getUsername()+" on "+this.getTime();
		siteHTML += "<table width=\"500\" height=\"100\" border=\"1\">\r\n" + 
		"  <tr>\r\n" + 
		"    <td width=\"60\" height=\"100\"><table width=\"60\" height=\"100\" border=\"1\">\r\n" + 
		"      <tr>\r\n" + 
		"        <td height=\"60\" bgcolor=\"#EDF356\">Votes: "+this.getVotes()+"</td>\r\n" + 
		"      </tr>\r\n" + 
		"      <tr>\r\n" + 
		"        <td height=\"40\">";
		if (username != null && conn.hasVoted(username, siteId)){
			siteHTML += "Voted";
		} else {
			if (username == null){
				username = "";
			}
			siteHTML += "<a href=\"Vote?siteId="+siteId+"&username="+username+"\">Vote</a>";
		}
		try{
			conn.closeConnection();
		} catch (SQLException e){
			e.printStackTrace();
		}
		siteHTML +=	"</td>\r\n" + 
		"      </tr>\r\n" + 
		"    </table></td>\r\n" + 
		"    <td width=\"440\"><table width=\"440\" height=\"100\" border=\"1\">\r\n" + 
		"      <tr>\r\n" + 
		"        <td height=\"20\"><a href=\""+this.getUrl()+"\"target=\"_blank\">"+this.getTitle()+"</a></td>\r\n" + 
		"      </tr>\r\n" + 
		"      <tr>\r\n" + 
		"        <td height=\"80\">"+this.getContent()+"</td>\r\n" + 
		"      </tr>\r\n" + 
		"    </table></td>\r\n" + 
		"  </tr>\r\n" + 
		"</table>";
		siteHTML += "Category: "+this.category+"&nbsp&nbsp&nbsp<a href=\"addComment.jsp?siteId=" + siteId+"\"> Add Comment </a>&nbsp&nbsp&nbsp&nbsp" ;

		if (more == true){
			String siteName = "story.jsp";
			siteHTML +="" +
			"<a href=\"" + siteName +"?siteId="+this.getSiteId()+"\"> more...</a>"; 		
		}
		siteHTML += "<br/>";
		return siteHTML;
	}

	/**
	 * Convert a ResultSet of Site tuples into an ArrayList of Site objects
	 * @param results
	 * @return
	 */
	public static ArrayList<Site> buildSites(ResultSet results){
		ArrayList<Site> sites = new ArrayList<Site>();
		try {
			while(results.next()){
				int siteId = results.getInt("siteId");
				String url = results.getString("url");
				String title = results.getString("title");
				Timestamp time = results.getTimestamp("time");
				String content = results.getString("content");
				String category = results.getString("category");
				String username = results.getString("username");
				sites.add(new Site(siteId, url, title, time, content, category, username));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sites;
	}

	public static ArrayList<String> generateSiteHTMLs(ArrayList<Site> sites, String username) {
		ArrayList<String> siteHTMLs = new ArrayList<String>();
		for (Site site: sites){
			siteHTMLs.add(site.generateHTML(true, username));
		}
		return siteHTMLs;
	}

	@Override
	public boolean equals(Object otherSite){
		if (!(otherSite instanceof Site)){
			return false;
		} else {
			if (this.siteId == ((Site)otherSite).getSiteId()){
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public int hashCode(){
		return siteId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getUsername() {
		return username;
	}

	public int getSiteId() {
		return siteId;
	}

}

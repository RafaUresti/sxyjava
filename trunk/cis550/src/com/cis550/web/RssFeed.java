package com.cis550.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.cis550.util.SQLConn;

/**
 * Servlet implementation class RssFeed
 */
public class RssFeed extends HttpServlet {
	private static final long serialVersionUID = 1324L;
//	ArrayList<String> friendList;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RssFeed() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		System.out.print("reached here");
		String currentUser;
//		friendList = new ArrayList<String>();
		HttpSession session = request.getSession(true);
		currentUser = (String)session.getAttribute("name");
		String rss = "";
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		// String docType =
		// "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
		// "Transitional//EN\">\n";
		
		SQLConn sqlConn = null;
		
		String friends;
		String url;
		String comment;
		String post;
		//String vote;
		rss += "<?xml version=\"1.0\" encoding=\"utf-8\"?> " +
		"<rss version='2.0'>"
		+ "<channel>";
//		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?> " +
//				"<rss version='2.0'>"
//				+ "<channel>");
		
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn = new SQLConn();
			Connection conn = sqlConn.getConnection();
			
			Statement stmt = conn.createStatement();
            // execute select query
            String query = "SELECT user2 FROM Friendships WHERE user1 = '"+currentUser+"'" +
            		" UNION SELECT user1 FROM Friendships WHERE user2='"+currentUser+"'";//TODO TWO way selection
            ResultSet friendTable = stmt.executeQuery(query);		
            
            while(friendTable.next()){//TODO for test only
            	 System.out.println(friendTable.getString(1));
            }
           
			
			while (friendTable.next()) {
				// TODO
				friends = friendTable.getString(1);
				
				String query1 = "SELECT C.content S.url FROM Comments C, Sites S WHERE C.username = '"+friends+"' AND C.siteId = S.siteId";
				ResultSet commentTable = stmt.executeQuery(query1);
				
				String query2 = "SELECT S.url FROM Votes V, Sites S WHERE V.siteId = S.siteId AND V.username = friends";
				ResultSet voteTable = stmt.executeQuery(query2);
				
				String query3 = "SELECT url, content FROM Sites WHERE username = friends";
				ResultSet postTable = stmt.executeQuery(query3);
				
				while (commentTable.next()){
					out.println("<item>");
					out.println("<title>"+friends+"</title>");
					url = commentTable.getString (2);//TODO story.jsp?
					out.println("<link>"+url+"</link>");
					comment = commentTable.getString(1);
					out.println("<description>"+comment+"</description>");
					out.println("</item>");
					
					rss+="<item><br/><title>"+friends+"</title><br/>" +
						"<link>"+url+"</link><br/>" +
								"<description>"+comment+"</description><br/>" +
										"</item><br/>";
				}	
				
				while (voteTable.next()){
					out.println("<item>");
					out.println("<title>"+friends+"</title>");
					url = voteTable.getString (1);
					out.println("<link>"+url+"</link>");					
					out.println("<description>" +friends +"voted on" + url + "</description>");
					out.println("</item>");
					
					rss+="<item><br/><title>"+friends+"</title><br/>" +
					"<link>"+url+"</link><br/>" +
							"<description>"+url+"</description><br/>" +
									"</item><br/>";
				}		
				
				while (postTable.next()){
					out.println("<item>");
					out.println("<title>"+friends+"</title>");
					url = postTable.getString (1);
					out.println("<link>"+url+"</link>");
					post = postTable.getString(2);
					out.println("<description>"+post+"</description>");
					out.println("</item>");
					
					rss+="<item><br/><title>"+friends+"</title><br/>" +
					"<link>"+url+"</link><br/>" +
							"<description>"+post+"</description><br/>" +
									"</item><br/>";
					
					
				}		
			} 

		} catch (SQLException e) {
			e.printStackTrace();
			out.print(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			out.print(e.getMessage());
		} finally {
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		out.println(				
//				"<rss version = '2.0'>"+
//				"<channel>"+
//				"<item>"+
//				"<title>hello</title>"+
//				"<link>www.hotmail.com</link>"+
//				"<description>aaa</description>"+
//				"</item>"+
				"</channel>" 
				+ "</rss>");
		rss+="</channel></rss>";
		System.out.println(rss);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}


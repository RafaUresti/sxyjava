package com.cis550.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.cis550.util.SQLConn;
import com.cis550.model.Site;

/**
 * Servlet implementation class Registration
 */
public class newStory extends HttpServlet {
	private static final long serialVersionUID = 1324L;
	private SQLConn sqlConn;
	public Site site;
	java.util.Date today = new java.util.Date();

    /**
     * Default constructor. 
     */
    public newStory() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
	    HttpSession session = request.getSession();
	    if (session.getAttribute("name") == null){
	    	response.sendRedirect("login.html");
	    }
		PrintWriter writer = response.getWriter();
		int siteId = -1;
		String title = request.getParameter("title");
		String url = request.getParameter("url");
		String summary = request.getParameter("summary");
		String category = request.getParameter("category");
		if (title == null || title.trim().length() == 0){
			writer.print("Title cannot be empty!<br/>Press Back button to correct");
		} else if (url == null || url.trim().length() == 0){
			writer.print("URL cannot be empty!<br/>Press Back button to correct");
		} else if (category == null || category.equalsIgnoreCase("null") || category.trim().length() == 0){
			writer.print("Please choose a category!<br/>Press Back button to correct");
		}
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn= new SQLConn();
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			String query="";
			query = "select siteId, url, title from Sites where url = '"+url+"'";
			ResultSet nameSet = statement.executeQuery(query);
			if (nameSet.next()){
				writer.print("The url has already been posted:<br/>");
				siteId = nameSet.getInt("siteId");
				title = nameSet.getString("title");
				writer.print("<a href=story.jsp?siteId="+siteId+">"+title+"<a>");
				return;
			}
			
			query = "insert into Sites (url, title, time, content, category, username)" +
			" values('"+ url+"','"+ title+"','"+ 
			new java.sql.Timestamp(today.getTime())+"','"+ summary+"','"+ category+ "','" +
			(String)session.getAttribute("name")+"')";
			statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet siteIdSet = statement.getGeneratedKeys();
			if (siteIdSet.next()){
				siteId = siteIdSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			writer.print(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			writer.print(e.getMessage());
		} finally{
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (siteId > 0){
			this.site= new Site(siteId,url, title, new java.sql.Timestamp(today.getTime()),
				summary, category, (String)session.getAttribute("name"));
		} else{
			System.out.println("siteId failed to retrieve: "+ url);
		}
		response.sendRedirect("story.jsp?siteId="+ siteId);
	}
}

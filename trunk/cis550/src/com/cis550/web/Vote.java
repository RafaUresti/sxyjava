package com.cis550.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cis550.util.SQLConn;

/**
 * Servlet implementation class Vote
 */
public class Vote extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Vote() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Increment the vote of the Site that's voted on and insert the <username,siteId> 
	 * into Votes table
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String siteId = request.getParameter("siteId");
		String username = request.getParameter("username");
		if (username == null || username.trim().length() == 0){
			response.sendRedirect("login.html");
			return;
		}
		SQLConn sqlConn = new SQLConn();
		try {
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			String addVoteSql = "insert into Votes (username, siteId) VALUES ('"+username+"','"+siteId+"')";
			statement.executeUpdate(addVoteSql);
			response.sendRedirect("story.jsp?siteId="+siteId);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

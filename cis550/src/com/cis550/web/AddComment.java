package com.cis550.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import com.cis550.util.SQLConn;

/**
 * Servlet implementation class AddComment
 */
public class AddComment extends HttpServlet {
	private static final long serialVersionUID = 12342143L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddComment() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		String content = request.getParameter("comment");
		String username = (String) session.getAttribute("name");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		String siteId = request.getParameter("siteId");
		if (session.getAttribute("name") == null){
			response.sendRedirect("login.html");
			return;
		}
		String sql = "insert into Comments (username, siteId, content, time) values" +
				" ('"+username+"', '"+siteId+"', '"+content+"', '"+time+"')";
		PrintWriter writer = response.getWriter();
		SQLConn sqlConn = null;
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn = new SQLConn();
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate(sql);
			response.sendRedirect("story.jsp?"+"siteId="+siteId);
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
		
	}

}

package com.cis550.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;

import com.cis550.util.Crawler;
import com.cis550.util.SQLConn;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 12342143L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
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
		Crawler c = Crawler.getCrawler();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String sql = "SELECT username from RegisteredUsers where username = '"+username+"' and password = '"+password+"'";
		ResultSet nameSet;
		Statement statement;
		String name;
		PrintWriter writer = response.getWriter();
		SQLConn sqlConn = null;
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn = new SQLConn();
			Connection conn = sqlConn.getConnection();
			statement = conn.createStatement();
			nameSet = statement.executeQuery(sql);
			if (nameSet.next()){
				name = nameSet.getString("username");
				HttpSession session = request.getSession(true);
				session.setAttribute("name", name);
				response.sendRedirect("recommendation.jsp");
			}
			else{
				writer.print("Wrong user name or password, please go back and try again");
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
		
	}

}

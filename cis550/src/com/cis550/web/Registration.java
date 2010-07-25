package com.cis550.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cis550.util.SQLConn;

/**
 * Servlet implementation class Registration
 */
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1324L;
	private static int rid = 1;

    /**
     * Default constructor. 
     */
    public Registration() {
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
		SQLConn sqlConn = null;
		PrintWriter writer = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmpassword");
		String name = request.getParameter("name");
		String birthYear = request.getParameter("birthyear");
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		String profession = request.getParameter("profession");
		
		if (!password.equals(confirmPassword)){
			writer.print("passwords don't match!<br/>");
			writer.print("Please press back button to try again!");
			return;
		}
		else if (username == null || username.trim().isEmpty()){
			writer.print("Please provide name!<br/>");
			writer.print("Please press back button to try again!");
			return;
		}
		
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn = new SQLConn();
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			String query;
			query = "select username from RegisteredUsers where username = '"+username+"'";
			ResultSet nameSet = statement.executeQuery(query);
			if (nameSet.next()){
				writer.print("The username already exists!<br/> Please press \"BACK\" button and select another one.");
				return;
			}
			query = "insert into RegisteredUsers" +
			" values('"+ username+"','"+ password+"','"+ name +"','"+birthYear +"','"+city+"','"+state+"','"+profession+"')";
			writer.print(query);
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.print(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			writer.print(e.getMessage());
		} try {
			sqlConn.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("login.html");
		
	}

}

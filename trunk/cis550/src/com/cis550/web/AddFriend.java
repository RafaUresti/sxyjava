package com.cis550.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cis550.util.SQLConn;

/**
 * Servlet implementation class addFriend
 */
public class AddFriend extends HttpServlet {
	private static final long serialVersionUID = 12342143L;
	private SQLConn sqlConn;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddFriend() {
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
		HttpSession x= request.getSession(false);
		PrintWriter writer = response.getWriter();
		String myUsername = (String)x.getAttribute("name");
		String friend = request.getParameter("friend");
		if (friend.equalsIgnoreCase(myUsername)){
			writer.print("You cannot add yourself as a friend!<br/>Please click Back and try again");
			return;
		}
		String sql = "SELECT username from RegisteredUsers where username = '"+friend+"'";
		String friendCheckSql1 = "SELECT user1 from Friendships WHERE user2='"+friend+"'"+"AND user1='"+myUsername+"'";
		String friendCheckSql2 = "SELECT user2 from Friendships WHERE user1='"+friend+"'"+"AND user2='"+myUsername+"'";
		ResultSet nameSet;
		
		
		
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn= new SQLConn();
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			String query="";
			//statement = SQLConn.getStatement();
			nameSet = statement.executeQuery(sql);
			if (!nameSet.next()){
				writer.print("No such user exists, please go back and try again");
				return;
			} else {
				nameSet = statement.executeQuery(friendCheckSql1);
				if (nameSet.next()){
					writer.print(friend+" is already your friend!");
					return;
				}
				nameSet = statement.executeQuery(friendCheckSql2);
				if (nameSet.next()){
					writer.print(friend+" is already your friend!");
					return;
				}
			}
			query = "insert into Friendships" +
			" values('"+ myUsername+"','"+ friend+"')";
			statement.executeUpdate(query);
			response.sendRedirect("friends.jsp");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.print(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			writer.print(e.getMessage());
		} finally {
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

}

package com.cis550.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cis550.util.SQLConn;

/**
 * Servlet implementation class AdavancedQuery
 */
public class AdavancedQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdavancedQuery() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String advsearch = request.getParameter("advsearch");		
		StringTokenizer st = new StringTokenizer(advsearch);
		ArrayList<String> tempList = new ArrayList<String>();		
		String category=request.getParameter("category");
		String popularity=request.getParameter("popularity");
		String time = request.getParameter("time");
		time="2008-12-12 13:23:10";
		System.out.println("Reached Servelet" + time + category + popularity);
		
		while (st.hasMoreTokens()) {             
             //System.out.println(temWord);
             tempList.add(tempList.size(), st.nextToken());
        }
        
        Iterator<String> iterator = tempList.iterator();         
		
		ResultSet resultSet;
		ResultSet resultSetC;	
		ResultSet resultSetP;	
		ResultSet resultSetT;	
		String result;
		
		PrintWriter writer = response.getWriter();
		SQLConn sqlConn = null;
		
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn = new SQLConn();
			Connection conn = sqlConn.getConnection();
			
			PreparedStatement stmt = conn.prepareStatement("SELECT url, content " +
	     	"FROM Sites S, IIT I WHERE S.siteId = I.siteId and I.keywords = ? ");
			
			while(iterator.hasNext()){
				stmt.setString(1, iterator.next());
				resultSet = stmt.executeQuery();
				if (resultSet.next()) {
					// TODO
				} else {

					writer.print("Invalid search, please try again");
				}
			}
			
			
//			//category search
//			PreparedStatement stc = conn.prepareStatement("SELECT url, content " +
//	     	"FROM Sites WHERE category = ? ");
//			stc.setString(1,category);
//			resultSetC = stc.executeQuery();
			
			
			//combined
			PreparedStatement stp = conn.prepareStatement("select f.url, f.content from " +
					"(select s.*, count(v.username) as votes from Sites s, Votes v " +
					"where s.siteId=v.siteId group by v.siteId union " +
					"select s.*, 0 as votes from Sites s where s.siteId not in " +
					"(select siteId from votes)) f where votes > ?"+ 
					"INTERSECT SELECT url, content " +
			     	"FROM Sites WHERE category = ? "+
			     	"INTERSECT SELECT url, content " +
			     	"FROM Sites WHERE time > ? "
			     	);
			stp.setInt(1, Integer.parseInt(popularity));
			stp.setString(1,category);
			stp.setString(2,time);
			
			resultSetP = stp.executeQuery();
			
//			
//			PreparedStatement stt = conn.prepareStatement("SELECT url, content " +
//			     	"FROM Sites WHERE time > ? ");
//			stt.setString(1,time);
//			resultSetT = stt.executeQuery();
			
			
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

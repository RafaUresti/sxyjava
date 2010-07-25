package com.cis550.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cis550.Beans.SearchResultBean;
import com.cis550.model.Site;
import com.cis550.util.SQLConn;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Servlet implementation class userQuery
 */
public class KeywordQuery extends HttpServlet {
	private static final long serialVersionUID = 1324L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KeywordQuery() {
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
		
		String search = request.getParameter("search");	
		System.out.println("Search word:" + search);
		StringTokenizer st = new StringTokenizer(search);
		ArrayList<String> tempList = new ArrayList<String>();
		
		 while (st.hasMoreTokens()) {             
             //System.out.println(temWord);
             tempList.add(tempList.size(), st.nextToken());
         }
        
         Iterator<String> iterator = tempList.iterator();         
		
		ResultSet resultSet;		
		String result;
		PrintWriter writer = response.getWriter();
		SQLConn sqlConn = null;
		
		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn = new SQLConn();
			Connection conn = sqlConn.getConnection();
			
			PreparedStatement stmt = conn.prepareStatement("SELECT * " +
	     	"FROM Sites S, IIT I WHERE S.siteId = I.siteId AND I.keywords = ? ");
			HashMap<Site, Integer> hitSites = new HashMap<Site, Integer>();//<siteId, hit>
			while(iterator.hasNext()){
				stmt.setString(1, iterator.next());
				resultSet = stmt.executeQuery();
				ArrayList<Site> sites = Site.buildSites(resultSet);
				for (Site site:sites){
					if (hitSites.containsKey(site)){
						int hit = hitSites.get(site);
						hit++;
						hitSites.put(site, hit);
					} else {
						hitSites.put(site, new Integer(1));
					}
				}
//				if (resultSet.next()) {
//					
//					System.out.println(resultSet.getString(1));
//					System.out.println(resultSet.getString(2));
//					System.out.println(resultSet.getString(3));
//					
//				} else {
//
//					writer.print("Invalid search, please try again");
//				}
			}//end while
//			if (hitSites.size() == 0){
//				writer.print("Key word(s) not found!");
//				return;
//			} else {
				Set<Entry<Site, Integer>> entrySet = hitSites.entrySet();
				ArrayList<Entry<Site, Integer>> entryList = new ArrayList<Entry<Site, Integer>>(entrySet);
				Collections.sort(entryList, new Comparator<Map.Entry<Site, Integer>>(){
					@Override
					public int compare(Map.Entry<Site, Integer> arg0, Map.Entry<Site, Integer> arg1) {
						return arg1.getValue() - arg0.getValue();
					}
				});
				ArrayList<Site> sitesFound = new ArrayList<Site>();
				for (Map.Entry<Site, Integer> mapEntry: entryList){
					sitesFound.add(mapEntry.getKey());
					System.out.println(mapEntry.getKey().getSiteId()+":"+mapEntry.getValue());
				}
				HttpSession session = request.getSession();
				SearchResultBean.setSitesFound(sitesFound, (String)session.getAttribute("name"));
				response.sendRedirect("searchResult.jsp");
//			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			writer.print(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			writer.print(e.getMessage());
		}
		finally {
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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

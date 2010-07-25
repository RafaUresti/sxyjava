package com.upenn.seas.cis.dstz.session;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.upenn.seas.cis.dstz.general.DBA;
import com.upenn.seas.cis.dstz.general.GenFn;
import com.upenn.seas.cis.dstz.general.User;

/**
 * Servlet implementation class for Servlet: SvLogin
 *
 */
public class SvLogin extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public 	String   svRtMsg = null;      //return message
	private DBA      objDBA = null;
	private GenFn    objGF = null;

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		objDBA = new DBA();
		objGF = new GenFn();
	}	

	public void destroy()
	{
		if (objDBA.dbconn != null)
		{
			objDBA.Close();
		}
		super.destroy();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		String svUserEmail = req.getParameter("fzEmail");
		String svPassword = req.getParameter("fzPassword");
		String svUserID = null;
		String svUserName = null;
		String svUserGroup = null;
		String svStatusCode = "";

		String svSql = null;

		svSql = "SELECT UserID, FirstName, LastName, GroupCode ";
		svSql = svSql + " FROM tbl_user ";
		svSql = svSql + " WHERE Email = " + objGF.sqlString(svUserEmail);
		svSql = svSql + " AND Password = " + objGF.sqlString(svPassword);

		//write log file to track remote access
		String svLogFile = "mylogAS.txt";
		try 
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(svLogFile, true));
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			java.util.Date date = new java.util.Date();
			String svDatetime = dateFormat.format(date);

			out.write(req.getRemoteAddr() + "\t" + svUserEmail + "\t" + svPassword + "\t"
					+ svDatetime + "\n");
			out.close();
		} catch (IOException e) {
			svRtMsg = e.getMessage();
		}
		//end writing log file

		svRtMsg = objDBA.Open();
		if (svRtMsg != null)
		{ 

		}else
		{
			svRtMsg = objDBA.doQuery(svSql);

			if (svRtMsg != null)
			{
				svRtMsg = svSql + "<BR>" + svRtMsg;
			}
			try
			{
				if (objDBA.rs.next())
				{
					svUserID = String.valueOf(objDBA.rs.getInt("UserID"));
					svUserName = objDBA.rs.getString("FirstName") + " " + objDBA.rs.getString("LastName");
					svUserGroup = objDBA.rs.getString("GroupCode");

					HttpSession session = req.getSession(true);
					User ovUser = new User(); 

					ovUser.putUserID(svUserID);
					ovUser.putUserEmail(svUserEmail);
					ovUser.putUserName(svUserName);
					ovUser.putUserGroup(svUserGroup);

					session.setAttribute("User", ovUser);
					svRtMsg = ""; 
				}
				else
				{
					svRtMsg = "Invalid Email or password. Please try again.";
				}
			}catch (SQLException sqle)
			{
				svRtMsg = "Error code:" + sqle.getErrorCode() 
				+ " \n<br>" + "Error Msg: " + sqle.getMessage()
				+ " \n<br>" + "Sql statment: " + sqle.getSQLState()
				+ " \n<br>" + "Cause: " + sqle.getCause();
				try 
				{
					BufferedWriter out = new BufferedWriter(new FileWriter(svLogFile, true));
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					java.util.Date date = new java.util.Date();
					String svDatetime = dateFormat.format(date);

					out.write(svDatetime + "Error: " + svRtMsg + "\n");
					out.close();
				} catch (IOException e) {

				}
			}
		}
		if(svRtMsg.length()==0)
		{
			resp.sendRedirect(objGF.getFullContextUrl(req)+"/index.jsp?msg=You login successfully.");
		}else
		{
			resp.sendRedirect(objGF.getFullContextUrl(req)+"/login.jsp?msg="+svRtMsg);
		}
	}   	  	    
}
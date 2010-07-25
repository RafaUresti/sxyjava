package com.kemplerEnergy.servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.kemplerEnergy.position.*;
/**
 * Servlet implementation class ReportPosition
 */
public class ReportPositionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	int cols = 22;
	private static String marketZone[]= {"KMA","EC","NYH","S.EAST","FL.","WEST","GULF","DALLAS","FZE-D","RL-D",
		"EH","QE","FZE","FZE-C","CU","EZ","LA OPIS","NYH OPIS","NYH PLATTS","RB","RL","CORN"};
	private static String monthMapping[]={"F","G","H","J","K","M","N","Q","U","V","X","Z"};
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReportPositionServlet() {
//		super();
		// TODO Auto-generated constructor stub

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		if (request.getParameter("type").equalsIgnoreCase("position")) {
//			reportPosition(request,response);
	//	}

	}
	
	public void reportPosition(HttpServletRequest request,
			HttpServletResponse response, Calendar date) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);
		int day = date.get(Calendar.DAY_OF_MONTH);
		
		StringBuilder yearStrB = new StringBuilder(String.valueOf(year));
		
		String yearInShort = yearStrB.substring(2,4);
		String monthInShort = monthMapping[month];
		String dayInShort = String.valueOf(day);
		double [][] result = null;
		try {
		PositionView view = new PositionView();
		result = view.retriveData(monthInShort + String.valueOf(year).substring(2,4));// "N08"
		}
		catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
		catch (SQLException s) {
			s.printStackTrace();
		}
		
		printHeader(request, out);
		for(int i = 0; i < 20; i ++){
			if((month+i)%12 == 0) // entering next year
				year ++;
			printRow(out, monthMapping[(month+i)%12] + String.valueOf(year).substring(2,4), i, result);

		}
		printEnd(out);
	}
	
	private void printRow(PrintWriter out, String month, int rowIndex, double [][]result){
		out.println("<tr><td bgcolor=\"#777777\" height=\"30\"><div align=\"center\"><span class=\"STYLE1\">"+month+"<div></td>");
		for(int j =0;j<22;j++){
			out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">" +
					result[rowIndex][j] +
					"<div></td>");
		}
		/*
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">KMA<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">EC<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">NYH<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">S.EAST<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">FL.<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">WEST<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">GULF<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">DALLAS<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">FZE-D<div></td>");
		out.println("<td height=\"30\" ><div align=\"center\"><span class=\"STYLE_DATA\">RL-D<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">EH<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">QE<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">FZE<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">FZE-C<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">CU<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">EZ<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">LA OPIS<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">NYH OPIS<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">NYH PLATTS<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">RB<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">RL<div></td>");
		out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">CORN<div></td>");
		*/

		out.println("<td bgcolor=\"#777777\" height=\"30\"><div align=\"center\"><span class=\"STYLE1\">"+month+"<div></td>");
		for(int j =22;j<24;j++){
			out.println("<td height=\"30\"><div align=\"center\"><span class=\"STYLE_DATA\">" +
					result[rowIndex][j] +
					"<div></td>");
		}
//		out.println("<td><div align=\"center\" height=\"30\"><span class=\"STYLE_DATA\">COMPOSITE<div></td>");
//		out.println("<td><div align=\"center\" height=\"30\"><span class=\"STYLE_DATA\">SPREAD POSITION<div></td></<tr>");
	}

	private void printEnd(PrintWriter out) {
		out.println("</table></body></html>");
		out.close();
	}

	private void printHeader(HttpServletRequest request, PrintWriter out) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" /> <title>Report</title>" +
				"<style type=\"text/css\"><!--" +
				".STYLE1 {color: #FFFFFF}" +
				"-->"+
				"</style>" +
				"<style type=\"text/css\"><!--" +
				".STYLE_DATA {color: #000000}" +
				"-->"+
				"</style>" +
				"</head>");
		out.println("<body>");
		out.println("<h1>Report Position</h1><hr>");
		out.println(request.getParameter("type"));
		
		out.println("<table border=\"1\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" ><tr>");
	    out.println("<td colspan=\"26\" bgcolor=\"#ffffff\"><div align=\"center\"><span class=\"STYLE_DATA\">Trader Name - Position - Date - Time</span><div></td>");
	    out.println("</tr></table>");
		
		out.println("<table border=\"1\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" ><tr>");
	    out.println("<td colspan=\"11\" bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">CASH</span><div></td>");
		out.println("<td colspan=\"12\" bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">CLEARED</span><div></td>");
		out.println("<td colspan=\"3\" bgcolor=\"#777777\">&nbsp</td></tr>");
		out.println("<tr><td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">&nbsp<div></td>");
		for(int i = 0; i< cols; i++)
			out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">"+
					marketZone[i]+
					"<div></td>");
		/*
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">KMA<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">EC<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">NYH<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">S.EAST<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">FL.<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">WEST<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">GULF<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">DALLAS<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">FZE-D<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">RL-D<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">EH<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">QE<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">FZE<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">FZE-C<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">CU<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">EZ<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">LA OPIS<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">NYH OPIS<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">NYH PLATTS<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">RB<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">RL<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">CORN<div></td>");
		*/
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">&nbsp<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">COMPOSITE<div></td>");
		out.println("<td bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">SPREAD POSITION<div></td></<tr>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(request, response);
	}

}

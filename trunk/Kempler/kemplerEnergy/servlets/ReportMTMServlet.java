package com.kemplerEnergy.servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ReportMTM
 */
public class ReportMTMServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	int cols = 22;
	private static String marketZone[]= {"KMA","EC","NYH","S.EAST","FL.","WEST","GULF","DALLAS","FZE-D","RL-D",
		"EH","QE","FZE","FZE-C","CU","EZ","LA OPIS","NYH OPIS","NYH PLATTS","RB","RL","CORN"};
	private static String monthMapping[]={"F","G","H","J","K","M","N","Q","U","V","X","Z"};
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReportMTMServlet() {
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
		// if (request.getParameter("type").equalsIgnoreCase("position")) {
//		reportMTM(request, response);
		// }
	}

	protected void reportMTM(HttpServletRequest request,
			HttpServletResponse response, Calendar date) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);//0-11
		int day = date.get(Calendar.DAY_OF_MONTH);//1-**
		
		StringBuilder yearStrB = new StringBuilder(String.valueOf(year));
		
		String yearInShort = yearStrB.substring(2,4);
		String monthInShort = monthMapping[month];// 0 - 11
		String dayInShort = String.valueOf(day);
		
		printHeader(request, out);
		int rowTotalNum = 25;
		int colIndex = 0, rowIndex = 0, colSpan = 2;
		double [][] resultMTM={{2,4,},{1,9}};
		
		boolean firstQuarterPassed = false, lastQuarterEntered = false;//flag to denote whether the corresponding has been passed
		for(int i = 0; i < rowTotalNum; i ++){
			System.err.print(i);
			if((month+i)%12 == 0) // entering next year
				year ++;
			if(i==0){//first line
				printRow(out, monthMapping[(month+i)%12] + String.valueOf(year).substring(2,4),3-month%3,true,getColumnSum(rowIndex,colIndex,colSpan,resultMTM));
			}
			else{
				if(((month+i)%12)%3 ==0  &&  !firstQuarterPassed){
					printRow(out, monthMapping[(month+i)%12] + String.valueOf(year).substring(2,4),3,true,getColumnSum(rowIndex,colIndex,colSpan,resultMTM));
					firstQuarterPassed = true;
				}
				else if( ((month+i)%12)%3 ==0 && rowTotalNum - i <=3 && firstQuarterPassed && !lastQuarterEntered){ // enter last quarter
					printRow(out, monthMapping[(month+i)%12] + String.valueOf(year).substring(2,4),rowTotalNum - i,true,getColumnSum(rowIndex,colIndex,colSpan,resultMTM));
					lastQuarterEntered = true;
				}
				else if(((month+i)%12)%3 == 0 && !lastQuarterEntered && firstQuarterPassed) // in between
					printRow(out, monthMapping[(month+i)%12] + String.valueOf(year).substring(2,4),3,true,getColumnSum(rowIndex, colIndex,colSpan,resultMTM));
				else{// if(firstQuarterPassed && !lastQuarterEntered && ((month+i)%12)%3 !=0){
					printRow(out, monthMapping[(month+i)%12] + String.valueOf(year).substring(2,4),0,false,getColumnSum(rowIndex, colIndex,colSpan,resultMTM));
				}

			}
		}
		printEnd(out);
	}

	private double getColumnSum(int rowIndex, int colIndex, int colSpan, double[][] array)
	{
		double sum = 0.0;
		for(int i=0;i<colSpan;i++)
			sum += array[rowIndex+i][colIndex];
		return sum;
	}
	/*sumUp only used when hasSpan is set to be true
	 * 
	 */
	private void printRow(PrintWriter out,String month, int rowspan, boolean hasSpan, double sumUp) {
		out.println("<tr><td bgcolor=\"#777777\" height=\"30\"><div align=\"center\"><span class=\"STYLE1\">"+ month + "<div></td>");
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
		out.println("<td bgcolor=\"#777777\" height=\"30\"><div align=\"center\"><span class=\"STYLE1\">"		+ month + "<div></td>");
		out.println("<td><div align=\"center\" height=\"30\"><span class=\"STYLE_DATA\">COMPOSITE<div></td>");
		if(hasSpan)
		out.println("<td rowspan = \" " + String.valueOf(rowspan) + "\"><div align=\"center\" height=\"30\"><span class=\"STYLE_DATA\">" + String.valueOf(sumUp)+ "<div></td></<tr>");

	}

	private void printEnd(PrintWriter out) {
		out.println("</table></body></html>");
		out.close();
	}

	private void printHeader(HttpServletRequest request, PrintWriter out) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" /> <title>Report</title>"		+ "<style type=\"text/css\"><!--"		+ ".STYLE1 {color: #FFFFFF}"		+ "-->"		+ "</style>"		+ "<style type=\"text/css\"><!--"		+ ".STYLE_DATA {color: #000000}"		+ "-->"		+ "</style>"		+ "</head>");
		out.println("<body>");
		out.println("<h1>Report MTM - Updated</h1><hr>");
		out.println(request.getParameter("type"));

		out.println("<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" ><tr>");
		out.println("<td colspan=\"26\" bgcolor=\"#ffffff\"><div align=\"center\"><span class=\"STYLE_DATA\">Trader Name - MTM - Date - Time</span><div></td>");
		out.println("</tr></table>");

		out.println("<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" ><tr>");
		out.println("<td colspan=\"11\" bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">CASH</span><div></td>");
		out.println("<td colspan=\"13\" bgcolor=\"#777777\"><div align=\"center\"><span class=\"STYLE1\">CLEARED</span><div></td>");
		out.println("<td colspan=\"2\" bgcolor=\"#ffffff\">&nbsp</td></tr>");
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
		out.println("<td bgcolor=\"#ffffff\"><div align=\"center\"><span class=\"STYLE1\"> <div></td>");
		out.println("<td bgcolor=\"#ffffff\"><div align=\"center\"><span class=\"STYLE1\"> <div></td></<tr>");
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
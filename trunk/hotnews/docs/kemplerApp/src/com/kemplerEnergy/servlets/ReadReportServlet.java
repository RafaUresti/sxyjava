package com.kemplerEnergy.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.lang.StringBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class readReport
 */
public class ReadReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadReportServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		super.doGet(request, response);
//		response.setContentType("text/html;charset=gb2312");
		
		Calendar rightNow = Calendar.getInstance();
//		out.print(rightNow.get(Calendar.MONTH)+1);
//		out.print(rightNow.get(Calendar.DAY_OF_MONTH));

		if(!request.getParameter("today").equalsIgnoreCase("Today"))
		{
			//get the date of the input control
			StringBuilder month = new StringBuilder((request.getParameter("dateText").subSequence(0, 2)));
			StringBuilder day = new StringBuilder((request.getParameter("dateText").subSequence(3, 5)));
			StringBuilder year = new StringBuilder((request.getParameter("dateText").subSequence(6, 10)));
			rightNow.set(Integer.valueOf(year.toString()),
					Integer.valueOf(month.toString()) - 1, // to calendar form
							Integer.valueOf(day.toString()));
//			response.getWriter().print(Integer.valueOf(month.toString()));
		}
		
		
		if(request.getParameter("type").equalsIgnoreCase("POSITION")){
			ReportPositionServlet report = new ReportPositionServlet ();
			report.reportPosition(request, response, rightNow);
		}
		else{
			ReportMTMServlet report = new ReportMTMServlet ();
			report.reportMTM(request, response, rightNow);
		}

//		PrintWriter out = response.getWriter();
//		out.print(year.toString() + "-" + month.toString() + "-" + day.toString());
//		out.print(rightNow.get(Calendar.MONTH) + request.getParameter("today"));
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(request, response);
	}

}

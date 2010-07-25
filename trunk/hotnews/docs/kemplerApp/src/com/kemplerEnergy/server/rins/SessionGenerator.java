package com.kemplerEnergy.server.rins;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionGenerator extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AuthenticateUtil au = new AuthenticateUtil();
		if(au.check(request.getParameter("userName"), request.getParameter("passWord"))){
			HttpSession session = request.getSession(true);
			if(session.isNew()){
				session.setAttribute("USER_NAME", request.getParameter("userName"));
				session.setMaxInactiveInterval(60 * 60 * 1);//1 hour6 0 * 60 * 1
			}
			response.getWriter().print(au.getGroup(request.getParameter("userName")));
		}
		else
			response.getWriter().print("Authentication failed ");
	}
}
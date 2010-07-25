package com.kemplerEnergy.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class Authentication extends HttpServlet {

	/**
	 * Login/Logout process 
     * @requestXML <action type="login|logout">
     * 				<userName>$userName</userName>
     * 				<userPasswd>$userPasswd</userPasswd>
     * 			   </action>
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}

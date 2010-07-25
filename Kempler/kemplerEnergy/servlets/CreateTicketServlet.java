package com.kemplerEnergy.servlets;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet deals ticket issue, when trader open a create ticket view, the 
 * ticket number will be generated and reserved. 
 * @author Ji Fang
 */
public class CreateTicketServlet extends HttpServlet {

    public void init() throws ServletException {
    	//TODO database connection  
    }
    
    public void destroy() {
    	//TODO disconnect database  
    }
    
    /**
     * When trader select the product type. System should store the current
     * time, traderID and generate unique ticketNo. Mark the ticket status to
     * INC(incomplete). Store some dummy data to meet database Not Null 
     * constraint if necessary. 
     * @ruleID BR_1.01 
     * @requestXML <ticket>
     * 				<product>$productID</product>
     * 			   </ticket>
     * @respondXML <ticket>
     * 			    <time type="ticket">$time</date>
     * 			    <ticketNo>$ticketNo</ticketNo>
     * 			   </ticket>
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}

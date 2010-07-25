package com.kemplerEnergy.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet update ticket, applied to following conditions:
 * First time ticket creation. 
 * Ticket changed later on by contract manager or trader.
 * Ticket modified by administrator.
 * @author Ji Fang
 */
public class UpdateTicketServlet extends HttpServlet {

	/**
	 * change the ticket status accordingly. 
     * @ruleID BR_1.19 
     * @requestXML <ticket>
     * 				<physicalTicket type="T|F">
     * 				 <shipMode>$shipMode</shipMode>
     * 				 <ratable>"T|F"</ratable>
     * 				 <FOBPoint>$FOBPoint</FOBPoint>
     * 				</physicalTicket>
     * 				<comment>$comment</comment>
     * 				<trader>$traderID</trader>
     * 				<contract type="B|S">
     * 				 <time type="trade">$time</time>
     * 				 <product>$productID</product>
     * 				 <pricing type="1|2|3">
     * 				  <basis>$basis</basis>
     * 				  <futurePrice>$futurePrice</futurePrice>
     * 				  <fixPrice>$fixPrice</fixPrice>
     * 				 </pricing>
     * 				</contract>
     * 			   </ticket>
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}

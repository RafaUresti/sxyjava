package com.kemplerEnergy.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet search the ticket information passing from client and return all
 * the matching tickets.
 */
public class ReadTicketServlet extends HttpServlet {
	/**
	 * Read/Search for ticket information
     * @ruleID BR_1.19 
     * @requestXML <search>
     * 				<ticketNo>$ticketNo</ticketNo>
     * 				<trader>$traderName</trader>
     * 				<time type="ticket|trade">
     * 				 <start>$time</start>
     * 				 <end>$time</end>
     *				</time>
     * 			   </search>
     * @respondXML <count>$num</count> 
     * 			   <ticket>
     * 				<trader>$traderName</trader>
     * 				<time type="ticket>$time</time>
     * 				<ticketNo>$ticketNo</ticketNo>
     * 				<physicalTicket type="T|F">
     * 				 <shipMode>$shipMode</shipMode>
     * 				 <ratable>"T|F"</ratable>
     * 				 <FOBPoint>$FOBPoint</FOBPoint>
     * 				</physicalTicket>
     * 				<comment>$comment</comment>
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

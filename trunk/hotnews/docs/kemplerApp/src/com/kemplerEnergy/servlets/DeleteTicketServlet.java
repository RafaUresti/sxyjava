package com.kemplerEnergy.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Delete the ticket given ticket number.
 */
public class DeleteTicketServlet extends HttpServlet {
    
	/**
     * Ticket may only be allowed to be deleted by system administrator.
     * @requestXML <ticket>
     * 				<product>ticketNo</product>
     * 			   </ticket>
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}

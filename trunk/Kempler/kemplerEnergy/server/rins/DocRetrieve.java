package com.kemplerEnergy.server.rins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.util.Streams;

public class DocRetrieve extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1201006356136854806L;
	private static final String ROOT_DOC = "c:\\kemplerDoc\\RINsDir\\";

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// TODO: Session Check needed
		
		String filePath = ROOT_DOC + request.getParameter("filePath");
		File fi = new File(filePath);
		
		try {
			if (filePath.endsWith(".pdf"))
				response.setContentType("application/pdf");
			else if (filePath.endsWith(".csv")) {
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition","attachment; filename=report.csv" );

			}

			OutputStream out = response.getOutputStream();
			FileInputStream fis = new FileInputStream(fi);
			Streams.copy(fis, out, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().print("Sorry, File not found");
		} 
	}
}

package com.kemplerEnergy.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 * Servlet implementation class for Servlet: UploadServlet
 * 
 */
public class UploadServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	File tmpDir = null;
	File saveDir = null;

	public UploadServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				DiskFileItemFactory dff = new DiskFileItemFactory();
				dff.setRepository(tmpDir);
				dff.setSizeThreshold(1024000);
				ServletFileUpload sfu = new ServletFileUpload(dff);
				sfu.setFileSizeMax(-1);
				sfu.setSizeMax(-1);
				FileItemIterator fii = sfu.getItemIterator(request);
				while (fii.hasNext()) {
					FileItemStream fis = fii.next();
					if (!fis.isFormField() && fis.getName().length() > 0) {
						String fileName = fis.getName().substring(
								fis.getName().lastIndexOf("\\"));
						BufferedInputStream in = new BufferedInputStream(fis
								.openStream());
						BufferedOutputStream out = new BufferedOutputStream(
								new FileOutputStream(new File(saveDir
										+ fileName)));
						Streams.copy(in, out, true);
						UnzipFile.unzip(fileName);
						try {
							response.getWriter().println(
									"File upload:" + fileName + "; size "
											+ "\r\n");
						} catch (Exception e) {
							response.getWriter().println(e.toString());
						}
					}
				}
				redirectToReadReport(response);
//				response.getWriter().println("End");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* method that can redirects the web browser to get
	 * the report
	 */
	private void redirectToReadReport(HttpServletResponse response)
	throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
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
				"<meta HTTP-EQUIV=\"REFRESH\" content=\"0; url=readReport.jsp\">" +
				"</head>");
		out.println("<body>");
		out.println("</body></html>");
		out.close();
	}

	public void init() throws ServletException {
		super.init();
		String tmpPath = "c:\\KemplerFiles\\tmpdir";
		String savePath = "c:\\KemplerFiles\\updir";
		tmpDir = new File(tmpPath);
		saveDir = new File(savePath);
		if (!tmpDir.isDirectory())
			tmpDir.mkdir();
		if (!saveDir.isDirectory())
			saveDir.mkdir();
	}
}
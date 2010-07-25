package com.kemplerEnergy.server.rins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5617026672870928493L;
	File tmpDir = null;
	File saveDir = null;
//	String baseURL = "http://209.97.223.189:8080/RINs/DocRetrieve?filePath=";
//	String baseURL = "http://localhost:8888/com.kemplerEnergy.RINsMain/DocRetrieve?filePath=";
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		System.out.println("::" + request.getSession().getId());
		/*
		 * if(!SessionCheck.check(request)){ throw (new
		 * JavaScriptException("log in failed or session expired")); }
		 */
		
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				DiskFileItemFactory dff = new DiskFileItemFactory();
				dff.setRepository(tmpDir);
				dff.setSizeThreshold(1024000);
				ServletFileUpload sfu = new ServletFileUpload(dff);
				sfu.setFileSizeMax(-1);
				sfu.setSizeMax(-1);
//				FileItemIterator fii = sfu.getItemIterator(request);

				List<FileItem> items = new ArrayList<FileItem>();
//				List items = null;
				items = sfu.parseRequest(request);
//				if (items != null) {
//					System.out.println(items.size());
//					System.out.println(items);
//				}
				FileItem tempFile = null;
				String fileName = null;
//				System.out.println("before first loop");
				for(FileItem f : items)
				{
//					System.out.println("first loop");
					if(f.isFormField())
					{
//						System.out.println("read the path");
						if(f.getFieldName().equalsIgnoreCase("invoicePath"))
						{
							fileName = f.getString();
//							System.out.println(fileName);
						}
					}
					else
						tempFile = f;
				}
				File fi = new File(saveDir + "\\" + fileName);
				tempFile.write(fi);
				//XXX modified by Xiaoyi
				response.getWriter().print("SUCCESS");
				/*if(index!=-1)
				 * 	response.getWriter().println(saveDir + "\\" + fileName + "_" + String.valueOf(index) + ".pdf");
				 *	else
				 * 	response.getWriter().println(saveDir + "\\" + fileName + ".pdf");*/
				}
			}
		catch (Exception e) {
			e.printStackTrace();
			response.getWriter().println(e.toString());
		}
	}

	public void init() throws ServletException {
		super.init();
		String tmpPath = "c:\\tmpdir";
		String savePath = "c:\\kemplerDoc\\RINsDir";
		tmpDir = new File(tmpPath);
		try {
			saveDir = new File(savePath);
		} catch (Exception e) {
			System.err.print(e.toString());
		}
		if (!tmpDir.isDirectory())
			tmpDir.mkdir();
		if (!saveDir.isDirectory())
			saveDir.mkdir();
	}
}

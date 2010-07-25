package com.kemplerEnergy.servlets;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import com.kemplerEnergy.position.*;
public class UnzipFile {

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	public static void unzip(String fileName) {
		Enumeration entries;
		ZipFile zipFile;

		File saveDir = null;
		String savePath = "c:\\updir\\";
		saveDir = new File(savePath);
		if (!saveDir.isDirectory())
			saveDir.mkdir();
		try {
			zipFile = new ZipFile("c:\\updir\\" + fileName);
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				System.err.println("Extracting file: " + entry.getName());
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(new File(savePath
								+ entry.getName())));

				copyInputStream(zipFile.getInputStream(entry), out);
				out.close();
				FileLoader file = new FileLoader();
				System.err.println(fileName);
				file.parse(savePath	+ entry.getName());
			}
			zipFile.close();
			File zipFileToDelete = new File(savePath + fileName);
			zipFileToDelete.delete();

			
		} catch (IOException ioe) {
			System.err.println("Unhandled exception:");
			ioe.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
package cit010_Eliza;
import java.io.*;

import javax.swing.JFileChooser;

public class LineWriter {

	private String fileName;
	private PrintWriter printWriter;

	LineWriter(String message) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(message);
		int result = chooser.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				if (file != null) {
					fileName = file.getCanonicalPath();
					printWriter = new PrintWriter(new FileOutputStream(fileName), true);
				}
			}
			catch(Exception e) {
				System.err.println(fileName+"Opening Error!");
			}
		}
	}

	
	 void writeLine(String line) {
		 printWriter.append(line);
	 }

	 void close( ) {
		 printWriter.flush( );
		 try {
			 printWriter.close( );
		 }
		 catch(Exception e) { }
	 }

}

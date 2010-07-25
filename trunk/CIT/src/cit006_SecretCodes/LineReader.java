package cit006_SecretCodes;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Simple class to read lines in from a text file. To use, create a
 * <code>LineReader</code> object, then send it the <code>readLine()</code>
 * message as many times as desired. When <code>readLine()</code> returns
 * the value <code>null</code>, the last line has been read, and the file
 * is automatically closed for you.
 * 
 * @author David Matuszek
 * @version 3.0
 */
public class LineReader {
    private BufferedReader bufferedReader;
    
    /**
     * Creates a LineReader to read lines (as Strings) from a file.
     * Calling this constructor causes a standard file dialog box to
     * open, allowing the user to choose a file; the message is
     * displayed at the top of the dialog box.
     * 
     * @param message The message to use in the JFileChooser dialog.
     */
    public LineReader(String message) {
        // use a JFileChooser dialog to get the name and directory of a file
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(message);
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                if (file != null) {
                    String fileName = file.getCanonicalPath();
                    bufferedReader =
                        new BufferedReader(new FileReader(fileName));
                }
            }
            catch (IOException e) {
                System.out.println("Unable to read selected file");
            }
        }
    }
    
    /**
     * This constructor is used if you forget to supply a message to
     * be displayed in the dialog box.
     */
    public LineReader() {
        this("Read lines from what file?");
    }
    
    /**
     * Once you have created a LineReader for a file, each call to
     * <code>readLine()</code> will return another line from that file.
     * After the last line in read, <code>readLine()</code> will return
     * <code>null</code> instead of a String.
     * 
     * @return The line read, or <code>null</code> if all lines have been read.
     */
    public String readLine () {
        if (bufferedReader == null) {
            System.err.println("readLine() called without a valid input file.");
            return null;
        }
        try {
            String line = bufferedReader.readLine();
            if (line == null) close();
            return line;
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
        return null;
    } 
    
    /**
     * Once you have created a LineReader for a file, on call to this
     * method will return an array containing all the lines on that file.
     * 
     * @return The array of lines.
     */
    public String[] readAllLines() {
    	ArrayList<String> list = new ArrayList<String>();
    	String line;
    	while ((line = readLine()) != null) {
    		list.add(line);
    	}
    	return list.toArray(new String[0]);
    }

    /**
     * Closes the file used by this LineReader. May be called by the user
     * of this class, but doesn't need to be.
     */
     public void close () {
        try { bufferedReader.close (); }
        catch (IOException e) { }
    }
    
    /**
     * Main method used exclusively for testing.
     * 
     * @param args Not used.
     */
    public static void main(String[] args) {
        LineReader reader = new LineReader();
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
        reader = new LineReader("Do it again!");
        String[] lines = reader.readAllLines();
        for (int i = 0; i < lines.length; i++) {
        	System.out.println(lines[i]);
        }
    }
}

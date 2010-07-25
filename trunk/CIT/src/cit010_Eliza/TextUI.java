package cit010_Eliza;
import java.io.*;
import java.util.*;

/**
 * 
 * This is a text only user interface of Eliza. 
 * This program begins by trying to read in a file of patterns with some predetermined name (i.e.patterns.txt). 
 * If the file is not found or cannot be read, the program asks the user for the name of a file to read.
 * It repeats until a file is successfully read or until the user types quit.
 * The only way to quit is by telling Eliza Goodbye!
 *
 */
public class TextUI {

	FileReader fileReader;
	String fileName = "pattern.txt";
	String inputLine="";
	Scanner scanner = new Scanner(System.in);

	ArrayList<String> myPatternList = new ArrayList<String>();
	BufferedReader bufferedReader ;

	Eliza eliza;

	public static void main(String[] args) {
		new TextUI().run();
	}

	public void run() {

		do{
			try {

				bufferedReader =new BufferedReader(new FileReader(fileName));
			} catch (FileNotFoundException e) {

				System.out.println("File Not Found! Please type a file name!");
				fileName=scanner.nextLine();
				if(fileName.equalsIgnoreCase("quit")){
					System.out.println("Thanks. GoodBye!");
					return;
				}
			}
		}while(bufferedReader == null);

		try {
			String line;
			while((line = bufferedReader.readLine()) != null) {	
				myPatternList.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Hi, I'm Eliza. What can I do for you?");

		while (!(inputLine=scanner.nextLine()).equalsIgnoreCase("Goodbye")){
			eliza = new Eliza(myPatternList,inputLine);
			System.out.println("I: " + inputLine);
			System.out.println("Eliza: " + eliza.generateAnswer());
		}

		System.out.println("Game Over, Byebye");

	}
}

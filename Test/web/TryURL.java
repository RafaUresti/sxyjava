package web;
import java.net.*;
import java.io.*;
public class TryURL {
	public static void main(String[] args) {
		try { 
			BufferedReader input;
			String line;
			URL url = new URL(
			"http://www.cis.upenn.edu/~matuszek/cit597-2008");
			input = new BufferedReader(
					new InputStreamReader(url.openStream()));
			line = input.readLine();
			while (line != null) {
				System.out.println(line);
				line = input.readLine();
			}
			input.close();

		} catch (Exception e) {
			 }
	}
}

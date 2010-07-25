package edu.upenn.cis505.g48.utility;
import java.io.*;

public class Test{
	

	public static void main(String[] args) {
		test_email te1 = new test_email( 123, "HI test");
		
		System.out.println("Test Serializalbe.");
		System.out.println("The original email is: "+ te1.title+ "  " + te1.content);
		
		String file_n = "test_email1";
		try {
			Utility.write_to(te1, file_n);
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		test_email te2 = new test_email();
		try {
			te2 = (test_email) Utility.read_from(file_n);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println("The readout email is: "+ te2.title+ "  " + te2.content);
		
	}
	
}
package cis551proj3.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cis551proj3.cracker.SDES3Cracker;
import cis551proj3.cracker.SDESCracker;

public class Main {
	
	public static enum Encryption{
		SDES,
		THREE_SDES
	}
	
	public static void main(String[] argv){
		
		System.out.println("Welcome to our cracker!");
		
		// Check to make sure has the right arguments
		if(argv.length != 3){
			printUsage();
			return;
		}
		
		// Set the encryption type
		Encryption encryptType = Encryption.SDES;
		String cypherTextAscii = "";
		if(argv[0].equalsIgnoreCase("SDES")){
			encryptType = Encryption.SDES;
			System.out.println("SDES Cracking Enabled");
		}
		else if(argv[0].equalsIgnoreCase("3SDES")){
			encryptType = Encryption.THREE_SDES;
			System.out.println("3-SDES Cracking Enabled");
		}
		else{
			System.out.println("Invalid encryption type " + argv[0]);
			return;
		}

		// Read in the cypher text from the given file name
		try{
			BufferedReader cryptInput =  new BufferedReader(new FileReader(argv[1]));
			cypherTextAscii = cryptInput.readLine();
			cryptInput.close();
		}catch(IOException e){
			System.out.println("Error in reading message file " + argv[1]);
			return;
		}
		System.out.println("Read message file " + argv[1]);

		
		// Execute a SDES cracker attack
		if(encryptType.equals(Encryption.SDES)){
			SDESCracker cracker = new SDESCracker();
			
			try{
				cracker.loadDictionary(argv[2]);
			}catch(IOException e){
				System.out.println("I/O error when loading dictionary file " + argv[2]);
				return;
			}
			
			// Pack the cypher text into a byte array
			byte[] cypherText = new byte[cypherTextAscii.length()];
			for(int i = 0; i < cypherTextAscii.length(); i++){
				cypherText[i] = (cypherTextAscii.charAt(i) == '0' ? (byte)0 : (byte)1);
			}
			
			// Run the SDES Cracker
			try{
				cracker.crack(cypherText);
			}catch(IOException e){
				System.out.println("I/O Exception when reading dictionary " + e.getMessage());
			}
		}
		
		// Execute a 3-SDES cracker attack
		if(encryptType.equals(Encryption.THREE_SDES)){
			SDES3Cracker cracker = new SDES3Cracker();
			
			try{
				cracker.loadDictionary(argv[2]);
			}catch(IOException e){
				System.out.println("I/O error when loading dictionary file " + argv[2]);
				return;
			}
			// Pack the cypher text into a byte array
			byte[] cypherText = new byte[cypherTextAscii.length()];
			for(int i = 0; i < cypherTextAscii.length(); i++){
				cypherText[i] = (cypherTextAscii.charAt(i) == '0' ? (byte)0 : (byte)1);
			}
			
			// Run the SDES-3 Cracker
			try{
				cracker.crack(cypherText);
			}catch(IOException e){
				System.out.println("I/O Exception when reading dictionary " + e.getMessage());
			}
		}
	}
	
	public static void printUsage(){
		System.out.println("Usage: java Main.class <encryption> <cyphertext file> <dict file>");
		System.out.println("   - where <encryption> is 'SDES' or '3SDES'");
		System.out.println("   - where <cyphertext file> is the file with the cyphertext of 1's and 0's");
		System.out.println("   - where <dict file> is the text file with a list of \n seperated words");
	}
}

package cis551proj3.cracker;

import java.io.IOException;

import cis551proj3.cascii.*;
import cis551proj3.dict.*;
import cis551proj3.sdes.*;

public class SDES3Cracker {
	private final static int SCREEN_BYTES = 250;
	private final static double THRESHOLD = 0.4;
	Dictionary dict = null;

	public SDES3Cracker(){
		dict = new Dictionary();

	}
	
	public void loadDictionary(String dictFile) throws IOException{
		dict.loadFile(dictFile);
	}

	/**
	 * brute force implementation for crack
	 * @param cipherText
	 * @throws IOException
	 */
	public void crack(byte[] cipherText) throws IOException{

		byte[] keyTotal = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] key1 = {0,0,0,0,0,0,0,0,0,0};
		byte[] key2 = {0,0,0,0,0,0,0,0,0,0};
		byte[] shortCipherText = new byte[SCREEN_BYTES];

		for(int i = 0; i < Math.pow(2, 20); i++){

			// Calculate the key given the number i
			boolean carry = true;
			for(int j = 19; j >= 0; j--){
				if(!carry) break;

				if(keyTotal[j] == (byte)1){
					carry = true;
					keyTotal[j] = (byte)0;
				}
				else{
					keyTotal[j] = (byte)1;
					carry = false;
				}
			}

			System.arraycopy(keyTotal, 0, key1, 0, 10);
			System.arraycopy(keyTotal, 10, key2, 0, 10);

			String plainTextString = "";
			String shortTextString = "";
			
			// get first 250 bits if the cipher text is too long
			if (cipherText.length > SCREEN_BYTES){
				System.arraycopy(cipherText, 0, shortCipherText, 0, SCREEN_BYTES);
				shortTextString = CASCII.toString(TripleSDES.Decrypt(key1, key2, shortCipherText));
				if (countWords(shortTextString) < 4){
					printKeyProgress(i);
					continue;
				}
				else {
					plainTextString = CASCII.toString(TripleSDES.Decrypt(key1, key2, cipherText));
				}
			}
			else {
				plainTextString = CASCII.toString(TripleSDES.Decrypt(key1, key2, cipherText));
			}
			
			// checking the threshold
			if (countWords(plainTextString) > 3 && wordsRatio(plainTextString) > THRESHOLD){
				System.out.println("The plain text is already decrypted:");
				System.out.println(plainTextString);
				System.out.print("The keys are: ");
				SDES.printb(key1);
				System.out.print(" and ");
				SDES.printb(key2);
				System.out.println();
			}
			printKeyProgress(i);
		}
		System.out.println("Cracking finished.");
	}

	/**
	 * print job
	 * @param i
	 */
	private void printKeyProgress(int i){
		if (i == 0){
			System.out.println("Key search in progress:");
		}
		else if(i % 100000 == 0){
			System.out.println("Tried " + i +" possible keys");
		}
	}
	
	/**
	 * count correct matching out of total words
	 * @param text
	 * @return
	 */
	private int countWords(String text){
		String[] words = text.split(" |\\.|,|'|\\?|:");
		int wordCount = 0;
		for(String word : words){
			if(word != null){
				if(dict.lookup(word)){
					wordCount++;
				}
			}
		}
		return wordCount;
	}
	private double wordsRatio(String text){
		String[] words = text.split(" |\\.|,|'|\\?|:");
		int wordCount = 0;
		int totalWord = 0;
		for(String word : words){
			if(word != null){
				if(dict.lookup(word)){
					wordCount++;
				}
				totalWord++;
			}
		}
		return (double)wordCount/(double)totalWord;
	}
}
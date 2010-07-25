package cis551proj3.cracker;

import java.io.IOException;

import cis551proj3.cascii.*;
import cis551proj3.dict.*;
import cis551proj3.sdes.*;

public class SDESCracker {
	
	Dictionary dict = null;
	private double threshold = 0.4;
	
	public SDESCracker(){
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
	public void crack(byte[] cypherText) throws IOException{
		
		byte[] key = {0,0,0,0,0,0,0,0,0,0};
		byte[] plainText = new byte[cypherText.length];
		
		for(int i = 0; i < Math.pow(2, 10); i++){
			
			// Calculate the key given the number i
			int temp = i;
			for(int j = 9; j >= 0; j--){
				if(temp >= Math.pow(2, j)){
					temp = temp - (int)Math.pow(2, j);
					key[j] = 1;
				}
				else{
					key[j] = 0;
				}
			}
			
			byte[] cypherTextLetter = new byte[8];
			
			for(int j = 0; j < cypherText.length / 8; j++){
				System.arraycopy(cypherText, (j*8), cypherTextLetter, 0, 8);
				byte[] plainTextLetter= SDES.Decrypt(key, cypherTextLetter);
				System.arraycopy(plainTextLetter, 0, plainText, (j*8), 8);
			}

			String plainTextString = CASCII.toString(plainText);
			String[] words = plainTextString.split(" |\\.|,|'|\\?|:");
			
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
			
			// checking the threshold
			if(wordCount >= 3){
				if((double)wordCount/(double)totalWord >= threshold){
					System.out.println("The plain text is already decrypted:");
					System.out.println(plainTextString);
					System.out.print("The key is: ");
					for(int k=0;k<key.length;k++)
						System.out.print(key[k]);
					System.out.println();
					//return;
					//System.out.println(wordCount+" "+totalWord);
				}
			}
		}
		System.out.println("Cracking finished.");
	}
	
	
}
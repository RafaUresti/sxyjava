package cis551proj3.cracker;

import java.io.IOException;

import cis551proj3.cascii.*;
import cis551proj3.dict.*;
import cis551proj3.sdes.*;

public class DES3Cracker {
	
	Dictionary dict = null;
	
	public DES3Cracker(){
		dict = new Dictionary();
		try{
			dict.loadFile("DICT.TXT");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void crack(byte[] cypherText) throws IOException{
		
		byte[] keyTotal = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] key1 = {0,0,0,0,0,0,0,0,0,0};
		byte[] key2 = {0,0,0,0,0,0,0,0,0,0};
		byte[] plainText = new byte[cypherText.length];
		
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
			
			byte[] cypherTextLetter = new byte[8];
			
			for(int j = 0; j < cypherText.length / 8; j++){
				System.arraycopy(cypherText, (j*8), cypherTextLetter, 0, 8);
				byte[] plainTextLetter = TripleSDES.Decrypt(key1, key2, cypherTextLetter);
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
			
			if(wordCount >= 4){
				if((double)wordCount/(double)totalWord >= 0.4){
					System.out.println("The plain text is already decrypted:");
					System.out.println(plainTextString);
					System.out.print("The keys are: ");
					SDES.printb(key1);
					System.out.print(" and ");
					SDES.printb(key2);
					System.out.println();
					return;
					//System.out.println(wordCount+" "+totalWord);
				}
			}
			
			if(i % 10000 == 0){
				System.out.println(i);
			}
		}
	}
	
	
}
package classesForTesting;

import java.io.IOException;

import cis551proj3.sdes.TripleSDES;
import cis551proj3.cascii.CASCII;
import cis551proj3.cracker.SDES3Cracker;


public class SDES3CrackerTester {
	
	public static void main(String[] argc){
		
		// Create a new key
		byte[] key1 = {0,0,0,0,0,0,0,0,1,1,0};
		byte[] key2 = {0,0,1,0,0,0,1,1,1,1,0};

		byte[] plainText = CASCII.Convert("HELLO THIS IS A MESSAGE");
		byte[] cypherText = new byte[plainText.length];
		byte[] plainLetter = new byte[8];
		byte[] cypherLetter = null;
		
		SDES3Cracker k = new SDES3Cracker();
		
		for(int i = 0; i < plainText.length / 8; i++){

			System.arraycopy(plainText, (i*8), plainLetter, 0, 8);

			cypherLetter = 
				TripleSDES.Encrypt(key1, key2, plainLetter);

			System.arraycopy(cypherLetter, 0, cypherText, (i*8), 8);
		}
		
		try{
			k.crack(cypherText);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

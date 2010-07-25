package Testing;

import java.io.IOException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Arrays;

import cis551proj4.Crypto;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long ts = System.currentTimeMillis();
		Signature s1  = new Signature ("a", "b", "c", ts);
		Signature s2 = new Signature ("a", "b", "c", ts);
		Signature s3 = new Signature ("c", "b", "c", ts);
		
		Crypto crypto = new Crypto();
		KeyPair kp = crypto.makeRSAKeyPair();
		Key k = crypto.makeRijndaelKey();
		
		try {
			System.out.println("Size = " + crypto.objToBytes(k).length);
			byte[] encrypted = crypto.encryptRSA(k, kp.getPublic());
			System.out.println("Size = " + encrypted.length);
			System.out.println("Size = " + (crypto.objToBytes((Key)crypto.decryptRSA(encrypted, kp.getPrivate()))).length);
			byte[] encrypted2 = crypto.encryptRijndael(k, k);
			System.out.println("Size = " + encrypted2.length);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

}

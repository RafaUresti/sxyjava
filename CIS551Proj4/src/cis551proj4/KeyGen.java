package cis551proj4;
import java.security.*;
import javax.crypto.*;
//import cryptix.provider.rsa.*;
//import cryptix.provider.key.*;

/* This program creates a Key or KeyPair and writes it to disk */
public class KeyGen {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java KeyGen <RSA | Rijndael> <filename>");
			System.out.println("If the first argument is RSA, creates a " +
			"cryptix RSA KeyPair object.");
			System.out.println("If the first argument is Rijndael, creates " +
			"a cryptix Rijndael Key object.");
			System.exit(0);
		}
		try {
			SecureRandom sr = new SecureRandom();

			if (args[0].equals("RSA")) {
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(1024,sr);
				KeyPair kp = kpg.generateKeyPair();
				Disk.save(kp, args[1]);
			} else if (args[0].equals("Rijndael")) {
				KeyGenerator kg = KeyGenerator.getInstance("AES");
				kg.init(sr);
				Key key = kg.generateKey();
				Disk.save(key, args[1]);
			} else {
				System.out.println("First arg must be 'RSA' or 'Rijndael'");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

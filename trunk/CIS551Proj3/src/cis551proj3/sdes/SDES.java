package cis551proj3.sdes;

import cis551proj3.cascii.CASCII;

/**
 * Implementation class for SDES 
 * @author Allenxu
 *
 */

public class SDES {
	private static final int P10[] = { 3, 5, 2, 7, 4, 10, 1, 9, 8, 6 };
	private static final int P8[] = { 6, 3, 7, 4, 8, 5, 10, 9 };
	private static final int P4[] = { 2, 4, 3, 1 };
	private static final int IP[] = { 2, 6, 3, 1, 4, 8, 5, 7 };
	private static final int IPI[] = { 4, 1, 3, 5, 7, 2, 8, 6 };
	private static final int EP[] = { 4, 1, 2, 3, 2, 3, 4, 1 };
	private static final int S0[][] = { { 1, 0, 3, 2 }, { 3, 2, 1, 0 },
		{ 0, 2, 1, 3 }, { 3, 1, 3, 2 } };
	private static final int S1[][] = { { 0, 1, 2, 3 }, { 2, 0, 1, 3 },
		{ 3, 0, 1, 0 }, { 2, 1, 0, 3 } };

	private static byte[] cipher;
	private static byte[] plain;
	private static byte[] key1;
	private static byte[] key2;
	
	/**
	 * Takes in key and plain text and encrypt with SDES algorithm
	 * @param rawkey The key for SDES algorithm
	 * @param plaintext The plain text to encrypt
	 * @return The encrypted text
	 */
	public static byte[] Encrypt(byte[] rawkey, byte[] plaintext){
		generateKeys(rawkey);
		return convert(plaintext, true);
	}
	
	/**
	 * Takes in key and cipher text and decrypt with SDES algorithm
	 * @param rawkey The key for SDES algorithm
	 * @param ciphertext The cypher text to decrypt
	 * @return The decrypted text
	 */
	public static byte[] Decrypt(byte[] rawkey, byte[] ciphertext){
		generateKeys(rawkey);
		return convert(ciphertext, false);
	}
	
	/**
	 * Generate 2 sub-keys for SDES algorithm according to raw key
	 * @param rawkey The raw key for generating sub-keys
	 */
	private static void generateKeys(byte[] rawkey){
		byte[] LS1l;
		byte[] LS1r;
		byte[] LS2l;
		byte[] LS2r;
		LS1l = permute(rawkey, P10, 0, P10.length/2);
		LS1r = permute(rawkey, P10, P10.length/2, P10.length);
		LS1l = shift(LS1l, 1);
		LS1r = shift(LS1r, 1);
		rawkey = combine(LS1l, LS1r);
		key1 = permute(rawkey, P8, 0, P8.length);
		LS2l = shift(LS1l, 2);
		LS2r = shift(LS1r, 2);
		rawkey = combine(LS2l, LS2r);
		key2 = permute(rawkey, P8, 0, P8.length);
	}
	
	/**
	 * Converts between encrypted and decrypted bits.
	 * @param srcBits The bits to convert
	 * @param encrypt <code>True</code> if doing encrypting, <code>False</code> if doing decrypting
	 * @return
	 */
	private static byte[] convert(byte[] srcBits, boolean encrypt){
		byte[] destBits = new byte[srcBits.length];
		for (int i = 0; i < srcBits.length - 7; i += 8) {
			byte[] letterBits = new byte[8];
			byte[] convertedLetterBits = new byte[8];
			System.arraycopy(srcBits, i, letterBits, 0, 8);
			if (encrypt){
				convertedLetterBits = encrypt(letterBits);
			}
			else {
				convertedLetterBits = decrypt(letterBits);
			}
			System.arraycopy(convertedLetterBits, 0, destBits, i, 8);
		}
		return destBits;
	}
	/**
	 * The entrance for encryption, 8-bits plain will be the input, 
	 * and 8-bits cipher text will be returned 
	 * @param plainText
	 * @return cipher
	 */
	private static byte[] encrypt(byte[] plainText){
		plain = permute(plainText, IP, 0, IP.length);
		plain = fk(plain, key1);
		plain = sw(plain);
		plain = fk(plain, key2);
		cipher = permute(plain, IPI, 0, IPI.length);
		return cipher;
	}
	/**
	 * The entrance for decryption, 8-bits cipher will be the input, 
	 * and 8-bits plain text will be returned 
	 * @param cipherText
	 * @return
	 */
	private static byte[] decrypt(byte[] cipherText){
		cipher = permute(cipherText, IP, 0, IP.length);
		cipher = fk(cipher, key2);
		cipher = sw(cipher);
		cipher = fk(cipher, key1);
		plain = permute(cipher, IPI, 0, IPI.length);
		return plain;
	}

	/**
	 * Print job for byte arrays
	 * @param text
	 */
	public static void printb(byte[] text){
		for(int i=0;i<text.length;i++)
			System.out.print(text[i]);
	}

	/**
	 * static method to join two byte arrays into big one
	 * @param left
	 * @param right
	 * @return
	 */
	private static byte[] combine(byte[] left, byte[] right){
		byte[] returnPlain = new byte[left.length+right.length];
		for(int i = 0; i < left.length; i++){
			returnPlain[i] = left[i];
			returnPlain[i+left.length] = right[i];
		}
		return returnPlain;
	}

	/**
	 * shift "bit" number of bits in a byte array 
	 * @param prePlain
	 * @param bit
	 * @return
	 */
	private static byte[] shift(byte[] prePlain, int bit){
		byte[] postPlain = new byte[prePlain.length];
		for(int i = 0; i < prePlain.length; i++)
			postPlain[i] = prePlain[(i+bit)%prePlain.length];
		return postPlain;
	}

	/**
	 * permute the given plain text, according to given pArray, by the step
	 * from pStart to pStop
	 * @param prePlain
	 * @param pArray
	 * @param pStart
	 * @param pStop
	 * @return
	 */
	private static byte[] permute(byte[] prePlain, int[] pArray, int pStart, int pStop) {
		byte[] postPlain = new byte[pStop-pStart];
		for(int i = pStart; i < pStop; i++){
			postPlain[i-pStart] = prePlain[pArray[i] - 1];
		}
		return postPlain;
	}

	/**
	 * algorithm for fk implementation.
	 * @param text
	 * @param key
	 * @return
	 */
	private static byte[] fk(byte[] text, byte[] key) {
		byte[] left = new byte[text.length/2];
		byte[] right = new byte[text.length/2];
		byte[] ep = new byte[key.length];
		byte[] f = new byte[4];
		int t0, t1;
		//split into left and right
		for(int i=0;i<text.length/2;i++){
			left[i] = text[i];
			right[i] = text[i+text.length/2];		
		}
		//E/P
		ep = permute(right, EP, 0, 8);
		//XOR with sub key
		for(int i=0;i<ep.length;i++)
			ep[i] = (byte) (ep[i] ^ key[i]);
		//Index for sand-box
		t0 = S0[(int)ep[0]*2+(int)ep[3]][(int)ep[1]*2+(int)ep[2]];
		t1 = S1[(int)ep[4]*2+(int)ep[7]][(int)ep[5]*2+(int)ep[6]];
		f[0] = (byte) (t0/2);
		f[1] = (byte) (t0%2);
		f[2] = (byte) (t1/2);
		f[3] = (byte) (t1%2);
		//P4
		f = permute(f, P4, 0, P4.length);
		//XOR
		for(int i=0;i<left.length;i++)
			left[i] = (byte) (left[i]^f[i]);
		//Combine
		for(int i=0;i<text.length/2;i++){
			text[i] = left[i];
			text[i+text.length/2] = right[i];		
		}
		return text;
	}

	/**
	 * switch left and right part for a given text
	 * @param preText
	 * @return
	 */
	private static byte[] sw(byte[] preText) {
		byte[] postText = new byte[preText.length];
		for(int i=0;i<preText.length;i++){
			postText[i] = preText[(i+preText.length/2)%preText.length];
		}
		return postText;
	}

}
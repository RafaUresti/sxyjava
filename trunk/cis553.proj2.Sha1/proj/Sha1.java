/**This code is modified from:
 * http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
 */
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1 {

	private static String byteArrayToDecString(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		}
		//buf is the HEX representation of the data, which is converted to dec
		return convertToDec(buf.toString());
	}

	public static String convertToDec(String st){
		String dec = (new BigInteger(st,16)).toString();
		return dec;
	}
	
	public static String SHA1(String text)
	throws NoSuchAlgorithmException, UnsupportedEncodingException  {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		sha1hash = md.digest();
		return byteArrayToDecString(sha1hash);
	}
}


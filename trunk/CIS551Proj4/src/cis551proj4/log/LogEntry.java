package cis551proj4.log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import cis551proj4.Crypto;


public class LogEntry implements Serializable{
	byte[][] cryptObject;
	
	public static final long serialVersionUID = 234675423567543L;
	public static final int MAX_ENCRYPT = 210;
	
	public LogEntry(Serializable object, PublicKey key) 
		throws IOException, KeyException {
		
		Crypto crypt = new Crypto();

		byte[] origObj = crypt.objToBytes(object);
		
		int rows = (origObj.length / MAX_ENCRYPT) + 
			((origObj.length % MAX_ENCRYPT) > 0 ? 1 : 0);
		
		cryptObject = new byte[rows][];
		
		for(int i = 0; i < rows; i++){
			int size = (origObj.length - i * MAX_ENCRYPT > MAX_ENCRYPT) ? 
					MAX_ENCRYPT : origObj.length - i * MAX_ENCRYPT;

			cryptObject[i] = new byte[size];
			System.arraycopy(origObj, i * MAX_ENCRYPT, cryptObject[i], 0, size);
//			System.out.println(size+" "+cryptObject[i].length);
			cryptObject[i] = crypt.encryptRSA(cryptObject[i], key);
		}
	}

	public Object decryptObject(PrivateKey key) throws IOException, KeyException{
		Crypto crypt = new Crypto();

		int cryptSize = cryptObject[cryptObject.length - 1].length == MAX_ENCRYPT ?
				cryptObject.length * MAX_ENCRYPT : 
					((cryptObject.length - 1) * MAX_ENCRYPT) + cryptObject[cryptObject.length - 1].length;
		
		byte[] decryptObj = new byte[cryptSize];
		
		for(int i = 0; i < cryptObject.length; i++){
			byte[] temp = (byte[])crypt.decryptRSA(cryptObject[i], key);
			System.arraycopy(temp, 0, decryptObj, MAX_ENCRYPT * i, temp.length);
		}
		
		
		ByteArrayInputStream buf = new ByteArrayInputStream(decryptObj);
		ObjectInputStream oos = new ObjectInputStream(buf);

		try{
			return oos.readObject();
		} catch(ClassNotFoundException e){
			return null;
		}
	}
}

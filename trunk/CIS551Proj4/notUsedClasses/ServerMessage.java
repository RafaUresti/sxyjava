package cis551proj4;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.*;

public class ServerMessage implements Serializable {

	byte[] keyBytes;
	long nonce;
	String acctNum;
	
	public ServerMessage(){}
	
	public byte[] getKeyBytes() {
		return keyBytes;
	}

	public void setKeyBytes(byte[] keyBytes) {
		this.keyBytes = keyBytes;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public String getAcctNum() {
		return acctNum;
	}

	public void setAcctNum(String acctNum) {
		this.acctNum = acctNum;
	}
//	public class CryptoPackage implements Serializable{
//		
//		public static final long serialVersionUID = 12349423445L;
//		
//		byte[] keyBytes;
//		long nonce;
//		String acctNum;
//		
//		public CryptoPackage(Key key, long nonce, String acctNum){
//			writeKey(key);
//			this.nonce = nonce;
//			this.acctNum = acctNum;
//		}
//		
//		public void writeKey(Key encryptKey){
//			try{
//				ByteArrayOutputStream bos = new ByteArrayOutputStream();
//				ObjectOutputStream oos = new ObjectOutputStream(bos);
//				oos.writeObject(encryptKey);
//				oos.flush();
//				oos.close();
//				bos.close();
//				keyBytes = bos.toByteArray();
//			}catch(Exception e){
//				e.printStackTrace();
//				return;
//			}
//		}
//		
//		public Key readKey(){
//			try{
//				ByteArrayInputStream bos = new ByteArrayInputStream(keyBytes);
//				ObjectInputStream oos = new ObjectInputStream(bos);
//				return (Key)oos.readObject();
//			}catch(Exception e){
//				e.printStackTrace();
//				return null;
//			}
//		}
//		
//		public byte[] getKeyBytes() {
//			return keyBytes;
//		}
//		public void setKeyBytes(byte[] keyBytes) {
//			this.keyBytes = keyBytes;
//		}
//		public long getNonce() {
//			return nonce;
//		}
//		public void setNonce(long nonce) {
//			this.nonce = nonce;
//		}
//		public String getAcctNum() {
//			return acctNum;
//		}
//		public void setAcctNum(String acctNum) {
//			this.acctNum = acctNum;
//		}
//	}
//	
//	private static final long serialVersionUID = -4587533542598246690L;
//	private byte[] encryptedData;
//	
//	public ServerMessage(byte[] Data){
//		this.encryptedData = Data;
//	}
//	
//	public ServerMessage(CryptoPackage pack) throws IOException{
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		ObjectOutputStream oos = new ObjectOutputStream(bos);
//		oos.writeObject(pack);
//		oos.flush();
//		oos.close();
//		bos.close();
//		encryptedData = bos.toByteArray();
//	}
//	
//	public CryptoPackage getCryptoPackage(){
//		try{
//			ByteArrayInputStream bos = new ByteArrayInputStream(encryptedData);
//			ObjectInputStream oos = new ObjectInputStream(bos);
//			return (CryptoPackage)oos.readObject();
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	
//	public byte[] getData(){
//		return encryptedData;
//	}
}

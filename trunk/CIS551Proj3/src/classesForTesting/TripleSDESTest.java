//package cis551proj3.sdes;
//
//import org.junit.Test;
//
//
//public class TripleSDESTest {
//
//	@Test
//	public void test3Encryption1(){
//		byte[] key1 = { 0,0,0,0,0,0,0,0,0,0 };
//		byte[] key2 = { 0,0,0,0,0,0,0,0,0,0 };
//		byte[] plaintext = {0,0,0,0,0,0,0,0};
//		byte[] target = TripleSDES.Encrypt(key1, key2, plaintext);
//		System.out.println("3SDES table:");
//		SDES.printb(target);
//		System.out.println();
//	}
//	
//	@Test
//	public void test3Encryption2(){
//		byte[] key1 = { 1,0,0,0,1,0,1,1,1,0 };
//		byte[] key2 = { 0,1,1,0,1,0,1,1,1,0 };
//		byte[] plaintext = {1,1,0,1,0,1,1,1};
//		byte[] target = TripleSDES.Encrypt(key1, key2, plaintext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void test3Encryption3(){
//		byte[] key1 = { 1,0,0,0,1,0,1,1,1,0 };
//		byte[] key2 = { 0,1,1,0,1,0,1,1,1,0 };
//		byte[] plaintext = {1,0,1,0,1,0,1,0};
//		byte[] target = TripleSDES.Encrypt(key1, key2, plaintext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void test3Encryption4(){
//		byte[] key1 = { 1,1,1,1,1,1,1,1,1,1 };
//		byte[] key2 = { 1,1,1,1,1,1,1,1,1,1 };
//		byte[] plaintext = {1,0,1,0,1,0,1,0};
//		byte[] target = TripleSDES.Encrypt(key1, key2, plaintext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void test3Decryption1(){
//		byte[] key1 = { 1,0,0,0,1,0,1,1,1,0 };
//		byte[] key2 = { 0,1,1,0,1,0,1,1,1,0 };
//		byte[] cyphertext = {1,1,1,0,0,1,1,0};
//		byte[] target = TripleSDES.Decrypt(key1, key2, cyphertext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void test3Decryption2(){
//		byte[] key1 = { 1,0,1,1,1,0,1,1,1,1 };
//		byte[] key2 = { 0,1,1,0,1,0,1,1,1,0 };
//		byte[] cyphertext = {0,1,0,1,0,0,0,0};
//		byte[] target = TripleSDES.Decrypt(key1, key2, cyphertext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void test3Decryption3(){
//		byte[] key1 = { 0,0,0,0,0,0,0,0,0,0}; 
//		byte[] key2 = { 0,0,0,0,0,0,0,0,0,0}; 
//		byte[] cyphertext = {1,0,0,0,0,0,0,0};
//		byte[] target = TripleSDES.Decrypt(key1, key2, cyphertext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void test3Decryption4(){
//		byte[] key1 = { 1,1,1,1,1,1,1,1,1,1 };
//		byte[] key2 = { 1,1,1,1,1,1,1,1,1,1 };
//		byte[] cyphertext = {1,0,0,1,0,0,1,0};
//		byte[] target = TripleSDES.Decrypt(key1, key2, cyphertext);
//		SDES.printb(target);
//		System.out.println();
//	}
//}

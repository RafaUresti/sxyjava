//package cis551proj3.sdes;
//
//import cis551proj3.sdes.SDES;
//
//
//public class SDESTest {
//
//	@Test
//	public void testEncryption1(){
//		byte[] key = { 0,0,0,0,0,0,0,0,0,0 };
//		byte[] plaintext = {1,0,1,0,1,0,1,0};
//		byte[] target = SDES.Encrypt(key, plaintext);
//		byte[] cypherText = {0,0,0,1,0,0,0,1};
//		assertArrayEquals(target, cypherText);
//	}
//	
//	@Test
//	public void testEncryption2(){
//		byte[] key = { 1,1,1,0,0,0,1,1,1,0 };
//		byte[] plaintext = {1,0,1,0,1,0,1,0};
//		byte[] target = SDES.Encrypt(key, plaintext);
//		byte[] cypherText = {1,1,0,0,1,0,1,0};
//		assertArrayEquals(target, cypherText);
//	}
//	@Test
//	public void testEncryption3(){
//		byte[] key = { 1,1,1,0,0,0,1,1,1,0 };
//		byte[] plaintext = {0,1,0,1,0,1,0,1};
//		byte[] target = SDES.Encrypt(key, plaintext);
//		byte[] cypherText = {0,1,1,1,0,0,0,0};
//		assertArrayEquals(target, cypherText);
//	}
//	
//	@Test
//	public void testEncryption4(){
//		byte[] key = { 1,1,1,1,1,1,1,1,1,1};
//		byte[] plaintext = {1,0,1,0,1,0,1,0};
//		byte[] target = SDES.Encrypt(key, plaintext);
//		byte[] cypherText = {0,0,0,0,0,1,0,0};
//		assertArrayEquals(target, cypherText);
//	}
//	
//	@Test
//	public void testEncryption11(){
//		byte[] key = { 0,0,0,0,0,0,0,0,0,0 };
//		byte[] plaintext = { 0,0,0,0,0,0,0,0 };
//		byte[] target = SDES.Encrypt(key, plaintext);
//		System.out.println("SDES table:");
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void testEncryption12(){
//		byte[] key = { 1,1,1,1,1,1,1,1,1,1};
//		byte[] plaintext = {1,1,1,1,1,1,1,1};
//		byte[] target = SDES.Encrypt(key, plaintext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void testEncryption13(){
//		byte[] key = { 0,0,0,0,0,1,1,1,1,1 };
//		byte[] plaintext = {0,0,0,0,0,0,0,0};
//		byte[] target = SDES.Encrypt(key, plaintext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void testEncryption14(){
//		byte[] key = { 0,0,0,0,0,1,1,1,1,1 };
//		byte[] plaintext = {1,1,1,1,1,1,1,1};
//		byte[] target = SDES.Encrypt(key, plaintext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void testDecryption11(){
//		byte[] key = { 1,0,0,0,1,0,1,1,1,0 };
//		byte[] cyphertext = {0,0,0,1,1,1,0,0};
//		byte[] target = SDES.Decrypt(key, cyphertext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void testDecryption12(){
//		byte[] key = { 1,0,0,0,1,0,1,1,1,0 };
//		byte[] cyphertext = {1,1,0,0,0,0,1,0};
//		byte[] target = SDES.Decrypt(key, cyphertext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void testDecryption13(){
//		byte[] key = { 0,0,1,0,0,1,1,1,1,1 };
//		byte[] cyphertext = {1,0,0,1,1,1,0,1};
//		byte[] target = SDES.Decrypt(key, cyphertext);
//		SDES.printb(target);
//		System.out.println();
//	}
//	@Test
//	public void testDecryption14(){
//		byte[] key = { 0,0,1,0,0,1,1,1,1,1 };
//		byte[] cyphertext = {1,0,0,1,0,0,0,0};
//		byte[] target = SDES.Decrypt(key, cyphertext);
//		SDES.printb(target);
//		System.out.println();
//	}
//}

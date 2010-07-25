//// 1000101110 0110101110 10111110 0000000000 1000100010
//
//package cis551proj3.sdes;
//
//class TripleSDEStest{
//	public static void main(String[] args) throws Exception {
//		
//		/*
//		args[0] = "1000101110";
//		args[1] = "0110101110";
//		args[2] = "10111110";
//		args[3] = "0000000000";
//		args[4] = "1000100010";
//		*/
//		byte[] key1 = args[0].getBytes();
//		byte[] key2 = args[1].getBytes();
//		byte[] text = args[2].getBytes();
//		byte[] key3 = args[3].getBytes();
//		byte[] key4 = args[4].getBytes();
//		byte[] text1 = new byte[text.length];
//		System.arraycopy(text, 0, text1, 0, text.length);
//
//		for(int i=0;i<key1.length;i++){
//			key1[i] = (byte) (key1[i] - 48);
//			key2[i] = (byte) (key2[i] - 48);
//			key3[i] = (byte) (key3[i] - 48);
//			key4[i] = (byte) (key4[i] - 48);
//		}
//		for(int i=0;i<text.length;i++){
//			text[i] = (byte) (text[i] - 48);
//			text1[i] = (byte) (text1[i] - 48);
//		}
//		
//		SDES.printb(text);
//		SDES.printb(text1);
//		
//		SDES sdes1 = new SDES(key1);
//		SDES sdes2 = new SDES(key2);
//		SDES sdes3 = new SDES(key3);
//		SDES sdes4 = new SDES(key4);
//		
//		text = sdes1.decrypt(sdes2.encrypt(sdes1.decrypt(text)));
//		SDES.printb(text);
//		SDES.printb(key1);
//		SDES.printb(key2);
//		text1 = sdes3.decrypt(sdes4.encrypt(sdes3.decrypt(text1)));
//		SDES.printb(text1);
//		SDES.printb(key3);
//		SDES.printb(key4);
//	}
//}
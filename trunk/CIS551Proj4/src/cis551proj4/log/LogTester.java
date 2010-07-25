package cis551proj4.log;

import java.io.IOException;
import java.security.KeyException;
import java.security.KeyPair;

import cis551proj4.Crypto;

public class LogTester {
	public static void main(String argv[]){
		stringTest();
		exceptionTest();
		logMessageTest();
		logMessageFileTest();
	}
		
	public static void stringTest(){
		try{
			String s = "asl;dkfjas;dasl;dkfjas;dlkfjasdf;lkasjdf;alksdjfasd;lkfjasd;flka" +
					"jsdf;lasasl;dkfjas;dlkfjasdf;lkasjdf;alksdjfasd;lkfjasd;flkajsdf;la" +
					"sasl;dkfjas;dlkfjasdf;lkasjdf;alksdjfasd;lkfjasd;flkajsdf;lasasl;dk" +
					"fjas;dlkfjasdf;lkasjdf;alksdjfasd;lkfjasd;flkajsdf;lasasl;dkfjas;dl" +
					"kfjasdf;lkasjdf;alksdjfasd;lkfjasd;flkajsdf;lasasl;dkfjas;dlkfjasdf;" +
					"lkasjdf;alksdjfasd;lkfjasd;flkajsdf;lasasl;dkfjas;dlkfjasdf;lkasjdf;a" +
					"lksdjfasd;lkfjasd;flkajsdf;lasasl;dkfjas;dlkfjasdf;lkasjdf;alksdjfasd;" +
					"lkfjasd;flkajsdf;lasasl;dkfjas;dlkfjasdf;lkasjdf;alksdjfasd;lkfjasd;flk" +
					"ajsdf;lasasl;dkfjas;dlkfjasdf;lkasjdf;alksdjfasd;lkfjasd;flkajsdf;lasasl;" +
					"dkfjas;dlkfjasdf;lkasjdf;alksdjfasd;lkfjasd;flkajsdf;lasasl;dkfjas;dlkfja" +
					"sdf;lkasjdf;alksdjfasd;lkfjasd;flkajsdf;lasasl;dkfjas;dlkfjasdf;lkasjdf;al" +
					"sdjfasd;lkfjasd;flkajsdf;laslkfjasdf;lkasjdf;alksdjfasd;lkfjasd;flkajsdf;las";
			
			Crypto c = new Crypto();
			KeyPair k = c.makeRSAKeyPair();
			LogEntry e = new LogEntry(s, k.getPublic());
			String q = (String)e.decryptObject(k.getPrivate());
			
			System.out.println(q);
			
			if(!s.equals(q)){
				System.err.println("FAILED STRING ENCRYPTION");
				return;
			}
			
		}catch(KeyException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void exceptionTest(){
		try{
			Exception t = new Exception("test exception");
			
			Crypto c = new Crypto();
			KeyPair k = c.makeRSAKeyPair();
			LogEntry e = new LogEntry(t, k.getPublic());
			Exception q = (Exception)e.decryptObject(k.getPrivate());
			
			System.out.println(q.getMessage());
			
		}catch(KeyException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void logMessageTest(){
		try{
			LogMessage t = new LogMessage();
			t.info = "TEST TEST TEST";
			
			Crypto c = new Crypto();
			KeyPair k = c.makeRSAKeyPair();
			LogEntry e = new LogEntry(t, k.getPublic());
			LogMessage q = (LogMessage)e.decryptObject(k.getPrivate());
			
			System.out.println(q.info);
			
		}catch(KeyException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void logMessageFileTest(){
		try{
			LogMessage t = new LogMessage();
			t.info = "TEST TEST TEST";
			t.type = 0;
			Crypto c = new Crypto();
			KeyPair k = c.makeRSAKeyPair();
			
			Log l = new Log("test.log", k.getPublic());
			
			l.write(t);
			l.write(t);
			l.write(t);
			
			LogReader r= new LogReader(k.getPrivate());
			r.loadFile("test.log");
			System.out.println(r.getLogText());
		}catch(KeyException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
}

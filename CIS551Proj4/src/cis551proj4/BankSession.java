package cis551proj4;
import java.net.*;
import java.io.*;
import java.security.*;

import cis551proj4.log.LogMessage;

public class BankSession implements Session, Runnable {
	private Socket s;
	private ObjectOutputStream os;
	private ObjectInputStream is;

	private AccountDB accts;
	private Crypto crypto;
	private PrivateKey kPrivBank;
	private PublicKey  kPubBank;

	// These fields are initialized during authentication
	private Key kSession;
	private Account currAcct;
	private String atmID;

	// Add additional fields you need here

	BankSession(Socket s, AccountDB a, KeyPair p)
	throws IOException
	{
		this.s = s;
		OutputStream out =  s.getOutputStream();
		this.os = new ObjectOutputStream(out);
		InputStream in = s.getInputStream();
		this.is = new ObjectInputStream(in);
		this.accts = a;
		this.kPrivBank = p.getPrivate();
		this.kPubBank = p.getPublic();
		this.crypto = new Crypto();
	}

	public void run() {
		try {
			if (authenticateUser()) {
				while (doTransaction()) {
					// loop
				}
			}
			is.close();
			os.close();
			s.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Interacts with an ATMclient to 
	// (1) Authenticate the user
	// (2) If the user is valid, establish session key and any
	//     additional information needed for the protocol.
	// (3) Maintain a log of whether the login attempt succeeded
	// (4) Returns true if the user authentication succeeds, false otherwise
	public boolean authenticateUser() {
		byte[] data;
		PublicKey kPubUser;
		LogMessage lm = new LogMessage();
		lm.type = 0;

		try{
			System.out.println("Waiting for first message.");
			// (1) get message from client 
			UniversalMessage um = (UniversalMessage) this.is.readObject();
			InnerMessage im = (InnerMessage) crypto.decryptRSA(um.getData(), this.kPrivBank);
			this.atmID = um.getATMNumber();
			String AccountNumber = im.getAccountNumber();
			long nonce = im.getNonce();
			System.out.println("Got the first message.");
			
			// record in log
			lm.info = "Got message from "+this.atmID+" "+AccountNumber;
			BankServer.log.write(lm);
			
			// (2) send challenge
			this.kSession = crypto.makeRijndaelKey();
			this.currAcct = accts.getAccount(AccountNumber);
			kPubUser = this.currAcct.getKey();
			data = crypto.encryptRSA(new InnerMessage(AccountNumber, nonce), kPubUser);
			byte[] sKey = crypto.encryptRSA(this.kSession, kPubUser);
			UniversalMessage um2 = new UniversalMessage(data, sKey);
			this.os.writeObject(um2);
			System.out.println("Sending challenge.");
			
			// record in log
			lm.info = "Sending challenge to "+this.atmID+" "+AccountNumber;
			BankServer.log.write(lm);
			
			// (3) check challenge
			System.out.println("Waiting for Response.");
			UniversalMessage um3 = (UniversalMessage) this.is.readObject();
			Key repliedkey = (Key) crypto.decryptRSA(um3.getKSession(), this.kPrivBank);
			System.out.println("Got Response.");
			
			// record in log
			lm.info = "Got Response from "+this.atmID+" "+AccountNumber;
			BankServer.log.write(lm);
			
			// (4) return true for authentication succeeds, false otherwise
			if(repliedkey.equals(this.kSession)){
				data = crypto.encryptRSA(new InnerMessage("Accept "+this.currAcct.getOwner()), kPubUser);
				UniversalMessage um4 = new UniversalMessage(data);
				this.os.writeObject(um4);
				System.out.println("Sending Accept.");
				
				// record in log
				lm.info = "Sending accept to "+this.atmID+" "+AccountNumber;
				BankServer.log.write(lm);
			}
			else {
				data = crypto.encryptRSA(new InnerMessage("Reject"), kPubUser);
				UniversalMessage um4 = new UniversalMessage(data);
				this.os.writeObject(um4);
				
				// record in log
				lm.info = "Reject accept to "+this.atmID+" "+AccountNumber;
				BankServer.log.write(lm);
			}
		}catch(IOException e){
			System.out.println("Error during authentication");
			return false;
		}catch(ClassNotFoundException e){
			System.out.println("Error during authentication");
			return false;
		}catch(KeyException e){
			System.out.println("Error during authentication");
			return false;
		}catch(AccountException e){
			System.out.println("Error during authentication");
			return false;
		}
		
		// replace this with the appropriate code
		return true;
	}

	// Interacts with an ATMclient to 
	// (1) Perform a transaction 
	// (2) or end transactions if end-of-session message is received
	// (3) Maintain a log of the information exchanged with the client
	public boolean doTransaction() {
		LogMessage lm = new LogMessage();
		try{
			UniversalMessage um = (UniversalMessage) this.is.readObject();
			TransactionMessage tm = (TransactionMessage) crypto.decryptRijndael(um.getData(), this.kSession);
			byte[] data = crypto.objToBytes(tm);
			byte[] signature = (byte[]) crypto.decryptRijndael(um.getSignature(), this.kSession);
			if(!crypto.verify(data, signature, this.currAcct.getKey())){
				System.out.println("Verification failed");
				tm.request = "Verification failed.\n";
				data = this.crypto.encryptRijndael(tm, this.kSession);
				UniversalMessage um2 = new UniversalMessage(data);
				this.os.writeObject(um2);
				return false;
			}
			
			// record in log
			lm.data = data;
			lm.signature = signature;
			lm.type = 1;
			BankServer.log.write(lm);
			lm.signature = null;
			
			// parse the command
			String command = tm.request;
			if(command.equalsIgnoreCase("Deposit")){
				double amount = tm.amount;
				this.currAcct.deposit(amount);
				tm.request = "Deposit Complete.\nNew Balance is "+this.currAcct.getBalance();
				data = this.crypto.encryptRijndael(tm, this.kSession);
				UniversalMessage um2 = new UniversalMessage(data);
				this.os.writeObject(um2);
				
				// record in log
				lm.data = crypto.objToBytes(tm);
				lm.type = 2;
				BankServer.log.write(lm);
			}
			else if(command.equalsIgnoreCase("Withdrawal")){
				double amount = tm.amount;
				double balance = this.currAcct.getBalance();
				if(balance>=amount){
					this.currAcct.withdraw(amount);
					tm.request = "Withdrawal Complete.\nNew Balance is "+this.currAcct.getBalance();
				}
				else{
					tm.request = "Withdrawal failed. Not enough money in your account.\nYour Balance is "+this.currAcct.getBalance();
				}
				data = this.crypto.encryptRijndael(tm, this.kSession);
				UniversalMessage um2 = new UniversalMessage(data);
				this.os.writeObject(um2);
				
				// record in log
				lm.data = crypto.objToBytes(tm);
				lm.type = 2;
				BankServer.log.write(lm);
			}
			else if(command.equalsIgnoreCase("Balance")){
				tm.request = "Current Balance is "+this.currAcct.getBalance();
				data = this.crypto.encryptRijndael(tm, this.kSession);
				UniversalMessage um2 = new UniversalMessage(data);
				this.os.writeObject(um2);
				
				// record in log
				lm.data = crypto.objToBytes(tm);
				lm.type = 2;
				BankServer.log.write(lm);
			}
			else if(command.equalsIgnoreCase("Quit")){
				tm.request = "Session ended.";
				data = this.crypto.encryptRijndael(tm, this.kSession);
				UniversalMessage um2 = new UniversalMessage(data);
				this.os.writeObject(um2);
				
				// record in log
				lm.info = "Quit.";
				lm.type = 0;
				BankServer.log.write(lm);
				return false;
			}
			
		}catch(IOException e){
			System.out.println("Error during transaction");
			return false;
		}catch(ClassNotFoundException e){
			System.out.println("Error during transaction");
			return false;
		}catch(KeyException e){
			System.out.println("Error during transaction");
			return false;
		}catch(SignatureException e){
			System.out.println("Error during transaction");
			return false;
		}catch(TransException e){
			System.out.println("Error during transaction");
			return false;
		}
		return true;
	}
}


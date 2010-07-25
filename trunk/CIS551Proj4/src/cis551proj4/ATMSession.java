package cis551proj4;
import java.io.*;
import java.security.*;
import java.net.*;


public class ATMSession implements Session {
	private Socket s;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private BufferedReader textIn;

	private String ID;
	private ATMCard card;
	private PublicKey kBank;
	private PrivateKey kUser;
	private Crypto crypto;

	// This field is initialized during authentication
	private Key kSession;

	// Additional fields here

	ATMSession(Socket s, String ID, ATMCard card, PublicKey kBank) {
		this.s = s;
		this.ID = ID;
		this.card = card;
		this.kBank = kBank;
		this.crypto = new Crypto();
		try {
			textIn = new BufferedReader(new InputStreamReader(System.in));
			OutputStream out =  s.getOutputStream();
			this.os = new ObjectOutputStream(out);
			InputStream in = s.getInputStream();
			this.is = new ObjectInputStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// This method authenticates the user and establishes a session key.
	/* (non-Javadoc)
	 * @see Session#authenticateUser()
	 */
	public boolean authenticateUser() {
		System.out.println("Please enter your PIN: ");

		// First, the smartcard checks the user's pin to get the 
		// user's private key.
		try {
			String pin = textIn.readLine();
			kUser = card.getKey(pin);
		} catch (Exception e) {
			try {
				this.is.close();
				this.os.close();
				s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}
		try {

			// Implement the client half of the authentication protocol here
			// (1) First query
			SecureRandom sr = new SecureRandom();
			byte[] data;
			long nonce1 = sr.nextLong();
			
			data = crypto.encryptRSA(new InnerMessage(card.getAcctNum(), nonce1), kBank);
			UniversalMessage um1 = new UniversalMessage(this.ID, data);
			this.os.writeObject(um1);
			System.out.println("Sending AuthInit.");
			// (2) Get challenging message
			System.out.println("Waiting for challenge.");
			System.out.println("Got Challenge.");
			UniversalMessage um2 = (UniversalMessage) this.is.readObject();
			InnerMessage imData = (InnerMessage) crypto.decryptRSA(um2.getData(), kUser);
			kSession = (Key) crypto.decryptRSA(um2.getKSession(), kUser);
			if(nonce1 != imData.getNonce() || !this.card.getAcctNum().equals(imData.getAccountNumber())){
				System.out.println("Wrong reply from bank side");
				return false;
			}
			
			// (3) Reply session key
			byte[] sKey = crypto.encryptRSA(kSession, kBank);
			UniversalMessage um3 = new UniversalMessage(this.ID);
			um3.setKSession(sKey);
			this.os.writeObject(um3);
			System.out.println("Sending response");
			
			// (4) receive accept message from server
			System.out.println("Waiting for session key");
			UniversalMessage um4 = (UniversalMessage) this.is.readObject();
			InnerMessage im2 = (InnerMessage) crypto.decryptRSA(um4.getData(), kUser);
			if(!im2.getCommand().startsWith("Accept")){
				System.out.println("Error during authetication");
				return false;
			}
			String owner = im2.getCommand().substring(im2.getCommand().indexOf(" ")+1); 
			System.out.println("Welcome "+owner);
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
		catch(KeyException e){
			e.printStackTrace();
			return false;
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	void printMenu() {
		System.out.println("*****************************");
		System.out.println("(1) Deposit");
		System.out.println("(2) Withdraw");
		System.out.println("(3) Get Balance");
		System.out.println("(4) Quit\n");
		System.out.print  ("Please enter your selection: ");
	}

	int getSelection() {
		try {
			String s = textIn.readLine();
			int i = Integer.parseInt(s, 10);
			return i;
		} catch (IOException e) {
			return -1;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	double getDouble() {
		try {
			String s = textIn.readLine();
			double d = Double.parseDouble(s);
			return d;
		} catch (IOException e) {
			return 0.0;
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	void endSession() {
		doMoney("Quit", 0);
		try{
			this.os.close();
			this.is.close();
			s.close();
		}catch(IOException e){
			e.printStackTrace();
			return;
		}	
	}

	void doDeposit() {
		System.out.println("Please enter the amount you want to deposit:");
		double amount = getDouble();
		doMoney("Deposit", amount);
	}

	private void doMoney(String request, double amount) {
		long time = System.currentTimeMillis();
		TransactionMessage transactionMessage = new TransactionMessage(request, amount, card.getAcctNum(), ID, time);
		UniversalMessage um;
		try {
			byte[] tmb = crypto.objToBytes(transactionMessage);
			byte[] encryptedTmb = crypto.encryptRijndael(transactionMessage, kSession);
			um = new UniversalMessage(encryptedTmb);
			byte[] signature = crypto.sign(tmb, kUser);
			byte[] encryptedSignature = crypto.encryptRijndael(signature, kSession);
			um.setSignature(encryptedSignature);
			os.writeObject(um);
			TransactionMessage replyTransactionMessage = 
				(TransactionMessage)crypto.decryptRijndael(((UniversalMessage)is.readObject()).getData(), this.kSession);
			String reply = replyTransactionMessage.request;
			long replyTime = replyTransactionMessage.time;
			if (time != replyTime){
				System.out.println("Warning: Wrong timestamp received from Bank Server!");
				return;
			}
			System.out.println(reply);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(request + " failed. Please try later");
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (KeyException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	void doWithdrawal() {
		System.out.println("Please enter the amount you want to withdraw:");
		double amount = getDouble();
		doMoney("Withdrawal", amount);
	}

	void doBalance() {
		doMoney("Balance", 0);
	}

	public boolean doTransaction() {
		printMenu();
		int x = getSelection();
		switch(x) {
		case 1 : doDeposit(); break;
		case 2 : doWithdrawal(); break;
		case 3 : doBalance(); break;
		case 4 : {endSession(); return false;}
		default: {System.out.println("Invalid choice.  Please try again.");}
		}
		return true;
	} 
}

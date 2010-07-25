/*
Some SMTP servers require a username and password authentication before you
can use their Server for Sending mail. This is most common with couple
of ISP's who provide SMTP Address to Send Mail.

This Program gives any example on how to do SMTP Authentication
(User and Password verification)

This is a free source code and is provided as it is without any warranties and
it can be used in any your code for free.

Author : Sudhir Ancha
 */
package com.kemplerEnergy.util;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import java.util.*;

/*
 To use this program, change values for the following three constants,

 SMTP_HOST_NAME -- Has your SMTP Host Name
 SMTP_AUTH_USER -- Has your SMTP Authentication UserName
 SMTP_AUTH_PWD  -- Has your SMTP Authentication Password

 Next change values for fields

 emailMsgTxt  -- Message Text for the Email
 emailSubjectTxt  -- Subject for email
 emailFromAddress -- Email Address whose name will appears as "from" address

 Next change value for "emailList".
 This String array has List of all Email Addresses to Email Email needs to be sent to.


 Next to run the program, execute it as follows,

 SendMailUsingAuthentication authProg = new SendMailUsingAuthentication();

 */

public class Mail {

	private static final String CC_EMAIL = "Jeff.Marcus@KemplerEnergy.com";
	private static final String CC_EMAIL_2	= "John.Kiernan@KemplerEnergy.com";
	
	private static final String SMTP_HOST_NAME = "69.49.109.33";
	private static final String SMTP_AUTH_USER = "John.Kiernan@KemplerEnergy.com";
	private static final String SMTP_AUTH_PWD = "ethanol1";

	private String emailMsgTxt;
	private String emailSubjectTxt;
	private String emailFromAddress;
	
	// Add List of Email address to who email needs to be sent to
	private ArrayList<String> emailList;
	private ArrayList<String> attachments;
	
	
	public Mail(String emailFromAddress, String emailToAddress,
			String emailMsgTxt, String emailSubjectTxt) {
		this();
		this.emailFromAddress = emailFromAddress;
		this.emailList.add(emailToAddress);
		this.emailMsgTxt = emailMsgTxt;
		this.emailSubjectTxt = emailSubjectTxt;
	}
	
	
	public Mail() {
		attachments = new ArrayList<String>(); 
		emailList = new ArrayList<String>();
		emailMsgTxt = "Online Order Confirmation Message. Also include the Tracking Number.";
		emailSubjectTxt = "Order Confirmation Subject";
		emailFromAddress = SMTP_AUTH_USER;
//		emailList.add(CC_EMAIL);
	}

	public void addRecipient(String recipient) {
		// TODO: for test reason, all email will be only send to CC_EMAIL
		if (true) {
			emailList.add(CC_EMAIL);
			emailList.add(CC_EMAIL_2);
		}
		else
			
		if (recipient
				.matches("^[^@;:<>()\\[\\]\\/]+@([-a-z0-9]+\\.)+[a-z]{2,}$")) {
			emailList.add(recipient);
			System.out.println("CORRECT!");
		} else {
			System.out.println("Wrong format");
		}
	}

	public void addAttachement(String filePath) {
		if (attachments.size() >= 3)
			throw new IllegalArgumentException("You can upload at most three files");
		if (!filePath.endsWith(".pdf") && !filePath.endsWith(".csv"))
			throw new IllegalArgumentException("You can only upload csv or pdf files");
		attachments.add(filePath);
	}

	public void setEmailMsgTxt(String emailMsgTxt) {
		this.emailMsgTxt = emailMsgTxt;
	}

	public void setEmailSubjectTxt(String emailSubjectTxt) {
		this.emailSubjectTxt = emailSubjectTxt;
	}

	public void setEmailFromAddress(String emailFromAddress) {
		this.emailFromAddress = emailFromAddress;
	}

	public void sendMail() throws MessagingException {
		if (emailList.size() <= 0)
			throw new IllegalArgumentException("You have to specify at least one recipient");
		String[] emailArray = emailList.toArray(new String[emailList.size()]);
		sendMail(emailArray, emailSubjectTxt, emailMsgTxt, emailFromAddress);
	}
	
	private void sendMail(String recipients[], String subject, String message,
			String from) throws MessagingException {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");

		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getDefaultInstance(props, auth);

		session.setDebug(debug);

		// create a message
		MimeMessage msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject 
		msg.setSubject(subject);
		
		// set the Date: header
		msg.setSentDate(new Date());

		if (attachments.size() == 0) { // no attachment
			msg.setContent(message, "text/html");
		} else {

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(message, "UTF-8", "html");

			// create the second message part
			ArrayList<MimeBodyPart> mbp2 = new ArrayList<MimeBodyPart>();

			// attach the file to the message
			for (String filename : attachments) {
				FileDataSource fds = new FileDataSource(filename);
				MimeBodyPart newFilePart = new MimeBodyPart();
				newFilePart.setDataHandler(new DataHandler(fds));
				newFilePart.setFileName(fds.getName());
				mbp2.add(newFilePart);
			}

			// create the Multipart and add its parts to it
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);

			for (MimeBodyPart m : mbp2)
				mp.addBodyPart(m);

			// add the Multipart to the message
			msg.setContent(mp);

		}
		Transport.send(msg);
	}

	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
			return new PasswordAuthentication(username, password);
		}
	}

}

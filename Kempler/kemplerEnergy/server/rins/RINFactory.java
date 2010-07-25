package com.kemplerEnergy.server.rins;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.print.attribute.standard.DateTimeAtCompleted;

import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.rins.BOLInfo;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RIN.InvoiceIndex;
import com.kemplerEnergy.model.rins.RIN.TransacionType;
import com.kemplerEnergy.util.Mail;

/**
 * This is a helper class, all its method should be used by caller under the
 * scope of a transaction. There is no transaction deamarcation defined in this 
 * method. That means all its operation is NOT committed by itself. This is 
 * done by injecting EntityManager into each method, which is kinda ugly :(
 * @author Ji
 *
 */
public class RINFactory {

	private static final String ROOT_DIR = "c:\\kemplerDoc\\RINsDir\\";
	
	public RINFactory() {
	}

	private int getBalance(boolean finOnly, EntityManager em) {

		BigDecimal result = (BigDecimal) em.createNativeQuery(
				"SELECT SUM(R.Gallon_Amount) " +
				"FROM RIN R " +
				"WHERE R.Status = \'AVAILABLE\' " +
				(finOnly ? " AND R.RIN_Type = \'2\'; " : ";"))
				.getSingleResult();

		if (result == null) {
			return 0;
		}
		System.out.println("Total: " + result);
		return result.intValue();
	}

	public int[] getBalance(EntityManager em)  {
		int k2 = getBalance(true, em);
		int total = getBalance(false, em);

		int[] result = new int[] {total - k2, k2};
		
		return result;
	}
	
	/**
	 * Get list of available RINs for sale 	
	 * @param finOnly
	 * @param em
	 * @return
	 * @throws RINException
	 */
	public synchronized List<RIN> getRINsAvailableForSale(boolean finOnly, EntityManager em) throws RINException 
	{
	
			List<RIN> usableRINs = new ArrayList<RIN>();
			
			//TODO: Review 
			
			// Grab all qualified(un-sold) rins order by their invoice date
			if (finOnly) {
				usableRINs = (ArrayList<RIN>)em.createQuery(
						"SELECT r FROM RIN r "
						+ "WHERE r.rinStatus = \'AVAILABLE\' AND "
						+ "		 r.rinType = 2 "
						+ "ORDER BY r.originalRIN.id ")
						.getResultList();			
			} else {
				usableRINs = (ArrayList<RIN>)em.createQuery(
						"SELECT r FROM RIN r WHERE r.rinStatus = \'AVAILABLE\' "
						+ "ORDER BY r.rinType, r.originalRIN.id")
						.getResultList();
			}
						
			
//			if (finOnly) {
//				usableRINs = (ArrayList<RIN>)em.createQuery(
//						"SELECT r FROM RIN r "
//						+ "WHERE r.rinStatus = \'AVAILABLE\' AND "
//						+ "		 r.rinType = 2 "
//						+ "ORDER BY r.saleIndex, r.uniRIN")
//						.getResultList();			
//			} else {
//				usableRINs = (ArrayList<RIN>)em.createQuery(
//						"SELECT r FROM RIN r WHERE r.rinStatus = \'AVAILABLE\' "
//						+ "ORDER BY r.saleIndex, r.uniRIN")
//						.getResultList();
//			}
			
			// This method is resorting usableRINs completely, 
			// thus ORDER in previous queries do not play any role
			//sortUsableRingsByPurchaseDateAndUNIRIN(usableRINs, em);
			
//			Collections.sort(usableRINs, new Comparator<RIN>(){
//				
//				@Override
//				public int compare(RIN o1, RIN o2) {
//					
//					return o1.getSaleIndex() - o2.getSaleIndex();
//				}
//			});
			
			return usableRINs;
		
	}
	
	/**
	 * Given information of whether invoice is financial only, and the needed
	 * gallon amount return list of RINs to fulfill the requirement. 
	 * Only one RINs request will be processed at the same time
	 * @return the list of RINs meet requirement, if the inventory is not 
	 * 		   enough, it will return null.
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<RIN> getSaleRINs(boolean finOnly, int request, EntityManager em) throws RINException {

		if (request > getBalance(finOnly, em))
			return null;

		
		ArrayList<RIN> saleRINs;
		List<RIN> usableRINs = new ArrayList<RIN>();
		
		try {
			
			// sorting method in getRINsAvailableForSale
			usableRINs  = getRINsAvailableForSale(finOnly, em);
			
			saleRINs = new ArrayList<RIN>();			
			
			for (RIN r : usableRINs) 
			{
				
				if (request == 0)
				{
					return saleRINs;
				}
														
				System.out.print(String.format("Getted RIN: %s  Gallons: %d", r.toString(), r.getGallonAmount()));
				
				int availableGallons = r.getGallonAmount();
				
				if (request >= availableGallons) {
					
					r.setRinStatus("SOLD");
					//r.setSaleIndex(saleIndex);
					saleRINs.add(r);
					em.flush();
					request -= availableGallons;
					
					System.out.println(" SOLD, remain amount: " + request);
					
				} 
				else 
				{ 
					// need to split rin
					RIN newRIN, oldRIN;
					
					int saleIndex = r.getSaleIndex();
					
					ArrayList<RIN> rins = r.split(request);
					
					if (rins.size() == 1) {// split the RIN which has been split before
						oldRIN = r;
					}
					else {
						oldRIN = rins.get(1);
						oldRIN.setSaleIndex(saleIndex);
						em.persist(oldRIN);
					}
					
					newRIN = rins.get(0);
					oldRIN.setRinStatus("SOLD");
					//oldRIN.setSaleIndex(saleIndex);
					newRIN.setValidationMask(RIN.VERIFIED.toCharArray());
					
					
					saleRINs.add(oldRIN);
					
					newRIN.setSaleIndex(saleIndex + 1);
					
					em.persist(newRIN);										
					
					System.out.println(" SPLITTED, SOLD, remain amount: " + 0);
					
					break;
				}
				
				//saleIndex++;
			}

			return saleRINs;

		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RINException(e.getMessage());
		} 
	}
	
	/**
	 * Given retired RINs, retire all related RINs (siblings from original one)
	 * When our customer reported a retired RIN, we will try to find all its
	 * related invoices. This includes the original RIN (if this retired RIN
	 * has been split from a larger RIN) and its siblings.
	 * The input rin SHOULD NOT be set as retired!
	 * The method will only set the related RINs status to "CORRUPTED". 
	 * @return list of affected invoices
	 * @throws RINException 
	 */
	public synchronized  Set<Invoice> retireRINs(Set<RIN> incomingRINs, EntityManager em) {
		Set<Invoice> affectedInvoices = new HashSet<Invoice>();
		Set<RIN> retiredRINs = new HashSet<RIN>();
		
		for (RIN r : incomingRINs) {
			RIN rin = r;
			if (!em.contains(r))
				rin = (RIN) em.merge(r);
			rin.setRinStatus("NEW");
			rin.setRetiredCode("RIR"); // code for overlapping
			rin.setRetiredDate(new Timestamp(System.currentTimeMillis()));
			em.persist(rin);
			
		
			retiredRINs.add(rin);
		}
		
		for (RIN r: retiredRINs) {
			Set<Invoice> invoices;
			invoices = retireRINs(r, em);
			if (invoices != null) {
				for (Invoice i : invoices) {
					if (!i.isIn(affectedInvoices))
						affectedInvoices.add(i);
				}
			}
		}
		return affectedInvoices;
	}


	@SuppressWarnings("unchecked")
	private Set<Invoice> retireRINs(RIN incomingRIN, EntityManager em) {
		
		/* Since every RIN family does not intersect with each other, it's very
		 * safe to say, if any RIN isn't retired in this family, then all the
		 * member in this family can't be retired.
		 */
		if (incomingRIN.getRinStatus().equalsIgnoreCase("RETIRED") ||
				incomingRIN.getRinStatus().equalsIgnoreCase("CORRUPTED"))
			// throw new RINDuplicateRetire("This RIN has already been processed");
			return null;
		


		RIN rin = incomingRIN;
		if (!em.contains(incomingRIN))
			rin = (RIN)em.merge(incomingRIN);
		
		
		
		// find the parent, for some split RIN which does not associated
		// with any purchase invoice, it's very important to find the root
		RIN parentRIN = rin.getOriginalRIN();
		System.out.println("Parent RIN" + parentRIN.getId());
		Invoice purchaseInvoice = parentRIN.returnInvoice(InvoiceIndex.Last, TransacionType.PURCHASE);
		System.out.println(parentRIN.getInvoices());
		System.out.println("Purchase Invoice" + purchaseInvoice.getId());

		em.flush();
		// Find all this rin's siblings
		Query query = em.createQuery("SELECT r FROM RIN r WHERE r.originalRIN.id = :id");
		System.out.println(parentRIN.getId());
		query.setParameter("id", parentRIN.getId());
		List<RIN> rins = (ArrayList<RIN>)query.getResultList();

		
		// Retire rins first, set retired RIN information
		// set the current rin to be a new retired rin.
		rin.setRinStatus("CORRUPTED");
		rin.setRetiredCode("RIR");
		rin.setRetiredDate(new Timestamp(System.currentTimeMillis()));

		Set<Invoice> affectedInvoices = new HashSet<Invoice>();
		
		if (purchaseInvoice != null)
			affectedInvoices.add(purchaseInvoice);
		for (RIN r : rins) {
			Invoice saleInvoice = r.returnInvoice(InvoiceIndex.Last, TransacionType.SALE);
			if (saleInvoice == null || affectedInvoices.contains(saleInvoice))
				continue;
			affectedInvoices.add(saleInvoice);
		}
		
		for (RIN r: rins) {
			if (r.getInvoices() == null || r.getInvoices().isEmpty())
				r.setRinStatus("RETIRED");
			else r.setRinStatus("CORRUPTED");
			r.setRetiredCode("RIR");
			r.setRetiredDate(new Timestamp(System.currentTimeMillis()));
		}
		
		return affectedInvoices;
	}
	
	/**
	 * Given returned invoice, replace retired RIN of this invoice
	 * The bad rin MUST be set as "RETIRED" or "CORRUPTED"
	 * the "RETIRED" RIN will be set as "CORRUPTED" 
	 * @return true if replacement succeed
	 * @throws RINException 
	 */
	public boolean replaceInvoice(Invoice invoice, EntityManager em) throws RINException {
		
		if (!invoice.getStatus().equalsIgnoreCase("REPLACEMENT_PENDING") || 
				invoice.getInvoiceType().equalsIgnoreCase("PURCHASE")) 
		
			throw new RINException(
					"Only REPLACEMENT_PENDING selling invoice can be replaced, you supplied "
							+ invoice.getStatus() + " invoice");
		
		// request is old invoice gallon amount
		int request = invoice.getExpectedGallons();
		if (request <= 0) 
			throw new RINException("Quantity can not be negative or zero");
		
		// balance is the total gallon amount of good rins
		int balance = invoice.calculateActualGallons();
		
		int deficiency = request - balance;
		if (deficiency <= 0) 
			throw new RINException("There is no deficiency for this invoice");
		
		boolean finOnly =  invoice.getMode().getMode().equalsIgnoreCase("FIN");

		List<RIN> replacedRINs = getSaleRINs(finOnly, deficiency, em);
		if (replacedRINs == null || replacedRINs.isEmpty())
			return false;
		
		Invoice i = invoice;
		if (!em.contains(invoice)) 
			i = (Invoice)em.merge(invoice);
		
		// Add replaced RINs to the invoice
		for (RIN r: replacedRINs) {
			i.addRIN(r);
			r.setRinStatus("REPLACING"); 
		}
		
		for (RIN r: i.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("RETIRED"))
				r.setRinStatus("CORRUPTED");
		}
		i.setStatus("SOLD");
		i.validate();
		RPTDCreator rptd = new RPTDCreator(em);
		
		//TODO Review
		try {
			rptd.makeRPTD(i);
		} catch (Exception e) {
			throw new RINException(e.getMessage());
		}
		
		sendReplacementMail(i);

		for (RIN r: i.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("REPLACING"))
				r.setRinStatus("SOLD");
			else if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
				r.setRinStatus("RETIRED");
		}
		
		return true;
	}
	


	/**
	 * Check the current RIN inventory to replace any Pending replacement 
	 * invoice or pending sales invoice to our customer. 
	 * Replacement is based on FIFO order and will only do full-replacement.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void autoFulfill(EntityManager em) throws Exception {
		
		
		Query query;
		List<Invoice> pendingInvoices;
		
		try {
			query = em.createQuery("SELECT i FROM Invoice i "
					+ "WHERE i.status = \'REPLACEMENT_PENDING\' OR "
					+ "		 i.status = \'SALE_PENDING\' "
					+ "ORDER BY i.createTime");
			pendingInvoices = (ArrayList<Invoice>)query.getResultList();

		} catch (NoResultException e) {
			return;
		}
		
		if (pendingInvoices == null || pendingInvoices.isEmpty())
			return;
		
		int finBalance = getBalance(true, em); // also K2 Balance
		int totalBalance = getBalance(false, em);
		int K1Balance;
		
		for (Invoice i: pendingInvoices) {
			String type = i.getMode().getMode();
			int shortage = i.getExpectedGallons() - i.calculateActualGallons();
			
			if (type.equalsIgnoreCase("FIN")) {	// Financial Only
				if (shortage <= finBalance) {
					finBalance -= shortage;
					totalBalance -= shortage;
					if (i.getStatus().equalsIgnoreCase("SALE_PENDING"))
						sellRIN(i, em);
					else replaceInvoice(i, em);
				} else	continue;
			} else {
				if (shortage <= totalBalance) {
					K1Balance = totalBalance - finBalance;
					if (shortage <= K1Balance) { // K1 balance
						totalBalance -= shortage;
					} else {
						totalBalance -= shortage;
						finBalance -= (shortage - K1Balance);
					}
					if (i.getStatus().equalsIgnoreCase("SALE_PENDING"))
						sellRIN(i, em);
					else replaceInvoice(i, em);
				} else continue;
			}
		}
	}
	
	/**
	 * When we automatically made a sale, we send the email to Jeff 
	 * @param invoice
	 */
	public void sendSaleMail(Invoice invoice) {
		//String JEFF_EMAIL = "jf2476@columbia.edu";
		//String JEFF_EMAIL = "dmitry.nikolaychuk@gmail.com";
		//String email = JEFF_EMAIL ;
		String subject = "RINs for Sales " + invoice.getInvoiceNo();
		String shipMode = invoice.getMode().getMode();
		String bolInfo;
		String bolTicketNO = invoice.getBolInfo().toString();
		bolTicketNO = bolTicketNO.substring(1, bolTicketNO.length() - 1);
		
		if (shipMode.equalsIgnoreCase("FIN")) {
			bolInfo = "";
		} else if (shipMode.equalsIgnoreCase("BARGE"))
			bolInfo = "\n<br />Ref: " + "COA" + " :" + bolTicketNO;
		else if (shipMode.equalsIgnoreCase("ITT")) {
			bolInfo = "\n<br />Ref: " + shipMode + " :" + bolTicketNO;
		} else {
			bolInfo = "\n<br />Ref: BOL :" + bolTicketNO;
		}
		
		String context = "<p style=\"FONT-FAMILY: 'Arial';\">"
			+ "RIN PTD for sale to "
			+ invoice.getEpaPartner().getFullName()
			+ "\n<br />Invoice #" + invoice.getInvoiceNo()
			+ "\n<br /> " + ((invoice.getRptdK1() == null) ? "" : "<br />RPTD K1# " + invoice.getRptdK1())
			+ " " + ((invoice.getRptdK2() == null) ? "" : "<br />RPTD K2# " + invoice.getRptdK2()) + "<br />"
			+ bolInfo
			+ "\n<br /><table align=\"center\" border=\"1\" cellspacing=\"0\">"
			+ "	  <tr style=\"COLOR: #ffffff; BACKGROUND-COLOR: #000000;\">"
			+ "		  <td align=\"center\">Sold RIN(s)</td>"
			+ "   </tr>";
		
		
// Old code:		
//		for (RIN r: invoice.getRins()) {
//				context += "<tr><td>" + r.toString() + "</td></tr>";
//		}
		
		List<RIN> rins = invoice.getRins();
		
		Collections.sort(rins, new Comparator<RIN>(){
			
			@Override
			public int compare(RIN o1, RIN o2) {
				
				int compareResult = o1.getRinType() - o2.getRinType();
				
				if(compareResult == 0)
				{				
					return new Double(o1.getOriginalRIN().getId())
						.compareTo(new Double(o2.getOriginalRIN().getId()));
				}
				else
				{
					return compareResult;
				}
				
			}
		});
		
		for (RIN r: rins) {
			context += "<tr><td>" + r.toString() + "</td></tr>";
		}
		
		context += "</table>\n<br /><br />RINs@KemplerEnergy.com</p>";
		
		//Attention: mails are added from static fields
		sendMail(invoice, null, subject, context);
	}
	
	/**
	 * Notify our customer the retired RIN and replaced RINs or pending status
	 * @param invoice
	 */
	public void sendReplacementMail(Invoice invoice) {
		String email = invoice.getEpaPartner().getEmail();
		String subject = "Retired RINs on " + invoice.getInvoiceNo();
		String context = "<p style=\"FONT-FAMILY: 'Arial';\">Dear "
			+ invoice.getEpaPartner().getContact()
			+ ", <br />"
			+ "Pursuant to the EPA Regulation of Fuels and Fuel Additives: "
			+ "Renewable Fuel Standard Program; Final Rule �80.1131 the "
			+ "following RINs from our invoice numbered "
			+ invoice.getInvoiceNo() + " have been retired:<br />"
			+ "<table align=\"center\" border=\"1\" cellspacing=\"0\">"
			+ "	  <tr style=\"COLOR: #ffffff; BACKGROUND-COLOR: #000000;\">"
			+ "		  <td align=\"center\">Retired RIN Number(s)</td>"
			+ "   </tr>";
		for (RIN r: invoice.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("RETIRED") || 
				r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
				context += "<tr><td>" +r.toString() + "</td></tr>";
		}
		context += "</table>";
		
		if (invoice.getStatus().equalsIgnoreCase("SOLD")) {
			context += "<br />These RIN numbers have been replaced with the following"
					 + "<table align=\"center\" border=\"1\" cellspacing=\"0\">"
					 + "	  <tr style=\"COLOR: #ffffff; BACKGROUND-COLOR: #000000;\">"
					 + "		  <td align=\"center\">New RIN Number(s)</td>"
					 + "   </tr>";
			for (RIN r: invoice.getRins()) {
				if (r.getRinStatus().equalsIgnoreCase("REPLACING"))
					context += "<tr><td>" +r.toString() + "</td></tr>";
			}
			context += "</table>";

		}
		else context += "However, we don't have enough inventory to cover this shortage, " +
				"as soon as we have new RINs, we will replace them.";
		
		context += "<br />We apologize for any inconvenience and look forward to future business.<br /><br />"
			 	 + "Best Regards,<br /><br />"
			 	 + "Kempler Energy</p>";
		
		sendMail(invoice, email, subject, context);
	}

	/**
	 * @param invoice
	 * @param email
	 * @param subject
	 * @param context
	 */
	private void sendMail(Invoice invoice, String email, String subject,
			String context) {
		// TODO: Delete next line when deployed
		email = "dmitry.nikolaychuk@gmail.com";
		Mail mail = new Mail();
		mail.setEmailFromAddress("RINs@KemplerEnergy.com");
		mail.addRecipient(email);
		mail.setEmailSubjectTxt(subject);
		mail.setEmailMsgTxt(context);
		
		FileReader reader; 
		try {
			reader = new FileReader(ROOT_DIR + invoice.getCsvPath());
			mail.addAttachement(ROOT_DIR + invoice.getCsvPath());
		} catch (FileNotFoundException e) {}
		
		try {
			reader = new FileReader(ROOT_DIR + invoice.getRptdPath());
			mail.addAttachement(ROOT_DIR + invoice.getRptdPath());
		} catch (FileNotFoundException e) {}
		
		try {
			reader = new FileReader(ROOT_DIR + invoice.getInvoicePath());
			mail.addAttachement(ROOT_DIR + invoice.getInvoicePath());
		} catch (FileNotFoundException e) {
			System.out.println("caught");
		}
		
		try {
			mail.sendMail();
		} catch (MessagingException e) {
			// TODO save into the Outbox
			e.printStackTrace();
		}
	}
	
	/**
	 * Notify vendor that we need to replace certain RINs
	 * @param invoice
	 */
	public void requestReplacementMail(Invoice invoice) {
		
		String email = invoice.getEpaPartner().getEmail();
		String subject = "Retired RINs on " + invoice.getInvoiceNo();
		String context = "<p style=\"FONT-FAMILY: 'Arial';\">Dear "
				+ invoice.getEpaPartner().getContact()
				+ ", <br />"
				+ "Pursuant to the EPA Regulation of Fuels and Fuel Additives: "
				+ "Renewable Fuel Standard Program; Final Rule �80.1131 the "
				+ "following RINs from your invoice numbered "
				+ invoice.getInvoiceNo() + " have been retired:<br />"
				+ "<table align=\"center\" border=\"1\" cellspacing=\"0\">"
				+ "	  <tr style=\"COLOR: #ffffff; BACKGROUND-COLOR: #000000;\">"
				+ "		  <td align=\"center\">Retired RIN Number(s)</td>"
				+ "   </tr>";
		for (RIN r: invoice.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
				context += "<tr><td>" +r.toString() + "</td></tr>";
		}
		context += "</table>";
		
		// TODO currently don't need
		if (!invoice.getMode().getMode().equalsIgnoreCase("FIN") && false) {
			context += "Your invoice " + invoice.getInvoiceNo()
					+ ", relates to the following transfer:\n"
					+ "<table align=\"center\" border=\"1\" cellspacing=\"0\">"
					+ "	 <tr style=\"COLOR: #ffffff; BACKGROUND-COLOR: #000000;\">"
					+ "	   <td >Contract#</td>" 
					+ "	   <td>BOL#</td>"
					+ "	   <td>Volume</td>" 
					+ "	   <td>Load Number</td>" 
					+ "	 </tr>";
			for (BOLInfo b: invoice.getBolInfo()) {
				context += "  <tr>"
						 + "	   <td>" + "FAKE contract_no" + "</td>"
						 + "	   <td>" + b.getBolNumber() + "</td>"
						 + "	   <td>" + b.getQuantity() + "</td>"
						 + "	   <td>" + b.getLoadNumber() + "</td>"
						 + "  </tr>";
			}
			context += "</table>";
		}
		context += "<br />Please revert with replacement RINs for " 
				 + invoice.calculateGallons("CORRUPTED") 
				 + " RIN-Gallons.<br /><br />"
				 + "Best Regards,<br /><br />"
				 + "Kempler Energy</p>"; 

		// will not be here
		sendMail(invoice, email, subject, context);
	}
	
	/**
	 *  Send email to the vendor/customer to report retired RIN(CORRUPTED)
	 *  Archive old invoice for purchase invoice, generate new pending invoice
	 *  These are bad invoices, falls into two categories
	 *  1. Sale invoice: 
	 *     a) SOLD: Archive this invoice, create new Invoice set status to 
	 *     			REPLACEMENT_PENDING
	 *     b) REPLACEMENT_PENDING
	 *  2. Purchase invoice: 
	 *     a) ACCEPTED: (ONLY IN AUTO RETIREMENT)
	 *     				archive old invoice, copy all non-retired rins to new 
	 *     				invoice, new invoice -> REPLACEMENT_REQUESTED, send 
	 *     				email.
	 *     b) REPLACMENT_REQUESTED: just add gap and send new email
	 *     
	 *  
	 *  NO RPTD generation, 
	 *  RIN status will not be changed 
	 */
	public void reportInvoices(Set<Invoice> affectedInvoices, EntityManager em) {

		for (Invoice i : affectedInvoices) {

			Invoice badInvoice = i;
			if (!em.contains(i))
				badInvoice = (Invoice) em.merge(i);

			/* 
			 * Scene 1
			 * If badInvoice is a sale invoice
			 */
			if (badInvoice.getInvoiceType().equalsIgnoreCase("SALE")) {
				if (badInvoice.getStatus().equalsIgnoreCase("SOLD")) {
					Invoice oldInvoice = badInvoice;
					oldInvoice.setStatus("ARCHIVED");
					badInvoice = new Invoice(oldInvoice);
					badInvoice.setStatus("REPLACEMENT_PENDING");
					em.persist(badInvoice);
				} 
				if (!replaceInvoice(badInvoice, em))
					sendReplacementMail(badInvoice);
			} 
			/*
			 * Scene 2(b)
			 */
			else if (badInvoice.getStatus() // this is a already ON waiting for replacement invoice
					.equalsIgnoreCase("REPLACEMENT_REQUESTED")) {
				int request = 0;
				for (RIN r : badInvoice.getRins()) {
					if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
						request += r.getGallonAmount();
				}
				badInvoice.getGaps().add(request);
				requestReplacementMail(badInvoice);
				
			} 
			/*
			 * Scene 2(a)
			 */
			
			else { // new single batch replacement request
				if (!badInvoice.getStatus().equalsIgnoreCase("PENDING")) 
					badInvoice.setStatus("REPLACEMENT_REQUESTED");
				Invoice oldInvoice = badInvoice.getLastInvoice();
/*				if (oldInvoice != badInvoice && !oldInvoice.getStatus().equalsIgnoreCase("ARCHIVED")) {
					oldInvoice.setStatus("ARCHIVED");
					em.persist(oldInvoice);
				}*/
				
				oldInvoice.setStatus("ARCHIVED");
				Invoice newInvoice = new Invoice(badInvoice);
				newInvoice.setStatus("REPLACEMENT_REQUESTED");
				em.persist(newInvoice);
				requestReplacementMail(newInvoice);
			}
		}
	}
	
	/**
	 * @param invoice
	 * @param rinFactory
	 * @param em
	 */
	public synchronized void sellRIN(Invoice invoice, EntityManager em) throws Exception {

		boolean finOnly =  invoice.getMode().getMode().equalsIgnoreCase("FIN");
		int request = invoice.getExpectedGallons();

		if (request <= 0)
			throw new RINException("Quantity can not be negative or zero");



		List<RIN> rins = getSaleRINs(finOnly, request, em);
		
		if (rins == null)
			invoice.setStatus("SALE_PENDING");
		else {
			for (RIN r : rins) {
				r.setRinStatus("SOLD");
				r.setValidationMask(RIN.VERIFIED.toCharArray());
				invoice.addRIN(r);
			}
			invoice.setStatus("SOLD");
		}
		Invoice newInvoice = invoice;
		if (!em.contains(invoice))
			newInvoice = (Invoice)em.merge(invoice);
	
		em.flush();
		em.refresh(newInvoice);
		
		// Generate RPTD for successfully sold invoice
		if (newInvoice.getStatus().equalsIgnoreCase("SOLD")) {
			newInvoice.validate();
			RPTDCreator rptd = new RPTDCreator( em );
			
//			for (RIN r : i.getRins()) 
//			{
//				System.out.println(r);
//				System.out.println(r.getRinType());
//			}
			
			rptd.makeRPTD(newInvoice);
			CSVCreator csvCreator = new CSVCreator();
			try {
				csvCreator.makeCSV(newInvoice);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RINException(e.getMessage());
			}
			sendSaleMail(newInvoice);
		}
	}

}

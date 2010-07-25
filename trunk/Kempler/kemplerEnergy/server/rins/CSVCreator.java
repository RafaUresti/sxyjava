package com.kemplerEnergy.server.rins;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RIN.InvoiceIndex;
import com.kemplerEnergy.model.rins.RIN.TransacionType;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.csvreader.CsvWriter;

public class CSVCreator {
	
	private CsvWriter writer;

	private static final String[] INVOICE_CSV_HEADER = { "Invoice No",
			"Company ID", "Company Name", "Transaction Type",
			"Transaction Date", "Trading Partner Name", "Company ID", "RIN" };
	
	private static final String[] RF200_HEADER = { "## Report Form ID",
			"Report Type", "CBI", "Report Date", "Report Year", "Company ID",
			"Company Name", "Period Code", " Facility ID", "Transaction Type",
			"Retired RIN Code", "Transaction Date", "Trading Partner Name",
			"Company ID", "RIN" };
	private static final String[] RF100_HEADER = { "## Report Form ID",
			"Report Type", "CBI", "Report Date", "Report Year", "Company ID",
			"Company Name", "Period Code", " Facility ID", "RIN Status",
			"end of the quarter", "at the start of the quarter", " Purchased",
			"Sold", "Retired", "at the end of the quarter",
			"at the start of the quarter", " Purchased", "Sold", "Retired",
			"at the end of the quarter" };
	private static final String ROOT_DIR = "c:\\kemplerDoc\\RINsDir\\";
	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	
	private Timestamp startTime;
	private Timestamp endTime;
	
	public CSVCreator(long time) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
		
		int year = date.get(Calendar.YEAR);
		int quarter = (date.get(Calendar.MONTH) + 1) / 4;
		setTimeRange(year, quarter);
	}

	/**
	 * Set the time range to cover the input year and quarter
	 * @param year
	 * @param quarter
	 */
	private void setTimeRange(int year, int quarter) {
		int startMonth = (quarter - 1) * 3;
		int endMonth = quarter * 3;
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		
		startDate.set(year, startMonth, 1, 0, 0, 0);
		endDate.set(year, endMonth, 0, 23, 59, 59);
		
		startTime = new Timestamp(startDate.getTimeInMillis());
		endTime = new Timestamp(endDate.getTimeInMillis());
		
		System.out.println(startTime);
		System.out.println(endTime);
	}
	
	public CSVCreator() {
		// TODO Auto-generated constructor stub
	}

	public void makeCSV(Invoice invoice) throws IOException {
		String filePath = ROOT_DIR + invoice.getCsvPath();
		Date transactionDate;
		writer = new CsvWriter(filePath);

		// Meta Info
		String invoiceNo = invoice.getInvoiceNo() + "_" + invoice.getVersionNo();
		String partnerName = invoice.getEpaPartner().getFullName();
		String parnterId = String.valueOf(invoice.getEpaPartner().getEpaNo());
		
		// Header
		writer.endRecord();
		writer.writeRecord(INVOICE_CSV_HEADER);
		
		List<RIN> rins = invoice.getRins();
		
		Collections.sort(rins, new Comparator<RIN>()
		{			
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
		
		
		// Data RIN
		TransacionType type;
		for (RIN r: rins) {

			if (r.getRinStatus().equalsIgnoreCase("RETIRED")) {
				type = TransacionType.RETIREMENT;
			} else if (invoice.getInvoiceType().equalsIgnoreCase("PURCHASE"))
				type = TransacionType.PURCHASE;
			else if (invoice.getInvoiceType().equalsIgnoreCase("SALE"))
				type = TransacionType.SALE;
			else throw new IllegalArgumentException("Wrong RIN transaction type");
			
			writer.write(invoiceNo);
			writer.write("4731");
			writer.write("KEMPLER & CO., INC.");
			writer.write(type.toString());
			writer.write(getTransactionDate(r, type));
			writer.write(partnerName);
			writer.write(parnterId);
			writer.write(r.toString());
			writer.endRecord();
		}
		writer.close();
	}
	
	/**
	 * Create RF200 report
	 * @param year (e.g. 2008)
	 * @param quarter (e.g. 3)
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public String createReport(int year, int quarter) throws IOException {
		
		int k1Purchase = 0; 
		int k1Sale = 0; 
		int k1Retired = 0;
		int k2Purchase = 0; 
		int k2Sale = 0;
		int k2Retired = 0;
		
		List<RIN> inventory = new ArrayList<RIN>();
		List<RIN> rins = new ArrayList<RIN>(); 

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		inventory = (ArrayList<RIN>)em.createQuery("SELECT r FROM RIN r").getResultList();
		tx.commit();
		
		setTimeRange(year, quarter);
		
		System.out.println(inventory.size());
		
		for (RIN r: inventory) {
			if (r.getRinStatus().equalsIgnoreCase("RETIRED")) {
				if (r.getRetiredDate().after(startTime) && r.getRetiredDate().before(endTime))
					rins.add(r);
			} else {
				if (checkSale(r)) {
					RIN purchaseRIN = new RIN(r);
					purchaseRIN.setRinStatus("SOLD");
					rins.add(purchaseRIN);
				}
			}
			if (checkPurchase(r)) {
				RIN purchaseRIN = new RIN(r);
				purchaseRIN.setRinStatus("AVAILABLE");
				rins.add(purchaseRIN);
			}
		}
		
		System.out.println(rins.size());
		
		Collections.sort(rins, new Comparator<RIN>(){
			public int compare(RIN r1, RIN r2) {
				return getTransactionTime(r1).compareTo(getTransactionTime(r2));
			}
		});
		
		String filePath = ROOT_DIR + "report\\" + year + "_Q" + quarter + ".csv";
		writer = new CsvWriter(filePath);

		// Meta Info
		writer.write("## RFS RIN Transaction Report");
		writer.endRecord();
		writer.write("## RFS0200");
		// FIXME
		writer.write("Updated: " + new Date(System.currentTimeMillis()).toString());
		writer.endRecord();
		writer.write("## For instructions on columns 1-7 see Overhead for Renewable Fuel Standard Reports: OH-RFS01");
		writer.endRecord();
		writer.write("## OMB Control No. 2060-XXXX Expires Pending (See Instructions)");
		writer.endRecord();
		writer.write("##           1");
		for (int i = 1; i < 16; i++)
			writer.write(i + "");
		for (int i = 0; i < 3; i++) {
			writer.endRecord();
			writer.write("##");
		}
		for (int i = 0; i < 6; i++) {
			writer.write("");
		}
		writer.write("Compliance");
		writer.write("Compliance Basis/");
		for (int i = 0; i < 4; i++) {
			writer.write("");
		}
		writer.write("Trading Partner");
		writer.endRecord();

		// header RIN
		writer.writeRecord(RF200_HEADER);
		
		String today = new Date(System.currentTimeMillis()).toString();
		// Data
		for (RIN r: rins) {
			writer.write("RFS0200");
			writer.write("R");
			writer.write("Y");
			writer.write(today);
			writer.write(year + "");
			writer.write("4731");
			writer.write("KEMPLER & CO., INC.");
			writer.write("Q" + quarter);
			writer.write("NOTOP");
			writer.write(getTransactionType(r));
			if (r.getRinStatus().equalsIgnoreCase("RETIRED"))
				writer.write(r.getRetiredCode());
			else writer.write("N/A");
			writer.write(getTransactionDate(r));
			writer.write(getEPApartner(r));
			writer.write(getEPANo(r));
			writer.write(r.toString());
			writer.endRecord();
			
			// calculate the statistics value
			if (r.getRinType() == '1') {
				if (r.getRinStatus().equalsIgnoreCase("AVAILABLE"))
					k1Purchase += r.getGallonAmount();
				else if (r.getRinStatus().equalsIgnoreCase("SALE"))
					k1Sale += r.getGallonAmount();
				else if (r.getRinStatus().equalsIgnoreCase("RETIRED"))
					k1Retired += r.getGallonAmount();
			} else if (r.getRinType() == '2') {
				if (r.getRinStatus().equalsIgnoreCase("AVAILABLE"))
					k2Purchase += r.getGallonAmount();
				else if (r.getRinStatus().equalsIgnoreCase("SALE"))
					k2Sale += r.getGallonAmount();	
				else if (r.getRinStatus().equalsIgnoreCase("RETIRED"))
					k2Retired += r.getGallonAmount();
			} 
		}
		
		// meta data for RFS100
		writer.write("## RFS RIN Activity Report");
		writer.endRecord();
		writer.write("## RFS0100");
		// FIXME
		writer.write("Issue Date: " + new Date(System.currentTimeMillis()).toString());
		writer.endRecord();
		writer.write("## For instructions on columns 1-7 see Overhead for Renewable Fuel Standard Reports: OH-RFS01");
		writer.endRecord();
		writer.write("## OMB Control No. 2060-XXXX Expires Pending (See Instructions)");
		writer.endRecord();
		writer.write("##           1");
		for (int i = 1; i < 16; i++)
			writer.write(i + "");
		writer.endRecord();
		
		writer.write("##");
		writer.endRecord();
		
		writer.write("##");
		for (int i = 0; i < 9; i++) {
			writer.write("");
		}
		writer.write("Volume of renewable");
		for (int i = 0; i < 5; i++) {
			writer.write("Current-year");
		}
		for (int i = 0; i < 5; i++) {
			writer.write("Prior-year");
		}
		writer.endRecord();
		
		writer.write("##");
		for (int i = 0; i < 6; i++) {
			writer.write("");
		}
		writer.write("Compliance");
		writer.write("Compliance Basis/");
		writer.write("");
		writer.write("fuel owned at the");
		for (int i = 0; i < 2; i++) {
			writer.write("gallon-RINs owned");
			for (int j = 0; j < 3; j++) {
				writer.write("gallon-RINs");
			}
			writer.write("gallon-RINs owned");
		}
		writer.endRecord();

		// header RIN
		writer.writeRecord(RF100_HEADER);
		
		// data
		for (int i = 1; i <= 2; i++) {
			writer.write(year + "");
			writer.write("4731");
			writer.write("KEMPLER & CO., INC.");
			writer.write("Q" + quarter);
			writer.write("NOTOP");
			writer.write(i + "");
			// TODO: end of the quarter
			// TODO: at the start of the quarter
			if (i == 1) {
				writer.write(k1Purchase + "");
				writer.write(k1Sale + "");
				writer.write(k1Retired + "");
			} else {
				writer.write(k2Purchase + "");
				writer.write(k2Sale + "");
				writer.write(k2Retired + "");
			}
			// TODO: end of the quarter
			// TODO: at the start of the quarter
			writer.endRecord();
		}
		writer.close();
		return "report\\" + year + "_Q" + quarter + ".csv";
	}
	
	private Invoice getInvoice(RIN r) {
		if (r.getRinStatus().equalsIgnoreCase("RETIRED") ||
				r.getRinStatus().equalsIgnoreCase("AVAILABLE") ) {
				for (Invoice i: r.getOriginalRIN().getInvoices())
					if (i.getInvoiceType().equalsIgnoreCase("PURCHASE"))
						return i;
				throw new IllegalArgumentException("No purchase invoice found with this RIN");
			} else {
				for (Invoice i: r.getInvoices())
					if (i.getInvoiceType().equalsIgnoreCase("SALE"))
						return i;
				throw new IllegalArgumentException("No sale invoice found with this RIN");
			}	
	}
	
	private String getEPApartner(RIN r) {
		return getInvoice(r).getEpaPartner().getFullName();
	}

	private String getEPANo(RIN r) {
		return String.valueOf(getInvoice(r).getEpaPartner().getEpaNo());
	}
	
	/**
	 * Get the yyyy-mm-dd format of transaction date for given rin
	 * Here we are using (TODO: may need change) invoice date
	 * @param rin
	 * @return
	 */
	private String getTransactionDate(RIN rin) {
		if (rin.getRinStatus().equalsIgnoreCase("RETIRED")) {
			Timestamp ts = rin.getRetiredDate();
			return new Date(ts.getTime()).toString();
		} 
		if (rin.getRinStatus().equalsIgnoreCase("AVAILABLE")) {
			return rin.returnInvoice(InvoiceIndex.First, TransacionType.PURCHASE).getInvoiceDate();
		}
		if (rin.getRinStatus().equalsIgnoreCase("SOLD")) {
			return rin.returnInvoice(InvoiceIndex.First, TransacionType.SALE).getInvoiceDate();
		}
		throw new IllegalArgumentException("Invalid invoice status");
	}

	private boolean checkPurchase(RIN rin) {
		Timestamp time;
		try {
			time = getTransactionTime(rin, TransacionType.PURCHASE);
		} catch (RINException e) {
			return false;
		}
		if (time.before(startTime) || time.after(endTime))
			return false;
		else return true;
	}
	
	private boolean checkSale(RIN rin) {
		Timestamp time;
		try {
			time = getTransactionTime(rin, TransacionType.SALE);
		} catch (RINException e) {
			return false;
		}
		if (time.before(startTime) || time.after(endTime))
			return false;
		else return true;
	}
	
	private String getTransactionType(RIN rin) {
		if (rin.getRinStatus().equalsIgnoreCase("AVAILABLE"))
			return "PURCHASE";
		else if (rin.getRinStatus().equalsIgnoreCase("SOLD"))
			return "SALE";
		else if (rin.getRinStatus().equalsIgnoreCase("RETIRED"))
			return "RETIRED";
		else throw new IllegalArgumentException("Illegal rin status");
	}
	
	private String getTransactionDate(RIN rin, TransacionType type) {
		Timestamp ts = getTransactionTime(rin, type);
		return new Date(ts.getTime()).toString();
	}
	
	private Timestamp getTransactionTime(RIN rin, TransacionType type) {
		Invoice firstInvoice;
		Timestamp transactionTime;

		if (type == TransacionType.RETIREMENT) {
			if (!rin.getRinStatus().equalsIgnoreCase("RETIRED"))
				throw new IllegalArgumentException(
						"Only retired RIN has retired date");
			transactionTime = rin.getRetiredDate();
		} else {
			firstInvoice = rin.returnInvoice(InvoiceIndex.First, type);
			if (firstInvoice == null)
				throw new RINException("no invoice found");
// TODO:	transactionTime = firstInvoice.getCreateTime(); what should we use?
			transactionTime = new Timestamp(Date.valueOf(firstInvoice.getInvoiceDate()).getTime());
		}
		return transactionTime;
	}
	
	private Timestamp getTransactionTime(RIN rin) {
		if (rin.getRinStatus().equalsIgnoreCase("RETIRED")) {
			return rin.getRetiredDate();
		} 
		if (rin.getRinStatus().equalsIgnoreCase("AVAILABLE")) {
			return rin.returnInvoice(InvoiceIndex.First, TransacionType.PURCHASE).getCreateTime();
		}
		if (rin.getRinStatus().equalsIgnoreCase("SOLD")) {
			return rin.returnInvoice(InvoiceIndex.First, TransacionType.SALE).getCreateTime();
		}
		throw new IllegalArgumentException("Invalid invoice status");
	}
	
	public static void main (String[] args) {
		System.out.println("starting....");
		try {
			new CSVCreator().createReport(2008, 3);
//			new CSVCreator().setTimeRange(2008, 3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package com.kemplerEnergy.model.rins;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Random;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.BaseObject;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.model.ShipMode;

public class Invoice extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6606946211273605032L;

	public enum RinType {
		Empty, K1, K2, Both
	}

	/**
	 * FOR BOTH: ARCHIVED (When replacement request involved, and invoice is not supposed to be changed)
	 * FOR PURCHASE: PENDING (waiting for Accountant to verify),
	 * 			ACCEPTED (Verified by Accountant),
	 * 			REJECTED (Disapproved by Accountant), 
	 * 			INVALID (Rejected by Logistics),
	 * 			REPLACEMENT_REQUESTED (Waiting for vendor to replace retired RIN), 
	 * FOR SELL: SOLD, 
	 * 			 REPLACEMENT_PENDING (customer is in shortage of RINs for replacement, and on the waiting list), 
	 */
	private final static String[] TAG = { "PENDING", "ACCEPTED", "REJECTED", "INVALID", "SOLD", "REPLACEMENT_REQUESTED", "REPLACEMENT_PENDING", "ARCHIVED", "SALE_PENDING" };
	private final static String[] TRADE_TYPE = { "PURCHASE", "SALE" };

	private String transferDate;
	private int versionNo;
	protected String status ;
	private Timestamp createTime;
	private String invoiceNo;
	private String invoiceDate;
	private String invoiceType; //Either "PURCHASE" or "SALE"
	private int expectedGallons;
	
	private List<BOLInfo> bolInfo;

	// RPTD related information
	private String rptdK1;
	private String rptdK2;
	private String invoicePath;
	private String rptdPath;
	private String csvPath;
	
	// when replacement needed, this will store the replacement gallongs sent
	// out to the vendor in sequence
	private ArrayList<Integer> gaps;
	

	// foreign key constraint
	private Invoice lastInvoice;
	private ShipMode mode;
	private EPAPartner epaPartner;

	private List<RIN> rins;
	
	// Non mapping attribute
	private RinType rinType;

	public Invoice(){
		rptdK1 = null;
		rptdK2 = null;
		createTime = new Timestamp(System.currentTimeMillis());
		status = "PENDING";
		versionNo = 1;
		rinType = RinType.Empty;
		rins = new ArrayList<RIN>();
		bolInfo = new ArrayList<BOLInfo>();
		lastInvoice = this;
	}
	
	/**
	 * Generate new version of invoice, bad RINs is not copied
	 * @param i is the last version 
	 */
	public Invoice(Invoice i) {
		this();
		createTime = new Timestamp(System.currentTimeMillis());
		epaPartner = i.getEpaPartner();
		expectedGallons = i.getExpectedGallons();
		invoiceDate = i.getInvoiceDate();
		invoiceNo = i.getInvoiceNo();
		invoiceType = i.getInvoiceType();
		mode = i.getMode();
		for (RIN r: i.getRins()) {
			if (!r.getRinStatus().equalsIgnoreCase("RETIRED"))
				addRIN(r);
		}
		rptdK1 = i.getRptdK1();
		rptdK2 = i.getRptdK2();
		
		transferDate = i.getTransferDate();
		versionNo = i.getVersionNo() + 1;
		lastInvoice = i;
		
		// if gap involved
		gaps = i.getGaps();
	}


	/*
	 * ************************************************************************
	 * GETTER AND SETTER
	 * ************************************************************************
	 */
	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		if (invoiceNo == null) 
			return;
		this.invoiceNo = invoiceNo.trim();
	}
	
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	
	/**
	 * Gets the invoice type
	 * @return "PURCHASE" or "SALE"
	 */
	public String getInvoiceType() {
		return invoiceType;
	}

	
	public List<BOLInfo> getBolInfo() {
		return bolInfo;
	}

	public void setBolInfo(List<BOLInfo> bolInfo) {
		this.bolInfo = bolInfo;
	}

	/**
	 * Sets the invoice type, either "PURCHASE" or "SALE"
	 * @param invoiceType
	 */
	public void setInvoiceType(String invoiceType) {
		if (invoiceType == null) 
			return;
		invoiceType = invoiceType.trim();
		
		for (String s: TRADE_TYPE) {
			if (s.equalsIgnoreCase(invoiceType)) {
				this.invoiceType = invoiceType.toUpperCase();
				return;
			}
		}
		throw new IllegalArgumentException("Illegal invoice type");
	}
	
	public int getExpectedGallons() {
		return expectedGallons;
	}
	public void setExpectedGallons(int expectedGallons) {
		this.expectedGallons = expectedGallons;
	}
	

	
	public List<RIN> getRins() {
		return rins;
	}
	
	public void setRins(List<RIN> rins) {
		this.rins = rins;
	}

	public EPAPartner getEpaPartner() {
		return epaPartner;
	}

	public void setEpaPartner(EPAPartner epaPartner) {
		this.epaPartner = epaPartner;
	}

	public String getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(String transferDate) {
		if (transferDate == null) 
			return;
		this.transferDate = transferDate.trim();
	}


	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int version) {
		this.versionNo = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if (status == null) 
			return;
		status = status.trim();
		for (String s: TAG) {
			if (s.equalsIgnoreCase(status)) {
				this.status = status.toUpperCase();
				return;
			}
		}
		throw new IllegalArgumentException("Illegal invoice status");
	}

	public ShipMode getMode() {
		return mode;
	}

	public void setMode(ShipMode mode) {
		this.mode = mode;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}


	public String getInvoicePath() {
		return invoicePath;
	}

	public void setInvoicePath(String invoicePath) {
		this.invoicePath = invoicePath;
	}

	public String getRptdPath() {
		return rptdPath;
	}

	public void setRptdPath(String rptdPath) {
		this.rptdPath = rptdPath;
	}

	public String getCsvPath() {
		return csvPath;
	}

	public void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}

	public String getRptdK1() {
		return rptdK1;
	}

	public void setRptdK1(String rptdK1) {
		if (rptdK1 == null) 
			return;
		this.rptdK1 = rptdK1.trim();
	}

	public String getRptdK2() {
		return rptdK2;
	}

	public void setRptdK2(String rptdK2) {
		if (rptdK2 == null) 
			return;
		this.rptdK2 = rptdK2.trim();
	}
	
	public Invoice getLastInvoice() {
		return lastInvoice;
	}

	public void setLastInvoice(Invoice originalInvoice) {
		this.lastInvoice = originalInvoice;
	}
	
	public boolean addBOLInfo(BOLInfo b) {
		if (b == null) return false;
		
		if (b.getInvoice() != null)
			b.getInvoice().getBolInfo().remove(b);
		
		b.setInvoice(this);
		return bolInfo.add(b);
	}
	
	public void addBOLInfo(List<BOLInfo> bols) {
		if (bols == null || bols.isEmpty())
			return;
		for (BOLInfo b: bols) {
			addBOLInfo(b);
		}
			
	}
	
	public void addRIN(RIN rin) {
		boolean valid = false;
		if (invoiceType.equalsIgnoreCase("PURCHASE")) {
			for (Invoice i: rin.getInvoices()) {
				if (id == i.getId())
					return;
			}
/*			if (rin.getInvoices().size() != 0) {
//				throw new IllegalArgumentException("This RIN has already associated with other invoices");
				rin.getInvoices().remove();
			}
			else */			
				rin.getInvoices().add(this);
		} else { // Selling invoice
			for (Invoice i: rin.getInvoices()) {
				if (i.getInvoiceType().equalsIgnoreCase("PURCHASE")) {
					valid = true;
					break;
				}
			}
			if (!valid && rin.getId() == rin.getOriginalRIN().getId())
//				throw new IllegalArgumentException("This RIN has not been associated with a buy invoice yet");
				;
		}
		rin.getInvoices().add(this);
		rins.add(rin);
	}
	
/*	// TODO: further validation
	*//**
	 * Bidirectional deletion of the linkage between rin and associated invoices
	 *//*
	public void deleteRIN(RIN rin) {
		rin.getInvoices().remove(this);
		rins.remove(rin);
	}*/
	
	/**
	 * Sets the default status of the invoice.
	 * invoiceNo = "Dummy"; invoiceDate = "1776-07-04"; invoiceType = "PURCHASE";
	 * expectedGallons = 0; transferDate = "2023-02-05"; csvPath = "";
	 * status = "PENDING";
	 */
	public void setDefault() {
//		EPAPartner ep = new EPAPartner();
//		ep.setFullName("Dummy Partner number 1");
//		epaPartner = ep;
		invoiceNo = "Dummy";
		invoiceDate = "1776-07-04";
		invoiceType = "PURCHASE";
		expectedGallons = 0;
		transferDate = "2023-02-05";
		csvPath = "";
		status = "PENDING";
	}
	

	
	/**
	 * 
	 * @return the actual non-retired rins gallons
	 */
	public int calculateActualGallons() {
		int amount = 0;
		for (RIN r: rins){
			if (r.getRinStatus().equalsIgnoreCase("RETIRED") || 
				r.getRinStatus().equalsIgnoreCase("CORRUPTED") ||
				r.getRinStatus().equalsIgnoreCase("TO_BE_RETIRED"))
				continue;
			amount += r.getGallonAmount();
		}
		return amount;
	}
	
	public int calculateGallons(String status) {
		int amount = 0;
		for (RIN r: rins) {
			if (r.getRinStatus().equalsIgnoreCase(status))
				amount += r.getGallonAmount();
		}
		return amount;
	}
	
	
	public int calculateGallons(RinType type) {
		int amount = 0;
		boolean both = false;
		char cType = '0';
		
		if (type == RinType.K1)
			cType = '1';
		else if (type == RinType.K2)
			cType = '2';
		else if (type == RinType.Both)
			both = true;
		else throw new RINException("Not legal argument: rintype can not be empty");
		
		for (RIN r: rins){
			if (r.getRinStatus().equalsIgnoreCase("RETIRED"))
				continue;
			else if (both || r.getRinType() == cType)
				amount += r.getGallonAmount();
		}

		return amount;
	}
	
	public ArrayList<RIN> retrieveRINs(RinType type, String status) {
		ArrayList<RIN> list = new ArrayList<RIN>();
		for (RIN r: rins) {
			if ((r.getRinStatus().equalsIgnoreCase(status)) &&
				 (type == RinType.Both ||
				 (type == RinType.K1 && r.getRinType() == '1') || 
				 (type == RinType.K2 && r.getRinType() == '2')))
				list.add(r);
		}
		return list;
	}
	
	public static void main(String[] args) {
		new Invoice().save();
	}
	
	public boolean isIn(Set<Invoice> invoices) {
		for (Invoice i: invoices) {
			if (i.getId() == id) 
				return true;
		}
		return false;
	}

	public void removeRIN(RIN oldRIN) {
		oldRIN.getInvoices().remove(this);
		rins.remove(oldRIN);
	}
	
	/**
	 * Validate and update all information in the invoice
	 */
	public void validate() {
		validateRPTD();
		// replace all non letter/digit character
		try {
			String fileName = epaPartner.getName().replaceAll("[^a-zA-Z0-9]",
					"");
			fileName += "_" + invoiceNo.replaceAll("[^a-zA-Z0-9]", "") + "_v"
					+ versionNo + "_" + id;
			csvPath = "csv\\" + fileName + ".csv";
			rptdPath = "rptd\\" + fileName + ".pdf";
			invoicePath = "invoice\\" + fileName + ".pdf";
		} catch (NullPointerException e) {
			throw new RINException(
					"invoice #:" + invoiceNo + "invoice date" + invoiceDate + 
					" EPA Partner or its name is not in the invoice!\n" + e.getMessage());
		}
	}
	
	/**
	 * Validate and rewrite the RPTD No
	 */
	private void validateRPTD() {
		if (rins == null || rins.size() == 0)
			return;
//			throw new IllegalArgumentException("Empty RPTD");
		
		for (RIN r: rins) {
			if (rinType == RinType.Both)
				break;
			else if (rinType == RinType.Empty) {
				if (r.getRinType() == '1')
					rinType = RinType.K1;
				else rinType = RinType.K2;
			} else if (rinType == RinType.K1 && r.getRinType() == '2')
				rinType = RinType.Both;
			else if (rinType == RinType.K2 && r.getRinType() == '1')
				rinType = RinType.Both;
		}
		
		
		switch (rinType) {
		case Empty:
			rptdK1 = null;
			rptdK2 = null;
			break;
		case K1:
			if (rptdK1 == null || rptdK1.isEmpty() ) {
				if (rptdK2 != null && !rptdK2.isEmpty() )
					rptdK1 = rptdK2;
				else rptdK1 = invoiceNo + "_K1";
			}
			rptdK2 = null;
			break;
		case K2:
			if (rptdK2 == null || rptdK2.isEmpty() ) {
				if (rptdK1 != null && !rptdK1.isEmpty() )
					rptdK2 = rptdK1;
				else rptdK2 = invoiceNo + "_K2";
			}
			rptdK1 = null;
			break;
		case Both:
			if ((rptdK1 == null || rptdK1.isEmpty()) &&
				(rptdK2 == null || rptdK2.isEmpty())) {
				rptdK1 = invoiceNo + "_K1"; 
				rptdK2 = invoiceNo + "_K2";
			} else if ((rptdK1 == null || rptdK1.isEmpty()) &&
					(rptdK2 != null && !rptdK2.isEmpty())) {
				rptdK1 = rptdK2 + "_K1";
				rptdK2 = rptdK2 + "_K2";	
			} else if ((rptdK2 == null || rptdK2.isEmpty()) &&
					(rptdK1 != null && !rptdK1.isEmpty())) {
				rptdK2 = rptdK1 + "_K2";
				rptdK1 = rptdK1 + "_K1";	
			} else if (rptdK1.equalsIgnoreCase(rptdK2)) {
				rptdK2 = rptdK1 + "_K2";
				rptdK1 = rptdK1 + "_K1";		
			}
			break;
		default:
			throw new IllegalArgumentException("Undefined rin type for invoice");
		}
	}
	


	public ArrayList<Integer> getGaps() {
		return gaps;
	}

	public void setGaps(ArrayList<Integer> gaps) {
		this.gaps = gaps;
	}

}

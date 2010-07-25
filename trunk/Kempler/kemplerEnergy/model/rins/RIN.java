package com.kemplerEnergy.model.rins;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.BaseObject;

public class RIN extends BaseObject implements Comparable<RIN> {

	public enum InvoiceIndex { First, Last }

	public enum TransacionType { PURCHASE, SALE, RETIREMENT }
	/**
	 * 
	 */
	private static final long serialVersionUID = -7059853964577693152L; 
	/*
	 * RIN life cycle: 
	 * NEW: input by logistic 
	 * ACCEPTED: verified by accountant (temp status) 
	 * AVAILABLE: passed system overlapping checkpoint, ready for sale 
	 * SPLIT: split to several smaller RINs, leave for record 
	 * SOLD: like what it said 
	 * CORRUPTED: identified by logistic to be retired
	 * RETIRED: not too hard to understand
	 * REPLACING: replacing from the vendor, next state will be AVAILABLE
	 * TO_BE_RETIRED: only happen when retiring RIN procedure in a pending/
	 *   rejection stage. the RIN is not immediately retired, but after 
	 *   accountant verify or logistic reject. 
	 */
	private static final String[] STATUS = { "NEW", "ACCEPTED", "AVAILABLE", "SPLIT", "SOLD", "RETIRED", "CORRUPTED", "REPLACING", "TO_BE_RETIRED" };


	// retried RIN info, mapped into secondary table
	private static final String[] Types = { "RSP", "RCF", "RIV", "RBH", "RNR",
		"RIR", "RVC", "REO", "USE" };
	public final static String UNVALIDATED = "000000000";
	public final static String INCORRECT =   "111111111";
	public final static String VERIFIED =    "222222222";
	private static boolean validate(String s) {
		/*
		 * if (value[0] != '1' || value[1] < '0' || value[1] > '5') throw new
		 * RINException("Not a valid equivalence value");
		 */

		char[] data = s.toCharArray();
		for (char c : data) {
			if (c < '0' || c > '9')
				return false;
		}
		return true;
	}
	private char rinType;
	private char[] productionYear;

	private char[] companyId;

	private char[] facilityId;
	private char[] batchNbr;
	private char[] equiValue;

	private char energyType;
	private String uniRIN; // concatenation of all above

	private char[] startGallon;
	private char[] endGallon;

	private char[] validationMask;
	protected String rinStatus;
	private int gallonAmount;

	// foreign key constraint
	private Set<Invoice> invoices;
	private RIN originalRIN;
	private String retiredCode;
	
	private int saleIndex = 0;
	
	

	private Timestamp retiredDate;

	public RIN() {
		productionYear = new char[] { '0', '0', '0', '0' };
		companyId = new char[] { '0', '0', '0', '0' };
		facilityId = new char[] { '0', '0', '0', '0' };
		batchNbr = new char[] { '0', '0', '0', '0', '0' };
		equiValue = new char[] { '0', '0' };
		startGallon = new char[] { '0', '0', '0', '0', '0', '0', '0', '0' };
		endGallon = new char[] { '0', '0', '0', '0', '0', '0', '0', '0' };
		validationMask = UNVALIDATED.toCharArray();
		// '0' means pending, '1' means wrong, '2' means verified, 


		uniRIN = getUniRIN();
		rinStatus = "NEW";
		originalRIN = this;
		gallonAmount = 0;
		invoices = new HashSet<Invoice>();
	}

	/**
	 * Used for copy
	 * 
	 * @param r
	 * @throws RINException
	 */
	public RIN(RIN r) throws RINException {

		this();
		setRIN(r.readComponent());
		gallonAmount = r.getGallonAmount();
		
		for (Invoice i : r.getInvoices())
			i.addRIN(this);
		
		originalRIN = r.getOriginalRIN();
		getUniRIN();
	}

	public RIN(String rinNumber) throws RINException {
		this();
		try{
			RINParser rinParser = new RINParser(rinNumber);
			setRIN(rinParser.getRINComponents());
			uniRIN = getUniRIN();
			gallonAmount = calculateAmount();
		}catch (RINException e){
			throw e;
		}
	}

	private RIN(String[] components) throws RINException {
		this();
		this.setRIN(components);
	}

	private int calculateAmount() throws RINException {
		int amount = 0;
//		float accurateAmount;

		for (int i = 7; i >= 0; i--)
			amount += (endGallon[i] - startGallon[i]) * RINs.tensfolder(7 - i);
		amount++;
//		amount *= RINs.toInt(equiValue);
//		accurateAmount = (float) amount / 10;
//		amount = (int) Math.ceil(accurateAmount);

		if (amount <= 0)
			throw new RINException("gallon amount must be positive.");

		return amount;
	}

	public int compareTo(RIN r) {
		return (this.getOriginalRIN().getId() - r.getOriginalRIN().getId());
	}

	public char[] getBatchNbr() {
		return batchNbr;
	}

	public char[] getCompanyId() {
		return companyId;
	}

	public char[] getEndGallon() {
		return endGallon;
	}

	public char getEnergyType() {
		return energyType;
	}

	public char[] getEquiValue() {
		return equiValue;
	}

	public char[] getFacilityId() {
		return facilityId;
	}

	public int getGallonAmount() {
		return gallonAmount;
	}

	public int getId() {
		return id;
	}

	public Set<Invoice> getInvoices() {
		return invoices;
	}

	public RIN getOriginalRIN() {
		return originalRIN;
	}

	public char[] getProductionYear() {
		return productionYear;
	}

	public String getRetiredCode() {
		return retiredCode;
	}

	public Timestamp getRetiredDate() {
		return retiredDate;
	}

	public String getRinStatus() {
		return rinStatus;
	}

	public char getRinType() {
		return rinType;
	}

	public char[] getStartGallon() {
		return startGallon;
	}

	// uniRIN is also automatically verified and corrected by database
	public String getUniRIN() {

		String id = rinType + String.valueOf(productionYear)
		+ String.valueOf(companyId) + String.valueOf(facilityId)
		+ String.valueOf(batchNbr) + String.valueOf(equiValue)
		+ energyType;
		if (!id.equalsIgnoreCase(uniRIN)) 
			uniRIN = id;

		return uniRIN;
	}

	/*
	 * public void setSellInvoice(Invoice sellInvoice) { if (sellInvoice ==
	 * null) return; if (sellInvoice.getInvoiceType().equalsIgnoreCase("PURCHASE"))
	 * throw new RINException("Can\'t associate selling RIN to purchase invoice"
	 * ); if (this.sellInvoice != null &&
	 * this.sellInvoice.getRins().contains(this)) {
	 * this.sellInvoice.getRins().remove(this); } this.sellInvoice =
	 * sellInvoice; this.sellInvoice.getRins().add(this); }
	 */

	/*
	 * public void setBuyInvoice(Invoice buyInvoice) { if (buyInvoice == null)
	 * return; if (buyInvoice.getInvoiceType().equalsIgnoreCase("SALE")) throw
	 * newRINException(
	 * "Can\'t associate purchase type RIN to selling invoice"); if
	 * (this.buyInvoice != null) { this.buyInvoice.getRins().remove(this); }
	 * this.buyInvoice = buyInvoice; this.buyInvoice.getRins().add(this);
	 * this.buyInvoice = buyInvoice; }
	 */

	public char[] getValidationMask() {
		return validationMask;
	}

	public String[] readComponent() {
		return new String[] { String.valueOf(rinType),
				String.valueOf(productionYear), String.valueOf(companyId),
				String.valueOf(facilityId), String.valueOf(batchNbr),
				String.valueOf(equiValue), String.valueOf(energyType),
				String.valueOf(startGallon), String.valueOf(endGallon) };
	}

	/**
	 * get the "first" or "last" version of invoice for this RIN;
	 * @param idx: First one or Last one?
	 * @param type: "PURCHASE" or "SALE"
	 * @return the latest version of invoice, null if no such type of invoice
	 */
	public Invoice returnInvoice(InvoiceIndex idx, TransacionType type) {

		ArrayList<Invoice> invoices = new ArrayList<Invoice>();
		for (Invoice i : this.invoices) {

			if (i.getInvoiceType().equalsIgnoreCase(type.toString()))
				invoices.add(i);
		}

		if (invoices.size() == 0)
			return null;
		/*			throw new RINException("There is no active " + type
					+ " invoice associated with this RIN");*/

		if (idx == InvoiceIndex.First)
			return Collections.min(invoices, new Comparator<Invoice>() {
				public int compare(Invoice r1, Invoice r2) {
					return r1.getVersionNo() - r2.getVersionNo();
				}
			});

		else if (idx == InvoiceIndex.Last)
			return Collections.max(invoices, new Comparator<Invoice>() {
				public int compare(Invoice r1, Invoice r2) {
					return r1.getVersionNo() - r2.getVersionNo();
				}
			});
		
		else throw new IllegalArgumentException("Wrong invoice index");
	}

	public void setBatchNbr(char[] batchNbr) {
		this.batchNbr = batchNbr;
	}

	public void setCompanyId(char[] companyId) {
		this.companyId = companyId;
	}

	public void setDefault() {
		rinType = '0';
		productionYear = new char[] { '0', '0', '0', '0' };
		companyId = new char[] { '0', '0', '0', '0' };
		facilityId = new char[] { '0', '0', '0', '0' };
		batchNbr = new char[] { '0', '0', '0', '0', '0' };
		equiValue = new char[] { '0', '9' };
		energyType = '0';
		startGallon = new char[] { '0', '0', '0', '0', '0', '0', '0', '0' };
		endGallon = new char[] { '0', '0', '0', '0', '0', '0', '0', '0' };
		rinStatus = "NEW";
	}

	public void setEndGallon(char[] endGallon) {
		this.endGallon = endGallon;
	}

	public void setEnergyType(char energyType) {
		this.energyType = energyType;
	}

	public void setEquiValue(char[] equiValue) {
		this.equiValue = equiValue;
	}

	public void setFacilityId(char[] facilityId) {
		this.facilityId = facilityId;
	}

	public void setGallonAmount(int gallonAmount) {
		this.gallonAmount = gallonAmount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInvoices(Set<Invoice> invoices) {
		this.invoices = invoices;
	}

	public void setOriginalRIN(RIN originalRIN) {
		/*
		 * if (!returnUniRIN().equals(originalRIN.returnUniRIN())) throw new
		 * RINException(
		 * "the child RIN doesn\'t match with it\'s parent\'s RIN: child-> " +
		 * returnUniRIN() + " pareant-> " + originalRIN.returnUniRIN());
		 */
		this.originalRIN = originalRIN;
	}

	public void setProductionYear(char[] productionYear) {
		this.productionYear = productionYear;
	}

	public void setRetiredCode(String code) {
		this.retiredCode = code;

		/*		System.out.println("");
		for (int i = 0; i < 10; i++)
			System.out.print("*");
		System.out.println(">" +code + "<");
		System.out.println("");
		for (int i = 0; i < 10; i++)
			System.out.print("*");
		if (code == null || code.trim().isEmpty() || code.equalsIgnoreCase("NULL")) {
			System.out.println("");
			for (int i = 0; i < 10; i++)
				System.out.print("@");
			System.out.println("catched");
			System.out.println("");
			for (int i = 0; i < 10; i++)
				System.out.print("@");
			return;
		}
		if (!rinStatus.equalsIgnoreCase("RETIRED") && 
				!rinStatus.equalsIgnoreCase("CORRUPTED"))
			throw new IllegalArgumentException("Only retired RIN can set these attribute");
		for (String s: Types) {
			if (s.equalsIgnoreCase(code)) {
				this.retiredCode = code.toUpperCase();
				return;
			}
		}
		throw new IllegalArgumentException("Not legal retired code");*/
	}

	public void setRetiredDate(Timestamp retiredDate) {
		this.retiredDate = retiredDate;
	}

	private void setRIN(int i, String s) {
		switch (i) {
		case 0:
			rinType = s.toCharArray()[0];
			break;
		case 1:
			productionYear = s.toCharArray();
			break;
		case 2:
			companyId = s.toCharArray();
			break;
		case 3:
			facilityId = s.toCharArray();
			break;
		case 4:
			batchNbr = s.toCharArray();
			break;
		case 5:
			equiValue = s.toCharArray();
			break;
		case 6:
			energyType = s.toCharArray()[0];
		case 7:
			startGallon = s.toCharArray();
			break;
		case 8:
			endGallon = s.toCharArray();
			break;
		default:
			throw new IndexOutOfBoundsException(
			"legal index for RIN subcatergory is 0-8");
		}
	}

	public void setRIN(String[] rins) throws RINException {
		if (rins.length != 9)
			throw new IndexOutOfBoundsException("legal number of RINs is 8");
		for (int i = 0; i < 9; i++) {
			if (validate(rins[i]))
				setRIN(i, rins[i]);
			else
				new RINException("Incorrect format of rin component");
		}
		gallonAmount = calculateAmount();
	}

	public void setRinStatus(String rinStatus) {
		if (rinStatus == null || rinStatus.trim().isEmpty())
			return;

		if (rinStatus.equalsIgnoreCase("RETIRED") || rinStatus.equalsIgnoreCase("CORRUPTED")) {
			retiredCode = "RSP";
			retiredDate = new Timestamp(System.currentTimeMillis());
		}

		for (String s: STATUS) {
			if (s.equalsIgnoreCase(rinStatus)) {
				this.rinStatus = rinStatus.toUpperCase();
				return;
			}
		}
		throw new IllegalArgumentException("Not legal retired rinStatus");
	}

	public void setRinType(char rinType) {
		if (rinType == '1' || rinType == '2')
			this.rinType = rinType;
		else
			return; // throw new
		// RINException("RIN type can only be 1 or 2");
	}

	public void setStartGallon(char[] startGallon) {
		this.startGallon = startGallon;
	}

	public void setUniRIN(String uniRIN) {
		this.uniRIN = uniRIN;
	}

	public void setValidationMask(char[] validationMask) {
		this.validationMask = validationMask;
	}

	/**
	 * split the old RIN and generate the new RIN the new generated RIN will NOT
	 * be used for sale the old RIN should be used for sell. If the RIN being
	 * split has never been split before. It will generate two new RINs. And 
	 * these two new RINs has no purchase invoice associated. Also the split RIN
	 * has no sale invoice.
	 * If the RIN has been split more than once, the old one will simply being 
	 * replaced with new gallon amount.
	 * 
	 * @return set of RINs, single RIN if this RIN has been split before; two 
	 * RINs if this RIN is newly split, and newRIN get added first. 
	 */
	public ArrayList<RIN> split(int request) {

		if (!rinStatus.equalsIgnoreCase("AVAILABLE"))
			throw new IllegalStateException("Can only split AVAILABLE RIN");
		if (gallonAmount <= request)
			throw new IllegalStateException(
			"Not enough RIN gallons to meet request");

//		float accurateAmount = (float)amount * 10 / RINs.toInt(equiValue);
		// request is the actual number going to be split
		// because gallon amount need to be multiplied by equivalence value/10
//		int request = (int) Math.ceil(accurateAmount);

		ArrayList<RIN> rins = new ArrayList<RIN>();
		RIN newRIN = new RIN(this);
		RIN oldRIN = this;
		newRIN.setEndGallon(endGallon);
		rins.add(newRIN);

		if (id == originalRIN.getId()) {// The one has NOT been split YET
			oldRIN = new RIN(this);
			rins.add(oldRIN);
			rinStatus = "SPLIT";
			for (Invoice i: oldRIN.getInvoices())
				i.getRins().remove(oldRIN);
			oldRIN.getInvoices().clear();
			for (Invoice i: newRIN.getInvoices())
				i.getRins().remove(newRIN);
			newRIN.getInvoices().clear();

		} 

		oldRIN.endGallon = RINs.add(startGallon, (request - 1));
		newRIN.setStartGallon(RINs.add(startGallon, request));
		newRIN.setGallonAmount(gallonAmount - request);
		newRIN.setRinStatus("AVAILABLE");
		newRIN.setValidationMask(VERIFIED.toCharArray());
		oldRIN.gallonAmount = request;
		// it should be set as "SOLD" at caller
		oldRIN.rinStatus = "AVAILABLE";
		oldRIN.setValidationMask(VERIFIED.toCharArray());
		// buyInvoice.addRin(newRIN);
		return rins;
	}
	
	/**
	 * rin number separated by -
	 */
	public String toString() {
		String[] component = readComponent();
		String rin = component[0];
		for (int i = 1; i < component.length; i++)
			rin += "-" + component[i];
		return rin;
	}

	public void setSaleIndex(int saleIndex) {
		this.saleIndex = saleIndex;
	}

	public int getSaleIndex() {
		return saleIndex;
	}
}

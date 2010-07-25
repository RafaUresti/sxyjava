package com.kemplerEnergy.model.rins;

import com.kemplerEnergy.exception.RINException;




/**
 * Builds a RINParser object that parses it's components into RIN components.
 * number of RIN gallons is calculated.
 * @author Xiaoyi Sheng
 *
 */
public class RINParser {
	private String rin;
	private String[] rinComponents = new String[9];
	final static int[] rinComponentLengths = new int[]{1,4,4,5,5,2,1,8,8}; 
	private String exceptionMessage = "Incorrect RIN number format ";

	/**
	 * Constructor to take in 38 digit RIN number as String and break String
	 * fields
	 * 
	 * @param rin 38 digit RIN number
	 */
	public RINParser(String rin) {
		this.rin = rin.trim();
		if (rin.length() == 38)
			parseRIN38();
		else parseSepRIN();//if there are delimiters
		validateRIN();
	}
	private void validateRIN() {
		for (int i = 0; i < 9; i++){
			if (rinComponents[i].length()!= rinComponentLengths[i])
				throw new RINException(exceptionMessage);
		}
	}
	/**
	 * Parse RINs with delimiters " ", ".", "-" "_"and tab (as from excel)
	 */
	private void parseSepRIN() {
		StringTokenizer st = new StringTokenizer (rin, " ,.-_\t");
		if (st.countTokens()!= 9){
			throw new RINException(exceptionMessage+ st.countTokens());
		}
		for (int i = 0; i <9; i++){
			rinComponents[i] = st.nextToken();
		}
	}

	/**
	 * Parses RIN number Stringo separate fields
	 */
	private void parseRIN38() {
		rinComponents[0] = rin.charAt(0) + "";//rinType
		rinComponents[1] = rin.substring(1, 5);//productionYear
		rinComponents[2]= rin.substring(5, 9);//companyRegistrationID
		rinComponents[3] = rin.substring(9, 14);//facilityRegistrationID
		rinComponents[4] = rin.substring(14, 19);//batchNumber
		rinComponents[5] = rin.substring(19, 21);//equivalenceValue
		rinComponents[6] = rin.charAt(21) + "";//renewableEnergyType
		rinComponents[7] = rin.substring(22, 30);//startingGallonNumber
		rinComponents[8] = rin.substring(30, 38);//endingGallonNumber
	}

	public int getGallons(){
		int startingGallon = Integer.valueOf(this.getStartingGallonNumber());
		int endingGallon = Integer.valueOf(this.getEndingGallonNumber());
		return endingGallon - startingGallon;
	}
	
	public String[] getRINComponents(){
		return rinComponents;
	}
	public String getRINType() {
		return rinComponents[0];
	}

	public String getProductionYear() {
		return rinComponents[1];
	}

	public String getCompanyRegistrationID() {
		return rinComponents[2];
	}

	public String getFacilityRegistrationID() {
		return rinComponents[3];
	}

	public String getBatchNumber() {
		return rinComponents[4];
	}

	public String getEquivalenceValue() {
		return rinComponents[5];
	}

	public String getRenewableEnergyType() {
		return rinComponents[6];
	}

	public String getStartingGallonNumber() {
		return rinComponents[7];
	}

	public String getEndingGallonNumber() {
		return rinComponents[8];
	}
	public static int[] getRinComponentLengths() {
		return rinComponentLengths;
	}
}

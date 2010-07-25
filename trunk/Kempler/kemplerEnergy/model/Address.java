package com.kemplerEnergy.model;

public class Address extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1374260825402227008L;
	
	private String streetLine1;
	private String streetLine2;
	private String city;
	private String state;
	private String zipCode;
	private String sanitizedZip;
	private String country;

	private static final String[] Countries = { "United States", "Canada" };
	// foreign key
	private CounterParty counterParty;
	
	public Address() {}
	
	
	public String getStreetLine1() {
		return streetLine1;
	}


	public void setStreetLine1(String streetLine1) {
		this.streetLine1 = streetLine1;
	}


	public String getStreetLine2() {
		return streetLine2;
	}


	public void setStreetLine2(String streetLine2) {
		this.streetLine2 = streetLine2;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}

	public CounterParty getCounterParty() {
		return counterParty;
	}


	public void setCounterParty(CounterParty counterParty) {
		this.counterParty = counterParty;
	}


	public String getZipCode() {
		return zipCode;
	}


	public String getCountry() {
		return country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	/**
	 * the zipCode format is either 5 digits or 9 digits for US address
	 * it will bypass zipcode detection if address is from Canadian 
	 * @param zipCode
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
/*		if (zipCode == null || zipCode.isEmpty() || zipCode.equalsIgnoreCase("NULL"))
			return;
		
		sanitizedZip = zipCode.replaceAll("[^0-9]", "");
		
		if (country.equalsIgnoreCase("Canada") || sanitizedZip.length() == 5)
			this.zipCode = zipCode;
		else if (sanitizedZip.length() < 5) {
			int length = 5 - sanitizedZip.length();
			for (int i = 0; i < length; i++) {
				sanitizedZip = "0" + sanitizedZip;
			}
			this.zipCode = sanitizedZip;
			zipCode = sanitizedZip;
		}
		else if (sanitizedZip.length() == 9)
			this.zipCode = sanitizedZip.substring(0, 5) + '-' + sanitizedZip.substring(5);
		else throw new IllegalArgumentException("Illegal zipCode format: " + zipCode);*/
	}

	public void setCountry(String country) {
		
		for (String s: Countries) {
			if (s.equalsIgnoreCase(country)) {
				this.country = country;
				return;
			}
		}
		throw new IllegalArgumentException(
				"Only support \'United States\' and \'Canada\' You entered: " + country);
	}

	public void setDefault() {
		// TODO Auto-generated method stub
		streetLine1 = "N/A";
		city = "N/A";
		state = "NA";
		zipCode = "00000";
		country = "United States";
	}

}

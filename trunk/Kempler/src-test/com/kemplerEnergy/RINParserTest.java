package com.kemplerEnergy;

import static org.junit.Assert.*;

import org.junit.Test;

import com.kemplerEnergy.model.rins.RINParser;

public class RINParserTest {
	String rin1 = "1	2007	5044	70140	02449	10	2	00331881	00332547"; 
	String rin2 = "1 2007 5044 70140 02449 10 2 00331881 00332547";
	String rin3 = "1,2007,5044,70140,02449,10,2,00331881,00332547";
	String rin6 = "1 ,2007 ,5044 ,70140 ,02449 ,10 ,2 ,00331881 ,00332547";
	String rin4 = "1-2007-5044-70140-02449-10-2-00331881-00332547";
	String rin5 = "12007504470140024491020033188100332547";
	RINParser rinParser1 = new RINParser(rin1);
	RINParser rinParser2 = new RINParser(rin2);
	RINParser rinParser3 = new RINParser(rin3);
	RINParser rinParser4 = new RINParser(rin4);
	RINParser rinParser5 = new RINParser(rin5);

	@Test
	public void testGetGallons(){
		assertEquals(rinParser1.getGallons(), 666);
		assertEquals(rinParser2.getGallons(), 666);
		assertEquals(rinParser3.getGallons(), 666);
		assertEquals(rinParser4.getGallons(), 666);
		assertEquals(rinParser5.getGallons(), 666);
	}
	@Test
	public void testGetRINType() {
		assertEquals(rinParser1.getRINType(), "1");
		assertEquals(rinParser2.getRINType(), "1");
		assertEquals(rinParser3.getRINType(), "1");
		assertEquals(rinParser4.getRINType(), "1");
		assertEquals(rinParser5.getRINType(), "1");
	}

	@Test
	public void testGetYear() {
		assertEquals(rinParser1.getProductionYear(), "2007");
		assertEquals(rinParser2.getProductionYear(), "2007");
		assertEquals(rinParser3.getProductionYear(), "2007");
		assertEquals(rinParser4.getProductionYear(), "2007");
		assertEquals(rinParser5.getProductionYear(), "2007");
	}

	@Test
	public void testGetCompanyRegistrationID() {
		assertEquals(rinParser1.getCompanyRegistrationID(), "5044");
		assertEquals(rinParser2.getCompanyRegistrationID(), "5044");
		assertEquals(rinParser3.getCompanyRegistrationID(), "5044");
		assertEquals(rinParser4.getCompanyRegistrationID(), "5044");
		assertEquals(rinParser5.getCompanyRegistrationID(), "5044");
		
	}

	@Test
	public void testGetFacilityRegistrationID() {
		assertEquals(rinParser1.getFacilityRegistrationID(), "70140");
		assertEquals(rinParser2.getFacilityRegistrationID(), "70140");
		assertEquals(rinParser3.getFacilityRegistrationID(), "70140");
		assertEquals(rinParser4.getFacilityRegistrationID(), "70140");
		assertEquals(rinParser5.getFacilityRegistrationID(), "70140");
	}

	@Test
	public void testGetBatchNumber() {
		assertEquals(rinParser1.getBatchNumber(), "02449");
		assertEquals(rinParser2.getBatchNumber(), "02449");
		assertEquals(rinParser3.getBatchNumber(), "02449");
		assertEquals(rinParser4.getBatchNumber(), "02449");
		assertEquals(rinParser5.getBatchNumber(), "02449");
	}

	@Test
	public void testGetEquivalenceValue() {
		assertEquals(rinParser1.getEquivalenceValue(), "10");
		assertEquals(rinParser2.getEquivalenceValue(), "10");
		assertEquals(rinParser3.getEquivalenceValue(), "10");
		assertEquals(rinParser4.getEquivalenceValue(), "10");
		assertEquals(rinParser5.getEquivalenceValue(), "10");
	}

	@Test
	public void testGetRenewableEnergyType() {
		assertEquals(rinParser1.getRenewableEnergyType(), "2");
		assertEquals(rinParser2.getRenewableEnergyType(), "2");
		assertEquals(rinParser3.getRenewableEnergyType(), "2");
		assertEquals(rinParser4.getRenewableEnergyType(), "2");
		assertEquals(rinParser5.getRenewableEnergyType(), "2");
	}

	@Test
	public void testGetStartingGallonNumber() {
		assertEquals(rinParser1.getStartingGallonNumber(), "00331881");
		assertEquals(rinParser2.getStartingGallonNumber(), "00331881");
		assertEquals(rinParser3.getStartingGallonNumber(), "00331881");
		assertEquals(rinParser4.getStartingGallonNumber(), "00331881");
		assertEquals(rinParser5.getStartingGallonNumber(), "00331881");
	}

	@Test
	public void testGetEndingGallonNumber() {
		assertEquals(rinParser1.getEndingGallonNumber(), "00332547");
		assertEquals(rinParser2.getEndingGallonNumber(), "00332547");
		assertEquals(rinParser3.getEndingGallonNumber(), "00332547");
		assertEquals(rinParser4.getEndingGallonNumber(), "00332547");
		assertEquals(rinParser5.getEndingGallonNumber(), "00332547");
	}

	
}

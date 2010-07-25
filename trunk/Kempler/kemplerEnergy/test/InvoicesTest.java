package com.kemplerEnergy.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;


import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.Invoices;
import com.kemplerEnergy.model.rins.RIN;

public class InvoicesTest{

/*	public String getModuleName() {
		return "com.kemplerEnergy.RINsMain";
	}*/

	final String rin667K1 = "1	2007	5044	70140	02449	10	2	00331881	00332547";
	final String rin7218K1 = "1	2007	4477	70119	24499	10	2	00067204	00074421";
	final String rin667K2 = "2	2007	5044	70140	02449	10	2	00331881	00332547";
	final String rin7218K2 = "2	2007	4477	70119	24499	10	2	00067204	00074421";
	@Test
	public void testCalculateAllowedGaps(){
		ArrayList<Integer> gaps = new ArrayList<Integer>();
		gaps.add(200);
		gaps.add(300);
		gaps.add(400);
		ArrayList<Integer> allowedGaps = Invoices.calculateAllowedGaps(gaps);
		assertEquals(400, (int)allowedGaps.get(0));
		assertEquals(700, (int)allowedGaps.get(1));
		assertEquals(900, (int)allowedGaps.get(2));
	}
	
	@Test
	public void testGenerateRejectionReasons(){
		Invoice invoice = new Invoice();
		invoice.setDefault();
		RIN rin1 = new RIN(rin667K1);
		RIN rin2 = new RIN(rin667K2);
		RIN rin3 = new RIN(rin7218K1);
		RIN rin4 = new RIN(rin7218K2);
		rin1.setRinStatus("NEW");
		rin2.setRinStatus("NEW");
		rin3.setRinStatus("AVAILABLE");
		rin4.setRinStatus("AVAILABLE");
		
		invoice.addRIN(rin1);
		invoice.addRIN(rin2);
		invoice.addRIN(rin3);
		invoice.addRIN(rin4);
		invoice.setExpectedGallons(667+667+7218+7218+1000);//1000 GALLONS SHORT
//		invoice.setStatus("")
		
	}
}

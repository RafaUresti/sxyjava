package com.kemplerEnergy.model.rins;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;
import com.kemplerEnergy.model.rins.Invoices;

public class InvoiceTest /*extends GWTTestCase */{

/*	public String getModuleName() {
		return "com.kemplerEnergy.RINsMain";
	}*/

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
}

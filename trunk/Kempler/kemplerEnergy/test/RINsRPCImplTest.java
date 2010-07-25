package com.kemplerEnergy.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.model.ShipMode;
import com.kemplerEnergy.model.rins.BOLInfo;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINInvoiceSearch;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.kemplerEnergy.server.rins.RINsRPCImpl;

public class RINsRPCImplTest {

	private static final String REPLACEMENT_RIN1 = "1-2009-5223-70161-00731-10-2-00000001-00029287";
	private static final String RIN_FOR_OVERLAP1 = "1-2008-5223-70161-00731-10-2-00000001-00019287";
	private static final String RIN_FOR_OVERLAP2 = "1-2008-5223-70161-00731-10-2-00000001-00029287";
	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	private Invoice purchaseInvoice;
	private Invoice saleInvoice;
	private EPAPartner epaPartner;
	private ShipMode shipMode;
	private static int count = 0;
	
	@SuppressWarnings("unchecked")
	@Before
	public void before() {

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		//Read a random EPA partner from Database and assigned to epaPartner
		getRandomEPAShipMode(em);
		
		clearRINInvoiceTables(em);
		
		tx.commit();
		//Create purchase invoice object
		purchaseInvoice = new Invoice();
		purchaseInvoice.setDefault();
		purchaseInvoice.setEpaPartner(epaPartner);
		purchaseInvoice.setMode(shipMode);
		purchaseInvoice.setInvoiceType("PURCHASE");
		purchaseInvoice.setInvoiceNo("BUY" + count++);
		purchaseInvoice.validate();

		//Create sell invoice object
		saleInvoice = new Invoice();
		saleInvoice.setDefault();
		saleInvoice.setEpaPartner(epaPartner);
		saleInvoice.setMode(shipMode);
		saleInvoice.setInvoiceType("SALE");
		saleInvoice.setInvoiceNo("SELL" + count++);
		saleInvoice.validate();
	}

	/**
	 * Clean up RIN and INVOICE database tables
	 * @param em
	 */
	public static void clearRINInvoiceTables(EntityManager em) {
		try {//Clean up RIN and INVOICE database tables
			List<RIN> rins = (ArrayList<RIN>)em.createQuery("SELECT r FROM RIN r").getResultList();
			for (RIN r: rins) 
				em.remove(r);
		} catch (NoResultException e) {
			System.out.println("empty RIN table");
		}
		
		try {
			List<Invoice> invoices = (ArrayList<Invoice>)em.createQuery("SELECT i FROM Invoice i").getResultList();
			for (Invoice i: invoices)
				em.remove(i);
		} catch (NoResultException e) {
			System.out.println("empty invoice table");
		}
	}

	private Invoice createPurchaseInvoice (EntityManager em) {
		Invoice invoice = new Invoice();
		invoice.setDefault();
		getRandomEPAShipMode(em);
		invoice.setEpaPartner(epaPartner);
		invoice.setMode(shipMode);
		invoice.setInvoiceType("PURCHASE");
		invoice.setInvoiceNo("BUY" + count++);
		invoice.validate();
		return invoice;
	}
	
	private void getRandomEPAShipMode(EntityManager em) {
		try {
			int id = new Random().nextInt(20) + 100;
			Query query = em.createQuery("SELECT e FROM EPAPartner e WHERE e.id = :id");
			query.setParameter("id", id);
			epaPartner = (EPAPartner)query.getSingleResult();
			//Read a random ship mode from Database and assigned to shipMode
			query = em.createQuery("SELECT s FROM ShipMode s WHERE s.id = :id");
			id = new Random().nextInt(4) + 11;
			query.setParameter("id", id);
			shipMode = (ShipMode)query.getSingleResult();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create 10 RINs object, differring only in batch number
	 * @return
	 */
	private ArrayList<RIN> generateRINs() {
		List<RIN> rins = new ArrayList<RIN>();
		for (int i = 0; i < 10; i++) {
			RIN rin = new RIN("1-2008-5131-70129-02233-10-2-00000001-00000010");
			String batchNbr = new Random().nextInt(89999) + 10000 + "";
			int j = i % 2 + 1;
			char c = (char)(j + '0');
			rin.setRinType(c);
			rin.setBatchNbr(batchNbr.toCharArray());
			rins.add(rin);
		}
		return (ArrayList<RIN>) rins;
	}

	@Test	@Ignore
	public void testGetEPAPartner() {
		fail("Not yet implemented");
	}

	@Test	
	@Ignore
	public void testSaveOverlapInvoice() {
		RIN rin = new RIN(RIN_FOR_OVERLAP1);
		rin.setRinStatus("AVAILABLE");
		purchaseInvoice.addRIN(rin);
		
		purchaseInvoice.setStatus("PENDING");
		
		new RINsRPCImpl().saveInvoice(purchaseInvoice);

		Invoice invoice = new Invoice();
		rin = new RIN(RIN_FOR_OVERLAP1);
		rin.setRinStatus("ACCEPTED");
		invoice.setDefault();
		invoice.setEpaPartner(epaPartner);
		invoice.setMode(shipMode);
		invoice.addRIN(rin);
		invoice.setStatus("ACCEPTED");
		invoice.setInvoiceNo("OVERLAP");
		
		new RINsRPCImpl().saveInvoice(invoice);
	}
	
	@Test
	@Ignore
	public void testOverlap2() {
		testSaveOverlapInvoice();
		//Same rin are purchased again
		//Test if the rin is already retired, if the new one will be retired, too
		// old rins should not be in the email
		testSavePurchaseInvoice();
		testSavePendingInvoice();
	}
	
	@Test
	@Ignore
	public void testCustomerReportRetiredRIN() {
		testSellRINs();
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Invoice invoice = (Invoice)em.createQuery("SELECT i FROM Invoice i WHERE i.status = 'SOLD'").getSingleResult();
		Invoice newInvoice = new Invoice(invoice);
		invoice.getRins().get(0).setRinStatus("CORRUPTED");
		newInvoice.setStatus("REPLACEMENT_PENDING");
		tx.commit();
		
		new RINsRPCImpl().saveInvoice(newInvoice);

	}
	
	@Test
	@Ignore
	public void testVendorReportRetiredRINWithoutReplacement() {
		testSellRINs();
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Invoice invoice = (Invoice)em.createQuery("SELECT i FROM Invoice i WHERE i.status = 'ACCEPTED'").getSingleResult();
		Invoice newInvoice = new Invoice(invoice);
		invoice.getRins().get(0).setRinStatus("CORRUPTED");
		newInvoice.setStatus("REPLACEMENT_REQUESTED");
		tx.commit();
		
		new RINsRPCImpl().saveInvoice(newInvoice);

	}
	
	@Test
	@Ignore
	public void testVendorReportRetiredRINWithReplacement() {
		testSellRINs();
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Invoice invoice = (Invoice)em.createQuery("SELECT i FROM Invoice i WHERE i.status = 'ACCEPTED'").getSingleResult();
		tx.commit();

		
		RIN rin = new RIN(REPLACEMENT_RIN1);
		Invoice newInvoice = new Invoice(invoice);
		invoice.getRins().get(0).setRinStatus("CORRUPTED");
		newInvoice.setExpectedGallons(29287);
		newInvoice.addRIN(rin);
		newInvoice.setStatus("PENDING");
		new RINsRPCImpl().saveInvoice(newInvoice);
	}
	
	@Test
	@Ignore
	public void testVendorReportRetiredRINWithReplacement2() {
		testVendorReportRetiredRINWithReplacement();
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Invoice invoice = (Invoice)em.createQuery("SELECT i FROM Invoice i WHERE i.status = 'PENDING'").getSingleResult();
		tx.commit();

		invoice.setStatus("ACCEPTED");
		invoice.getRins().get(1).setRinStatus("ACCEPTED");
		new RINsRPCImpl().saveInvoice(invoice);

	}

	@Test	@Ignore
	public void testGetInvoiceListString() {
		fail("Not yet implemented");
	}

	@Test	
//	@Ignore
	public void testSellRINs() {
		testSaveInvoice();
		BOLInfo b = new BOLInfo();
		b.setDefault();
		b.setBolNumber("xiaoyi");
		BOLInfo b2 = new BOLInfo();
		b2.setDefault();
		b2.setBolNumber("haoda");
		List<BOLInfo> bs = new ArrayList<BOLInfo>();
		bs.add(b);
		bs.add(b2);
		saleInvoice.addBOLInfo(bs);
		saleInvoice.setExpectedGallons(100);
		new RINsRPCImpl().sellRINs(saleInvoice);
	}

	@Test
	@Ignore
	public void testSavePurchaseInvoice() {
		RIN rin = new RIN(RIN_FOR_OVERLAP2);
		purchaseInvoice.addRIN(rin);
		purchaseInvoice.setExpectedGallons(500);
		purchaseInvoice.setStatus("PENDING");
		BOLInfo b = new BOLInfo();
		b.setDefault();
		b.setBolNumber("xiaoyi");
		BOLInfo b2 = new BOLInfo();
		b2.setDefault();
		b2.setBolNumber("haoda");
		
		List<BOLInfo> bs = new ArrayList<BOLInfo>();
		bs.add(b);
		bs.add(b2);
		purchaseInvoice.addBOLInfo(bs);
		purchaseInvoice.validate();

		new RINsRPCImpl().saveInvoice(purchaseInvoice);
		
	}
	
	@Test 
	@Ignore
	public void testSavePendingInvoice() {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery("SELECT i FROM Invoice i WHERE i.status = 'PENDING'");
		query.setMaxResults(1);
		Invoice invoice = (Invoice)query.getSingleResult();
		invoice.setStatus("ACCEPTED");
		invoice.setExpectedGallons(invoice.calculateActualGallons());
		for (RIN r: invoice.getRins()) {
			r.setRinStatus("ACCEPTED");
		}
		tx.commit();
		new RINsRPCImpl().saveInvoice(invoice);

		
	}
	@Test
	@Ignore
	public void testGetInvoiceList() {
		RINInvoiceSearch search = new RINInvoiceSearch();
		search.setInvoiceNumber("as");
		search.setEpaPartner(epaPartner);
		ArrayList<Invoice> invoices = new ArrayList<Invoice>(); 
		invoices = new RINsRPCImpl().getInvoiceList(search);
		System.out.println("Invoice # is " + invoices.get(0).getInvoiceNo());
	}

	@Test	@Ignore
	public void testDeleteRIN() {
		fail("Not yet implemented");
	}

	@Test	@Ignore
	public void testGetInventory() {
		fail("Not yet implemented");
	}

	@Test	@Ignore
	public void testRejectBuyInvoice() {
		fail("Not yet implemented");
	}

	@Test	@Ignore
	public void testGetInvoiceListRINInvoiceSearch() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testSaveInvoice() {
		testSavePurchaseInvoice();
		testSavePendingInvoice();
	}
	
	@Test
	@Ignore
	public void getBOLList() {
		testSaveInvoice();
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT i FROM Invoice i");
		query.setMaxResults(1);
		Invoice i = (Invoice) query.getSingleResult();
		System.out.println(i.getBolInfo());
	}

	/**
	 * Purchase 10 set of RINs from 10 different invoices
	 * retired each one of them in the set
	 */
	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void testCustomerReportRetiredRIN2() {
		RINsRPCImpl rpc = new RINsRPCImpl();

		EntityManager em = emf.createEntityManager();
		clearRINInvoiceTables(em);

		// buy 10x100
		for (int i = 0; i < 10; i++) {
			Invoice buyInvoice = createPurchaseInvoice(em);
			buyInvoice.getRins().addAll(generateRINs());
			for (RIN r: buyInvoice.getRins()) {
				r.setRinStatus("ACCEPTED");
			}
			buyInvoice.validate();
			buyInvoice.setStatus("ACCEPTED");
			buyInvoice.setExpectedGallons(100);
			rpc.saveInvoice(buyInvoice);
		}
		
		// sell 1000
		saleInvoice.setExpectedGallons(1000);
		rpc.sellRINs(saleInvoice);

		// retire each one in the set
		List<Invoice> invoices = new ArrayList<Invoice>();
		invoices = (ArrayList<Invoice>) em.createQuery(
				"SELECT i FROM Invoice i WHERE i.status = 'ACCEPTED'")
				.getResultList();
		Invoice invoice = (Invoice) em.createQuery(
				"SELECT i FROM Invoice i WHERE i.status = 'SOLD'")
				.getSingleResult();
		for (Invoice i : invoices) {
			i.getRins().get(0).setRinStatus("CORRUPTED");
		}
		Invoice newInvoice = new Invoice(invoice);
		newInvoice.setStatus("REPLACEMENT_PENDING");
		rpc.saveInvoice(newInvoice);
	}
}

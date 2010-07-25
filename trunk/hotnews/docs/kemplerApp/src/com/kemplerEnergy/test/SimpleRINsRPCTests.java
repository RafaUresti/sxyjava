package com.kemplerEnergy.test;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.kemplerEnergy.client.rins.RINsVerificationPanel;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.model.ShipMode;
import com.kemplerEnergy.model.rins.BOLInfo;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINInvoiceSearch;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.kemplerEnergy.server.rins.RINsRPCImpl;
import com.sun.corba.se.impl.ior.iiop.RequestPartitioningComponentImpl;

@SuppressWarnings("unchecked")
public class SimpleRINsRPCTests {

	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	RIN rin1 = new RIN("1	2008	1000	10000	70138	10	2	00000101	00000200");//100Gal
	RIN rin1_copy =  new RIN("1	2008	1000	10000	70138	10	2	00000101	00000200");//same as rin1
	RIN rin1_1gal = new RIN("1	2008	1000	10000	70138	10	2	00000200	00000300");//overlap rin1 1gal
	RIN rin1_1Cover = new RIN ("1	2008	1000	10000	70138	10	2	00000100	00000200");//covers rin1
	RIN rin1_1Within = new RIN ("1	2008	1000	10000	70138	10	2	00000101	00000199");//within rin1

	RIN rin2 = new RIN("1	2008	1000	10000	70139	10	2	00000001	00000200");//200Gal
	RIN rin2Split1 = new RIN("1	2008	1000	10000	70139	10	2	00000001	00000100");//100Gal
	RIN rin2Split2 = new RIN("1	2008	1000	10000	70139	10	2	00000101	00000200");//100Gal
	RIN rin2_copy = new RIN("1	2008	1000	10000	70139	10	2	00000001	00000200");
	RIN rin3 = new RIN("2	2008	1000	10000	70140	10	2	00000001	00000300");//300Gal
	RIN rin3_copy = new RIN("2	2008	1000	10000	70140	10	2	00000001	00000300");
	RIN[] rinGroup1 = new RIN[]{rin1, rin2, rin3};

	RIN rin4 = new RIN("1	2008	1000	10000	70142	10	2	00000001	00000400");//400Gal
	RIN rin5 = new RIN("2	2008	1000	10000	70143	10	2	00000001	00000100");//100Gal
	RIN rin6 = new RIN("2	2008	1000	10000	70144	10	2	00000001	00000200");//200Gal
	RIN[] rinGroup2 = new RIN[]{rin4, rin5, rin6};

	RIN[] rinGroup1_2 = new RIN[]{rin1_copy, rin4, rin5};
	RIN[] rinGroup1_2_1gal = new RIN[] {rin1_1gal, rin4, rin5};
	RIN[] rinGroup1_2_Cover = new RIN[]{rin1_1Cover, rin4, rin5};
	RIN[] rinGroup1_2_Within = new RIN[]{rin1_1Within, rin4, rin5};

	RIN[] rinGroup1_2_2RINs = new RIN[]{rin1_copy, rin2_copy, rin6};

	RIN[] rinGroup_FIN = new RIN[]{rin3_copy, rin6};

	//	RIN[] rinGroup1_2_
	ArrayList<Invoice> allInvoices;
	ArrayList<RIN> allRINs;
	ArrayList<Invoice>[] rinInvoices;


	@Before
	public void setUp() throws Exception {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		RINsRPCImplTest.clearRINInvoiceTables(em);
		tx.commit();
	}

	@Test
////	@Ignore
	public void testPurchase(){
		new RINsRPCImpl().saveInvoice(createPurchaseInvoice("p1", rinGroup1, 5765, "RAIL"));

		//Array of invoice lists for each RIN
		rinInvoices = new ArrayList[]{getRINInvoices(rin1), getRINInvoices(rin2),getRINInvoices(rin3)};
		allInvoices = getAllInvoices();

		//Assert Invoice info
		assertEquals(allInvoices.size(), 1);
		assertEquals(allInvoices.get(0).getInvoiceNo(), "p1");
		assertEquals(allInvoices.get(0).getInvoiceType(), "PURCHASE");
		assertEquals(allInvoices.get(0).getStatus(), "PENDING");
		assertEquals(allInvoices.get(0).getTransferDate(), "2008-08-01");
		assertEquals(allInvoices.get(0).getInvoiceDate(), "2008-08-02");
		assertEquals(allInvoices.get(0).getExpectedGallons(), 600);
		assertEquals(allInvoices.get(0).calculateActualGallons(), 600);
		assertEquals(allInvoices.get(0).getMode().getMode(), "Rail");
		assertEquals(allInvoices.get(0).getEpaPartner().getFullName(), "Ad Astra Energy Inc.");
		assertEquals(allInvoices.get(0).getBolInfo().get(0).getBolNumber(), "XiaoyiBOL");
		assertEquals(allInvoices.get(0).getBolInfo().get(0).getQuantity(), 10);
		assertEquals(allInvoices.get(0).getBolInfo().get(0).getLoadNumber(), "small load");
		assertEquals(allInvoices.get(0).getBolInfo().get(1).getBolNumber(), "HaodaBOL");
		assertEquals(allInvoices.get(0).getBolInfo().get(1).getQuantity(), 590);
		assertEquals(allInvoices.get(0).getBolInfo().get(1).getLoadNumber(), "big load");
		printInvoiceInfo(allInvoices.get(0));

		//Assert RIN info
		allRINs = getAllRINs();

		assertEquals(allRINs.size(), 3);
		System.out.println("IN TESTING: VALIDATION MASK FOR UNVALIDATED IS: " + Arrays.toString(RIN.UNVALIDATED.toCharArray()));
		System.out.println("IN TESTING: VALIDATION MASK FOR INCORRECT IS: " + Arrays.toString(RIN.INCORRECT.toCharArray()));
		System.out.println("IN TESTING: VALIDATION MASK FOR VERIFIED IS: " + Arrays.toString(RIN.VERIFIED.toCharArray()));

		for (RIN rin: allRINs){
			assertEquals(Arrays.toString(rin.getValidationMask()), Arrays.toString(new char[]{'0','0','0','0','0','0','0','0','0'}));
		}
		
		assertEquals(allRINs.get(0).toString(), "1-2008-1000-10000-70138-10-2-00000101-00000200");
		assertEquals(allRINs.get(0).getRinStatus(), "NEW");
		

		assertEquals(allRINs.get(1).toString(), "1-2008-1000-10000-70139-10-2-00000001-00000200");
		assertEquals(allRINs.get(1).getRinStatus(), "NEW");

		assertEquals(allRINs.get(2).toString(), "2-2008-1000-10000-70140-10-2-00000001-00000300");
		assertEquals(allRINs.get(2).getRinStatus(), "NEW");

		//Identify invoices that contains the RIN
		assertEquals(rinInvoices[0].size(), 1);
		assertEquals(rinInvoices[0].get(0).getInvoiceNo(), "p1");
		assertEquals(String.valueOf(rinInvoices[0].get(0).getEpaPartner().getEpaNo()), "5765");

		assertEquals(rinInvoices[1].size(), 1);
		assertEquals(rinInvoices[1].get(0).getInvoiceNo(), "p1");
		assertEquals(String.valueOf(rinInvoices[0].get(0).getEpaPartner().getEpaNo()), "5765");

		assertEquals(rinInvoices[2].size(), 1);
		assertEquals(rinInvoices[2].get(0).getInvoiceNo(), "p1");
		assertEquals(String.valueOf(rinInvoices[0].get(0).getEpaPartner().getEpaNo()), "5765");
	}

	@Test
//	@Ignore
	public void testAcceptPurchaseInvoice1(){
		testPurchase();
		allInvoices = getAllInvoices();
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 1);

		acceptInvoice(allInvoices.get(0));
		new RINsRPCImpl().saveInvoice(allInvoices.get(0));
		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 1);
		assertEquals(allInvoices.get(0).getStatus(), "ACCEPTED");
		assertEquals(allRINs.size(), 3);
		assertEquals(allRINs.get(0).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");
	}

	@Test
//	@Ignore
	public void testAccountantRejectPurchaseInvoice(){
		testPurchase();
		allInvoices = getAllInvoices();
		allRINs = getAllRINs();
		Invoice buyInvoice = allInvoices.get(0);
		assertEquals(buyInvoice.getStatus(), "PENDING");
		buyInvoice.setStatus("REJECTED");

		new RINsRPCImpl().saveInvoice(buyInvoice);
		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();
		Invoice rejectedBuyInvoice = allInvoices.get(0);
		assertEquals(rejectedBuyInvoice.getStatus(), "REJECTED");
	}

	@Test
//	@Ignore
	public void testSellRINs100K1(){
		testAcceptPurchaseInvoice1();
		Invoice saleInvoice = createSaleInvoice("s1", 5446, "barge", 100);
		new RINsRPCImpl().sellRINs(saleInvoice);

		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();

		assertEquals(allInvoices.size(), 2);
		assertEquals(allInvoices.get(0).getStatus(), "ACCEPTED");
		assertEquals(allInvoices.get(1).getStatus(), "SOLD");
		assertEquals(allRINs.size(), 3);
		assertEquals(allRINs.get(0).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");

		assertEquals(allRINs.get(0).getInvoices().size(), 2);
		assertEquals(allInvoices.get(1).getInvoiceNo(), "s1");
		assertEquals(allInvoices.get(1).getEpaPartner().getFullName(), "Central States Enterprises, Inc.");
	}

	/**
	 * TESTS <b>SPLITTING</b> selling 200 K1 when in inventory 100k1+200K1+300K2,
	 * The second K1 will be <b>SPLIT</b>
	 */
	@Test
//	@Ignore
	public void testSellRINs200K1Split(){
		testAcceptPurchaseInvoice1();
		Invoice saleInvoice = createSaleInvoice("s1", 5446, "barge", 200);

		new RINsRPCImpl().sellRINs(saleInvoice);

		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 2);
		assertEquals(allRINs.size(), 5);
		assertEquals(allRINs.get(0).toString(), rin1.toString());
		assertEquals(allRINs.get(0).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(0).getInvoices().size(), 2);

		//This RIN only has P invoice
		assertEquals(allRINs.get(1).toString(), rin2.toString());
		assertEquals(allRINs.get(1).getRinStatus(), "SPLIT");
		assertEquals(allRINs.get(1).getInvoices().size(), 1);

		assertEquals(allRINs.get(2).toString(), rin3.toString());
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(2).getInvoices().size(), 1);

		//This RIN only has S invoice
		assertEquals(allRINs.get(3).toString(), rin2Split1.toString());
		assertEquals(allRINs.get(3).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(3).getInvoices().size(), 1);

		//This RIN has no P/S invoice
		assertEquals(allRINs.get(4).toString(), rin2Split2.toString());
		assertEquals(allRINs.get(4).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(4).getInvoices().size(), 0);

	}

	@Test
//	@Ignore
	public void testSellRINs300K1(){
		testAcceptPurchaseInvoice1();
		Invoice saleInvoice = createSaleInvoice("s1", 5446, "barge", 300);

		new RINsRPCImpl().sellRINs(saleInvoice);

		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 2);
		assertEquals(allRINs.size(), 3);

		assertEquals(allRINs.get(0).toString(), rin1.toString());
		assertEquals(allRINs.get(0).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(0).getInvoices().size(), 2);

		assertEquals(allRINs.get(1).toString(), rin2.toString());
		assertEquals(allRINs.get(1).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(1).getInvoices().size(), 2);

		assertEquals(allRINs.get(2).toString(), rin3.toString());
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(2).getInvoices().size(), 1);
	}

	@Test
//	@Ignore
	public void testSellRINs300FIN(){
		testAcceptPurchaseInvoice1();
		Invoice saleInvoice = createSaleInvoice("s1", 5446, "FIN", 300);

		new RINsRPCImpl().sellRINs(saleInvoice);

		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 2);
		assertEquals(allRINs.size(), 3);

		assertEquals(allRINs.get(0).toString(), rin1.toString());
		assertEquals(allRINs.get(0).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(0).getInvoices().size(), 1);

		assertEquals(allRINs.get(1).toString(), rin2.toString());
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(1).getInvoices().size(), 1);

		assertEquals(allRINs.get(2).toString(), rin3.toString());
		assertEquals(allRINs.get(2).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(2).getInvoices().size(), 2);
	}

	/**
	 * Sell all RINs available
	 */
	@Test
//	@Ignore
	public void testSellRINs600(){
		testAcceptPurchaseInvoice1();
		Invoice saleInvoice = createSaleInvoice("s1", 5446, "barge", 600);

		new RINsRPCImpl().sellRINs(saleInvoice);

		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 2);
		assertEquals(allRINs.size(), 3);

		assertEquals(allRINs.get(0).toString(), rin1.toString());
		assertEquals(allRINs.get(0).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(0).getInvoices().size(), 2);

		assertEquals(allRINs.get(1).toString(), rin2.toString());
		assertEquals(allRINs.get(1).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(1).getInvoices().size(), 2);

		assertEquals(allRINs.get(2).toString(), rin3.toString());
		assertEquals(allRINs.get(2).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(2).getInvoices().size(), 2);
	}

	/**
	 * K2 RIN was added first, but K1 RIN was sold first
	 */
	@Test
//	@Ignore
	public void testSellK1RINsFirst(){
		createAcceptK2FirstPurchaseInvoice();

		Invoice saleInvoice = createSaleInvoice("s1", 5446, "barge", 300);

		new RINsRPCImpl().sellRINs(saleInvoice);

		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 2);
		assertEquals(allRINs.size(), 3);

		assertEquals(allRINs.get(0).toString(), rin3.toString());
		assertEquals(allRINs.get(0).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(0).getInvoices().size(), 1);

		assertEquals(allRINs.get(1).toString(), rin1.toString());
		assertEquals(allRINs.get(1).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(1).getInvoices().size(), 2);

		assertEquals(allRINs.get(2).toString(), rin2.toString());
		assertEquals(allRINs.get(2).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(2).getInvoices().size(), 2);
	}

	/**
	 * Sell 600gal FIN when only 300K2 available
	 */
	@Test
//	@Ignore
	public void testOverSellRINs600FIN(){
		testAcceptPurchaseInvoice1();
		Invoice saleInvoice = createSaleInvoice("s1", 5446, "FIN", 600);

		new RINsRPCImpl().sellRINs(saleInvoice);

		allInvoices = getAllInvoices();//gets updated info
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 2);
		assertEquals(allRINs.size(), 3);

		assertEquals(allRINs.get(0).toString(), rin1.toString());
		assertEquals(allRINs.get(0).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(0).getInvoices().size(), 1);

		assertEquals(allRINs.get(1).toString(), rin2.toString());
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(1).getInvoices().size(), 1);

		assertEquals(allRINs.get(2).toString(), rin3.toString());
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(2).getInvoices().size(), 1);

		assertEquals(allInvoices.get(1).getStatus(), "SALE_PENDING");
		assertEquals(allInvoices.get(1).getRins().size(), 0);
	}

	/**
	 * Created 600Gal K2 sale invoice when only 300 available. 
	 * Autosale after another 300K2 is accepted;
	 */
	@Test
//	@Ignore
	public void testAutoSellAfterOverSell600FIN(){
		testOverSellRINs600FIN();
		new RINsRPCImpl().saveInvoice(createPurchaseInvoice("p2", rinGroup2, 5765, "Rail"));
		allInvoices = getAllInvoices();
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 3);
		assertEquals(allInvoices.get(0).getStatus(), "ACCEPTED");
		assertEquals(allInvoices.get(1).getStatus(), "SALE_PENDING");
		assertEquals(allInvoices.get(2).getStatus(), "PENDING");

		acceptInvoice(allInvoices.get(2));
		new RINsRPCImpl().saveInvoice(allInvoices.get(2));

		allInvoices = getAllInvoices();
		allRINs = getAllRINs();

		assertEquals(allInvoices.size(), 3);
		assertEquals(allInvoices.get(0).getStatus(), "ACCEPTED");
		assertEquals(allInvoices.get(1).getStatus(), "SOLD");
		assertEquals(allInvoices.get(2).getStatus(), "ACCEPTED");
	}


	/**
	 * 2 invoices with 1 rin the same, retire both and send
	 * emails to both vendors
	 */
	@Test
//	@Ignore
	public void testAcceptOverlapAvailableRINSame(){
		saveAcceptedPurchaseInvoice("p1", rinGroup1, 5765, "BARGE");
		saveAcceptedPurchaseInvoice("p2", rinGroup1_2, 7458, "rail");
		testAcceptOverlapAvailableRINs();
	}

	private void testAcceptOverlapAvailableRINs() {
		allRINs = getAllRINs();
		allInvoices = getAllInvoices();
		assertEquals(allRINs.size(), 6);
		assertEquals(allInvoices.size(), 4);//2 new REPLACEMENT_REQUESTED invoices are generated

		assertEquals(allRINs.get(0).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(3).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(4).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(5).getRinStatus(), "AVAILABLE");

		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(2).getStatus(), "REPLACEMENT_REQUESTED");
		assertEquals(allInvoices.get(3).getStatus(), "REPLACEMENT_REQUESTED");
	}

	/**
	 * 2 invoices with 1 gallon overlap, retire both and send
	 * emails to both vendors
	 */
	@Test 
//	@Ignore
	public void testAcceptOverlapAvailableRIN1gal(){
		saveAcceptedPurchaseInvoice("p1", rinGroup1, 5765, "BARGE");
		saveAcceptedPurchaseInvoice("p2", rinGroup1_2_1gal, 7458, "rail");

		testAcceptOverlapAvailableRINs();
	}

	/**
	 * Overlap Covers
	 */
	@Test 
//	@Ignore
	public void testAcceptOverlapAvailableRINCover(){
		saveAcceptedPurchaseInvoice("p1", rinGroup1, 5765, "barge");
		saveAcceptedPurchaseInvoice("p2", rinGroup1_2_Cover, 7458, "rail");

		testAcceptOverlapAvailableRINs();
	}

	/**
	 * Overlap within
	 */
	@Test 
//	@Ignore
	public void testAcceptOverlapAvailableRINWithin(){
		saveAcceptedPurchaseInvoice("p1", rinGroup1, 5765, "barge");
		saveAcceptedPurchaseInvoice("p2", rinGroup1_2_Within, 7458, "rail");
		testAcceptOverlapAvailableRINs();
	}

	/**
	 * 	2 RINs overlaps 2 RINs
	 */
	@Test 
//	@Ignore
	public void testAcceptOverlapAvailableRIN5(){
		saveAcceptedPurchaseInvoice("p1", rinGroup1, 5765, "barge");
		saveAcceptedPurchaseInvoice("p2", rinGroup1_2_2RINs, 7458, "rail");

		allRINs = getAllRINs();
		allInvoices = getAllInvoices();
		assertEquals(allRINs.size(), 6);
		assertEquals(allInvoices.size(), 4);

		assertEquals(allRINs.get(0).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(1).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(3).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(4).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(5).getRinStatus(), "AVAILABLE");

		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(2).getStatus(), "REPLACEMENT_REQUESTED");
		assertEquals(allInvoices.get(3).getStatus(), "REPLACEMENT_REQUESTED");
	}

	/**
	 * 1 K2 RIN was split into 3 and sold to 2 customers. This RIN was then retired
	 * due to overlap of a new Invoice RIN. All these RINs are retired. </br>
	 * Since no K2 rin is available in inventory, the 2 sale invoices are ARCHIVED
	 * and new invoices set to "REPLACEMENT_PENDING". </br>
	 * 6 emails are sent: 2 initial sales, 2 for vendors to get replacement,
	 * 2 for customers reporting retirements.
	 */
	@Test 
//	@Ignore
	public void testAcceptOverlapSoldRIN1(){
		saveAcceptedPurchaseInvoice("p1", rinGroup1, 5765, "barge");
		sellSaleInvoice("s1", 5446, "FIN", 50);
		sellSaleInvoice("s2", 4888, "FIN", 150);
		saveAcceptedPurchaseInvoice("p2", new RIN[]{rin3_copy}, 7458, "FIN");

		allRINs = getAllRINs();
		allInvoices = getAllInvoices();
		assertEquals(allRINs.size(), 7);//rin3 is split into 4, RIN6 is split into 3
		assertEquals(allInvoices.size(), 8);//Each invoice has a new version now

		assertEquals(allRINs.get(0).getRinStatus(), "AVAILABLE");//rin1
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");//rin2
		assertEquals(allRINs.get(2).getRinStatus(), "RETIRED");//rin3-->split  300gal
		assertEquals(allRINs.get(3).getRinStatus(), "RETIRED");//child of rin3 50gal
		assertEquals(allRINs.get(4).getRinStatus(), "RETIRED");//child of rin3 150gal
		assertEquals(allRINs.get(5).getRinStatus(), "RETIRED");//child of rin3 100gal
		assertEquals(allRINs.get(6).getRinStatus(), "RETIRED");//rin3_copy 300gal

		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(2).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(3).getStatus(), "ARCHIVED");

		assertEquals(getLatestInvoice("p1", 5765).getStatus(), "REPLACEMENT_REQUESTED");
		assertEquals(getLatestInvoice("s1", 5446).getStatus(), "REPLACEMENT_PENDING");
		assertEquals(getLatestInvoice("s2", 4888).getStatus(), "REPLACEMENT_PENDING");
		assertEquals(getLatestInvoice("p2", 7458).getStatus(), "REPLACEMENT_REQUESTED");
	}



	/**
	 * After testAcceptOverlapSoldRIN1, new K2 RIN is bought and accepted.
	 * REPLACEMENT_PENDING invoices are fulfilled.</br>
	 * 2 extra emails are sent with good RIN numbers.
	 */
	@Test 
//	@Ignore
	public void testAcceptOverlapSoldRIN2(){
		testAcceptOverlapSoldRIN1();

		saveAcceptedPurchaseInvoice("p3", new RIN[]{rin6}, 4105, "FIN");
		allRINs = getAllRINs();
		allInvoices = getAllInvoices();
		assertEquals(allRINs.size(), 10);//rin3 is split into 4, RIN6 is split into 3
		assertEquals(allInvoices.size(), 9);//Each invoice has a new version now

		assertEquals(allRINs.get(0).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(2).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(3).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(4).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(5).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(6).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(7).getRinStatus(), "SPLIT");//rin6-->split 200gal
		assertEquals(allRINs.get(8).getRinStatus(), "SOLD");//child of rin6 50gal
		assertEquals(allRINs.get(9).getRinStatus(), "SOLD");//child of rin6 150gal


		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(2).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(3).getStatus(), "ARCHIVED");

		assertEquals(getLatestInvoice("p1", 5765).getStatus(), "REPLACEMENT_REQUESTED");
		assertEquals(getLatestInvoice("s1", 5446).getStatus(), "SOLD");
		assertEquals(getLatestInvoice("s2", 4888).getStatus(), "SOLD");
		assertEquals(getLatestInvoice("p2", 7458).getStatus(), "REPLACEMENT_REQUESTED");
		assertEquals(getLatestInvoice("p3", 4105).getStatus(), "ACCEPTED");
	}

	/**
	 * Similar to testAcceptOverlapSoldRIN1, but we have replacement RINs
	 * available to replace the retired RINs in the sale invoices.
	 * 6 emails are sent: 2 for sales, 2 for replacement request
	 * 2 for replace sales retired RINs
	 */
	@Test
//	@Ignore
	public void testAcceptOverlapSoldRIN3(){
		saveAcceptedPurchaseInvoice("p1", new RIN[]{rin1,rin2,rin3,rin6}, 5765, "barge");
		sellSaleInvoice("s1", 5446, "FIN", 50);
		sellSaleInvoice("s2", 4888, "FIN", 150);
		saveAcceptedPurchaseInvoice("p2", new RIN[]{rin3_copy}, 7458, "FIN");

		allRINs = getAllRINs();
		allInvoices = getAllInvoices();
		assertEquals(allRINs.size(), 10);//rin3 is split into 4, RIN6 is split into 3
		assertEquals(allInvoices.size(), 8);//Each invoice has a new version now

		assertEquals(allRINs.get(0).getRinStatus(), "AVAILABLE");//rin1
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");//rin2
		assertEquals(allRINs.get(2).getRinStatus(), "RETIRED");//rin3-->split  300gal
		assertEquals(allRINs.get(3).getRinStatus(), "SPLIT");//rin6
		assertEquals(allRINs.get(4).getRinStatus(), "RETIRED");//child of rin3 50gal
		assertEquals(allRINs.get(5).getRinStatus(), "RETIRED");//child of rin3 150gal
		assertEquals(allRINs.get(6).getRinStatus(), "RETIRED");//child of rin3 100gal
		assertEquals(allRINs.get(7).getRinStatus(), "RETIRED");//rin3_copy 300gal
		assertEquals(allRINs.get(8).getRinStatus(), "SOLD");//Child of rin6 50gal
		assertEquals(allRINs.get(9).getRinStatus(), "SOLD");//Child of rin6 150gal

		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(2).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(3).getStatus(), "ARCHIVED");

		assertEquals(getLatestInvoice("p1", 5765).getStatus(), "REPLACEMENT_REQUESTED");
		assertEquals(getLatestInvoice("s1", 5446).getStatus(), "SOLD");
		assertEquals(getLatestInvoice("s2", 4888).getStatus(), "SOLD");
		assertEquals(getLatestInvoice("p2", 7458).getStatus(), "REPLACEMENT_REQUESTED");
	}

	/**
	 * Similar to testAcceptOverlapSoldRIN1, but the second
	 * purchase invoice also contains good K2 RINs that could 
	 * be used directly for replacement of the sale invoices.
	 * FIXME available rins in the same invoice not used
	 */ 
	@Test
//	@Ignore
	public void testAcceptOverlapSoldRIN4(){
		saveAcceptedPurchaseInvoice("p1", rinGroup1, 5765, "barge");
		sellSaleInvoice("s1", 5446, "FIN", 50);
		sellSaleInvoice("s2", 4888, "FIN", 150);
		saveAcceptedPurchaseInvoice("p2", rinGroup_FIN, 7458, "FIN");

		allRINs = getAllRINs();
		allInvoices = getAllInvoices();
		assertEquals(allRINs.size(), 10);//rin3 is split into 4, RIN6 is split into 3
		assertEquals(allInvoices.size(), 8);//Each invoice has a new version now

		assertEquals(allRINs.get(0).getRinStatus(), "AVAILABLE");//rin1
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");//rin2
		assertEquals(allRINs.get(2).getRinStatus(), "RETIRED");//rin3-->split  300gal
		assertEquals(allRINs.get(4).getRinStatus(), "RETIRED");//child of rin3 50gal
		assertEquals(allRINs.get(5).getRinStatus(), "RETIRED");//child of rin3 150gal
		assertEquals(allRINs.get(6).getRinStatus(), "RETIRED");//child of rin3 100gal
		assertEquals(allRINs.get(7).getRinStatus(), "RETIRED");//rin3_copy 300gal
		assertEquals(allRINs.get(3).getRinStatus(), "SPLIT");//rin6
		assertEquals(allRINs.get(8).getRinStatus(), "SOLD");//Child of rin6 50gal
		assertEquals(allRINs.get(9).getRinStatus(), "SOLD");//Child of rin6 150gal

		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(2).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(3).getStatus(), "ARCHIVED");

		assertEquals(getLatestInvoice("p1", 5765).getStatus(), "REPLACEMENT_REQUESTED");
		assertEquals(getLatestInvoice("s1", 5446).getStatus(), "SOLD");
		assertEquals(getLatestInvoice("s2", 4888).getStatus(), "SOLD");
		assertEquals(getLatestInvoice("p2", 7458).getStatus(), "REPLACEMENT_REQUESTED");
	}


	@Test 
//	@Ignore
	public void testDeleteNEWRINs(){
		testPurchase();
		allInvoices = getAllInvoices();
		assertEquals(allInvoices.get(0).getRins().size(), 3);
		new RINsRPCImpl().deleteRIN(allInvoices.get(0).getRins().get(0));
		allInvoices = getAllInvoices();
		assertEquals(allInvoices.get(0).getRins().size(), 2);
	}

	/**
	 * Vendor reports retired RIN for 2 times before they were sold. 
	 * rin1 (100gal) then rin2 (200gal). Gap should be 200.
	 * No email was sent 
	 */
	@Test 
//	@Ignore
	public void testVendorRetireAvailableRIN(){
		testAcceptPurchaseInvoice1();
		allInvoices = getAllInvoices();
		Invoice p1 = allInvoices.get(0);
		assertEquals(p1.getStatus(), "ACCEPTED");
		Invoice p1v2 = new Invoice(p1);//A new invoice is created
		p1v2.getRins().get(0).setRinStatus("CORRUPTED");//rin1 is retired
		p1v2.setStatus("REPLACEMENT_REQUESTED");

		new RINsRPCImpl().saveInvoice(p1v2);

		allInvoices = getAllInvoices();
		assertEquals(allInvoices.get(0).getRins().size(), 3);
		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(0).getRins().size(), 3);

		assertEquals(allInvoices.get(1).getRins().size(), 3);
		assertEquals(allInvoices.get(1).getStatus(), "REPLACEMENT_REQUESTED");
		assertEquals(allInvoices.get(1).getRins().size(), 3);
		assertEquals(allInvoices.get(1).getRins().get(0).getRinStatus(), "RETIRED");
		assertEquals(allInvoices.get(1).getRins().get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allInvoices.get(1).getRins().get(1).getRinStatus(), "AVAILABLE");

		assertEquals(allInvoices.get(1).getVersionNo(), 2);

		p1v2 = getLatestInvoice("p1", 5765);
		p1v2.getRins().get(1).setRinStatus("CORRUPTED");//rin2 is retired
		p1v2.setStatus("REPLACEMENT_REQUESTED");

		new RINsRPCImpl().saveInvoice(p1v2);

		allInvoices = getAllInvoices();
		assertEquals(allInvoices.get(0).getRins().size(), 3);
		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(0).getRins().size(), 3);

		assertEquals(allInvoices.get(1).getRins().size(), 3);
		assertEquals(allInvoices.get(1).getStatus(), "REPLACEMENT_REQUESTED");//p1 v2
		assertEquals(allInvoices.get(1).getGaps().size(), 1);//p1 v2
		assertEquals(allInvoices.get(1).getGaps().get(0), 200);//p1 v2


		assertEquals(allInvoices.get(1).getRins().size(), 3);
		assertEquals(allInvoices.get(1).getRins().get(0).getRinStatus(), "RETIRED");
		assertEquals(allInvoices.get(1).getRins().get(1).getRinStatus(), "RETIRED");
		assertEquals(allInvoices.get(1).getRins().get(2).getRinStatus(), "AVAILABLE");

		assertEquals(allInvoices.get(1).getVersionNo(), 2);
	}

	/**
	 * Vendor reports retired RIN that has been sold.
	 * 2 emails should be sent, 1 sales and 1 replacement for the sales
	 */
	@Test
//	@Ignore
	public void testVendorRetireSoldRIN1(){
		testSellRINs100K1();//Bought 100k1, 200k1, 300k2; sold 100k1
		allInvoices = getAllInvoices();
		Invoice p1 = allInvoices.get(0);
		assertEquals(p1.getStatus(), "ACCEPTED");
		Invoice p1v2 = new Invoice(p1);//A new invoice is created
		assertEquals(p1v2.getRins().get(0).toString(), "1-2008-1000-10000-70138-10-2-00000101-00000200");
		p1v2.getRins().get(0).setRinStatus("CORRUPTED");
		p1v2.setStatus("REPLACEMENT_REQUESTED");

		Invoice s1 = allInvoices.get(1);
		assertEquals(s1.getStatus(), "SOLD");


		new RINsRPCImpl().saveInvoice(p1v2);

		allRINs = getAllRINs();
		assertEquals(allRINs.size(), 5);
		assertEquals(allRINs.get(0).getRinStatus(), "RETIRED");//rin1
		assertEquals(allRINs.get(1).getRinStatus(), "SPLIT");//rin2
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");//rin3
		assertEquals(allRINs.get(3).getRinStatus(), "SOLD");//child from rin2
		assertEquals(allRINs.get(4).getRinStatus(), "AVAILABLE");//child from rin2


		allInvoices = getAllInvoices();
		assertEquals(allInvoices.size(), 4);

		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");//original purchase invoice
		assertEquals(allInvoices.get(0).getRins().size(), 3);



		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");//original sale invoice
		assertEquals(allInvoices.get(1).getRins().size(), 1);
		assertEquals(allInvoices.get(1).getRins().get(0).getRinStatus(), "RETIRED");

		assertEquals(allInvoices.get(2).getRins().size(), 3);//purchase invoice v2
		assertEquals(allInvoices.get(2).getStatus(), "REPLACEMENT_REQUESTED");


		assertEquals(allInvoices.get(3).getRins().size(), 2);//sales invoice v2, with the retired RIN and new one
		assertEquals(allInvoices.get(3).getStatus(), "SOLD");
		assertEquals(allInvoices.get(3).getRins().get(0).getRinStatus(), "RETIRED");
		assertEquals(allInvoices.get(3).getRins().get(1).getRinStatus(), "SOLD");
		assertEquals(allInvoices.get(3).getVersionNo(), 2);
	}

	/**
	 * rin2 is sold to 2 customers, then retired by vendor.
	 * 2 sales email, 2 replacement emails
	 */
	@Test 
//	@Ignore
	public void testVendorRetireSoldRIN(){ 
		testSellRINs200K1Split();//Sold rin1 and half of rin2
		Invoice saleInvoice = createSaleInvoice("s2", 4888, "rail", 100);

		new RINsRPCImpl().sellRINs(saleInvoice);//Sold the second half of rin2

		allInvoices = getAllInvoices();
		Invoice p1 = allInvoices.get(0);
		assertEquals(p1.getStatus(), "ACCEPTED");
		Invoice p1v2 = new Invoice(p1);//A new invoice is created
		assertEquals(p1v2.getRins().get(1).toString(), "1-2008-1000-10000-70139-10-2-00000001-00000200");
		p1v2.getRins().get(1).setRinStatus("CORRUPTED");
		p1v2.setStatus("REPLACEMENT_REQUESTED");

		new RINsRPCImpl().saveInvoice(p1v2);//Retire RIN

		allInvoices = getAllInvoices();
		allRINs = getAllRINs();

		assertEquals(allInvoices.size(), 6);//3 original invoices + 3 new ones
		assertEquals(allRINs.size(), 8);//rin1, rin2(3 pieces), rin3 (4 pieces)
		assertEquals(allRINs.get(0).getRinStatus(), "SOLD");//rin1
		assertEquals(allRINs.get(1).getRinStatus(), "RETIRED");//rin2
		assertEquals(allRINs.get(2).getRinStatus(), "SPLIT");//rin3
		assertEquals(allRINs.get(3).getRinStatus(), "RETIRED");//child of rin2
		assertEquals(allRINs.get(4).getRinStatus(), "RETIRED");//child of rin2
		assertEquals(allRINs.get(5).getRinStatus(), "SOLD");//child of rin3
		assertEquals(allRINs.get(6).getRinStatus(), "SOLD");//child of rin3
		assertEquals(allRINs.get(7).getRinStatus(), "AVAILABLE");//child of rin3

		for (int i = 0; i < 3; i ++){
			assertEquals(allInvoices.get(i).getStatus(), "ARCHIVED");//3 original invoices ARCHIVED
		}
		assertEquals(allInvoices.get(3).getStatus(), "REPLACEMENT_REQUESTED");//new purchase invoice
		assertEquals(allInvoices.get(4).getStatus(), "SOLD");//new sale invoice
		assertEquals(allInvoices.get(4).getVersionNo(), 2);//new sale invoice
		assertEquals(allInvoices.get(5).getVersionNo(), 2);//new sale invoice

	}

	/**
	 * Should not affect the original purchase invoice
	 * and no additional email shall be sent to the original vendor.
	 * New REPLACEMENT_REQUESTED email should be sent to
	 * the new purchase invoice vendor and new REPLACEMENT_REQUESTED
	 * invoice is created.
	 */
	@Test 
//	@Ignore
	public void testAcceptOverlapRetiredRIN(){
		testCustomerRetireSoldRINWithReplacement1();
		saveAcceptedPurchaseInvoice("p3", new RIN[]{rin1_copy}, 4105, "RAIL");
		
		allRINs = getAllRINs();
		allInvoices = getAllInvoices();
		assertEquals(allRINs.size(), 6);//rin3 is split into 3, plus rin1, rin2, rin1_copy
		assertEquals(allInvoices.size(), 6);//4 invoices after Retirement. 
											//1 extra ARCHIVED purchase invoice and the corresponding 
											//new REPLACEMENT_REQUESTED invoice 

		assertEquals(allRINs.get(0).getRinStatus(), "RETIRED");//rin1
		assertEquals(allRINs.get(1).getRinStatus(), "SOLD");//rin2
		assertEquals(allRINs.get(2).getRinStatus(), "SPLIT");//rin3
		assertEquals(allRINs.get(3).getRinStatus(), "SOLD");//child of rin3
		assertEquals(allRINs.get(4).getRinStatus(), "AVAILABLE");//child of rin3
		assertEquals(allRINs.get(5).getRinStatus(), "RETIRED");//rin1_copy

		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");//p1
		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");//s1
		assertEquals(allInvoices.get(2).getStatus(), "SOLD");//s1_v2
		assertEquals(allInvoices.get(3).getStatus(), "REPLACEMENT_REQUESTED");//p1_v2
		assertEquals(allInvoices.get(4).getStatus(), "ARCHIVED");//p3
		assertEquals(allInvoices.get(5).getStatus(), "REPLACEMENT_REQUESTED");//p3
	}

	/**
	 * Customer reports retirement of a RIN. No replacement is available.
	 * 1 sales email, 1 request replacement email to vendor, 1 email to 
	 * customer notifying insufficient inventory
	 */
	@Test 
//	@Ignore
	public void testCustomerRetireSoldRINWithoutReplacement1(){
		testSellRINs600();//all rins are sold

		allInvoices = getAllInvoices();
		assertEquals(allInvoices.get(1).getStatus(), "SOLD");

		Invoice s1v2 = new Invoice(allInvoices.get(1));
		s1v2.getRins().get(0).setRinStatus("CORRUPTED");
		s1v2.setStatus("REPLACEMENT_PENDING");

		new RINsRPCImpl().saveInvoice(s1v2);//Retire RIN

		allInvoices = getAllInvoices();
		allRINs = getAllRINs();

		assertEquals(allInvoices.size(), 4);
		assertEquals(allInvoices.get(2).getStatus(), "REPLACEMENT_PENDING");
		assertEquals(allInvoices.get(3).getStatus(), "REPLACEMENT_REQUESTED");


		assertEquals(allRINs.size(), 3);
		assertEquals(allRINs.get(0).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(1).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(2).getRinStatus(), "SOLD");
	}

	/**
	 * Customer reports retirement of a RIN. Replacement is available.
	 * 1 sales email, 1 request replacement email to vendor, 1 email to 
	 * customer replacing the retired RIN
	 * FIXME sometimes the retired RIN doesn't show up in the replacement sales invoice
	 */
	@Test 
//	@Ignore
	public void testCustomerRetireSoldRINWithReplacement1(){
		testSellRINs300K1();//rin1 and rin2 are sold

		allInvoices = getAllInvoices();
		assertEquals(allInvoices.get(1).getStatus(), "SOLD");

		Invoice s1v2 = new Invoice(allInvoices.get(1));
		s1v2.getRins().get(0).setRinStatus("CORRUPTED");
		s1v2.setStatus("REPLACEMENT_PENDING");

		new RINsRPCImpl().saveInvoice(s1v2);//Retire RIN

		allInvoices = getAllInvoices();
		allRINs = getAllRINs();

		assertEquals(allInvoices.size(), 4);
		assertEquals(allInvoices.get(2).getStatus(), "SOLD");
		assertEquals(allInvoices.get(3).getStatus(), "REPLACEMENT_REQUESTED");

		assertEquals(allRINs.size(), 5);//rin3 is split
		assertEquals(allRINs.get(0).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(1).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(2).getRinStatus(), "SPLIT");
		assertEquals(allRINs.get(3).getRinStatus(), "SOLD");
		assertEquals(allRINs.get(4).getRinStatus(), "AVAILABLE");
	}

	/**
	 * RIN is sold to 2 customers, then retired by 1 customer.
	 * 2 sales email, 2 replacement email and 1 RIN replacement request
	 * email to vendor
	 */
	@Test
//	@Ignore
	public void testCustomerRetireSoldRINWithReplacement2(){
		testSellRINs200K1Split();//Sold rin1 and half of rin2
		Invoice saleInvoice = createSaleInvoice("s2", 4888, "rail", 100);

		new RINsRPCImpl().sellRINs(saleInvoice);//Sold the second half of rin2

		allInvoices = getAllInvoices();
		Invoice s1 = allInvoices.get(1);
		assertEquals(s1.getStatus(), "SOLD");
		Invoice s1v2 = new Invoice(s1);//A new invoice is created
		assertEquals(s1v2.getRins().get(1).toString(), "1-2008-1000-10000-70139-10-2-00000001-00000100");
		s1v2.getRins().get(1).setRinStatus("CORRUPTED");
		s1v2.setStatus("REPLACEMENT_PENDING");

		new RINsRPCImpl().saveInvoice(s1v2);//Sold the second half of rin2

		allInvoices = getAllInvoices();
		allRINs = getAllRINs();

		assertEquals(allInvoices.size(), 6);
		assertEquals(allRINs.size(), 8);
		for (int i = 0; i < 3; i ++){
			assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		}
		assertEquals(allInvoices.get(3).getStatus(), "SOLD");
		assertEquals(getLatestInvoice("s2", 4888).getStatus(), "SOLD");
		assertEquals(getLatestInvoice("p1", 5765).getStatus(), "REPLACEMENT_REQUESTED");

		assertEquals(allRINs.get(0).getRinStatus(), "SOLD");//rin1
		assertEquals(allRINs.get(1).getRinStatus(), "RETIRED");//rin2
		assertEquals(allRINs.get(2).getRinStatus(), "SPLIT");//rin3
		assertEquals(allRINs.get(3).getRinStatus(), "RETIRED");//child of rin2
		assertEquals(allRINs.get(4).getRinStatus(), "RETIRED");//child of rin2
		assertEquals(allRINs.get(5).getRinStatus(), "SOLD");//child of rin3
		assertEquals(allRINs.get(6).getRinStatus(), "SOLD");//child of rin3
		assertEquals(allRINs.get(7).getRinStatus(), "AVAILABLE");//child of rin3

	}

	/**
	 * Vendor retires and replaces an AVAILABLE RIN. No email shall be sent.
	 * New PENDING invoice gets generated with NEW rin.
	 * FIXME replacement_pending invoice was also generated with email
	 * sent to vendor
	 */
	@Test 
//	@Ignore
	public void testVendorRetireReplaceAvailableRIN(){
		
		testAcceptPurchaseInvoice1();
		allInvoices = getAllInvoices();
		allRINs = getAllRINs();
		
		assertEquals(allInvoices.size(), 1);

		Invoice p1v2 = new Invoice(allInvoices.get(0));
		p1v2.getRins().get(0).setRinStatus("CORRUPTED");//rin1 100gal
		p1v2.getRins().add(rin5);
		new RINsRPCImpl().saveInvoice(p1v2);
		
		allInvoices = getAllInvoices();
		allRINs = getAllRINs();
		assertEquals(allRINs.size(), 4);
		assertEquals(allInvoices.size(), 2);
		
		assertEquals(allInvoices.get(0).getRins().size(), 3);
		assertEquals(allInvoices.get(1).getRins().size(), 4);
		
		assertEquals(allRINs.get(0).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(3).getRinStatus(), "NEW");

		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(1).getStatus(), "PENDING");
		
		assertEquals(allInvoices.get(0).getRins().get(0).getRinStatus(), "RETIRED");
		assertEquals(allInvoices.get(0).getRins().get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allInvoices.get(0).getRins().get(2).getRinStatus(), "AVAILABLE");

		assertEquals(allInvoices.get(1).getRins().get(0).getRinStatus(), "RETIRED");
		assertEquals(allInvoices.get(1).getRins().get(1).getRinStatus(), "AVAILABLE");
		assertEquals(allInvoices.get(1).getRins().get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allInvoices.get(1).getRins().get(3).getRinStatus(), "NEW");

		

	}

	/**
	 * Vendor retires and replaces SOLD RIN. 
	 * 1 email for sales, 1 email for replacement of sales.
	 * FIXME: Like before, 1 extra "REPLACEMENT_REQUESTED" invoice was created.
	 * Vendor was notified with NO RIN in the email.	
	 * And the retired RIN isn't copied into the new sales invoice.
	 */
	@Test 
//	@Ignore
	public void testVendorReplaceSoldRIN(){
		testSellRINs100K1();
		allInvoices = getAllInvoices();
		allRINs = getAllRINs();
		
		assertEquals(allInvoices.size(), 2);

		Invoice p1v2 = new Invoice(allInvoices.get(0));
		p1v2.getRins().get(0).setRinStatus("CORRUPTED");//rin1 100gal
		p1v2.getRins().add(rin5);
		new RINsRPCImpl().saveInvoice(p1v2);
		
		allInvoices = getAllInvoices();
		allRINs = getAllRINs();
		assertEquals(allRINs.size(), 6);//rin1, rin2 split into 3, rin3, rin5
		assertEquals(allInvoices.size(), 4);//2 purchase, 2 sales versions
		
		assertEquals(allInvoices.get(0).getRins().size(), 3);//p1
		assertEquals(allInvoices.get(0).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(1).getRins().size(), 1);//s1
		assertEquals(allInvoices.get(1).getStatus(), "ARCHIVED");
		assertEquals(allInvoices.get(2).getRins().size(), 4);//p1_V2
		assertEquals(allInvoices.get(2).getStatus(), "PENDING");
		assertEquals(getLatestInvoice("s1", 5446).getRins().size(), 2);//s1_V2, retired rin1 and child of rin2
		assertEquals(getLatestInvoice("s1", 5446).getStatus(), "SOLD");

		
		assertEquals(allRINs.get(0).getRinStatus(), "RETIRED");
		assertEquals(allRINs.get(1).getRinStatus(), "SPLIT");//rin2
		assertEquals(allRINs.get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allRINs.get(3).getRinStatus(), "NEW");
		assertEquals(allRINs.get(4).getRinStatus(), "SOLD");//child of rin2
		assertEquals(allRINs.get(5).getRinStatus(), "AVAILABLE");//child of rin2


		//p1
		assertEquals(allInvoices.get(0).getRins().get(0).getRinStatus(), "RETIRED");//
		assertEquals(allInvoices.get(0).getRins().get(1).getRinStatus(), "SPLIT");
		assertEquals(allInvoices.get(0).getRins().get(2).getRinStatus(), "AVAILABLE");
		//s1
		assertEquals(allInvoices.get(1).getRins().get(0).getRinStatus(), "RETIRED");
		//p1_v2
		assertEquals(allInvoices.get(2).getRins().get(0).getRinStatus(), "RETIRED");
		assertEquals(allInvoices.get(2).getRins().get(1).getRinStatus(), "SPLIT");
		assertEquals(allInvoices.get(2).getRins().get(2).getRinStatus(), "AVAILABLE");
		assertEquals(allInvoices.get(2).getRins().get(3).getRinStatus(), "NEW");
		//s1_v2
		assertEquals(getLatestInvoice("s1", 5446).getRins().get(0).getRinStatus(), "RETIRED");
		assertEquals(getLatestInvoice("s1", 5446).getRins().get(1).getRinStatus(), "SOLD");


	}

	//-----------------------------HELPER METHODS-----------------------------------------------------------
	/**
	 * Creates an invoice, accepts it and saves it into the database</br>
	 * Vendor1:	Ad Astra Energy Inc.	5765</br>
	 * Vendor2: Amyris Fuels, Inc.	7458</br>
	 * Vendor3: J. Aron & Company	4105</br>
	 * Customer1:	Central States Enterprises, Inc.	5446</br>
	 * Customer2:	Archer Daniels Midland	4888</br>
	 * @param epaNumber
	 */
	public void saveAcceptedPurchaseInvoice(String invoiceNo, RIN[] rinGroup, int epaNo, String shipMode){
		Invoice invoice = createPurchaseInvoice(invoiceNo, rinGroup, epaNo, shipMode);
		acceptInvoice(invoice);
		new RINsRPCImpl().saveInvoice(invoice);
	}

	/**
	 * Creates an PENDING purchase invoice. No database operation
	 * @param invoiceNo
	 * @param rinGroup
	 * @param epaNo
	 * @param shipMode
	 * @return The purchase invoice
	 */
	public Invoice createPurchaseInvoice(String invoiceNo, RIN[] rinGroup, int epaNo, String shipMode){
		int amount = calculateAmount(rinGroup);

		setRINStatus(rinGroup, "NEW");
		Invoice purchaseInvoice = new Invoice();
		purchaseInvoice.setInvoiceType("PURCHASE");
		purchaseInvoice.setInvoiceNo(invoiceNo);
		purchaseInvoice.setTransferDate("2008-8-1");
		purchaseInvoice.setInvoiceDate("2008-8-2");
		purchaseInvoice.setEpaPartner(getEPAPartner(epaNo));
		purchaseInvoice.setMode(getShipMode("RAIL"));
		if (!shipMode.equalsIgnoreCase("FIN")){
			BOLInfo b1 = new BOLInfo();
			b1.setBolNumber("XiaoyiBOL");
			b1.setQuantity(10);
			b1.setLoadNumber("small load");
			BOLInfo b2 = new BOLInfo();
			b2.setBolNumber("HaodaBOL");
			b2.setQuantity(amount - 10);
			b2.setLoadNumber("big load");
			List<BOLInfo> bs = new ArrayList<BOLInfo>();
			bs.add(b1);
			bs.add(b2);
			purchaseInvoice.addBOLInfo(bs);
		}
		purchaseInvoice.setExpectedGallons(amount);
		purchaseInvoice.validate();
		addRINsToInvoice(rinGroup, purchaseInvoice);

		return purchaseInvoice;
	}

	/**
	 * Creates an purchase invoice with K2 RIN saved before K1, saves the
	 * invoice and accepts by accountant
	 */
	private void createAcceptK2FirstPurchaseInvoice() {
		//Save PENDING Purchase invoice
		rinGroup1 = new RIN[]{rin3, rin1, rin2};//K2 purchased first
		new RINsRPCImpl().saveInvoice(createPurchaseInvoice("p2", rinGroup1, 5765, "Rail"));
		//Accountant accept invoice
		allInvoices = getAllInvoices();
		allRINs = getAllRINs();
		assertEquals(allInvoices.size(), 1);
		acceptInvoice(allInvoices.get(0));
		new RINsRPCImpl().saveInvoice(allInvoices.get(0));
	}

	/**
	 * Creats an Sale invoice and saves to database
	 */
	public void sellSaleInvoice(String invoiceNo, int epaNo, String shipMode, int amount){
		Invoice saleInvoice = createSaleInvoice(invoiceNo, epaNo, shipMode, amount);
		new RINsRPCImpl().sellRINs(saleInvoice);
	}

	/**
	 * Creates an sale invoice without any database operation
	 * @param invoiceNo
	 * @param epaNo
	 * @param shipMode
	 * @param amount
	 * @return The sale invoice
	 */
	public Invoice createSaleInvoice(String invoiceNo, int epaNo, String shipMode, int amount){
		Invoice saleInvoice = new Invoice();
		saleInvoice.setInvoiceType("SALE");
		saleInvoice.setInvoiceNo(invoiceNo);
		saleInvoice.setTransferDate("2008-8-5");
		saleInvoice.setInvoiceDate("2008-8-6");
		saleInvoice.setEpaPartner(getEPAPartner(epaNo));//Ad Astra Energy Inc.
		saleInvoice.setMode(getShipMode(shipMode));
		BOLInfo b1 = new BOLInfo();
		b1.setBolNumber("SaleXiaoyiBOL");
		b1.setQuantity(10);
		b1.setLoadNumber("Sale small load");
		BOLInfo b2 = new BOLInfo();
		b2.setBolNumber("SaleHaodaBOL");
		b2.setQuantity(amount - 10);
		b2.setLoadNumber("Sale big load");
		List<BOLInfo> bs = new ArrayList<BOLInfo>();
		bs.add(b1);
		bs.add(b2);
		saleInvoice.addBOLInfo(bs);
		saleInvoice.setExpectedGallons(amount);
		saleInvoice.validate();
		return saleInvoice;
	}

	/**
	 * Changes the status of an PENDING invoice to accepted and 
	 * all the NEW rins' validation mask to RIN.VERIFIED
	 * @param buyInvoice
	 */
	public void acceptInvoice(Invoice buyInvoice){
		buyInvoice.setStatus("ACCEPTED");
		List<RIN> invoiceRINs = buyInvoice.getRins();
		for (RIN rin: invoiceRINs){
			if (rin.getRinStatus().equalsIgnoreCase("NEW")){
				rin.setValidationMask(RIN.VERIFIED.toCharArray());
				rin.setRinStatus("ACCEPTED");
			}
		}
	}


	/**
	 *  Gets EPA Partner from DB by 4 digit epa number
	 */
	public static EPAPartner getEPAPartner(int epaNumber){
		char[] epaNo = new char[4];
		for (int i = 0; i < 4; i ++){
			epaNo[i] = (epaNumber+"").charAt(i);
		}
		EntityManager em = emf.createEntityManager();
		EPAPartner epa;
		Query query = em.createQuery("SELECT e FROM EPAPartner e WHERE e.epaNo = :epaNo");
		query.setParameter("epaNo", epaNo);
		epa = (EPAPartner)query.getSingleResult();
		return epa;
	}

	/**
	 * Gets shipMode from DB
	 * @param shipMode
	 * @return The shipMode
	 */
	public static ShipMode getShipMode(String shipMode){
		EntityManager em = emf.createEntityManager();
		ShipMode mode;
		Query query = em.createQuery("SELECT s FROM ShipMode s WHERE s.mode = :mode");
		query.setParameter("mode", shipMode);
		mode = (ShipMode)query.getSingleResult();
		return mode;
	}

	/**
	 * Sets the status of a group of RINs
	 * @param rins
	 * @param status
	 */
	public static void setRINStatus(RIN[] rins, String status){
		for (RIN rin: rins){
			rin.setRinStatus(status);
		}
	}
	public static void addRINsToInvoice(RIN[] rins, Invoice invoice){
		for (RIN rin: rins){
			invoice.addRIN(rin);
		}
	}

	/**
	 * Gets all the invoices from DB
	 * @return All the invoices in DB
	 */
	public ArrayList<Invoice> getAllInvoices() {
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT i from Invoice i");
		ArrayList<Invoice> invoices = (ArrayList)query.getResultList();
		Collections.sort(invoices, new invoiceComparator());
		return invoices;
	}

	/**
	 * Retrieves all RIN entries from Database
	 * @return RIN objects
	 */
	public static ArrayList<RIN> getAllRINs(){
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT r from RIN r");
		ArrayList<RIN> rins = (ArrayList)query.getResultList();
		return rins;
	}

	/**
	 * Retrieves the list of invoices that a RIN is in.
	 * @param rin
	 * @return Invoice List sorted by CreationTime
	 */
	public ArrayList<Invoice> getRINInvoices(RIN rin){
		ArrayList<Invoice> rinInvoices = new ArrayList<Invoice>(rin.getInvoices());
		Collections.sort(rinInvoices, new invoiceComparator());
		return rinInvoices;
	}

	/**
	 * Comparator for Invoice according to invoice id
	 * @author Xiaoyi Sheng
	 *
	 */
	public class invoiceComparator implements Comparator<Invoice>{
		public int compare(Invoice o1, Invoice o2) {
			return o1.getId() - o2.getId();
		}
	}

	/**
	 * Calculates the gallon amount sum of all RINs in the rinGroup 
	 * @param rinGroup
	 * @return The total gallon amount
	 */
	public static int calculateAmount(RIN[] rinGroup){
		int amount = 0;
		for (RIN rin: rinGroup){
			amount += rin.getGallonAmount();
		}
		return amount;
	}
	/**
	 * Prints the invoice information
	 * @param invoice
	 */
	public static void printInvoiceInfo(Invoice invoice){
		System.out.println("\n\n------------------------------------------\n\n");
		System.out.println("Invoice No: " + invoice.getInvoiceNo());
		System.out.println("Invoice Type: " + invoice.getInvoiceType());
		System.out.println("Status: " + invoice.getStatus());
		System.out.println("Transfer Date: " + invoice.getTransferDate());
		System.out.println("Invoice Date: " + invoice.getInvoiceDate());
		System.out.println("Expected Gallons: " + invoice.getExpectedGallons());
		System.out.println("Actual Gallons: " + invoice.calculateActualGallons());
		System.out.println("shipMode: " + invoice.getMode().getMode());
		System.out.println("Epa Partner Full Name: " + invoice.getEpaPartner().getFullName());

		for (int i = 0; i < invoice.getBolInfo().size(); i ++){
			System.out.println("BolNumber" + i + ": " + invoice.getBolInfo().get(i).getBolNumber());
			System.out.println("Quantity: " + invoice.getBolInfo().get(i).getQuantity());
			System.out.println("LoadNumber: " + invoice.getBolInfo().get(i).getLoadNumber());
		}

		for (RIN rin: invoice.getRins()){
			System.out.println("Status: " + rin.getRinStatus() + "  "+rin.toString());
			System.out.println("    Validation Mask:" + Arrays.toString(rin.getValidationMask()));
		}
		System.out.println("\n\n------------------------------------------\n\n");
	}

	/**
	 * Gets the latest version invoice from database that has
	 * the specified invoice No and epa No
	 * @param invoiceNo
	 * @param epaNo
	 * @return The latest version invoice
	 */
	public static Invoice getLatestInvoice(String invoiceNo, int epaNo){
		RINInvoiceSearch p1Search = new RINInvoiceSearch();
		p1Search.setEpaPartner(getEPAPartner(epaNo));
		p1Search.setInvoiceNumber(invoiceNo);
		Invoice p1 = new RINsRPCImpl().getInvoice(p1Search);
		return p1;
	}
}

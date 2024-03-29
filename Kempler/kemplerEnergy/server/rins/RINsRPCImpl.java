package com.kemplerEnergy.server.rins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import javax.mail.MessagingException;
import javax.persistence.*;

import com.kemplerEnergy.client.rins.RINsEntryPanel;
import com.kemplerEnergy.client.rins.RINsRPC;
import com.kemplerEnergy.exception.AuthorizationException;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.model.ShipMode;
import com.kemplerEnergy.model.admin.*;
import com.kemplerEnergy.model.rins.*;
import com.kemplerEnergy.model.rins.RIN.InvoiceIndex;
import com.kemplerEnergy.model.rins.RIN.TransacionType;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.kemplerEnergy.util.Mail;

import org.jfree.util.Log;
import net.sf.hibernate4gwt.gwt.HibernateRemoteService;

/**
 * For simplicity, all read-only operation will have its own entity manager Any
 * operation involved database written is synchronized
 * 
 * RPTD generation: has been called by saveAcceptedInvoice() and sellRINs()
 * 
 * @author Ji
 * 
 */
public class RINsRPCImpl extends HibernateRemoteService implements RINsRPC {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8087725318176717376L;

	private static final EntityManagerFactory emf = HibernateUtil.getEmf();

	private final static String[] INVOICE_TYPE = { "PENDING", "ACCEPTED",
			"REJECTED", "SOLD", "REPLACEMENT_REQUESTED", "REPLACEMENT_PENDING",
			"ARCHIVED", "SALE_PENDING" };

	private final static String ROOT_DIR = "c:\\";
	private RPTDCreator rptd;
	private CSVCreator csvCreator;
	private EntityManager saveEm;
	private EntityTransaction saveTx;

	private RINFactory factory = new RINFactory();
	private Invoice invoice;

	public RINsRPCImpl() {
	}

	private void authorize(String method) {

		// TODO: Review
		if (true)
			return; // XXX: test only

		if (!SessionCheck.check(this.getThreadLocalRequest())) {
			throw (new AuthorizationException(
					"log in failed or session expired"));
		}

		final String AUTHORIZATION_ERROR = "You are not authorized for this operation!";
		String userName = (String) this.getThreadLocalRequest().getSession()
				.getAttribute("USER_NAME");
		EntityManager em = HibernateUtil.getEmf().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		String packageName = "com.kemplerEnergy.server.rins.RINsRPCImpl.";
		method = packageName + method;
		User user;

		tx.begin();

		try {
			Query query = em.createQuery("select u from User u "
					+ "where u.userName = :name ");
			query.setParameter("name", userName);

			user = (User) query.getSingleResult();
			tx.commit();
			System.out.println("in authorization");

			for (Group group : user.getGroups()) {
				for (Method m : group.getUsableMethods()) {
					// System.out.println(m.getName());
					if (m.getName().equalsIgnoreCase(method)) {
						System.out.println("passed authorization");
						return;
					}
				}
			}
			throw new AuthorizationException(AUTHORIZATION_ERROR);
		} catch (NoResultException e) {
			throw new AuthorizationException(AUTHORIZATION_ERROR);
		} catch (RuntimeException e) {
			throw new RINException(e.getMessage());
		}
	}

	public EPAPartner getEPAPartner(String fullName) throws RINException,
			AuthorizationException {
		authorize("getEPAPartner(String)");

		EPAPartner company = null;
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			company = (EPAPartner) em.createQuery(
					"select c from EPAPartner c where c.fullName = \'"
							+ fullName + "\' ").getSingleResult();
			return company;

		} catch (NonUniqueResultException e) {
			List<?> companys = em.createQuery(
					"select c from EPAPartner c where c.fullName = \'"
							+ fullName + "\'").getResultList();
			return (EPAPartner) companys.get(0);
		} catch (RuntimeException e) {
			Log.error("Error when get EPA partner", e);
			throw new RINException(e.getMessage());
		} finally {
			tx.commit();
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void saveInvoice(Invoice invoiceToBeSaved)
			throws RINException, AuthorizationException {
		authorize("saveInvoice(Invoice)");

		rptd = new RPTDCreator(emf.createEntityManager());
		csvCreator = new CSVCreator();
		saveEm = emf.createEntityManager();
		saveTx = saveEm.getTransaction();

		System.out.println("Saving...");

		try {
			saveTx.begin();

			invoice = invoiceToBeSaved;
			// Merge the invoice
			if (!saveEm.contains(invoiceToBeSaved)) {
				invoice = (Invoice) saveEm.merge(invoiceToBeSaved);
				saveEm.flush();
				saveEm.refresh(invoice);
				System.out.println("Merging " + invoice.getBolInfo().size());

			}

			invoice.setCreateTime(new Timestamp(System.currentTimeMillis()));
			invoiceToBeSaved.validate();

			System.out.println(invoice.getBolInfo());

			if (invoice.getStatus().equalsIgnoreCase("PENDING")) {
				savePendingInvoice();
			} else if (invoice.getStatus().equalsIgnoreCase("ACCEPTED")) {
				saveAcceptedInvoice();
			} else if (invoice.getStatus().equalsIgnoreCase(
					"REPLACEMENT_REQUESTED")) {
				saveReplacementRequestedInvoice();
			} else if (invoice.getStatus().equalsIgnoreCase(
					"REPLACEMENT_PENDING")) {
				saveReplacementPendingInvoice();
			}

			for (RIN r : invoice.getRins()) {
				if (r.getRinStatus().equalsIgnoreCase("ACCEPTED"))
					r.setRinStatus("AVAILABLE");
			}

			Invoice oldInvoice = invoice.getLastInvoice();
			if (oldInvoice != invoice
					&& !oldInvoice.getStatus().equalsIgnoreCase("ARCHIVED")) {
				oldInvoice.setStatus("ARCHIVED");
				saveEm.persist(oldInvoice);
			}

			saveEm.persist(invoice);
			if (invoice.getStatus().equalsIgnoreCase("ACCEPTED")
					|| invoice.getStatus().equalsIgnoreCase(
							"REPLACEMENT_REQUESTED")) {
				saveEm.flush();
				// Check whether system has customer on RIN waiting list
				factory.autoFulfill(saveEm);
			}
			saveTx.commit();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RINException(e.getMessage());

		} finally {
			saveEm.close();
		}

	}

	/**
	 * Sale invoice: When customer report retired RIN, also two situations 1.
	 * Original invoice is "SOLD" UI suppose to create a new invoice, and leave
	 * old invoice untouched 2. Original invoice is in "REPLACEMENT_PENDING"
	 */
	private void saveReplacementPendingInvoice() {
		Set<RIN> retiredRINs = new HashSet<RIN>();
		Set<Invoice> affectedInvoices = new HashSet<Invoice>();

		for (RIN r : invoice.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("CORRUPTED")) {
				retiredRINs.add(r);
			}
		}
		affectedInvoices = factory.retireRINs(retiredRINs, saveEm);
		// affectedInvoices.remove(invoice);
		factory.reportInvoices(affectedInvoices, saveEm);

		// Change rin status to RETIRED
		for (Invoice badInvoice : affectedInvoices) {
			for (RIN r : badInvoice.getRins()) {
				if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
					r.setRinStatus("RETIRED");
			}
		}
	}

	/**
	 * Purchase invoice: when vendor reported a retired RIN WITHOUT immediate
	 * replacement. Could be following two scenarios 1. The original invoice is
	 * already in "REPLACEMENT_REQUESTED" state: It will contain "RETIRED" RIN,
	 * new retired RIN will be set as "CORRUPTED" (from UI) 2. The original
	 * invoice is in "ACCEPTED" state UI SHOULD create a new invoice, and there
	 * can't be any retired RIN in it. The old invoice MUST NOT be changed
	 * except status to be "ARCHIVED".
	 * 
	 * The input RIN MUST be set as "CORRUPTED" to differ the possible
	 * previously retired RINs. It will be changed to "RETIRED" before saved to
	 * the DB.
	 */
	private void saveReplacementRequestedInvoice() {
		Set<RIN> retiredRINs = new HashSet<RIN>();
		Set<Invoice> affectedInvoices = new HashSet<Invoice>();

		int gap1 = invoice.calculateGallons("RETIRED");
		int gap2 = invoice.calculateGallons("CORRUPTED");
		for (RIN r : invoice.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("CORRUPTED")) {
				retiredRINs.add(r);
			}
		}

		if (gap2 > 0) {
			if (gap1 != 0) { // scene 1: used to be "REPLACEMENT_REQUESTED"
								// (multiple retirements)
				if (invoice.getGaps() == null)
					invoice.setGaps(new ArrayList<Integer>());
				invoice.getGaps().add(gap2);
			} else { // scene 2
				Invoice lastInvoice = invoice.getLastInvoice();
				if (lastInvoice != invoice
						&& !lastInvoice.getStatus()
								.equalsIgnoreCase("ARCHIVED")) {
					lastInvoice.setStatus("ARCHIVED");
				}
			}
			affectedInvoices = factory.retireRINs(retiredRINs, saveEm);
			// No need to send the email to vendor at this time
			affectedInvoices.remove(invoice);
			factory.reportInvoices(affectedInvoices, saveEm);

			for (Invoice badInvoice : affectedInvoices) {
				for (RIN r : badInvoice.getRins()) {
					if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
						r.setRinStatus("RETIRED");
				}
			}
		}
		for (RIN r : invoice.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
				r.setRinStatus("RETIRED");
		}
	}

	/**
	 * Purchase invoice: when new RIN get verified by accountant </br> invoice
	 * can come from two sources:</br> 1. From new inputed invoice: Fully
	 * replaced</br> 2. From replaced invoice </br> a) Partially saved:</br>
	 * current last version -> ACCEPTED</br> new version of invoice ->
	 * REPLACEMENT_REQUESTED</br> b) Fully replaced </br> The system will then
	 * detect the overlapping within our inventory</br> Then system will try to
	 * resolve the waiting list.</br>
	 */
	private void saveAcceptedInvoice() throws Exception {

		int shortage = invoice.getExpectedGallons()
				- invoice.calculateActualGallons();

		System.out.println("Accepted invoice: bol size: "
				+ invoice.getBolInfo().size());

		/*
		 * a) Multiple retirement: Say we have Invoice_v1 retired 300 gallons of
		 * RINs in two batches, one for 100 and another one for 200. The
		 * possible replacement can also come in two batches. For instance,
		 * Invoice_v2 received 100 gallons of replacement. Then the v2's retired
		 * RIN will contain all 300 RINs, while v3's retired RIN will just be
		 * 200 in short.
		 */
		if (shortage > 0) { // 2(a)
			invoice.setStatus("ARCHIVED");
			List<RIN> rinsCopy = new ArrayList<RIN>();
			Invoice newInvoice = new Invoice(invoice);

			// temp pool of bad RIN
			for (RIN r : invoice.getRins()) {
				if (r.getRinStatus().equalsIgnoreCase("RETIRED"))
					rinsCopy.add(r);
			}

			// sort in reverse order!
			Collections.sort(rinsCopy, new Comparator<RIN>() {
				public int compare(RIN o1, RIN o2) {
					return o2.getRetiredDate().compareTo(o1.getRetiredDate());
				}
			});

			// copy the remaining retired RIN to the next invoice
			for (RIN r : rinsCopy) {
				shortage -= r.getGallonAmount();
				if (shortage > 0)
					newInvoice.addRIN(r);
			}

			newInvoice.setStatus("REPLACEMENT_REQUESTED");
			invoice.setGaps(null);
			saveEm.persist(newInvoice);
			// NO RPTD for new invoice now.
		}

		// check overlapping, overlapped RINs will be stored into the
		// retiredRINs. And therefore to be retired.
		List<RIN> toBeRetiredRINs = new ArrayList<RIN>();
		List<RIN> rinCopy = new ArrayList<RIN>();
		Set<RIN> retiredRINs = new HashSet<RIN>();

		for (RIN r : invoice.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("ACCEPTED"))
				rinCopy.add(r);
			if (r.getRinStatus().equalsIgnoreCase("TO_BE_RETIRED"))
				toBeRetiredRINs.add(r);
		}

		List<ArrayList<RIN>> rinGroup = null;
		if (!rinCopy.isEmpty())
			rinGroup = RINs.groupRINs(rinCopy, true);

		// for each RIN group (first 22 digits) check inventory
		if (rinGroup != null && !rinGroup.isEmpty()) {
			checkOverlap(retiredRINs, rinGroup);
		}

		// System auto retire RINs
		Set<Invoice> affectedInvoices = factory.retireRINs(retiredRINs, saveEm);
		factory.reportInvoices(affectedInvoices, saveEm);
		for (Invoice badInvoice : affectedInvoices) {
			for (RIN r : badInvoice.getRins()) {
				if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
					r.setRinStatus("RETIRED");
			}
		}

		/*
		 * Only for "ACCEPTED" invoice, invoice status will be changed
		 */
		if (invoice.getStatus().equalsIgnoreCase("ACCEPTED")) {

			// String filePath = invoice.get

			// Generate RPTD Document
			if (invoice.getVersionNo() == 1) {
				for (RIN r : invoice.getRins()) {
					if (r.getRinStatus().equalsIgnoreCase("ACCEPTED"))
						r.setRinStatus("AVAILABLE");
				}
			}
			System.out.println("generating purchase rptd");
			for (RIN r : invoice.getRins()) {
				System.out.println(r);
				System.out.println(r.getRinStatus());
			}
			System.out
					.println("now : bol size: " + invoice.getBolInfo().size());
			invoice.validate();
			rptd.makeRPTD(invoice);
			csvCreator.makeCSV(invoice);
			if (invoice.getVersionNo() > 1) {
				for (RIN r : invoice.getRins()) {
					if (r.getRinStatus().equalsIgnoreCase("ACCEPTED"))
						r.setRinStatus("AVAILABLE");
				}
			}
		}
		// There are "TO_BE_RETIRED" RINs
		if (!toBeRetiredRINs.isEmpty()) {
			for (RIN r : toBeRetiredRINs)
				r.setRinStatus("CORRUPTED");
			Set<Invoice> invoices = new HashSet<Invoice>();
			invoices.add(toBeRetiredRINs.get(0).returnInvoice(
					InvoiceIndex.Last, TransacionType.PURCHASE));
			factory.reportInvoices(invoices, saveEm);
		}
	}

	private void checkOverlap(Set<RIN> retiredRINs,
			List<ArrayList<RIN>> rinGroup) {
		for (List<RIN> rinList : rinGroup) {
			RIN rin = rinList.get(0);
			List<RIN> rins;
			try {
				Query query = saveEm.createQuery("SELECT r FROM RIN r "
						+ "WHERE (r.rinStatus = \'AVAILABLE\' OR "
						+
						// "	   r.rinStatus = \'SPLIT\' OR " +
						"	   r.rinStatus = \'RETIRED\') AND "
						+ "	  r.uniRIN = :uniRIN");
				query.setParameter("uniRIN", rin.getUniRIN());
				rins = (ArrayList<RIN>) query.getResultList();

			} catch (NoResultException e) {
				rins = null;
			}

			if (rins != null && !rins.isEmpty()) {
				rinList.addAll(rins);
				Set<RIN> overlappedRINs = RINs.checkOverlap(rinList);
				Set<RIN> toBeRetiredRINs = new HashSet<RIN>();
				// if some overlapping check hits pending invoice?
				for (RIN r : overlappedRINs) {
					Invoice lastInvoice = r.returnInvoice(InvoiceIndex.Last,
							TransacionType.PURCHASE);
					if (lastInvoice != null &&
					// lastInvoice.getVersionNo() > 1 &&
							(lastInvoice.getStatus()
									.equalsIgnoreCase("PENDING") || lastInvoice
									.getStatus().equalsIgnoreCase("REJECTED"))) {
						r.setRinStatus("TO_BE_RETIRED");
						toBeRetiredRINs.add(r);
					}
				}
				overlappedRINs.removeAll(toBeRetiredRINs);
				if (!overlappedRINs.isEmpty())
					retiredRINs.addAll(overlappedRINs);
			}
		}
	}

	/**
	 * Purchase invoice: pending invoice could from </br> 1. new Invoice input
	 * by logistic </br> 2. replacing RIN from vendor: </br> NO "CORRUPTED" RIN
	 * in this invoice, only "RETIRED"</br> a) full replacement: </br> b)
	 * partial replacement: </br> 3. retire and replacing RIN from vendor:</br>
	 * NO "RETIRED" RIN in this invoice, only "CORRUPTED", new</br> retired RIN
	 * has been entered into the system. System should</br> auto retire related
	 * RIN in the inventory.</br> a) full replacement: </br> invoice will be
	 * kept as "PENDING" status to wait for</br> accountant verification.
	 * Meanwhile, retire related RIN</br> No email is generated. b) partial
	 * replacement: </br>
	 */
	private void savePendingInvoice() {
		Set<RIN> retiredRINs = new HashSet<RIN>();
		for (RIN r : invoice.getRins()) {
			if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
				retiredRINs.add(r);
		}
		// scene 3
		if (!retiredRINs.isEmpty()) {
			Set<Invoice> affectedInvoices = new HashSet<Invoice>();
			affectedInvoices = factory.retireRINs(retiredRINs, saveEm);
			affectedInvoices.remove(invoice);
			factory.reportInvoices(affectedInvoices, saveEm);

			for (Invoice badInvoice : affectedInvoices) {
				for (RIN r : badInvoice.getRins()) {
					if (r.getRinStatus().equalsIgnoreCase("CORRUPTED"))
						r.setRinStatus("RETIRED");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ShipMode> getShipModeList() throws RINException,
			AuthorizationException {
		authorize("getShipModeList()");

		List<?> shipModes = null;

		try {
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			shipModes = em.createQuery("select s from ShipMode s")
					.getResultList();
			tx.commit();
			return (ArrayList<ShipMode>) shipModes;

		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RINException(e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	public ArrayList<EPAPartner> getEPAPartnerList() throws RINException,
			AuthorizationException {
		authorize("getEPAPartnerList()");
		List<?> company;
		EntityManager em = HibernateUtil.getEmf().createEntityManager();
		try {
			company = em
					.createQuery(
							"select c from EPAPartner c "
									+ "where c.epaNo IS NOT NULL AND "
									+ "c.fullName IS NOT NULL AND "
									+ "c.epaNo != \'\'").getResultList();
			return (ArrayList<EPAPartner>) company;
		} catch (RuntimeException e) {
			throw new RINException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Invoice> getInvoiceList(String status)
			throws RINException, AuthorizationException {
		authorize("getInvoiceList(String)");
		boolean flag = true;
		for (String s : INVOICE_TYPE) {
			if (s.equalsIgnoreCase(status)) {
				status = status.toUpperCase();
				flag = false;
				break;
			}
		}
		if (flag)
			throw new RINException("Illegal invoice status");

		List<Invoice> invoices;
		EntityManager em = HibernateUtil.getEmf().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			invoices = (List<Invoice>) em.createQuery(
					"select i from Invoice i " + "where i.status = \'" + status
							+ "\' AND " + "i.invoiceType = \'PURCHASE\'")
					.getResultList();
			//System.out.println(invoices.get(0).getEpaPartner().getAddresses().
			// get(0));
			tx.commit();
			return (ArrayList<Invoice>) invoices;
		} catch (RuntimeException e) {
			throw new RINException(e.getMessage());
		}
	}

	/**
	 * Selling rins, given an empty invoice, it will create a new invoice
	 * generate RPTD and return the file path
	 * 
	 * @throws RINException
	 *             , AuthorizationException
	 */
	public void sellRINs(Invoice invoice) throws RINException,
			AuthorizationException {
		authorize("sellRINs(Invoice)");

		if (invoice == null)
			throw new RINException("invoice can not be null");

		EntityManager em = emf.createEntityManager();

		try {
			RINFactory rinFactory = new RINFactory();

			EntityTransaction tx = em.getTransaction();
			tx.begin();
			rinFactory.sellRIN(invoice, em);
			tx.commit();

		} catch (Exception ex) {
			throw new RINException(ex.getMessage());
		} finally {
			em.close();
		}
	}

	/**
	 * @return latest version invoice
	 */
	@SuppressWarnings("unchecked")
	public Invoice getInvoice(RINInvoiceSearch search) throws RINException,
			AuthorizationException {
		authorize("getInvoice(RINInvoiceSearch)");
		EntityManager em = HibernateUtil.getEmf().createEntityManager();

		EntityTransaction tx = em.getTransaction();
		String invoiceNo = search.getInvoiceNumber();
		EPAPartner epaPartner = search.getEpaPartner();
		// Need to add other RINInvoiceSearch criterias
		Query query;
		Invoice i;
		try {
			tx.begin();
			query = em
					.createQuery("SELECT i FROM Invoice i "
							+ "WHERE i.invoiceNo = :invoiceNo AND i.epaPartner = :epaPartner");
			query.setParameter("invoiceNo", invoiceNo);
			query.setParameter("epaPartner", epaPartner);
			List<Invoice> invoices = (ArrayList<Invoice>) query.getResultList();
			tx.commit();

			if (invoices == null || invoices.isEmpty())
				throw new RINException("No invoice found");
			return Collections.max(invoices, new Comparator<Invoice>() {
				public int compare(Invoice r1, Invoice r2) {
					return r1.getVersionNo() - r2.getVersionNo();
				}
			});
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RINException(e.getMessage());
		}
	}

	/**
	 * Delete rin from database (Merge the detached rin first)
	 */
	public synchronized void deleteRIN(RIN r) throws RINException,
			AuthorizationException {
		authorize("deleteRIN(RIN)");
		if (!r.getRinStatus().equalsIgnoreCase("NEW")) {
			throw new RINException("Only NEW RINs can be deleted!");
		}
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			RIN newRIN = em.merge(r);

			System.out.println(newRIN.getId());
			/*
			 * Query query =
			 * em.createQuery("SELECT i FROM Invoice i WHERE :rin MEMBER OF i.rins "
			 * ); query.setParameter("rin", newRIN);
			 * 
			 * Invoice i = (Invoice)query.getSingleResult();
			 * System.out.println(i.getId()); i.getRins().remove(newRIN);
			 */

			em.remove(newRIN);

			tx.commit();
		} catch (RuntimeException e) {
			// TODO:
		}
	}

	public int[] getInventory() throws RINException, AuthorizationException {
		authorize("getInventory()");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		int[] result = new RINFactory().getBalance(em);
		tx.commit();

		return result;
	}

	/*
	 * private void run() { EntityManager em = emf.createEntityManager();
	 * 
	 * EntityTransaction tx = em.getTransaction(); try {
	 * 
	 * 
	 * if (i.getGaps() == null) i.setGaps(new ArrayList<Integer>()); for (int
	 * j=0; j<10; j++) i.getGaps().add(j);
	 * 
	 * 
	 * Query query = em.createQuery("SELECT r FROM RIN r WHERE r.id > 1");
	 * query.setMaxResults(1); RIN r = (RIN)query.getSingleResult(); new
	 * RINFactory().retireRINs(r, null);
	 * 
	 * 
	 * // saveInvoice(i);
	 * 
	 * tx.begin(); Query query =
	 * em.createQuery("SELECT i FROM Invoice i WHERE i.id = :id");
	 * query.setParameter("id", 34); query.setMaxResults(1); Invoice i =
	 * (Invoice)query.getSingleResult();
	 * 
	 * System.out.println(i.getId()); // for (Integer x: i.getGaps()) //
	 * System.out.println(x); tx.commit(); new
	 * RINFactory().requestReplacementMail(i);
	 * 
	 * i.getRins().get(0).getInvoices().remove(i); i.getRins().remove(0);
	 * 
	 * // RIN oldRIN = i.getRins().get(0); //
	 * oldRIN.setValidationMask("211222112".toCharArray()); //
	 * newRINs.add(oldRIN); //
	 * 
	 * saveInvoice(i);
	 * 
	 * // testFetch(i); r = new RIN(); r.setDefault();
	 * 
	 * Invoice i = new Invoice(); i.setDefault(); i.setEpaPartner(e);
	 * i.addRin(r);
	 * 
	 * saveInvoice(i); EPAPartner e = new EPAPartner(); e.setDefault();
	 * 
	 * i.setEpaPartner(e);
	 * 
	 * em.persist(i); tx.commit();
	 * 
	 * } catch (RuntimeException e) { Log.error("error", e);
	 * e.printStackTrace(); }
	 * 
	 * 
	 * ArrayList<EPAPartner> company = getRINEPAPartnerList(); for (EPAPartner
	 * c: company) { System.out.println(c.getFullName());
	 * System.out.println(c.getEpaNo()); }
	 * 
	 * ArrayList<Invoice> invoice = getInvoiceList("ACCEPTED"); for (Invoice i:
	 * invoice) { System.out.println(i.getStatus());
	 * System.out.println(i.getRins().size());
	 * System.out.println(i.getEPAPartner().getName());
	 * System.out.println(i.getMode().getMode()); List<RIN> rins = i.getRins();
	 * ArrayList<RIN> newRins = new ArrayList<RIN>(rins);
	 * Collections.sort(newRins); System.out.println("Invoice: " + i.getId());
	 * System.out.println("RIN:"); for (RIN o: newRins) {
	 * System.out.println(o.getId()); System.out.println(o.readRIN()); } }
	 * 
	 * 
	 * RIN rin;
	 * 
	 * for (int j = 0; j < 5; j++) { rin = new RIN(); String[] sr = { "1",
	 * "0001", "0000", "00000", "00000", "10", "0", "00000010", "00000010" };
	 * rin.setRIN(sr); i.addRin(rin); }
	 * 
	 * 
	 * 
	 * }
	 * 
	 * public static void main(String [] args) { new
	 * RINsRPCImpl().testPendingSave(); }
	 * 
	 * private void testPendingSave() { EntityManager em =
	 * emf.createEntityManager(); EntityTransaction tx = em.getTransaction();
	 * tx.begin(); Query query =
	 * em.createQuery("SELECT i FROM Invoice i WHERE i.status = 'PENDING'");
	 * query.setMaxResults(1); Invoice invoice =
	 * (Invoice)query.getSingleResult(); invoice.setStatus("ACCEPTED"); for (RIN
	 * r: invoice.getRins()) { r.setRinStatus("ACCEPTED"); } tx.commit();
	 * saveInvoice(invoice); }
	 */
	/**
	 * Three scenarios: 1. Reject "NEW" invoice entered by logistics The invoice
	 * is not in the database yet, simply send the email. 2. Reject "REJECTED"
	 * invoice disapproved by accountant The invoice is in the database and need
	 * to be removed 3. Reject "REPLACEMENT_REQUESTED" invoice by logistics
	 * 
	 */
	public void rejectBuyInvoice(Invoice incomingInvoice, String reason)
			throws RINException, AuthorizationException {
		authorize("rejectBuyInvoice(Invoice, String)");
		Invoice invoice = incomingInvoice;

		// Not scene 1
		if (!invoice.getInvoiceType().equalsIgnoreCase("NEW")) {

			RINFactory factory = new RINFactory();
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();

			try {
				tx.begin();
				if (!em.contains(incomingInvoice))
					invoice = (Invoice) em.merge(incomingInvoice);
				// scene 2
				if (invoice.getInvoiceType().equalsIgnoreCase("REJECTED")) {
					em.remove(invoice);
					tx.commit();
					em.close();
				} else {
					// check "TO_BE_RETIRED" RIN
					Set<RIN> retiredRINs = new HashSet<RIN>();
					for (RIN r : invoice.getRins()) {
						if (r.getRinStatus().equalsIgnoreCase("TO_BE_RETIRED")) {
							r.setRinStatus("CORRUPTED");
							retiredRINs.add(r);
						}
					}
					if (!retiredRINs.isEmpty()) {
						RIN rin = null;
						for (RIN r : retiredRINs) {
							r.setRinStatus("CORRUPTED");
							rin = r;
						}
						Set<Invoice> affectedInvoices = new HashSet<Invoice>();
						affectedInvoices.add(rin.returnInvoice(
								InvoiceIndex.Last, TransacionType.PURCHASE));
						factory.reportInvoices(affectedInvoices, em);

						for (Invoice badInvoice : affectedInvoices) {
							for (RIN r : badInvoice.getRins()) {
								if (r.getRinStatus().equalsIgnoreCase(
										"CORRUPTED"))
									r.setRinStatus("RETIRED");
							}
						}
					}
				}
				tx.commit();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RINException(
						"error when rejecting invoice, please try later.\n"
								+ e.getMessage());
			}
		}
		Mail mail = new Mail();
		String address = invoice.getEpaPartner().getEmail();
		String subject = "Test: reject invoice " + invoice.getInvoiceNo();
		String content = "You stupid, send us correct stuff OK?" + reason;

		mail.addRecipient(address);
		mail.setEmailSubjectTxt(subject);
		mail.setEmailMsgTxt(content);

		try {
			mail.sendMail();
		} catch (MessagingException e) {
			// TODO save to the outbox
			throw new RINException(
					"Fail to send out the email, please manually send the email."
							+ e.getMessage());
		}
	}

	/**
	 * Select following criteria InvoiceNo: if not empty EPApartner: if not null
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Invoice> getInvoiceList(RINInvoiceSearch invoiceSearch)
			throws RINException, AuthorizationException {
		authorize("getInvoiceList(RINInvoiceSearch)");
		List<Invoice> invoices = new ArrayList<Invoice>();
		List<Invoice> result = new ArrayList<Invoice>();

		EntityManager em = emf.createEntityManager();
		try {
			invoices = (ArrayList<Invoice>) em.createQuery(
					"SELECT i FROM Invoice i").getResultList();
			result.addAll(invoices);
		} catch (NoResultException e) {
			return null;
		} catch (RuntimeException e) {
			throw new RINException(e.getMessage());
		} finally {
			em.close();
		}

		// select by invoiceNo
		if (!invoiceSearch.getInvoiceNumber().isEmpty()) {
			for (Invoice i : invoices) {
				if (!invoiceSearch.getInvoiceNumber().equalsIgnoreCase(
						i.getInvoiceNo()))
					result.remove(i);
			}
		}
		// select by EPApartner
		if (invoiceSearch.getEpaPartner() != null) {
			for (Invoice i : invoices) {
				if (invoiceSearch.getEpaPartner().getId() != i.getEpaPartner()
						.getId())
					result.remove(i);
			}
		}
		// select by Invoice Type
		if (!invoiceSearch.getInvoiceType().equalsIgnoreCase("BOTH")) {
			for (Invoice i : invoices) {
				if (!invoiceSearch.getInvoiceType().equalsIgnoreCase(
						i.getInvoiceType()))
					result.remove(i);
			}
		}

		// Select by From Date, remove the ones whose date is smaller than
		// the from date of invoiceSearch
		if (!invoiceSearch.getFromDate().isEmpty()) {
			for (Invoice i : invoices) {
				if (compareDates(invoiceSearch.getFromDate(), i
						.getInvoiceDate()) > 0)
					result.remove(i);
			}
		}
		// select by To Date
		if (!invoiceSearch.getToDate().isEmpty()) {
			for (Invoice i : invoices) {
				if (compareDates(invoiceSearch.getToDate(), i.getInvoiceDate()) < 0)
					result.remove(i);
			}
		}
		System.out.println(result.size());
		return (ArrayList<Invoice>) result;
	}

	/**
	 * Compares two dates. Dates have to be of format "year-month-day".
	 * Otherwise RINException will be thrown.
	 * 
	 * @param date1
	 * @param date2
	 * @return <code>-1</code> if date1 is earlier than date2, <code>0</code> if
	 *         equal <code>1</code> if later.
	 */
	private static int compareDates(String date1, String date2) {
		String[] d1 = date1.split("-");
		String[] d2 = date2.split("-");
		if (d1.length != 3 || d2.length != 3) {
			throw new RINException("Wrong date format!");
		}
		try {
			for (int i = 0; i < 3; i++) {
				if (Integer.valueOf(d1[i]) < Integer.valueOf(d2[i]))
					return -1;
				else if (Integer.valueOf(d1[i]) > Integer.valueOf(d2[i]))
					return 1;
			}
			return 0;
		} catch (NumberFormatException e) {
			throw new RINException("Wrong date format!");
		}
	}

	public String generateRFSReport(int year, int quarter) {
		try {
			return new CSVCreator().createReport(year, quarter);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RINException(e.getMessage());
		}
	}

	/**
	 * Get version of application from 
	 * manifest file
	 */
	@Override
	public String getVersionDetail() throws Exception {
		
		// TODO Auto-generated method stub
		String filename = new String();
		String versionValue = new String();
		
		String className = this.getClass().getSimpleName();
		
		String classFileName = className + ".class";
		
		String classFilePath = this.getClass().getPackage().toString().replace('.', '/')
		+ "/" + className;
		
		String pathToThisClass = this.getClass().getResource(classFileName).toString();
		String pathToManifest = pathToThisClass.toString().substring(0, 
		pathToThisClass.length() + 2 - ("/" + classFilePath).length())
		+ "/META-INF/MANIFEST.MF";
		Manifest manifest = new Manifest(new URL(pathToManifest).openStream());
		
		final Attributes mattr = manifest.getMainAttributes();
        
            versionValue  = mattr.getValue("Implementation-Version");
            
          return versionValue;
        
	}

	/**
	 * Get list of available rings
	 */
	public RIN[] getAvailableRINs(Invoice sellInvoice) throws RINException,
			AuthorizationException {

		// authorize("sellRINs(Invoice)");

		if (sellInvoice == null)
			throw new RINException("invoice cannot be null");

		boolean finOnly = sellInvoice.getMode().getMode().equalsIgnoreCase(
				"FIN");

		RINFactory rinFactory = new RINFactory();
		EntityManager em = emf.createEntityManager();

		ArrayList<RIN> rins = new ArrayList<RIN>();
		rins = (ArrayList<RIN>) rinFactory.getRINsAvailableForSale(finOnly, em);

		em.close();

		return (RIN[]) rins.toArray(new RIN[rins.size()]);

	}

	/**
	 * Change sorting positions for available rings
	 */
	public void saveAvailableRINsPositions(List<Integer> rinIds,
			List<Integer> rinPositions) throws RINException,
			AuthorizationException {

		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		int i = 0;
		for (Integer ringID : rinIds) {
			RIN rinFromServer = em.find(RIN.class, ringID);
			rinFromServer.setSaleIndex(rinPositions.get(i));
			em.persist(rinFromServer);
			i++;
		}

		tx.commit();

		em.close();

	}
}

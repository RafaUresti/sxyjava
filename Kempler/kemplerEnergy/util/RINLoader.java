package com.kemplerEnergy.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.csvreader.CsvWriter;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.model.ShipMode;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINs;
import com.kemplerEnergy.persistence.HibernateUtil;

public class RINLoader {

	private static final int COL = 8;
	private CsvWriter writer;


	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	private static EntityManager em = emf.createEntityManager();
	private Hashtable<String, RIN> rinsTable = new Hashtable<String, RIN>(1500);

	public static void main(String[] args) throws RINException {
		new RINLoader().run(args);
	}

	private void run(String[] args) {
		RIN rin = null;
		List<RIN> rins = new ArrayList<RIN>();
		CSVFile file;

		Invoice invoice = null;
		
		RINParser parser;

		String[] data;
		String[] rinNumber;
		
		ShipMode mode;

		boolean isCorrupted = false;
		
		if (args == null) {
			System.out.println("usage: you have to supply filename.");
			System.exit(1);
		}

		EntityTransaction tx = em.getTransaction();

		
		try {
			file = new CSVFile(args[0]);
			int rinIdx = file.getIdxOf("RIN");

			if (file.getHeaderCount() != COL) {
				System.err
						.println("Header's number isn't correct, input file has "
								+ file.getHeaderCount()
								+ "Heades. It suppose to have "
								+ COL
								+ "headers.");
				System.exit(1);
			}
			RINcsv csvFile = new RINcsv(file.getHeaders());
			writer = new CsvWriter("c:\\unprocessed.csv");

			writer.writeRecord(file.getHeaders());
			tx.begin();

			while (file.readData()) {
				// read data from csv file into a string array
				data = file.getData();
				csvFile.setValue(data);

				if (file.getColumnCount() == 0)
					continue;
				
				if (file.getColumnCount() > 1) // new invoice
				{ 
//					System.out.println(count++);
					if (isCorrupted == true)
						isCorrupted = false;
					
					if (invoice != null) {
						save(invoice, rins);
					}
					invoice = new Invoice();

					// set the shipmode
					String shipName = csvFile.getMode();
					try {
						mode = (ShipMode) em.createQuery(
								"select s from ShipMode s where s.mode = \'"
										+ shipName + "\'").getSingleResult();
					} catch (NoResultException n) {
						writer.writeRecord(data);
						isCorrupted = true;
						System.out.println(shipName);
						continue;
					}
					
					invoice.setMode(mode);
					
					// set counter Party
					EPAPartner company;
					try {
						Query query = em.createQuery("select c from EPAPartner c where c.fullName = :name");
						query.setParameter("name", csvFile.getCounterParty());
						company = (EPAPartner) query.getSingleResult();
						
					} catch (NoResultException e) {
						try 
						{
							Query query = em.createQuery("select c from EPAPartner c where c.name = :name");
							query.setParameter("name", csvFile.getCounterParty());
							company = (EPAPartner) query.getSingleResult();
						} 
						catch (NoResultException e1) 
						{
							writer.writeRecord(data);
							isCorrupted = true;
							System.out.println("Can't find EPA partner: " + csvFile.getCounterParty() + "in database");
							continue;	
						}
					}
					invoice.setEpaPartner(company);
					
					invoice.setExpectedGallons(csvFile.getQuantity());
					invoice.setInvoiceNo(csvFile.getInvoiceNo());
					
					invoice.setTransferDate(csvFile.getTradeDate());
					invoice.setInvoiceDate(csvFile.getTradeDate());

					if (csvFile.getTradeType().equalsIgnoreCase("PURCHASE")) {
						invoice.setInvoiceType("PURCHASE");
						invoice.setStatus("ACCEPTED");
					}
					else {
						invoice.setInvoiceType("SALE");
						invoice.setStatus("SOLD");
					}
				} // end of new invoice
				
				if (isCorrupted) {
					writer.writeRecord(data);
					continue;
				}
				
				parser = new RINParser(data[rinIdx]);
				rinNumber = parser.getRINComponents();
				rin = new RIN();
				
				rin.setRIN(rinNumber);
				rin.setOriginalRIN(rin);
				rin.getUniRIN();
				rins.add(rin);

			}
			
			if (rins.size() > 0)
				save(invoice, rins);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			tx.commit();
			em.close();
			writer.close();
		}
	}

	private void save(Invoice invoice, List<RIN> rins) {
		List<RIN> combinedRINs;
		if (invoice.getInvoiceType().equalsIgnoreCase("PURCHASE"))
			combinedRINs = RINs.combine(rins);
		else
			combinedRINs = rins;

		for (RIN r : combinedRINs) {
			if (rinsTable.containsKey(r.toString())) {
				RIN rin = rinsTable.get(r.toString());
				
				if (invoice.getInvoiceType().equalsIgnoreCase("PURCHASE")) {
					if (rin.getRinStatus().equalsIgnoreCase("AVAILABLE")) {
						System.out.println("Warning: duplicated purchased RIN");
						System.out.println(rin);
					}
				}
				else if (rin.getRinStatus().equalsIgnoreCase("SOLD")) {
						System.out.println("Warning: duplicated Sale RIN");
						System.out.println(rin);
				} else {
					rin.setRinStatus("SOLD");
				}
			
				invoice.addRIN(rin);
			} else {

				if (invoice.getInvoiceType().equalsIgnoreCase("PURCHASE"))
					r.setRinStatus("AVAILABLE");
				else
					r.setRinStatus("SOLD");
				
				invoice.addRIN(r);

				rinsTable.put(r.toString(), r);
			}
		}

		invoice.validate();
		em.persist(invoice);

		rins.clear();
	}

	
}

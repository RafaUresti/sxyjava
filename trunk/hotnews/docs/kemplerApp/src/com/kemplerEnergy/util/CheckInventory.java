package com.kemplerEnergy.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import com.csvreader.CsvWriter;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINs;
import com.kemplerEnergy.persistence.HibernateUtil;

public class CheckInventory {
	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	private static EntityManager em = emf.createEntityManager();
	
	public static void main(String[] args) {
		verifySplit();
		autoSplit();
		try {
			checkInvoiceGallon();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Figure out the relationship between split children and their parent
	 */
	@SuppressWarnings("unchecked")
	private static void verifySplit() {
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		List<RIN> inventory;
		ArrayList<ArrayList<RIN>> groups;
		try {
			inventory = (ArrayList<RIN>)em.createQuery("SELECT r FROM RIN r").getResultList();
			if (inventory != null && inventory.size() > 1) {
				groups = RINs.groupRINs(inventory, false);
				
				if (groups != null && groups.size() > 0) {
					for (ArrayList<RIN> rins: groups) {
						Set<RIN> rinset = RINs.checkOverlap(rins);
						
						if (rinset != null && rinset.size() > 1) {
							ArrayList<RIN> overlappedRINs = new ArrayList<RIN>();

							for (RIN r : rinset) {
								overlappedRINs.add(r);
							}

							RINs.checkSplit(overlappedRINs);
						}
					}
				}
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.close();
		}
		
	}
	
	/**
	 * Sometimes, client didn't split purchase RIN, i.e.
	 * Bought RIN 001-100
	 * Sale RIN 001-050
	 * System will help to generate this missing 51-100 RIN and 
	 * put it into the inventory
	 */
	@SuppressWarnings("unchecked")
	private static void autoSplit() {
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		List<RIN> inventory;
		ArrayList<ArrayList<RIN>> groups;
		
		int count = 0;
		try {
			
			inventory = (ArrayList<RIN>)em.createQuery("SELECT r FROM RIN r").getResultList();
			groups = RINs.groupRINs(inventory, false);

			for (ArrayList<RIN> groupRINs : groups) {
				count++;
				for (RIN rin: groupRINs) 
				{
					if (rin.getRinStatus().equalsIgnoreCase("SPLIT")) {
//						System.out.println("RIN: " + count + " " + rin);
						ArrayList<RIN> newGroup = new ArrayList<RIN>();
						newGroup.addAll(groupRINs);
						newGroup.remove(rin);
						split(rin, newGroup);
					}
				}
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.close();
		}
		
	}
	
	private static void split(RIN rin, ArrayList<RIN> groupRINs) {
		int start = RINs.toInt(rin.getStartGallon()); 
		int end = RINs.toInt(rin.getEndGallon());
		int currentStart, currentEnd;
		
		ArrayList<int[]> covers = new ArrayList<int[]>();
		ArrayList<int[]> gaps = new ArrayList<int[]>();
		
		RINs.sortByGallon(groupRINs);
			
		for (int i = 0; i < groupRINs.size(); i++) 
		{
			
			currentStart = RINs.toInt(groupRINs.get(i).getStartGallon());
			currentEnd = RINs.toInt(groupRINs.get(i).getEndGallon());
			
			if (currentEnd < start)
				continue;
			if (currentStart > end)
				break;
			
			if (currentStart <= start) 
				currentStart = start;
			if (currentEnd >= end)
				currentEnd = end;
			
			int[] pair = new int[2];
			pair[0] = currentStart;
			pair[1] = currentEnd;
			covers.add(pair);
		}
		
		int i;
		for (i = 0; i < covers.size(); i++) {
			if (i == 0)
				currentStart = start;
			else currentStart = covers.get(i - 1)[1] + 1;
			
			currentEnd = covers.get(i)[0] - 1;
			
			if (currentEnd >= currentStart) {
				int[] pair = new int[2];
				pair[0] = currentStart;
				pair[1] = currentEnd;
				gaps.add(pair);
			}
		}
		currentStart = covers.get(i - 1)[1] + 1;
		currentEnd = end;
		if (currentEnd >= currentStart) {
			int[] pair = new int[2];
			pair[0] = currentStart;
			pair[1] = currentEnd;
			gaps.add(pair);
		}
		
		for (int[] gap: gaps) {
			RIN newRIN = new RIN(rin);
			newRIN.setStartGallon(RINs.toCharArray(gap[0]));
			newRIN.setEndGallon(RINs.toCharArray(gap[1]));
			newRIN.setRinStatus("AVAILABLE");
			save(newRIN);
		}
	}

	private static void save(RIN newRIN) {
		
		System.out.println(newRIN);
		try {
			em.persist(newRIN);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@SuppressWarnings("unchecked")
	private static void checkInvoiceGallon() throws IOException {
		
		String filePath = "c:\\invoice.csv";
		CsvWriter writer = new CsvWriter(filePath);
		
		// header
		writer.write("DATE");
		writer.write("Counter Party");
		writer.write("Expected Gallon Amount (in data sheet)");
		writer.write("Actual Gallon Amount");
		writer.write("Invoice No");
		writer.write("Type");
		writer.endRecord();

		EntityTransaction tx = em.getTransaction();
		
		List<Invoice> invoices = new ArrayList<Invoice>();

		try {
			tx.begin();
			invoices = em.createQuery("SELECT i FROM Invoice i").getResultList();
			tx.commit();
		} catch (NoResultException e) {
			System.out.println("Empty inventory");
		} finally {
			em.close();
		}
		for (Invoice i: invoices) {
			if (i.calculateActualGallons() != i.getExpectedGallons()) {
				
				writer.write(i.getInvoiceDate());
				writer.write(i.getEpaPartner().getFullName());
				writer.write(i.getExpectedGallons() + "");
				writer.write(i.calculateActualGallons() + "");
				writer.write(i.getInvoiceNo());
				writer.write(i.getInvoiceType());
				writer.endRecord();

			}
		}
		writer.close();
	}
}

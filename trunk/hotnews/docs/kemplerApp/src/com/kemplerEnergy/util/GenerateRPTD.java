package com.kemplerEnergy.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;


import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.kemplerEnergy.server.rins.CSVCreator;
import com.kemplerEnergy.server.rins.RPTDCreator;

public class GenerateRPTD {

	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	private static EntityManager em = emf.createEntityManager();
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		create();
	}

	@SuppressWarnings("unchecked")
	static void create() {
		RPTDCreator rptdCreator = new RPTDCreator();
		CSVCreator csvCreator = new CSVCreator();
		EntityTransaction tx = em.getTransaction();
		
		List<Invoice> invoices = new ArrayList<Invoice>();

		try {
			tx.begin();
			invoices = em.createQuery("SELECT i FROM Invoice i").getResultList();
			for (Invoice i: invoices) {
				i.validate();
				rptdCreator.makeRPTD(i);
				csvCreator.makeCSV(i);
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
	
	private static void create(int id) {
		RPTDCreator rptdCreator = new RPTDCreator();
		CSVCreator csvCreator = new CSVCreator();
		
		Invoice invoice;

		try {
			Query query = em.createQuery("SELECT i FROM Invoice i WHERE i.id = :id");
			query.setParameter("id", id);
			invoice = (Invoice) query.getSingleResult();
			rptdCreator.makeRPTD(invoice);
			csvCreator.makeCSV(invoice);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
}

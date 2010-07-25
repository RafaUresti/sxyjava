package com.kemplerEnergy.util;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;



import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.persistence.HibernateUtil;


public class MakeAvailable {

	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	private static EntityManager em = emf.createEntityManager();

	public static void main(String[] args) throws RINException {
		
		printInvoices(25528);
//		deleteInvoiceRecord(11274);
//		deleteInvoiceRecord(11275);
//		deleteInvoiceRecord(11276);
//		
		//makeInvoiceRinsAvailable(11261);
			
	}

	public static void printInvoices(int invoiceNumber) {

		List<Invoice> invoices = (List<Invoice>) em.createQuery(
				"select i from Invoice i where i.invoiceNo=" + invoiceNumber)
				.getResultList();

		for (Invoice invoice : invoices) {
			

				
				System.out.println("ID:" + invoice.getId() + " Date: "
						+ invoice.getInvoiceDate() + " Number: "
						+ invoice.getInvoiceNo() + " Path: "
						+ invoice.getInvoicePath() + " Status: "
						+ invoice.getVersionNo());

				for (RIN rin : invoice.getRins()) {
					System.out.println("   ID: " + rin.getId() + " " + rin.toString() + " Status: "
							+ rin.getRinStatus() + " RetiredCode: "
							+ rin.getRetiredCode() + " RetiredDate: "
							+ rin.getRetiredDate());

					

				}

				
			
		}

	}

	public static void deleteInvoiceRecord(int id)
	{
		System.out.println("Delete invoice: " + id);
		EntityTransaction tr = (EntityTransaction) em.getTransaction();
		tr.begin();
		Invoice invoice = (Invoice)em.find(Invoice.class, id);
		assert(invoice != null);
		em.remove(invoice);
		tr.commit();
	}
	
	public static void makeInvoiceRinsAvailable(int invoiceID) {
		
		System.out.println("Make available rins invoice: " + invoiceID);
		
		List<Invoice> invoices = (List<Invoice>) em.createQuery(
				"select i from Invoice i where i.id=" + invoiceID)
				.getResultList();

		for (Invoice invoice : invoices) {
			

				EntityTransaction tr = (EntityTransaction) em.getTransaction();
				tr.begin();

				System.out.println("ID:" + invoice.getId() + " Date: "
						+ invoice.getInvoiceDate() + " Number: "
						+ invoice.getInvoiceNo() + " Path: "
						+ invoice.getInvoicePath() + " Status: "
						+ invoice.getVersionNo());

				for (RIN rin : invoice.getRins()) {
					System.out.println("   " + rin.toString() + " Status: "
							+ rin.getRinStatus() + " RetiredCode: "
							+ rin.getRetiredCode() + " RetiredDate: "
							+ rin.getRetiredDate());

					rin.setRetiredCode(null);
					rin.setRetiredDate(null);
					rin.setRinStatus("AVAILABLE");
					em.persist(rin);

				}				

				tr.commit();
			
		}

	}
}

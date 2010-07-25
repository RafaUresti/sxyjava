package com.kemplerEnergy.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.kemplerEnergy.server.rins.RINFactory;

import junit.framework.TestCase;

public class SendMailTest extends TestCase {

	public void testSendMailAboutInvoice() {

		EntityManagerFactory emf = HibernateUtil.getEmf();
		EntityManager em = emf.createEntityManager();

		RINFactory factory = new RINFactory();
		Invoice invoice = em.find(Invoice.class, 11319);
		assertNotNull(invoice);
		factory.sendSaleMail(invoice);

		em.close();
	}

}

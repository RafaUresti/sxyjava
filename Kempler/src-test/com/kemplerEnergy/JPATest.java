package com.kemplerEnergy;

import java.util.ArrayList;
import java.util.List;

import com.kemplerEnergy.model.*;
import com.kemplerEnergy.model.rins.*;

import javax.persistence.*;

public class JPATest {

    @SuppressWarnings("unchecked")
	public static void main(String[] args) {

        // Start EntityManagerFactory
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("kempler");

		EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();	
        ArrayList<Invoice> invoices = null;
		try {
			invoices = (ArrayList<Invoice>)em.createQuery("select i from Invoice i where i.id = ").getSingleResult();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			tx.commit();
			em.close();
			e.printStackTrace();
			throw e;
		}
        tx.commit();
		em.close();
		
		for (Invoice i: invoices) {
			System.out.println(i.getStatus());
			System.out.println(i.getRins().size());
			System.out.println(i.getEpaPartner().getName());
			System.out.println(i.getMode().getMode());
		}
		
    }
}

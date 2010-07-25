package com.kemplerEnergy.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import com.kemplerEnergy.model.BaseObject;

public class PersistentObj {
	
	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	
	public static void save(BaseObject o) {

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		o.setDefault();
		em.persist(o);
		tx.commit();
		em.close();
		emf.close();
	}

	public static void save(Object o) {
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(o);
		tx.commit();
		em.close();
		emf.close();
	}
}

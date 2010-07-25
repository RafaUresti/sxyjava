package com.kemplerEnergy;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

import com.kemplerEnergy.model.admin.Group;
import com.kemplerEnergy.model.admin.Method;
import com.kemplerEnergy.persistence.HibernateUtil;

public class GroupTest {
	
	private static EntityManagerFactory emf;

	private Group group;
	
	@Before
	public void beforeClass() {
		emf = HibernateUtil.getEmf();
	}
	@Test

	public void testGetUsableMethods() {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			Query query = em.createQuery("SELECT g FROM Group g WHERE g.id = :id");
			query.setParameter("id", 1);
			group = (Group)query.getSingleResult();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		tx.commit();
		System.out.println(group.getName() + " has following access:");
		for (Method m: group.getUsableMethods())
			System.out.println(m.getName());
	}

}

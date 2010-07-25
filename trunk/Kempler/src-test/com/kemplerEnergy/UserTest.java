package com.kemplerEnergy;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

import com.kemplerEnergy.model.admin.Group;
import com.kemplerEnergy.model.admin.User;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.kemplerEnergy.util.BCrypt;

public class UserTest {

	private static EntityManagerFactory emf;

	@Before
	public void beforeClass() {
		emf = HibernateUtil.getEmf();
	}
	
	@Test
	public void testSetPassword() {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		Group group;
		User user;
		tx.begin();
		try {
			Query query = em.createQuery("SELECT g FROM Group g WHERE g.id = :id");
			query.setParameter("id", 1);
			group = (Group)query.getSingleResult();
			
			user = new User();
			user.setUserName("xyz");
			String hashedPWD = BCrypt.hashpw("xyz", BCrypt.gensalt());
			user.setPassword(hashedPWD);
			user.getGroups().add(group);
			
			em.persist(user);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		tx.commit();
	}

}

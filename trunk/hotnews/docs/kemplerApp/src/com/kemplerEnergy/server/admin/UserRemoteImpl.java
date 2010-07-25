package com.kemplerEnergy.server.admin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import net.sf.hibernate4gwt.gwt.HibernateRemoteService;

import com.kemplerEnergy.client.admin.UserRemote;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.admin.Group;
import com.kemplerEnergy.model.admin.User;
import com.kemplerEnergy.persistence.HibernateUtil;

public class UserRemoteImpl extends HibernateRemoteService implements UserRemote {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6789419789726606373L;
	private static final EntityManagerFactory emf = HibernateUtil.getEmf();

	
	@SuppressWarnings("unchecked")
	public ArrayList<Group> getGroupList() {
		List<Group> groups = new ArrayList<Group>();
		EntityManager em = emf.createEntityManager();
		try {
			groups = em.createQuery("SELECT g FROM Group g").getResultList();
		} catch (RuntimeException e) {
			throw new RINException(e.getMessage());
		}
		return (ArrayList<Group>) groups;
	}

	public void saveUser(ArrayList<User> users) {
		System.out.println("Saving...");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			for (User u: users) {
				User user = u;
				if (!em.contains(u)) 
					user = (User)em.merge(u);
				else em.persist(user);
			}
			tx.commit();
		} catch (RuntimeException e) {
			throw new RINException(e.getMessage());
		} finally {
			em.close();
		}
	}
}

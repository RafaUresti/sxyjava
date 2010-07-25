package com.kemplerEnergy.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import net.sf.hibernate4gwt.core.HibernateBeanManager;

import org.hibernate.*;

/**
 * Startup Hibernate and provide access to the singleton SessionFactory
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static EntityManagerFactory emf;

	static {
		try {
			// sessionFactory = new
			// Configuration().configure().buildSessionFactory();
			emf = Persistence.createEntityManagerFactory("kempler");
			HibernateBeanManager.getInstance().setEntityManagerFactory(emf);

		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		// Alternatively, we could look up in JNDI here
		return sessionFactory;
	}

	public static EntityManagerFactory getEmf() {
		return emf;
	}
	public static void shutdown() {
		// Close caches and connection pools
		if (getSessionFactory() != null)
			getSessionFactory().close();
		if (getEmf() != null)
			getEmf().close();
	}
	
	
	protected void finalize() {
		shutdown();
	}
}

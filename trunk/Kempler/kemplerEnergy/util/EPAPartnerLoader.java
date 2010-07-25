package com.kemplerEnergy.util;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.NonUniqueObjectException;

import com.kemplerEnergy.model.Address;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.persistence.HibernateUtil;

public class EPAPartnerLoader {

	private static final int COL = 7;

	public static void main(String[] args) {

		if (args == null) {
			System.out.println("usage: you have to supply filename.");
			System.exit(1);
		}

		CSVFile file;
		CSVdata data;

		// data object
		EPAPartner company;
		Address address;

		EntityManagerFactory emf = HibernateUtil.getEmf();
		EntityManager em = emf.createEntityManager();

		try {
			file = new CSVFile(args[0]);
			String[] header = file.getHeaders();
			data = new CSVdata(header);

			if (file.getHeaderCount() != COL)
				System.err
						.println("Header's number isn't correct, input file has "
								+ file.getHeaderCount()
								+ "Heades. It suppose to have "
								+ COL
								+ "headers.");

			while (file.readData()) {

				// read data from csv file in a string array
				String[] row = file.getData();
				data.setData(row);

				EntityTransaction tx = em.getTransaction();
				tx.begin();
				
				String epaNo = data.getValue("EPA#");
				
				Query query;
				
				try {
					query = em.createQuery("SELECT e FROM EPAPartner e WHERE e.epaNo = :name");
					query.setParameter("name", epaNo.toCharArray());
					company = (EPAPartner)query.getSingleResult();
				} catch (NoResultException e) {
					tx.commit();
					continue;
				} catch (NonUniqueResultException e) {
					tx.commit();
					continue;
				} 

				
				company.setPhone(data.getValue("PHONE"));
				company.setContact(data.getValue("CONTACT"));
				company.setEmail(data.getValue("EMAIL"));
				tx.commit();

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		em.close();
	}
}


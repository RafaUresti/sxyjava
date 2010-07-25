package com.kemplerEnergy.util;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.kemplerEnergy.model.Address;
import com.kemplerEnergy.model.EPAPartner;

public class CounterPartyLoader {
	private static final int COL = 11;
	
	public static void main(String[] args) {

		if (args == null) {
			// Start EntityManagerFactory

			System.out.println("usage: you have to supply filename.");
			System.exit(1);
		}

		CSVFile file;
		CSVdata data;

		// data object
		EPAPartner company;
		Address address;

		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("kempler");
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

				address = new Address();
				address.setStreetLine1(data.getValue("ADDRESS_LINE_1"));
				address.setStreetLine2(data.getValue("ADDRESS_LINE_2"));
				address.setCity(data.getValue("CITY"));
				address.setState(data.getValue("STATE"));
//				address.setFullState(data.getValue("FULLSTATE"));
				address.setCountry(data.getValue("COUNTRY"));
				address.setZipCode(data.getValue("ZIP"));

/*				String name = data.getValue("NAME");
				String epa = data.getValue("EPA");
				
				Query query = em.createQuery("SELECT e FROM EPAPartner e WHERE e.name = :name");
				query.setParameter("name", name);
				if (query.getResultList().size() != 1) {
					System.out.println(name + "has more than one or no entry");
					tx.commit();
					continue;
				}

				EPAPartner com = (EPAPartner)query.getSingleResult();
				com.setEpaNo(epa.toCharArray());
				
				em.flush(); */
				
				company = new EPAPartner();
				company.setName(data.getValue("NAME"));
				company.setFullName(data.getValue("FULL_NAME"));
				company.setType("EPA_PARTNER");
				String epa = data.getValue("EPAID");
				if (epa == null || epa.isEmpty() || epa.equalsIgnoreCase("n/a")) {
					tx.commit();
					continue;
				}
				company.setEpaNo(epa.toCharArray());
				company.addAddress(address);
				em.persist(company);
				
				System.out.println(data.getValue("EPAID").toCharArray());
				System.out.println(data.getValue("NAME"));
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

package com.kemplerEnergy.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.kemplerEnergy.server.rins.RINFactory;

public class JPAQueries {
	
	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	private static EntityManager em = emf.createEntityManager();

	public static void main(String[] args) {

		try {
			
			List<RIN> usableRINs = (ArrayList<RIN>)em.createQuery(
					"SELECT r FROM RIN r WHERE  r.rinStatus = \'AVAILABLE\' "
					+ "ORDER BY r.rinType, r.originalRIN.id")
					.getResultList();	
									
			//RINFactory.sortUsableRingsByPurchaseDateAndUNIRIN(usableRINs, em);
			
			for(RIN tmpRin : usableRINs)
			{
				String date = ((Invoice)tmpRin.getInvoices().toArray()[0]).getInvoiceDate();
				
				echo("[" + tmpRin.getId() + "]\t[" + tmpRin.getUniRIN() + "]\t"  
						+ new String(tmpRin.getStartGallon()) + "-" + new String(tmpRin.getEndGallon()) 
						+ "\t" + tmpRin.getGallonAmount() + "\t[" + date + "]" );
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			em.close();
		}
	}
	
	private static void echo(String str)
	{
		System.out.println(str);
	}
	
}

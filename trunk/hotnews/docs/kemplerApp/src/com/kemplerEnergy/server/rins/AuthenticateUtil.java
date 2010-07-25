package com.kemplerEnergy.server.rins;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;


import com.kemplerEnergy.model.admin.Group;
import com.kemplerEnergy.model.admin.User;
import com.kemplerEnergy.persistence.HibernateUtil;
import com.kemplerEnergy.util.BCrypt;

public class AuthenticateUtil {

	private static EntityManagerFactory emf; 

	public AuthenticateUtil(){
		emf = HibernateUtil.getEmf();
	}
	
	
	public boolean check(String userName, String pwd){
		
		String pwdDB;
		Query query;
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try{
			
			String sql = "select USER_PASSWORD from ADMIN_USER where USER_NAME = " +
			"\'" + userName + "\'";
			query = em.createNativeQuery(sql);
			query.setMaxResults(1);
			pwdDB = (String)query.getSingleResult();
			tx.commit();
			return BCrypt.checkpw(pwd, pwdDB);// not good for B/S mode
		} catch (NoResultException e) {
			return false;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		} 
	}
	
	public String getGroup(String userName) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Query query;
		try{
			
			query = em.createQuery("SELECT u FROM User u WHERE u.userName = :name");
			query.setParameter("name", userName);
			query.setMaxResults(1);
			
			User user = (User)query.getSingleResult();
			tx.commit();
			
			for (Group g: user.getGroups()) {
				if (g.getName().equalsIgnoreCase("Logistics"))
					return "Logistics";
				if (g.getName().equalsIgnoreCase("Accountant"))
					return "Accountant";
				if (g.getName().equalsIgnoreCase("Administrator"))
					return "Administrator";
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;

	}
/*	public boolean register(String userName, String pwd, String type, String nt){
		String query;
		String hashedPWD = BCrypt.hashpw(pwd, BCrypt.gensalt());
		try{
			this.AuthenticateUtilConnec();
			query = "INSERT INTO user_pwd (USER_NAME, PWD, TYP, NOTE)" +
					"VALUES (\'" + userName + "\',\'" + hashedPWD + "\',\'" + type +
					"\','" + nt + "\');";
			System.out.print(query);
			boolean result = statement.execute(query);
			return result;
		}
		catch (Exception e){
			System.err.println(e.toString());
		}
		return false;
	}
	
	public boolean register(String userName, String pwd, String type){
		return register(userName, pwd, type, "");
	}*/
	
	public static void main(String [] args){
		AuthenticateUtil au = new AuthenticateUtil();
//		au.register("haodang2", "pwd", "t");
//		System.out.print(au.check("haodang2","pwd"));
		System.out.print("done");
	}
}
package com.cis550.util;

import java.io.*;
import java.net.*;
import java.sql.*;

import java.util.*;

import javax.servlet.ServletException;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class Indexer extends Thread{

	private HashMap<String, Integer> dictionary; /*For query*/

	private ArrayList<String> wordList; /*Store keys*/

	private ArrayList<ArrayList<Integer>> idList; /*Tuple list*/

	private int hashtableIndex = 0;

	private boolean started = false;

	private static Indexer search;


	private Indexer() {
		dictionary = new HashMap<String, Integer>();
		wordList = new ArrayList<String>();
		idList = new ArrayList<ArrayList<Integer>>();
		hashtableIndex = 0;
	}

	public static Indexer getSearch(){
		if (search == null){
			search = new Indexer();
		}
		return search;
	}

	/*
	 * index: index of the tuple
	 *
	 */
	private void generateInvertedIndexTable(int index, String word) {
		if (!dictionary.containsKey(word)) { // there isn't whis word.

			//add this word to the dictionary
			dictionary.put(word, new Integer(hashtableIndex++));

			//add the index to the coorsponding apartList, create it first
			ArrayList list = new ArrayList<Integer>();
			list.add(list.size(), new Integer(index));
			idList.add(idList.size(), list);

			//also, add the word in the
			wordList.add(wordList.size(), word);
		} else { //find the word in the hashtable
			int indexInHashTable = dictionary.get(word).intValue();
			(idList.get(indexInHashTable)).add((idList.get(indexInHashTable)).size(), new Integer(index));
		}
	}

	/*
	 * Add the filtered words here
	 */
	private boolean filter(String word) {
		if (word.equals("an")) {
			return true;
		}
		if (word.equals("only")) {
			return true;
		}
		if (word.equals("and")) {
			return true;
		}
		if (word.equals("a")) {
			return true;
		}
		if (word.equals("by")) {
			return true;
		}
		if (word.equals("the")) {
			return true;
		}
		if (word.equals("in")) {
			return true;
		}
		return false;
	}

	private String formulateWord(String word) {
		word = word.trim();
		return word;
	}

	public Set<String> removeDuplicate(ArrayList<String> list) {
		HashMap<String, Integer> hm = new HashMap();
		int j = 0;
		for (int i = 0; i < list.size(); i++) {
			if (!hm.containsKey(list.get(i))) {
				hm.put(list.get(i), new Integer(j));
			}
		}

		//System.out.println(hm.keySet());
		return hm.keySet();
	}

	private String removeSymbols(String string) {
		string = string.toLowerCase();
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) > 'z' || string.charAt(i) < 'a') {
				string = string.replace(string.charAt(i), ' ');
			}
		}
		//System.out.println(string);
		return string;
	}



	public void statistic() throws ServletException, IOException, ClassNotFoundException {
		SQLConn sqlConn = null;
		try {        	
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn = new SQLConn();
			Connection conn = sqlConn.getConnection();

			Statement stmt = conn.createStatement();

			// execute select query
			String query = "SELECT siteId, content, title FROM Sites";
			ResultSet table = stmt.executeQuery(query);

			while (table.next()) {
				int index = table.getInt(1);
				String content1 = table.getString(2);
				String content2 = table.getString(3);                

				//System.out.println(content1);

				content1 = removeSymbols(content1);
				content2 = removeSymbols(content2);
				StringTokenizer st1 = new StringTokenizer(content1);
				StringTokenizer st2 = new StringTokenizer(content2);

				ArrayList<String> temList = new ArrayList<String>();
				while (st1.hasMoreTokens()) {
					String temWord = st1.nextToken();
					if (filter(temWord)) {
						continue;
					}
					temWord = formulateWord(temWord);
					if (temWord.length() < 2) {
						continue;
					}
					temList.add(temList.size(), temWord);
				}

				while (st2.hasMoreTokens()) {
					String temWord = st2.nextToken();
					if (filter(temWord)) {
						continue;
					}
					temWord = formulateWord(temWord);
					if (temWord.length() < 2) {
						continue;
					}
					temList.add(temList.size(), temWord);
				}


				Set<String> newList = removeDuplicate(temList);

				Iterator<String> iterator = newList.iterator();

				while (iterator.hasNext()) {
					//System.out.println(iterator.next());
					generateInvertedIndexTable(index, iterator.next());
				}

				//System.out.println(table.getInt(1));
			}

			//insert them to 'search' table
			PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO IIT(siteId,keywords) VALUES(?,?)");

			for (int i = 0; i < idList.size(); i++) {
				//System.out.print(wordList.get(i)+": ");
				for (int j = 0; j < idList.get(i).size(); j++) {
					//System.out.print(idList.get(i).get(j)+" ");
					stmt1.setInt(1, (idList.get(i).get(j)).intValue());
					//System.out.println(idList.get(i).get(j).intValue());
					stmt1.setString(2, wordList.get(i));   
					//System.out.println(idList.get(i));
					try{
						stmt1.executeUpdate();
					}
					catch(MySQLIntegrityConstraintViolationException e){

					}

				}
				//System.out.println();
			}

			stmt.close();
			conn.close();

		}catch (SQLException e) {
			e.printStackTrace();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		} finally{
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void query(String query){

		// TODO Auto-generated method stub


		StringTokenizer st = new StringTokenizer(query);
		ArrayList<String> tempList = new ArrayList<String>();

		while (st.hasMoreTokens()) {             
			//System.out.println(temWord);
			tempList.add(tempList.size(), st.nextToken());
		}

		Iterator<String> iterator = tempList.iterator();         

		ResultSet resultSet;		
		String result;

		SQLConn sqlConn = null;

		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			sqlConn = new SQLConn();
			Connection conn = sqlConn.getConnection();

			PreparedStatement stmt = conn.prepareStatement("SELECT url, content " +
			"FROM Sites S, IIT I WHERE S.siteId=I.siteId AND I.keywords = ? ");

			while(iterator.hasNext()){
				stmt.setString(1, iterator.next());
				resultSet = stmt.executeQuery();
				while (resultSet.next()){
					System.out.println(resultSet.getString(1));
				}

			} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}


	}

	public void run() {
		Indexer search = new Indexer();
		if (started == false){
			started = true;
		} else {
			return;
		}
		while (true){
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				search.statistic();
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
		//search.query("dsafdsaf");
		//System.out.println("abbb");
	}
	public boolean isStarted() {
		return started;
	}
}

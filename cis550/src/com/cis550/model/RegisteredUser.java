package com.cis550.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisteredUser {

	private String userName;
	private String name;
	private int birthYear;
	private String city;
	private String state;
	private String profession;

	public RegisteredUser(String userName, String name, int birthYear, String city, 
			String state, String profession){
		this.userName = userName;
		this.name = name;
		this.birthYear = birthYear;
		this.city = city;
		this.state = state;
		this.profession = profession;
	}

	public static ArrayList<RegisteredUser> buildUsers(ResultSet results){
		ArrayList<RegisteredUser> users = new ArrayList<RegisteredUser>();
		try {
			while(results.next()){
				String username = results.getString("username");
				String name = results.getString("name");
				int birthYear = results.getInt("birthyear");
				String city = results.getString("city");
				String state = results.getString("state");
				String profession = results.getString("profession");
				users.add(new RegisteredUser(username, name, birthYear, city, state, profession));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	public String generateHTML(){
		Calendar toDay = Calendar.getInstance();
		int year = toDay.get(Calendar.YEAR);
		year-=this.birthYear;
		String str="<br/><br/><br/>"+
		"<table><tr>"+
		"<td>User Name:</td>"+
		"<td>"+this.userName+"<td/>"+
		"</tr><tr>"+
		"<td>Name:</td>"+
		"<td>"+ this.name +"</td>"+
		"</tr> <tr>"+
		"<td>Age:</td>"+
		"<td>"+year+"</td>"+
		"</tr><tr>"+
		"<td>Location:</td>"+
		"<td>"+this.city+"</td>"+
		"</tr><tr>"+
		"<td>Profession:</td>"+
		"<td>"+this.profession+"</td>"+
		"</tr></table>";
		
		return str;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
}

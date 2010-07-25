package com.kemplerEnergy.model.admin;


import java.util.HashSet;
import java.util.Set;

import com.kemplerEnergy.model.BaseObject;

public class User extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3593385751201500746L;
	
	private String userName;
	private String password;

	private String phone;
	private String email;
	private String IM;
	private String lastName;
	private String firstName;
	
	private Set<Group> groups;

	public User() {
		groups = new HashSet<Group>();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIM() {
		return IM;
	}

	public void setIM(String im) {
		IM = im;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@Override
	public void setDefault() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Equality function
	 */
	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		} else if (this == obj) {
			return true;
		}
		
		// ID comparison
		User other = (User) obj;
		return (id == other.getId());
	}
}

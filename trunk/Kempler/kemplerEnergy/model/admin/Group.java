package com.kemplerEnergy.model.admin;

import java.util.HashSet;
import java.util.Set;

import com.kemplerEnergy.model.BaseObject;


public class Group extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4164921944337589399L;
	private String name;
	private Set<Method> usableMethods;
	
	public Group() {
		usableMethods = new HashSet<Method>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Method> getUsableMethods() {
		return usableMethods;
	}

	public void setUsableMethods(Set<Method> usableMethods) {
		this.usableMethods = usableMethods;
	}

	@Override
	public void setDefault() {
		// TODO Auto-generated method stub
	}

}

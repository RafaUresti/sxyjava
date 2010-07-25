package com.kemplerEnergy.model.admin;

import com.kemplerEnergy.model.BaseObject;


public class Method extends BaseObject {

	private String name;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6283208002306190688L;

	public Method() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setDefault() {
		// TODO Auto-generated method stub
		
	}

}

package com.kemplerEnergy.model;

import java.io.Serializable;
import net.sf.hibernate4gwt.pojo.java5.LazyPojo;

abstract public class BaseObject extends LazyPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3551961063393959844L;
	
	protected int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public abstract void setDefault();
		
	protected void save() {
		this.setDefault();
//		com.kemplerEnergy.persistence.PersistentObj.save(this);
	}
}


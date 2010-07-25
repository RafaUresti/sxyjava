package com.kemplerEnergy.unfinished;

import java.util.HashSet;
import java.util.Set;

public class Product {
	
	private Long id;
	private String name;
	
	private Commodity commodityType;
	
	
	public Product() {
		// TODO Auto-generated constructor stub
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCommodity(Commodity c) {
		if (c == null) 
			throw new IllegalArgumentException("Null Commodity added to ticket!");
		if (this.commodityType != null) 
			this.commodityType.getProducts().remove(this);
		this.commodityType = c;
		this.commodityType.getProducts().add(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Commodity getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(Commodity commodityType) {
		this.commodityType = commodityType;
	}

}
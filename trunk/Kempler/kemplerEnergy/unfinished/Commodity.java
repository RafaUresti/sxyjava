package com.kemplerEnergy.unfinished;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Commodity implements Serializable {
	
	private Long id;
	private String name;
	private Set<Product> Products = new HashSet<Product>();
	
	public Commodity() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProducts(Set<Product> products) {
		Products = products;
	}

	public Set<Product> getProducts() {
		return Products;
	}
	
	public void addProduct(Product p) {
		if (p == null) 
			throw new IllegalArgumentException("Null product added to commodity!");
		p.setCommodity(this);
	}
}

package com.kemplerEnergy.model;

public class Product extends BaseObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5679766468556537433L;

	private String name;
	private int defaultUnit;
	private boolean physicalProduct;

	private ShipMode defaultShipMode;
	private MarketZone marketZone;
	private Commodity commodityType;
	
	
	public Product() {
		// TODO Auto-generated constructor stub
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getDefaultUnit() {
		return defaultUnit;
	}


	public void setDefaultUnit(int defaultUnit) {
		this.defaultUnit = defaultUnit;
	}


	public boolean isPhysicalProduct() {
		return physicalProduct;
	}


	public void setPhysicalProduct(boolean physicalProduct) {
		this.physicalProduct = physicalProduct;
	}


	public ShipMode getDefaultShipMode() {
		return defaultShipMode;
	}


	public void setDefaultShipMode(ShipMode defaultShipMode) {
		this.defaultShipMode = defaultShipMode;
	}


	public MarketZone getMarketZone() {
		return marketZone;
	}


	public void setMarketZone(MarketZone marketZone) {
		this.marketZone = marketZone;
	}


	public Commodity getCommodityType() {
		return commodityType;
	}


	public void setCommodityType(Commodity commodityType) {
		this.commodityType = commodityType;
	}


	public void setDefault() {
		name = "Fake product";
		defaultUnit = 0;
		physicalProduct = false;
		defaultShipMode = new ShipMode("test for product");
		marketZone = new MarketZone("test for prod");
		commodityType = new Commodity("test for product");
		
	}

	public static void main(String[] args) {
		new Product().save();
	}

}
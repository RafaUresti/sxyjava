package com.kemplerEnergy.unfinished;

import com.kemplerEnergy.model.BaseObject;



public class Contract extends BaseObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7349971292158488583L;
	
	private static final String[] PriceType = { "TO_BE_DETERMINED", "UN_PRICED", "FULLY_PRICED" };
	private static final String[]  TradeType = { "PURCHASE", "SALE"};
	private String tradeType;
	private String tradePeriod;
	private String contractType;
	
	private String pricingType;
	private String futurePrice;
	private double basis;
	private double fullyPrice;
	
	private Product productType;
	private Ticket ticket;
	
	public Contract() {}


	public String getTradeType() {
		return tradeType;
	}


	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}


	public String getTradePeriod() {
		return tradePeriod;
	}


	public void setTradePeriod(String tradePeriod) {
		this.tradePeriod = tradePeriod;
	}


	public String getContractType() {
		return contractType;
	}


	public void setContractType(String contractType) {
		this.contractType = contractType;
	}


	public String getPricingType() {
		return pricingType;
	}


	public void setPricingType(String pricingType) {
		this.pricingType = pricingType;
	}


	public String getFuturePrice() {
		return futurePrice;
	}


	public void setFuturePrice(String futurePrice) {
		this.futurePrice = futurePrice;
	}


	public double getBasis() {
		return basis;
	}


	public void setBasis(double basis) {
		this.basis = basis;
	}


	public double getFullyPrice() {
		return fullyPrice;
	}


	public void setFullyPrice(double fullyPrice) {
		this.fullyPrice = fullyPrice;
	}


	public Product getProductType() {
		return productType;
	}


	public void setProductType(Product productType) {
		this.productType = productType;
	}


	public Ticket getTicket() {
		return ticket;
	}


	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}


	/*	public String getFuturePrice() {
		if (pricingType == null)
			throw new IllegalArgumentException(
					"No pricing type being defined yet!");
		else if (pricingType != PriceType.Un_Priced)
			throw new IllegalArgumentException(
					"Only un_priced pricing has future price!");
		return futurePrice;
	}

	public void setFuturePrice(String futurePrice) {
		if (pricingType == null)
			pricingType = PriceType.Un_Priced;
		else if (pricingType != PriceType.Un_Priced)
			throw new IllegalArgumentException(
					"Only un_priced pricing has future price!");
		this.futurePrice = futurePrice;
	}

	public double getBasis() {
		if (pricingType == null)
			throw new IllegalArgumentException(
					"No pricing type being defined yet!");
		else if (pricingType != PriceType.Un_Priced)
			throw new IllegalArgumentException(
					"Only un_priced pricing has basis!");
		return basis;
	}

	public void setBasis(double basis) {
		if (pricingType == null)
			pricingType = PriceType.Un_Priced;
		else if (pricingType != PriceType.Un_Priced)
			throw new IllegalArgumentException(
					"Only un_priced pricing has basis!");
		this.basis = basis;
	}

	public double getFullyPrice() {
		if (pricingType == null)
			throw new IllegalArgumentException(
					"No pricing type being defined yet!");
		else if (pricingType != PriceType.Fully_Priced)
			throw new IllegalArgumentException(
					"Only fully priced pricing has full price!");
		return fullyPrice;
	}

	public void setFullyPrice(double fullyPrice) {
		if (pricingType == null)
			pricingType = PriceType.Fully_Priced;
		else if (pricingType != PriceType.Fully_Priced)
			throw new IllegalArgumentException(
					"Only fully priced pricing has fully price!");
		this.fullyPrice = fullyPrice;
	}*/
	@Override
	public void setDefault() {
		// TODO Auto-generated method stub
		
	}
	
	
}

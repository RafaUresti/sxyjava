package com.kemplerEnergy.unfinished;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.kemplerEnergy.model.BaseObject;
import com.kemplerEnergy.model.Broker;
import com.kemplerEnergy.model.CounterParty;
import com.kemplerEnergy.unfinished.Contract;
import com.kemplerEnergy.unfinished.Trader;




public class Ticket extends BaseObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5924556908206946755L;

	/*
	 * Invalid: 
	 */
	private final String[] TicketStatus = {"INVALID", "PENDING", "ACCEPTED", "REJECTED" };

	private Timestamp createTime;
	private Date tradeDate;
	private String comment;
	private String ticketNo;
	private String status;
	
	private Broker broker;
	private Trader trader;
	private CounterParty counterParty;
	
	private Contract purchaseContract;
	private Contract saleContract;
	
	public Ticket() {}

	
	@Override
	public void setDefault() {
		createTime = new Timestamp(System.currentTimeMillis());
		tradeDate = new Date(System.currentTimeMillis());
		comment = "";
		status = "PENDING";
		ticketNo = "2000xxx";
		broker = new Broker();
		trader = null;
		counterParty = null;
		purchaseContract = null;
		saleContract = null;
	}
	
	public static void main(String[] args) {
		new Ticket().save();
	}


	public Timestamp getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}


	public Date getTradeDate() {
		return tradeDate;
	}


	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getTicketNo() {
		return ticketNo;
	}


	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Broker getBroker() {
		return broker;
	}


	public void setBroker(Broker broker) {
		this.broker = broker;
	}


	public Trader getTrader() {
		return trader;
	}


	public void setTrader(Trader trader) {
		this.trader = trader;
	}


	public CounterParty getCounterParty() {
		return counterParty;
	}


	public void setCounterParty(CounterParty counterParty) {
		this.counterParty = counterParty;
	}


	public Contract getPurchaseContract() {
		return purchaseContract;
	}


	public void setPurchaseContract(Contract purchaseContract) {
		this.purchaseContract = purchaseContract;
	}


	public Contract getSaleContract() {
		return saleContract;
	}


	public void setSaleContract(Contract saleContract) {
		this.saleContract = saleContract;
	}
	
	
}


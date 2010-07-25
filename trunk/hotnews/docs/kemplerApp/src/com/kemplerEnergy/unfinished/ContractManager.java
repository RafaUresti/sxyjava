package com.kemplerEnergy.unfinished;

import com.kemplerEnergy.model.admin.User;



public class ContractManager extends User implements TicketOperation {

	private int id;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void delete(Ticket ticket) {
		// TODO Auto-generated method stub

	}

	@Override
	public Ticket get(Ticket ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Ticket ticket) {
		// TODO Auto-generated method stub

	}

}

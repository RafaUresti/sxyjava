package com.kemplerEnergy.unfinished;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kemplerEnergy.model.admin.User;
import com.kemplerEnergy.util.BCrypt;

public class Trader extends User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 245706314583274654L;
	private List<Ticket> tickets = new ArrayList<Ticket>();	
	
	public Trader() {
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
	
	public void setDefault() {
		setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
		setEmail("jf2476@columbia.edu");
		setFirstName("First");
		setLastName("Last");
		setUserName("trader");
	}

	public static void main(String[] args) {
		new Trader().save();
	}
	
}

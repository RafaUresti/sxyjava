package com.kemplerEnergy.unfinished;

import java.util.List;

import com.kemplerEnergy.client.admin.UserRemote;
import com.kemplerEnergy.client.admin.UserRemoteAsync;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TicketRemoteAsync {
	/**
	 * Create new ticket
	 */
	public void newTicket(AsyncCallback<Ticket> callback);
	public void getTicketList(String status, AsyncCallback<List<Ticket>> callback);
	public void saveTicket(Ticket ticket, AsyncCallback<Void> callback);
}

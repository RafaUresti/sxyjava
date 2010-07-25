package com.kemplerEnergy.unfinished;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.kemplerEnergy.client.admin.UserRemote;
import com.kemplerEnergy.client.admin.UserRemoteAsync;

public interface TicketRemote extends RemoteService {
	/**
	 * Utility class for simplifing access to the instance of async service.
	 */
	public static class Util {
		private static TicketRemoteAsync instance;
		public static TicketRemoteAsync getInstance(){
			if (instance == null) {
				instance = (TicketRemoteAsync) GWT.create(UserRemote.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "/TicketRemote");
			}
			return instance;
		}
	}
	
	/**
	 * Create new ticket
	 * @return
	 */
	public Ticket newTicket();
	public List<Ticket> getTicketList(String status);
	public void saveTicket(Ticket ticket);
}

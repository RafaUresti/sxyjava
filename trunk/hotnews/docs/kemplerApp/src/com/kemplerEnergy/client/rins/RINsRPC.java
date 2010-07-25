package com.kemplerEnergy.client.rins;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.kemplerEnergy.exception.AuthorizationException;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.model.ShipMode;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINInvoiceSearch;

public interface RINsRPC extends RemoteService{
	public EPAPartner getEPAPartner(String name) throws RINException, AuthorizationException;
	public ArrayList<EPAPartner> getEPAPartnerList() throws RINException, AuthorizationException;
	public ArrayList<ShipMode> getShipModeList() throws RINException, AuthorizationException;
	
	public void deleteRIN(RIN r) throws RINException, AuthorizationException;
	
	public void sellRINs(Invoice sellInvoice) throws RINException, AuthorizationException;
	public ArrayList<Invoice> getInvoiceList(String status) throws RINException, AuthorizationException;
	public Invoice getInvoice(RINInvoiceSearch invoiceSearch) throws RINException, AuthorizationException;
	public ArrayList<Invoice> getInvoiceList(RINInvoiceSearch invoiceSearch) throws RINException, AuthorizationException;
	public void saveInvoice(Invoice invoice) throws RINException, AuthorizationException;
	public void rejectBuyInvoice(Invoice invoice, String reason) throws RINException, AuthorizationException;
	public int[] getInventory() throws RINException, AuthorizationException;
	
	/**
	 * Generate RFS report
	 * @param year (2008, 2009, etc.)
	 * @param quarter (1, 2, 3, 4)
	 * @return the relative path of the CSV file
	 */
	public String generateRFSReport(int year, int quarter);
	
	public static class Util {
		private static RINsRPCAsync instance;
		public static RINsRPCAsync getInstance(){
			if (instance == null) {
				instance = (RINsRPCAsync) GWT.create(RINsRPC.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "RINsRPC");
			}
			return instance;
		}
	}
}

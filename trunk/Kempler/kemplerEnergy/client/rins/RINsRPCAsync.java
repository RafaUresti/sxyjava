package com.kemplerEnergy.client.rins;

import java.util.ArrayList;
import java.util.List;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.model.ShipMode;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINInvoiceSearch;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RINsRPCAsync{
	public void getEPAPartner(String name, AsyncCallback<EPAPartner> callback);
	public void getEPAPartnerList(AsyncCallback<ArrayList<EPAPartner>> callback);
	public void getShipModeList(AsyncCallback<ArrayList<ShipMode>> callback);
	
	public void deleteRIN(RIN r, AsyncCallback<Void> callback);
	
	public void sellRINs(Invoice sellInvoice, AsyncCallback<Void> callback);
	public void getInvoiceList(String status, AsyncCallback<ArrayList<Invoice>> callback);
	public void getInvoice(RINInvoiceSearch invoiceSearch, AsyncCallback<Invoice> callback);
	public void getInvoiceList(RINInvoiceSearch invoiceSearch, AsyncCallback<ArrayList<Invoice>> callback);
	public void saveInvoice(Invoice invoice, AsyncCallback<Void> callback);
	public void rejectBuyInvoice(Invoice invoice, String reason, AsyncCallback<Void> callback);
	public void getInventory(AsyncCallback<int[]> callback);
	public void getVersionDetail(AsyncCallback<String> callback);//method to get the version number
	public void getAvailableRINs(Invoice sellInvoice, AsyncCallback<RIN[]> callback);
	public void saveAvailableRINsPositions(List<Integer> rinIds, List<Integer> rinPositions, AsyncCallback<Void> callback);
	
	/**
	 * Generate RFS report
	 * @param year (2008, 2009, etc.)
	 * @param quarter (1, 2, 3, 4)
	 * @param callback the callback to return the relative path of the CSV file
	 */
	public void generateRFSReport(int year, int quarter, AsyncCallback<String> callback);
}

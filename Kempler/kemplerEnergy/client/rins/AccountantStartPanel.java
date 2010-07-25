package com.kemplerEnergy.client.rins;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kemplerEnergy.model.rins.Invoice;

/**
 * The panel displays a list of PENDING invoices that the accountant
 * needs to verify.
 * @author sxycode
 *
 */
public class AccountantStartPanel extends Composite{
	final RINsRPCAsync svc = RINsRPC.Util.getInstance();
	
	private RootPanel rootPanel = RootPanel.get("middle");
	private ArrayList<Invoice> pendingInvoices;
	private VerticalPanel pendingInvoiceListPanel = new VerticalPanel();
	
	public AccountantStartPanel(){
		listPendingInvoices();
		this.initWidget(pendingInvoiceListPanel);
	}
	
	/**
	 * Lists all the PENDING invoices
	 */
	private void listPendingInvoices() {
		svc.getInvoiceList("PENDING", new AsyncCallback<ArrayList<Invoice>>(){
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get PENDING invoices: " + caught.getMessage());
			}
			public void onSuccess(ArrayList<Invoice> result) {
				pendingInvoices = result;
				if (pendingInvoices == null || pendingInvoices.isEmpty()){
					Window.alert("No PENDING invoices at this time!");
				}else{
					for (int i = 0; i < pendingInvoices.size(); i ++){
						final Invoice invoice = pendingInvoices.get(i);
						final HorizontalPanel pendingInvoicePanel = new HorizontalPanel();
						final Label pendingInvoiceLabel = new Label("PENDING Invoice # "+invoice.getInvoiceNo()+ " from "+invoice.getEpaPartner().getFullName());
						final Button pendingInvoiceButton = new Button ("Verify this!");
						pendingInvoicePanel.add(pendingInvoiceLabel);
						pendingInvoicePanel.add(pendingInvoiceButton);
						pendingInvoiceListPanel.add(pendingInvoicePanel);
						pendingInvoiceButton.addClickListener(new ClickListener(){
							public void onClick(Widget sender) {
								rootPanel.clear();
								rootPanel.add(new RINsVerificationPanel(invoice));
							}
						});
					}
				}
			}
			
		});
	}
}

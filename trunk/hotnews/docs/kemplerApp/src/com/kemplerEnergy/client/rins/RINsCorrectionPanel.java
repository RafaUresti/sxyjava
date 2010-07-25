package com.kemplerEnergy.client.rins;

import java.util.ArrayList;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;

/**
 * The class to create a panel for the Logistics to correct
 * RINs in the invoice that has been rejected by the accountant
 * @author Xiaoyi Sheng
 *
 */
public class RINsCorrectionPanel extends RINsEntryPanel{
	final RINsRPCAsync svc = RINsRPC.Util.getInstance();

	private ArrayList<RIN> rins = new ArrayList<RIN>();
	private Invoice buyInvoice;
	public RINsCorrectionPanel(Invoice buyInvoice){
		super(buyInvoice);
		this.buyInvoice = buyInvoice;
		this.getInvoice().setStatus("PENDING");
		createRINEntries();
	}

	public void createRINEntries() {
		Window.alert("# of rins: "+ rins.size());
		for (int i = 0; i < rins.size(); i ++){
			final RINEntryRow row =  getEntryRowList().get(i);
			final int rinIndex = i;
			row.showRowStatus(rins.get(i));
			if (!row.showRowStatus(rins.get(i))){//ONLY change the delete button of unverified RIN rows
				row.remove(row.getDeleteButton());
				Button newDeleteButton = new Button("Delete");
				row.add(newDeleteButton);
				newDeleteButton.addClickListener(new ClickListener(){
					public void onClick(Widget arg0) {
						if (Window.confirm("Are you sure you want to delete this RIN?")){//confirm before delete
							svc.deleteRIN(rins.get(rinIndex), new AsyncCallback<Void>(){
								public void onFailure(Throwable arg0) {
									Window.alert("Database error: failed to delete the RIN!" + arg0.getMessage());
								}
								public void onSuccess(Void arg0) {
									Window.alert("RIN deleted from the database successfully!");
									//------------The migration of the original listener:XXX--------------------
									RINsCorrectionPanel.this.getManualEntryTable().remove(row);
									getInvoice().getRins().remove(rinIndex);
									ManualEntryTable met = RINsCorrectionPanel.this.getManualEntryTable();
									RINsEntryPanel rep = met.getRinsEntryPanel();
									met.getRowList().remove(row);
									rep.setActualGallons(buyInvoice.calculateActualGallons());//refresh the actual gallons
									met.setSaveButtonStatus();
									ArrayList<RINEntryRow> rowList = RINsCorrectionPanel.this.getEntryRowList();
									if (rowList.size() == 1){
										rowList.get(0).disableDeleteButton();//prevents deleting the only row left
									}
									rowList.get(rowList.size()-1).enableNextButton();//enable the "Next" button on the last row
									//--------------End original listener migration----------------------------------
								}
							});
						}
					}
				});
			}
		}

	}
}

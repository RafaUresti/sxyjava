package com.kemplerEnergy.client.rins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;

/**
 * Panel for Accountant to verify RINs input by 
 * Logistics. Left side shows the RINs and the right
 * side shows the original PTD document
 * @author Xiaoyi Sheng
 *
 */
public class RINsVerificationPanel extends Composite{

	final RINsRPCAsync svc = RINsRPC.Util.getInstance();

	protected RootPanel rootPanel = RootPanel.get("middle");
	protected HorizontalPanel hPanel = new HorizontalPanel();
	private VerticalPanel rinSide = new VerticalPanel();

	private List<RIN> rins;
	private ArrayList<RIN> newRINs = new ArrayList<RIN>();//All the "NEW" RINs
	private Invoice buyInvoice = new Invoice();
	private ArrayList<VerificationRow> rowList = new ArrayList<VerificationRow>();
	private HorizontalPanel rejectAcceptPanel = new HorizontalPanel();
	private Button rejectButton = new Button("Reject");
	private Button acceptButton = new Button("Accept");
	private Frame emailFrame;
	private final int LW = 500, RW = 1000, VW = 1500;
	private final String VPANEL_WIDTH = VW+"px", VPANEL_HEIGHT = "700px";
	private final String LEFTSIDE_WIDTH = LW+"px", LEFTSIDE_HEIGHT = "100%";
	private final String RIGHTSIDE_WIDTH = RW+"px", RIGHTSIDE_HEIGHT = "100%";

	
	
	public RINsVerificationPanel(Invoice buyInvoice){
		
		this.initWidget(hPanel);
		emailFrame = new Frame(RPTDDisplayPanel.PATH + buyInvoice.getInvoicePath());
		this.buyInvoice = buyInvoice;
//		invoiceURL += buyInvoice.getId();//TODO set invoice URL to display on the right side
		rins = buyInvoice.getRins();//Gets all the rins in the invoice
		this.setSize(VPANEL_WIDTH, VPANEL_HEIGHT);
		createLeftSide();
		createRightSide();
	}
	
	private void createRightSide() {
		emailFrame.setHeight(RIGHTSIDE_HEIGHT);
		emailFrame.setWidth(RIGHTSIDE_WIDTH);
		hPanel.add(emailFrame);
	}
	
	private void createLeftSide() {
		//Only NEW RINs will be displayed
		for (int i = 0; i < rins.size(); i ++){
			if (rins.get(i).getRinStatus().equalsIgnoreCase("NEW"))
				newRINs.add(rins.get(i));
		}
		for (int i = 0; i < newRINs.size(); i ++){
			VerificationRow row = new VerificationRow(newRINs.get(i));
			rowList.add(row);
			rinSide.add(row);//Add the row to UI

			for (int j = 0; j < 9; j ++){
				row.getRINComponentBoxes()[j].setText(newRINs.get(i).readComponent()[j]);
			}
			row.showRowStatus(newRINs.get(i));
			row.setReadOnly();
		}
		rejectAcceptPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		rejectAcceptPanel.setSpacing(20);
		rejectAcceptPanel.add(rejectButton);
		rejectAcceptPanel.add(acceptButton);
		acceptButton.setEnabled(false);
		rinSide.add(rejectAcceptPanel);
		rinSide.setHeight(LEFTSIDE_HEIGHT);
		rinSide.setWidth(LEFTSIDE_WIDTH);
		hPanel.add(rinSide);

		rejectButton.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				if (Window.confirm("Are you sure you want to reject the Invoice?")){
					rejectButton.setEnabled(false);
					buyInvoice.setStatus("REJECTED");
					
					svc.saveInvoice(buyInvoice, new AsyncCallback<Void>(){
						public void onFailure(Throwable arg0) {
							rejectButton.setEnabled(true);
							Window.alert("Rejection failure: "+arg0.getMessage());
						}
						public void onSuccess(Void arg0) {
							Window.alert("Rejection success!");
							rootPanel.clear();
							rootPanel.add(new AccountantStartPanel());
						}
					});
				}
			}
		});

		acceptButton.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				acceptButton.setEnabled(false);
				for (int i = 0; i < newRINs.size();i++){
					newRINs.get(i).setRinStatus("ACCEPTED");
				}//Can only change the status of NEW RINs
				buyInvoice.setStatus("ACCEPTED");
				svc.saveInvoice(buyInvoice, new AsyncCallback<Void>(){
					public void onFailure(Throwable arg0) {
						Window.alert("Acception failure: " + arg0.getMessage());
						acceptButton.setEnabled(true);
					}
					public void onSuccess(Void arg0) {
						Window.alert("Acception success!");
						rootPanel.clear();
						rootPanel.add(new AccountantStartPanel());
					}
				});
			}
		});
	}
	/**
	 * A RIN number row with listeners. Sets the validation mask
	 * of RINs. Checking the check box sets the validation mask 
	 * to 222222222. Clicking of any component of a RIN sets the 
	 * corresponding validation mask value to 1.
	 * @author Xiaoyi Sheng
	 *
	 */
	private class VerificationRow extends RINEntryRow{
		RIN rin;
		String[] rinComponents;
		CheckBox checkBox = new CheckBox();

		VerificationRow(RIN rin){
			super();
			this.remove(this.getDeleteButton());
			this.remove(this.getNextButton());
			this.add(checkBox);
			this.rin = rin;
			this.rinComponents = rin.readComponent();
			createRINComponentBoxes();
			configureCheckBox();
		}

		private void configureCheckBox() {
			checkBox.setChecked(Arrays.equals(rin.getValidationMask(), RIN.VERIFIED.toCharArray()));//if the validationMask of the rin is already verified
			checkBox.addClickListener(new ClickListener(){
				public void onClick(Widget sender){
					if (checkBox.isChecked()){
						setVerified(true);
						rin.setValidationMask(RIN.VERIFIED.toCharArray());
						setAcceptButtonStatus();
					}
					else {
						setVerified(false);
						rin.setValidationMask(RIN.UNVALIDATED.toCharArray());
						RINsVerificationPanel.this.acceptButton.setEnabled(false);
					}
					VerificationRow.this.showRowStatus(rin);
				}

			});
		}

		private void createRINComponentBoxes() {
			for (int i = 0; i < 9; i ++){
				final int componentIndex = i;

				this.getRINComponentBoxes()[i].addClickListener(new ClickListener(){
					public void onClick(Widget sender) {
						if (!DOM.getStyleAttribute(VerificationRow.this.getRINComponentBoxes()[componentIndex].getElement(), "backgroundColor").equalsIgnoreCase("yellow")){
							DOM.setStyleAttribute(VerificationRow.this.getRINComponentBoxes()[componentIndex].getElement(), "backgroundColor", "yellow");
							checkBox.setChecked(false);
							checkBox.setEnabled(false);
							rin.getValidationMask()[componentIndex] = '1';
							RINsVerificationPanel.this.acceptButton.setEnabled(false);
						}
						else {
							DOM.setStyleAttribute(VerificationRow.this.getRINComponentBoxes()[componentIndex].getElement(), "backgroundColor", "white");
							//ReEnable checkbox if no textbox in the rin is marked yellow
							rin.getValidationMask()[componentIndex] = '0';
							setCheckBoxStatus();
							setAcceptButtonStatus();
						}
					}
				});
			}
		}
		private void setCheckBoxStatus(){
			boolean hasMismatch = false;
			for (char m: rin.getValidationMask()){
				if (m == '1'){
					hasMismatch = true;
					setVerified(false);
					break;
				}
			}
			if (!hasMismatch)
				checkBox.setEnabled(true);
		}
		private void setAcceptButtonStatus(){
			boolean readyToAccept = true;
			for (int i = 0; i < RINsVerificationPanel.this.rowList.size(); i++){
				if (!RINsVerificationPanel.this.rowList.get(i).isVerified()){
					RINsVerificationPanel.this.acceptButton.setEnabled(false);
					readyToAccept = false;
					break;
				}
			}
			if (readyToAccept){
				RINsVerificationPanel.this.acceptButton.setEnabled(true);
			}
		}
	}
}

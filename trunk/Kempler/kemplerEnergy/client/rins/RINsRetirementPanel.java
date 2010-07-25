package com.kemplerEnergy.client.rins;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kemplerEnergy.client.util.UIs;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINInvoiceSearch;

/**
 * Panel to retire RINs in an invoice. The invoice 
 * should not be in PENDING/REJECTED/SALE_PENDING
 * status
 * @author sxycode
 *
 */
public class RINsRetirementPanel extends RINsEntryPanel{
	final RINsRPCAsync svc = RINsRPC.Util.getInstance();

	private ManualEntryTable rinTable;
	private ArrayList<RINEntryRow> entryRowList;
	private ArrayList<RINRetireRow> retireRowList = new ArrayList<RINRetireRow>();
	private ArrayList<RIN> rinList;
	private Button findRINsButton = new Button("Find RINs");
	private Button saveRetirementButton = new Button("Save Retirement");
	private Button enterReplacementsButton = new Button ("Enter Replacements");
	final Button submitButton = new Button("Submit");
	private PTDUpload uploadPTDForm;
	private Invoice invoice;

	public RINsRetirementPanel(Invoice invoice){
		super(invoice);
		this.invoice = getInvoice();
		uploadPTDForm = new PTDUpload();
		rinList = (ArrayList<RIN>) getInvoice().getRins();
		rinTable = getManualEntryTable();
		entryRowList = rinTable.getRowList();
		showRINTable();
		assembleFindRINsButton();
		assembleBottom();
	}

	private void showRINTable() {
		this.vPanel.remove(getProcessButton());
		this.vPanel.remove(getRejectSavePanel());
		//Get rid of nextButton and deleteButton, replace with retireButton and add listeners
		for (int i = 0; i < rinList.size(); i ++){//the i th RIN, now rinList and rowList match
			final int rowIndex = i;
			final RINRetireRow row = new RINRetireRow(entryRowList.get(rowIndex));
			final RIN rin = rinList.get(rowIndex);
			retireRowList.add(row);
			row.setReadOnly(true);//Cannot change, but retire
			if (rin.getRinStatus().equalsIgnoreCase("RETIRED")){//If RINs have already been retired
				row.retireButton.setVisible(false);
			}
			row.retireButton.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					if (Window.confirm("Are you sure this is the RIN to retire/replace?")){
						rin.setRinStatus("CORRUPTED");
						submitButton.setEnabled(true);
						markToBeRetiredRow(row, rin);
						int actualGallons = RINsRetirementPanel.this.getInvoice().calculateActualGallons();
						RINsRetirementPanel.this.setActualGallons(actualGallons);
					}
				}
			});

		}
		rinTable.remove(entryRowList.get(entryRowList.size()-1));//The last row is removed
		entryRowList.remove(entryRowList.size()-1);
	}


	private void assembleFindRINsButton(){
		this.vPanel.insert(findRINsButton,vPanel.getWidgetIndex(getEnterRINsBottom()));
		findRINsButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				ArrayList<RIN> quickRINList = processQuickRINs();
				if (quickRINList != null)
					enableRetireButtons(findRINs(quickRINList));
			}

			/**
			 * Enable only the retire buttons that belong to rows specified by indices
			 * @param indices
			 */
			private void enableRetireButtons(ArrayList<Integer> indices){
				//Disable retire buttons before enabling the desired ones
				for (int i = 0; i < retireRowList.size(); i ++){
					retireRowList.get(i).retireButton.setEnabled(false);
				}
				//Enable retire buttons according to indices
				for (int i = 0; i < indices.size(); i ++){
					retireRowList.get(indices.get(i)).retireButton.setEnabled(true);
				}
			}
		});
	}


	private void assembleBottom(){
		this.vPanel.insert(UIs.getSPH10(), vPanel.getWidgetIndex(getEnterRINsBottom()));
		this.vPanel.add(UIs.getSPH10());

		final Label askReplacementLabel = new Label ("Are Replacement RINs Available?");
		final RadioButton unavailableButton = new RadioButton("Replacement?", "NO /");
		final RadioButton availableButton = new RadioButton("Replacement?", "YES");

		final HorizontalPanel submitPanel = new HorizontalPanel();
		final VerticalPanel replacementChoicePanel = new VerticalPanel();
		final HorizontalPanel yesNoPanel = new HorizontalPanel();



		replacementChoicePanel.add(askReplacementLabel);
		yesNoPanel.add(unavailableButton);
		yesNoPanel.add(availableButton);
		replacementChoicePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		replacementChoicePanel.add(yesNoPanel);




		submitPanel.add(saveRetirementButton);
		saveRetirementButton.setEnabled(false);
		submitPanel.add(UIs.getSPW30());
		submitPanel.add(replacementChoicePanel);
		submitPanel.add(UIs.getSPW30());
		submitPanel.add(enterReplacementsButton);
		enterReplacementsButton.setEnabled(false);
		this.vPanel.add(submitPanel);

		final Label uploadNewPTDLabel = new Label("Upload new RPTD");
		final HorizontalPanel uploadPanel = new HorizontalPanel();
		uploadNewPTDLabel.setVisible(false);
		uploadPanel.setVisible(false);
		uploadPanel.add(uploadNewPTDLabel);
		uploadPanel.add(uploadPTDForm);
		vPanel.add(UIs.getSPH10());

		final VerticalPanel buyBottomPanel = new VerticalPanel();
		buyBottomPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buyBottomPanel.add(submitPanel);
		buyBottomPanel.add(UIs.getSPH10());
		buyBottomPanel.add(uploadPanel);

		submitButton.setEnabled(false);

		if (invoice.getInvoiceType().equalsIgnoreCase("PURCHASE")){
			vPanel.add(buyBottomPanel);
		}else if (invoice.getInvoiceType().equalsIgnoreCase("SALE")){
			vPanel.add(submitButton);
			submitButton.addClickListener(new ClickListener(){
				public void onClick(Widget arg0) {
					if (Window.confirm("Are you sure to retire these RINs?")){
						submitButton.setEnabled(false);
						invoice.setStatus("REPLACEMENT_PENDING");
						svc.saveInvoice(invoice, new AsyncCallback<Void>(){
							public void onFailure(Throwable arg0) {
								Window.alert(arg0.getMessage());
								submitButton.setEnabled(true);
							}
							public void onSuccess(Void arg0) {
								Window.alert("RIN retirement successful and customer has been notified!");
								rootPanel.clear();
								rootPanel.add(new LogisticsStartPanel());
							}
						});
					}
				}
			});
		}


		unavailableButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				saveRetirementButton.setEnabled(true);
				enterReplacementsButton.setEnabled(false);
				uploadNewPTDLabel.setVisible(false);
				uploadPanel.setVisible(false);
			}
		});
		availableButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				saveRetirementButton.setEnabled(false);
				enterReplacementsButton.setEnabled(true);
				uploadNewPTDLabel.setVisible(true);
				uploadPanel.setVisible(true);
			}
		});
		saveRetirementButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				if (Window.confirm("Are you sure that NO REPLACEMENT RIN is available from " 
						+ invoice.getEpaPartner().getFullName() +"?")){
					saveRetirementButton.setEnabled(false);
					invoice.setStatus("REPLACEMENT_REQUESTED");
					svc.saveInvoice(RINsRetirementPanel.this.getInvoice(), new AsyncCallback<Void>(){
						public void onFailure(Throwable arg0) {
							Window.alert(arg0.getMessage());
							saveRetirementButton.setEnabled(true);
						}
						public void onSuccess(Void arg0) {
							Window.alert("Retirement Saved!");
							rootPanel.clear();
							rootPanel.add(new LogisticsStartPanel());
						}
					});
				}
			}
		});

		enterReplacementsButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				if (Window.confirm("Are you sure that " + invoice.getEpaPartner().getFullName() +
				" has provided with REPLACEMENT RINs?")){
					RINsRetirementPanel.this.uploadPTDForm.addFormHandler(invoice, enterReplacementsButton);
					RINsRetirementPanel.this.uploadPTDForm.submit();
				}
			}
		});
	}

	/**
	 * Mark the row that has RIN to be retired by changing the retire button
	 * to "TO_BE_RETIRED"
	 * @param row
	 * @param rin
	 */
	private void markToBeRetiredRow (final RINRetireRow row, final RIN rin) {
		row.showRowStatus(rin);
		row.retireButton.setVisible(false);
		//This RIN will be set to "CORRUPTED" before saving to database
		row.getStatusLabel().setText("TO_BE_RETIRED");
	}

	private class RINRetireRow extends RINEntryRow{
		final Button retireButton = new Button("Retire");
		RINEntryRow entryRow;
		RINRetireRow(RINEntryRow row){
			retireButton.setEnabled(false);//Do not allow to retire unless match with input in Quick Entry Area
			row.remove(row.getNextButton());
			row.remove(row.getDeleteButton());
			row.add(retireButton);
			entryRow = row;
		}
		/**
		 * Add a widget to the row
		 */
		public void addToRow(Widget w){
			entryRow.add(w);
		}
		/**
		 * Make the original entry row read only
		 * @param readOnly if to be set read only
		 */
		protected void setReadOnly(boolean readOnly){
			entryRow.setReadOnly();
		}
		/**
		 * Highlights the original entry row according to the status of the RIN.
		 * @param rin
		 */
		protected boolean showRowStatus(RIN rin){
			return entryRow.showRowStatus(rin);
		}
		protected Label getStatusLabel(){
			return entryRow.getStatusLabel();
		}
	}
}

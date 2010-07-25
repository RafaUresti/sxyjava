package com.kemplerEnergy.client.rins;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kemplerEnergy.client.util.UIs;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RINInvoiceSearch;

/**
 * The first panel Logistics sees that have a list of Tasks:</br>
 * NEW Invoice/Correct Invoices/Retire RINs/Replace RINs/Search Invoices
 * @author sxycode
 *
 */
public class LogisticsStartPanel extends Composite{
	final RINsRPCAsync svc = RINsRPC.Util.getInstance();


	private RootPanel rootPanel = RootPanel.get("middle");
	private EPAPartner epaPartner;
	private VerticalPanel taskChoicePanel = new VerticalPanel();
	private Button enterNewInvoiceButton = new Button("New Invoice");
	private Button correctInvoiceButton = new Button("Correct Invoices");

	private Button retireRINButton = new Button("Retire RINs");
	private Button replaceRINButton = new Button("Replace RINs");

	private Button searchButton = new Button("Search Invoices");

	private final String HEIGHT = "300px";

	public LogisticsStartPanel(){
		super();
		taskChoicePanel.setHeight(HEIGHT);

		taskChoicePanel.add(enterNewInvoiceButton);
		taskChoicePanel.add(correctInvoiceButton);

		taskChoicePanel.add(retireRINButton);
		taskChoicePanel.add(replaceRINButton);

		taskChoicePanel.add(searchButton);
		taskChoicePanel.add(new RFSReportGenerationPanel());
		this.initWidget(taskChoicePanel);

		addListeners();
	}
	private void addListeners() {
		enterNewInvoiceButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				rootPanel.clear();
				rootPanel.add(new RINInvoiceEntryPanel());
			}
		});
		correctInvoiceButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0){
				rootPanel.clear();
				rootPanel.add(new CorrectionListPanel());

			}
		});

		retireRINButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				rootPanel.clear();
				rootPanel.add(new RetireSearchPanel());
			}
		});
		replaceRINButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				rootPanel.clear();
				rootPanel.add(new ReplaceSearchPanel());
			}
		});

		searchButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				rootPanel.clear();
				rootPanel.add(new SearchPanel());
			}
		});
	}


	
	/**
	 * Panel to display the list of invoices needing correction
	 * @author sxycode
	 *
	 */
	private class CorrectionListPanel extends VerticalPanel{

		private ArrayList<Invoice>  correctionInvoiceList = new ArrayList<Invoice>();
		CorrectionListPanel(){
			svc.getInvoiceList("REJECTED", new AsyncCallback<ArrayList<Invoice>>(){
				public void onFailure(Throwable arg0) {
					Window.alert("Failed to retrieve correction invoices: "+ arg0.getMessage());
				}
				public void onSuccess(ArrayList<Invoice> arg0) {
					correctionInvoiceList = arg0;
					if (correctionInvoiceList == null || correctionInvoiceList.isEmpty()){
						Window.alert("No invoice needs correction at this time!");
						rootPanel.clear();
						rootPanel.add(new LogisticsStartPanel());
					}else{
						for (int i = 0; i < correctionInvoiceList.size(); i++){
							final Label correctLabel = new Label("Error Invoice # "
									+correctionInvoiceList.get(i).getInvoiceNo()+ " from "
									+correctionInvoiceList.get(i).getEpaPartner().getFullName());
							final Button correctButton = new Button("Correct this");
							final HorizontalPanel correctPanel = new HorizontalPanel();
							correctPanel.add(correctLabel);
							correctPanel.add(correctButton);
							CorrectionListPanel.this.add(correctPanel);

							final int index = i;
							correctButton.addClickListener(new ClickListener(){
								public void onClick(Widget arg0) {
									RINsCorrectionPanel rcp = new RINsCorrectionPanel(correctionInvoiceList.get(index));
									rcp.setTitleFunction("Correct");
									rootPanel.clear();
									rootPanel.add(rcp);
								}
							});
						}
					}
				}
			});
		}
	}

	/**
	 * The Panel to search an invoice to retire by invoice number
	 * and Counter Party name. The invoice cannot be PENDING/REJECTED/SALE_PENDING
	 * @author sxycode
	 *
	 */
	private class RetireSearchPanel extends HorizontalPanel{
		private InvoiceSearchPanel retireInvoiceSearchPanel= new InvoiceSearchPanel("");//TODO
		private Button retireBadRINsButton = new Button("Retire Bad RINs");
		private RINInvoiceSearch retireInvoiceSearch = new RINInvoiceSearch();
		
		RetireSearchPanel(){
			this.add(retireInvoiceSearchPanel);
			this.add(retireBadRINsButton);
			this.setSpacing(20);
			retireBadRINsButton.addClickListener(new ClickListener(){
				public void onClick(Widget arg0){
					if (retireInvoiceSearchPanel.epaPartnerPanel.getGetEPAButton().isEnabled()){
						Window.alert("Please Get EPA #!");
						return;
					}
					retireBadRINsButton.setEnabled(false);
					String badInvoiceNumber =  retireInvoiceSearchPanel.badRINsInvoiceBox.getText();
					epaPartner = retireInvoiceSearchPanel.epaPartnerPanel.getEPAPartner();

					if (badInvoiceNumber!=null && !badInvoiceNumber.isEmpty() && epaPartner != null){
						retireInvoiceSearch.setEpaPartner(epaPartner);
						retireInvoiceSearch.setInvoiceNumber(retireInvoiceSearchPanel.invoiceNumber);
						
						svc.getInvoice(retireInvoiceSearch, new AsyncCallback<Invoice>(){
							public void onFailure(Throwable arg0) {
								Window.alert(arg0.getMessage());
								retireBadRINsButton.setEnabled(true);
							}
							public void onSuccess(Invoice badInvoice) {
								if (badInvoice != null){
									//The invoice has to be: ACCEPTED/SOLD/REPLACEMENT_REQUESTED/REPLACEMENT_PENDING
									if (!verifyInvoice(badInvoice)){
										retireBadRINsButton.setEnabled(true);
										return;
									}
										
									String invoiceStatus = badInvoice.getStatus();
							
									Invoice invoice;
									if (invoiceStatus.equalsIgnoreCase("ACCEPTED")){
										//Create a new version of the invoice, retired RINs are removed
										invoice = new Invoice(badInvoice);
									}
									else invoice = badInvoice;
									RINsRetirementPanel rrp = new RINsRetirementPanel(invoice);
									rrp.setTitleFunction("Retire");
									rootPanel.clear();
									rootPanel.add(rrp);
								}
								else Window.alert("No such invoice found!");
							}
							
							/**
							 * Check if the invoice is in verified/fulfilled status: </br>
							 * ACCEPTED/SOLD/REPLACEMENT_REQUESTED/REPLACEMENT_PENDING</br>.
							 * If the invoice status is PENDING/REJECTED/SALE_PENDING,
							 * it is not verified/fulfilled
							 * @param invoice
							 * @return <code>true</code> if the invoice is verified/fulfilled status
							 */
							private boolean verifyInvoice(Invoice invoice){
								String invoiceStatus = invoice.getStatus();
								if (invoiceStatus.equalsIgnoreCase("PENDING")){
									Window.alert("This invoice hasn't been verified by the Accountant yet!");
									return false;
								}
								if (invoiceStatus.equalsIgnoreCase("REJECTED") ){
									Window.alert("This invoice has been rejected by the Accountant!");
									return false;
								}
								if (invoiceStatus.equalsIgnoreCase("SALE_PENDING")){
									Window.alert("This invoice is an unfulfilled SALES invoice!");
									return false;
								}
								return true;
							}
						});
					}
				}
			});
		}
	}
	/**
	 * The Panel to search for an invoice to replace RINs by invoice number
	 * and Counter Party name. The invoice status has to be REPLACEMENT_REQUESTED
	 * @author sxycode
	 *
	 */
	private class ReplaceSearchPanel extends VerticalPanel{
		private InvoiceSearchPanel buyInvoiceSearchPanel = new InvoiceSearchPanel("PURCHASE");
		private HorizontalPanel hPanel = new HorizontalPanel();
		private PTDUpload newPTDUpload = new PTDUpload();
		private Invoice invoice;
		private Button replaceBadRINsButton = new Button("Replace Bad RINs");
		private RINInvoiceSearch buyInvoiceSearch = new RINInvoiceSearch();
		
		ReplaceSearchPanel(){
			hPanel.add(buyInvoiceSearchPanel);
			hPanel.add(replaceBadRINsButton);
			hPanel.setSpacing(20);
			this.add(hPanel);
			this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			
			final HorizontalPanel uploadPanel = new HorizontalPanel();
			uploadPanel.add(new Label("Upload New PTD"));
			uploadPanel.add(newPTDUpload);
			this.add(uploadPanel);
			this.setSpacing(20);
			
			replaceBadRINsButton.addClickListener(new ClickListener(){
				public void onClick(Widget arg0) {
					if (buyInvoiceSearchPanel.epaPartnerPanel.getGetEPAButton().isEnabled()){
						Window.alert("Please Get EPA #!");
						return;
					}
					replaceBadRINsButton.setEnabled(false);
					String badInvoiceNumber =  buyInvoiceSearchPanel.badRINsInvoiceBox.getText();
					epaPartner = buyInvoiceSearchPanel.epaPartnerPanel.getEPAPartner();

					if (badInvoiceNumber!=null && !badInvoiceNumber.isEmpty() && epaPartner != null){
						buyInvoiceSearch.setEpaPartner(epaPartner);
						buyInvoiceSearch.setInvoiceNumber(buyInvoiceSearchPanel.invoiceNumber);
						svc.getInvoice(buyInvoiceSearch, new AsyncCallback<Invoice>(){
							public void onFailure(Throwable arg0) {
								Window.alert(arg0.getMessage());
								replaceBadRINsButton.setEnabled(true);
							}
							public void onSuccess(Invoice badBuyInvoice) {
								if (badBuyInvoice != null){
									if (!badBuyInvoice.getInvoiceType().equalsIgnoreCase("PURCHASE")){
										Window.alert("This is not a PURCHASE Invoice!");
										replaceBadRINsButton.setEnabled(true);
										return;
									}
									if (!badBuyInvoice.getStatus().equalsIgnoreCase("REPLACEMENT_REQUESTED")){
										Window.alert("This invoice is NOT an REPLACEMENT REQUESTED invoice!");
										replaceBadRINsButton.setEnabled(true);
										return;
									}
									invoice = badBuyInvoice;
									newPTDUpload.generateInvoicePath(invoice);
									newPTDUpload.addFormHandler(invoice, replaceBadRINsButton);
									newPTDUpload.submit();
								}
								else Window.alert("No such invoice found!");
							}
						});
					}
				}
			});
			
		}

	}

	/**
	 * The Panel to search for an invoice by invoice number
	 * and Counter Party name
	 * @author sxycode
	 *
	 */
	private class InvoiceSearchPanel extends HorizontalPanel{
		String invoiceNumber = "";

		TextBox  badRINsInvoiceBox = new TextBox();
		EPAPartnerSuggestionPanel epaPartnerPanel = new EPAPartnerSuggestionPanel();
		String invoiceLabelText = "Invoice#: ";
		Label invoiceLabel;
		public InvoiceSearchPanel(String type){
			if (!type.isEmpty())
				invoiceLabelText = type + " " + invoiceLabelText;
			invoiceLabel = new Label (invoiceLabelText);
			this.add(invoiceLabel);
			this.add(badRINsInvoiceBox);
			this.add(epaPartnerPanel);
			badRINsInvoiceBox.addChangeListener(new ChangeListener(){
				public void onChange(Widget arg0) {
					invoiceNumber = badRINsInvoiceBox.getText();
				}
			});
		}
	}

	/**
	 * Panel used to pick a time and period for RFS report generation
	 * @author sxycode
	 *
	 */
	private class RFSReportGenerationPanel extends HorizontalPanel{
		TextBox yearBox = new TextBox();
		int year;
		ListBox quarterBox = new ListBox();
		int quarter;
		Button getReportButton = new Button("Generate Report");
		HTML rfsLink;
		RFSReportGenerationPanel(){
			this.add(yearBox);
			this.add(quarterBox);
			for (int i = 1; i <= 4; i ++ )
				quarterBox.addItem(i + "");
			this.add(getReportButton);
			addListeners();
		}
		private void addListeners(){
			yearBox.addChangeListener(new ChangeListener(){
				public void onChange(Widget arg0) {
					// TODO Auto-generated method stub
					String yearInput = yearBox.getText();
					try{
						year = Integer.valueOf(yearInput);
					}catch(NumberFormatException e){
						Window.alert("Year must be a number!");
					}
				}
			});
			quarterBox.addChangeListener(new ChangeListener(){
				public void onChange(Widget arg0) {
					quarter = quarterBox.getSelectedIndex() + 1;
				}
			});
			getReportButton.addClickListener(new ClickListener(){
				public void onClick(Widget arg0) {
					if (year < 2000 || year > 2099){
						Window.alert("Please input 4-digit year!");
						return;
					}
					getReportButton.setEnabled(false);
					svc.generateRFSReport(year, quarter, new AsyncCallback<String>(){
						public void onFailure(Throwable arg0) {
							Window.alert("Failed to generate report: " + arg0.getMessage());
							getReportButton.setEnabled(true);
							return;
						}
						public void onSuccess(String arg0) {
							String filePath = "<a href=\"" + SearchPanel.URL + arg0 + "\" target=\"_blank\">RPTD</a>";
							rfsLink = new HTML(filePath);
							RFSReportGenerationPanel.this.add(rfsLink);
							getReportButton.setEnabled(true);
						}
						
					});
				}
				
			});
			
		}
	}
}

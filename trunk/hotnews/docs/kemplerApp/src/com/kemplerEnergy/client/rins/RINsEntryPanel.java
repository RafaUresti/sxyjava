package com.kemplerEnergy.client.rins;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.*;
import com.kemplerEnergy.client.util.UIs;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.Invoices;
import com.kemplerEnergy.client.rins.LogisticsStartPanel;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINParser;
import com.kemplerEnergy.model.rins.RINs;
import com.kemplerEnergy.model.rins.StringTokenizer;
import com.kemplerEnergy.model.rins.Invoice.RinType;

/**
 * The RIN Entry Panel for the Logistics to enter RINs from an invoice
 * @author  sxycode
 */
public class RINsEntryPanel extends Composite{

	final RINsRPCAsync svc = RINsRPC.Util.getInstance();

	protected RootPanel rootPanel = RootPanel.get("middle");
	protected VerticalPanel vPanel = new VerticalPanel();

	private int expectedGallons;
	private int actualGallons = 0;

	private Label invoiceNumberLabel;
	private Label expectedGallonsLabel;
	private Label enterRINsBottom;
	private ScrollPanel manualEntryTablePanel;
	private ManualEntryTable manualEntryTable;
	private TextArea quickEntryArea;
	private Button processButton;

	private HorizontalPanel rejectSavePanel;
	final String MANUAL_ENTRY_HEIGHT = "300px";
	final String QUICK_ENTRY_WIDTH = "700px";
	final String QUICK_ENTRY_HEIGHT = "180px";

	private Button saveButton;
	private Button rejectButton;
	private ArrayList<RINEntryRow> entryRowList;//All the manual entry rows

	private String invoiceNumber;
	private Invoice invoice;

	public RINsEntryPanel(Invoice invoice){

		this.initWidget(vPanel);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.invoice = invoice;
		this.invoiceNumber = invoice.getInvoiceNo();
		createEnterRINsTop();
		createManualEntryTable();
		createQuickEntryPanel();
		createEnterRINsBottom();
		setInvoiceNumber(invoice.getInvoiceNo());
	}

	/**
	 * The Title lines of Enter RINs page
	 */
	private void createEnterRINsTop() {
		invoiceNumberLabel = new Label();
		expectedGallonsLabel = new Label();
		expectedGallonsLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(invoiceNumberLabel);
		vPanel.add(expectedGallonsLabel);
		setExpectedGallons(invoice.getExpectedGallons());
	}

	/**
	 * RIN manual entry area
	 */
	private void createManualEntryTable() {
		manualEntryTablePanel = new ScrollPanel();
		manualEntryTable = new ManualEntryTable(this, invoice);
		entryRowList = manualEntryTable.getRowList();
		manualEntryTablePanel.add(manualEntryTable);
		manualEntryTablePanel.setHeight(MANUAL_ENTRY_HEIGHT);//XXX
		vPanel.add(manualEntryTablePanel);
	}
	/**
	 * RIN "Quick Entry" area
	 */
	private void createQuickEntryPanel() {

		quickEntryArea = new TextArea();
		quickEntryArea.setVisible(true);
		processButton = new Button("Process");
		processButton.addClickListener(new ClickListener(){//all the rin parsers from the quick entry box
			public void onClick(Widget sender) {
				ArrayList<RIN> quickRINList = processQuickRINs();
				if (quickRINList != null)
					presentRINs(quickRINList);
			}
		});
		quickEntryArea.setTextAlignment(TextBoxBase.ALIGN_CENTER);
		quickEntryArea.setWidth(QUICK_ENTRY_WIDTH);
		quickEntryArea.setHeight(QUICK_ENTRY_HEIGHT);
		vPanel.add(quickEntryArea);
		vPanel.add(processButton);
		vPanel.add(UIs.getSPH10());
		vPanel.add(UIs.getSPH10());
	}


	private void createEnterRINsBottom() {
		enterRINsBottom = new Label("Number of Gallons:  "+actualGallons+"  (Currently Entered)" +"\n");
		rejectSavePanel = new HorizontalPanel();
		rejectSavePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		enterRINsBottom.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(enterRINsBottom);
		vPanel.add(UIs.getSPH10());

		rejectButton = new Button("Reject");
		saveButton = new Button("Save");
		saveButton.setEnabled(false);

		rejectSavePanel.add(rejectButton);
		rejectSavePanel.add(UIs.getSPW100());
		rejectSavePanel.add(saveButton);
		vPanel.add(rejectSavePanel);

		setActualGallons(invoice.calculateActualGallons());
		manualEntryTable.setSaveButtonStatus();

		saveButton.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				if (!manualEntryTable.entryUpdated()){//In case user forgets to update after a change
					Window.alert("Please Update what you have changed! Expected: "
							+expectedGallons+" Actual: "+actualGallons);
					return;
				}
				boolean matchGallons = Invoices.matchGallonNumbers(invoice);
				ArrayList<RIN> overLapRINs = null;

				try {
					overLapRINs =  RINs.checkOverlap(invoice);
				} catch (Exception e) {
					Window.alert(e.getMessage());}
				
				if (matchGallons && (overLapRINs == null || overLapRINs.isEmpty())){
					saveButton.setEnabled(false);
					try{
						svc.saveInvoice(invoice, new AsyncCallback<Void>(){
							public void onFailure(Throwable caught) {
								Window.alert("Save invoice failed! Please try again!");
								saveButton.setEnabled(true);
							}
							public void onSuccess(Void result) {
								Window.alert("Save RIN invoice success! ");
								rootPanel.clear();
								rootPanel.add(new LogisticsStartPanel());
							}
						});
					}catch (RuntimeException e){
						Window.alert(e.getMessage()+ " when saving invoice");
						e.printStackTrace();
					}
				}
				else if (!overLapRINs.isEmpty()){
					findRINs(overLapRINs);
					Window.alert("There are overlapping RINs in the invoice!");
					saveButton.setEnabled(false);
				}
			}
		});

		rejectButton.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
				if (!Window.confirm("Are you sure all RINs are entered and you want to reject the invoice?"))
					return;
				String rejectionReasons;
				try {
					rejectionReasons = Invoices.generateRejectionReasons(invoice);
				} catch (RINException e) {
					Window.alert(e.getMessage());
					return;
				}
				rejectButton.setEnabled(false);
				
				svc.rejectBuyInvoice(invoice, rejectionReasons, new AsyncCallback<Void>(){
					public void onFailure(Throwable arg0) {
						Window.alert("Rejection email failed to send: " + arg0.getMessage());
						rejectButton.setEnabled(true);
						return;
					}
					public void onSuccess(Void arg0) {
						Window.alert("Rejection email is sent!");	
						rootPanel.clear();
						rootPanel.add(new LogisticsStartPanel());
					}
				});
			}
		});
	}





	/**
	 * Turn RIN entries in the quickEntry box into ArrayList of RINs
	 * @return
	 */
	protected ArrayList<RIN> processQuickRINs() {
		StringTokenizer st = new StringTokenizer(quickEntryArea.getText(), "\n");
		ArrayList<RIN> quickRINList = new ArrayList<RIN>();
		boolean isCorrectRINs = true;
		for (int i = 1; st.hasMoreTokens(); i++){
			String rinString = st.nextToken();
			rinString = rinString.trim();//get rid of empty row input
			try{
				if(rinString.length() > 0)
					quickRINList.add(new RIN(rinString));
			}catch (RINException e){
				isCorrectRINs = false;//isCorrectRINs is true only when all RINs are correct
				Window.alert("RIN # "+ i+": "+ rinString+": " + e.getMessage());
				return null;
			}
		}
		if(isCorrectRINs)//present RINs only when all of them are in correct format
			return quickRINList;
		else return null;
	}
	/**
	 * Get the RINs into the manual entry table
	 */		
	protected void presentRINs(ArrayList<RIN> quickRINList) {	
		try{
			for (RIN r: quickRINList){
				RINEntryRow lastEntryRow = entryRowList.get(entryRowList.size()-1);
				for (int i = 0; i < 9; i++){
					lastEntryRow.getRINComponentBoxes()[i].setText(r.readComponent()[i]);
				}
				lastEntryRow.getNextButton().click();//create a new entry row

			}	
			quickEntryArea.setText("");//clear after processing
		}catch (NullPointerException e){
			Window.alert("RIN list is empty: " + e.getMessage());
		}
	}

	/**
	 * Finds and highlights the RINs in this panel
	 * @param rinsToFind the RINs to find 
	 */
	protected ArrayList<Integer> findRINs(ArrayList<RIN> rinsToFind){
		ArrayList<Integer> foundIndices = new ArrayList<Integer>();
		manualEntryTable.setRINTableStatus();//Reset all the colors
		for (int i = 0; i < invoice.getRins().size(); i++){
			RINEntryRow row = manualEntryTable.getRowList().get(i);
			for (int j = 0; j < rinsToFind.size(); j++){

				if (rinsToFind.get(j).toString().equalsIgnoreCase(invoice.getRins().get(i).toString())){
					row.highlightRow("yellow");
					foundIndices.add(i);
				}
			}
		}
		return foundIndices;
	}


	public Button getSaveButton() {
		return saveButton;
	}

	public Button getRejectButton() {
		return rejectButton;
	}

	public void setActualGallons(int gallons){
		actualGallons = gallons;
		enterRINsBottom.setText("Number of Gallons:  "+actualGallons+"  (Currently Entered)" +"\n");
	}

	public void setExpectedGallons(int gallons){
		expectedGallons = gallons;
		expectedGallonsLabel.setText("Number of Gallons:  "+expectedGallons+"  (Expected)\n\n");
	}
	public int getExpectedGallons(){
		return expectedGallons;
	}
	private void setInvoiceNumber (String invoiceNumber){
		this.invoiceNumber = invoiceNumber;
		invoiceNumberLabel.setText("Enter RINs for Invoice# " + this.invoiceNumber);
	}

	/**
	 * Sets the function part of the title, either "Enter","Retire" or "Replace"
	 * @param function
	 */
	protected void setTitleFunction(String function){
		invoiceNumberLabel.setText(function + " RINs for Invoice# " + this.invoiceNumber);
	}
	public ManualEntryTable getManualEntryTable() {
		return manualEntryTable;
	}


	public Label getEnterRINsBottom() {
		return enterRINsBottom;
	}
	public ArrayList<RINEntryRow> getEntryRowList() {
		return entryRowList;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice){
		this.invoice = invoice;
	}
	public HorizontalPanel getRejectSavePanel() {
		return rejectSavePanel;
	}
	public Button getProcessButton() {
		return processButton;
	}

}

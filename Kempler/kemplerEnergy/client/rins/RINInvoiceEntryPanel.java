package com.kemplerEnergy.client.rins;

/**
 * Panel to input purchase/sale invoice information
 */
import java.util.ArrayList;

import javassist.expr.NewArray;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.*;
import com.kemplerEnergy.client.util.UIs;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.ShipMode;
import com.kemplerEnergy.model.rins.BOLInfo;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;

public class RINInvoiceEntryPanel extends Composite{
	final RINsRPCAsync svc = RINsRPC.Util.getInstance();

	protected RootPanel rootPanel = RootPanel.get("middle");
	private PTDUpload invoicePTDUpload;
	private Invoice invoice = new Invoice();
	private VerticalPanel vPanel = new VerticalPanel();
	
	private String ptdPath = "";
	private String invoiceType = "";//PURCHASE/SALE
	private Label invoiceTypeLabel = new Label("Invoice Type: ");
	private RadioButton invoiceTypeBuy = new RadioButton("invoiceType", "PURCHASE /" );
	private RadioButton invoiceTypeSale = new RadioButton("invoiceType", "SALE");
	private HorizontalPanel invoiceTypePanel = new HorizontalPanel();

	private Label inventoryLabel = new Label();
	private int[] inventory;

	private String invoiceNumber = "";
	private Label invoiceNumberLabel = new Label("Invoice #:");
	private TextBox invoiceNumberBox = new TextBox();
	private HorizontalPanel invoiceNumberPanel = new HorizontalPanel();

	private String invoiceDate = "";
	private Label invoiceDateBoxLabel = new Label("Invoice Date:");
	private TextBox invoiceDateBox = new TextBox();
	private HorizontalPanel invoiceDatePanel = new HorizontalPanel();

	private final CalendarWidget invoiceCalendar = new CalendarWidget();
	private PopupPanel invoiceCalendarPanel = new PopupPanel(true);

	private String transferDate = "";
	private Label transferDateBoxLabel = new Label("Transfer Date:");
	private TextBox transferDateBox = new TextBox();
	private HorizontalPanel transferDatePanel = new HorizontalPanel();

	private final CalendarWidget transferCalendar = new CalendarWidget();
	private PopupPanel transferCalendarPanel = new PopupPanel(true);

	private EPAPartnerSuggestionPanel epaPartnerPanel = new EPAPartnerSuggestionPanel();

	private int gallonAmount = 0;
	private Label gallonAmountLabel =  new Label ("Gallon Amount:");
	private TextBox gallonAmountBox = new TextBox();
	private HorizontalPanel gallonAmountPanel = new HorizontalPanel();


	private ShipMode shipMode;
	private Label shipModeLabel = new Label("Ship Mode:");
	private ListBox shipModeBox = new ListBox();
	private ArrayList<ShipMode> shipModeList;
	private HorizontalPanel shipModePanel = new HorizontalPanel();

	private ArrayList<BOLInfo> bols;
	private Label shippingInfoLabel =  new Label ("Shipping Info:");
	private HorizontalPanel shippingInfoPanel = new HorizontalPanel();
	private SimplePanel shippingTablePanel = new SimplePanel();
	private ShippingEntryTable shippingEntryTable;


	private String rptdK1 = "", rptdK2 = "";
	private Label rptdK1Label = new Label("RPTD # for K1 (or both):");
	private Label rptdK2Label = new Label("RPTD # for K2:");
	private TextBox rptdK1Box = new TextBox();
	private TextBox rptdK2Box = new TextBox();
	private HorizontalPanel rptdK1Panel = new HorizontalPanel();
	private HorizontalPanel rptdK2Panel = new HorizontalPanel();
	private Label uploadLabel = new Label("Invoice-PTD PDF Document Upload:");
	private HorizontalPanel uploadPanel = new HorizontalPanel();

	private Button submitButton = new Button("Submit");
	
	private Button sortAvailableButton = new Button("Sort available RINs");
	
	

	public RINInvoiceEntryPanel(){
		
		this.initWidget(vPanel);
		invoicePTDUpload = new PTDUpload();
		createInputPanel();
		addListeners();
	}
	
	private void createInputPanel() {
		final String CELL_WIDTH = "200px", CELL_HEIGHT = "30px";
		final String VPANEL_WIDTH = "500px", VPANEL_HEIGHT = "400px";

		this.setSize("800px", "600");
		invoiceTypePanel.add(invoiceTypeLabel);
		invoiceTypePanel.add(invoiceTypeBuy);
		invoiceTypePanel.add(invoiceTypeSale);
		invoiceTypePanel.setCellWidth(invoiceTypeLabel, CELL_WIDTH);
		invoiceTypePanel.setCellHeight(invoiceTypeLabel, CELL_HEIGHT);
		invoiceTypeLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		inventoryLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		invoiceNumberPanel.add(invoiceNumberLabel);
		invoiceNumberPanel.add(invoiceNumberBox);
		invoiceNumberPanel.setCellWidth(invoiceNumberLabel, CELL_WIDTH);
		invoiceNumberPanel.setCellHeight(invoiceNumberLabel, CELL_HEIGHT);
		invoiceNumberLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		invoiceDatePanel.add(invoiceDateBoxLabel);
		invoiceDatePanel.add(invoiceDateBox);
		invoiceDatePanel.setCellWidth(invoiceDateBoxLabel, CELL_WIDTH);
		invoiceDatePanel.setCellHeight(invoiceDateBoxLabel, CELL_HEIGHT);
		invoiceDateBox.setReadOnly(true);
		invoiceDateBoxLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		invoiceCalendarPanel.add(invoiceCalendar);
		invoiceCalendarPanel.center();
		invoiceCalendarPanel.hide();

		transferDatePanel.add(transferDateBoxLabel);
		transferDatePanel.add(transferDateBox);
		transferDatePanel.setCellWidth(transferDateBoxLabel, CELL_WIDTH);
		transferDatePanel.setCellHeight(transferDateBoxLabel, CELL_HEIGHT);
		transferDateBox.setReadOnly(true);
		transferDateBoxLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		transferCalendarPanel.add(transferCalendar);
		transferCalendarPanel.center();
		transferCalendarPanel.hide();

		shippingInfoPanel.add(shippingInfoLabel);
		shippingInfoPanel.add(shippingTablePanel);
		shippingInfoPanel.setCellWidth(shippingInfoLabel, CELL_WIDTH);
		shippingInfoPanel.setCellHeight(shippingInfoLabel, CELL_HEIGHT);
		shippingInfoLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		gallonAmountPanel.add(gallonAmountLabel);
		gallonAmountPanel.add(gallonAmountBox);
		gallonAmountPanel.setCellWidth(gallonAmountLabel, CELL_WIDTH);
		gallonAmountPanel.setCellHeight(gallonAmountLabel, CELL_HEIGHT);
		gallonAmountLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		shipModePanel.add(shipModeLabel);
		shipModePanel.add(shipModeBox);
		shipModePanel.setCellWidth(shipModeLabel, CELL_WIDTH);
		shipModePanel.setCellHeight(shipModeLabel, CELL_HEIGHT);
		shipModePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		svc.getShipModeList(new AsyncCallback<ArrayList<ShipMode>>(){
			public void onFailure(Throwable caught){
				Window.alert("Failed to retrieve Ship Mode List: " +caught.getMessage() );
			}
			public void onSuccess(ArrayList<ShipMode> result){
				shipModeList = result;
				try{
					shipModeBox.addItem("    ");//default value, no ship mode selected
					for (ShipMode mode: shipModeList)
						shipModeBox.addItem(mode.getMode());//build the ShipMode drop down list
				}catch(RuntimeException e){
					Window.alert("Ship Mode List cannot be retrieved!");
				}
			}
		});

		rptdK1Panel.add(rptdK1Label);
		rptdK1Panel.add(rptdK1Box);	
		rptdK1Panel.setCellWidth(rptdK1Label, CELL_WIDTH);
		rptdK1Panel.setCellHeight(rptdK1Label, CELL_HEIGHT);
		rptdK1Panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		rptdK2Panel.add(rptdK2Label);
		rptdK2Panel.add(rptdK2Box);
		rptdK2Panel.setCellWidth(rptdK2Label, CELL_WIDTH);
		rptdK2Panel.setCellHeight(rptdK2Label, CELL_HEIGHT);
		rptdK2Panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		uploadPanel.add(uploadLabel);
		uploadPanel.add(invoicePTDUpload);
		uploadPanel.setCellWidth(uploadLabel, CELL_WIDTH);
		uploadPanel.setCellHeight(uploadLabel, CELL_HEIGHT);
		uploadLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		vPanel.setSize(VPANEL_WIDTH, VPANEL_HEIGHT);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(invoiceTypePanel);
		vPanel.add(inventoryLabel);
		vPanel.add(invoiceNumberPanel);
		vPanel.add(invoiceDatePanel);
		vPanel.add(transferDatePanel);

		vPanel.add(epaPartnerPanel);
		vPanel.add(shipModePanel);
		vPanel.add(gallonAmountPanel);
		vPanel.add(submitButton);
		
		//Sort available
		//vPanel.add(sortAvailableButton);
										
	}
	
	private void addListeners() {
		
		
		
		invoiceTypeBuy.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				invoiceType = "PURCHASE";
				if (vPanel.getWidgetIndex(rptdK1Panel) == -1){
					vPanel.insert(rptdK1Panel, vPanel.getWidgetIndex(submitButton));
					vPanel.insert(rptdK2Panel, vPanel.getWidgetIndex(submitButton));
					vPanel.insert(uploadPanel, vPanel.getWidgetIndex(submitButton));
				}
				if (inventoryLabel.getText()!=null)
					inventoryLabel.setText(null);
			}	
		});
		invoiceTypeSale.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				invoiceType = "SALE";
				if (vPanel.getWidgetIndex(rptdK1Panel) != -1){
					vPanel.remove(rptdK1Panel);
					vPanel.remove(rptdK2Panel);
					vPanel.remove(uploadPanel);
				}

				svc.getInventory(new AsyncCallback<int[]>(){
					public void onFailure(Throwable arg0) {
						Window.alert("Failed to retrieve RIN balance!");
					}
					public void onSuccess(int[] arg0) {
						inventory = arg0;
						if (inventory != null)
							inventoryLabel.setText("Available K1: "+ inventory[0]+" gallons, K2: "+inventory[1]+ " gallons");
						else Window.alert("Inventory is empty!");
					}
				});
			}
		});

		invoiceNumberBox.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender) {
				invoiceNumber = invoiceNumberBox.getText().trim();			
			}
		});

		invoiceDateBox.addFocusListener(new FocusListener(){
			public void onFocus(Widget sender) {
				invoiceCalendarPanel.show();
			}
			public void onLostFocus(Widget sender) {}
		});

		invoiceCalendar.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				int month =invoiceCalendar.getMonth()+1;
				invoiceDate = invoiceCalendar.getYear()+"-"+month+"-"+invoiceCalendar.getDay();
				invoiceDateBox.setText(invoiceDate);
				invoiceCalendarPanel.hide();
			}
		});

		transferDateBox.addFocusListener(new FocusListener(){
			public void onFocus(Widget sender) {
				transferCalendarPanel.show();
			}
			public void onLostFocus(Widget sender) {}
		});

		transferCalendar.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				int month =transferCalendar.getMonth()+1;
				transferDate = transferCalendar.getYear()+"-"+month+"-"+transferCalendar.getDay();
				transferDateBox.setText(transferDate);
				transferCalendarPanel.hide();
			}
		});
		//This module uses epa number to locate counter party information
		/*		epaNumberBox.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender) {
				epaNumber = epaNumberBox.getText().trim();
				if (epaNumber.length()!=4){
					Window.alert("EPA # is 4 digits long!");
				}
				else{
					svc.getCounterParty(epaNumber, new AsyncCallback() {
						public void onFailure(Throwable caught) {
							Window.alert("Failed to retrieve Counter Party Information!");
						}
						public void onSuccess(Object result) {
							try{
								counterParty = (CounterParty)result;
								epaNumberPanel.add(new Label(counterParty.getFullName()));
							}catch (ClassCastException e){
								Window.alert("No such Counter Party available in Database! Please contact Database Administrator!");
							}
						}
					});
				}
			}
		});*/

		//Bring up the list of counter party names
		//		final AsyncCallback<CounterParty> callback = new AsyncCallback<CounterParty>(){
		//		public void onFailure(Throwable caught){
		//		Window.alert("Failed to get Counter Party Information!");
		//		}
		//		public void onSuccess(CounterParty counterParty){
		//		Window.alert("EPA!!");
		//		try{
		//		RINInvoiceEntryPanel.this.counterParty = counterParty;
		//		epaNumberLabel.setText("EPA#: "+String.valueOf(counterParty.getEpaNo()));
		//		}catch(NullPointerException e){
		//		Window.alert("No EPA exists for this Counter Party!");
		//		}
		//		}
		//		};
		/*counterPartySuggestionPanel.counterPartyNameBox.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender){
				counterPartySuggestionPanel.getEPAButton.setEnabled(true);
			}
		});

		counterPartySuggestionPanel.counterPartyNameBox.addEventHandler(new SuggestionHandler(){
			public void onSuggestionSelected(SuggestionEvent event) {
				counterPartySuggestionPanel.getEPAButton.setEnabled(true);
				counterPartySuggestionPanel.epaNumberLabel.setText("EPA #: ");
			}
		});

		counterPartySuggestionPanel.getEPAButton.addClickListener(new ClickListener(){
		 *//**
		 * Gets the Counter Party that matches the name provided
		 *//*
			public void onClick(Widget sender) {
				counterPartySuggestionPanel.counterParty = null;
				counterPartySuggestionPanel.counterParty = getCounterParty(counterPartySuggestionPanel.counterPartyNameBox.getText().trim());
				if (counterPartySuggestionPanel.counterParty == null){
					Window.alert("No EPA# for this counter party!");
				}else{
					counterPartySuggestionPanel.getEPAButton.setEnabled(false);
					counterPartySuggestionPanel.epaNumberLabel.setText("EPA #: "+String.valueOf(counterPartySuggestionPanel.counterParty.getEpaNo()));
				}
			}

			private CounterParty getCounterParty(String counterPartyName) {
				for (CounterParty c: counterPartySuggestionPanel.counterPartyList){
					if (counterPartyName.equalsIgnoreCase(c.getFullName())){
						return c;
					}
				}
				return null;
			}
		});

		  *//**
		  * Once Counter Party name is changed, user needs to reverify the Counter Party and get the EPA
		  *//*
		counterPartySuggestionPanel.counterPartyNameBox.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender) {
				counterPartySuggestionPanel.getEPAButton.setEnabled(true);
				counterPartySuggestionPanel.epaNumberLabel.setText("EPA #: ");
			}
		});*/

		gallonAmountBox.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender) {
				try{
					gallonAmount = Integer.valueOf(gallonAmountBox.getText());//Throws NumberFormatException if input is not number
					if (gallonAmount < 1)
						throw new NumberFormatException();
				}catch (NumberFormatException e){//catches either exception
					Window.alert("Invalid Gallon Amount: " + e.getMessage());
				}
			}
		});

		shipModeBox.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender) {
				int shipModeIndex;
				shipModeIndex = shipModeBox.getSelectedIndex() - 1;
				if (shipModeIndex >= 0){// is -1 when empty is selected
					shipMode = shipModeList.get(shipModeIndex);
					//					Window.alert("Ship Mode is: "+shipMode.getMode());
				}
				else shipMode = null;
				if (!shipMode.getMode().equalsIgnoreCase("FIN")){
					shippingEntryTable = new ShippingEntryTable(shipMode);
					shippingTablePanel.setWidget(shippingEntryTable);
					vPanel.insert(shippingInfoPanel, vPanel.getWidgetIndex(gallonAmountPanel));
				}else {
					vPanel.remove(shippingInfoPanel);
				}
			}
		});

		rptdK1Box.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender) {
				rptdK1 = rptdK1Box.getText().trim();
			}
		});

		rptdK2Box.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender) {
				rptdK2 = rptdK2Box.getText().trim();
			}
		});

		sortAvailableButton.addClickListener(new ClickListener(){
			
			public void onClick(Widget sender) {
				
				if (!validateInput()){
					return;
				}			
				
				final RINsAvailablePanel sortPanel = new RINsAvailablePanel(invoice);
				
				sortPanel.addPopupListener(new PopupListener(){
																							
					public void onPopupClosed(PopupPanel arg0, boolean arg1) {
						sortAvailableButton.setEnabled(true);						
					}					
					
				});
								
				sortAvailableButton.setEnabled(false);
				
				sortPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			          public void setPosition(int offsetWidth, int offsetHeight) {
			            int left = (Window.getClientWidth() - offsetWidth) / 3;
			            int top = (Window.getClientHeight() - offsetHeight) / 3;
			            sortPanel.setPopupPosition(left, top);
			          }
			        });
				
			
								
			}

			private boolean validateInput() {
				
				if (shipMode == null){
					Window.alert("Please select Ship Mode!");
					return false;
				}
				
				try{
					buildInvoice();
				}catch (RINException e){
					Window.alert(e.getMessage());
					return false;
				}
				
				return true;
			}
		}
		);
		
		submitButton.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				if (Window.confirm("Are you sure all information is correct?")){
					if (!validateInput()){
						return;
					}
					submitButton.setEnabled(false);
					if (invoice.getInvoiceType().equalsIgnoreCase("PURCHASE")){
						RINInvoiceEntryPanel.this.invoicePTDUpload.submit();
					}else{//SALE invoice
						svc.sellRINs(invoice, new AsyncCallback<Void>(){
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
								submitButton.setEnabled(true);
							}
							public void onSuccess(Void result) {
								if (shipMode.getMode().equalsIgnoreCase("FIN") && inventory[1] < gallonAmount){
									Window.alert("Sale invoice maybe pending as insufficient K2 in inventory");
								}else if (inventory[0]+inventory[1] < gallonAmount){
									Window.alert("Sale invoice maybe pending as insufficient RIN in inventory");
								}else Window.alert("RIN sales successful!");
								rootPanel.clear();
								rootPanel.add(new LogisticsStartPanel());
							}
						}) ;
					}
				}
			}

			/**
			 * Checks all the entries of the invoice, builds the invoice fields
			 * including invoice path, rptd path and csv path
			 * @return <code>true</code> if validated
			 */
			private boolean validateInput() {
				if (invoiceNumber.equalsIgnoreCase("") ){
					Window.alert("Invoice Number is missing!");
					return false;
				}
				if (invoiceDate.equalsIgnoreCase("")){
					Window.alert("Invoice Date is missing!");
					return false;
				}
				if (transferDate.equalsIgnoreCase("")){
					Window.alert("Transfer Date is missing!");
					return false;
				}
				if (epaPartnerPanel.getGetEPAButton().isEnabled()){
					Window.alert("Please get EPA # for this EPA Partner!");
					return false;
				}
				if (shipMode == null){
					Window.alert("Please select Ship Mode!");
					return false;
				}
				if (gallonAmount < 1){
					Window.alert("Wrong Gallon Amount!");
					return false;
				}
				if (invoiceType.equalsIgnoreCase("")){
					Window.alert("Please select Invoice Type!");
					return false;
				}
				if (invoiceType.equalsIgnoreCase("PURCHASE")){
					if (!invoicePTDUpload.getFilename().endsWith(".pdf")&&!invoicePTDUpload.getFilename().endsWith(".PDF")){
						Window.alert("Please upload Invoice-PTD pdf file that contains RINs!");
						return false;
					}
					if (rptdK1.equalsIgnoreCase("") && rptdK2.equalsIgnoreCase("")){
						Window.alert("Please provide RPTD number!");
						return false;
					}
				}

				if (!shipMode.getMode().equalsIgnoreCase("FIN")){//have to have BOL if not FIN only shipmode
					bols = new ArrayList<BOLInfo>();
					int bolTotalAmount = 0;
					for (int i = 0; i < shippingEntryTable.bolRows.size(); i ++){
						try {
							bols.add(shippingEntryTable.bolRows.get(i).bol);
							bols.get(i).verifyBOLEntry();
							bolTotalAmount += bols.get(i).getQuantity();
						}catch (RINException e){
							Window.alert("BOL Info # "+ ++i +": "+e.getMessage());
							return false;
						}
					}
					if (bolTotalAmount != gallonAmount){
						Window.alert("BOL total amount doesn't match with expected gallon Amount!");
						return false;
					}
				}
				try{
					buildInvoice();
					invoice.validate();//ptd path, rptd path and csv path are generated
					invoicePTDUpload.generateInvoicePath(invoice);//invoicePTDUpload now has invoice path
				}catch (RINException e){
					Window.alert(e.getMessage());
					return false;
				}
				return true;
			}
		});

		// Add an event handler to the form.
		invoicePTDUpload.addFormHandler(new FormHandler() {
			public void onSubmit(FormSubmitEvent event) {}

			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				// When the form submission is successfully completed, this event is
				// fired. Assuming the service returned a response of type text/html,
				// we can get the result text here (see the FormPanel documentation for
				// further explanation).
					try{
						if (event.getResults().equalsIgnoreCase("SUCCESS")){
							rootPanel.clear();
							RINsEntryPanel rep = new RINsEntryPanel(invoice);
							rep.setTitleFunction("Enter");
							rootPanel.add(rep);
						}				
						else Window.alert(event.getResults());
					}catch (StringIndexOutOfBoundsException e){
						Window.alert("File upload failed! ");
						submitButton.setEnabled(true);
					}
			}
		});
	}
			private void buildInvoice() {
				invoice.setInvoiceType(invoiceType);
				invoice.setInvoiceNo(invoiceNumber);
				invoice.setInvoiceDate(invoiceDate);
				invoice.setTransferDate(transferDate);
				invoice.setEpaPartner(epaPartnerPanel.getEPAPartner());
				invoice.setMode(shipMode);
				invoice.addBOLInfo(bols);
				invoice.setExpectedGallons(gallonAmount);
				if (invoiceType.equalsIgnoreCase("PURCHASE")){
					invoice.setRptdK1(rptdK1);
					invoice.setRptdK2(rptdK2);
					invoice.setRptdPath(ptdPath);
				}else if (invoiceType.equalsIgnoreCase("SALE")){
					invoice.setInvoiceType("SALE");
				}
			}

	public String getInvoiceFileAddress() {
		return ptdPath;
	}

	/**
	 * Class for the ROL entry table
	 * @author sxycode
	 *
	 */
	private class ShippingEntryTable extends VerticalPanel{
		ArrayList<ShippingEntryRow> bolRows = new ArrayList<ShippingEntryRow>();
		ShipMode shipMode;
		ShippingEntryTable(ShipMode shipMode){
			this.shipMode = shipMode;
			addRow();
		}
		private void addRow() {
			final ShippingEntryRow row = new ShippingEntryRow(shipMode);
			bolRows.add(row);
			this.add(row);
			if (bolRows.size()==1)
				row.deleteButton.setEnabled(false);//prevents deletion of the only row available
			if (bolRows.size()>1){
				for (int i = 0; i < bolRows.size()-1; i ++)	//don't enable the delete button of the last row
					bolRows.get(i).deleteButton.setEnabled(true);	
			}
			row.moreButton.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					int rowIndex = bolRows.indexOf(row);//gets updated rowIndex
					row.moreButton.setEnabled(false);
					if (rowIndex == bolRows.size()-1){
						ShippingEntryTable.this.addRow();
					}
				}
			});	
			row.deleteButton.addClickListener(new ClickListener(){
				public void onClick(Widget arg0) {
					if (Window.confirm("Are you sure you want to delete this BOL?")){//confirm before delete
						ShippingEntryTable.this.remove(row);
						bolRows.remove(row);
						if (bolRows.size() == 1){
							bolRows.get(0).deleteButton.setEnabled(false);//prevents deleting the only row left
						}
						bolRows.get(bolRows.size()-1).moreButton.setEnabled(true);//enable the "Next" button on the last row
					}
				}
			});
		}
//		public void setShipMode(ShipMode shipMode){
//			this.shipMode = shipMode;
//			for (BOLEntryRow bolRow: bolRows){
//				bolRow.setShipMode(shipMode);
//			}
//		}
	}

	/**
	 * class for a BOL Entry Row. Each row has an BOLInfo object bol
	 * to store the BOL information
	 * @author sxycode
	 *
	 */
	private class ShippingEntryRow extends HorizontalPanel{
		ShipMode shipMode;
		String bolText;
		String loadNumberText;
		final String QUANTITY_TEXT = "Quantity";
		TextBox bolNumberBox = new TextBox();
		TextBox quantityBox = new TextBox();
		TextBox loadNumberBox = new TextBox();
		Button moreButton = new Button("More...");
		Button deleteButton = new Button("Delete");
		BOLInfo bol = new BOLInfo();
		ShippingEntryRow(ShipMode shipMode){
			this.shipMode = shipMode;
			quantityBox.setText(QUANTITY_TEXT);
			bolNumberBox.setWidth("100px");
			quantityBox.setWidth("70px");
			loadNumberBox.setWidth("100px");
			this.add(bolNumberBox);
			this.add(quantityBox);
			this.add(loadNumberBox);
			this.add(moreButton);
			this.add(deleteButton);
			addListeners();
		}
		
		private void addListeners() {
			if (shipMode.getMode().equalsIgnoreCase("RAIL")){
				bolText = "BOL #";
				loadNumberText = "LOAD TICKET";
			}else if (shipMode.getMode().equalsIgnoreCase("BARGE")){
				bolText = "COA #";
				loadNumberText = "LOAD TICKET";
			}else if (shipMode.getMode().equalsIgnoreCase("TRUCK")){
				bolText = "BOL #";
				loadNumberText = "LOAD TICKET";
			}else if (shipMode.getMode().equalsIgnoreCase("ITT")){
				bolText = "TRANSFER #";
				loadNumberText = "TANK #";
			}
				
			addBOLNumberBoxListeners();
			addQuantityBoxListeners();
			addLoadNumberListeners();
		}
		/**
		 * Displays a default text in a text box before modification.
		 * @param textBox
		 * @param DEFAULT_TEXT
		 */

		private void addBOLNumberBoxListeners() {
			//displays title for bol number box
			final String BOL_TEXT = bolText;
			bolNumberBox.setText(BOL_TEXT);
			UIs.addDefaultTextFocusListener(bolNumberBox, BOL_TEXT);

			bolNumberBox.addChangeListener(new ChangeListener(){
				public void onChange(Widget arg0) {
					String entry = bolNumberBox.getText().trim();
					if (!entry.isEmpty() && !entry.equalsIgnoreCase(BOL_TEXT))
						bol.setBolNumber(entry);
					else bolNumberBox.setText(BOL_TEXT);
				}
			});

		}

		private void addQuantityBoxListeners() {
			UIs.addDefaultTextFocusListener(quantityBox, QUANTITY_TEXT);

			quantityBox.addChangeListener(new ChangeListener(){
				public void onChange(Widget arg0) {
					try{
						String entry = quantityBox.getText().trim();
						if (!entry.isEmpty() && !entry.equalsIgnoreCase(QUANTITY_TEXT))
							bol.setQuantity(Integer.valueOf(entry));
						else quantityBox.setText(QUANTITY_TEXT);
					}catch (NumberFormatException e){
						Window.alert("Quantity must be integer: " + e.getMessage());
					}
				}
			});
		}

		private void addLoadNumberListeners(){
			final String LOAD_NUMBER_TEXT = loadNumberText;
			loadNumberBox.setText(LOAD_NUMBER_TEXT);
			UIs.addDefaultTextFocusListener(loadNumberBox, LOAD_NUMBER_TEXT);

			loadNumberBox.addChangeListener(new ChangeListener(){
				public void onChange(Widget arg0) {
					String entry = loadNumberBox.getText().trim();
					if (!entry.isEmpty() && !entry.equalsIgnoreCase(LOAD_NUMBER_TEXT))
						bol.setLoadNumber(entry);
					else loadNumberBox.setText(LOAD_NUMBER_TEXT);
				}
			});
		}
		/**
		 * Used to verify each bol entry when clicking "submit" button
		 * @return <code>true</code> if BOL entry is valid
		 */
		public BOLInfo getBOL(){
			return bol;
		}
	}


}

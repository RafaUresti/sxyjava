package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CounterPartyProfileView extends Composite {

	private AdminViewController view;
	
	public CounterPartyProfileView(AdminViewController view) {

		final VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		verticalPanel.setSize("100%", "100%");
		this.view= view;
		

		final TabPanel tabPanel = new TabPanel();
		tabPanel.setStyleName("gwt-TabBar");

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		tabPanel.add(verticalPanel_1, "Counter Party Profile");
		verticalPanel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final FlexTable flexTable = new FlexTable();
		verticalPanel_1.add(flexTable);
		verticalPanel_1.setCellHorizontalAlignment(flexTable, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidth("90%");

		final Label fullLegalNameLabel = new Label("Full Legal Name");
		flexTable.setWidget(0, 0, fullLegalNameLabel);

		final TextBox textBox = new TextBox();
		flexTable.setWidget(0, 1, textBox);

		final Label streetAddressLabel = new Label("Street Address");
		flexTable.setWidget(1, 0, streetAddressLabel);

		final Label cityLabel = new Label("City");
		flexTable.setWidget(2, 0, cityLabel);

		final Label stateLabel = new Label("State");
		flexTable.setWidget(3, 0, stateLabel);

		final Label zipCodeLabel = new Label("Zip Code");
		flexTable.setWidget(4, 0, zipCodeLabel);

		final TextArea textArea = new TextArea();
		flexTable.setWidget(1, 1, textArea);

		final TextBox textBox_1 = new TextBox();
		flexTable.setWidget(2, 1, textBox_1);
		textBox_1.setWidth("100%");

		final ListBox listBox = new ListBox();
		flexTable.setWidget(3, 1, listBox);

		final TextBox textBox_2 = new TextBox();
		flexTable.setWidget(4, 1, textBox_2);

		final CheckBox bankCheckBox = new CheckBox();
		flexTable.setWidget(5, 0, bankCheckBox);
		bankCheckBox.setText("Bank");

		final CheckBox storageCheckBox = new CheckBox();
		flexTable.setWidget(6, 0, storageCheckBox);
		storageCheckBox.setText("Storage");

		final VerticalPanel verticalPanel_2 = new VerticalPanel();
		tabPanel.add(verticalPanel_2, "RelationShip Type");
		verticalPanel_2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final FlexTable flexTable_1 = new FlexTable();
		verticalPanel_2.add(flexTable_1);
		flexTable_1.setWidth("90%");

		final Label rinCompanyRegistrationLabel = new Label("RIN Company Registration Id.");
		flexTable_1.setWidget(1, 0, rinCompanyRegistrationLabel);

		final TextBox textBox_3 = new TextBox();
		flexTable_1.setWidget(1, 1, textBox_3);

		final Label rinFacilityRegistrationLabel = new Label("RIN Facility Registration id.");
		flexTable_1.setWidget(2, 0, rinFacilityRegistrationLabel);

		final TextBox textBox_4 = new TextBox();
		flexTable_1.setWidget(2, 1, textBox_4);

		final Label label_9 = new Label("");
		flexTable_1.setWidget(3, 0, label_9);
		flexTable_1.getCellFormatter().setHeight(3, 0, "50");

		final Label epaInformationLabel = new Label("EPA Information");
		flexTable_1.setWidget(0, 0, epaInformationLabel);


		final Label bankAccountwireLabel = new Label("Bank Account (Wire) Information");
		flexTable_1.setWidget(4, 0, bankAccountwireLabel);

		final Label bankNoLabel = new Label("Bank No.");
		flexTable_1.setWidget(5, 0, bankNoLabel);

		final TextBox textBox_5 = new TextBox();
		flexTable_1.setWidget(5, 1, textBox_5);

		final Label bsbNoLabel = new Label("BSB No.");
		flexTable_1.setWidget(6, 0, bsbNoLabel);

		final TextBox textBox_6 = new TextBox();
		flexTable_1.setWidget(6, 1, textBox_6);

		final Label accountNoLabel = new Label(" Account No.");
		flexTable_1.setWidget(7, 0, accountNoLabel);

		final TextBox textBox_7 = new TextBox();
		flexTable_1.setWidget(7, 1, textBox_7);

		final Label label = new Label("");
		verticalPanel_2.add(label);
		label.setHeight("50");
		
		tabPanel.selectTab(0);
		verticalPanel.add(tabPanel);
		tabPanel.setSize("100%", "100%");


		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		tabPanel.add(horizontalPanel, "Vendor");
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final VerticalPanel verticalPanel_3 = new VerticalPanel();
		horizontalPanel.add(verticalPanel_3);
		horizontalPanel.setCellWidth(verticalPanel_3, "50%");
		
		final FlexTable flexTable_3 = new FlexTable();
		verticalPanel_3.add(flexTable_3);
		flexTable_3.setWidth("100%");

		final Label specialVendorLabel = new Label("Special Vendor");
		flexTable_3.setWidget(0, 0, specialVendorLabel);

		final CheckBox specialVendorCheckBox = new CheckBox();
		flexTable_3.setWidget(1, 0, specialVendorCheckBox);
		flexTable_3.getFlexCellFormatter().setColSpan(1, 0, 2);
		specialVendorCheckBox.setText("Special Vendor");

		final CheckBox brokerCheckBox = new CheckBox();
		flexTable_3.setWidget(2, 1, brokerCheckBox);
		brokerCheckBox.setText("Broker");

		final CheckBox inspectionCompanyCheckBox = new CheckBox();
		flexTable_3.setWidget(3, 1, inspectionCompanyCheckBox);
		inspectionCompanyCheckBox.setText("Inspection Company");

		final CheckBox freightCarrierCheckBox = new CheckBox();
		flexTable_3.setWidget(4, 1, freightCarrierCheckBox);
		freightCarrierCheckBox.setText("Freight Carrier");
		
		final Label labeln = new Label("");
		verticalPanel_3.add(labeln);
		labeln.setHeight("15");
		

		final FlexTable flexTable_4 = new FlexTable();
		verticalPanel_3.add(flexTable_4);
		flexTable_4.setWidth("100%");

		final Label label_1 = new Label("Expense G/L account");
		flexTable_4.setWidget(0, 0, label_1);
		flexTable_4.getFlexCellFormatter().setColSpan(0, 0, 2);
		
		final Label glAccountCodeLabel = new Label("G/L account Code");
		flexTable_4.setWidget(1, 0, glAccountCodeLabel);
		flexTable_4.getCellFormatter().setWidth(1, 0, "50%");
		
		final TextBox textBox_8 = new TextBox();
		flexTable_4.setWidget(1, 1, textBox_8);
		textBox_8.setWidth("90%");
		
		final Label labelnn = new Label("");
		verticalPanel_3.add(labelnn);
		labelnn.setHeight("15");
		
		final FlexTable flexTable_6 = new FlexTable();
		verticalPanel_3.add(flexTable_6);
		flexTable_6.setWidth("100%");

		final Label label_2 = new Label("Default payment terms");
		flexTable_6.setWidget(0, 0, label_2);

		final Label label_3 = new Label("Barge");
		flexTable_6.setWidget(1, 0, label_3);

		final Label label_4 = new Label("In Tank Transfer");
		flexTable_6.setWidget(2, 0, label_4);

		final Label label_5 = new Label("Truck");
		flexTable_6.setWidget(3, 0, label_5);

		final Label label_6 = new Label("Other");
		flexTable_6.setWidget(4, 0, label_6);

		final ListBox listBox_1 = new ListBox();
		flexTable_6.setWidget(1, 1, listBox_1);

		final ListBox listBox_2 = new ListBox();
		flexTable_6.setWidget(2, 1, listBox_2);

		final ListBox listBox_3 = new ListBox();
		flexTable_6.setWidget(3, 1, listBox_3);

		final ListBox listBox_4 = new ListBox();
		flexTable_6.setWidget(4, 1, listBox_4);
		
		final Label labelnnn = new Label("");
		verticalPanel_3.add(labelnnn);
		labelnnn.setHeight("15");

		final FlexTable flexTable_7 = new FlexTable();
		verticalPanel_3.add(flexTable_7);
		flexTable_7.setWidth("100%");

		final Label holdStatusLabel = new Label("Hold Status");
		flexTable_7.setWidget(0, 0, holdStatusLabel);

		final Label statusLabel = new Label("Status");
		flexTable_7.setWidget(1, 0, statusLabel);

		final Label reasonLabel = new Label("reason");
		flexTable_7.setWidget(2, 0, reasonLabel);

		final ListBox listBox_5 = new ListBox();
		flexTable_7.setWidget(1, 1, listBox_5);

		final TextArea textArea_1 = new TextArea();
		flexTable_7.setWidget(2, 1, textArea_1);
		
		
		final VerticalPanel verticalPanel_4 = new VerticalPanel();
		horizontalPanel.add(verticalPanel_4);
		verticalPanel_4.setWidth("100%");
		horizontalPanel.setCellWidth(verticalPanel_4, "50%");

		final Label notesLabel = new Label("Notes");
		verticalPanel_4.add(notesLabel);

		final TextArea subjectPriorityDateTextArea = new TextArea();
		verticalPanel_4.add(subjectPriorityDateTextArea);
		subjectPriorityDateTextArea.setText("subject, priority, date, time, user");
		verticalPanel_4.setCellHorizontalAlignment(subjectPriorityDateTextArea, HasHorizontalAlignment.ALIGN_LEFT);
		subjectPriorityDateTextArea.setSize("90%", "250");

		final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		tabPanel.add(horizontalPanel_1, "Customer");
		

		final VerticalPanel verticalPanel_5 = new VerticalPanel();
		horizontalPanel_1.add(verticalPanel_5);
		verticalPanel_5.setWidth("100%");
		horizontalPanel_1.setCellWidth(verticalPanel_5, "50%");
		
		final VerticalPanel verticalPanel_6 = new VerticalPanel();
		horizontalPanel_1.add(verticalPanel_6);
		verticalPanel_6.setWidth("100%");
		horizontalPanel_1.setCellWidth(verticalPanel_6, "50%");

		final FlexTable flexTable_2 = new FlexTable();
		verticalPanel_5.add(flexTable_2);
		flexTable_2.setWidth("100%");
		
		final Label DUNsLabel = new Label("DUNs");
		flexTable_2.setWidget(0, 0, DUNsLabel);
		flexTable_2.getCellFormatter().setWidth(0, 0, "50%");
		
		final Label DUNsNoLabel = new Label("DUNs No.");
		flexTable_2.setWidget(1, 0, DUNsNoLabel);
		flexTable_2.getCellFormatter().setWidth(1, 0, "50%");

		final TextBox textBox1 = new TextBox();
		flexTable_2.setWidget(1, 1, textBox1);
		
		final Label Labela = new Label();
		flexTable_2.setWidget(2, 0, Labela);
		Labela.setHeight("15");
		flexTable_2.getCellFormatter().setWidth(2, 0, "50%");
		
		final Label CreditLimitLabel = new Label("Credit Limit");
		flexTable_2.setWidget(3, 0, CreditLimitLabel);
		flexTable_2.getCellFormatter().setWidth(3, 0, "50%");
		
		final Label CreditLimitLabeln = new Label("Credit Limit");
		flexTable_2.setWidget(4, 0, CreditLimitLabeln);
		flexTable_2.getCellFormatter().setWidth(4, 0, "50%");
		
		final TextBox textBox2 = new TextBox();
		flexTable_2.setWidget(4, 1, textBox2);
		
		final Label Labelb = new Label();
		flexTable_2.setWidget(5, 0, Labelb);
		Labelb.setHeight("15");
		flexTable_2.getCellFormatter().setWidth(5, 0, "50%");
		
		final Label StoredfinancialsLabel = new Label("Stored financials");
		flexTable_2.setWidget(6, 0, StoredfinancialsLabel);
		flexTable_2.getFlexCellFormatter().setColSpan(6, 0, 2);
		flexTable_2.getCellFormatter().setWidth(6, 0, "50%");
		
		final TextArea StoredfinancialsTextArea = new TextArea();
		flexTable_2.setWidget(7, 0, StoredfinancialsTextArea);
		StoredfinancialsTextArea.setSize("100%", "150");
		flexTable_2.getFlexCellFormatter().setColSpan(7,0,2);
		flexTable_2.getCellFormatter().setWidth(7, 0, "50%");
		
		final Label Labeld = new Label();
		flexTable_2.setWidget(8, 0, Labeld);
		Labeld.setHeight("15");
		flexTable_2.getCellFormatter().setWidth(8, 0, "50%");
		
		final Label IncomingGLAccountLabel = new Label("Incoming G/L Account");
		flexTable_2.setWidget(9, 0, IncomingGLAccountLabel);
		flexTable_2.getCellFormatter().setWidth(9, 0, "50%");
		flexTable_2.getFlexCellFormatter().setColSpan(9,0,2);
		
		final Label GLAccountCodeLabel = new Label("Account Code");
		flexTable_2.setWidget(10, 0, GLAccountCodeLabel);
		flexTable_2.getCellFormatter().setWidth(10, 0, "50%");
		
		final TextBox textBox3 = new TextBox();
		flexTable_2.setWidget(10, 1, textBox3);

		
		final Label NotesLabel = new Label("Notes");
		verticalPanel_6.add(NotesLabel);
		
		final TextArea subjectPriorityDateTextArea1 = new TextArea();
		verticalPanel_6.add(subjectPriorityDateTextArea1);
		subjectPriorityDateTextArea1.setText("subject, priority, date, time, user");
		verticalPanel_6.setCellHorizontalAlignment(subjectPriorityDateTextArea1, HasHorizontalAlignment.ALIGN_LEFT);
		subjectPriorityDateTextArea1.setSize("90%", "250");

		final VerticalPanel verticalPanel_7 = new VerticalPanel();
		tabPanel.add(verticalPanel_7, "Bank Information");
		
		verticalPanel_7.setWidth("100%");
		verticalPanel_7.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final FlexTable flexTable1 = new FlexTable();
		verticalPanel_7.add(flexTable1);
		flexTable1.setWidth("100%");
		verticalPanel_7.setCellHorizontalAlignment(flexTable1, HasHorizontalAlignment.ALIGN_CENTER);

		final Label label1 = new Label("Account No(s)");
		flexTable1.setWidget(0, 0, label1);

		final Label label_11 = new Label("");
		flexTable1.setWidget(1, 0, label_11);
		label_11.setHeight("15");

		final Button addButton = new Button();
		flexTable1.setWidget(2, 0, addButton);
		flexTable1.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		addButton.setText("Add");

		final Button deleteButton = new Button();
		flexTable1.setWidget(2, 1, deleteButton);
		flexTable1.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER);
		deleteButton.setText("Delete");

		final Label label_21 = new Label("");
		flexTable1.setWidget(3, 0, label_21);
		label_21.setHeight("15");

		final Label accountNoLabel1 = new Label("Account No");
		flexTable1.setWidget(4, 0, accountNoLabel1);
		flexTable1.getCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);

		final TextBox textBox11 = new TextBox();
		flexTable1.setWidget(4, 1, textBox11);
		flexTable1.getCellFormatter().setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_CENTER);
		textBox11.setWidth("90%");

		final VerticalPanel verticalPanel_8 = new VerticalPanel();
		tabPanel.add(verticalPanel_8, "Contact Info");

		final FlexTable flexTable11 = new FlexTable();
		verticalPanel_8.add(flexTable11);
		
		flexTable11.setWidth("90%");
		verticalPanel_8.setCellHorizontalAlignment(flexTable11, HasHorizontalAlignment.ALIGN_CENTER);
		
		final Label contactsLabel = new Label("Contacts");
		flexTable11.setWidget(0, 0, contactsLabel);
		flexTable11.getCellFormatter().setWidth(0, 0, "20%");
		
		final Button addNewButton = new Button();
		flexTable11.setWidget(1, 0, addNewButton);
		addNewButton.setText("Add New");
		flexTable11.getCellFormatter().setWidth(1, 0, "20%");
		flexTable11.getFlexCellFormatter().setColSpan(1, 0, 2);
		
		final Label creditLabel = new Label("Credit");
		flexTable11.setWidget(2, 0, creditLabel);
		flexTable11.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable11.getCellFormatter().setWidth(2, 0, "20%");

		final TextBox textBox111 = new TextBox();
		flexTable11.setWidget(2, 1, textBox111);
		textBox111.setWidth("100%");

		final TextBox textBox_11 = new TextBox();
		flexTable11.setWidget(2, 2, textBox_11);
		textBox_11.setWidth("100%");

		final TextBox textBox_21 = new TextBox();
		flexTable11.setWidget(2, 3, textBox_21);
		textBox_21.setWidth("100%");

		final TextBox textBox_31 = new TextBox();
		flexTable11.setWidget(2, 4, textBox_31);
		textBox_31.setWidth("100%");
		
		final Label traderLabel = new Label("Trader");
		flexTable11.setWidget(3, 0, traderLabel);
		flexTable11.getCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable11.getCellFormatter().setWidth(3, 0, "20%");

		final TextBox textBox_41 = new TextBox();
		flexTable11.setWidget(3, 1, textBox_41);
		textBox_41.setWidth("100%");

		final TextBox textBox_51 = new TextBox();
		flexTable11.setWidget(3, 2, textBox_51);
		textBox_51.setWidth("100%");

		final TextBox textBox_61 = new TextBox();
		flexTable11.setWidget(3, 3, textBox_61);
		textBox_61.setWidth("100%");

		final TextBox textBox_71 = new TextBox();
		flexTable11.setWidget(3, 4, textBox_71);
		textBox_71.setWidth("100%");

		final Label operationLabel = new Label("Operation");
		flexTable11.setWidget(4, 0, operationLabel);
		flexTable11.getCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable11.getCellFormatter().setWidth(4, 0, "20%");

		final TextBox textBox_81 = new TextBox();
		flexTable11.setWidget(4, 1, textBox_81);
		textBox_81.setWidth("100%");

		final TextBox textBox_9 = new TextBox();
		flexTable11.setWidget(4, 2, textBox_9);
		textBox_9.setWidth("100%");

		final TextBox textBox_10 = new TextBox();
		flexTable11.setWidget(4, 3, textBox_10);
		textBox_10.setWidth("100%");

		final TextBox textBox_111 = new TextBox();
		flexTable11.setWidget(4, 4, textBox_111);
		textBox_111.setWidth("100%");

		final Label contactAdminLabel = new Label("Contact Admin");
		flexTable11.setWidget(5, 0, contactAdminLabel);
		flexTable11.getCellFormatter().setHorizontalAlignment(5, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable11.getCellFormatter().setWidth(5, 0, "20%");

		final TextBox textBox_12 = new TextBox();
		flexTable11.setWidget(5, 1, textBox_12);
		textBox_12.setWidth("100%");

		final TextBox textBox_13 = new TextBox();
		flexTable11.setWidget(5, 2, textBox_13);
		textBox_13.setWidth("100%");

		final TextBox textBox_14 = new TextBox();
		flexTable11.setWidget(5, 3, textBox_14);
		textBox_14.setWidth("100%");

		final TextBox textBox_15 = new TextBox();
		flexTable11.setWidget(5, 4, textBox_15);
		textBox_15.setWidth("100%");

		final Label schedulingLabel = new Label("Scheduling");
		flexTable11.setWidget(6, 0, schedulingLabel);
		flexTable11.getCellFormatter().setHorizontalAlignment(6, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable11.getCellFormatter().setWidth(6, 0, "20%");

		final TextBox textBox_16 = new TextBox();
		flexTable11.setWidget(6, 1, textBox_16);
		textBox_16.setWidth("100%");
		
		final TextBox textBox_17 = new TextBox();
		flexTable11.setWidget(6, 2, textBox_17);
		textBox_17.setWidth("100%");

		final TextBox textBox_18 = new TextBox();
		flexTable11.setWidget(6, 3, textBox_18);
		textBox_18.setWidth("100%");

		final TextBox textBox_19 = new TextBox();
		flexTable11.setWidget(6, 4, textBox_19);
		textBox_19.setWidth("100%");
		

		
		tabPanel.selectTab(0);

		
	}

}

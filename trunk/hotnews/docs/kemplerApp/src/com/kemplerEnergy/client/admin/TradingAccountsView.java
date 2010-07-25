package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TradingAccountsView extends Composite {
	private AdminViewController view;
	public TradingAccountsView(AdminViewController view) {
		this.view = view;

		final TabPanel tabPanel = new TabPanel();
		initWidget(tabPanel);
		tabPanel.setWidth("100%");

		final VerticalPanel verticalPanel = new VerticalPanel();
		tabPanel.add(verticalPanel, "Borker");
		verticalPanel.setWidth("100%");
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		flexTable.setWidth("100%");
		verticalPanel.setCellHorizontalAlignment(flexTable, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellHeight(flexTable, "50%");
		verticalPanel.setCellWidth(flexTable, "100%");

		final Label label_1 = new Label("Enter Text");
		flexTable.setWidget(0, 0, label_1);

		final Label label_2 = new Label("Clearport User Name");
		flexTable.setWidget(1, 0, label_2);

		final Label label_3 = new Label("Clearport User Name");
		flexTable.setWidget(2, 0, label_3);

		final TextBox textBox = new TextBox();
		flexTable.setWidget(1, 1, textBox);
		textBox.setWidth("100%");

		final TextBox textBox_2 = new TextBox();
		flexTable.setWidget(2, 1, textBox_2);
		textBox_2.setWidth("100%");

		final Label label_4 = new Label("Clearport User password");
		flexTable.setWidget(1, 2, label_4);

		final Label label_5 = new Label("Clearport User password");
		flexTable.setWidget(2, 2, label_5);

		final TextBox textBox_1 = new TextBox();
		flexTable.setWidget(1, 3, textBox_1);
		textBox_1.setWidth("100%");

		final TextBox textBox_3 = new TextBox();
		flexTable.setWidget(2, 3, textBox_3);
		textBox_3.setWidth("100%");

		final Button button = new Button();
		flexTable.setWidget(3, 1, button);
		flexTable.getCellFormatter().setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_CENTER);
		button.setText("Add");

		final Button button_1 = new Button();
		flexTable.setWidget(3, 3, button_1);
		flexTable.getCellFormatter().setHorizontalAlignment(3, 3, HasHorizontalAlignment.ALIGN_CENTER);
		button_1.setText("Delete");

		final FlexTable flexTable_1 = new FlexTable();
		verticalPanel.add(flexTable_1);
		verticalPanel.setCellHorizontalAlignment(flexTable_1, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable_1.setWidth("100%");
		verticalPanel.setCellHeight(flexTable_1, "50%");
		verticalPanel.setCellWidth(flexTable_1, "100%");

		final Label label_6 = new Label("G/L Reference");
		flexTable_1.setWidget(0, 0, label_6);

		final Label label_7 = new Label("Broker Commissions G/L Reference");
		flexTable_1.setWidget(1, 0, label_7);

		final TextBox textBox_4 = new TextBox();
		flexTable_1.setWidget(1, 1, textBox_4);
		textBox_4.setWidth("100%");
		
		
		
		

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		tabPanel.add(verticalPanel_1, "Futures Profile");
		verticalPanel_1.setWidth("100%");
		verticalPanel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final FlexTable flexTable1 = new FlexTable();
		verticalPanel_1.add(flexTable1);
		flexTable1.setWidth("100%");
		verticalPanel_1.setCellHorizontalAlignment(flexTable1, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_1.setCellWidth(flexTable1, "100%");
		flexTable1.getCellFormatter().setHorizontalAlignment(9, 6, HasHorizontalAlignment.ALIGN_CENTER);

		final Label label = new Label("Futures Exchange Profile");
		flexTable1.setWidget(0, 0, label);

		final Label label_11 = new Label("Exchange");
		flexTable1.setWidget(1, 1, label_11);

		final Label label_21 = new Label("Contract Size");
		flexTable1.setWidget(1, 2, label_21);

		final Label label_31 = new Label("Unit of Measure");
		flexTable1.setWidget(1, 3, label_31);

		final Label label_41 = new Label("Contract Symbol");
		flexTable1.setWidget(1, 4, label_41);

		final Label label_51 = new Label("Contract Type");
		flexTable1.setWidget(2, 0, label_51);

		final Label label_61 = new Label("Contract Type");
		flexTable1.setWidget(3, 0, label_61);

		final Label label_71 = new Label("Contract Type");
		flexTable1.setWidget(4, 0, label_71);

		final Label label_8 = new Label("Contract Type");
		flexTable1.setWidget(5, 0, label_8);

		final Label label_9 = new Label("Contract Type");
		flexTable1.setWidget(6, 0, label_9);

		final Label label_10 = new Label("Contract Type");
		flexTable1.setWidget(7, 0, label_10);

		final Button button1 = new Button();
		flexTable1.setWidget(2, 5, button1);
		button1.setText("Add/Edit Exchange");

		final Button button_11 = new Button();
		flexTable1.setWidget(4, 5, button_11);
		button_11.setText("Add/Edit Contract Type");

		final ListBox listBox = new ListBox();
		flexTable1.setWidget(2, 1, listBox);

		final ListBox listBox_1 = new ListBox();
		flexTable1.setWidget(2, 3, listBox_1);
		
		
		
		final VerticalPanel verticalPanel_2 = new VerticalPanel();
		tabPanel.add(verticalPanel_2, "Exchange Fee Profile");
		verticalPanel_2.setWidth("100%");
		verticalPanel_2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final FlexTable flexTable11 = new FlexTable();
		verticalPanel_2.add(flexTable11);
		flexTable11.setWidth("100%");
		verticalPanel_2.setCellHorizontalAlignment(flexTable11, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_2.setCellWidth(flexTable11, "100%");
		flexTable11.getCellFormatter().setHorizontalAlignment(9, 11, HasHorizontalAlignment.ALIGN_CENTER);

		final Label label1 = new Label("Amount");
		flexTable11.setWidget(1, 2, label1);

		final Label label_111 = new Label("G/L Reference");
		flexTable11.setWidget(1, 5, label_111);

		final Label commissionsLabel = new Label("Comissions");
		flexTable11.setWidget(2, 1, commissionsLabel);

		final Label basedOnLabel = new Label("Based On");
		flexTable11.setWidget(2, 2, basedOnLabel);

		final Label feesLabel = new Label("Fees");
		flexTable11.setWidget(2, 3, feesLabel);

		final Label basedOnLabel_1 = new Label("Based On");
		flexTable11.setWidget(2, 4, basedOnLabel_1);
		flexTable11.getCellFormatter().setVerticalAlignment(2, 4, HasVerticalAlignment.ALIGN_BOTTOM);

		final Label comissioinsLabel = new Label("Comissioins");
		flexTable11.setWidget(2, 5, comissioinsLabel);

		final Label feesLabel_1 = new Label("Fees");
		flexTable11.setWidget(2, 6, feesLabel_1);

		final Label exchangeNameLabel = new Label("Exchange Name");
		flexTable11.setWidget(3, 0, exchangeNameLabel);

		final Label exchangeNameLabel_1 = new Label("Exchange Name");
		flexTable11.setWidget(4, 0, exchangeNameLabel_1);

		final ListBox listBox1 = new ListBox();
		flexTable11.setWidget(3, 2, listBox1);

		final ListBox listBox_11 = new ListBox();
		flexTable11.setWidget(3, 4, listBox_11);

		final Label exchangeFeePricesLabel = new Label("Exchange Fee Prices");
		flexTable11.setWidget(0, 0, exchangeFeePricesLabel);
		
		
		

		final VerticalPanel verticalPanel_3 = new VerticalPanel();
		tabPanel.add(verticalPanel_3, "Trader Porfile");
		verticalPanel_3.setWidth("100%");
		verticalPanel_3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final FlexTable flexTable111 = new FlexTable();
		verticalPanel_3.add(flexTable111);
		flexTable111.setWidth("100%");
		verticalPanel_3.setCellHorizontalAlignment(flexTable111, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_3.setCellWidth(flexTable111, "100%");
		
		flexTable111.getCellFormatter().setWidth(0, 0, "18%");
		flexTable111.getCellFormatter().setWidth(0, 1, "15%");
		flexTable111.getCellFormatter().setWidth(0, 2, "18%");
		flexTable111.getCellFormatter().setWidth(0, 3, "15%");
		flexTable111.getCellFormatter().setWidth(0, 4, "18%");
		flexTable111.getCellFormatter().setWidth(0, 5, "15%");
		

		final Label traderProfileLabel = new Label("Trader Profile");
		flexTable111.setWidget(0, 0, traderProfileLabel);

		final Label traderNameLabel = new Label("Trader Name");
		flexTable111.setWidget(1, 0, traderNameLabel);

		final TextBox textBox1 = new TextBox();
		flexTable111.setWidget(1, 1, textBox1);
		///textBox1.setWidth("55%");

		final Label traderInLabel = new Label("Trader Initial");
		flexTable111.setWidget(1, 2, traderInLabel);

		final TextBox textBox_11 = new TextBox();
		flexTable111.setWidget(1, 3, textBox_11);
		///textBox_11.setWidth("55%");

		final Label label_311 = new Label("Digital");
		flexTable111.setWidget(1, 4, label_311);

		
		final TextBox textBox_21 = new TextBox();
		flexTable111.setWidget(1, 5, textBox_21);
		textBox_21.setWidth("35%");
		
		
		
		
		final CheckBox checkBox = new CheckBox();
		flexTable111.setWidget(2, 0, checkBox);
		checkBox.setText("ContactType");

		final CheckBox checkBox_1 = new CheckBox();
		flexTable111.setWidget(2, 1, checkBox_1);
		checkBox_1.setText("ContactType");

		final CheckBox checkBox_2 = new CheckBox();
		flexTable111.setWidget(2, 2, checkBox_2);
		checkBox_2.setText("ContactType");

		final CheckBox checkBox_3 = new CheckBox();
		flexTable111.setWidget(2, 3, checkBox_3);
		checkBox_3.setText("ContactType");
		

		final Label label_411 = new Label("Futures Account");
		flexTable111.setWidget(3, 0, label_411);

		final TextBox textBox_31 = new TextBox();
		flexTable111.setWidget(3, 1, textBox_31);
		///textBox_31.setWidth("55%");

		final Label label_511 = new Label("Internal Account");
		flexTable111.setWidget(3, 2, label_511);

		final TextBox textBox_41 = new TextBox();
		flexTable111.setWidget(3, 3, textBox_41);
		///textBox_41.setWidth("55%");

		final Button button11 = new Button();
		flexTable111.setWidget(3, 4, button11);
		button11.setText("Add/Edit");
		///button11.setWidth("50%");
		flexTable111.getFlexCellFormatter().setColSpan(3, 4, 2);
		
		final Label label_611 = new Label("Trader Name");
		flexTable111.setWidget(4, 0, label_611);

		final TextBox textBox_5 = new TextBox();
		flexTable111.setWidget(4, 1, textBox_5);
		///textBox_5.setWidth("55%");

		final Label label_711 = new Label("Trader Initial");
		flexTable111.setWidget(4, 2, label_711);

		final TextBox textBox_6 = new TextBox();
		flexTable111.setWidget(4, 3, textBox_6);
		///textBox_6.setWidth("55%");

		final Label label_81 = new Label("Digital");
		flexTable111.setWidget(4, 4, label_81);

		
		final TextBox textBox_7 = new TextBox();
		flexTable111.setWidget(4, 5, textBox_7);
		textBox_7.setWidth("35%");
		
		
		final CheckBox checkBox_4 = new CheckBox();
		flexTable111.setWidget(5, 0, checkBox_4);
		checkBox_4.setText("ContactType");

		final CheckBox checkBox_5 = new CheckBox();
		flexTable111.setWidget(5, 1, checkBox_5);
		checkBox_5.setText("ContactType");

		final CheckBox checkBox_6 = new CheckBox();
		flexTable111.setWidget(5, 2, checkBox_6);
		checkBox_6.setText("ContactType");

		final CheckBox checkBox_7 = new CheckBox();
		flexTable111.setWidget(5, 3, checkBox_7);
		checkBox_7.setText("ContactType");
		

		final Label label_91 = new Label("Futures Account");
		flexTable111.setWidget(6, 0, label_91);

		final TextBox textBox_8 = new TextBox();
		flexTable111.setWidget(6, 1, textBox_8);
		///textBox_8.setWidth("55%");

		final Label label_101 = new Label("Internal Account");
		flexTable111.setWidget(6, 2, label_101);

		final TextBox textBox_9 = new TextBox();
		flexTable111.setWidget(6, 3, textBox_9);
		///textBox_9.setWidth("55%");

		final Button button_111 = new Button();
		flexTable111.setWidget(6, 4, button_111);
		button_111.setText("Add/Edit");
		///button_111.setWidth("50%");
		flexTable111.getFlexCellFormatter().setColSpan(6, 4, 2);
		
		final Button button_2 = new Button();
		flexTable111.setWidget(7, 2, button_2);
		button_2.setText("Add/Edit");
		///button_2.setWidth("50%");
		flexTable111.getFlexCellFormatter().setColSpan(7, 2, 2);
		
		
		

		final VerticalPanel verticalPanel_4 = new VerticalPanel();
		tabPanel.add(verticalPanel_4, "Broker Comissions");
		verticalPanel_4.setWidth("100%");
		verticalPanel_4.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final FlexTable flexTable1111 = new FlexTable();
		verticalPanel_4.add(flexTable1111);
		flexTable1111.setWidth("100%");
		verticalPanel_4.setCellHorizontalAlignment(flexTable1111, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_4.setCellWidth(flexTable1111, "100%");
		

		final Label label11 = new Label("Broker Comissions");
		flexTable1111.setWidget(0, 0, label11);

		final Label label_1111 = new Label("Contract Type");
		flexTable1111.setWidget(1, 1, label_1111);

		final Label label_211 = new Label("Per");
		flexTable1111.setWidget(1, 2, label_211);

		final Label label_3111 = new Label("Contract Type");
		flexTable1111.setWidget(1, 3, label_3111);

		final Label label_4111 = new Label("Per");
		flexTable1111.setWidget(1, 4, label_4111);

		final Label label_5111 = new Label("Contract Type");
		flexTable1111.setWidget(1, 5, label_5111);

		final Label label_6111 = new Label("Broker");
		flexTable1111.setWidget(2, 0, label_6111);

		final Label label_7111 = new Label("Broker");
		flexTable1111.setWidget(3, 0, label_7111);

		final ListBox listBox11 = new ListBox();
		flexTable1111.setWidget(2, 1, listBox11);

		final ListBox listBox_111 = new ListBox();
		flexTable1111.setWidget(2, 2, listBox_111);
		
		
		
		
		
		
		
		
		tabPanel.selectTab(0);
		
	}

}

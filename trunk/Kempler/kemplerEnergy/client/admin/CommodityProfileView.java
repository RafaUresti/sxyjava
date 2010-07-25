package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommodityProfileView extends Composite {
	private AdminViewController view;
	public CommodityProfileView(AdminViewController view) {
		

		final TabPanel tabPanel = new TabPanel();
		initWidget(tabPanel);
		tabPanel.setWidth("100%");
		this.view= view;

		final VerticalPanel verticalPanel = new VerticalPanel();
		tabPanel.add(verticalPanel, "Commodity Profile");
		verticalPanel.setWidth("100%");

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setWidth("100%");
		verticalPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellWidth(horizontalPanel, "100%");

		final FlexTable flexTable = new FlexTable();
		horizontalPanel.add(flexTable);
		flexTable.setWidth("100%");
		horizontalPanel.setCellWidth(flexTable, "50%");

		final Label mainLabel = new Label("Main");
		flexTable.setWidget(0, 0, mainLabel);

		final Label commodityLabel = new Label("Commodity");
		flexTable.setWidget(2, 0, commodityLabel);
		flexTable.getCellFormatter().setWidth(2, 0, "30%");
		
		final ListBox listBox = new ListBox();
		flexTable.setWidget(2, 1, listBox);
		listBox.setWidth("100%");
		flexTable.getCellFormatter().setWidth(2, 1, "35%");
		
		final Button button = new Button();
		flexTable.setWidget(2, 2, button);
		flexTable.getCellFormatter().setWidth(2, 2, "35%");
		button.setText("Add/Edit");

		final Label label = new Label("Quality/Grade");
		flexTable.setWidget(4, 0, label);

		final TextBox textBox = new TextBox();
		flexTable.setWidget(4, 1, textBox);
		textBox.setWidth("100%");

		final Button button_1 = new Button();
		flexTable.setWidget(4, 2, button_1);
		button_1.setText("Add/Edit");

		final Label label_1 = new Label("Quality/Grade");
		flexTable.setWidget(5, 0, label_1);

		final TextBox textBox_1 = new TextBox();
		flexTable.setWidget(5, 1, textBox_1);
		textBox_1.setWidth("100%");

		final Button button_2 = new Button();
		flexTable.setWidget(5, 2, button_2);
		button_2.setText("Add/Edit");

		final Label stccCodeLabel = new Label("STCC Code");
		flexTable.setWidget(7, 0, stccCodeLabel);

		final TextBox textBox_2 = new TextBox();
		flexTable.setWidget(7, 1, textBox_2);
		textBox_2.setWidth("100%");

		final Label label_3 = new Label("");
		flexTable.setWidget(3, 0, label_3);
		label_3.setHeight("15");

		final Label label_4 = new Label("");
		flexTable.setWidget(1, 0, label_4);
		label_4.setHeight("15");

		final Label label_5 = new Label("");
		flexTable.setWidget(6, 0, label_5);
		label_5.setHeight("15");



		final FlexTable flexTable_1 = new FlexTable();
		horizontalPanel.add(flexTable_1);
		flexTable_1.setWidth("100%");
		horizontalPanel.setCellHorizontalAlignment(flexTable_1, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellWidth(flexTable_1, "50%");

		final Label unitOfMeasursLabel = new Label("Units of Measur");
		flexTable_1.setWidget(0, 0, unitOfMeasursLabel);

		final ListBox listBox_1 = new ListBox();
		flexTable_1.setWidget(1, 0, listBox_1);
		listBox_1.setWidth("50%");

		final ListBox listBox_2 = new ListBox();
		flexTable_1.setWidget(2, 0, listBox_2);
		listBox_2.setWidth("50%");

		final ListBox listBox_3 = new ListBox();
		flexTable_1.setWidget(3, 0, listBox_3);
		listBox_3.setWidth("50%");

		final ListBox listBox_4 = new ListBox();
		flexTable_1.setWidget(4, 0, listBox_4);
		listBox_4.setWidth("50%");

		final ListBox listBox_5 = new ListBox();
		flexTable_1.setWidget(5, 0, listBox_5);
		listBox_5.setWidth("50%");

		final ListBox listBox_6 = new ListBox();
		flexTable_1.setWidget(6, 0, listBox_6);
		listBox_6.setWidth("50%");

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		verticalPanel.add(verticalPanel_1);
		verticalPanel_1.setWidth("100%");
		verticalPanel.setCellHorizontalAlignment(verticalPanel_1, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellWidth(verticalPanel_1, "100%");

		final Label label_2 = new Label("");
		verticalPanel_1.add(label_2);
		label_2.setHeight("15");

		final FlexTable flexTable_2 = new FlexTable();
		verticalPanel_1.add(flexTable_2);
		flexTable_2.setWidth("100%");

		final Label vehicleCapLabel = new Label("Vehicle Capacity");
		flexTable_2.setWidget(0, 0, vehicleCapLabel);
		flexTable_2.getCellFormatter().setWidth(0, 0, "30%");

		final Label label_6 = new Label("Vehicle 1");
		flexTable_2.setWidget(1, 0, label_6);
		flexTable_2.getCellFormatter().setWidth(1, 0, "30%");

		final TextBox textBox_4 = new TextBox();
		flexTable_2.setWidget(1, 1, textBox_4);
		textBox_4.setWidth("100%");
		flexTable_2.getCellFormatter().setWidth(1, 1, "35%");

		final ListBox listBox_7 = new ListBox();
		flexTable_2.setWidget(1, 2, listBox_7);
		flexTable_2.getCellFormatter().setWidth(1, 2, "35%");
		listBox_7.setWidth("50%");

		final Label label_7 = new Label("Vehicle 2");
		label_7.setWidth("100%");
		flexTable_2.setWidget(2, 0, label_7);
		flexTable_2.getCellFormatter().setWidth(2, 0, "30%");

		final TextBox textBox_3 = new TextBox();
		flexTable_2.setWidget(2, 1, textBox_3);
		textBox_3.setWidth("100%");
		flexTable_2.getCellFormatter().setWidth(2, 1, "35%");

		final ListBox listBox_9 = new ListBox();
		listBox_9.setWidth("50%");
		flexTable_2.setWidget(2, 2, listBox_9);
		flexTable_2.getCellFormatter().setWidth(2, 2, "35%");
		

		tabPanel.selectTab(0);
		setWidth("100%");
		
	}

}

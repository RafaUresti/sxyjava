package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KemplerVehiclesProfileView extends Composite {
	private AdminViewController view;
	public KemplerVehiclesProfileView(AdminViewController view) {
		this.view = view;

		final TabPanel tabPanel = new TabPanel();
		initWidget(tabPanel);
		tabPanel.setWidth("100%");

		final VerticalPanel verticalPanel = new VerticalPanel();
		tabPanel.add(verticalPanel, "Kempler Vehicles Profile");
		verticalPanel.setWidth("100%");

		final FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		verticalPanel.setCellHorizontalAlignment(flexTable, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellWidth(flexTable, "100%");

		final Label kemplerVehiclesLabel = new Label("Kempler Vehicles");
		flexTable.setWidget(0, 0, kemplerVehiclesLabel);

		final Label label = new Label("");
		flexTable.setWidget(1, 0, label);
		label.setHeight("15");

		final Label label_1 = new Label("A list of Kempler Vehicles");
		flexTable.setWidget(2, 0, label_1);
		flexTable.getCellFormatter().setWidth(2, 0, "35%");

		final CheckBox checkBox = new CheckBox();
		flexTable.setWidget(2, 4, checkBox);
		flexTable.getCellFormatter().setWidth(2, 4, "35%");
		checkBox.setText("Active/Inactive");

		final Label label_2 = new Label("");
		flexTable.setWidget(2, 1, label_2);
		flexTable.getCellFormatter().setWidth(2, 1, "10%");

		final Label label_4 = new Label("");
		flexTable.setWidget(2, 2, label_4);
		flexTable.getCellFormatter().setWidth(2, 2, "10%");

		final Label label_6 = new Label("");
		flexTable.setWidget(2, 3, label_6);
		flexTable.getCellFormatter().setWidth(2, 3, "10%");

		final ListBox listBox = new ListBox();
		flexTable.setWidget(3, 0, listBox);
		listBox.setSize("100%", "150");
		listBox.setVisibleItemCount(5);
	

		final Label label_3 = new Label("");
		verticalPanel.add(label_3);
		label_3.setHeight("15");

		final FlexTable flexTable_1 = new FlexTable();
		verticalPanel.add(flexTable_1);
		verticalPanel.setCellHorizontalAlignment(flexTable_1, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellWidth(flexTable_1, "100%");

		final Label VehicleNolabel = new Label("Vehicle Number");
		flexTable_1.setWidget(0, 0, VehicleNolabel);
		
		final TextBox textBox = new TextBox();
		flexTable_1.setWidget(0, 1, textBox);
		textBox.setWidth("100%");
		
		final Label VehicleTypelabel = new Label("Vehicle Type");
		flexTable_1.setWidget(1, 0, VehicleTypelabel);
		
		final ListBox listBox_1 = new ListBox();
		flexTable_1.setWidget(1, 1, listBox_1);
		listBox_1.setWidth("100%");

		final Button button = new Button();
		flexTable_1.setWidget(2, 0, button);
		button.setText("Add");

		final Button button_1 = new Button();
		flexTable_1.setWidget(2, 1, button_1);
		button_1.setText("Delete");
		
		tabPanel.selectTab(0);

	}

}

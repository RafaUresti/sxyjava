package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;

public class Addtemp extends Composite {

	public Addtemp() {

		final VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		verticalPanel.setWidth("100%");
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final SimplePanel simplePanel = new SimplePanel();
		verticalPanel.add(simplePanel);
		simplePanel.setWidth("100%");
		verticalPanel.setCellWidth(simplePanel, "100%");

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		simplePanel.setWidget(verticalPanel_1);
		verticalPanel_1.setSize("100%", "100%");

		final ListBox listBox = new ListBox();
		verticalPanel_1.add(listBox);
		listBox.setWidth("100%");
		verticalPanel_1.setCellWidth(listBox, "100%");

		final FlexTable flexTable = new FlexTable();
		verticalPanel_1.add(flexTable);
		flexTable.setWidth("100%");
		verticalPanel_1.setCellWidth(flexTable, "100%");

		final Label label = new Label("New Label");
		flexTable.setWidget(0, 0, label);
		flexTable.getCellFormatter().setWidth(0, 0, "15%");

		final Label label_1 = new Label("New Label");
		flexTable.setWidget(0, 1, label_1);
		setWidth("100%");

		
	}



}

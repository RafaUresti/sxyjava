package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MarketZoneProfileView extends Composite {
	private AdminViewController view;
	public MarketZoneProfileView(AdminViewController view) {
		this.view=view;

		final TabPanel tabPanel = new TabPanel();
		initWidget(tabPanel);
		tabPanel.setWidth("100%");

		final VerticalPanel verticalPanel = new VerticalPanel();
		tabPanel.add(verticalPanel, "Market Zone Profile");
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setWidth("100%");
		
		final FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		verticalPanel.setCellHorizontalAlignment(flexTable, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidth("100%");

		final Label label = new Label("Contract Type");
		flexTable.setWidget(0, 0, label);

		final Label label_1 = new Label("Contract Type");
		flexTable.setWidget(0, 1, label_1);

		final Label label_3 = new Label("Contract Type");
		flexTable.setWidget(0, 2, label_3);

		final Label label_5 = new Label("Contract Type");
		flexTable.setWidget(0, 3, label_5);

		final ListBox listBox = new ListBox();
		flexTable.setWidget(1, 0, listBox);
		listBox.setWidth("100%");

		final ListBox listBox_1 = new ListBox();
		flexTable.setWidget(1, 1, listBox_1);
		listBox_1.setWidth("100%");

		final ListBox listBox_2 = new ListBox();
		flexTable.setWidget(1, 2, listBox_2);
		listBox_2.setWidth("100%");

		final ListBox listBox_3 = new ListBox();
		flexTable.setWidget(1, 3, listBox_3);
		listBox_3.setWidth("100%");

		final TextBox textBox = new TextBox();
		flexTable.setWidget(2, 0, textBox);
		textBox.setWidth("100%");

		final TextBox textBox_1 = new TextBox();
		flexTable.setWidget(2, 1, textBox_1);
		textBox_1.setWidth("100%");

		final TextBox textBox_2 = new TextBox();
		flexTable.setWidget(2, 2, textBox_2);
		textBox_2.setWidth("100%");

		final TextBox textBox_3 = new TextBox();
		flexTable.setWidget(2, 3, textBox_3);
		textBox_3.setWidth("100%");

		final Label label_51 = new Label();
		verticalPanel.add(label_51);
		label_51.setHeight("15");
		
		final FlexTable flexTable1 = new FlexTable();
		verticalPanel.add(flexTable1);
		verticalPanel.setCellHorizontalAlignment(flexTable1, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable1.setWidth("100%");

		final Label label_2 = new Label("Basis");
		flexTable1.setWidget(0, 0, label_2);

		final Label label_4 = new Label("K08");
		flexTable1.setWidget(1, 1, label_4);

		final Label label_7 = new Label("M08");
		flexTable1.setWidget(1, 2, label_7);

		final Label label_8 = new Label("N08");
		flexTable1.setWidget(1, 3, label_8);

		final Label label_9 = new Label("Q08");
		flexTable1.setWidget(1, 4, label_9);

		final Label label_11 = new Label("U08");
		flexTable1.setWidget(1, 5, label_11);

		final Label label_12 = new Label("V08");
		flexTable1.setWidget(1, 6, label_12);

		final Label label_13 = new Label("Contract Type");
		flexTable1.setWidget(2, 0, label_13);

		final Label label_14 = new Label("Contract Type");
		flexTable1.setWidget(3, 0, label_14);
	

		final Label label_15 = new Label("Contract Type");
		flexTable1.setWidget(4, 0, label_15);

		final Label label_6 = new Label("Contract Type");
		flexTable1.setWidget(5, 0, label_6);
	

		final Label label_10 = new Label("Contract Type");
		flexTable1.setWidget(6, 0, label_10);


		final Label label_16 = new Label("Contract Type");
		flexTable1.setWidget(7, 0, label_16);


		
		
		
		
		tabPanel.selectTab(0);
		
	}

}

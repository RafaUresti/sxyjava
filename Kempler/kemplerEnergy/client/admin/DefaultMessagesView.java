package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DefaultMessagesView extends Composite {
	private AdminViewController view;
	public DefaultMessagesView(AdminViewController view) {
		this.view= view;

		final TabPanel tabPanel = new TabPanel();
		initWidget(tabPanel);
		tabPanel.setWidth("100%");

		final VerticalPanel verticalPanel = new VerticalPanel();
		tabPanel.add(verticalPanel, "Contracts");
		verticalPanel.setWidth("100%");
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setWidth("100%");
		
		verticalPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellWidth(horizontalPanel, "100%");
		verticalPanel.setCellHeight(horizontalPanel, "20%");

		final TextArea textArea = new TextArea();
		horizontalPanel.add(textArea);
		textArea.setSize("100%", "150");
		horizontalPanel.setCellWidth(textArea, "90%");
		textArea.setText("List of Messages with a ref.# for each");

		final Button deleteButton = new Button();
		horizontalPanel.add(deleteButton);
		horizontalPanel.setCellHorizontalAlignment(deleteButton, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellWidth(deleteButton, "10%");
		deleteButton.setText("Delete");

		final Label label_1 = new Label();
		verticalPanel.add(label_1);
		verticalPanel.setCellHeight(label_1, "5%");


		final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_1);
		horizontalPanel_1.setWidth("100%");
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_1, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellWidth(horizontalPanel_1, "100%");
		verticalPanel.setCellHeight(horizontalPanel_1, "20%");

		final TextArea textArea_1 = new TextArea();
		horizontalPanel_1.add(textArea_1);
		textArea_1.setSize("100%", "120");
		horizontalPanel_1.setCellWidth(textArea_1, "90%");
		textArea_1.setText("Edit/Add new message");

		final Button addButton = new Button();
		horizontalPanel_1.add(addButton);
		horizontalPanel_1.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_CENTER);
		addButton.setText("Add");

		final Label label_2 = new Label("");
		verticalPanel.add(label_2);
		verticalPanel.setCellHeight(label_2, "5%");

		final HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_2);
		horizontalPanel_2.setWidth("100%");
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_2, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellWidth(horizontalPanel_2, "100%");
		verticalPanel.setCellHeight(horizontalPanel_2, "50%");

		final TextArea textArea_2 = new TextArea();
		horizontalPanel_2.add(textArea_2);
		textArea_2.setSize("100%", "90");
		horizontalPanel_2.setCellWidth(textArea_2, "25%");
		textArea_2.setText("Contract Type");

		final TextArea textArea_3 = new TextArea();
		horizontalPanel_2.add(textArea_3);
		textArea_3.setSize("100%", "90");
		horizontalPanel_2.setCellWidth(textArea_3, "25%");
		textArea_3.setText("Pricing Type");

		final TextArea delievryModeTextArea = new TextArea();
		horizontalPanel_2.add(delievryModeTextArea);
		delievryModeTextArea.setSize("100%", "90");
		horizontalPanel_2.setCellWidth(delievryModeTextArea, "25%");
		delievryModeTextArea.setText("Delivery Mode");

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		horizontalPanel_2.add(verticalPanel_1);
		horizontalPanel_2.setCellWidth(verticalPanel_1, "25%");

		final Label label = new Label("");
		verticalPanel_1.add(label);
		label.setHeight("15");

		final TextArea textArea_5 = new TextArea();
		verticalPanel_1.add(textArea_5);
		textArea_5.setSize("100%", "60");
		textArea_5.setText("list of messages used");
		

		final Label label_3 = new Label("");
		verticalPanel_1.add(label_3);
		label_3.setHeight("15");

		

		final VerticalPanel verticalPanel_11 = new VerticalPanel();
		tabPanel.add(verticalPanel_11, "Settlements");
		verticalPanel_11.setWidth("100%");
		verticalPanel_11.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final HorizontalPanel horizontalPanel1 = new HorizontalPanel();
		verticalPanel_11.add(horizontalPanel1);
		horizontalPanel1.setWidth("100%");
		
		verticalPanel_11.setCellHorizontalAlignment(horizontalPanel1, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_11.setCellWidth(horizontalPanel1, "100%");
		verticalPanel_11.setCellHeight(horizontalPanel1, "20%");

		final TextArea textArea1 = new TextArea();
		horizontalPanel1.add(textArea1);
		textArea1.setSize("100%", "150");
		horizontalPanel1.setCellWidth(textArea1, "90%");
		textArea1.setText("List of Messages with a ref.# for each");

		final Button deleteButton1 = new Button();
		horizontalPanel1.add(deleteButton1);
		horizontalPanel1.setCellHorizontalAlignment(deleteButton1, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel1.setCellWidth(deleteButton1, "10%");
		deleteButton1.setText("Delete");

		final Label label_11 = new Label();
		verticalPanel_11.add(label_11);
		verticalPanel_11.setCellHeight(label_11, "5%");


		final HorizontalPanel horizontalPanel_11 = new HorizontalPanel();
		verticalPanel_11.add(horizontalPanel_11);
		horizontalPanel_11.setWidth("100%");
		verticalPanel_11.setCellHorizontalAlignment(horizontalPanel_11, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_11.setCellWidth(horizontalPanel_11, "100%");
		verticalPanel_11.setCellHeight(horizontalPanel_11, "20%");

		final TextArea textArea_11 = new TextArea();
		horizontalPanel_11.add(textArea_11);
		textArea_11.setSize("100%", "120");
		horizontalPanel_11.setCellWidth(textArea_11, "90%");
		textArea_11.setText("Edit/Add new message");

		final Button addButton1 = new Button();
		horizontalPanel_11.add(addButton1);
		horizontalPanel_11.setCellHorizontalAlignment(addButton1, HasHorizontalAlignment.ALIGN_CENTER);
		addButton1.setText("Add");

		final Label label_21 = new Label("");
		verticalPanel_11.add(label_21);
		verticalPanel_11.setCellHeight(label_21, "5%");

		final HorizontalPanel horizontalPanel_21 = new HorizontalPanel();
		verticalPanel_11.add(horizontalPanel_21);
		horizontalPanel_21.setWidth("100%");
		verticalPanel_11.setCellHorizontalAlignment(horizontalPanel_21, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_11.setCellWidth(horizontalPanel_21, "100%");
		verticalPanel_11.setCellHeight(horizontalPanel_21, "50%");

		final TextArea textArea_21 = new TextArea();
		horizontalPanel_21.add(textArea_21);
		textArea_21.setSize("100%", "90");
		horizontalPanel_21.setCellWidth(textArea_21, "25%");
		textArea_21.setText("Sales/Purchases Settlement Credit/debit memo");

		final TextArea textArea_31 = new TextArea();
		horizontalPanel_21.add(textArea_31);
		textArea_31.setSize("100%", "90");
		horizontalPanel_21.setCellWidth(textArea_31, "25%");
		textArea_31.setText("Pricing Type");

		final TextArea delievryModeTextArea1 = new TextArea();
		horizontalPanel_21.add(delievryModeTextArea1);
		delievryModeTextArea1.setSize("100%", "90");
		horizontalPanel_21.setCellWidth(delievryModeTextArea1, "25%");
		delievryModeTextArea1.setText("Delivery Mode");

		final VerticalPanel verticalPanel_111 = new VerticalPanel();
		horizontalPanel_21.add(verticalPanel_111);
		horizontalPanel_21.setCellWidth(verticalPanel_111, "25%");

		final Label label1 = new Label("");
		verticalPanel_111.add(label1);
		label1.setHeight("15");

		final TextArea textArea_51 = new TextArea();
		verticalPanel_111.add(textArea_51);
		textArea_51.setSize("100%", "60");
		textArea_51.setText("list of messages used");
		

		final Label label_31 = new Label("");
		verticalPanel_111.add(label_31);
		label_31.setHeight("15");
		
		
		
		tabPanel.selectTab(0);
		
	}

}

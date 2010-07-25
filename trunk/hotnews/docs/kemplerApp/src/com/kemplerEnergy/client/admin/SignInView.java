package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SignInView extends Composite {
	private VerticalPanel verticalPanel = new VerticalPanel();
	private FlexTable flexTable = new FlexTable();
	private Label usernameLabel = new Label("UserName");
	private TextBox UserNametextBox = new TextBox();
	private Label passwordLabel = new Label("password");
	private PasswordTextBox passwordTextBox = new PasswordTextBox();
	private Button signInButton = new Button();
	private AdminViewController controller;
	
	
	public SignInView(AdminViewController view) {

		initWidget(verticalPanel);
		verticalPanel.setWidth("50%");
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.controller = view;

		final Label label = new Label("");
		verticalPanel.add(label);
		label.setHeight("35");
		verticalPanel.add(flexTable);
		flexTable.setWidth("50%");
		flexTable.setWidget(0, 0, usernameLabel);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(0, 1, UserNametextBox);
		UserNametextBox.setWidth("100%");
		flexTable.setWidget(1, 0, passwordLabel);
		flexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(1, 1, passwordTextBox);
		passwordTextBox.addKeyboardListener(new KeyboardListenerAdapter() {
			public void onKeyPress(final Widget sender, final char keyCode, final int modifiers) {
				if(keyCode == KEY_ENTER )
	            	 SignIn();
			}
		});
		passwordTextBox.setWidth("100%");
		flexTable.setWidget(2, 0, signInButton);
		flexTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		signInButton.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				SignIn();
			}
		});
		signInButton.setText("Sign In");

		final Button resetButton = new Button();
		flexTable.setWidget(2, 1, resetButton);
		flexTable.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER);
		resetButton.setText("Reset");
	}
	public void SignIn()
	{
		String name=UserNametextBox.getText();
		String password=passwordTextBox.getText();
		if (name.length() == 0 ||password.length() == 0) {
			Window.alert("Username or password is empty.");
		}	
		else
			controller.getListener().onSignIn(name,password);
	}
	public void refresh()
	{
		UserNametextBox.setText("");
		passwordTextBox.setText("");
	}



}

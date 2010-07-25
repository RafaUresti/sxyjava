package com.kemplerEnergy.client;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.kemplerEnergy.client.admin.AdminListener;
import com.kemplerEnergy.client.admin.AdminServiceClientImpl;
import com.kemplerEnergy.client.admin.AdminViewController;
import com.kemplerEnergy.client.admin.AdministratorStartPanel;
import com.kemplerEnergy.client.rins.AccountantStartPanel;
import com.kemplerEnergy.client.rins.LogisticsStartPanel;

public class LoginControlPanel extends Composite{
	FormPanel formPanel = new FormPanel();
	TextBox userName = new TextBox();
	PasswordTextBox password = new PasswordTextBox();
 
	Button submitButton = new Button("Log in");
	Button resetButton = new Button("Reset");
//	Button btn_reg = new Button("Register");
//	Button btn_test = new Button("In the session?");

	VerticalPanel vp = new VerticalPanel();
	HorizontalPanel hp = new HorizontalPanel();

	public LoginControlPanel(){
		initWidget(formPanel);
		formPanel.setStylePrimaryName(".LoginPanel");
		submitButton.setWidth(80+"");
		vp.add(userName);
		vp.add(password);
		hp.add(submitButton);
		hp.add(resetButton);
		vp.add(hp);
//		vp.add(btn_test);
		
		userName.setName("userName");
		password.setName("passWord");
		
		formPanel.setAction("SessionGenerator");
		formPanel.setMethod(FormPanel.METHOD_GET);
		formPanel.add(vp);
		this.addListener();
		this.addHandler();
	}
	
	private void addListener(){
		submitButton.addClickListener(new ClickListener(){
			public void onClick(Widget sender){
				LoginControlPanel.this.formPanel.submit();
			}
		});
		
		resetButton.addClickListener(new ClickListener(){
			public void onClick(Widget sender){
				LoginControlPanel.this.userName.setText("");
				LoginControlPanel.this.password.setText("");
			}
		});
		
/*		btn_test.addClickListener(new ClickListener(){
			public void onClick(Widget sender){
				String sessionID = Cookies.getCookie("sid");
				Window.alert(sessionID);
			}
		});*/
		
		password.addKeyboardListener(new KeyboardListener(){
			
			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				// TODO Auto-generated method stub
				if (keyCode == KEY_ENTER)
					submitButton.click();
				}

			public void onKeyPress(Widget arg0, char arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			public void onKeyUp(Widget arg0, char arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
		});
//		btn_reg.addClickListener(new ClickListener(){
//			public void onClick(Widget sender){
//				
//			}
//		});
	}
	
	private void addHandler(){

		formPanel.addFormHandler(new FormHandler(){
			public void onSubmit(FormSubmitEvent event)
			{
				submitButton.setEnabled(false);
				submitButton.setText("Logging in...");
			}

			public void onSubmitComplete(FormSubmitCompleteEvent event)
			{
//				String sessionID = event.getResults();
//				/*(Get sessionID from server's response to your login request.)*/
//				final long DURATION = 1000 * 60 * 60 * 24 * 1; //duration remembering login. 1 day
//				Date expires = new Date(System.currentTimeMillis() + DURATION);
//				Cookies.setCookie("sid", sessionID, expires, null, "/", false);
//				Window.alert(event.getResults());
				
				String userGroup = event.getResults();
				if (userGroup.equalsIgnoreCase("Logistics")){
					RootPanel.get("middle").clear();
					RootPanel.get("middle").add(new LogisticsStartPanel());
				}
				else if (userGroup.equalsIgnoreCase("Accountant")){
					RootPanel.get("middle").clear();
					RootPanel.get("middle").add(new AccountantStartPanel());
				} 
				else if (userGroup.equalsIgnoreCase("Administrator")) {
					RootPanel.get("middle").clear();
					RootPanel.get("middle").add(
							new AdministratorStartPanel(
									new AdminViewController(
											new AdminServiceClientImpl())));
				}
				else{
					Window.alert("Log in failed. Please check your user name and password and try again.");
					submitButton.setEnabled(true);
					submitButton.setText("Log in");
				}
			}
		});
	}


}
package com.kemplerEnergy.client.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kemplerEnergy.model.admin.User;
import com.kemplerEnergy.model.admin.Group;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserGroupView extends Composite {
	private AdminViewController Controller;
	private SimplePanel simplePanel;
	private SimplePanel simplePanel_1;
	private VerticalPanel verticalPanel_1;
	private FlexTable flexTable_1;
	public UserGroupView(AdminViewController Controller) {
		this.Controller= Controller;
		final TabPanel tabPanel = new TabPanel();
		initWidget(tabPanel);
		tabPanel.setWidth("100%");

		final VerticalPanel verticalPanel = new VerticalPanel();
		tabPanel.add(verticalPanel, "Manage Users");
		verticalPanel.setWidth("100%");

		final FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		flexTable.setWidth("100%");
		verticalPanel.setCellWidth(flexTable, "100%");

		final RadioButton addRadioButton = new RadioButton("new-group");
		flexTable.setWidget(0, 0, addRadioButton);
		addRadioButton.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowAction("Add");
			}
		});
		addRadioButton.setText("Add");

		final RadioButton modifyRadioButton = new RadioButton("new-group");
		flexTable.setWidget(0, 1, modifyRadioButton);
		modifyRadioButton.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowAction("Modify");
			}
		});
		modifyRadioButton.setText("Modify");
		
		

		simplePanel = new SimplePanel();
		verticalPanel.add(simplePanel);
		verticalPanel.setCellWidth(simplePanel, "100%");
		simplePanel.setWidth("100%");
		simplePanel.setVisible(false);

		verticalPanel_1 = new VerticalPanel();
		simplePanel.setWidget(verticalPanel_1);
		verticalPanel_1.setSize("100%", "100%");
		final ListBox listBox_2 = new ListBox();
		verticalPanel_1.add(listBox_2);
		listBox_2.addChangeListener(new ChangeListener() {
			public void onChange(final Widget sender) {
				AddNewUsers(listBox_2.getValue(listBox_2.getSelectedIndex()));
			}
		});
		
		listBox_2.addItem("select number to be added");
		listBox_2.addItem("1");
		listBox_2.addItem("2");
		listBox_2.addItem("3");
		listBox_2.addItem("4");
		listBox_2.addItem("5");

		simplePanel_1 = new SimplePanel();
		verticalPanel.add(simplePanel_1);
		simplePanel_1.setWidth("100%");
		verticalPanel.setCellWidth(simplePanel_1, "100%");
		simplePanel_1.setVisible(false);
		

		flexTable_1 = new FlexTable();
		simplePanel_1.setWidget(flexTable_1);
		flexTable_1.setSize("100%", "100%");

		final Label accountantLabel = new Label("Accountant");
		flexTable_1.setWidget(1, 0, accountantLabel);

		final Label user1Label = new Label("User1");
		flexTable_1.setWidget(1, 1, user1Label);

		final Label label_2 = new Label("*****");
		flexTable_1.setWidget(1, 2, label_2);

		final Label user1user1comLabel = new Label("user1@user1.com");
		flexTable_1.setWidget(1, 3, user1user1comLabel);

		final Label user1yahoocomLabel = new Label("user1@yahoo.com");
		flexTable_1.setWidget(1, 4, user1yahoocomLabel);

		final Label label_5 = new Label("555-555-55555");
		flexTable_1.setWidget(1, 5, label_5);

		final Label label_6 = new Label("555-555-55555");
		flexTable_1.setWidget(1, 6, label_6);

		final Hyperlink deleteHyperlink = new Hyperlink("Delete", "Delete");
		flexTable_1.setWidget(1, 7, deleteHyperlink);
		deleteHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				EditUser("Delete");
			}
		});

		final Hyperlink editHyperlink = new Hyperlink("Edit", "Edit");
		flexTable_1.setWidget(1, 8, editHyperlink);
		editHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				EditUser("Edit");
			}
		});

		final Label userTypeLabel = new Label("User Type");
		flexTable_1.setWidget(0, 0, userTypeLabel);

		final Label userNameLabel = new Label("User Name");
		flexTable_1.setWidget(0, 1, userNameLabel);

		final Label passwordLabel = new Label("Password");
		flexTable_1.setWidget(0, 2, passwordLabel);

		final Label emailLabel = new Label("Email");
		flexTable_1.setWidget(0, 3, emailLabel);

		final Label yahooImLabel = new Label("Yahoo IM");
		flexTable_1.setWidget(0, 4, yahooImLabel);

		final Label phoneLabel = new Label("Phone");
		flexTable_1.setWidget(0, 5, phoneLabel);

		final Label mobileLabel = new Label("Mobile");
		flexTable_1.setWidget(0, 6, mobileLabel);
		
		
		tabPanel.selectTab(0);
		setWidth("100%");
		
	}
	
	
	void AddNewUsers(String number)
	{
		if ((verticalPanel_1.getWidgetCount()==2)||(number.equals("select number to be added")))
			verticalPanel_1.remove(1);
		final int addNumber;
		final FlexTable flexTable = new FlexTable();
		flexTable.setSize("100%", "100%");
		flexTable.clear();
		
		verticalPanel_1.add(flexTable);
		verticalPanel_1.setCellWidth(flexTable, "100%");
		
		if (!number.equals("select number to be added"))
		{
			final UserRemoteAsync svc = UserRemote.Util.getInstance();
			final List<String> groupList = new ArrayList<String>();
			
			addNumber=Integer.valueOf(number);
			
			Label label = new Label("User Type");
			Label labelUserName = new Label("User Name");
			Label labelPassword = new Label("Password");
			Label labelFirstName= new Label ("First Name");
			Label labelName = new Label("last Name");
			Label labelEmail = new Label("Email");
			Label labelYahooIM = new Label("YahooIM");
			Label labelPhone = new Label("Phone");
			Label labelMobile = new Label("Mobile");
			flexTable.setWidget(0, 0, label);
			flexTable.getCellFormatter().setWidth(0,0,"12%");
			flexTable.setWidget(0, 1, labelUserName);
			flexTable.getCellFormatter().setWidth(0,1,"11%");
			flexTable.setWidget(0, 2, labelPassword);
			flexTable.getCellFormatter().setWidth(0,2,"11%");
			flexTable.setWidget(0, 3, labelFirstName);
			flexTable.getCellFormatter().setWidth(0,3,"11%");
			flexTable.setWidget(0, 4, labelName);
			flexTable.getCellFormatter().setWidth(0,4,"11%");
			flexTable.setWidget(0, 5, labelEmail);
			flexTable.getCellFormatter().setWidth(0,5,"11%");
			flexTable.setWidget(0, 6, labelYahooIM);
			flexTable.getCellFormatter().setWidth(0,6,"11%");
			flexTable.setWidget(0, 7, labelPhone);
			flexTable.getCellFormatter().setWidth(0,7,"11%");
			flexTable.setWidget(0, 8, labelMobile);
			flexTable.getCellFormatter().setWidth(0,8,"11%");
			
			svc.getGroupList(new AsyncCallback<ArrayList<Group>>() {

				public void onFailure(Throwable caught) {
					Window.alert("Failed to retrieve Group name: " + caught.getMessage());						
				}

				public void onSuccess(ArrayList<Group> result) {
					for (Group g: result) {
						groupList.add(g.getName());
					}
					createTable(addNumber, flexTable, groupList);
				}
				
			});
			

			final Button buttonAdd = new Button();
			buttonAdd.setText("Add");
			flexTable.setWidget(addNumber+1, 3, buttonAdd);
			buttonAdd.setWidth("100%");
			buttonAdd.addClickListener(new ClickListener() {
				public void onClick(Widget sender) {
					buttonAdd.setEnabled(false);
					ArrayList<User> users = new ArrayList<User>();
					for (int rownum=1; rownum<=addNumber;rownum++) {
							ListBox userType;
							userType = (ListBox) flexTable.getWidget(rownum, 0);
							Group usergroup = new Group();
							usergroup.setName(userType.getItemText(userType.getSelectedIndex()));
							Set<Group> group= new HashSet<Group>();
							group.add(usergroup);
							User user = new User();
							user.setGroups(group);
							TextBox textBoxuserinfo = new TextBox();
							textBoxuserinfo = (TextBox)flexTable.getWidget(rownum, 1);
							user.setUserName(textBoxuserinfo.getText());
							textBoxuserinfo = (TextBox)flexTable.getWidget(rownum, 2);
							user.setPassword(textBoxuserinfo.getText());
							textBoxuserinfo = (TextBox)flexTable.getWidget(rownum, 3);
							user.setFirstName(textBoxuserinfo.getText());
							textBoxuserinfo = (TextBox)flexTable.getWidget(rownum, 4);
							user.setLastName(textBoxuserinfo.getText());
							textBoxuserinfo = (TextBox)flexTable.getWidget(rownum, 5);
							user.setEmail(textBoxuserinfo.getText());
							textBoxuserinfo = (TextBox)flexTable.getWidget(rownum, 6);
							user.setIM(textBoxuserinfo.getText());
							textBoxuserinfo = (TextBox)flexTable.getWidget(rownum, 7);
							user.setPhone(textBoxuserinfo.getText());
							textBoxuserinfo = (TextBox)flexTable.getWidget(rownum, 8);
							users.add(user);	
						}
					for(User u: users)
						Window.alert(u.getUserName());
				final UserRemoteAsync rvc = UserRemote.Util.getInstance();
				rvc.saveUser(users, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert("Failed to save User!");
						buttonAdd.setEnabled(true);

					}

					public void onSuccess(Void result) {
						Window.alert("Save successfully!");
						buttonAdd.setEnabled(true);

					}
				});
				
				}
				
			});
		}
	}


	private void createTable(int AddNumber, FlexTable flexTable,
			final List<String> groupList) {
		for(int i=1;i<=AddNumber;i++)
		{
			ListBox listBox = new ListBox();
			listBox.setWidth("90%");
			for (String groupName: groupList) {
				listBox.addItem(groupName);
				
			}
			listBox.setSelectedIndex(0);
			listBox.setItemSelected(0, true);
			
			flexTable.setWidget(i, 0, listBox);
			flexTable.getCellFormatter().setWidth(i,0,"12%");
			TextBox textBoxusername = new TextBox();
			textBoxusername.setWidth("90%");
			PasswordTextBox passwordTextBox=new PasswordTextBox();
			passwordTextBox.setWidth("90%");
			TextBox textBoxemail = new TextBox();
			textBoxemail.setWidth("90%");
			TextBox textBoxyahoo =new TextBox();
			textBoxyahoo.setWidth("90%");
			TextBox textBoxphone= new TextBox();
			textBoxphone.setWidth("90%");
			TextBox textBoxmobile= new TextBox();
			textBoxmobile.setWidth("90%");
			TextBox textBoxfirstname = new TextBox();
			textBoxfirstname.setWidth("90%");
			TextBox textBoxname= new TextBox();
			textBoxname.setWidth("90%");
			flexTable.setWidget(i, 1, textBoxusername);
			flexTable.getCellFormatter().setWidth(i,1,"11%");
			flexTable.setWidget(i, 2, passwordTextBox);
			flexTable.getCellFormatter().setWidth(i,2,"11%");
			flexTable.setWidget(i, 3, textBoxfirstname);
			flexTable.getCellFormatter().setWidth(i,3,"11%");
			flexTable.setWidget(i, 4, textBoxname);
			flexTable.getCellFormatter().setWidth(i,4,"11%");
			flexTable.setWidget(i, 5, textBoxemail);
			flexTable.getCellFormatter().setWidth(i,5,"11%");
			flexTable.setWidget(i, 6, textBoxyahoo);
			flexTable.getCellFormatter().setWidth(i,6,"11%");
			flexTable.setWidget(i, 7, textBoxphone);
			flexTable.getCellFormatter().setWidth(i,7,"11%");
			flexTable.setWidget(i, 8, textBoxmobile);
			flexTable.getCellFormatter().setWidth(i,8,"11%");
		}
	}
	
	
	void ShowAction(String Action)
	{
		if (Action.equals("Add"))
		{
			simplePanel.setVisible(true);
			simplePanel_1.setVisible(false);
		}
		if (Action.equals("Modify"))
		{
			simplePanel.setVisible(false);
			simplePanel_1.setVisible(true);
		}	
	}
	
	void EditUser(String DoE)
	{
		final PopupPanel popup = new PopupPanel(false);
	    VerticalPanel PopUpPanelContents = new VerticalPanel();

	    ClickListener listener = new ClickListener()
	    {
	        public void onClick(Widget sender)
	        {
	            popup.hide();
	        }
	    };
	    ClickListener listenerD = new ClickListener()
	    {
	        public void onClick(Widget sender)
	        {
	        	flexTable_1.removeCells(1, 0, 9);
	        	popup.hide();
	        }
	    };
	    
	    VerticalPanel holder = new VerticalPanel();
	    
	    final FlexTable flexTable_1 = new FlexTable();
	    holder.add(flexTable_1);
		flexTable_1.setWidth("100%");
		holder.setCellWidth(flexTable_1, "100%");
		if (DoE.equals("Edit"))
		{
			final ListBox listBox_2 = new ListBox();
			flexTable_1.setWidget(0, 0, listBox_2);
			listBox_2.addItem("Accountant");
	
			final TextBox textBox = new TextBox();
			flexTable_1.setWidget(1, 0, textBox);
			textBox.setText("User1");
			textBox.setWidth("100%");
	
			final TextBox textBox_1 = new TextBox();
			flexTable_1.setWidget(2, 0, textBox_1);
			textBox_1.setText("*****");
			textBox_1.setWidth("100%");
	
			final TextBox textBox_2 = new TextBox();
			flexTable_1.setWidget(3, 0, textBox_2);
			textBox_2.setText("user1@user1.com");
			textBox_2.setWidth("100%");
	
			final TextBox textBox_3 = new TextBox();
			flexTable_1.setWidget(4, 0, textBox_3);
			textBox_3.setText("user1@yahoo.com");
			textBox_3.setWidth("100%");
	
			final TextBox textBox_4 = new TextBox();
			flexTable_1.setWidget(5, 0, textBox_4);
			textBox_4.setText("555-555-55555");
			textBox_4.setWidth("100%");
	
			final TextBox textBox_5 = new TextBox();
			flexTable_1.setWidget(6, 0, textBox_5);
			textBox_5.setText("555-555-55555");
			textBox_5.setWidth("100%");
	
			final Button buttonN = new Button();
			flexTable_1.setWidget(7, 0, buttonN);
			buttonN.setText("Modify");
			Button button = new Button("Close", listener);
			flexTable_1.setWidget(7, 1,button);
		}
		else
		{
			final Label doYouWantLabel = new Label("Do you want to delete this user?");
			flexTable_1.setWidget(0, 0, doYouWantLabel);
			flexTable_1.getFlexCellFormatter().setColSpan(0,0,2);
			final Button buttonY = new Button("YES",listenerD);
			flexTable_1.setWidget(0, 1, buttonY);
			final Button buttonN = new Button("NO",listener);
			flexTable_1.setWidget(0, 2, buttonN);
			
		}
	    PopUpPanelContents.add(holder);
	    popup.setWidget(PopUpPanelContents);
	    popup.center();



	}

}


	
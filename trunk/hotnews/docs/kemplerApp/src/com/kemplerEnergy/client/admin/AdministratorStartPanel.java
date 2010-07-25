package com.kemplerEnergy.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AdministratorStartPanel extends Composite {
	private AdminViewController controller;
	private CounterPartyProfileView counterpartyprofile;
	private CommodityProfileView commodityprofile;
	private MarketZoneProfileView marketzoneprofile;
	private DefaultMessagesView defaultmessage;
	private KemplerVehiclesProfileView kemplervehiclesprofile;
	private TradingAccountsView tradingaccounts;
	private UserGroupView usergroup;
	private VerticalPanel verticalPanel;
	private DockPanel dockPanel;
	private Panel simplePanel;
	
	public AdministratorStartPanel(AdminViewController controller) {
		this.controller = controller;
		dockPanel= new DockPanel();
		verticalPanel = new VerticalPanel();
		initWidget(dockPanel);
		dockPanel.setWidth("100%");
		dockPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		dockPanel.add(horizontalPanel, DockPanel.NORTH);
		horizontalPanel.setWidth("100%");
		dockPanel.setCellWidth(horizontalPanel, "100%");

		final Label userLabel = new Label("User Name: aaaaa");
		horizontalPanel.add(userLabel);
		horizontalPanel.setCellWidth(userLabel, "100%");
		userLabel.setWidth("100%");
		horizontalPanel.setCellHorizontalAlignment(userLabel, HasHorizontalAlignment.ALIGN_RIGHT);
		
		
		dockPanel.add(verticalPanel, DockPanel.CENTER);
		dockPanel.setCellWidth(verticalPanel, "90%");
		
		dockPanel.setCellHorizontalAlignment(verticalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setWidth("100%");
		verticalPanel.setHeight("100%");

		final StackPanel stackPanel = new StackPanel();
		dockPanel.add(stackPanel, DockPanel.WEST);
		stackPanel.setWidth("100%");
		dockPanel.setCellHorizontalAlignment(stackPanel, HasHorizontalAlignment.ALIGN_CENTER);
		dockPanel.setCellWidth(stackPanel, "15%");

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		stackPanel.add(verticalPanel_1, "Admin");
		verticalPanel_1.setWidth("100%");

		final Hyperlink hyperlink = new Hyperlink("User And Group", "User And Group");
		verticalPanel_1.add(hyperlink);
		hyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowComposite("User And Group");
			}
		});
		

		final Hyperlink counterPartyProfileHyperlink = new Hyperlink("Counter Party Profile", "Counter Party Profile");
		counterPartyProfileHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowComposite("Counter Party Profile");
			}
		});
		
		verticalPanel_1.add(counterPartyProfileHyperlink);
		
		final Hyperlink signOutHyperlink = new Hyperlink("Sign Out", "some history token");
		verticalPanel_1.add(signOutHyperlink);

		
		stackPanel.add(signOutHyperlink, "Sign In/Sign Out");
		

		final Hyperlink commodityProfileHyperlink = new Hyperlink("Commodity Profile", "Commodity Profile");
		verticalPanel_1.add(commodityProfileHyperlink);
		commodityProfileHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowComposite("Commodity Profile");
			}
		});

		final Hyperlink marketZoneProfileHyperlink = new Hyperlink("Market Zone Profile", "Market Zone Profile");
		verticalPanel_1.add(marketZoneProfileHyperlink);
		marketZoneProfileHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowComposite("Market Zone Profile");
				
			}
		});

		final Hyperlink kemplerVehiclesProfileHyperlink = new Hyperlink("Vehicles Profile", "Vehicles Profile");
		verticalPanel_1.add(kemplerVehiclesProfileHyperlink);
		kemplerVehiclesProfileHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowComposite("Vehicles Profile");
			}
		});
		
		final Hyperlink defaultMessagesHyperlink = new Hyperlink("Default Messages", "Default Messages");
		verticalPanel_1.add(defaultMessagesHyperlink);
		defaultMessagesHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowComposite("Default Messages");
			}
		});

		final Hyperlink tradingAccountsHyperlink = new Hyperlink("Trading Accounts", "Trading Accounts");
		verticalPanel_1.add(tradingAccountsHyperlink);
		tradingAccountsHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
				ShowComposite("Trading Accounts");
				
			}
		});

		final Hyperlink marketDataHyperlink = new Hyperlink("Market Data", "some history token");
		verticalPanel_1.add(marketDataHyperlink);
		signOutHyperlink.addClickListener(new ClickListener() {
			public void onClick(final Widget sender) {
	
			}
		});

		simplePanel = new SimplePanel();
		verticalPanel.add(simplePanel);
		verticalPanel.setCellWidth(simplePanel, "100%");
		simplePanel.setSize("100%", "100%");
		verticalPanel.setCellHorizontalAlignment(simplePanel, HasHorizontalAlignment.ALIGN_CENTER);

		
		
	}
	private void ShowComposite(String CompositeName){
		simplePanel.clear();
		if (CompositeName.equals("Counter Party Profile")){
			counterpartyprofile = new CounterPartyProfileView(controller);
			simplePanel.add(counterpartyprofile);
			return;
		}
		if (CompositeName.equals("Commodity Profile")){
			commodityprofile = new CommodityProfileView(controller);
			simplePanel.add(commodityprofile);
			return;
		}
		if (CompositeName.equals("Market Zone Profile")){
			marketzoneprofile = new MarketZoneProfileView(controller);
			simplePanel.add(marketzoneprofile);
			return;
		}
		if (CompositeName.equals("Vehicles Profile")){
			kemplervehiclesprofile = new KemplerVehiclesProfileView(controller);
			simplePanel.add(kemplervehiclesprofile);
			return;
		}
		
		if (CompositeName.equals("Default Messages")){
			defaultmessage = new DefaultMessagesView(controller);
			simplePanel.add(defaultmessage);
			return;
		}
		if (CompositeName.equals("Trading Accounts")){
			tradingaccounts = new TradingAccountsView(controller);
			simplePanel.add(tradingaccounts);
			return;
		}
		if (CompositeName.equals("User And Group")){
			usergroup = new UserGroupView(controller);
			simplePanel.add(usergroup);
			return;
		}
		
	}
	

	
}

package com.kemplerEnergy.client.rins;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Temporary class to provide user selection function
 * before Login control is enabled
 * @author sxycode
 *
 */
public class RINsStartPanel extends Composite{
	
	RootPanel rootPanel = RootPanel.get("middle");
	private VerticalPanel accountChoosingPanel = new VerticalPanel();
	private AccountantStartPanel accountantStartPanel;
	private LogisticsStartPanel logisticsStartPanel;
	private Button logisticsButton = new Button("Logistics");
	private Button accountantButton = new Button("accountant");
	private final String HEIGHT = "100px";
	
	public RINsStartPanel(){
		accountChoosingPanel.setHeight(HEIGHT);
		accountChoosingPanel.add(logisticsButton);
		accountChoosingPanel.add(accountantButton);
		this.initWidget(accountChoosingPanel);
		addListeners();
	}

	private void addListeners() {
		logisticsButton.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				logisticsStartPanel = new LogisticsStartPanel();
				rootPanel.clear();
				rootPanel.add(logisticsStartPanel);
			}
		});
		accountantButton.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				accountantStartPanel = new AccountantStartPanel();
				rootPanel.clear();
				rootPanel.add(accountantStartPanel);
			}
		});
	}
	public VerticalPanel getAccountChoosingPanel() {
		return accountChoosingPanel;
	}
}

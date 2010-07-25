package com.hotnews.project.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class hotnews implements EntryPoint {
	private Button clickMeButton;
	private Button secondButton;
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get("left");
		RootPanel rootPanel2 = RootPanel.get("middle");
		clickMeButton = new Button();
		secondButton = new Button();
		rootPanel.add(clickMeButton);
		
		rootPanel2.add(secondButton);
		clickMeButton.setText("Click me!");
		secondButton.setText("I am the second one");
		clickMeButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				Window.alert("Hello, GWT World!");
			}
		});
		secondButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				Window.alert("Hello, GWT second!");
			}
		});
	}
}

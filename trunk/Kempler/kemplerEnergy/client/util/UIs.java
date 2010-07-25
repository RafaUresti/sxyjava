package com.kemplerEnergy.client.util;

import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UIs {
	
	/**
	 * Sets background text of a text box to DEFAULT_TEXT, which disappears
	 * upon click and change of the textBox
	 * @param textBox
	 * @param DEFAULT_TEXT
	 */
	public static void addDefaultTextFocusListener(final TextBox textBox,
			final String DEFAULT_TEXT) {

		textBox.addFocusListener(new FocusListener(){
			public void onFocus(Widget arg0) {
				String entry = textBox.getText().trim();
				if (entry.equalsIgnoreCase(DEFAULT_TEXT))
					textBox.setText("");
			}
			public void onLostFocus(Widget arg0) {
				String entry = textBox.getText().trim();
				if (entry.isEmpty())
					textBox.setText(DEFAULT_TEXT);
			}
		});
	}

	/*
	 * panels for spacing purposes, H=height,W=width,number = pixels,a/b/c=versions
	 */
	public static Panel getSPH10(){
		final Panel spH10a = new SimplePanel();
		spH10a.setHeight("10px");
		return spH10a;
	}
	public static Panel getSPW100(){
		final Panel spW100 = new SimplePanel();
		spW100.setWidth("100px");
		return spW100;
	}
	public static Panel getSPW30(){
		final SimplePanel spacingPanel = new SimplePanel();
		spacingPanel.setWidth("30");
		return spacingPanel;
	}
}

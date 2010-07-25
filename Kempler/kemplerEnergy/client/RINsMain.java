package com.kemplerEnergy.client;

import java.util.ArrayList;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget; //import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.ClickListener;
//import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.ListBox;
//import com.google.gwt.user.client.ui.RootPanel;
//import com.google.gwt.user.client.ui.ScrollPanel;
//import com.google.gwt.user.client.ui.TabBar;
//import com.google.gwt.user.client.ui.TabPanel;
//import com.google.gwt.user.client.ui.TextArea;
//import com.google.gwt.user.client.ui.Widget;
//import com.google.gwt.user.client.ui.HTMLTable;
//import com.google.gwt.user.client.ui.TextBoxBase;
//import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.kemplerEnergy.client.rins.RINInvoiceEntryPanel;
import com.kemplerEnergy.client.rins.RINsRPC;
import com.kemplerEnergy.client.rins.RINsRPCAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RINsMain implements EntryPoint {

	private RINInvoiceEntryPanel rinInvoiceEntryPanel;
	final Label version = new Label(); //added to get the version number on the login screen
	final RINsRPCAsync svc = RINsRPC.Util.getInstance();
	// private TextBox tb = new TextBox();
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		/* Start of my code */
//		RINsStartPanel sp = new RINsStartPanel();
//		RootPanel.get("middle").add(sp);
		
		LoginControlPanel lcp = new LoginControlPanel();
		RootPanel.get("middle").add(version);//adding the label to the root panel
		RootPanel.get("middle").add(lcp);
		
		// to get the version number
		svc.getVersionDetail(new AsyncCallback<String>()
		{
			public void onFailure(Throwable arg0) {
				//Window.alert(arg0.getMessage());
			}	
			public void onSuccess(String arg0) {
				version.setText("Version " + arg0);		
			}
		});
		//RootPanel.get("bottom").add(version);

	}
}
// DOM.setStyleAttribute(tb.getElement(), "backgroundColor", "yellow");
/*
 * Hao's code of onModuleLoad() final Button sampleBLocal = new Button("Local
 * Sample"); final Button sampleBRPC = new Button("RPC Sample"); final ListBox
 * lBox = new ListBox(); final Button processButn = new Button("Process"); final
 * TextArea inputTextArea = new TextArea(); final TabBar bar = new TabBar();
 * sampleBLocal.addClickListener(new ClickListener() { public void
 * onClick(Widget sender) { Window.alert("sample"); } });
 * sampleBRPC.addClickListener(new ClickListener(){ public void onClick(Widget
 * sender){ // greet("Hao Dang"); sampleComplexResult(); } });
 * processButn.addClickListener(new ClickListener(){ public void onClick(Widget
 * sender){ inputTextArea.getText(); } }); bar.addTab("Tab 1"); bar.addTab("Tab
 * 2"); RootPanel.get("upperRight").add(bar); lBox.setVisibleItemCount(10);
 * lBox.setWidth("300px"); RootPanel.get("left").add(lBox);
 * RootPanel.get("middle").add(inputTextArea);
 * RootPanel.get("middle").add(processButn);
 * RootPanel.get("sampleLocal").add(sampleBLocal);
 * RootPanel.get("sampleRPC").add(sampleBRPC);
 * 
 * TabPanel tp = new TabPanel(); tp.add(new HTML("Foo"), "foo"); tp.add(new
 * HTML("Bar"), "bar"); tp.add(new HTML("Baz"), "baz"); // Show the 'bar' tab
 * initially. tp.selectTab(1); // Add it to the root panel.
 * RootPanel.get().add(tp); } public void greet(String name) { RINsRPCAsync svc =
 * (RINsRPCAsync) GWT.create(RINsRPC.class); ServiceDefTarget endpoint =
 * (ServiceDefTarget) svc;//endpoint(server).
 * endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "RINsRPC");//entry
 * point of server AsyncCallback callback = new AsyncCallback() { public void
 * onSuccess(Object result) { RootPanel.get().add(new HTML(result.toString())); }
 * public void onFailure(Throwable ex) { RootPanel.get().add(new
 * HTML(ex.toString())); } }; svc.greeting(name, callback); } public void
 * sampleComplexResult() { RINsRPCAsync svc = (RINsRPCAsync)
 * GWT.create(RINsRPC.class); ServiceDefTarget endpoint = (ServiceDefTarget)
 * svc; endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "RINsRPC");
 * AsyncCallback callback = new AsyncCallback() { public void onSuccess(Object
 * result) { SampleResult sr = (SampleResult) result; RootPanel.get().add( new
 * HTML(String.valueOf(sr.type) + " " + sr.name + " " +
 * sr.resultArrayList.get(0))); } public void onFailure(Throwable ex) {
 * RootPanel.get().add(new HTML(ex.toString())); } }; ArrayList<String> al =
 * new ArrayList(); al.add("TEST"); svc.getSampleResult(0, "test", al,
 * callback); }
 */


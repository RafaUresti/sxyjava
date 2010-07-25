package com.kemplerEnergy.client.rins;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestionEvent;
import com.google.gwt.user.client.ui.SuggestionHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kemplerEnergy.model.EPAPartner;

/**
 * Panel to input EPA partner full name to retrieve EPA Partner information
 * from DB 
 * @author sxycode
 *
 */
public class EPAPartnerSuggestionPanel extends Composite{
	final RINsRPCAsync svc = RINsRPC.Util.getInstance();
	
	private VerticalPanel vPanel = new VerticalPanel();
	
	private EPAPartner epaPartner;
	private ArrayList<EPAPartner> epaPartnerList = new ArrayList<EPAPartner>();
	private ArrayList<String> epaPartnerNameList = new ArrayList<String>();
	private Label epaNumberLabel = new Label ("EPA #:");
	private Label epaPartnerNameLabel = new Label("EPA Partner Name:");

	private MultiWordSuggestOracle epaPartnerOracle = new MultiWordSuggestOracle();
	private SuggestBox epaPartnerNameBox = new SuggestBox(epaPartnerOracle);


	private HorizontalPanel epaPartnerPanel = new HorizontalPanel();
	private Button getEPAButton =  new Button("Get EPA#");
	final String CELL_WIDTH = "200px", CELL_HEIGHT = "30px";

	public EPAPartnerSuggestionPanel (){

		vPanel.add(epaPartnerPanel);
		vPanel.add(epaNumberLabel);
		this.initWidget(vPanel);
		
		epaPartnerPanel.add(epaPartnerNameLabel);
		epaPartnerPanel.add(epaPartnerNameBox);
		epaPartnerPanel.add(getEPAButton);
		epaPartnerPanel.setCellWidth(epaPartnerNameLabel, CELL_WIDTH);
		epaPartnerPanel.setCellHeight(epaPartnerNameLabel, CELL_HEIGHT);
		epaPartnerNameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		epaNumberLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		//Get list for company name list
		svc.getEPAPartnerList(new AsyncCallback<ArrayList<EPAPartner>>(){
			public void onFailure(Throwable caught){
				Window.alert("Failed to retrieve RIN EPA Partner List: " + caught.getMessage());
			}
			public void onSuccess(ArrayList<EPAPartner> result){
				EPAPartnerSuggestionPanel.this.epaPartnerList = result;
				try{
					for (EPAPartner c: EPAPartnerSuggestionPanel.this.epaPartnerList)
						EPAPartnerSuggestionPanel.this.epaPartnerNameList.add(c.getFullName());
					EPAPartnerSuggestionPanel.this.epaPartnerOracle.addAll(EPAPartnerSuggestionPanel.this.epaPartnerNameList);
				}catch(NullPointerException e){
					Window.alert("EPA Partner List is empty!");
				}
			}
		});
		addListeners();
	}




	private void addListeners() {
		epaPartnerNameBox.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender){
				EPAPartnerSuggestionPanel.this.getEPAButton.setEnabled(true);
			}
		});

		epaPartnerNameBox.addEventHandler(new SuggestionHandler(){
			public void onSuggestionSelected(SuggestionEvent event) {
				EPAPartnerSuggestionPanel.this.getEPAButton.setEnabled(true);
				EPAPartnerSuggestionPanel.this.epaNumberLabel.setText("EPA #: ");
			}
		});

		getEPAButton.addClickListener(new ClickListener(){
			/**
			 * Gets the EPA Partner that matches the name provided
			 */
			public void onClick(Widget sender) {
				EPAPartnerSuggestionPanel.this.epaPartner = null;
				EPAPartnerSuggestionPanel.this.epaPartner = getEPAPartner(EPAPartnerSuggestionPanel.this.epaPartnerNameBox.getText().trim());
				if (EPAPartnerSuggestionPanel.this.epaPartner == null){
					Window.alert("No EPA# for this EPA Partner!");
				}else{
					EPAPartnerSuggestionPanel.this.getEPAButton.setEnabled(false);
					EPAPartnerSuggestionPanel.this.epaNumberLabel.setText("EPA #: "+String.valueOf(EPAPartnerSuggestionPanel.this.epaPartner.getEpaNo()));
//					Window.alert(EPAPartnerSuggestionPanel.this.epaPartner.returnDefaultAddress()+"");
				}
			}

			private EPAPartner getEPAPartner(String epaPartnerName) {
				for (EPAPartner c: EPAPartnerSuggestionPanel.this.epaPartnerList){
					if (epaPartnerName.equalsIgnoreCase(c.getFullName())){
						return c;
					}
				}
				return null;
			}
		});

		/**
		 * Once EPA Partner name is changed, user needs to reverify the EPA Partner and get the EPA
		 */
		epaPartnerNameBox.addChangeListener(new ChangeListener(){
			public void onChange(Widget sender) {
				EPAPartnerSuggestionPanel.this.getEPAButton.setEnabled(true);
				EPAPartnerSuggestionPanel.this.epaNumberLabel.setText("EPA #: ");
			}
		});
	}
	public EPAPartner getEPAPartner() {
		return epaPartner;
	}
	public void setEPAPartner(EPAPartner epa){
		epaPartner = epa;
	}
	public Button getGetEPAButton() {
		return getEPAButton;
	}
	public Label getEpaPartnerNameLabel() {
		return epaPartnerNameLabel;
	}
	

	public HorizontalPanel getEpaPartnerPanel() {
		return epaPartnerPanel;
	}
	public SuggestBox getEpaPartnerNameBox() {
		return epaPartnerNameBox;
	}


}
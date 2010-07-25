package com.kemplerEnergy.client.rins;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINParser;

/**
 * Row in the manual entry table of RINsEntryPanel
 * @author  Xiaoyi Sheng
 */
public class RINEntryRow extends HorizontalPanel{
	private Button nextButton = new Button("Update/Next");
	private Button deleteButton = new Button("Delete");
	private Label statusLabel = new Label("");

	private int[] rinComponentLengths = RINParser.getRinComponentLengths();
	private TextBox[] rinComponentBoxes = new TextBox[9];
	private boolean verified = false;//used in RINsVerification panel to mark verification of each row
	private int rinGallons = 0;

	public RINEntryRow(){
		this.setSpacing(5);
		createRow();
	}

	private void createRow() {
		//		this.prepareRow(0);//create the row
		for (int i = 0; i < 9; i++){
			rinComponentBoxes[i] = new TextBox();
			rinComponentBoxes[i].setMaxLength(rinComponentLengths[i]);
			rinComponentBoxes[i].setWidth(rinComponentLengths[i]*8 + 6 +"px");
			this.add(rinComponentBoxes[i]);	
			rinComponentBoxes[i].addChangeListener(new ChangeListener(){
				public void onChange(Widget sender) {
					nextButton.setEnabled(true);//if the data is changed, enable the button to update the data
				}

			});
		}
		this.add(nextButton);
		this.add(deleteButton);//prevent deletion of last row	
		this.add(statusLabel);
	}

	boolean calculateGallons() {
		try{
			int equivalenceValue =Integer.valueOf(rinComponentBoxes[5].getText());
			int endingGallon = Integer.valueOf(rinComponentBoxes[8].getText());
			int beginningGallon = Integer.valueOf(rinComponentBoxes[7].getText());
			rinGallons = (int)Math.ceil((1+ endingGallon - beginningGallon) * equivalenceValue/10.0);
		}catch (NumberFormatException e){
			Window.alert("Starting/Ending Gallon must be NUMBERS!");
			return false;
		}
		if (rinGallons < 1){
			Window.alert("Ending Gallon must be LARGER than Starting Gallon!");
			return false;
		}
		else return true;
	}

	/**
	 * Shows row status by highlighting the row according to the status of the RIN and set row
	 * to read only unless rin is NEW.
	 * ACCEPTED: LightGrey
	 * AVAILABLE: LightCyan
	 * SPLIT: Bisque
	 * SOLD:LightPink
	 * CORRUPTED: GoldenRod
	 * RETIRED:DarkGoldenRod
	 * NEW: According to validation mask: 0:white, 1: yellow 2: LightGrey
	 * @param rin
	 * @return <code>false</code> if the RIN hasn't been verified by accountant 
	 */
	protected boolean showRowStatus(RIN rin){
		char[] validationMask = rin.getValidationMask();
		verified = true;
		boolean noStatusText = statusLabel.getText().equalsIgnoreCase("");
		if (rin.getRinStatus().equalsIgnoreCase("ACCEPTED")){
			for (int j = 0; j < 9; j ++){
				DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
						"backgroundColor", "LightGrey");
			}
			setReadOnly();
			if (noStatusText)
				statusLabel.setText("ACCEPTED");
			return true;
		}
		if (rin.getRinStatus().equalsIgnoreCase("AVAILABLE")){
			for (int j = 0; j < 9; j ++){
				DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
						"backgroundColor", "LightCyan");
			}
			setReadOnly();
			if (noStatusText)
			statusLabel.setText("AVAILABLE");
			return true;
		}
		if (rin.getRinStatus().equalsIgnoreCase("SPLIT")){
			for (int j = 0; j < 9; j ++){
				DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
						"backgroundColor", "Bisque");
			}
			setReadOnly();
			if (noStatusText)
			statusLabel.setText("SPLIT");
			return true;
		}
		if (rin.getRinStatus().equalsIgnoreCase("SOLD")){
			for (int j = 0; j < 9; j ++){
				DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
						"backgroundColor", "LightPink");
			}
			setReadOnly();
			if (noStatusText)
			statusLabel.setText("SOLD");
			return true;
		}
		if (rin.getRinStatus().equalsIgnoreCase("CORRUPTED")){
			for (int j = 0; j < 9; j ++){
				DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
						"backgroundColor", "GoldenRod");
			}
			setReadOnly();
			//Do not show status as CORRUPTED is a tempory status
			return true;
		}
		if (rin.getRinStatus().equalsIgnoreCase("RETIRED")){
			for (int j = 0; j < 9; j ++){
				DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
						"backgroundColor", "DarkGoldenRod");
			}
			setReadOnly();
			if (noStatusText)
			statusLabel.setText("RETIRED");
			return true;
		}
		if (rin.getRinStatus().equalsIgnoreCase("NEW")){
			for (int j = 0; j < 9; j ++){
				if (validationMask[j] == '1'){
					DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
							"backgroundColor", "yellow");
					verified = false;
				}
				else if (validationMask[j] == '0'){
					DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
							"backgroundColor", "white");
					verified = false;
				}
				else if (validationMask[j] == '2'){
					DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
							"backgroundColor", "LightGrey");
				}
			}
		}
		return verified;
	}

	/**
	 * Highlights a row with a certain color
	 * @param color
	 */
	protected void highlightRow(String color){
		for (int j = 0; j < 9; j ++){
			DOM.setStyleAttribute(rinComponentBoxes[j].getElement(), 
					"backgroundColor", color);
		}
		return;
	}
	
	/**
	 * Check if the row is highlighted by the color
	 * @param color the color to find
	 * @return <code>true</code> if is highlighted
	 */
	protected boolean isHilighted(String color){
		for (int i = 0; i < 9; i ++){
			if (!DOM.getStyleAttribute(rinComponentBoxes[i].getElement(), "backgroundColor").equalsIgnoreCase(color)){
				return false;
			}
		}
		return true;
	}
	/**
	 * Set the row component boxes to be read only and hide
	 * nextbutton and deletebutton if readOnly is true. Reverse
	 * otherwise.
	 */
	protected void setReadOnly(){
//		if (readOnly){
			for (int i = 0; i < 9; i ++)
				rinComponentBoxes[i].setReadOnly(true);
			nextButton.setEnabled(false);
			nextButton.setVisible(false);//cannot simply disable. addRow() will enable it
			deleteButton.setEnabled(false);
			deleteButton.setVisible(false);
//		}else{
//			for (int i = 0; i < 9; i ++)
//				rinComponentBoxes[i].setReadOnly(false);
//			nextButton.setVisible(true);
//			deleteButton.setVisible(true);
//		}
	}


	/**
	 * Makes sure that each entry in the rin component boxes are 
	 * of correct length
	 * @return
	 */
	boolean verifyRINLength() {
		for (int i = 0; i < 9; i++){
			if (rinComponentBoxes[i].getText().length()!= rinComponentLengths[i]){
				Window.alert("Incorrect RIN format!");
				return false;
			}
		}
		return true;

	}			

	/**
	 * @param verified
	 * @uml.property  name="verified"
	 */
	public void setVerified(boolean verified){
		this.verified = verified;
	}
	/**
	 * @return
	 * @uml.property  name="verified"
	 */
	public boolean isVerified(){
		return verified;
	}
	/**
	 * @return
	 * @uml.property  name="deleteButton"
	 */
	public Button getDeleteButton(){
		return deleteButton;
	}
	/**
	 * @return
	 * @uml.property  name="nextButton"
	 */
	public Button getNextButton(){
		return nextButton;
	}
	public void disableNextButton(){
		nextButton.setEnabled(false);
	}
	public void enableNextButton(){
		nextButton.setEnabled(true);
	}
	public void disableDeleteButton(){
		deleteButton.setEnabled(false);
	}
	public void enableDeleteButton(){
		deleteButton.setEnabled(true);
	}
	public int getRINGallons() {
		return rinGallons;
	}
	/**
	 * @return
	 * @uml.property  name="rinComponentBoxes"
	 */
	public TextBox[] getRINComponentBoxes() {
		return rinComponentBoxes;
	}
	

	protected Label getStatusLabel() {
		return statusLabel;
	}
}

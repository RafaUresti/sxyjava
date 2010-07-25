package com.kemplerEnergy.client.rins;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;

public class RINsAvailablePanel extends PopupPanel {

	final RINsRPCAsync svc = RINsRPC.Util.getInstance();
	RIN[] availableRINs;
	Invoice sellInvoice;
	
	final ScrollPanel scrollablePanel = new ScrollPanel();
	final Panel rinsPanel = new VerticalPanel();
	TextBox[] textBoxes;
	
	final Button normalizeButton = new Button("Normalize");

	public RINsAvailablePanel(Invoice sellInvoice) {
						
		// PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
		// If this is set, the panel closes itself automatically when the user
		// clicks outside of it.
		super(false);
		
		this.sellInvoice = sellInvoice;
				
		final Panel verticalPanel = new VerticalPanel();

		final Button saveRefreshButton = new Button("Save and Refresh");
		
		final Button cancelButton = new Button("Close");
		cancelButton.setTitle("Close popup without saving");
		
		final Button resetButton = new Button("Reset");		
		resetButton.setTitle("All indexes will be setted to ZERRO");
		
		saveRefreshButton.addClickListener(new ClickListener() {
			
			public void onClick(Widget arg0) {
				saveRINs();				
			}
		});		
		
		cancelButton.addClickListener(new ClickListener() {
			
			public void onClick(Widget arg0) {
				hide();
			}
		});
		
		normalizeButton.addClickListener(new ClickListener() {
			
			public void onClick(Widget arg0) {
				normalizeIndexes();
			}
		});
		
		resetButton.addClickListener(new ClickListener() {
			
			public void onClick(Widget arg0) {
				if(Window.confirm("Are you sure?"))
				{
					resetIndexes();
				}
			}
		});


		final Panel buttonPanel = new HorizontalPanel();
		
		buttonPanel.add(saveRefreshButton);
		
		buttonPanel.add(new Label(" "));
		buttonPanel.add(normalizeButton);
		
		buttonPanel.add(new Label(" "));
		buttonPanel.add(resetButton);
		
		buttonPanel.add(new Label(" "));
		buttonPanel.add(cancelButton);
		
		verticalPanel.add(buttonPanel);
		
		verticalPanel.add(scrollablePanel);

		scrollablePanel.setWidth("500");
		scrollablePanel.setHeight("500");
		
		setWidget(verticalPanel);
		setAnimationEnabled(true);

		setStyleName("RINsAvailablePanel");
				
		setWidth("500");
		setHeight("500");	
						
		refreshRINs();										
	}	

	/**
	 * Reset indexes 
	 */
	private void resetIndexes()
	{
		
		List<Integer> rinIds = new ArrayList<Integer>();
		List<Integer> rinPositions = new ArrayList<Integer>();
		
		
		for(RIN rin : availableRINs)
		{
			rinIds.add(rin.getId());
			rinPositions.add(0);												
		}
		
		callSaveAvailableRINsPositions(rinIds, rinPositions);
	}
	
	/**
	 * Normalize indexes with step 10
	 */
	private void normalizeIndexes()
	{
		
		List<Integer> rinIds = new ArrayList<Integer>();
		List<Integer> rinPositions = new ArrayList<Integer>();
		
		int i = 0;
		for(RIN rin : availableRINs)
		{
			rinIds.add(rin.getId());
			rinPositions.add(i);						
			
			i+=10;
		}
		
		callSaveAvailableRINsPositions(rinIds, rinPositions);
	}
	
	/**
	 * Save edited RINs
	 */
	private void saveRINs()
	{
		int i = 0;
		List<Integer> rinIds = new ArrayList<Integer>();
		List<Integer> rinPositions = new ArrayList<Integer>();
		
		for(RIN rin : availableRINs)
		{
			rinIds.add(rin.getId());
			rinPositions.add(new Integer( textBoxes[i].getText() ).intValue());						
			i++;
		}
		
		callSaveAvailableRINsPositions(rinIds, rinPositions);
	}
	
	/**
	 * Call RPC to save edited RINs
	 * @param rinIds
	 * @param rinPositions
	 */
	private void callSaveAvailableRINsPositions(List<Integer> rinIds, List<Integer> rinPositions)
	{
		svc.saveAvailableRINsPositions(rinIds, rinPositions, new AsyncCallback<Void>()
				{
					public void onFailure(Throwable arg0) 
					{
						Window.alert("Cannot save RINs: " + arg0.getMessage());
					};
					
					public void onSuccess(Void arg0) {
						
						refreshRINs();
					};
				}
				);
	}

	/**
	 * Refresh panel with RINs
	 */
	private void refreshRINs()
	{
		scrollablePanel.clear();
		scrollablePanel.setWidget(new Label("Please, wait ..."));
		scrollablePanel.ensureVisible(scrollablePanel);
		rinsPanel.clear();
				
		svc.getAvailableRINs(sellInvoice, new AsyncCallback<RIN[]>() {
			public void onFailure(Throwable ex) {
				Window.alert("Cannot retrieve RINs: " + ex.getMessage());
			};

			public void onSuccess(RIN[] rins) {
				
				availableRINs = rins;				
				Panel rinsPanel = new VerticalPanel();
				
				List<TextBox> textBoxesList  = new ArrayList<TextBox>();
				
				for(RIN rin : availableRINs)
				{
					Panel rinPanel = new HorizontalPanel();
					
					TextBox textBox = new TextBox();
					textBox.setText("" + rin.getSaleIndex());
					textBox.setWidth("30");
					
					textBox.addChangeListener(new ChangeListener(){
						
						public void onChange(Widget arg0) {
							
							normalizeButton.setEnabled(false);							
						}
					});
					
					textBoxesList.add(textBox);
					
					rinPanel.add(textBox);
					
					//Label label = new Label(" " + rin.getUniRIN() + " " + new String(rin.getStartGallon()) + " " + new String(rin.getEndGallon()) + " " + rin.getGallonAmount());
					Label labelRIN = new Label(rin.toString());
					
					if(rin.getRinType() == '1')
					{
						labelRIN.setStyleName("RINsAvailableLabelType1");
					}
					else
					{
						labelRIN.setStyleName("RINsAvailableLabelType2");
					}
					
					rinPanel.add(labelRIN);
					
					Label labelGallons = new Label(" " + rin.getGallonAmount());
					labelGallons.setStyleName("RINsAvailableLabelGallons");
					
					rinPanel.add(labelGallons);
					
					rinsPanel.add(rinPanel);
					
				}
				
				textBoxes = textBoxesList.toArray(new TextBox[textBoxesList.size()]);
				
				scrollablePanel.setWidget(rinsPanel);
				
				normalizeButton.setEnabled(true);
			};

		});
	}
		
}

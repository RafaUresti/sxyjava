package com.kemplerEnergy.client.rins;

import java.util.ArrayList;


import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.Invoices;
import com.kemplerEnergy.model.rins.RIN;

/**
 * The table of RIN components used by RINsEntryPanel.
 * @author  Xiaoyi Sheng
 */
public class ManualEntryTable extends VerticalPanel{
	private ArrayList<RINEntryRow> rowList = new ArrayList<RINEntryRow>();//provides indexing for rows
	private Invoice invoice;
	private RINsEntryPanel rinsEntryPanel;
	private ArrayList<RIN> rinList;
	public ManualEntryTable(RINsEntryPanel panel, Invoice invoice){
		super();
		rinsEntryPanel = panel;
		this.invoice = invoice;
		this.rinList = (ArrayList<RIN>) invoice.getRins();
		
		//present Original RINs
		for (RIN r: invoice.getRins()){
			addRow();
			RINEntryRow lastRow =  rowList.get(rowList.size()-1);
			for (int j = 0; j < 9; j ++){
				lastRow.getRINComponentBoxes()[j].setText(r.readComponent()[j]);
				lastRow.getNextButton().setEnabled(false);
			}
		}
		this.setRINTableStatus();
		//The extra empty row for data entry
		addRow();
	}

	private void addRow() {	
		final RINEntryRow row = new RINEntryRow();
		final String[] rinComponents = new String[9];
		this.add(row);
		rowList.add(row);

		if (rowList.size()==1)
			row.disableDeleteButton();//prevents deletion of the only row available
		if (rowList.size()>1){
			for (int i = 0; i < rowList.size()-1; i ++)	//don't enable the delete button of the last row
				rowList.get(i).enableDeleteButton();	
			rowList.get(rowList.size()-1).disableDeleteButton();
		}
		row.getNextButton().addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				int rowIndex;
				if (row.verifyRINLength() && row.calculateGallons()){
					rowIndex = rowList.indexOf(row);//gets updated rowIndex
					row.disableNextButton();
					for (int i = 0; i < 9; i ++){
						rinComponents[i] = row.getRINComponentBoxes()[i].getText();
					}
					if (rowIndex == rowList.size()-1){
						ManualEntryTable.this.addRow();
						RIN rin = new RIN();
						try {
							rin.setRIN(rinComponents);
						} catch (RINException e) {
							Window.alert(e.getMessage());
						}
						invoice.addRIN(rin);
					}
					else {
						try {
							invoice.getRins().get(rowIndex).setRIN(rinComponents);
						} catch (RINException e) {
							Window.alert(e.getMessage());
						}
					}//updates the RIN in the buyInvoice without replacing the RIN object 
					rinsEntryPanel.setActualGallons(invoice.calculateActualGallons());
					setSaveButtonStatus();
				}
			}
		});	
		row.getDeleteButton().addClickListener(new ClickListener(){
			int rowIndex;
			public void onClick(Widget sender) {
				rowIndex = rowList.indexOf(row);//gets updated rowIndex
				if (Window.confirm("Are you sure you want to delete this RIN?")){//confirm before delete
					ManualEntryTable.this.remove(row);
					rowList.remove(row);
					invoice.getRins().remove(rowIndex);
					rinsEntryPanel.setActualGallons(invoice.calculateActualGallons());//refresh the actual gallons
					setSaveButtonStatus();
					if (rowList.size() == 1){
						rowList.get(0).disableDeleteButton();//prevents deleting the only row left
					}
					rowList.get(rowList.size()-1).enableNextButton();//enable the "Next" button on the last row
				}
			}
		});
	}

	/**
	 * Enable the save button if gallons match or the gap is allowed 
	 */
	public void setSaveButtonStatus(){
		if (Invoices.matchGallonNumbers(invoice) || Invoices.matchAllowedGaps(invoice))
			rinsEntryPanel.getSaveButton().setEnabled(true);
		else rinsEntryPanel.getSaveButton().setEnabled(false);
	}
	

	/**
	 * Sets the color and read only status of all the rows according to the attributes
	 * of the corresponding RINs. See RINEntryRow.showRowStatus for detail
	 */
	public void setRINTableStatus(){
		for (int i = 0; i < rinList.size(); i ++){
			rowList.get(i).showRowStatus(rinList.get(i));
		}
	}

	public ArrayList<RINEntryRow> getRowList() {
		return rowList;
	}
	public RINsEntryPanel getRinsEntryPanel() {
		return rinsEntryPanel;
	}

	/**
	 * Check if RIN entry is updated by checking the status
	 * of the NEXT button of each row except for the last empty
	 * row.
	 * @return <code>false</code> if the NEXT button of any row
	 *  other than the last one is enabled
	 */
	public boolean entryUpdated() {
		for (int i = 0; i < rowList.size()-1; i ++){//except for the last row
			if (rowList.get(i).getNextButton().isEnabled()){
				return false;
			}
		}
		return true;
	}

}

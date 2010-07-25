package com.kemplerEnergy.client.rins;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.kemplerEnergy.model.rins.Invoice;

/**
 * FormPanel to upload original PTD document
 * from the vendor.
 * @author Xiaoyi Sheng
 *
 */
public class PTDUpload extends FormPanel{

	private final VerticalPanel vPanel = new VerticalPanel();
	private final FileUpload uploadPTD = new FileUpload();
	private final TextBox invoicePathBox = new TextBox();
	protected RootPanel rootPanel = RootPanel.get("middle");

	public PTDUpload(){
		this.setAction("UploadServlet");
		this.setEncoding(FormPanel.ENCODING_MULTIPART);
		this.setMethod(FormPanel.METHOD_POST);
		
		invoicePathBox.setName("invoicePath");
		invoicePathBox.setVisible(false);
		vPanel.add(invoicePathBox);
		
		uploadPTD.setName("fileName");
		vPanel.add(uploadPTD);
		
		this.add(vPanel);

	}

	/**
	 * Need to be called to before submitting to get
	 * the invoice file path
	 */
	public void generateInvoicePath(Invoice invoice){
		invoicePathBox.setText(invoice.getInvoicePath());
		System.out.println("generating the invoice path: " + invoice.getInvoicePath());
	}
	
	public void addFormHandler(final Invoice invoice, final Button button){
		this.addFormHandler(new FormHandler(){
			public void onSubmit(FormSubmitEvent event) {
				if (!PTDUpload.this.getFilename().endsWith(".pdf")&&
						!PTDUpload.this.getFilename().endsWith(".PDF")){
					Window.alert("Please upload the new PTD PDF file!");
					event.setCancelled(true);
					button.setEnabled(true);
					return;
				}
				button.setEnabled(false);
			}
			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				if (event.getResults().equalsIgnoreCase("SUCCESS")){
					RINsEntryPanel rrp = new RINsEntryPanel(invoice);
					rrp.setTitleFunction("Replace");
					rootPanel.clear();
					rootPanel.add(rrp);
				}
			}
		});
	}
	public String getFilename(){
		return uploadPTD.getFilename();
	}
}

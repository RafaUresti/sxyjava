package com.kemplerEnergy.client.rins;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.kemplerEnergy.model.rins.Invoice;

/**
 * Obsolete class to display RPTD documents generated.
 * @author Xiaoyi Sheng
 *
 */
public class RPTDDisplayPanel extends HorizontalPanel{
	String[] rptdPath = new String[2];
	private Frame rptdK1Frame;
	private Frame rptdK2Frame;
	private final String HEIGHT = "800px", WIDTH = "500px";
	//final static String PATH = "http://209.97.223.189:8080/RINs/DocRetrieve?filePath=";
	final static String PATH = "http://java:8080/RINs/DocRetrieve?filePath=";
	
	RPTDDisplayPanel(Invoice invoice){
//		rptdPath[0] = PATH + invoice.getRptdK1Path();
//		rptdPath[1] = PATH + invoice.getRptdK2Path();XXX
		
		
		if (!(rptdPath[0]==null) && !(rptdPath[0].isEmpty())){
			rptdK1Frame = new Frame(rptdPath[0]);
			rptdK1Frame.setHeight(HEIGHT);
			rptdK1Frame.setWidth(WIDTH);
			this.add(rptdK1Frame);
		}
		if (!(rptdPath[1]==null) && !(rptdPath[1].isEmpty())){
			rptdK2Frame = new Frame(rptdPath[1]);
			rptdK2Frame.setHeight(HEIGHT);
			rptdK2Frame.setWidth(WIDTH);
			this.add(rptdK2Frame);
		}
	}
}

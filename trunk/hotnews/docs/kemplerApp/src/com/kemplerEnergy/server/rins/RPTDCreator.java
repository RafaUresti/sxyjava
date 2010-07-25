package com.kemplerEnergy.server.rins;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.kemplerEnergy.model.Address;
import com.kemplerEnergy.model.CounterParty;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.Invoice.RinType;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


public class RPTDCreator {
//	String baseURL = "http://209.97.223.189:8080/RINs/DocRetrieve?filePath=";
	
	String rptdNO = "RPTD NO.";
	String date = "Transfer Date";
	String invNO = "INVOICE NO.";
	String pathPrefix = "c:\\kemplerDoc\\RINsDir\\";

	//	transferor

	String companyTransferor;
	String addTransferorL1;
	String addTransferorL2;
	String cszTransferor;
	String phoneTransferor;
	String contactTransferor;
	String emailTransferor;
	String epaTransferor;

	//	transferee

	String companyTransferee;
	String addTransfereeL1;
	String addTransfereeL2;
	String cszTransferee;
	String phoneTransferee;
	String contactTransferee;
	String emailTransferee;
	String epaTransferee;

	//	lines

	String isTransfered = "T";
	String trf = "N/A";
	String drf = "N/A";
	String vrf = "N/A";
	String bolTicketNO = "T";
	String invoiceNO = "";
	String gallonAssigned = "N/A";
	String gallonUnAssigned = "N/A";
	String mt = "MODE OF TRANSPORT";

	String k1Original;
	String k2Original;
	List<RIN> rinsListOriginal = new ArrayList<RIN>();
	List<RIN> rinsList1 = new ArrayList<RIN>();
	List<RIN> rinsList2 = new ArrayList<RIN>();
	List<RIN> retiredRinsList1 = new ArrayList<RIN>();
	List<RIN> retiredRinsList2 = new ArrayList<RIN>();
	List<RIN> replacedRinsList1 = new ArrayList<RIN>();
	List<RIN> replacedRinsList2 = new ArrayList<RIN>();
	
	private CounterParty cp;
	private String [] filePath={null, null};
	private String fileDir = "c:\\rptd\\";

	Rectangle border = new Rectangle(0f, 0f);
	Rectangle bordern = new Rectangle(0f, 0f);


	public RPTDCreator(){
	}

	private void splitInvoice(Invoice iv){

		if (iv.getInvoiceType().equalsIgnoreCase("SALE")) {
			rinsList1 = iv.retrieveRINs(RinType.K1, "SOLD");
			rinsList2 = iv.retrieveRINs(RinType.K2, "SOLD");
		} else {
			rinsList1 = iv.retrieveRINs(RinType.K1, "AVAILABLE");
			rinsList1.addAll(iv.retrieveRINs(RinType.K1, "SOLD"));
			rinsList2 = iv.retrieveRINs(RinType.K2, "AVAILABLE");
			rinsList2.addAll(iv.retrieveRINs(RinType.K2, "SOLD"));
		}
		
		retiredRinsList1 = iv.retrieveRINs(RinType.K1, "CORRUPTED");
		replacedRinsList1 = iv.retrieveRINs(RinType.K1, "REPLACING");
		retiredRinsList2 = iv.retrieveRINs(RinType.K2, "CORRUPTED");
		replacedRinsList2 = iv.retrieveRINs(RinType.K2, "REPLACING");
		
	}

	public String[] makeRPTD(Invoice iv){
		
		filePath[0] = null;
		filePath[1] = null;
		
		this.splitInvoice(iv);
//		System.out.println("Making RPTD: K1:" + iv.getRptdK1() + "  K2:" + iv.getRptdK2());
		if(iv.getRptdK1() != null){
//			iv.setRptdK2(null);
			filePath[0] = fileDir + iv.getEpaPartner().getName() + "1.pdf";
			generateRPTD(1, iv, filePath[0]);
		}

		if(iv.getRptdK2() != null){
			filePath[1] = fileDir + iv.getEpaPartner().getName() + "2.pdf";
			generateRPTD(2, iv, filePath[1]);
		}
		
		PDFConcatenate pdfconc = new PDFConcatenate();
		pdfconc.concatenate(filePath, pathPrefix + iv.getRptdPath());
		
		return filePath;
	}

	private void generateRPTD(int type, Invoice iv, String filePath){//, List<RIN> arrayRIN ){
		
		Address address;
		try {
			address = iv.getEpaPartner().returnDefaultAddress();
		} catch (IllegalArgumentException e) {
			address = new Address();
			address.setDefault();
		}

//		System.out.println("type is "+type);

		invNO = iv.getInvoiceNo();
		date = iv.getTransferDate();

		if(type == 1)
			rptdNO = iv.getRptdK1();
		else
			rptdNO = iv.getRptdK2();

		if(iv.getInvoiceType().equalsIgnoreCase("PURCHASE")){
			//deal with buy invoice, Kempler is transferee
			companyTransferee = "Kempler & Co. Inc.";
			addTransfereeL1 = "233 Broadway";
			addTransfereeL2 = "Suite 2705";
			cszTransferee = "New York, NY, 10007";
			phoneTransferee = "(212) 766-7970";
			contactTransferee = "Jeff Marcus";
			emailTransferee = "RINs@KemplerEnergy.com";
			epaTransferee = "4731";
			

			
			companyTransferor = iv.getEpaPartner().getName();
			
			addTransferorL1 = address.getStreetLine1();
			addTransferorL2 = address.getStreetLine2();
			cszTransferor = address.getCity() + " , " +
							address.getState()+ ", " +
							address.getCountry();
			phoneTransferor = String.valueOf(iv.getEpaPartner().getPhone());
			contactTransferor = iv.getEpaPartner().getContact();
			emailTransferor = iv.getEpaPartner().getEmail();
			epaTransferor = String.valueOf(iv.getEpaPartner().getEpaNo());
		}
		else if(iv.getInvoiceType().equalsIgnoreCase("SALE")){
			//			deal with sell invoice, Kempler is transferor
			companyTransferor = "Kempler & Co. Inc.";
			addTransferorL1 = "233 Broadway";
			addTransferorL2 = "Suite 2705";
			cszTransferor = "New York, NY, 10007";
			phoneTransferor = "(212) 766-7970";
			contactTransferor = "Jeff Marcus";
			emailTransferor = "RINs@KemplerEnergy.com";
			epaTransferor = "4731";
			
			companyTransferee = iv.getEpaPartner().getName();
			addTransfereeL1 = address.getStreetLine1();
			addTransfereeL2 = address.getStreetLine2();
			cszTransferee = address.getCity() + " , " +
							address.getState()+ ", " +
							address.getCountry();
			phoneTransferee = String.valueOf(iv.getEpaPartner().getPhone());
			contactTransferee = iv.getEpaPartner().getContact();
			emailTransferee = iv.getEpaPartner().getEmail();
			epaTransferee = String.valueOf(iv.getEpaPartner().getEpaNo());
		}
		else{
			;//			need exception
		}

		/* 
		 * FILL IN
		 */
		
		if (iv.getMode().getMode().equalsIgnoreCase("FIN")) {
			vrf = "N/A"; //Volume of Renewable Fuel Transferred?
			bolTicketNO = "N/A";
		}
		else {
			vrf = String.valueOf(iv.getExpectedGallons()); 
//			System.out.println("BOL size" + iv.getBolInfo().size());
//			System.out.println("BOL info" + iv.getBolInfo());
			
			//BOL Ticket Number For Fuel Transferred?
			bolTicketNO = iv.getBolInfo().toString();
			bolTicketNO = bolTicketNO.substring(1, bolTicketNO.length() - 1);
			
		}
		
		if(type == 2){ // RPTD K2 != NULL
			isTransfered = "No";
			trf = drf = bolTicketNO = "N/A";
			gallonAssigned = "N/A";
			gallonUnAssigned = iv.calculateGallons(RinType.K2) + "";
		}
		else if(type == 1){ // RPTD K1 != NULL
			isTransfered = "Yes";
			trf = "Ethanol"; //Type of Renewable Fuel Transferred withRINs? 
			drf = iv.getTransferDate(); //Date Renewable Fuel was Transferred? TODO
			gallonAssigned = iv.calculateGallons(RinType.K1) + ""; //Assigned Gallon-RINs Being Transferred?
			gallonUnAssigned = "N/A"; //Un-Assigned Gallon-RINs Being Transferred? 
		}
		mt = iv.getMode().getMode(); // Mode of Transport 
		
		String transferInfo = "BOL Ticket";
		if (mt.equalsIgnoreCase("ITT"))
			transferInfo = "Transfer";
		else if (mt.equalsIgnoreCase("BARGE"))
			transferInfo = "COA";
		
		invoiceNO = iv.getInvoiceNo();
		//		no need to exceed here to add other codes to generate this doc.

		border.setBorderWidthLeft(0f);
		border.setBorderWidthBottom(0f);
		border.setBorderWidthRight(0f);
		border.setBorderWidthTop(0f);

		bordern.setBorderWidthLeft(0f);
		bordern.setBorderWidthBottom(0.5f);
		bordern.setBorderWidthRight(0f);
		bordern.setBorderWidthTop(0f);

		try {
			File fp = new File(filePath);
			FileOutputStream outs = new FileOutputStream(fp);

			Document document = new Document(PageSize.A4, 5, 5, 50, 5);

			PdfWriter.getInstance(document, outs);
			document.open();

			PdfPTable tableb = new PdfPTable(1);
			tableb.addCell(makecell(" ", border));


			float[] widthj={50f, 50f};
			PdfPTable tablej = new PdfPTable(widthj);
			
			Image image = Image.getInstance("C:\\kemplerDoc\\RINsDir\\logo.bmp");
			PdfPCell logo = new PdfPCell(image, true);
			logo.setBorderWidth(0f);
			
			tablej.addCell(logo);
//			tablej.addCell(makecell(" ", border));
			PdfPCell title = makecell("RIN Transfer Document - PTD", border, "center", 12);
			
			tablej.addCell(title);
			document.add(tablej);
			document.add(tableb);

			float[] width1= {30f, 30f, 40f};
			PdfPTable table1 = new PdfPTable(width1);
			table1.addCell(makecell("PTD Number", border));
			table1.addCell(makecell(rptdNO, border));
			table1.addCell(makecell("Date  " + date, border));
			document.add(table1);

			float[] width_2= {30f, 70f};
			PdfPTable table_2 = new PdfPTable(width_2);
			table_2.addCell(makecell("Invoice No.", border));
			table_2.addCell(makecell(invNO, border));
			document.add(table_2);
			document.add(tableb);


			PdfPTable table2 = new PdfPTable(2);
			float[] width2={35f, 65f};
			PdfPTable nested1 = new PdfPTable(width2);
			PdfPCell celln = makecell("From:Transferor", border, "center", 10);
			celln.setColspan(2);
			celln.setBackgroundColor(new Color(0x73, 0x99, 0xB1));
			nested1.addCell(celln);
			nested1.addCell(makecell("Company", border));
			nested1.addCell(makecell(companyTransferor, border));
			nested1.addCell(makecell("Address", border));
			nested1.addCell(makecell(addTransferorL1, border));
			nested1.addCell(makecell(" ", border));
			nested1.addCell(makecell(addTransferorL2, border));
			nested1.addCell(makecell("City, State, Zip", border));
			nested1.addCell(makecell(cszTransferor, border));
			nested1.addCell(makecell("Phone", border));
			nested1.addCell(makecell(phoneTransferor, border));
			nested1.addCell(makecell("Contact", border));
			nested1.addCell(makecell(contactTransferor, border));
			nested1.addCell(makecell("Email", border));
			nested1.addCell(makecell(emailTransferor, border));
			nested1.addCell(makecell("EPA Entity Number", border));
			nested1.addCell(makecell(epaTransferor, border));
			PdfPTable nested2 = new PdfPTable(width2);
			PdfPCell cellnn = makecell("To:Transferee", border, "center", 10);
			cellnn.setColspan(2);
			cellnn.setBackgroundColor(new Color(0x73, 0x99, 0xB1));
			nested2.addCell(cellnn);
			nested2.addCell(makecell("Company", border));
			nested2.addCell(makecell(companyTransferee, border));
			nested2.addCell(makecell("Address", border));
			nested2.addCell(makecell(addTransfereeL1, border));
			nested2.addCell(makecell(" ", border));
			nested2.addCell(makecell(addTransfereeL2, border));
			nested2.addCell(makecell("City, State, Zip", border));
			nested2.addCell(makecell(cszTransferee, border));
			nested2.addCell(makecell("Phone", border));
			nested2.addCell(makecell(phoneTransferee, border));
			nested2.addCell(makecell("Contact", border));
			nested2.addCell(makecell(contactTransferee, border));
			nested2.addCell(makecell("Email", border));
			nested2.addCell(makecell(emailTransferee, border));
			nested2.addCell(makecell("EPA Entity Number", border));
			nested2.addCell(makecell(epaTransferee, border));
			table2.addCell(nested1);
			table2.addCell(nested2);
			document.add(table2);
			document.add(tableb);



			float[] width3 = {70f, 30f};
			PdfPTable table3 = new PdfPTable(width3);
			table3.addCell(makecell("Are Assigned RINs being transferred:", bordern));
			table3.addCell(makecell(isTransfered, bordern, "center"));
			table3.addCell(makecell("Type of Renewable Fuel Transferred with RINs:", bordern));
			table3.addCell(makecell(trf, bordern, "center"));
			table3.addCell(makecell("Date Renewable Fuel was Transferred:", bordern));
			table3.addCell(makecell(drf, bordern, "center"));
			table3.addCell(makecell("Volume of Renewable Fuel Transferred:", bordern));
			table3.addCell(makecell(vrf, bordern, "center"));
			table3.addCell(makecell(transferInfo + " Number For Fuel Transferred:", bordern));
			table3.addCell(makecell(bolTicketNO, bordern, "center"));
			table3.addCell(makecell("Invoice Number for Fuel Transferred:", bordern));
			table3.addCell(makecell(invoiceNO, bordern, "center"));
			table3.addCell(makecell("Assigned Gallon-RINs Being Transferred:", bordern));
			table3.addCell(makecell(gallonAssigned, bordern, "center"));
			table3.addCell(makecell("Un-Assigned Gallon-RINs Being Transferred:", bordern));
			table3.addCell(makecell(gallonUnAssigned, bordern, "center"));
			table3.addCell(makecell("Mode of Transport:", bordern));
			table3.addCell(makecell(mt, bordern, "center"));
			document.add(table3);
			document.add(tableb);

//			retired rins
			float[] width ={50f, 20f};
			PdfPTable table_retiredrins = new PdfPTable(width);
			Font font = FontFactory.getFont("Helvetica", 10);
			Paragraph RIV_r = null;
			if(type == 1 && retiredRinsList1 != null && retiredRinsList1.size() > 0)
				RIV_r = new Paragraph("Retired Assigned RIN's K-Value=(1)", font);
			else if (type == 2 && retiredRinsList2 != null && retiredRinsList2.size() > 0)
				RIV_r = new Paragraph("Retired Un-Assigned RIN's K-Value=(2)", font);

			if (RIV_r != null) {
				PdfPCell cellnnn_r = new PdfPCell(RIV_r);
				cellnnn_r.setColspan(2);
				cellnnn_r.cloneNonPositionParameters(border);
				table_retiredrins.addCell(cellnnn_r);
				table_retiredrins.addCell(makecell("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tRIN", bordern));
				table_retiredrins.addCell(makecell("Qty Gallon RIN", bordern, "center"));
			

				if (type == 1)// k1
					for (int i = 0; i < retiredRinsList1.size(); i++) {
						printRINCell(table_retiredrins, retiredRinsList1.get(i)
								.toString(), String.valueOf(retiredRinsList1
								.get(i).getGallonAmount()));
					}

				else
					// k2
					for (int i = 0; i < retiredRinsList2.size(); i++) {
						printRINCell(table_retiredrins, retiredRinsList2.get(i)
								.toString(), String.valueOf(retiredRinsList2
								.get(i).getGallonAmount()));
					}

				document.add(table_retiredrins);
			}
			
//			float[] width ={20f, 50f, 20f};
			PdfPTable table = new PdfPTable(width);
//			Font font = FontFactory.getFont("Helvetica", 20);
			Paragraph riv = null;
			if (type == 1 && rinsList1 != null && rinsList1.size() > 0)
				riv = new Paragraph("Assigned RIN's K-Value=(1)", font);
			else if (type == 2 && rinsList2 != null && rinsList2.size() > 0)
				riv = new Paragraph("Un-Assigned RIN's K-Value=(2)", font);
			
			if (riv != null) {
				PdfPCell cellnnn = new PdfPCell(riv);
				cellnnn.setColspan(2);
				cellnnn.cloneNonPositionParameters(border);
				table.addCell(cellnnn);
				table.addCell(makecell("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tRIN", bordern));
				table.addCell(makecell("Qty Gallon RIN", bordern, "center"));

				if (type == 1)
					for (int i = 0; i < rinsList1.size(); i++) {
						printRINCell(table, rinsList1.get(i).toString(), String
								.valueOf(rinsList1.get(i).getGallonAmount()));
					}

				else
					for (int i = 0; i < rinsList2.size(); i++) {
						printRINCell(table, rinsList2.get(i).toString(), String
								.valueOf(rinsList2.get(i).getGallonAmount()));
					}

				document.add(table);
			}
			// Replaced
//			float[] width ={20f, 50f, 20f};
			PdfPTable table_replaced = new PdfPTable(width);
//			Font font = FontFactory.getFont("Helvetica", 20);
			Paragraph riv_replaced = null;
			if (type == 1 && replacedRinsList1 != null && replacedRinsList1.size() > 0)
				riv_replaced = new Paragraph(
						"Replaced Assigned RIN's K-Value=(1)", font);
			else if (type == 2 && replacedRinsList2 != null && replacedRinsList2.size() > 0)
				riv_replaced = new Paragraph(
						"Replaced Un-Assigned RIN's K-Value=(2)", font);

			if (riv_replaced != null) {
				PdfPCell cellnnn_replaced = new PdfPCell(riv_replaced);
				cellnnn_replaced.setColspan(2);
				cellnnn_replaced.cloneNonPositionParameters(border);
				table_replaced.addCell(cellnnn_replaced);
				table_replaced.addCell(makecell("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tRIN", bordern));
				table_replaced.addCell(makecell("Qty Gallon RIN", bordern, "center"));

				if (type == 1)
					for (int i = 0; i < replacedRinsList1.size(); i++) {
						printRINCell(table_replaced, replacedRinsList1.get(i)
								.toString(), String.valueOf(replacedRinsList1
								.get(i).getGallonAmount()));
					}

				else
					for (int i = 0; i < replacedRinsList2.size(); i++) {
						printRINCell(table_replaced, replacedRinsList2.get(i)
								.toString(), String.valueOf(replacedRinsList2
								.get(i).getGallonAmount()));
					}

				document.add(table_replaced);
			}
			document.close();
		}
		catch(Exception de) {
			de.printStackTrace();
			System.err.println("document: " + de.getMessage());
		}
	}


	private void printRINCell(PdfPTable table, String RIN, String Qty) {
		table.addCell(makecell(RIN, border));
		table.addCell(makecell(Qty, border, "center"));// TODO Auto-generated method stub

	}

	private PdfPCell makecell(String text, Rectangle borders, String alignment, int fontSize) {
		Font font0 = FontFactory.getFont("Helvetica", fontSize);
		Paragraph p = new Paragraph(text, font0);
		PdfPCell cell = new PdfPCell(p);
		cell.cloneNonPositionParameters(borders);
		if (alignment.equalsIgnoreCase("center"))
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		else if (alignment.equalsIgnoreCase("left"))
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		else if (alignment.equalsIgnoreCase("right"))
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		return cell;
	}
	
	private PdfPCell makecell(String text, Rectangle borders) {
		return makecell(text, borders, "left", 8);
	}
	
	
	private PdfPCell makecell(String text, Rectangle borders, String alignment) {
		return makecell(text, borders, alignment, 8);
	}

}

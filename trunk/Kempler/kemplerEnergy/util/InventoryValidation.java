package com.kemplerEnergy.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.csvreader.CsvWriter;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RIN;
import com.kemplerEnergy.model.rins.RINs;
import com.kemplerEnergy.persistence.HibernateUtil;

public class InventoryValidation {

	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	private static EntityManager em = emf.createEntityManager();

	public static void main(String[] args) {
		// verifySplit();
		// autoSplit();
		try {
			checkInvoiceGallonsAndRinGallons();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			em.close();
		}
	}

	/**
	 * Figure out the relationship between split children and their parent
	 */
	@SuppressWarnings("unchecked")
	private static void verifySplit() {
		EntityTransaction tx = em.getTransaction();

		tx.begin();
		List<RIN> inventory;
		ArrayList<ArrayList<RIN>> groups;
		try {
			inventory = (ArrayList<RIN>) em.createQuery("SELECT r FROM RIN r")
					.getResultList();
			if (inventory != null && inventory.size() > 1) {
				groups = RINs.groupRINs(inventory, false);

				if (groups != null && groups.size() > 0) {
					for (ArrayList<RIN> rins : groups) {
						Set<RIN> rinset = RINs.checkOverlap(rins);

						if (rinset != null && rinset.size() > 1) {
							ArrayList<RIN> overlappedRINs = new ArrayList<RIN>();

							for (RIN r : rinset) {
								overlappedRINs.add(r);
							}

							RINs.checkSplit(overlappedRINs);
						}
					}
				}
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.close();
		}

	}

	/**
	 * Sometimes, client didn't split purchase RIN, i.e. Bought RIN 001-100 Sale
	 * RIN 001-050 System will help to generate this missing 51-100 RIN and put
	 * it into the inventory
	 */
	@SuppressWarnings("unchecked")
	private static void autoSplit() {
		EntityTransaction tx = em.getTransaction();

		tx.begin();
		List<RIN> inventory;
		ArrayList<ArrayList<RIN>> groups;
		try {
			inventory = (ArrayList<RIN>) em.createQuery("SELECT r FROM RIN r")
					.getResultList();
			groups = RINs.groupRINs(inventory, false);

			int count = 0;
			for (ArrayList<RIN> groupRINs : groups) {
				for (RIN rin : groupRINs) {

					if (rin.getRinStatus().equalsIgnoreCase("SPLIT")) {
						System.out.println(count++);
						split(rin, groupRINs);
					}
				}
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.close();
		}

	}

	private static void split(RIN rin, ArrayList<RIN> groupRINs) {
		int start = RINs.toInt(rin.getStartGallon());
		int end = RINs.toInt(rin.getEndGallon());
		int currentStart, currentEnd;

		ArrayList<int[]> covers = new ArrayList<int[]>();
		ArrayList<int[]> gaps = new ArrayList<int[]>();

		RINs.sortByGallon(groupRINs);

		for (int i = 0; i < groupRINs.size(); i++) {
			currentStart = RINs.toInt(groupRINs.get(i).getStartGallon());
			currentEnd = RINs.toInt(groupRINs.get(i).getEndGallon());

			if (currentEnd < start)
				continue;
			if (currentStart > end)
				break;

			if (currentStart <= start)
				currentStart = start;
			if (currentEnd >= end)
				currentEnd = end;

			int[] pair = new int[2];
			pair[0] = currentStart;
			pair[1] = currentEnd;
			covers.add(pair);
		}

		int i;
		for (i = 0; i < covers.size(); i++) {
			if (i == 0)
				currentStart = start;
			else
				currentStart = covers.get(i - 1)[1] + 1;

			currentEnd = covers.get(i)[0] - 1;

			if (currentEnd >= currentStart) {
				int[] pair = new int[2];
				pair[0] = currentStart;
				pair[1] = currentEnd;
				gaps.add(pair);
			}
		}
		currentStart = covers.get(i - 1)[1] + 1;
		currentEnd = end;
		if (currentEnd >= currentStart) {
			int[] pair = new int[2];
			pair[0] = currentStart;
			pair[1] = currentEnd;
			gaps.add(pair);
		}

		for (int[] gap : gaps) {
			RIN newRIN = new RIN(rin);
			newRIN.setStartGallon(RINs.toCharArray(gap[0]));
			newRIN.setEndGallon(RINs.toCharArray(gap[1]));
			newRIN.setRinStatus("AVAILABLE");
			save(newRIN);
		}
	}

	private static void save(RIN newRIN) {

		System.out.println(newRIN);
		try {
			em.persist(newRIN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void echo(String str, Object... arguments) {
		MessageFormat.format(str, arguments);
		System.out.println();
	}

	private static void checkInvoiceGallonsAndRinGallons() throws Exception {

		String headers[] = { "Date", "Mode", "Counterparty", "Total Inbound",
				"Total Outbound gallons", "Invoice No.", "RIN", "Type" };

		HSSFWorkbook workbook = new HSSFWorkbook();
		FileOutputStream fileOut = new FileOutputStream(
				"c:\\InventoryValidationReport.xls");

		
		short rowCounter = 1;
		
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row = sheet.createRow(rowCounter++);
		
		HSSFCellStyle headerStyle = workbook.createCellStyle();
		
		HSSFFont headerFont = workbook.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setColor(HSSFColor.WHITE.index);
		
		headerStyle.setFont(headerFont);
		headerStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);					

		for (short column = 0; column < headers.length; column++) 
		{
			HSSFRichTextString richString = new HSSFRichTextString();
			HSSFCell cell = row.createCell((short)(column + (short)1));			
			cell.setCellStyle(headerStyle);
			cell.setCellValue(new HSSFRichTextString(headers[column]));
		}				
		
	    List<Invoice> invoices = em.createQuery("SELECT i FROM Invoice i").getResultList();
		
	    HSSFCellStyle validInvoiceStyle = workbook.createCellStyle();
	    
	    validInvoiceStyle.setFillForegroundColor(HSSFColor.WHITE.index);
	    //validInvoiceStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    HSSFFont validInvoiceFont = workbook.createFont();	    
	    validInvoiceFont.setColor(HSSFColor.BLACK.index);	
	    validInvoiceFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
	    validInvoiceStyle.setFont(validInvoiceFont);
	    
	    HSSFCellStyle invalidInvoiceStyle = workbook.createCellStyle();
	    HSSFFont invalidInvoiceFont = workbook.createFont();	    
	    invalidInvoiceFont.setColor(HSSFColor.WHITE.index);
	    invalidInvoiceStyle.setFillForegroundColor(HSSFColor.RED.index);
	    invalidInvoiceStyle.setFillPattern(HSSFCellStyle.BORDER_THIN);
	    invalidInvoiceFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);	
	    invalidInvoiceStyle.setFont(invalidInvoiceFont);
	    
	    HSSFCellStyle errorStyle = workbook.createCellStyle();
	    HSSFFont errorFont = workbook.createFont();	
   
	    errorFont.setColor(HSSFColor.RED.index);
	    errorStyle.setFillForegroundColor(HSSFColor.WHITE.index);
	    errorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    errorFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);	
	    errorStyle.setFont(errorFont);
	    
	    int testCounter = 0;
		for (Invoice invoice : invoices){
			
			row = sheet.createRow(rowCounter++);
			
			if (testCounter == 7 || invoice.calculateActualGallons() != invoice.getExpectedGallons()) 
			{
				HSSFPatriarch patr = sheet.createDrawingPatriarch();

				//anchor defines size and position of the comment in worksheet
				HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short) 6, 5));

				// set text in the comment
				comment.setString(new HSSFRichTextString("invoice.ExpectedGallons <> SUM(invoice.RINs.Gallons)"));

				
				//set comment author.
				//you can see it in the status bar when moving mouse over the commented cell
				comment.setAuthor("DNK");
				fillInvoiceRow(row, invalidInvoiceStyle, errorStyle, invoice, comment);
			}
			else
			{
				fillInvoiceRow(row, validInvoiceStyle, errorStyle, invoice, null);
			}						
			
			testCounter++;
		}
		
		workbook.write(fileOut);
		fileOut.close();
	}
	
	
	
	private static void fillInvoiceRow(HSSFRow row, HSSFCellStyle style, HSSFCellStyle errorStyle, Invoice invoice, HSSFComment comment)
	{
		// Date,Mode,Counterparty,Total Inbound,Total Outbound gallons,Invoice
		// No.,RIN,Type,,,,,
		
		short column = 0;
		
		
		HSSFCell cell = row.createCell(column++);			
		
		
		if(comment != null)
		{
			cell.setCellStyle(errorStyle);
			cell.setCellComment(comment);
			cell.setCellValue(new HSSFRichTextString("ERROR"));
		}
		else
		{
			cell.setCellValue(new HSSFRichTextString(""));
		}
			
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(invoice.getInvoiceDate()));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(invoice.getMode().getMode()));
				
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(invoice.getEpaPartner().getFullName()));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);
		
		if(invoice.getInvoiceType().equalsIgnoreCase("PURCHASE"))
		{
			cell.setCellValue(invoice.getExpectedGallons()); 
		}
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);
		
		if(invoice.getInvoiceType().equalsIgnoreCase("SALE"))
		{
			cell.setCellValue(invoice.getExpectedGallons()); 
		}
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);			
		cell.setCellValue(new HSSFRichTextString(invoice.getInvoiceNo())); 
		
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);			
	
		cell.setCellValue(new HSSFRichTextString(
				//1 2008 5131 70129 02301 10 2
				invoice.getRins().get(0).getUniRIN() 
				+ "-" + new String(invoice.getRins().get(0).getStartGallon()) 
				+ "-" + new String(invoice.getRins().get(0).getEndGallon())
				));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);			
		cell.setCellValue(new HSSFRichTextString(invoice.getInvoiceType())); 
				
	}
}

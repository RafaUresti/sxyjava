package com.kemplerEnergy.util;

import java.io.FileOutputStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.kemplerEnergy.model.Address;
import com.kemplerEnergy.model.EPAPartner;
import com.kemplerEnergy.persistence.HibernateUtil;

public class EPAPartnerExport {
	
	private static EntityManagerFactory emf = HibernateUtil.getEmf();
	private static EntityManager em = emf.createEntityManager();
	
	private static HSSFWorkbook workbook = new HSSFWorkbook();

	public static void main(String[] args) {
		
		try {
			doExport("c:\\EPAPartners_export.xls");
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			em.close();
		}
	}

	private static void doExport(String fileName) throws Exception {
		
		String headersPartner[] = { "Id", "Name", "FullName", "Phone",
				"Contact"};
						
		FileOutputStream fileOut = new FileOutputStream( fileName );
		
		short rowCounter = 0;
		
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row = sheet.createRow(rowCounter++);
		
		HSSFCellStyle headerStyle = workbook.createCellStyle();
		
		HSSFFont headerFont = workbook.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setColor(HSSFColor.WHITE.index);
		
		headerStyle.setFont(headerFont);
		headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);					

		for (short column = 0; column < headersPartner.length; column++) 
		{
			HSSFRichTextString richString = new HSSFRichTextString();
			HSSFCell cell = row.createCell((short)(column));			
			cell.setCellStyle(headerStyle);
			cell.setCellValue(new HSSFRichTextString(headersPartner[column]));
		}	
		
		
		HSSFCellStyle cellStyle = workbook.createCellStyle();
	    
	    cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
	    HSSFFont partnerFont = workbook.createFont();	    
	    partnerFont.setColor(HSSFColor.BLACK.index);	
	    partnerFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
	    cellStyle.setFont(partnerFont);
	    	    
	    List<EPAPartner> partners = (List<EPAPartner>)em.createQuery("SELECT p FROM EPAPartner p").getResultList();
			    
	    for(EPAPartner partner : partners){
	    	
	    	row = sheet.createRow(rowCounter++);
	    	rowCounter = fillPartnerRow(row, cellStyle, partner, sheet, rowCounter);
	    }
		
		workbook.write(fileOut);
		fileOut.close();
	}	
	
	private static short fillPartnerRow(HSSFRow row, HSSFCellStyle style, EPAPartner partner, HSSFSheet sheet, short rowCounter)
	{
		
		short column = 0;						
					
		HSSFCell cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(partner.getId());
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(partner.getName()));
				
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(partner.getFullName()));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(partner.getPhone()));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(partner.getContact()));
		
		
		if(partner.getAddresses().size() > 0)
		{
			String headersAddress[] = { "Id", "City", "Country", "State",
					"StreetLine1", "StreetLine2", "ZipCode"};

			
			HSSFCellStyle headerStyle = workbook.createCellStyle();
			
			HSSFFont headerFont = workbook.createFont();
			headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			headerFont.setColor(HSSFColor.WHITE.index);
			
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
	        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);	
	        
	        HSSFRow newRow = sheet.createRow(rowCounter++);
	        
	        for (short columnHeader = 0; columnHeader < headersAddress.length; columnHeader++) 
			{
				HSSFCell cellHeader = newRow.createCell((short) (columnHeader + column));			
				cellHeader.setCellStyle(headerStyle);
				cellHeader.setCellValue(new HSSFRichTextString(headersAddress[columnHeader]));
			}	        	      	        
		}
			
		for ( Address address : partner.getAddresses())
		{
			HSSFRow newRow = sheet.createRow(rowCounter++);							       
	        fillAddressRow(newRow, style, address, column);	        
		}
		
		return rowCounter;									
	}
	
	
	private static void fillAddressRow(HSSFRow row, HSSFCellStyle style, Address address, short column)
	{					
							
		HSSFCell cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(address.getId());
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(address.getCity()));
				
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(address.getCountry()));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(address.getState()));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(address.getStreetLine1()));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(address.getStreetLine2()));
		
		cell = row.createCell(column++);
		cell.setCellStyle(style);		
		cell.setCellValue(new HSSFRichTextString(address.getZipCode()));
												
	}
	

}

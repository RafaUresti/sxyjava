package XlsxRipper;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.junit.Test;


public class XlsxRipper {

	/**
	 * @param args
	 */
	@Test 
	public void verifyFile(){
		assertTrue(new File("d:/Book1.xlsx").exists());
		assertTrue(new File("d:/Book1.xlsx").canRead());
		assertTrue(new File("d:/Book1.xlsx").canWrite());
	}


	@Test 
	public void openFile()  throws IOException, ZipException{
		ZipFile xlsxFile = new ZipFile(new File("d:/Book1.xlsx"));
		assertEquals(xlsxFile.getName(), "d:\\Book1.xlsx");
	}
	@SuppressWarnings("unchecked")
	@Test
	public void listContents()  throws IOException, ZipException{
		boolean documentFound = false;
		ZipFile xlsxFile = new ZipFile(new File("d:/Book1.xlsx"));
		Enumeration entriesIter =  xlsxFile.entries();
		while (entriesIter.hasMoreElements()){
			ZipEntry entry = (ZipEntry) entriesIter.nextElement();
			if(entry.getName().equals("sheet1.xml"))
				documentFound = true;
		}
		assertTrue(documentFound);
	}
	@Test 
	public void getDocument()  throws IOException, ZipException{
		ZipFile xlsxFile = new ZipFile(new File("d:/Book1.xlsx"));
		ZipEntry documentXML =  xlsxFile.getEntry("xl/worksheets/sheet1.xml");
		assertNotNull(documentXML);
		}
}



package com.kemplerEnergy.server.rins;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * Tool that can be used to concatenate existing PDF files.
 */
public class PDFConcatenate {

    /**
     * This class can be used to concatenate existing PDF files.
     * (This was an example known as PdfCopy.java)
     * @param args the command line arguments
     */
	public PDFConcatenate(){};
	/**
	 * 
	 * @param args the file path array
	 * @param destination the destination file path
	 * @return true or false
	 */
    public boolean concatenate(String args[],String destination) {
        if (args.length < 1) {
            System.err.println("arguments: file[],destfile");
            return false;
        }
        else {
//        	System.out.println("PdfCopy example");
            try {
                int pageOffset = 0;
                ArrayList master = new ArrayList();
                int index = 0;
                String outFile = destination;
                Document document = null;
                PdfCopy  writer = null;
                while (index < args.length) {
                	if(args[index] == null){
                		index++;
                		continue;
                	}

                    // we create a reader for a certain document
                    PdfReader reader = new PdfReader(args[index]);
                    reader.consolidateNamedDestinations();
                    // we retrieve the total number of pages
                    int n = reader.getNumberOfPages();
                    List bookmarks = SimpleBookmark.getBookmark(reader);
                    if (bookmarks != null) {
                        if (pageOffset != 0)
                            SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
                        master.addAll(bookmarks);
                    }
                    pageOffset += n;
                    
                    if (index == 0 || args[0] == null) {
                        // step 1: creation of a document-object
                        document = new Document(reader.getPageSizeWithRotation(1));
                        // step 2: we create a writer that listens to the document
                        writer = new PdfCopy(document, new FileOutputStream(outFile));
                        // step 3: we open the document
                        document.open();
                    }
                    // step 4: we add content
                    PdfImportedPage page;
                    for (int i = 0; i < n; ) {
                        ++i;
                        page = writer.getImportedPage(reader, i);
                        writer.addPage(page);
                    }
                    PRAcroForm form = reader.getAcroForm();
                    if (form != null)
                        writer.copyAcroForm(reader);
                    
                    (new File(args[index])).delete();
                    
                    index++;
                }
                if (!master.isEmpty())
                    writer.setOutlines(master);
                // step 5: we close the document
                if (document != null) document.close();
                return true;
            }
            catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    
    public static void main(String [] args){
    	PDFConcatenate c = new PDFConcatenate ();
    	String [] para = {"d:/d1.pdf","d:/d2.pdf","d:/d3.pdf"};
    	c.concatenate(para,"d:/d4.pdf");
    }
}

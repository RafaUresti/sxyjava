package jpcapParser;
  
import java.io.File;

import net.sourceforge.jpcap.capture.PacketCapture;

/**
 * This class is to create an object for capturing packet
 */
public class JpParser{
	private String filename;
	private static final int PACKET_COUNT = -1;
	public PacketHandler handler = new PacketHandler();
	
	/**
	 * Default Constructor
	 * 
	 * Takes a filename and opens it for parsing
	 * @param filename the filename to parse
	 * @throws Exception throws exceptions for file problems
	 */
	public JpParser(String filename) throws Exception{
		this.filename = filename;
		this.openfile(); 
	}
	
	/**
	 *  Print the file name for debugging
	 */
	public void printfilename(){
		System.out.println(filename);
	}

	/**
	 *  Initialize packet capture
	 */
	public void openfile() throws Exception{
	    PacketCapture pcap = new PacketCapture();
	    if(new File(this.filename).exists())
	    	pcap.openOffline(this.filename);
	    else
	    	throw new IllegalArgumentException("File not exists");
	    // Start capturing packets...
	    pcap.addPacketListener(handler);
	    pcap.capture(PACKET_COUNT);
	}
}
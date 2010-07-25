package jpcapParser;
  
import java.util.ArrayList;
import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;

/**
 * PacketHandler responds to handle different type of packets (TCP/UDP) and store it into packet list
 */
public class PacketHandler implements PacketListener {
	private ArrayList<Packet> packetList; 
	private int storePacketCounter = 0;
	private int getPacketCounter = 0;
	
	/**
	 * Default Constructor
	 */
	public PacketHandler(){
		this.packetList = new ArrayList<Packet>();
	}
	
	/**
	 *  Handle the packet when it arrives
	 */
	public void packetArrived(Packet packet) {
		
		// for the case of tcp packet arrives
		if(packet instanceof TCPPacket){
			TCPPacket tcpPacket = (TCPPacket)packet;
			this.packetList.add(storePacketCounter, tcpPacket);
		}
		
		// for the case of udp packet arrives
		else if(packet instanceof UDPPacket){
			UDPPacket udpPacket = (UDPPacket)packet;
			this.packetList.add(storePacketCounter, udpPacket);
		}
		storePacketCounter += 1;
	}

	// Get the next packet from packet list
	public Packet getNextPacket(){
		int counter = this.getPacketCounter;
		this.getPacketCounter += 1;
		return (Packet) this.packetList.get(counter);
	}
	
	// Check whether reach the end of packet list
	public boolean hasMorePacket(){
		if(this.getPacketCounter < this.storePacketCounter)
			return true;
		else
			return false;		
	}
}

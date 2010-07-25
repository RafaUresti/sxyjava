package ids;

import net.sourceforge.jpcap.net.*;

import java.util.*;

/**
 * TcpStream Class
 * 
 * Represents a single TCP Stream (One direction).  Handles reordering via a hashmap of waiting
 * packets, and a linkedlist of ordered packets that have been sent.
 * 
 * @author lukecheng
 *
 */
public class TcpStream {
	
	// The waiting hashmap with packets waiting for the right sequence
	private TreeMap<Long, TCPPacket> waiting = new TreeMap<Long, TCPPacket>();
	
	// Linked List of ordered packets
	private LinkedList<TCPPacket> ordered = new LinkedList<TCPPacket>();
	
	// The initial sequence number, determined by the SYN or SYN+ACK packet.
	private long startSequence = (long)(-1);
	
	/**
	 * Default Constructor
	 */
	public TcpStream(){}
	
	/**
	 * processPacket processes a given TCP packet against the current stream
	 * Either adds it to the ordered list of packets, or puts it in the waiting hashmap
	 * 
	 * @param packet The packet to analyze and store
	 * @return The current complete stream of active packets
	 */
	public LinkedList<TCPPacket> processPacket(TCPPacket packet){
		Iterator<TCPPacket> it = ordered.iterator();
		while(it.hasNext()){
			TCPPacket tp = it.next();
			if(tp.getSequenceNumber() == packet.getSequenceNumber()
					&& tp.getAcknowledgementNumber() == packet.getAcknowledgementNumber()){
				System.out.println("Due found "+ tp.getSequenceNumber());
				return ordered;
			}
		}
		
		// If syn packet, record number and discard
		if (packet.isSyn()){
			startSequence = packet.getSequenceNumber();
			return ordered;
		}

		// If empty packet, discard
		else if (packet.getTCPData().length == 0){
			return ordered;
		}
		
		// If no incoming packets yet, and this packet is the start sequence, then save the packet
		else if((startSequence + 1) == packet.getSequenceNumber() && ordered.isEmpty()){
			ordered.add(packet);
			return ordered;
		}
		
		// If the incoming already has packets, take the last packet received and calculate the next sequence number,
		// if the new packet is expected, add it to the recieved packets
		else if(!ordered.isEmpty()){
			long lastSeq = ordered.getLast().getSequenceNumber();
			long dataSize = ordered.getLast().getTCPData().length;
			if(lastSeq + dataSize == packet.getSequenceNumber()){
				ordered.add(packet);
				return ordered;
			}
		}
		
		// Otherwise, add it to the waiting list, and loop through the list making sure none that are waiting need to
		// be put in the stream
		waiting.put(packet.getSequenceNumber(), packet);
		if(!ordered.isEmpty()){
			while (waiting.size() > 0){
				TCPPacket lastListPacket = ordered.getLast();
				long firstKey = waiting.firstKey();
				long lastListSeq = lastListPacket.getSequenceNumber();
				long lastListSize = lastListPacket.getTCPData().length;
				if (firstKey ==  lastListSeq + lastListSize){
					ordered.add(waiting.get(firstKey));
					waiting.remove(firstKey);
				} else {
					break;
				} 
			}
		}
		return ordered;
	}
}

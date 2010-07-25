package ids;

import net.sourceforge.jpcap.net.*;

import java.util.*;

/**
 * Class TcPConversation
 * 
 * Keeps a total ordering of all TCP packets coming in and out of a given session
 * Used to evaluate protocol rules
 * 
 * @author lukecheng
 *
 */
public class TcpConversation {
	// Stores all the incoming and outgoing packets
	private TreeMap<Long, LinkedList<TCPPacket>> incoming = new TreeMap<Long, LinkedList<TCPPacket>>();
	private TreeMap<Long, LinkedList<TCPPacket>> outgoing = new TreeMap<Long, LinkedList<TCPPacket>>();

	// Linked List of ordered packets
	private LinkedList<TCPPacket> ordered;

	// The initial sequence number, determined by the SYN or SYN+ACK packet.
	private long incomingStartSequence = (long)(-1);
	private long outgoingStartSequence = (long)(-1);
	private String localIP;

	/**
	 * Default Constructor
	 * 
	 * @param localIP The IP address of the local host
	 */
	public TcpConversation(String localIP){
		this.localIP = localIP;
	}

	/**
	 * processPacket processes a given TCP packet against the current stream
	 * Either adds it to the ordered list of packets, or puts it in the waiting hashmap
	 * 
	 * @param packet The packet to analyze and store
	 * @return The current complete stream of active packets
	 */
	public LinkedList<TCPPacket> processPacket(TCPPacket packet){
		if (packet.getDestinationAddress().equals(localIP)){  //incoming
			if(packet.isSyn() || incomingStartSequence != -1){
				if(packet.isSyn()) incomingStartSequence = packet.getSequenceNumber();
				putInMap(packet, incoming);
			}

		}else if(packet.getSourceAddress().equals(localIP)){  // outgoing
			if(packet.isSyn() || outgoingStartSequence != -1){
				if(packet.isSyn()) outgoingStartSequence = packet.getSequenceNumber();
				putInMap(packet, outgoing);
			}
		}
		else{} // Packet not for us

		ordered = assembleOrderedList();
		if (ordered.size() == 0){//No TCP connection established before receiving the packet, which is not SYN
			ordered.add(packet);
		}
		return ordered;
	}

	/**
	 * Assembles the received incoming and outgoing TCPPackets in order. 
	 * @return The ordered incoming and outgoing TCPPackets
	 */
	private LinkedList<TCPPacket> assembleOrderedList() {
		ordered = new LinkedList<TCPPacket>();
		Iterator<Long> incomingItr = incoming.keySet().iterator();
		LinkedList<TCPPacket> incomingOrdered = new LinkedList<TCPPacket>();
		while (incomingItr.hasNext()){
			incomingOrdered.addAll(incoming.get(incomingItr.next()));
		}
		Collections.sort(incomingOrdered, new TCPPacketComparator());//The packets with the same sequence number may come out of order

		Iterator<Long> outgoingItr = outgoing.keySet().iterator();
		LinkedList<TCPPacket> outgoingOrdered = new LinkedList<TCPPacket>();
		while (outgoingItr.hasNext()){
			outgoingOrdered.addAll(outgoing.get(outgoingItr.next()));
		}
		Collections.sort(outgoingOrdered, new TCPPacketComparator());//The packets with the same sequence number may come out of order
		int inIndex = 0, outIndex = 0;
		
		//put the packets from the sorted outgoing and incoming lists together
		//to reconstruct the TCP conversation
		while(true){
			TCPPacket incoming = null;
			long incomingSeq = -1;
			long incomingAck = -1;
			TCPPacket outgoing = null;
			long outgoingSeq = -1;
			long outgoingAck = -1;
			if (inIndex < incomingOrdered.size()){
				incoming = incomingOrdered.get(inIndex);
				incomingSeq = incoming.getSequenceNumber();
				incomingAck = incoming.getAcknowledgementNumber();
			}
			if (outIndex < outgoingOrdered.size()){
				outgoing = outgoingOrdered.get(outIndex);
				outgoingSeq = outgoing.getSequenceNumber();
				outgoingAck = outgoing.getAcknowledgementNumber();
			}
			if (incoming != null && outgoing != null){
				if (incomingSeq == outgoingAck && incomingAck == outgoingSeq + outgoing.getTCPData().length){
					ordered.add(outgoing);
					outIndex ++;
					ordered.add(incoming);
					inIndex ++;
				} else if (incomingAck == outgoingSeq && outgoingAck == incomingSeq + incoming.getTCPData().length){
					ordered.add(incoming);
					inIndex ++;
					ordered.add(outgoing);
					outIndex ++;
				} else if (incomingAck > outgoingSeq){
					ordered.add(outgoing);
					outIndex ++;
				} else if (outgoingAck > incomingSeq){
					ordered.add(incoming);
					inIndex ++;
				} else {
					break;
				}
			} else if (incoming != null){//outgoing is null
				if (inIndex > 0 && incoming.getAcknowledgementNumber() 
						== incomingOrdered.get(inIndex - 1).getAcknowledgementNumber()){
					ordered.add(incoming);
					inIndex ++;
				} else if (inIndex == 0){
					ordered.add(incoming);
					inIndex ++;
				} else {
					break;
				}
			} else if (outgoing != null){// incoming is null
				if (outIndex > 0 && outgoing.getAcknowledgementNumber() 
						== outgoingOrdered.get(outIndex - 1).getAcknowledgementNumber()){
					ordered.add(outgoing);
					outIndex ++;
				} else if (outIndex == 0){
					ordered.add(outgoing);
					outIndex ++;
				} else {
					break;
				}
			} else {//Both incoming and outgoing packet lists are processed
				break;
			}
		}
		return ordered;
	}

	/**
	 * Puts a packet in the correct linked list in the correct hashmap, given the packet's direction
	 * 
	 * @param packet The incoming packet
	 * @param uniMap The HashMap
	 */
	private void putInMap(TCPPacket packet, TreeMap<Long, LinkedList<TCPPacket>> uniMap) {

		LinkedList<TCPPacket> packetList = uniMap.get(new Long(packet.getSequenceNumber()));

		if(packetList == null){
			(packetList = new LinkedList<TCPPacket>()).add(packet);
			uniMap.put(new Long(packet.getSequenceNumber()), packetList);
		}
		else{
			packetList.add(packet);
		}
	}

}
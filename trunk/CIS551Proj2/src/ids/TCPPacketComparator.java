package ids;

import java.util.Comparator;

import net.sourceforge.jpcap.net.TCPPacket;

public class TCPPacketComparator implements Comparator<TCPPacket>{
	/**
	 * Compares two tcp packets first by sequence numbers, then by ack numbers,
	 * and finally by size. Smaller first.
	 */
	public int compare(TCPPacket p1, TCPPacket p2) {
		long seqDiff = p1.getSequenceNumber() - p2.getSequenceNumber();
		if (seqDiff > 0) return 1;
		if (seqDiff < 0) return -1;
		long ackDiff = p1.getAcknowledgementNumber() - p2.getAcknowledgementNumber();
		if (ackDiff > 0) return 1;
		if (ackDiff < 0) return -1;
		return p1.getTCPData().length - p2.getTCPData().length;
	}

}

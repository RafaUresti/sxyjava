/**
 * <pre>   
 * Contains details about the recognized protocols in Fishnet
 * </pre>   
 */
public class Protocol {

    public static final int PING_PKT = 0;
    public static final int PING_REPLY_PKT = 1;
    public static final int LINK_INFO_PKT = 2;
    public static final int TRANSPORT_PKT = 4;
    public static final int DV_INFO_PKT = 5;
    public static final int CHORD_PKT = 6;
    /**
     * Tests if the given protocol is valid
     * @param protocol The protocol to be checked
     * @return True if protocol is valid, else false
     */
    public static boolean isProtocolValid(int protocol) {
	return (protocol == PING_PKT ||
		protocol == PING_REPLY_PKT ||
		protocol == LINK_INFO_PKT ||
		protocol == TRANSPORT_PKT ||
		protocol == DV_INFO_PKT ||
		protocol == CHORD_PKT
		);
    }

    /**
     * Returns a string representation of the given protocol.
     * Can be used for debugging
     * @param protocol The protocol whose string representation is desired
     * @return The string representation of the given protocol. "Unknown Protocol" if the protocol is not recognized
     */
    public static String protocolToString(int protocol) {
	switch (protocol) {
	case PING_PKT:
	    return "Ping Packet";
	case PING_REPLY_PKT:
	    return "Ping Reply Packet";
	case LINK_INFO_PKT:
	    return "Link State Packet";
	case TRANSPORT_PKT:
	    return "Transport Packet";
	case DV_INFO_PKT:
	    return "Distance vector packet";
	case CHORD_PKT:
		return "PennChord packet";
	default:
	    return "Unknown Protocol";
	}
    }
}

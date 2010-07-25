package ids;

/**
 * Superclass of All sessions (UDPSession and TCPSession)
 * 
 * Contains the variables necessary for all sessions, specifically the identifying variables,
 * attackerIP, attackerPort, localPort and localIp
 * 
 * Contains an Enum that has state indicating whether or not the packet is going or coming,
 * the PacketDirection Enum is used extensively in the TCP and UDP Sessions
 * 
 * @author lukecheng
 *
 */
public class IPSession {
	
	// Convienence Enum for indicating packet direction
	public enum PacketDirection {INCOMING, OUTGOING, NEITHER}
	
	// Identifying values of Sessions
	protected String attackerIp = "";
	protected int attackerPort = 0;
	protected int localPort = 0;
	protected String localIp = "";
	
	/**
	 * Default constructor
	 * @param attackerIp attacker's ip
	 * @param attackerPort attacker's port
	 * @param localIp local ip
	 * @param localPort local port
	 */
	public IPSession(String attackerIp, int attackerPort, String localIp, int localPort){
		this.attackerIp = attackerIp;
		this.attackerPort = attackerPort;
		this.localPort = localPort;
		this.localIp = localIp;
	}
}

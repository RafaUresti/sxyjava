package ids;


/**
 * SessionId Class
 * 
 * Acts as a unique identifier for a given session, and the necessary machinery to be used
 * as a key in a hashtable.
 * 
 * A given session is defined uniquely as a attacker/localhost ip/port quaduple
 * 
 * Implements equals in order to work in a hashtable (hashcode is tried first, then equals)
 * 
 * @author lukecheng
 *
 */
public class SessionId {
	
	// Identifying values
	public String attackerIp = "";
	public String localIp = "";
	public int localPort = 0;
	public int attackerPort = 0;
	
	/**
	 * Primary Constructor
	 * @param attackerIp attacker ip (remote)
	 * @param attackerPort attacker port (remote)
	 * @param localIp local ip (always the same)
	 * @param localPort local port (local port)
	 */
	public SessionId(String attackerIp, int attackerPort, String localIp, int localPort){
		this.attackerIp = attackerIp;
		this.localIp = localIp;
		this.localPort = localPort;
		this.attackerPort = attackerPort;
	}
	@Override
	public int hashCode(){
		return 0;
	}
	
	@Override
	public boolean equals(Object obj){
		if (!(obj instanceof SessionId)){
			return false;
		}
		SessionId anObject = (SessionId)obj;
		if (!attackerIp.equals(anObject.attackerIp)) return false;
		if (attackerPort!=anObject.attackerPort) return false;
		if (localPort!=anObject.localPort) return false;
		if (!localIp.equals(anObject.localIp)) return false;
		return true;
	}
}

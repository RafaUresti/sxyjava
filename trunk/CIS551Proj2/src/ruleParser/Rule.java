package ruleParser;

/**
 * Class containing the common components of 
 * tcp_stream_rule and protocol_rule
 * @author sxycode
 *
 */
public abstract class Rule {
	protected String name;
	protected int hostPort;
	protected String attackerPort;
	protected String attacker;
	public String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	public int getHostPort() {
		return hostPort;
	}
	void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}
	public String getAttackerPort() {
		return attackerPort;
	}
	void setAttackerPort(String attackerPort) {
		this.attackerPort = attackerPort;
	}
	public String getAttacker() {
		return attacker;
	}
	void setAttacker(String attacker) {
		this.attacker = attacker;
	}
	
	
	
}
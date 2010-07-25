package ruleParser;

import java.util.ArrayList;

/**
 * Class for protocol_rule
 * @author Xiaoyi Sheng
 *
 */
public class ProtocolRule extends Rule{
	private ArrayList<SubRule> subRules;
	private String proto;


	/**
	 * The protocol is either "tcp" or "udp".
	 * @return
	 */
	public String getProto() {
		return proto;
	}
	
	/**
	 * Setter to set the protocol to be either "tcp" or "udp"
	 * @param proto
	 */
	void setProto(String proto) {
		if (proto.trim().equals("tcp") || proto.trim().equals("udp")){
			this.proto = proto.trim();
		} else {
			throw new IllegalArgumentException("Proto is either \"tcp\" or \"udp\".");
		}	
	}
	
	public ArrayList<SubRule> getSubRules() {
		return subRules;
	}

	void setSubRules(ArrayList<SubRule> subRules) {
		this.subRules = subRules;
	}
	
}

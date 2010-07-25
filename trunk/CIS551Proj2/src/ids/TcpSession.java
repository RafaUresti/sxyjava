package ids;

import net.sourceforge.jpcap.net.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

import ruleParser.*;

/**
 * The TCPSession class holds all the state necessary to reconstruct a single TCP session
 * A single TCPSession contains two streams, outgoing and incoming.
 * 
 * Every time a packet is processed by the session, the current stream of packets in that direction
 * (INCOMING or OUTGOING) is run against the TCP stream regex rules.  
 * 
 * @author xiaoyi, wenjie, luke
 *
 */
public class TcpSession extends IPSession {
	public enum TCPState { CLOSED, LISTEN, SYN_RCVD, ESTABLISHED, SYN_SENT,
		FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, CLOSING, TIME_WAIT, LAST_ACK}

	public enum TCPType { SERVER, CLIENT}

	private TcpStream outgoing = new TcpStream();
	private TcpStream incoming = new TcpStream();
	private LinkedList<TCPPacket> packetSequence = null;
	private Set<String> errorName = new HashSet<String>();
	private TcpConversation tcpConversation = new TcpConversation(localIp);
	private int lastTotalOrderLength = 0;
	private LinkedList<TCPPacket> previousOrderedPackets;

	public TcpSession(String attackerIp, int attackerPort, String localIp, int localPort){
		super(attackerIp, attackerPort, localIp, localPort);
		new LinkedList<TCPPacket>();
		new LinkedList<TCPPacket>();
		previousOrderedPackets = new LinkedList<TCPPacket>();
	}
	/**
	 * In-coming and out-going packages are separated and    
	 * tcp conversation is call to generate a total ordering
	 * package list
	 * 
	 * @param packet Raw TCP packet to handle 
	 */
	public void processPacket(TCPPacket packet){
		String source = packet.getSourceAddress();
		String dest = packet.getDestinationAddress();
		if(source.equals(localIp)){
			packetSequence = outgoing.processPacket(packet);
		}

		if(dest.equals(localIp)){
			packetSequence = incoming.processPacket(packet);
		}

		LinkedList<TCPPacket> orderedPackets = tcpConversation.processPacket(packet); 
		checkTcpProtocolRules(orderedPackets, source, dest);

		// Check the rules against this state
		checkSequenceRules(packetSequence);

		return;
	}

	/**
	 * For all the most updated packages, all protocol rules include any sub rules
	 * are checked in this method. If it's a single rule, just use pattern matching,
	 * else, we will compare the pattern by the backward ordering - the latest package
	 * will first be checked and move backward if return true from pattern matching.
	 * 
	 * @param packet Total-ordering package
	 * @param source source address
	 * @param dest destination address
	 */
	private void checkTcpProtocolRules(LinkedList<TCPPacket> orderedPackets, String source, String dest) {
		int increaseLength = orderedPackets.size() - lastTotalOrderLength;
		if (orderedPackets.size() == 1 && !orderedPackets.get(0).isSyn()){ //If the previous ordered packets doesn't have a TCP session established
			lastTotalOrderLength = 0;									   //Then it should not be considered against the new ordered list
		} else {
			lastTotalOrderLength = orderedPackets.size();
		}

		previousOrderedPackets = orderedPackets;

		Iterator<Rule> iter = Simulator.rules.iterator();
		Outerloop1:
			while(iter.hasNext()){
				Rule r = iter.next();
				if(r instanceof ProtocolRule){
					ProtocolRule pr = (ProtocolRule)r;

					// Check if rule is relevant
					if(!pr.getProto().equals("tcp")) continue;
					if(pr.getHostPort() != -1 && pr.getHostPort() != localPort) continue;
					if (!pr.getAttackerPort().equals(String.valueOf(attackerPort))
							&& !pr.getAttackerPort().equals("any")) continue;
					if(!pr.getAttacker().equals(source) && !pr.getAttacker().equals("any")) continue;

					// Loop backwards through rules / packets
					ArrayList<SubRule> subRules = pr.getSubRules();
					int lastSubRuleIndex = subRules.size() - 1;
					int lastPacketIndex = orderedPackets.size() - 1;
					if (increaseLength == 0){
						return;
					}

					Outerloop2:
						for (int i = 0; i < increaseLength; i ++){					
							int endPacketIndexToCheck = lastPacketIndex - i;
							if (endPacketIndexToCheck < lastSubRuleIndex){
								return;
							}
							Outerloop3:
								for (int j = lastSubRuleIndex, k = endPacketIndexToCheck; j >= 0 && k >= 0; j --, k --){
									if ((subRules.get(j).getHostType().equals("from_host")&& (dest.equals(localIp)||dest.equals(attackerIp))) 
											|| (subRules.get(j).getHostType().equals("to_host") && (source.equals(attackerIp)||source.equals(localIp)))){
										if (j < lastSubRuleIndex){
											while (k >= 0 && orderedPackets.get(k).isAck() && orderedPackets.get(k).getTCPData().length == 0){
												k --;
												i ++;
											}
										}
										Pattern pattern = Pattern.compile(subRules.get(j).getContent());
										Matcher matcher = pattern.matcher(new String(orderedPackets.get(k).getTCPData()));
										if(!matcher.find() || !matchFlags(orderedPackets.get(k), subRules.get(j).getFlags())){
											if (i >= increaseLength - 1){
												continue Outerloop1;
											}
											else {
												continue Outerloop2;
											}
										}
										else{
											if(j == 0){
												System.err.println("Potential risk violate rule "+pr.getName());
												return;
											}
											else
												continue Outerloop3;
										}
									} else{

										if (i >= increaseLength - 1){
											continue Outerloop1;
										}
										else {
											continue Outerloop2;
										}
									}
								}// end of inner for
						}// end of outter for
				}
			}
	}

	/**
	 * Matches a set of flags from a packet to an actual packet
	 * Used for quick comparisons
	 * 
	 * If the packet's flags or the comparing flags are null, then flags don't matter
	 * 
	 * @param packet Packet to compare flags to
	 * @param flags array to compare it to
	 * @return
	 */
	public boolean matchFlags(TCPPacket packet, boolean[] flags){
		if (flags == null){
			return true;
		}
		if (flags.length != 6){
			throw new IllegalArgumentException("Wrong flag format!");
		}
		boolean[] packetFlags = {packet.isSyn(), packet.isAck(), 
				packet.isFin(), packet.isRst(), packet.isPsh(), packet.isUrg()};
		for (int i = 0; i < 6; i ++){
			if (packetFlags[i] != flags[i]){
				return false;
			}
		}
		return true;
	}

	/**
	 * Check a given sequence of TCP packets against the set of rules given by the Simulator
	 * 
	 * This does one-way stream analysis only, so the packets are either incoming or outgoing
	 * 
	 * @param packetSequence The sequence of consequitive packets
	 */
	public void checkSequenceRules(LinkedList<TCPPacket> packetSequence){

		String checkString = "";
		String direction = "OUTGOING";

		// If Sequence is empty, do nothing
		if(packetSequence.isEmpty()){
			return;
		}

		// Determine packet direction
		if (packetSequence.getFirst().getDestinationAddress().equals(localIp)){ // incoming
			direction = "INCOMING";
		}
		else if (packetSequence.getFirst().getSourceAddress().equals(localIp)){ // outgoing
			direction = "OUTGOING";
		}
		else{
			return;
		}

		// Construct the String to check
		for(TCPPacket temp : packetSequence){
			try{
				checkString += new String(temp.getData(), "ISO-8859-1");
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
			}
		}

		// Check the rules
		for(Rule tempRule : Simulator.rules){
			if(ruleIsValid(tempRule, direction)){
				TcpStreamRule tempTcpStreamRule = (TcpStreamRule)tempRule;
				String regex = new String(tempTcpStreamRule.getContent());
				Pattern myPattern = Pattern.compile(".*" + regex + ".*");
				Matcher m = myPattern.matcher(checkString);
				if(m.find()){
					if (!errorName.contains(tempTcpStreamRule.getName())){
						System.err.println("Potential risk violate rule " + tempTcpStreamRule.getName());
						errorName.add(tempTcpStreamRule.getName());
					}
				}
			}
		}
	}

	/**
	 * ruleIsValid()
	 * 
	 * Check and see if a rule applied to this stream
	 * 
	 * @param rule The rule, as given by the rule parser
	 * @param direction The direction of the packet
	 */
	public boolean ruleIsValid(Rule rule, String direction){

		// Check if rule is a TCPStream rule (VS UDP)
		if(!(rule instanceof TcpStreamRule)){
			return false;
		}

		TcpStreamRule tcpRule = (TcpStreamRule)rule;

		// Make sure packet is going right direction for rule
		if(tcpRule.getHostType().equals("from_host") && direction.equals("INCOMING")){
			return false;
		}

		if(tcpRule.getHostType().equals("to_host") && direction.equals("OUTGOING")){
			return false;
		}

		// Check if attacker IP address applies to this rule
		if( ! (rule.getAttacker().equalsIgnoreCase("any") || rule.getAttacker().equals(attackerIp))){
			return false;
		}

		// Check if attacker port applies
		if(!rule.getAttackerPort().equals("any") 
				&& !rule.getAttackerPort().equals(String.valueOf(super.attackerPort))){
			return false;
		}

		// Check if Host port applies to this rule (-1 = any)
		if( !(rule.getHostPort() != -1) && !(rule.getHostPort() == localPort)){
			return false;
		}

		return true;
	}
}

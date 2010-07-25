package ids;

import net.sourceforge.jpcap.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ruleParser.ProtocolRule;
import ruleParser.Rule;
import ruleParser.SubRule;

/**
 * Class UdpSession
 *  
 * This class is to match the pattern for UDP packet,
 * the latest packet will try to match the last sub 
 * rule, and then move backward
 * 
 * A UDP session keeps all the packets in total ordering
 * 
 * */
public class UdpSession extends IPSession {
	private ArrayList<UDPPacket> totalStream = new ArrayList<UDPPacket>();

	/**
	 * Default constructor
	 * 
	 * @param attackerIp The attacker's IP
	 * @param attackerPort The attacker's remote port
	 * @param localIp The local IP address
	 * @param localPort The local port
	 */
	public UdpSession(String attackerIp, int attackerPort, String localIp, int localPort){
		super(attackerIp, attackerPort, localIp, localPort);
	}

	public PacketDirection processPacket(UDPPacket packet){
		String source = packet.getSourceAddress();
		String dest = packet.getDestinationAddress();
		PacketDirection returnDir = PacketDirection.NEITHER;

		totalStream.add(packet);

		Iterator<Rule> iter;
		if(Simulator.rules.size()!=0) {
			iter = Simulator.rules.iterator();
			Outerloop:
			while(iter.hasNext()){
				Rule r = iter.next();
				
				// rule need to be protocol rule
				if(r instanceof ProtocolRule){
					ProtocolRule pr = (ProtocolRule)r;
					// do port and host checking
					// if there's some case that not fit the rules, just continue and check the next rule
					if(!pr.getProto().equals("udp")) continue;
					if(pr.getHostPort() != -1 && pr.getHostPort() != localPort) continue;
					if (!pr.getAttackerPort().equals(String.valueOf(attackerPort))
							&& !pr.getAttackerPort().equals("any")) continue;
					if(!pr.getAttacker().equals(source) && !pr.getAttacker().equals("any")) continue;
					ArrayList<SubRule> subRules = pr.getSubRules();
					// two pointers for checking backward 
					int lastSubRuleIndex = subRules.size() - 1;
					int lastPacketIndex = totalStream.size() - 1;
					if (lastSubRuleIndex > lastPacketIndex){
						continue;
					}
	
					// Do pattern matching
					Outerloop3:
					for (int i = lastSubRuleIndex, j = lastPacketIndex; i >= 0 && j >= 0; i --, j --){
						if ((subRules.get(i).getHostType().equals("from_host")&& source.equals(attackerIp)) 
								|| (subRules.get(i).getHostType().equals("to_host") && dest.equals(localIp))){
							Pattern pattern = Pattern.compile(".*" + subRules.get(i).getContent() + ".*");
							Matcher matcher = pattern.matcher(new String(totalStream.get(j).getUDPData()));
							if (!matcher.find()){
								continue Outerloop;
							}
							else{
								if(i == 0){
									System.err.println("Potential risk violate rule "+pr.getName());
									return returnDir;
								}
								else
									continue Outerloop3;
							}
						}
						else
							continue Outerloop;
					}
					
					// print error message
					System.err.println("Potential risk violate rule "+pr.getName());
				}
			}
		}
		return returnDir;
	}
}

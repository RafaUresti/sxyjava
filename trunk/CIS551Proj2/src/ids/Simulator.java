package ids;

import ids.IPSession.PacketDirection;

import java.util.*;
import net.sourceforge.jpcap.net.*;
import ruleParser.*;

/**
 * The Simulator Class
 * 
 * Holds all the necessary machinery to keep the TCP / UDP state for all active sessions
 * 
 * The Simulator is fed packets from the jpcap parser, and has a list of rules that it uses
 * to compare against the given TCP / UDP state
 * 
 * @author lukecheng
 *
 */
public class Simulator {
	
	// The two hashtables that keep all TCP and UDP sessions, respectively
	HashMap<SessionId, IPSession> tcpMap = new HashMap<SessionId, IPSession>();
	HashMap<SessionId, IPSession> udpMap = new HashMap<SessionId, IPSession>();
	
	// The local IP address.  All IP addresses are stored as strings
	String localIp = null;
	
	// The rule list, made static to be accessible by everyone
	public static ArrayList<Rule> rules = null;
	
	/**
	 * Default constructor to create a Simulator class
	 * 
	 * @param localIp The IP address of the host we are protecting
	 * @param rules An arraylist of rules handed to us by the rule parser
	 */
	public Simulator(String localIp, ArrayList<Rule> rules){
		this.localIp = localIp;
		Simulator.rules = rules;
	}
	
	/**
	 * Processes a Packet into the Simulator, decides whetehr TCP or UDP and whether
	 * incoming or outgoing
	 * 
	 * @param packet the packet for the simulator to consider
	 */
	public void processPacket(IPPacket packet){
		
		String destIp = packet.getDestinationAddress();
		String sourceIp = packet.getSourceAddress();
		IPSession.PacketDirection pktDirection = PacketDirection.NEITHER;
		
		// Determine if the packet is leaving or going into the host
		if(sourceIp.equals(localIp)){
			pktDirection = PacketDirection.OUTGOING;
		}
		else if(destIp.equals(localIp)){
			pktDirection = PacketDirection.INCOMING;
		}
		else{
			System.out.println("Packet not from or to the local host");
			return;
		}
		
		// Determine the packet type (TCP or UDP), ignore other types
		if(packet instanceof TCPPacket){
			processTcpPacket((TCPPacket)packet, pktDirection);
		}
		else if(packet instanceof UDPPacket){
			processUdpPacket((UDPPacket)packet, pktDirection);
		}
		else{
			System.out.println("Unrecognized Packet Type: " + packet.getClass().getName());
			System.out.println("Note: This program only recognizes TCP and UDP");
		}
	}
	
	/**
	 * Process a TCP packet handed by processPacket
	 * 
	 * @param packet The TCPPacket to be processed
	 * @param pktDirection The direction that the packet is going (OUTGOING or INCOMING)
	 */
	public void processTcpPacket(TCPPacket packet, IPSession.PacketDirection pktDirection){
		SessionId sessionId = null;
		TcpSession tcpSession = null;
		
		// Create a sessionID (hash key) for the session of the incoming packet
		if(pktDirection.equals(IPSession.PacketDirection.OUTGOING)){
			sessionId = new SessionId(packet.getDestinationAddress(), 
					packet.getDestinationPort(), 
					packet.getSourceAddress(), packet.getSourcePort());
		}
		else{
			sessionId = new SessionId(packet.getSourceAddress(), 
					packet.getSourcePort(), 
					packet.getDestinationAddress(), packet.getDestinationPort());
		}
		
		// Check the approperate hashtable for an active session
		tcpSession = (TcpSession)tcpMap.get(sessionId);
		
		// If the sesion DNE or is a syn, create a new session and push it into the table
		if(tcpSession == null ||(packet.isSyn() && !packet.isAck())){
			tcpSession = new TcpSession(sessionId.attackerIp, 
					sessionId.attackerPort, 
					sessionId.localIp, 
					sessionId.localPort);
			tcpMap.put(sessionId, tcpSession);
		}
		
		// Hand the packet to the session for processing
		tcpSession.processPacket(packet);
	}
	
	/**
	 * Process a UDP packet handed to by processPacket
	 * Finds the session for the packet, or creates the session
	 * 
	 * @param packet The packet to analyze
	 * @param pktDirection The direction of the packet, OUTGOING or INCOMING
	 */
	public void processUdpPacket(UDPPacket packet, IPSession.PacketDirection pktDirection){
		SessionId sessionId = null;
		UdpSession udpSession = null;

		// Create a SessionID for the packet, to hash against udpMap
		if(pktDirection.equals(IPSession.PacketDirection.OUTGOING)){
			sessionId = new SessionId(packet.getDestinationAddress(), 
					packet.getDestinationPort(), 
					packet.getSourceAddress(), packet.getSourcePort());
		}
		else{
			sessionId = new SessionId(packet.getSourceAddress(), 
					packet.getSourcePort(), 
					packet.getDestinationAddress(), packet.getDestinationPort());
		}
		
		// Get the session
		udpSession = (UdpSession)udpMap.get(sessionId);

		// If the session DNE, create a new one
		if(udpSession == null){
			udpSession = new UdpSession(sessionId.attackerIp, 
					sessionId.attackerPort, 
					sessionId.localIp, 
					sessionId.localPort);
			udpMap.put(sessionId, udpSession);
		}
		
		// If the session does exist, feed the packet to the session.
		udpSession.processPacket(packet);
	}
	
	/**
	 * Resets the Simulator, clears all sessions
	 */
	public void reset(){
		udpMap.clear();
		tcpMap.clear();
	}
	
}

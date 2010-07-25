
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class Node {
	/*
	 * speicify which protocols to use
	 */
	//private final int mProtocol = Protocol.LINK_INFO_PKT; // use Link State protocol
	private final int mProtocol = Protocol.DV_INFO_PKT; // use Distance Vector protcol
	// Uses broadcast instead of routing (milestone 1) if true.
	private final boolean PING_BROADCAST = false;

	/*
	 * specify timeout and peroid
	 */
	// Timeout pings in 10 seconds
	private final long mPingTimeout = 4000;
	// neighbors expire within 10 seconds on discovery ping
	private final long mNeighborTimeout = 4000;    // neighbor discovery peroid
	private final long mNeighbroDiscoveryPeriod = 2000;

	/*
	 * for LINK STATE
	 */
	// link state routing table
	private RoutingTable mRoutingTable = null;
	// period of sending out Link state info
	private final long mLinkStatePeriod = 2000;

	/*
	 * for DISTANCE VECTOR
	 */
	// distance vector (routing table)
	private DistanceVector mDistanceVector = null;
	// peroid of sending distance vector
	private final long mDistanceVectorPeriod = 2000;

	/*
	 * members for class Node
	 */
	// node manager
	private Manager mManager;
	// addr of this node
	private int mAddr;
	// Used to name this node.
	private String mName;    // Hashtable for storing name,node pairs for advertised names.
	private Hashtable<String, Integer> mNameTable = null;    // To store PingRequests.
	private ArrayList<PingRequest> mPingArray;    // Next sequence number to use for sent packets.
	private int mSequenceNumber = 0; //Integer.MIN_VALUE;
	// Hashtable to store last sequence number seen from a particular node <node, seq>.
	private Hashtable<Integer, Integer> mSequenceTable = null;
	// Hashtable to store list of neighbors and the last update time <node,time>.
	private Hashtable<Integer, Long> mNeighborTable = null;

	/*
	 * for DEBUGGING
	 */
	// whether show when receiving a packet
	private static final boolean DEBUG_RECEIVE_PACKET = false;
	// Ping and Ping Reply output.
	public static final boolean DEBUG_PING = true;
	// Link State Packets (send and receive) output.
	public static final boolean DEBUG_LINKSTATE = false;
	// debug distance vector
	private static final boolean DEBUG_DISTANCEVECTOR = false;
	// Neighbor Discovery output.
	public static final boolean DEBUG_NEIGHBOR = false;
	// Naming (Name packets sent, received or table updated) output.
	public static final boolean DEBUG_NAME = false;


	private PennChordNode chordNode;
	/**
	 * Create a new node
	 * 
	 * @param manager
	 *            The manager that is managing Fishnet
	 * @param addr
	 *            The address of this node
	 */
	public Node(Manager manager, int addr) {
		this.mManager = manager;
		this.mAddr = addr;
		this.mPingArray = new ArrayList<PingRequest>();
		this.mSequenceTable = new Hashtable<Integer, Integer>();
		this.mNeighborTable = new Hashtable<Integer, Long>();
		this.mRoutingTable = new RoutingTable(this);
		this.mNameTable = new Hashtable<String, Integer>();
		mDistanceVector = new DistanceVector(this.mAddr);///
		chordNode = new PennChordNode(this,addr);
	}

	public int getAddr() {
		return this.mAddr;
	}

	/**
	 * Called by the manager to start this node up.
	 */
	public void start() {
		logOutput("started.");
		this.addTimer(mPingTimeout, "pingTimedOut");
		this.addTimer(mNeighborTimeout, "timeoutNeighbors");
		this.addTimer(mNeighbroDiscoveryPeriod, "discoverNeighbors");

		// use protocol: distance vector
		if (mProtocol == Protocol.DV_INFO_PKT) {
			this.addTimer(mDistanceVectorPeriod, "sendDistanceVector");
		}

		// use protocol: link state
		if (mProtocol == Protocol.LINK_INFO_PKT) {
			this.addTimer(mLinkStatePeriod, "sendLinkState");
		}
	}

	/**
	 * Called by the manager when a packet has arrived for this node
	 * 
	 * @param from
	 *            The address of the node that has sent this packet
	 * @param msg
	 *            The serialized form of the packet.
	 */
	public void onReceive(Integer from, byte[] msg) {
		Packet packet = Packet.unpack(msg);
		logOutput("Received packet from " + from, DEBUG_RECEIVE_PACKET);

		if (packet == null) {
			logError("Unable to unpack message: " + Utility.byteArrayToString(msg) + " Received from " + from);
			return;
		}

		int seq = packet.getSeq();
		int src = packet.getSrc();
		int ttl = packet.getTTL();

		// Determine if own packet has been rebroadcast to self.
		if (src == this.mAddr) {
			// Discard packet
			logOutput("Packet from self: discard", DEBUG_RECEIVE_PACKET);
			return;
		}

		// Determine if packet has been seen before, or detect wrap-around.
		if (mSequenceTable.get(src) == null 
				|| mSequenceTable.get(src) < seq 
				|| (mSequenceTable.get(src) > seq && Math.abs((long) mSequenceTable.get(src) - (long) seq) > (long) Integer.MAX_VALUE)) {
			mSequenceTable.put(src, seq);
		} else {
			// Discard packet
			logOutput("Packet sequence number invalid: discard.", DEBUG_RECEIVE_PACKET);
			return;
		}

		// Reduce TTL
		packet.setTTL(ttl - 1);

		this.receivePacket(from.intValue(), packet);
	}

	/**
	 * Called by the manager when there is a command for this node from the
	 * user. Command can be input either from keyboard or file. For now sending
	 * messages as ping to a neighbor.
	 * 
	 * @param command
	 *            The command for this node
	 */
	public void onCommand(String command) {
		if (this.matchDumpCommand(command)) {
			return;
		} else if (this.matchLSCommand(command)) {
			return;
		} else if (this.matchBuildCommand(command)) {
			return;
		} else if (this.matchNameCommand(command)) {
			return;
		} else if (this.matchDiscoverCommand(command)) {
			return;
		} else if (this.matchHelpCommand(command)) {
			return;
		} 
		else 
		{
			logOutput("0000000000000000Chord command:" + command +" received...................");
			if(chordNode.onCommand(command))
			{
				logOutput("Chord command:" + command +" received...................");
				return;
			}
			else if (this.matchPingCommand(command)) {
				return;
			}
			logError("Unrecognized command: " + command);
		}

	}

	/**
	 * Callback method given to manager to invoke ping time-outs.
	 */
	public void pingTimedOut() {
		Iterator iter = this.mPingArray.iterator();
		while (iter.hasNext()) {
			PingRequest pingRequest = (PingRequest) iter.next();
			if ((pingRequest.getTimeSent() + mPingTimeout) < this.mManager.now()) {
				try {
					if (DEBUG_PING) {
						logOutput("Timing out ping: " + pingRequest, DEBUG_PING);
					}
					iter.remove();
				} catch (Exception e) {
					logError("Exception occured while trying to remove an element from ArrayList pings.  Exception: " + e);
					return;
				}
			}
		}
		this.addTimer(mPingTimeout, "pingTimedOut");
	}

	/**
	 * Callback method given to manager to invoke neighbor discovery.
	 */
	public void discoverNeighbors() {
		/*
		 * Packet is of type PING_PKT with packet destination BROADCAST and TTL
		 * 1. The payload is empty. The BROADCAST flag in the packet header will
		 * be used as a signature to identify this as a neighbor discovery
		 * packet. The pings table is not updated here as destination is
		 * unknown. The reply to this packet will also ignore the ping table.
		 * This ping packet will also be used by the recipient node to update
		 * its neighbor table.
		 */
		Packet packet = new Packet(Packet.BROADCAST_ADDRESS, this.mAddr, 1,
				Protocol.PING_PKT, assignSequenceNumber(), Utility.stringToByteArray("NEIGHBOR_DISCOVERY"));

		logOutput("Sending Neigbor Discovery broadcast.", DEBUG_NEIGHBOR);
		this.send(Packet.BROADCAST_ADDRESS, packet);

		// Rediscover neighbors in 10 to 30 seconds.
		this.addTimer(mNeighbroDiscoveryPeriod, "discoverNeighbors");
	}

	public void timeoutNeighbors() {
		boolean pruned = false;
		long now = this.mManager.now();

		for (Iterator<Entry<Integer, Long>> iter = mNeighborTable.entrySet().iterator(); iter.hasNext();) {
			Entry<Integer, Long> entry = iter.next();
			if (now - entry.getValue().longValue() > mNeighborTimeout) {
				// Expire entry
				iter.remove();

				// Also remove LSP for this neighbor
				mRoutingTable.removeLSP(entry.getKey());
				pruned = true;

				/// also remove from distance vector, all entries like (X, X, neighborAddr)
				mDistanceVector.remove(entry.getKey());
			}
		}

		if (pruned) {
			logOutput("Neighbor(s) pruned: " + mNeighborTable.toString(), DEBUG_NEIGHBOR);
			sendLinkState();
		}

		addTimer(mNeighborTimeout, "timeoutNeighbors");
	}

	/**
	 * Floods the network with current link state status. Invoked whenever a
	 * change is detected in the neighborTable.
	 */
	public void sendLinkState() {
		// make sure it is using link state protocol
		if (mProtocol != Protocol.LINK_INFO_PKT) {
			return;	// Build neighbor list as int array.
		}
		Set<Integer> s = mNeighborTable.keySet();
		int[] neighbors = new int[s.size()];
		int i = 0;
		for (Iterator<Integer> iter = s.iterator(); iter.hasNext();) {
			neighbors[i++] = iter.next();
		}

		// Initialize linkState and broadcast.
		logOutput("Broadcasting LSP : " + Arrays.toString(neighbors), DEBUG_LINKSTATE);

		LinkState linkState = new LinkState(neighbors);
		Packet packet = new Packet(Packet.BROADCAST_ADDRESS, this.mAddr,
				Packet.MAX_TTL, Protocol.LINK_INFO_PKT, assignSequenceNumber(),
				linkState.pack());
		send(Packet.BROADCAST_ADDRESS, packet);

		// Also update own neighbor tables (and re-build routing tables).
		mRoutingTable.addLSP(this.mAddr, neighbors);

		addTimer(mLinkStatePeriod, "sendLinkState");
	}

	/*
	 * periodically send out distance vector to the neighbors
	 */
	public void sendDistanceVector() {
		// make sure it is using distance vector protocol
		if (mProtocol != Protocol.DV_INFO_PKT) {
			return;
		}
		int neighborAddr;
		try {
			for (Iterator<Integer> iter = mNeighborTable.keySet().iterator(); iter.hasNext();) {
				neighborAddr = iter.next();

				///send only to the neighbors, so TTL is 1
				Packet packet = new Packet(neighborAddr, this.mAddr, 1,
						Protocol.DV_INFO_PKT, assignSequenceNumber(), mDistanceVector.pack());
				logOutput(packet.toString(), DEBUG_DISTANCEVECTOR);

				send(neighborAddr, packet);
			}
		} catch (Exception e) {
			logOutput("sendDistanceVector: exceptoin " + e.getMessage());
		}

		addTimer(mDistanceVectorPeriod, "sendDistanceVector");
	}

	private boolean matchHelpCommand(String command) {
		if (command.startsWith("help")) {
			StringBuilder string = new StringBuilder();
			string.append("Commands:\n");
			string.append("\tdump linkstate\n");
			string.append("\tdump names\n");
			string.append("\tdump neighbors\n");
			string.append("\tdump tables\n");
			string.append("\tls\n");
			string.append("\tname <nodename>\n");
			string.append("\tnodename|nodenumber <message>\n");

			logOutput(string.toString());

			return true;
		} else {
			return false;
		}
	}

	private boolean matchDumpCommand(String command) {
		if (!command.startsWith("dum")) {
			return false;
		} else {
			StringTokenizer stringTokenizer = new StringTokenizer(command);
			stringTokenizer.nextToken(); // discard
			if (stringTokenizer.hasMoreTokens()) {
				String nextToken = stringTokenizer.nextToken();
				if (nextToken.startsWith("nei")) {
					return matchDumpNeighborsCommand();
				} else if (nextToken.startsWith("nam")) {
					return matchDumpNameTableCommand();
				} else if (nextToken.startsWith("l")) {
					return matchDumpLinkStateCommand();
				} else if (nextToken.startsWith("t") || nextToken.startsWith("r")) {
					return matchDumpRoutingTable();
				} else if (nextToken.startsWith("d")) {
					return matchDumpDistanceVector();
				}
			}
		}

		return false;
	}

	private boolean matchLSCommand(String command) {
		if (!command.startsWith("ls")) {
			return false;
		} else {
			String output = Arrays.toString(this.mRoutingTable.getNodes());
			logOutput("Nodes in network: " + output);
			return true;
		}
	}

	private boolean matchDumpNeighborsCommand() {
		logOutput("Neighbors: " + mNeighborTable.toString());
		return true;
	}

	private boolean matchDumpLinkStateCommand() {
		logOutput("Link State: " + this.mRoutingTable.getLinkStatePacketsAsString());
		return true;
	}

	private boolean matchDumpDistanceVector() {
		logOutput("Distance vector" + this.mDistanceVector.toString());
		return true;
	}

	private boolean matchDumpRoutingTable() {
		logOutput("Routing Table: " + this.mRoutingTable.toString());
		return true;
	}

	/**
	 * To be used for debugging; starts timer for neighbor disovery (in the case
	 * that it is not automatically started).
	 */
	private boolean matchDiscoverCommand(String command) {
		if (!command.startsWith("dis")) {
			return false;
		} else {
			logOutput("Starting Neighbor Discovery");
			// start immedietly.
			this.addTimer(1, "discoverNeighbors");
			return true;
		}
	}

	/**
	 * To be used for debugging. Used to manually re-build routing table.
	 */
	private boolean matchBuildCommand(String command) {
		if (!command.startsWith("b")) {
			return false;
		} else {
			this.mRoutingTable.build();
			return true;
		}
	}

	private boolean matchDumpNameTableCommand() {
		if (this.mName != null) {
			logOutput("Name Table: " + this.mName + "=" + this.mAddr + ", " + this.mNameTable.toString());
		} else {
			logOutput("Name Table: " + this.mNameTable.toString());
		}
		return true;
	}

	/**
	 * Name command used to name a node, and advertise the name to other nodes.
	 */
	private boolean matchNameCommand(String command) {
		if (!command.startsWith("n")) {
			return false;
		} else {
			// Not allowing white spaces in names.
			// Name may not start with a number.
			StringTokenizer commandTokenizer = new StringTokenizer(command);
			commandTokenizer.nextToken(); // skip command itself.
			if (commandTokenizer.hasMoreTokens()) {
				String name = commandTokenizer.nextToken();
				if (name.matches("[0-9]*") || name.equals("name")) {
					logError("Invalid name: cannot use a number or 'name' for name.");
					return true;
				} else if (mNameTable.containsKey(name)) {
					logError("Invalid name: already exists in the network.");
					return true;
				}
				this.mName = name;
				advertiseName();
				return true;
			}
			logError("Invalid name.");
			return true;
		}
	}

	private boolean matchPingCommand(String command) {
		logOutput("matchPingCommand: " + command, DEBUG_PING);
		int index = command.indexOf(" ");
		if (index == -1) {
			return false;
		}
		try {
			// Check to see if the user is pinging by name (vs. node number).
			int destAddr = -1;
			String node = command.substring(0, index);
			if (!node.matches("[0-9]*")) {
				// Ping by name.
				Integer nodeAddr = mNameTable.get(node);
				if (nodeAddr == null) {
					logError("Node " + node + " unknown!");
					return true;
				} else {
					destAddr = nodeAddr.intValue();
				}
			} else {
				// Ping by node number.
				destAddr = Integer.parseInt(node);
			}
			String message = command.substring(index + 1);
			Packet packet = new Packet(destAddr, this.mAddr, Packet.MAX_TTL,
					Protocol.PING_PKT, assignSequenceNumber(), Utility.stringToByteArray(message));

			if (PING_BROADCAST) {
				this.send(Packet.BROADCAST_ADDRESS, packet);
			} else {
				/// if using LS protocol
				if (mProtocol == Protocol.LINK_INFO_PKT) {
					this.send(mRoutingTable.getNextHop(destAddr), packet);
					logOutput("Issue ping from " + this.mAddr + " for destination " + packet.getDest() + " via " + mRoutingTable.getNextHop(packet.getDest()) + " with message: " + Utility.byteArrayToString(packet.getPayload()), DEBUG_LINKSTATE);
				} /// if using Distance vector protocol
				else if (mProtocol == Protocol.DV_INFO_PKT) {
					this.send(mDistanceVector.getNextHop(destAddr), packet);
					logOutput("Issue ping from " + this.mAddr + " for destination " + packet.getDest() + " via " + mDistanceVector.getNextHop(packet.getDest()) + " with message: " + Utility.byteArrayToString(packet.getPayload()), DEBUG_DISTANCEVECTOR);
				} /// other protocols, to be extented in the future
				else {
					logOutput("using no protocol", DEBUG_PING);
				}
			}
			this.mPingArray.add(new PingRequest(destAddr, Utility.stringToByteArray(message), this.mManager.now()));
			logOutput("Send Ping from " + packet.getSrc() + " for destination " + packet.getDest() + " with message: " + Utility.byteArrayToString(packet.getPayload()), DEBUG_PING);

			return true;
		} catch (Exception e) {
			logError("MatchPingCommand: Exception: " + e.getMessage() + "" + e.getLocalizedMessage());
		}

		return true;
	}

	public void receivePacket(int from, Packet packet) {
		switch (packet.getProtocol()) {
		case Protocol.PING_PKT:
			this.receivePing(packet);
			break;
		case Protocol.PING_REPLY_PKT:
			this.receivePingReply(packet);
			break;
		case Protocol.LINK_INFO_PKT:
			this.receiveLinkInfo(packet);
			break;
		case Protocol.NAME_PKT:
			this.receiveNamePkt(packet);
			break;
		case Protocol.DV_INFO_PKT:
			this.receiveDistanceVector(packet);
			break;
		default:
			chordNode.processPacket(from, packet);
		}
	}

	private void receivePing(Packet packet) {
		try {
			if (packet.getDest() == this.mAddr) {
				logOutput("Received Ping from " + packet.getSrc() + " for destination " + packet.getDest() + " with message: " + Utility.byteArrayToString(packet.getPayload()));
				Packet reply = new Packet(packet.getSrc(), this.mAddr,
						Packet.MAX_TTL, Protocol.PING_REPLY_PKT,
						assignSequenceNumber(), packet.getPayload());
				if (PING_BROADCAST) {
					this.send(Packet.BROADCAST_ADDRESS, reply);
				} else {
					// if using LS protocol
					if (mProtocol == Protocol.LINK_INFO_PKT) {
						this.send(mRoutingTable.getNextHop(reply.getDest()), reply);
						logOutput("Destination match. send ping reply from " + this.mAddr + " for destination " + reply.getDest() + " via " + mRoutingTable.getNextHop(reply.getDest()) + " with message: " + Utility.byteArrayToString(reply.getPayload()), DEBUG_LINKSTATE);
					} // if using Distance vector protocol
					else if (mProtocol == Protocol.DV_INFO_PKT && mDistanceVector.getNextHop(reply.getDest()) != null) {
						this.send(mDistanceVector.getNextHop(reply.getDest()), reply);
						logOutput("Destination match. send ping reply from " + this.mAddr + " for destination " + reply.getDest() + " via " + mDistanceVector.getNextHop(reply.getDest()) + " with message: " + Utility.byteArrayToString(reply.getPayload()), DEBUG_DISTANCEVECTOR);
					} // other protocols, to be extented in the future
					else {
						logOutput("using no protocol", DEBUG_PING);
					}
				}
			} else if (packet.getDest() == Packet.BROADCAST_ADDRESS) {
				// Neighbor Discovery ping: send reply
				logOutput("Neighbor Discovery Ping from " + packet.getSrc() + ", Sending reply.", DEBUG_NEIGHBOR);

				Packet reply = new Packet(Packet.BROADCAST_ADDRESS, this.mAddr,
						1, Protocol.PING_REPLY_PKT, assignSequenceNumber(), packet.getPayload());
				this.send(packet.getSrc(), reply); // No need to broadcast!

				// Let's update our Neighbor table as well
				addNeighbor(packet);
			} else {
				// Destination not match: send it on.
				logOutput("Destination does not match. Resending.", DEBUG_PING);

				if (PING_BROADCAST) {
					this.send(Packet.BROADCAST_ADDRESS, packet);
				} else {
					// if using LS protocol
					if (mProtocol == Protocol.LINK_INFO_PKT) {
						this.send(mRoutingTable.getNextHop(packet.getDest()), packet);
						logOutput("keep on sending from " + this.mAddr + " for destination " + packet.getDest() + " via " + mRoutingTable.getNextHop(packet.getDest()) + " with message: " + Utility.byteArrayToString(packet.getPayload()), DEBUG_LINKSTATE);
					} // if using Distance vector protocol
					else if (mProtocol == Protocol.DV_INFO_PKT && mDistanceVector.getNextHop(packet.getDest()) != null) {
						this.send(mDistanceVector.getNextHop(packet.getDest()), packet);
						logOutput("keep on sending from " + this.mAddr + " for destination " + packet.getDest() + " via " + mDistanceVector.getNextHop(packet.getDest()) + " with message: " + Utility.byteArrayToString(packet.getPayload()), DEBUG_DISTANCEVECTOR);
					} // other protocols, to be extented in the future
					else {
						logOutput("using no protocol", DEBUG_PING);
					}
				}
			}

		} catch (IllegalArgumentException e) {
			logError("Exception while trying to send a Ping Reply. Exception: " + e);
		}
	}

	// Check that ping reply matches what was sent
	private void receivePingReply(Packet packet) {
		Iterator iter = this.mPingArray.iterator();

		String payload = Utility.byteArrayToString(packet.getPayload());

		if (packet.getDest() == this.mAddr) {
			logOutput("Got Ping Reply from " + packet.getSrc() + " for destination " + packet.getDest() + " with message: " + payload, DEBUG_PING);

			// Destination match
			logOutput("Destination match. Reply accepted.", DEBUG_PING);
			while (iter.hasNext()) {
				PingRequest pingRequest = (PingRequest) iter.next();
				if ((pingRequest.getDestAddr() == packet.getSrc()) && (Utility.byteArrayToString(pingRequest.getMsg()).equals(payload))) {

					try {
						iter.remove();
					} catch (Exception e) {
						logError("Exception occured while trying to remove an element from ArrayList pings while processing Ping Reply.  Exception: " + e);
					}
					return;
				}
			}
			logError("Unexpected Ping Reply from " + packet.getSrc() + ": " + payload);
		} else if (packet.getDest() == Packet.BROADCAST_ADDRESS) {
			// Neighbor Discovery reply. Add to neighborTable.
			logOutput("Neighbor Discovery Reply. Updating Table.", DEBUG_NEIGHBOR);
			addNeighbor(packet);
		} else {
			// Destination not match: send it on
			logOutput("Destination does not match. Resending.", DEBUG_PING);

			if (PING_BROADCAST) {
				this.send(Packet.BROADCAST_ADDRESS, packet);
			} else {
				/// if using LS protocol
				if (mProtocol == Protocol.LINK_INFO_PKT) {
					this.send(mRoutingTable.getNextHop(packet.getDest()), packet);
					logOutput("keep on sending ping reply from " + this.mAddr + " for destination " + packet.getDest() + " via " + mRoutingTable.getNextHop(packet.getDest()) + " with message: " + Utility.byteArrayToString(packet.getPayload()), DEBUG_LINKSTATE);
				} /// if using Distance vector protocol
				else if (mProtocol == Protocol.DV_INFO_PKT && mDistanceVector.getNextHop(packet.getDest()) != null) {
					this.send(mDistanceVector.getNextHop(packet.getDest()), packet);
					logOutput("keep on sending ping reply from " + this.mAddr + " for destination " + packet.getDest() + " via " + mDistanceVector.getNextHop(packet.getDest()) + " with message: " + Utility.byteArrayToString(packet.getPayload()), DEBUG_DISTANCEVECTOR);
				} // other protocols, to be extented in the future
				else {
					logOutput("using no protocol", DEBUG_PING);
				}
			}
		}
	}

	/**
	 * Update neighbors and routing tables, and rebroadcast packet.
	 */
	private void receiveLinkInfo(Packet packet) {
		// make sure it is using LS protocol
		if (mProtocol != Protocol.LINK_INFO_PKT) {
			return;
		}
		LinkState linkState = LinkState.unpack(packet.getPayload());
		int[] neighbors = linkState.getNeighbors();
		int src = packet.getSrc();

		logOutput("Received Link Info from " + src + " with neighbors: " + Arrays.toString(neighbors), DEBUG_LINKSTATE);// Rebroadcast (yes, before processing)
		send(Packet.BROADCAST_ADDRESS, packet);

		// Add link state (and rebuild routing table).
		int[] prevNeighbors = mRoutingTable.addLSP(src, neighbors);
		if (prevNeighbors == null || prevNeighbors.length < neighbors.length) {
			// If this LSP is from a 'new' node, or a neighbor is added,
			// advertise name.
			advertiseName();
		}
	}

	/*
	 * receive a distance vector packet from neighbor, merge it with current dv
	 */
	private void receiveDistanceVector(Packet packet) {
		// make sure it is using Distance vector
		if (mProtocol != Protocol.DV_INFO_PKT) {
			return;
		}
		logOutput("receive a distance vector packet", DEBUG_DISTANCEVECTOR);
		if (this.mDistanceVector.merge(packet.getPayload()) == -1) {
			logOutput("distance vector format wrong", DEBUG_DISTANCEVECTOR);
		}
	}

	/**
	 * Update local nameTable with node name.
	 */
	private void receiveNamePkt(Packet packet) {
		// Remove any existing entry for this fisnhet addr.
		// If there isn't one, it silently fails and everyone's happy
		String theName = Utility.byteArrayToString(packet.getPayload());

		// If nameTable already contains an entry for this name, ignore
		if (!mNameTable.containsKey(theName) && !theName.equals(this.mName)) {
			// Iterate through node numbers in table
			for (Entry<String, Integer> entry : mNameTable.entrySet()) {
				// If node has updated its name, remove old name.
				if (entry.getValue() == packet.getSrc()) {
					mNameTable.remove(entry.getKey());
					break; // there can be only one of these.
				}
			}

			// Put the new entry in the table.
			mNameTable.put(theName, packet.getSrc());
			logOutput("New name added: " + theName + " = " + packet.getSrc(), DEBUG_NAME);
		} else {
			logOutput("Name '" + theName + "' not added, already exists", DEBUG_NAME);
		}

		// Flood it on, regardless of whether we modified our table
		send(Packet.BROADCAST_ADDRESS, packet);
	}

	private void addNeighbor(Packet packet) {
		Long prev = mNeighborTable.put(packet.getSrc(), this.mManager.now());
		if (prev == null) {
			// New neighbor! Report this exciting discovery.
			logOutput("Neighbor(s) added: " + mNeighborTable.toString(), DEBUG_NEIGHBOR);
			sendLinkState();

			/* udpate distance vector, (neighborAddr, 0, neighborAddr)
			 */
			mDistanceVector.put(packet.getSrc(), 1, packet.getSrc());
		}
	}

	/**
	 * Broadcasts 'name' to other nodes on network.
	 */
	private void advertiseName() {
		if (this.mName != null) {
			logOutput("Broadcasting name = " + this.mName, DEBUG_NAME);
			Packet packet = new Packet(Packet.BROADCAST_ADDRESS, this.mAddr,
					Packet.MAX_TTL, Protocol.NAME_PKT, assignSequenceNumber(),
					Utility.stringToByteArray(this.mName));
			send(Packet.BROADCAST_ADDRESS, packet);
		}
	}

	private void send(int destAddr, Packet packet) {
		if (destAddr > Packet.MAX_ADDRESS || destAddr < 0) {
			logError("Error: dest addr is not valid");
			return;
		}

		if (packet.getTTL() == 0) {
			logOutput("TTL expired!", DEBUG_RECEIVE_PACKET);
		} else {
			try {
				this.mManager.sendPkt(this.mAddr, destAddr, packet.pack());
			} catch (IllegalArgumentException e) {
				logError("Exception: " + e);
				logOutput("send a packet to " + destAddr + ": " + packet.toString(), true);
			}
		}
	}

	// Adds a timer, to fire in deltaT milliseconds, with a callback to a public
	// function of this class that takes no parameters
	void addTimer(long deltaT, String methodName) {
		try {
			Method method = Callback.getMethod(methodName, this, null);
			Callback cb = new Callback(method, this, null);
			this.mManager.addTimer(this.mAddr, deltaT, cb);
		} catch (Exception e) {
			logError("Failed to add timer callback. Method Name: " + methodName + "\nException: " + e);
		}
	}

	/**
	 * Returns a random number between start and end. Used to give a random
	 * start time to the Neigbor Discovery callback (to prevent flooding in the
	 * Fishnet network by all nodes at the same time).
	 */
	public long random(long start, long end) {
		long range = end - start;
		return Math.round((Math.random() * range) + start);
	}

	public void logError(String output) {
		this.log(output, System.err);
	}

	public void logOutput(String output) {
		this.log(output, System.out);
	}

	public void logOutput(String output, boolean Debug) {
		if (Debug) {
			this.log(output, System.out);
		}
	}
	public synchronized int assignSequenceNumber() {
		return mSequenceNumber++;
	}
	public void log(String output, PrintStream stream) {
		stream.println("[" + this.mManager.now() + "] Node " + this.mAddr + ": " + output);
	}
	public void stabilize(){

		chordNode.stabilize();

	}
}

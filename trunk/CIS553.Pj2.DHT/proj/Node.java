
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * <pre>   
 * Node -- Class defining the data structures and code for the
 * protocol stack for a node participating in the fishnet
 * This code is under student control; we provide a baseline
 * "hello world" example just to show how it interfaces with the FishNet classes.
 *
 * NOTE: per-node global variables are prohibited.
 *  In simulation mode, we execute many nodes in parallel, and thus
 *  they would share any globals.  Please put per-node state in class Node
 *  instead.
 *
 * In other words, all the code defined here should be generic -- it shouldn't
 * matter to this code whether it is executing in a simulation or emulation
 * 
 * This code must be written as a state machine -- each upcall must do its work and return so that 
 * other upcalls can be delivered
 * </pre>   
 */
import java.util.HashMap;
import java.util.HashSet;

public class Node {

	public final long PingTimeout = 10000;  // Timeout pings in 10 seconds
	private Manager manager;
	private int addr;
	private ArrayList pings; // To store PingRequests.
	private HashMap<Integer, Integer> sequenceNumbers;
	private HashMap<Integer, Integer> linkStateSequenceNumbers;
	private HashSet<Integer> previousNeighbors;
	private HashSet<Integer> updatedNeighbors;
	private int currentSequenceNumber;
	private final String POLLING = "polling neighbors";
	private Hashtable<Integer, LinkState> linkStates;
	private LinkState linkState;
	private Hashtable<Integer,Long> linkTime;
	private int linkStateSequence;
	private HashMap<Integer, RoutingEntry> confirmedList;
	private Hashtable<String, Integer> aliasToAddr;
	private Hashtable<Integer, String> addrToAlias;
	private String alias;
	private HashSet<String> keywords;
	private boolean testMessageOn;
	private PennChordNode chordNode;
	private boolean linkStateReady = false;


	/**
	 * Create a new node
	 * @param manager The manager that is managing Fishnet
	 * @param addr The address of this node
	 */
	public Node(Manager manager, int addr) {
		this.manager = manager;
		this.addr = addr;
		this.pings = new ArrayList();
		sequenceNumbers = new HashMap<Integer, Integer>();
		previousNeighbors = new HashSet();
		updatedNeighbors = new HashSet();
		linkStates = new Hashtable<Integer, LinkState>();
		currentSequenceNumber = 0;
		linkStateSequence = 0;
		linkStateSequenceNumbers = new HashMap<Integer, Integer>();
		confirmedList = new HashMap<Integer, RoutingEntry>();
		linkTime = new Hashtable<Integer, Long>();
		aliasToAddr = new Hashtable<String, Integer>();
		addrToAlias = new Hashtable<Integer, String>();
		keywords = new HashSet<String>();
		keywords.add("dump");
		keywords.add("ls");
		testMessageOn = false;
		chordNode = new PennChordNode(this,addr);
	}

	/**
	 * Called by the manager to start this node up.
	 */
	public void start() {
		logOutput("started");
		this.addTimer(PingTimeout, "pingTimedOut");
		this.addTimer(PingTimeout, "neighborDiscovery");
		this.addTimer(PingTimeout*3,"linkStateTimeOut");
		this.addTimer(PingTimeout*2, "floodLinkState");
	}

	public void floodLinkState()
	{
		broadcastLinkState();
		this.addTimer(PingTimeout*2, "floodLinkState");
	}

	public void linkStateTimeOut()
	{
		synchronized(linkStates){
			synchronized(linkTime)
			{
				Iterator<Integer> itr = this.linkTime.keySet().iterator();
				HashSet<Integer> timeouts = new HashSet<Integer>();
				while(itr.hasNext())
				{
					int current=itr.next();
					long was=linkTime.get(current);
					if((manager.now()-was)>(PingTimeout*3))
					{
						timeouts.add(current);

					}

				}
				Iterator<Integer> itr2 = timeouts.iterator();
				while (itr2.hasNext()){
					int it = itr2.next();
					linkTime.remove(it);
					linkStates.remove(it);
					printTestMessage(it+" just timedOut\n");
				}
				linkTime.notifyAll();
				linkStates.notifyAll();
			}
		}

		this.addTimer(PingTimeout*3, "linkStateTimeOut");
	}

	/**
	 * Called by the manager when a packet has arrived for this node
	 * @param from The address of the node that has sent this packet
	 * @param msg The serialized form of the packet.
	 */
	public void onReceive(Integer from, byte[] msg) {
		Packet packet = Packet.unpack(msg);
		//logOutput("received packet from " + from);
		if (packet == null) {
			logError("Unable to unpack message: " + Utility.byteArrayToString(msg) + " Received from " + from);
			return;
		}

		this.receivePacket(from.intValue(), packet);
	}

	/**
	 * Called by the manager when there is a command for this node from the user.
	 * Command can be input either from keyboard or file.
	 * For now sending messages as ping to a neighbor.
	 * @param command The command for this node
	 */
	public void onCommand(String command) {
		
		System.out.println("*****************************************");
		if (this.matchDumpCommand(command)) {
		  this.logOutput("Dump command");
			return;
		} else if (this.matchNameCommand(command)){
		  this.logOutput("match command");
			return;
		} else if (this.matchLsCommand(command)){
		  this.logOutput("Ls command");
			return;
		} else if (this.matchSetCommand(command)){
		  this.logOutput("Set command");
			return;
		}
		else 
		{
		 // this.logOutput("chord command " + chordNode.onCommand(command));
		  if(chordNode.onCommand(command))
		  {	  
  			return;
  		}
  		else if (this.matchPingCommand(command)) {
  			return;
  		}else logError("Unrecognized command: " + command);
		}
		
	}

	private boolean matchSetCommand(String command) {
		String[] parsedCommand = command.split(" ");
		if (!parsedCommand[0].equalsIgnoreCase("set")){
			return false;
		}
		if (command.equalsIgnoreCase("set message on")){
			testMessageOn = true;
		}
		if (command.equalsIgnoreCase("set message off")){
			testMessageOn = false;
		}
		return true;
	}

	private boolean matchLsCommand(String command) {
		command = command.trim();
		if (!command.equalsIgnoreCase("ls"))
			return false;
		Iterator<Integer> addrs = confirmedList.keySet().iterator();
		logOutput("Nodes in the network:\n");
		logOutput("Node Address \t| Alias\n");
		while (addrs.hasNext()){
			int nodeAddr = addrs.next();
			String name = addrToAlias.get(nodeAddr);
			logOutput(nodeAddr+ " \t|" + (name != null? name: "") +"\n");
		}
		return true;
	}

	private boolean matchNameCommand(String command) {
		String[] parsedCommand = command.split(" ");
		if (!parsedCommand[0].equalsIgnoreCase("name")) 
			return false;
		alias = parsedCommand[1];
		if (aliasToAddr.contains(alias)){
			logOutput(alias + " is already in use.\n");
			return true;
		}
		if (keywords.contains(alias)){
			logOutput("Name cannot begin with " + alias +"\n");
			return true;
		}
		try {
			int i = Integer.valueOf(alias);
			logOutput("Cannot name node with a number.\n");
			return true;
		}catch (NumberFormatException e){
		}
		aliasToAddr.put(alias, this.addr);
		addrToAlias.put(this.addr, alias);
		introduce();
		return true;
	}

	/**
	 * Send out name packets
	 */
	private void introduce() {
		Packet packet = new Packet(Packet.BROADCAST_ADDRESS, this.addr, Packet.MAX_TTL,
				Protocol.NAME_PKT, this.assignSequenceNumber(), Utility.stringToByteArray(alias));
		this.send(Packet.BROADCAST_ADDRESS, packet);
	}

	/**
	 * Callback method given to manager to invoke when a timer fires.
	 */
	public void pingTimedOut() {
		Iterator iter = this.pings.iterator();
		while (iter.hasNext()) {
			PingRequest pingRequest = (PingRequest) iter.next();
			if ((pingRequest.getTimeSent() + PingTimeout) < this.manager.now()) {
				try {
					logOutput("Timing out ping: " + pingRequest);
					iter.remove();
				} catch (Exception e) {
					logError("Exception occured while trying to remove an element from ArrayList pings.  Exception: " + e);
					return;
				}
			}
		}
		this.addTimer(PingTimeout, "pingTimedOut");
	}

	private void broadcastLinkState() throws IllegalArgumentException {

		//when new neighbors are found broadcast linkstate packets
		//first create array of neighbors
		if (previousNeighbors.isEmpty()){
			printTestMessage("No Neighbor information found yet\n");
			return;
		}
		Integer[] temp = this.previousNeighbors.toArray(new Integer[0]);
		int[] neighbors = new int[temp.length];
		for (int i = 0; i < temp.length; i++) {
			neighbors[i] = temp[i];
		}
		linkState = new LinkState(neighbors);
		Packet linkStatePacket = new Packet(Packet.BROADCAST_ADDRESS, this.addr, Packet.MAX_TTL, Protocol.LINK_INFO_PKT, this.linkStateSequence, linkState.pack());
		this.linkStateSequence++;
		this.send(Packet.BROADCAST_ADDRESS, linkStatePacket);
	}

	private boolean isValidLinkStateSequenceNumber(Packet packet) {

		int thisPacketSequence = packet.getSeq();
		int lastSeq;

		try {
			lastSeq = this.linkStateSequenceNumbers.get(packet.getSrc());
		} catch (NullPointerException npe) {
			lastSeq = -1;
		}

		if (lastSeq >= thisPacketSequence) {
			return false;
		}
		this.linkStateSequenceNumbers.put(packet.getSrc(), packet.getSeq());
		return true;

	}

	private boolean matchPingCommand(String command) {
		int index = command.indexOf(" ");
		if (index == -1) {
			return false;
		}
		try {
			int destAddr = -1;
			String dest = command.substring(0, index);
			try{
				destAddr= Integer.parseInt(dest);
			} catch (NumberFormatException e){
				Integer tempAddr = aliasToAddr.get(dest);
				if ( tempAddr == null){
					logOutput("Bad command or Unrecognized destination name.\n");
					return true;
				}
				destAddr = tempAddr;
			}
			String message = command.substring(index + 1);
			Packet packet = new Packet(destAddr, this.addr, Packet.MAX_TTL, Protocol.PING_PKT, this.assignSequenceNumber(),
					Utility.stringToByteArray(message));
			int nextHop = confirmedList.get(packet.getDest()).nextHop;
			this.send(nextHop, packet);
			this.pings.add(new PingRequest(destAddr, Utility.stringToByteArray(message), this.manager.now()));
			return true;
		} catch (NullPointerException e){
			logOutput("Routing information not complete yet. Please try again later\n");
			return true;
		} catch (Exception e) {
			logError("Exception: " + e);
		}
		return false;
	}

	private boolean matchDumpCommand(String command) {
		command = command.trim();
		String[] parsedCommand = command.split(" ");
		if (!parsedCommand[0].equalsIgnoreCase("dump")) {
			return false;
		}
		if (command.equalsIgnoreCase("dump neighbors")) {
			printNeighbors();
			return true;
		}
		if (command.equalsIgnoreCase("dump linkstate")) {
			dumpLinkState();
			return true;
		}
		if (command.equalsIgnoreCase("dump table")) {
			dumpTable();
			return true;
		}

		return false;
	}

	private void dumpLinkState() {

		Iterator<Integer> itr = linkStates.keySet().iterator();
		while(itr.hasNext()){
			int current=itr.next();
			int[] neighbors = linkStates.get(current).getNeighbors();
			logOutput("Neighbors of:"+current+" are: ");
			for(int i=0; i<neighbors.length;i++)
			{
				logOutput(neighbors[i]+",");

			}
			logOutput("\n");
		}
		return;

	}

	private void dumpTable() {
		Set<Map.Entry<Integer, RoutingEntry>> routeSet = confirmedList.entrySet();
		Iterator<Map.Entry<Integer, RoutingEntry>> routeListIterator = routeSet.iterator();
		logOutput("The content of the Routing Table for ---------------" + this.addr + " is:\n");
		while (routeListIterator.hasNext()) {
			Map.Entry<Integer, RoutingEntry> entry = routeListIterator.next();
			int destAddr = entry.getKey();
			int cost = entry.getValue().cost;
			int nextHop = entry.getValue().nextHop;
			logOutput(destAddr + ", " + cost + ", " + nextHop + "\n");
		}

	}

	private void printNeighbors() {
		Iterator<Integer> itr = this.previousNeighbors.iterator();
		logOutput("Neighbors of " + this.addr + " seen during last poll:\n");
		while (itr.hasNext()) {
			logOutput(itr.next() + ",");
		}
		logOutput("\n");

		itr = this.updatedNeighbors.iterator();
		logOutput("Neighors " + this.addr + "  seen during this poll:\n");
		while (itr.hasNext()) {
			logOutput(itr.next() + ",");
		}
		logOutput("\n");

	}

	public void neighborDiscovery() {
		if (!this.previousNeighbors.equals(this.updatedNeighbors)) {
			//this.printNeighbors();
			this.previousNeighbors = this.updatedNeighbors;
			broadcastLinkState();
		}
		this.updatedNeighbors = new HashSet<Integer>();
		byte[] payload = this.POLLING.getBytes();
		Packet packet = new Packet(Packet.BROADCAST_ADDRESS, this.addr, 1, Protocol.PING_PKT, this.assignSequenceNumber(), payload);
		this.send(Packet.BROADCAST_ADDRESS, packet);
		this.addTimer(PingTimeout, "neighborDiscovery");
	}

	public void receivePacket(int from, Packet packet) {
		if (packet.getSrc() == this.addr) {
			return;
		}
		if (!isValidSequence(packet)) return;
		packet.setTTL(packet.getTTL() - 1);
		if (!(packet.getDest() == this.addr || packet.getDest() == Packet.BROADCAST_ADDRESS)) {
			//The packet is for someone else

			if (packet.getTTL() > 0) {
				try{
					int nextHop = confirmedList.get(packet.getDest()).nextHop;
					logOutput("Received a packet from " + packet.getSrc() + 
							" for " + packet.getDest() + ". Forwarding to " + nextHop);
					this.send(nextHop, packet);
				}catch (NullPointerException e){
					logOutput("Cannot forward packet due to incomplete routing table\n");
					return;
				}
			}
			return;
		}


		switch (packet.getProtocol()) {

		case Protocol.PING_PKT:
			this.receivePing(packet);
			break;

		case Protocol.PING_REPLY_PKT:
			this.receivePingReply(packet);
			break;

		case Protocol.LINK_INFO_PKT:
			this.receiveLinkState(packet);
			break;

		case Protocol.NAME_PKT:
			this.receiveName(packet);
			break;
		default:
			chordNode.processPacket(from, packet);
		}
	}

	private void receiveName(Packet packet) {
		String name = Utility.byteArrayToString(packet.getPayload());
		if (addrToAlias.containsKey(packet.getSrc())){
			addrToAlias.remove(packet.getSrc());//An old name stored for the node
		}
		aliasToAddr.put(name, packet.getSrc());
		addrToAlias.put(packet.getSrc(), name);
		this.send(Packet.BROADCAST_ADDRESS, packet);
	}

	/**
	 * This function is for validation of sequence numbers for all protocols.
	 */
	private boolean isValidSequence(Packet packet) {
		if (packet.getProtocol() == Protocol.LINK_INFO_PKT)
			return isValidLinkStateSequenceNumber(packet);
		int thisPacketSequenceNumber = packet.getSeq();
		int packetSource = packet.getSrc();
		int lastSequence = -1;
		//try
		{
			if (this.sequenceNumbers.get(packetSource) == null) {
				this.sequenceNumbers.put(packetSource, thisPacketSequenceNumber);
				return true;
			} else {
				lastSequence = this.sequenceNumbers.get(packetSource);
				if (lastSequence >= thisPacketSequenceNumber) {

					return false;

				} else {
					this.sequenceNumbers.put(packetSource, thisPacketSequenceNumber);
					return true;
				}
			}
		}

	}

	private void receivePing(Packet packet) {
		if (!Utility.byteArrayToString(packet.getPayload()).equals(POLLING) 
				|| testMessageOn)
			logOutput("Received Ping from " + packet.getSrc() + " with message: "
					+ Utility.byteArrayToString(packet.getPayload()));
		try {
			//if the packet is for us
			if (packet.getDest() == this.addr) {
				Packet reply = new Packet(packet.getSrc(), this.addr, Packet.MAX_TTL, Protocol.PING_REPLY_PKT, this.assignSequenceNumber(), packet.getPayload());
				int nextHop = confirmedList.get(packet.getSrc()).nextHop;
				this.send(nextHop, reply);
				return;
			} else if (packet.getDest() == Packet.BROADCAST_ADDRESS && packet.getTTL() == 0) {
				this.updatedNeighbors.add(packet.getSrc());
				//We know who this neighbor is.
				//He will know about us when we ping him
				//We do not reply to broadcast packets.
			} else if (packet.getDest() == Packet.BROADCAST_ADDRESS) {
				// do nothing
				//according to RFC 1122 pings to broadcast address may be silently
				//discarded
			}
		} catch(NullPointerException e){
			logOutput("Packet not routed due to incomplete routing table\n");
			return;
		} catch (IllegalArgumentException e) {
			logError("Exception while trying to send a Ping Reply. Exception: " + e);
			return;
		}
	}

	// Check that ping reply matches what was sent
	private void receivePingReply(Packet packet) {
		Iterator iter = this.pings.iterator();
		String payload = Utility.byteArrayToString(packet.getPayload());
		while (iter.hasNext()) {
			PingRequest pingRequest = (PingRequest) iter.next();
			if ((pingRequest.getDestAddr() == packet.getSrc()) &&
					(Utility.byteArrayToString(pingRequest.getMsg()).equals(payload))) {

				logOutput("Got Ping Reply from " + packet.getSrc() + ": " + payload);
				try {
					iter.remove();
				} catch (Exception e) {
					logError("Exception occured while trying to remove an element from ArrayList pings while processing Ping Reply.  Exception: " + e);
				}
				return;
			}
		}
		logError("Unexpected Ping Reply from " + packet.getSrc() + ": " + payload);
	}

	private void receiveLinkState(Packet packet) {
		if (!isValidLinkStateSequenceNumber(packet)) {
			return;
		}
		LinkState newLinkState = LinkState.unpack(packet.getPayload());
		synchronized(linkStates){
			this.linkStates.put(packet.getSrc(), newLinkState);

			synchronized(linkTime)
			{
				this.linkTime.put(packet.getSrc(), manager.now());
				linkTime.notifyAll();
			}
			linkStates.notifyAll();
		}

		//broadcast to others
		if (packet.getTTL() > 0) {
			this.send(Packet.BROADCAST_ADDRESS, packet);
		}
		computeRoutingTable();
	}

	private void computeRoutingTable() {

		//check if possible first
		if (previousNeighbors.isEmpty()){
			printTestMessage("No neighbor discovered yet for Node " + this.addr+"\n");
			return;
		}
		synchronized(linkStates){
			this.linkStates.put(addr, linkState);
			linkStates.notifyAll();
		}

		//Check if have linkState information from all nodes
		HashSet<Integer> nodes = new HashSet<Integer>();
		HashSet<Integer> neigh = new HashSet<Integer>();
		Iterator<Integer> itr = this.linkStates.keySet().iterator();
		while (itr.hasNext()) {
			int key = itr.next();
			nodes.add(key);
			if (!linkStates.containsKey(key)){
				break;
			}
			LinkState lin = linkStates.get(key);
			if (lin == null){
				break;
			}
			for (int i : lin.getNeighbors()) {
				neigh.add(i);
			}
		}
		if (!nodes.equals(neigh)) {
			printTestMessage("LinkState information not complete yet\n");
			linkStateReady = false;
			return;
		}
		linkStateReady = true;

		//Step 1: Initialize the routeList with an entry for my self; this entry has a cost of 0
		confirmedList.clear();
		HashMap<Integer, RoutingEntry> tempList = new HashMap<Integer, RoutingEntry>();
		confirmedList.put(this.addr, new RoutingEntry(0, this.addr));
		synchronized(linkStates){
			linkStates.put(this.addr, linkState);
			linkStates.notifyAll();
		}

		//Step 2: For the node just added to the routeList, call it Next, select its LSP
		int next = this.addr;
		int[] thisNeighbors = linkState.getNeighbors();
		for (int nbr : thisNeighbors) {
			tempList.put(nbr, new RoutingEntry(1, nbr));   
		}

		confirmedList.put(thisNeighbors[0], tempList.get(thisNeighbors[0]));
		tempList.remove(thisNeighbors[0]);
		next = thisNeighbors[0];
		while (true) {
			int[] neighbors;
			neighbors = linkStates.get(next).getNeighbors();
			//Step 3: For each neighbor of Next, calculate the Cost to reach this Neighbor
			//as the sum of the cost from myself to Next and from Next to Neighbor.
			for (int i = 0; i < neighbors.length; i++) {
				int neighbor = neighbors[i];
				RoutingEntry neighborRouteEntry = confirmedList.get(neighbor);
				RoutingEntry neighborTempRouteEntry = tempList.get(neighbor);
				int costToNext = confirmedList.get(next).cost;
				int nextHop = confirmedList.get(next).nextHop;
				if (neighborRouteEntry == null && neighborTempRouteEntry == null) {
					tempList.put(neighbor, new RoutingEntry(costToNext + 1, nextHop));
				} else if (neighborTempRouteEntry != null) {
					int tempCost = neighborTempRouteEntry.cost;
					if (tempCost > costToNext + 1) {
						tempList.put(neighbor, new RoutingEntry(costToNext + 1, nextHop));
					}
				}
			}

			//Step 4: 
			if (tempList.isEmpty()) {
				break;
			} else {
				int minCost = -1;
				int minCostNode = -1;
				Set<Map.Entry<Integer, RoutingEntry>> tempSet = tempList.entrySet();
				Iterator<Map.Entry<Integer, RoutingEntry>> tempListIterator = tempSet.iterator();
				while (tempListIterator.hasNext()) {
					Map.Entry<Integer, RoutingEntry> k = tempListIterator.next();
					int cost = k.getValue().cost;
					int node = k.getKey();
					if (minCost == -1 || minCost > cost) {
						minCost = cost;
						minCostNode = node;
					}
				}
				confirmedList.put(minCostNode, tempList.get(minCostNode));
				next = minCostNode;
				tempList.remove(minCostNode);
			}
		}

	}

	public void send(int destAddr, Packet packet) {
		try {
			this.manager.sendPkt(this.addr, destAddr, packet.pack());
		} catch (IllegalArgumentException e) {
			logError("Exception: " + e);
		}
	}

	// Adds a timer, to fire in deltaT milliseconds, with a callback to a public function of this class that takes no parameters
	public void addTimer(long deltaT, String methodName) {
		try {
			Method method = Callback.getMethod(methodName, this, null);
			Callback cb = new Callback(method, this, null);
			this.manager.addTimer(this.addr, deltaT, cb);
		} catch (Exception e) {
			logError("Failed to add timer callback. Method Name: " + methodName +
					"\nException: " + e);
		}
	}

	private void logError(String output) {
		this.log(output, System.err);
	}

	public void logOutput(String output) {
		this.log(output, System.out);
	}

	private void log(String output, PrintStream stream) {
		stream.println("Node " + this.addr + ": " + output);
	}
	
	public synchronized int assignSequenceNumber() {
		return currentSequenceNumber++;
	}
	
	
	private void printTestMessage(String message){
		if (!testMessageOn){
			return;
		}
		else logOutput(message);
	}
	
	public void stabilize(){
	
		chordNode.stabilize();
		
	}
	public boolean isLinkStateReady() {
		return linkStateReady;
	}
}

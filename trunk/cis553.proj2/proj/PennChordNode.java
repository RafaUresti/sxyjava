import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.lang.Math;
import java.math.BigInteger;
import java.util.Random;
import java.util.Random;
import sun.rmi.log.LogOutputStream;
/**
 * This class encapsulates simplified functionalities of a chord node.
 * It serves as an overlay node above the Fishnet node.
 * To use this class, you need to do the following modification to your Node.java in project 1.
 * 1) Add an instance of this class in your Fishnet Node
 * 				
 * 		PennChordNode chordNode; 
 * 
 * 2) Initialize the PennChordNode in your Fishnet Node's constructor
 * 
 * 		chordNode = new PennChordNode(this,addr);
 * 
 * 3) Modify your Fishnet Nodes' receivePacket() function so that when a packet with an 
 *    unknown protocol is received, Fishnet Node will call PennChordNode's processPacket()
 * 
 * 		switch (packet.getProtocol())
 *		{
 *			case Protocol.PING_PKT:
 *				...
 *			default:
 * 		    	chordNode.processPacket(from, packet);
 *		}
 *
 * 4) Modify your Fishnet Nodes' onCommand() function so that when an PennChord command is
 *    received, Fishnet Node will call PennChordNode's onCommand()
 *      
 *      if (this.matchPingCommand(command))
 *		....
 *		else
 *		{
 *			if( !chordNode.onCommand(command) )
 *				logError("Unrecognized command: " + command);
 *		}
 * 5) Modify the access modifier of your Fishnet Node's functions such as addTimer(),
 *    receivePacket(),logOutput() from private to public, so that PennChordNode can 
 *    untilize these functions
 * 
 */
public class PennChordNode
{
	private Node baseNode;
	private int myAddr;
	private int successor;
	private long successorKey;
	private int predecessor;
	private long predecessorKey;
	private Hashtable<String, ArrayList<String>> dataTable; //Mapping Key to Values
	private long myAddrKey;
	private final int STABILIZE_TIME = 1000;
	private boolean lookupDebugOn;
	private boolean debugOn;
	private int lookupCount;
	private int hopCount;
	private int entryNode;
	private boolean newToRing;
	private boolean inRing;
	public PennChordNode(Node baseNode,int addr)
	{
		this.baseNode = baseNode;
		this.myAddr = addr;
		this.myAddrKey = genKeyForNode();
		initializePennChordNode();
		baseNode.addTimer(baseNode.PingTimeout*10, "stabilize");//TODO check if right
	}

	private void initializePennChordNode() {
		debugOn = false;
		lookupDebugOn = false;
		this.predecessor = -1;
		this.predecessorKey = -1;
		this.successor = -1;
		this.successorKey = -1;
		this.dataTable = new Hashtable<String, ArrayList<String>>();
		lookupCount = 0;
		hopCount = 0;
		entryNode = -1;
		this.newToRing = false;
		this.inRing = false;
	}

	/**
	 * Called by underlying Fishnet node when a packet with an unknown protocol is received.
	 * @param from The direct neighbor who send/forward the packet
	 * @param packet The packet to process
	 */
	public  void processPacket(int from, Packet packet)
	{
		// process incoming packets here.
		if (!isRoutingTableComplete()) return;
		if (packet.getProtocol() != Protocol.CHORD_PKT){
			baseNode.logOutput("unrecognized protocol type");
			return;
		}
		byte[] payloadChord = packet.getPayload();
		PennChordMessage pennChordMsg = unpack(payloadChord);
		if (!inRing && PennChordMessage.MessageType.JOIN_REPLY != pennChordMsg.getMessageType())
			return;//Do not respond to a PennChordMessage if not part of ring
		
		if (PennChordMessage.MessageType.JOIN == pennChordMsg.getMessageType()){
			receiveJoin(packet);
		}
		else if (PennChordMessage.MessageType.JOIN_REPLY == pennChordMsg.getMessageType()){
			receiveJoinReply(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.NOTIFY == pennChordMsg.getMessageType()){
			receiveNotify(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.STABILIZE == (pennChordMsg.getMessageType())){
			receiveStabilize(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.STABILIZE_REPLY == (pennChordMsg.getMessageType())){
			receiveStabilizeReply(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.STORE == (pennChordMsg.getMessageType())){
			receiveStore(packet);
		}
		else if (PennChordMessage.MessageType.LOOKUP == (pennChordMsg.getMessageType())){
			printDebug("recieved lookup request from: "+pennChordMsg.getSrc() + "with sequence#: "+  packet.getSeq() + "from "+packet.getSrc() );
			receiveLookup(packet);
		}
		else if (PennChordMessage.MessageType.LOOKUP_REPLY == (pennChordMsg.getMessageType())){
			receiveLookupReply(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.CHANGE_PREDECESSOR == (pennChordMsg.getMessageType())){
			receiveChangePredecessor(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.CHANGE_SUCCESSOR == (pennChordMsg.getMessageType())){
			receiveChangeSuccessor(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.REQUEST_DATA == (pennChordMsg.getMessageType())){
			receiveDataRequest(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.RINGSTATE_OUTPUT == (pennChordMsg.getMessageType())){
			receiveRingstate(pennChordMsg);
		}
		else if(PennChordMessage.MessageType.REQUEST_DEBUG == (pennChordMsg.getMessageType())){
			receiveRequestDebug(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.REQUEST_LOOKUP_HOPS == (pennChordMsg.getMessageType())){
			receiveLookupHops(pennChordMsg);
		}
		else if (PennChordMessage.MessageType.TRANSFER_DATA == (pennChordMsg.getMessageType())){
			receiveTransferData(pennChordMsg);
		}
		else {
			baseNode.logOutput("unrecognized message received:" + pennChordMsg.getMessageType());
		}
	}

	private void receiveChangeSuccessor(PennChordMessage pennChordMsg) {
		int newSuccessor = (int) pennChordMsg.getArgument1();
		if (newSuccessor < 0 )
			return;
		successor = newSuccessor;
		successorKey = pennChordMsg.getArgument2();
		printDebug("---------Node "+ myAddr+ " successor is " + successor);
	}

	private void receiveChangePredecessor(PennChordMessage pennChordMsg) {
		int newPredecessor = (int) pennChordMsg.getArgument1();
		if (newPredecessor < 0)
			return;
		predecessor = newPredecessor;
		predecessorKey = pennChordMsg.getArgument2();
		printDebug("---------Node "+ myAddr+ " predecessor is " + predecessor);

	}

	/**
	 * Send join message to the node with a specified fishnet address.
	 * If the entry node is current node, initiate a new ring.
	 * @param entryNodeAddr
	 */
	public void join(int entryNodeAddr)
	{	
		baseNode.logOutput("I am:"+ this.myAddr + ", my key:" + myAddrKey + ".Joining "+entryNodeAddr);
		this.entryNode = entryNodeAddr;
		if(entryNodeAddr == this.myAddr)
		{
			//create a new ring, e.g. 0 join 0
			this.successorKey = this.myAddrKey;
			this.successor = this.myAddr;
			this.predecessorKey = this.myAddrKey;
			this.predecessor = this.myAddr;
			baseNode.logOutput("create ring");
			this.inRing = true;
		}else{
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN, myAddr, this.myAddrKey,
					null, null);
			byte[] packedChordMessage = pack(chordMessage);
			Packet joinPacket = new Packet(entryNodeAddr, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(entryNodeAddr, joinPacket);
		}
	}

	private void receiveJoin(Packet packet){
		PennChordMessage pcm = unpack(packet.getPayload());
		int src = pcm.getSrc();
		long srcKey = pcm.getSrcAddrKey(); 
		if (srcKey == myAddrKey || srcKey == successorKey){//if there is addrKey duplication, send a join_reply with -1 as auguments
			baseNode.logOutput("Duplication key: "+ srcKey +" found for newly joined node: "+src + "with existing node:" + (srcKey == myAddrKey? myAddr:successor)+"");
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN_REPLY, -1, -1, null, null);
			byte[] packedChordMessage = pack(chordMessage);
			Packet joinReplyPacket = new Packet(src, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(src, joinReplyPacket);
			return;
		}
		baseNode.logOutput("My addKey:"+myAddrKey+" ReceiveJoin : srcKey: "+srcKey);
		if (isOnlyNode()){//Only one node in ring
			this.successor = src;
			this.successorKey = srcKey; 
			this.predecessor = src;
			this.predecessorKey = srcKey;
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN_REPLY, myAddr, this.myAddrKey,
					null, null);
			byte[] packedChordMessage = pack(chordMessage);
			Packet joinReplyPacket = new Packet(src, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(src, joinReplyPacket);
			baseNode.logOutput("Only one node in ring and hence accept the joining msg of Node: "+src);
		}else if (srcKey > this.myAddrKey && srcKey < this.successorKey){//between me and my successor
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN_REPLY, successor, this.successorKey,
					null, null);
			byte[] packedChordMessage = pack(chordMessage);
			Packet joinReplyPacket = new Packet(src, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			//baseNode.logOutput(src + " is between me and my successor "+ successor+", hence send my successor in join_reply");
			sendPacket(src, joinReplyPacket);
		}
		else if (this.successorKey < this.myAddrKey && (this.myAddrKey < srcKey || srcKey < this.successorKey)){
			//I am the biggest myAddrKey of the ring, my successor is the smallest
			//and (srcKey is bigger than me OR srcKey is smaller than my successor)
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN_REPLY, successor, this.successorKey,
					null, null);
			//baseNode.logOutput("I am the biggest node and hence accepting the addres bigger than me");
			byte[] packedChordMessage = pack(chordMessage);
			Packet joinReplyPacket = new Packet(src, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(src, joinReplyPacket);
		}else if (srcKey < this.myAddrKey){//before me, pass onto my successor to go around the ring
			//baseNode.logOutput(src+" is before me, pass onto my successor"+successor+" to go around the ring");
			Packet joinReplyPacket = new Packet(this.successor, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packet.getPayload());
			sendPacket(this.successor, joinReplyPacket);
		}else if (srcKey > this.myAddrKey && srcKey > this.successorKey){//larger than me and successor, not for me
			Packet joinReplyPacket = new Packet(this.successor, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packet.getPayload());
			sendPacket(this.successor, joinReplyPacket);
		}
		else baseNode.logOutput("!!!!!!!!!!!!!!!!!Not accepting "+ src + " into our ring!");
	}

	/**
	 * Gets successor information from the JOIN_REPLY message
	 * @param pcm JOIN_REPLY message
	 */
	private void receiveJoinReply(PennChordMessage pcm){
		if ((int)pcm.getArgument1() < 0){//addrKey duplication detected
			baseNode.logOutput("Duplication key "+myAddrKey+" found");
			this.myAddrKey = genKeyForNode();
			baseNode.logOutput("Joining with new key "+myAddrKey);
			join(entryNode);
			return;
		}
		this.successor = (int)pcm.getArgument1();
		this.successorKey = pcm.getArgument2();
		this.newToRing = true;
		this.inRing = true;
		//baseNode.logOutput("In receiveJoinReply: My "+this.myAddr+"successor is:"+successor);
	}

	/**
	 * Called by manager to store data
	 */
	public void store(String key,String value)
	{
		long lookupKey = hashCode(key);//FIXME
		printDebug("hashCode(key) : "+lookupKey);
		printDebug("current Node hash address: "+this.myAddrKey);
		ArrayList<String> valueList = new ArrayList<String>();
		//if the key should be stored in this node
		if (isKeyMine(lookupKey) ){
			if (dataTable.containsKey(key)) {
				valueList = dataTable.get(key);
				valueList.add(value);
				dataTable.put(key, valueList);
			}else {
				valueList.add(value);
				dataTable.put(key, valueList);
			}
			printDebug(key + " has been stored.");
			Iterator<String> itr = dataTable.keySet().iterator();
			printDebug("Output data table----------------");
			while(itr.hasNext()){
				String nextKey = itr.next();
				printDebug(nextKey+", " + dataTable.get(nextKey));
			}
		}else{//Send a STORE message to successor
			valueList.add(value);
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.STORE, 0, 0,
					key, valueList);   // change the packet structure... added myAddrkey and for srcAddrKey and for argAddrkey

			byte[] packedChordMessage = pack(chordMessage);
			Packet storePacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
					baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(successor, storePacket);
		}
	}

	/**
	 * <pre>
	 * Checks if the key is for me:
	 * 1. I am the only node in the ring
	 * 2. The key is bigger than my predecessorKey and smaller or equal to myAddrKey
	 * 3. I am the smallest node in the ring
	 * 		2.1 the lookupKey is smaller than me
	 * 		2.2 the lookupKey is bigger than my predecessor
	 * </pre>
	 * @param lookupKey The key to look up
	 * @return <code>true</code> if this key is my responsibility
	 */
	private boolean isKeyMine(long lookupKey) {
		return isOnlyNode()||(lookupKey <= this.myAddrKey && lookupKey > this.predecessorKey) || 
		(isSmallestNode()&& (lookupKey < myAddrKey || lookupKey > predecessorKey));
	}

	private void receiveStore(Packet storePacket) {
		byte[] payloadChord = storePacket.getPayload();
		PennChordMessage penChordMsg =  null;
		String msgKey=null;
		ArrayList<String> newValueList = new ArrayList<String> ();
		ArrayList<String> existingValueList = new ArrayList<String> ();

		penChordMsg = unpack(payloadChord);
		msgKey = penChordMsg.getKey();
		long lookupKey = hashCode(msgKey);
		newValueList = penChordMsg.getValue();

		//if the key should be stored in this node
		if (isKeyMine(lookupKey)){
			printKeyValuePairs(msgKey, newValueList);
			if (dataTable.containsKey(msgKey)) {
				existingValueList = dataTable.get(msgKey);
				existingValueList.addAll(newValueList);
				dataTable.put(msgKey, existingValueList);
			}else {
				dataTable.put(msgKey, newValueList);
			}

		}else{//Send a look up message to successor
			storePacket = new Packet(this.successor, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), storePacket.getPayload());
			sendPacket(successor, storePacket);
		}
	}


	public void lookup(String key)
	{
		//send lookup message to find the value corresponds to the key.
		//the result is printed when the response message is received(not in this function)
		long lookupKey = hashCode(key);
		lookupCount++;
		//if the key should be stored in this node
		if (isKeyMine(lookupKey)){
			//if I am not the smallest myAddrKey, and the key is within my range
			outputKeyValues(key);
		}
		else{//Send a look up message to successor
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.LOOKUP, 0, 0,
					key, null);        // change the packet structure... added myAddrkey and for srcAddrKey and for argAddrkey
			if (lookupDebugOn){
				printLookupDebugInfo(key, lookupKey);
				chordMessage.setDebugOn(true);
			}
			byte[] packedChordMessage = pack(chordMessage);
			Packet lookupPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
					baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(successor, lookupPacket);
		}
		lookupDebugOn = false;
	}

	private void printLookupDebugInfo(String key, long lookupKey) {
		baseNode.logOutput("LookupRequest<" + this.myAddr + ", " + this.myAddrKey
				+ ">: RequestString<" + key + ", " + lookupKey + "), NextHop<" 
				+ this.successor + ", " + this.successorKey + ">");
	}

	/**
	 * Print out the key and the corresponding value
	 * @param key The key to print
	 */
	private void outputKeyValues(String key) {
		if (!dataTable.containsKey(key)){
			baseNode.logOutput(key + "is not maching found!");
			return;
		}
		else{
			ArrayList<String> values = dataTable.get(key);
			printKeyValuePairs(key, values);
		}
	}

	private void printKeyValuePairs(String key, ArrayList<String> values) {
		for (String value: values){
			baseNode.logOutput("<"+key+", "+value+">" + " hashKey:" + hashCode(key));
		}
		System.out.println("\n\n");
	}

	private void printDataTable() {
		Iterator<String> itr = dataTable.keySet().iterator();
		baseNode.logOutput("Output data table:");
		while(itr.hasNext()){
			String nextKey = itr.next();
			printKeyValuePairs(nextKey, dataTable.get(nextKey));
		}
	}

	private void receiveLookup(Packet packet){
		hopCount++;
		PennChordMessage pcm = unpack(packet.getPayload());
		String lookupString = pcm.getKey();
		int src = pcm.getSrc();
		long lookupKey = hashCode(lookupString);

		if (isKeyMine(lookupKey)){
			sendKeyValues(lookupString, src);
		}
		else{//Pass the packet onto successor
			if (pcm.isDebugOn()){
				printLookupDebugInfo(lookupString, lookupKey);
			}
			packet = new Packet(this.successor, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), pack(pcm));
			sendPacket(successor, packet);
		}
	}

	private void sendKeyValues(String key, int src){
		if (!dataTable.containsKey(key)){
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey, 
					PennChordMessage.MessageType.LOOKUP_REPLY, 0, 0,
					key, null);
			byte[] packedChordMessage = pack(chordMessage);
			Packet lookupReplyPacket = new Packet(src, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
					baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(src, lookupReplyPacket);
			return;
		}
		else{
			ArrayList<String> values = dataTable.get(key);
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.LOOKUP_REPLY, 0, 0,
					key, values);
			byte[] packedChordMessage = pack(chordMessage);
			Packet lookupPacket = new Packet(src, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
					baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(src, lookupPacket);
		}
	}

	private void receiveLookupReply(PennChordMessage pcm){
		ArrayList<String> values = pcm.getValue();
		if (values == null){
			baseNode.logOutput("No values found for "+pcm.getKey()+"\n\n");
			return;
		}
		baseNode.logOutput("Values found after for "+pcm.getKey()+" are:\n");
		printKeyValuePairs(pcm.getKey(), values);
	}

	/**
	 * Converts a PennChordMessage object to byte array
	 * @param chordMessage
	 * @return byte array of the object
	 */
	private byte[] pack(PennChordMessage chordMessage) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(chordMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return bos.toByteArray();
	}

	public static PennChordMessage unpack(byte[] payloadChord) {
		PennChordMessage pennChordMsg = null;
		ByteArrayInputStream inputStrem = new ByteArrayInputStream(payloadChord);
		try {
			ObjectInputStream ois = new ObjectInputStream(inputStrem);
			//baseNode.logOutput("Reading object...");
			Object obj = ois.readObject();
			//baseNode.logOutput("Object read, o = " + obj + ".");
			pennChordMsg = (PennChordMessage)obj;
		}
		catch (IOException cnfe) {
			System.out.println("IOException!");
			cnfe.printStackTrace();
		}
		catch(ClassNotFoundException e){
			System.out.println("ClassNotFoundException!");
			e.printStackTrace();
		}
		return pennChordMsg;
	}

	public void stabilize(){
		printDebug("stabilize called");
		if (isOnlyNode()){//I am the only node in the ring
			baseNode.addTimer(STABILIZE_TIME, "stabilize");
			return;
		}
		PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
				PennChordMessage.MessageType.STABILIZE, 0, 0,
				null, null);
		byte[] packedChordMessage = pack(chordMessage);
		if (successor >= 0 && baseNode.isLinkStateReady()){
			Packet predecessorPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
					baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(successor, predecessorPacket);
		}
		baseNode.addTimer(STABILIZE_TIME, "stabilize");//TODO check if right
	}

	/**
	 * Called when receiving a PennChordMessage.messageType.STABILIZE message asking for
	 * my predecessor.
	 * 
	 * @param packet Message asking for my predecessor
	 */
	private void receiveStabilize(PennChordMessage pcm){
		int src = pcm.getSrc();

		PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey, 
				PennChordMessage.MessageType.STABILIZE_REPLY, predecessor, predecessorKey,
				null, null);
		byte[] packedChordMessage = pack(chordMessage);
		Packet predecessorPacket = new Packet(src, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedChordMessage);
		printDebug("Receive stabilize: My SUCCESSOR &&&&&&&&&&&&&&&&&&&&&:"+successor+", my predecessor is:" + predecessor);
		sendPacket(src, predecessorPacket);
	}

	/**
	 * Called when receiving a PennChordMessage.messageType.STABILIZE_REPLY message,
	 * which includes the predecessor of my successor
	 * @param pcm message containing my successor's predecessor
	 */
	private void receiveStabilizeReply(PennChordMessage pcm){
		int src = pcm.getSrc();
		int x = (int)pcm.getArgument1();
		printDebug("Received stabilizeReply from "+ src +" that has predecessor to be:" + x);

		long xKey = pcm.getArgument2(); // change to new random key of argument 
		if (x >=0 && ((xKey > myAddrKey && xKey < successorKey)|| 
				(isLargestNode() && (xKey > myAddrKey || xKey < this.successorKey))
		/* if I am the last node in the ring and xKey is smaller my successorKey or larger than myAddrKey*/)){
			successor = x;
			successorKey = xKey;
		}
		PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey, 
				PennChordMessage.MessageType.NOTIFY, myAddr, myAddrKey,
				null, null);
		byte[] packedChordMessage = pack(chordMessage);
		Packet predecessorPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedChordMessage);
		sendPacket(successor, predecessorPacket);
	}

	/**
	 * Called when receiving a PennChordMessage.messageType.NOTIFY message
	 * to update the predecessor
	 * @param pcm
	 */
	private void receiveNotify(PennChordMessage pcm){
		int x = (int)pcm.getArgument1();//address of n
		long xKey = pcm.getArgument2(); 
		printDebug("Receive notify: my predecessor is: "+ x);
		if (predecessor == -1 || (xKey > predecessorKey && xKey< myAddrKey) ||
				(this.predecessorKey > this.myAddrKey && (xKey > predecessorKey || xKey < this.successorKey))
		/* if I am the last node in the ring and xKey is smaller my successorKey or larger than myAddrKey*/){
			if (newToRing){
				PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey, 
						PennChordMessage.MessageType.REQUEST_DATA, predecessorKey, 0,
						null, null);
				byte[] packedChordMessage = pack(chordMessage);
				Packet predecessorPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
						baseNode.assignSequenceNumber(), packedChordMessage);
				sendPacket(successor, predecessorPacket);
				newToRing = false;
			}
			predecessor = x;
			predecessorKey = xKey;
		}
	}

	/**
	 * When receives data request from a newly joining node
	 * @param pcm The REQUEST_DATA message from the new node
	 */
	private void receiveDataRequest(PennChordMessage pcm){
		Iterator<String> keyData = dataTable.keySet().iterator();
		int src = pcm.getSrc();
		String keyToSend = null;
		ArrayList<String> dataToSend = new ArrayList<String>();
		while(keyData.hasNext()){
			String keyValue = keyData.next();
			long keyHash = hashCode(keyValue);
			if ((keyHash <= pcm.getSrcAddrKey() && keyHash > pcm.getArgument1())/*normal case*/
					||(pcm.getSrcAddrKey() < pcm.getArgument1() /*predecessor is the smallest*/
							&& (keyHash <= pcm.getSrcAddrKey() || keyHash > pcm.getArgument1()))){
				keyToSend = keyValue;
				dataToSend = dataTable.get(keyValue);

				pcm = new PennChordMessage(myAddr, myAddrKey, 
						PennChordMessage.MessageType.TRANSFER_DATA, 
						0, 0, keyToSend, dataToSend);
				byte[] packedPcm = pack(pcm);
				Packet transferPacket = new Packet(src, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
						baseNode.assignSequenceNumber(), packedPcm);
				sendPacket(src, transferPacket);
			}
		}
	}


	public void leave()
	{
		transferData();
		notifySuccessor();
		notifyPredecessor();
		initializePennChordNode();
	}

	private void notifyPredecessor() {
		PennChordMessage pcm = new PennChordMessage(myAddr, myAddrKey,
				PennChordMessage.MessageType.CHANGE_SUCCESSOR, 
				successor, successorKey, null, null);
		byte[] packedPcm = pack(pcm);
		Packet transferPacket = new Packet(predecessor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedPcm);
		printDebug("Notifying predecessor "+predecessor+": your successor should be:" + successor);
		sendPacket(predecessor, transferPacket);
	}

	private void notifySuccessor() {
		PennChordMessage pcm = new PennChordMessage(myAddr, myAddrKey, 
				PennChordMessage.MessageType.CHANGE_PREDECESSOR, 
				predecessor, predecessorKey, null, null);
		byte[] packedPcm = pack(pcm);
		Packet transferPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedPcm);
		printDebug("Notifying successor: "+successor+" your predecessor should be:" + predecessor);
		sendPacket(successor, transferPacket);
	}

	private void transferData() {
		if (dataTable.isEmpty()) 
			return;
		Iterator<String> data = dataTable.keySet().iterator();
		String transferKey = null;
		ArrayList<String>transferValues = new ArrayList<String>();
		while(data.hasNext()){
			transferKey = data.next();
			if (transferKey == null){
				System.out.print("-----Null------ key found");
				continue;
			}
			transferValues = dataTable.get(transferKey);
			PennChordMessage pcm = new PennChordMessage(myAddr, myAddrKey, 
					PennChordMessage.MessageType.TRANSFER_DATA, 
					0, 0, transferKey, transferValues);
			byte[] packedPcm = pack(pcm);
			Packet transferPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
					baseNode.assignSequenceNumber(), packedPcm);
			sendPacket(successor, transferPacket);
		}
	}

	private void receiveTransferData(PennChordMessage pcm){
		int src = pcm.getSrc();
		String key = pcm.getKey();
		ArrayList<String> values = pcm.getValue();
		if (key == null){
			baseNode.logOutput("Got null key from src:"+src);
			return;
		}
		this.dataTable.put(key, values);
		baseNode.logOutput("Receive data from node "+src +":");
		printKeyValuePairs(key, values);
	}

	/**
	 * Called by command "ringstate" to make all nodes on
	 * the ring to print there ring state in a clockwise order
	 */
	private void outputRingstate() {
		// TODO Auto-generated method stub
		printRingstate();
		PennChordMessage pcm = new PennChordMessage(myAddr, myAddrKey, 
				PennChordMessage.MessageType.RINGSTATE_OUTPUT, 
				0, 0, null, null);
		byte[] packedPcm = pack(pcm);
		Packet ringstatePacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedPcm);
		sendPacket(successor, ringstatePacket);
	}

	private void printRingstate() {
		baseNode.logOutput("RingState<"+myAddr+", " + myAddrKey + ">: " +
				"Pred<"+ predecessor + ", " + predecessorKey + ">, "+ 
				"Succ<"+ successor+", "+successorKey+">" );
	}

	private void receiveRingstate(PennChordMessage pcm){
		if (pcm.getSrc() == myAddr)
			return;
		printRingstate();
		byte[] packedPcm = pack(pcm);
		Packet ringstatePacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedPcm);
		sendPacket(successor, ringstatePacket);
	}

	private void requestDebug(boolean debugOn, int src, long srcKey) {
		PennChordMessage pcm;
		pcm = new PennChordMessage(src, srcKey, 
				PennChordMessage.MessageType.REQUEST_DEBUG, 
				0, 0, null, null);
		if (debugOn) pcm.setDebugOn(true);
		byte[] packedPcm = pack(pcm);
		Packet debugRequestPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedPcm);
		sendPacket(successor, debugRequestPacket);
	}

	private void requestDebug(boolean debugOn){
		requestDebug(debugOn, myAddr, myAddrKey);
	}

	private void receiveRequestDebug(PennChordMessage pcm){
		if (pcm.getSrc() == myAddr) return;
		if(pcm.isDebugOn()){
			debugOn = true;
			requestDebug(true, pcm.getSrc(), pcm.getSrcAddrKey());
		}
		else{
			debugOn = false;
			requestDebug(false, pcm.getSrc(), pcm.getSrcAddrKey());
		}

	}
	private void requestLookupHops(int lookupCount, int hopCount, int src, long srcKey) {
		PennChordMessage pcm;
		pcm = new PennChordMessage(src, srcKey, 
				PennChordMessage.MessageType.REQUEST_LOOKUP_HOPS, 
				lookupCount, hopCount, null, null);
		byte[] packedPcm = pack(pcm);
		Packet debugRequestPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedPcm);
		sendPacket(successor, debugRequestPacket);
	}
	private void requestLookupHops(){
		requestLookupHops(lookupCount, hopCount, myAddr, myAddrKey); 
	}

	private void receiveLookupHops(PennChordMessage pcm){
		if (pcm.getSrc() == myAddr){
			outputLookupHops(pcm);
		}
		else{
			requestLookupHops((int)(pcm.getArgument1()+lookupCount),
					(int)(pcm.getArgument2()+hopCount), pcm.getSrc(), pcm.getSrcAddrKey());
		}
	}


	private void outputLookupHops(PennChordMessage pcm) {
		baseNode.logOutput("Average hop counts per lookup is: " +(double)pcm.getArgument2()/(double)pcm.getArgument1());
	}

	private boolean isOnlyNode(){
		return (this.successor == this.predecessor && this.myAddr == this.successor);
	}
	private boolean isSmallestNode(){
		return (this.predecessorKey > this.myAddrKey);
	}
	private boolean isLargestNode(){
		if (this.successorKey < 0) return false;
		return (this.successorKey < this.myAddrKey);
	}
	/**
	 * Process the command from scripts or console, called by the onCommand function of the baseNode
	 * @param command The command passed in
	 * @return true if the command is successfully parsed, false otherwise
	 */
	public boolean onCommand(String command)
	{
		command = command.trim();
		int index = command.indexOf(" ");
		if(command.indexOf("join") > -1)
		{
			if (index == -1)
			{
				return false;
			}
			try
			{
				int entryNodeAddr = Integer.parseInt(command.substring(index+1,command.length()));
				if (isRoutingTableComplete()){
					join(entryNodeAddr);
				}
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		else if (command.startsWith("lookupdebug")){
			if (index == -1)
			{
				return false;
			}
			String key = command.substring(index+1,command.length());
			if (isRoutingTableComplete()){
				lookupDebugOn = true;
				printDebug("%%lookupDebugon is set to true");
				lookup(key);
			}
			return true;
		}
		else if(command.startsWith("lookup"))
		{
			if (index == -1)
			{
				return false;
			}

			String key = command.substring(index+1,command.length());
			if (isRoutingTableComplete()){
				lookup(key);
			}
			return true;
		}
		else if(command.startsWith("store"))
		{
			if (index == -1)
			{
				return false;
			}
			String keyValuePair = command.substring(index+1,command.length());
			int index2 = keyValuePair.indexOf(" ");
			String key = keyValuePair.substring(0,index2);
			String value = keyValuePair.substring(index2+1,keyValuePair.length());
			if (isRoutingTableComplete()){
				store(key,value);
			}
			return true;
		}
		else if (command.startsWith("leave"))
		{
			if (isRoutingTableComplete()){
				leave();
			}
			return true;
		}

		else if (command.equalsIgnoreCase("debug on")){
			debugOn = true;
			requestDebug(true);
			return true;
		}
		else if (command.equalsIgnoreCase("debug off")){
			debugOn = false;
			requestDebug(false);
			return true;
		}
		else if (command.equalsIgnoreCase("ringstate")){
			outputRingstate();
			return true;
		}
		else if (command.equalsIgnoreCase("print data table")){
			printDataTable();
			return true;
		}
		else if (command.equalsIgnoreCase("get average hopCount")){
			requestLookupHops();
			return true;
		}
		return false;
	}





	/**
	 * Checks if the routing table is complete for the node
	 * if not, output the warning
	 * @return <code>true<code> if the routing table is complete
	 */
	private boolean isRoutingTableComplete() {
		if (!baseNode.isLinkStateReady()){
			baseNode.logOutput("Routing table construction is not completed yet!");
			return false;
		}
		return true;
	}

	/**
	 * Utility function to send a packet to a specific fishnet address
	 * With your code of Project 1, this function should work automatically.
	 * @param packet the packet to send, the destination is specified within the packet
	 */
	private void sendPacket(int dest, Packet packet)
	{
		//If you have compile error, please change the access modifier of
		//the receivePacket() of Node to public
		baseNode.receivePacket(dest, packet);
	}

	/**
	 * Convert key to its 32 bit hash value
	 * @param
	 * @return
	 */
	private static long hashCode(String key){
		//convert string to long integer
		long primeNum = 4294967291L;
		long encodedKey = 0;
		long tmpKey;
		for(int i=0; i<key.length(); i++)
		{
			tmpKey = key.charAt(i);
			for(int j=0; j<i; j++)
			{
				tmpKey = tmpKey * 10;
			}
			encodedKey = encodedKey + tmpKey;
		}
		encodedKey = Math.abs(encodedKey) % primeNum ;

		return encodedKey;
	}

	private void printDebug(String debugInfo){
		if (debugOn) baseNode.logOutput(debugInfo);
	}
	/**
	 * Generate random number in [0, 2^32) for new joining node
	 * @param argument
	 * @return the myAddrKey
	 */
	private long genKeyForNode()
	{
		long rndNum = Integer.MAX_VALUE;
		Random objRnd = new Random();
		long tmpNum = objRnd.nextInt();
		if(tmpNum < 0)
		{
			rndNum = rndNum - tmpNum;
		}else
		{
			rndNum = tmpNum;
		}
		return rndNum;
	}
}

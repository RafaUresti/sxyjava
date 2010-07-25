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
	private TreeMap<Long, ArrayList<String>> hashKeyTable;//Mapping hashKey to Keys
	private HashMap<String, ArrayList<String>> dataTable; //Mapping Key to Values
	private long myAddrKey;
	private final int STABILIZE_TIME = 15000;
	public PennChordNode(Node baseNode,int addr)
	{
		this.baseNode = baseNode;
		this.myAddr = addr;
		initializePennChordNode();
		baseNode.addTimer(baseNode.PingTimeout, "stabilize");//TODO check if right
	}

	private void initializePennChordNode() {
		this.predecessor = -1;
		this.predecessorKey = -1;
		this.successor = -1;
		this.successorKey = -1;
		this.myAddrKey = -1;
		this.hashKeyTable = new TreeMap<Long, ArrayList<String>>();
		this.dataTable = new HashMap<String, ArrayList<String>>();
	}

	/**
	 * Called by underlying Fishnet node when a packet with an unknown protocol is received.
	 * @param from The direct neighbor who send/forward the packet
	 * @param packet The packet to process
	 */
	public  void processPacket(int from, Packet packet)
	{
		// process incoming packets here.
		if (packet.getProtocol() != Protocol.CHORD_PKT){
			baseNode.logOutput("unrecognized protocol type");
			return;
		}
		byte[] payloadChord = packet.getPayload();
		PennChordMessage pennChordMsg = unpack(payloadChord);
		if (PennChordMessage.MessageType.JOIN == pennChordMsg.getMessageType()){
			receiveJoin(packet);
		}
		if (PennChordMessage.MessageType.JOIN_REPLY == pennChordMsg.getMessageType()){
			receiveJoinReply(pennChordMsg);
		}
		if (PennChordMessage.MessageType.NOTIFY == pennChordMsg.getMessageType()){
			receiveNotify(pennChordMsg);
		}
		if (PennChordMessage.MessageType.STABILIZE == (pennChordMsg.getMessageType())){
			receiveStabilize(pennChordMsg);
		}
		if (PennChordMessage.MessageType.STABILIZE_REPLY == (pennChordMsg.getMessageType())){
			receiveStabilizeReply(pennChordMsg);
		}
		if (PennChordMessage.MessageType.STORE == (pennChordMsg.getMessageType())){
			receiveStore(packet);
		}
		if (PennChordMessage.MessageType.LOOKUP == (pennChordMsg.getMessageType())){
			receiveLookup(packet);
		}
		if (PennChordMessage.MessageType.LOOKUP_REPLY == (pennChordMsg.getMessageType())){
			receiveLookupReply(pennChordMsg);
		}

	}

	/**
	 * Send join message to the node with a specified fishnet address.
	 * If the entry node is current node, initiate a new ring.
	 * @param entryNodeAddr
	 */
	public void join(int entryNodeAddr)
	{	baseNode.logOutput("In join: entryNodeAddr: "+entryNodeAddr);
		
		long keyValue = genKeyForNode();
		this.myAddrKey = keyValue;
		if(entryNodeAddr == this.myAddr)
		{
			//create a new ring, e.g. 0 join 0
			 
			this.successorKey = this.myAddrKey;
			this.successor = this.myAddr;
			this.predecessorKey = -1;
			this.predecessor = -1;
			baseNode.logOutput("create ring");
		}else{
			baseNode.logOutput("sending a packet to: "+entryNodeAddr);       
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN, myAddr, this.myAddrKey,
					null, null);    // change the packet structure... added myAddrkey and for srcAddrKey and for argAddrkey
			baseNode.logOutput("created join message");
			byte[] packedChordMessage = pack(chordMessage);
			
			Packet joinPacket = new Packet(entryNodeAddr, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(entryNodeAddr, joinPacket);
			
		}
	}

	private void receiveJoin(Packet packet){
		PennChordMessage pcm = unpack(packet.getPayload());
		int src = pcm.getSrc();
		long srcKey = pcm.getSrcAddrKey(); // changed to new Random key of src
		
		baseNode.logOutput("In receiveJoin : srcKeysrcKey: "+srcKey);
		if (this.myAddrKey == this.successorKey){//Only one node in ring
			this.successor = src;
			this.successorKey = pcm.getSrcAddrKey(); // changed to new Random key of src
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN_REPLY, myAddr, this.myAddrKey,
					null, null);   // change the packet structure... added myAddrkey and for srcAddrKey and for argAddrkey
			byte[] packedChordMessage = pack(chordMessage);
			Packet joinReplyPacket = new Packet(src, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(src, joinReplyPacket);  
			
			baseNode.logOutput("Only one node in ring and hence accept the joining msg of Node: "+src);
			
		}else if (srcKey > this.myAddrKey && srcKey < this.successorKey){//between me and my successor
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN_REPLY, successor, this.successorKey,
					null, null);       // change the packet structure... added myAddrkey and for srcAddrKey and for argAddrkey
			byte[] packedChordMessage = pack(chordMessage);
			Packet joinReplyPacket = new Packet(src, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(src, joinReplyPacket);
		}else if (srcKey < this.myAddrKey){//before me, independent of others, pass onto my successor to go around the ring
			sendPacket(this.successor, packet);
		}
		else if (this.successorKey < this.myAddrKey && this.myAddrKey < srcKey){//I am the biggest myAddrKey of the ring, my successor is the smallest
			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.JOIN_REPLY, successor, this.successorKey,
					null, null);
			baseNode.logOutput("I am the biggest node and hence accepting the addres bigger than me");
			byte[] packedChordMessage = pack(chordMessage);
			Packet joinReplyPacket = new Packet(src, this.myAddr, Packet.MAX_TTL,
					Protocol.CHORD_PKT, baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(src, joinReplyPacket);
		}else if (srcKey > this.myAddrKey && srcKey > this.successorKey){//larger than me and successor
			sendPacket(this.successor, packet);
		}
	}
	private void receiveJoinReply(PennChordMessage pcm){
		this.successor = (int)pcm.getArgument();
		this.successorKey = pcm.getArgAddrKey(); // change to new random key of argument
		
		baseNode.logOutput("In receiveJoinReply: My "+this.myAddr+"successor is:"+successor);
	}

	/**
	 * Called by manager to store data
	 */
	public void store(String key,String value)
	{
		long lookupKey = hashCode(key);//FIXME
		baseNode.logOutput("hashCode(key) : "+lookupKey);
		baseNode.logOutput("current Node hash address: "+this.myAddrKey);
		ArrayList<String> valueList = new ArrayList<String>();
		//if the key should be stored in this node
		if ((lookupKey <= this.myAddrKey && lookupKey > this.predecessorKey) || 
				this.myAddrKey == this.successorKey /*if I am the only node in ring*/){
			if (dataTable.containsKey(lookupKey)) {
				valueList = dataTable.get(lookupKey);
				valueList.add(value);
				dataTable.put(key, valueList);
			}else {
				valueList.add(value);
				dataTable.put(key, valueList);
			}
			baseNode.logOutput(key + " has been store.");
			Iterator<String> itr = dataTable.keySet().iterator();
	    baseNode.logOutput("Output data table");
	    while(itr.hasNext()){
	      String nextKey = itr.next();
	      baseNode.logOutput(nextKey+", " + dataTable.get(nextKey));
	    }
		}else{
			//Send a STORE message to successor
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
		if ((lookupKey <= this.myAddrKey && lookupKey > this.predecessorKey) ||
				(lookupKey > this.predecessorKey && this.predecessorKey > this.myAddrKey /*if I am the smallest node*/)){
			if (dataTable.containsKey(lookupKey)) {
				existingValueList = dataTable.get(lookupKey);
				existingValueList.add(newValueList.get(0));
				dataTable.put(msgKey, existingValueList);
			}else {
				dataTable.put(msgKey, newValueList);
			}
		}else{//Send a look up message to successor
			sendPacket(successor, storePacket);
		}

	}
	public void lookup(String key)
	{
		//send lookup message to find the value corresponds to the key.
		//the result is printed when the response message is received(not in this function)
		long lookupKey = hashCode(key);
		//if the key should be stored in this node
		if (lookupKey > this.myAddrKey && this.myAddrKey < this.predecessorKey 
				&& lookupKey > this.predecessorKey){
			//the lookupKey is greater than all myAddrKeys and I am the smallest myAddrKey of the ring
			outputKeyValues(key);
			baseNode.logOutput("LookupRequest<" + this.myAddr + ", " + this.myAddrKey
					+ ">: RequestString<" + key + ", " + lookupKey + "), Found");
		}		
		else if (lookupKey <= this.myAddrKey && lookupKey > this.predecessorKey){
			//if I am not the smallest myAddrKey, and the key is within my range
			baseNode.logOutput("LookupRequest<" + this.myAddr + ", " + this.myAddrKey
					+ ">: RequestString<" + key + ", " + lookupKey + "), Found");
			outputKeyValues(key);
		}
		else{//Send a look up message to successor
			baseNode.logOutput("LookupRequest<" + this.myAddr + ", " + this.myAddrKey
					+ ">: RequestString<" + key + ", " + lookupKey + "), NextHop<" 
					+ this.successor + ", " + this.successorKey + ">");

			PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey,
					PennChordMessage.MessageType.LOOKUP, 0, 0,
					key, null);        // change the packet structure... added myAddrkey and for srcAddrKey and for argAddrkey
			byte[] packedChordMessage = pack(chordMessage);
			Packet lookupPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
					baseNode.assignSequenceNumber(), packedChordMessage);
			sendPacket(successor, lookupPacket);
		}
	}
	private void outputKeyValues(String key) {
		if (!dataTable.containsKey(key)){
			baseNode.logOutput(key + "is not maching found!");
			return;
		}
		else{
			ArrayList<String> values = dataTable.get(key);
			for (String value: values){
				baseNode.logOutput("<"+key+", "+value+">");
			}
		}
		Iterator<String> itr = dataTable.keySet().iterator();
		baseNode.logOutput("Output data table");
		while(itr.hasNext()){
		  String nextKey = itr.next();
		  baseNode.logOutput(nextKey+", " + dataTable.get(nextKey));
		}
	}

	private void receiveLookup(Packet packet){
		PennChordMessage pcm = unpack(packet.getPayload());
		String lookupString = pcm.getKey();
		int src = pcm.getSrc();
		long lookupKey = hashCode(lookupString);
		if (lookupKey > this.myAddrKey && this.myAddrKey < this.predecessorKey 
				&& lookupKey > this.predecessorKey){
			//the lookupKey is greater than all myAddrKeys and I am the smallest myAddrKey of the ring
			sendKeyValues(lookupString, src);
		}		
		else if (lookupKey <= this.myAddrKey && lookupKey > this.predecessorKey){
			//if I am not the smallest myAddrKey, and the key is within my range
			sendKeyValues(lookupString, src);
		}
		else{//Pass the packet onto successor
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
					PennChordMessage.MessageType.LOOKUP, 0, 0,
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
			baseNode.logOutput("No values found for "+pcm.getKey());
			return;
		}
		baseNode.logOutput("Values found for "+pcm.getKey()+" are:\n");
		for (String value: values){
			baseNode.logOutput(value+"\n");
		}
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

	private PennChordMessage unpack(byte[] payloadChord) {
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
	 * Called when receiving a PennChordMessage.messageType.STABILIZE message
	 * @param packet
	 */
	private void receiveStabilize(PennChordMessage pcm){
		int src = pcm.getSrc();

		PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey, 
				PennChordMessage.MessageType.STABILIZE_REPLY, predecessor, predecessorKey,
				null, null);    // change the packet structure... added myAddrkey and for srcAddrKey and for argAddrkey
		byte[] packedChordMessage = pack(chordMessage);
		Packet predecessorPacket = new Packet(src, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedChordMessage);
		sendPacket(successor, predecessorPacket);
	}

	/**
	 * Called when receiving a PennChordMessage.messageType.STABILIZE_REPLY message
	 * @param pcm
	 */
	private void receiveStabilizeReply(PennChordMessage pcm){
		int src = pcm.getSrc();
		int x = (int)pcm.getArgument();
		long xKey = pcm.getArgAddrKey(); // change to new random key of argument 
		if ((xKey > myAddrKey && x < successorKey)|| 
				this.successorKey < this.myAddrKey && (xKey > myAddrKey && x > successorKey)/* if I am the last node in the ring*/){
			successor = x;
			successorKey = xKey;
		}
		PennChordMessage chordMessage = new PennChordMessage(myAddr, myAddrKey, 
				PennChordMessage.MessageType.NOTIFY, myAddr, myAddrKey,
				null, null);
		byte[] packedChordMessage = pack(chordMessage);
		Packet predecessorPacket = new Packet(src, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedChordMessage);
		sendPacket(successor, predecessorPacket);
	}

	/**
	 * Called when receiving a PennChordMessage.messageType.NOTIFY message
	 * @param pcm
	 */
	private void receiveNotify(PennChordMessage pcm){
		int x = (int)pcm.getArgument();//address of n
		long xKey = pcm.getArgAddrKey(); // change to new random key of argument
		if (predecessor == -1 || (xKey > predecessorKey && xKey< myAddrKey) ||
				(this.predecessorKey > this.myAddrKey && xKey > predecessorKey) /* if I am the first node in the ring*/){
			predecessor = x;
			predecessorKey = xKey;
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
		sendPacket(predecessor, transferPacket);
	}

	private void notifySuccessor() {
		PennChordMessage pcm = new PennChordMessage(myAddr, myAddrKey, 
				PennChordMessage.MessageType.CHANGE_PREDECESSOR, 
				predecessor, predecessorKey, null, null);
		byte[] packedPcm = pack(pcm);
		Packet transferPacket = new Packet(successor, myAddr, Packet.MAX_TTL, Protocol.CHORD_PKT, 
				baseNode.assignSequenceNumber(), packedPcm);
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

	/**
	 * Process the command from scripts or console, called by the onCommand function of the baseNode
	 * @param command The command passed in
	 * @return true if the command is sucessfully parsed, false otherwise
	 */
	public boolean onCommand(String command)
	{
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
				baseNode.logOutput("before join");
				join(entryNodeAddr);
				baseNode.logOutput("after join");
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		else if(command.startsWith("lookup"))
		{
			if (index == -1)
			{
				return false;
			}

			String key = command.substring(index+1,command.length());

			lookup(key);

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
			store(key,value);
			return true;

		}
		else if (command.startsWith("leave"))
		{
			leave();
			return true;
		}

		return false;
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
		baseNode.send(dest, packet);
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

	/**
	 * Generate random number in [0, 2^32) for new joining node
	 * @param argument
	 * @return
	 */
	private long genKeyForNode()
	{
		long rndNum = Integer.MAX_VALUE;
		Random objRnd = new Random();
		long tmpNum = objRnd.nextInt();

		if(tmpNum < 0)
		{
			rndNum = rndNum - tmpNum;
			baseNode.logOutput("New key = " + String.valueOf(rndNum));
		}else
		{
			rndNum = tmpNum;
			baseNode.logOutput("New key = " + String.valueOf(rndNum));
		}
		return rndNum;
	}

	/**
	 * Convert node address to 32 bit myAddrKey
	 * @param argument
	 * @return
	 */
	private long hashCode(int argument){
		//TODO
		return hashCode(String.valueOf(argument));
	}

	//TODO fix when wrapping around 
	//        3 ---- 9 ---- 19
	//        |              |
	//        400---- 190 --- 90
	//now 500 want to join, 400 < 500 < 3?
	// fix in receiveStabilizeReply/store/receiveNotify ----  done by Joshi....
	//TODO add lookup debug command
	//TODO test the whole thing
	//TODO add addrKey to PennChordMessage constructor
	//change hashKey to myAddrKey
}

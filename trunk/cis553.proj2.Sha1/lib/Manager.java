import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * <pre>   
 * Abstract class defining generic routines for running network code under Fishnet
 * </pre>   
 */
public abstract class Manager { 
    
    private int pktsSent;
    private long start;
    protected SortedEventQueue sortedEvents;
    protected CommandsParser parser;   // parser for fishnet commands file
    protected BufferedReader reader;

    /**
     * Initialize Manager. 
     * @param time Starting time in microseconds
     */
    protected Manager(long time) {
	this.pktsSent = 0;
	this.start = time / 1000;
	this.sortedEvents = new SortedEventQueue();
	this.parser = null;
	this.reader = null;
    }

    /**
     * Starts the Manager. This runs in an infinite loop until network is stopped
     * Instantiates nodes and gets them running
     */
    public abstract void start();

    /**
     * Stops Fishnet. Normally this method should not return
     */
    public void stop() {
	System.err.println("Fishnet exiting after time: " + String.valueOf(this.now() - this.start) + " msec." +
			   "\nNumber of packets sent: " + String.valueOf(this.pktsSent));
	System.exit(0);			   
    }

    /**
     * Sets the fishnet file that commands should be read from
     * @param filename The name of the file that commands should be read from
     * @throws FileNotFoundException If the named filed does not exist, is a directory rather than a regular file, or
     *                               for some other reason cannot be opened for reading
     */
    public void setFishnetFile(String filename) throws FileNotFoundException {
	this.reader = new BufferedReader(new FileReader(filename));
    }

    /**
     * Send the pkt to the specified node
     * @param from The node that is sending the packet
     * @param to Int spefying the destination node
     * @param pkt The packet to be sent, serialized to a byte array
     * @return True if the packet was sent, false otherwise
     * @throws IllegalArgumentException If the arguments are invalid
     */
    public boolean sendPkt(int from, int to, byte[] pkt) throws IllegalArgumentException {
	if ( (pkt.length > Packet.MAX_PACKET_SIZE) || 
	     !Packet.validAddress(to)                  || 
	     !Packet.validAddress(from) ||
		 !Packet.unpack(pkt).isValidToSend()) {
		System.out.print("packet to string....................\n");
		if (!Packet.validAddress(to)){
			System.out.println("To address is:" + to);
			System.out.println(Packet.unpack(pkt).toString());
			Packet pck = Packet.unpack(pkt);
			PennChordMessage pcm = PennChordNode.unpack(pck.getPayload());
			
			System.out.println("MESSAGE TYPE IS:@@@@@@@@@@@@@@@@@@@@@"+pcm.getMessageType());
		}
	    throw new IllegalArgumentException("Either pkt is not valid, address is not valid, or TTL is not valid");
	}
	
	
	this.pktsSent++;
	return true;
    }

    /**
     * Adds a timer to be fired at time t
     * @param nodeAddr Addr of node that is registering this timer
     * @param t The time when the timer should fire. In milliseconds
     * @param callback The callback to be invoked when the timer fires
     */
    public void addTimerAt(int nodeAddr, long t, Callback callback) {
	if( (callback == null) || (t < this.now()) ) {	    
	    return;
	}
	
	this.sortedEvents.addEvent(new Event(t * 1000, callback));	
    }

    /**
     * Adds a timer to be fired at deltaT milliseconds in the future
     * @param nodeAddr Addr of node that is registering this timer
     * @param deltaT The time interval after which the timer should fire. In milliseconds
     * @param callback The callback to be invoked when the timer fires
     */
    public void addTimer(int nodeAddr, long deltaT, Callback callback) {
	this.addTimerAt(nodeAddr, this.now() + deltaT, callback);
    }

    /**
     * Retrieve current time in milliseconds
     * @return Current time in milliseconds
     */
    public abstract long now();

    /**
     * Sends the msg to the the specified node 
     * @param nodeAddr Address of the node to whom the message should be sent
     * @param msg The msg to send to the node
     * @return True if msg sent, false if address is not valid
     */
    public abstract boolean sendNodeMsg(int nodeAddr, String msg);

    /**
     * Sets the amount to scale real time by. Is only valid for simiulator
     * @param timescale The amount to scale real time by
     */
    public void setTimescale(double timescale) {
	// do nothing. Simulator will override this function.
	// Defined here since is not valid for emulator
    }

    protected void setParser(CommandsParser parser) {
	this.parser = parser;
    }


    /**
     * Reads one line of the fish commands file and returns how long to delay further parsing if time command was found
     * Call this function after now > 0
     * @param deferTill Should be the value returned by this function. Can be initialized to 0 for first invocation
     * @return How long (in microseconds) to defer further parsing till. -1 if reached eof. 0 if no delay
     */
    protected long readFishFile(long deferTill) {
	String line;
	if(this.reader != null && deferTill <= (this.now() * 1000)) {	
	    try {
		line = this.reader.readLine();
		if(line == null) {
		    this.reader = null;
		}else {
		    return Math.max(this.parser.parseLine(line, this.now() * 1000), 0);		    		    
		}
	    }catch(IOException e) {
		System.err.println("IOException occured while trying to read fish file in Simulator!! Error: " + e);
		this.reader = null;
	    }	    
	}
	if(this.reader == null) {
	    return -1;
	}
	return deferTill;
    }        

    protected void addEvent(long timeToOccur, String methodName, Object obj, String[] paramTypes, Object[] params) {
	if(timeToOccur < 0) {
	    return;
	}
	try {
	    Method method = Callback.getMethod(methodName, obj, paramTypes);
	    Callback cb = new Callback(method, obj, params);
	    this.sortedEvents.addEvent(new Event(timeToOccur, cb));
	}catch(Exception e) {
	    System.err.println("Failed to add event in Manager. Method Name: " + methodName + " Object: " + obj + 
			       "\nException: " + e);
	}
    }

}

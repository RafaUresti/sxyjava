import java.io.Serializable;
import java.util.ArrayList;

public class PennChordMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2686959871534800983L;

	/**
	 * <pre>
	 * FIND_SUCCESSOR: Used for updating finger table
	 * SUCCESSOR_REPLY: Used for replying a finger table update FIND_SUCCESSOR message
	 * JOIN: Used for adding a new node to the chord ring
	 * JOIN_REPLY:Reply packet for JOIN message when successor of new node is found
	 * TRANSFER_DATA: Used to transfer all <key,values> to successor when leaving
	 * CHANGE_SUCCESSOR: Inform predecessor of its new successor to be the successor of this 
	 * 					pennChord node before leaving the Chord ring. 
	 * CHANGE_PREDECESSOR: Inform successor of its new predecessor to be the predecessor of this 
	 * 					pennChord node before leaving the Chord ring.
	 * NOTIFY: Notify message used in stabilize to update predecessor of the target node
	 * STABILIZE: Find the predecessor of successor when stabilizing
	 * STABILIZE_REPLY: Reply the predecessor information
	 * LOOKUP: Inquire about values associated with a key
	 * LOOKUP_REPLY: Inform the <key,values> pair when found or flag.NO_MATCHING_KEY if not existing
	 * STORE: Used when need to store <key, value> pair into the Chord ring.
	 * </pre>
	 */
	public static enum MessageType {FIND_SUCCESSOR, SUCCESSOR_REPLY, JOIN, JOIN_REPLY, TRANSFER_DATA, CHANGE_SUCCESSOR, CHANGE_PREDECESSOR,
		NOTIFY, STABILIZE, STABILIZE_REPLY,LOOKUP, LOOKUP_REPLY, STORE}
	private int src;
	private long srcAddrKey; 
	private MessageType messageType;
	private long argument;
	private long argAddrKey;
	private String key;	
	private ArrayList<String> value;
	
	public PennChordMessage(int src, long srcAddrKey, MessageType messageType, 
	    long argument, long argAddrKey, String key, ArrayList<String> value){
		this.src = src;
		this.srcAddrKey = srcAddrKey;
		this.messageType = messageType;
		this.argument = argument;
		this.argAddrKey = argAddrKey;
		this.key = key;
		this.value = value;
	}
	
	public int getSrc() {
		return src;
	}

	public void setSrc(int src) {
		this.src = src;
	}
	
	public long getSrcAddrKey() {
    return srcAddrKey;
  }

  public void setSrcAddrKey(int srcAddrKey) {
    this.srcAddrKey = srcAddrKey;
  }

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType protocol) {
		this.messageType = protocol;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ArrayList<String> getValue() {
		return value;
	}

	public void setValue(ArrayList<String> value) {
		this.value = value;
	}

	public void addValue(String value){
		this.value.add(value);
	}
	
	public long getArgument() {
		return argument;
	}
	
	public void setArgument(long argument){
		this.argument = argument;
	}
	
	public long getArgAddrKey() {
    return argAddrKey;
  }
  
  public void setAddrKey(long argAddrKey){
    this.argAddrKey = argAddrKey;
  }

}

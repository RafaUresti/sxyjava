import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class PennChordMessage implements Serializable{
  /**
   * 
   */
  private static final long serialVersionUID = 2686959871534800983L;

  /**
   * <pre>
   * JOIN: Used for adding a new node to the chord ring. 
   *    Argument1: myAddr; Argument2: myAddrKey;
   * JOIN_REPLY:Reply packet for JOIN message when successor of new node is found. 
   *    Argument1 is successor and argument2 is successorKey. 
   *    Both are negative if duplication key found.
   * TRANSFER_DATA: Used to transfer all <key,values> to successor when leaving
   * CHANGE_SUCCESSOR: Inform predecessor of its new successor to be the successor of this 
   *    pennChord node before leaving the Chord ring. 
   *    Argument1: successor. Argument2: successorKey
   * CHANGE_PREDECESSOR: Inform successor of its new predecessor to be the predecessor of this 
   *    pennChord node before leaving the Chord ring. 
   *    Argument1: predecessor. Argument2: predecessorKey
   * NOTIFY: Notify message used in stabilize to update predecessor of the target node. 
   *    Argument1: myAddr; Argument2: myAddrKey;
   * STABILIZE: Find the predecessor of successor when stabilizing. No arguments needed.
   * STABILIZE_REPLY: Reply the predecessor information. 
   *    Argument1: predecessor, predecessorKey.
   * LOOKUP: Inquire about values associated with a key, which is stored in the key field.
   * LOOKUP_REPLY: Inform the <key,values> pair when found or flag.NO_MATCHING_KEY if not existing
   * STORE: Used when need to store <key, value> pair into the Chord ring. Stored in the key & value fields
   * REQUEST_DATA: When new node joins, send this message to successor 
   *    to request redistribution of data, with keys in the range (predecessorKey, myAddrKey]. 
   *    Argument1: predecessorKey
   * RINGSTATE_OUTPUT: Sent to successor to request printing of current 
   *    ring state for each node. No arguments
   * REQUEST_DEBUG: Send out request to pass around the ring to turn on 
   *    debug mode for each node. No arguments
   * REQUEST_LOOKUP_HOPS: Send out packet to pass around the ring to collect 
   *    the lookupCount and hopCount of all the lookups. 
   *    Argument1: the sum of all the lookupCounts from the originator; 
   *    Argument2: sum of hopCounts
   * </pre>
   */
  public static enum MessageType {JOIN, JOIN_REPLY, TRANSFER_DATA, CHANGE_SUCCESSOR, CHANGE_PREDECESSOR,
    NOTIFY, STABILIZE, STABILIZE_REPLY,LOOKUP, LOOKUP_REPLY, STORE, REQUEST_DATA, RINGSTATE_OUTPUT, 
    REQUEST_DEBUG, REQUEST_LOOKUP_HOPS, FIX_FINGERS, FIX_FINGERS_REPLY, TEST}
  private int src;
  private long srcAddrKey; 
  private MessageType messageType;
  private long argument1;
  private long argument2;
  private String key; 
  private ArrayList<String> value;
  public long[] ftIdxValue;
  public int[] ftAddr;
  public long[] ftAddrKey;
  private boolean debugOn;

  public PennChordMessage(int src, long srcAddrKey, MessageType messageType, 
      long argument1, long argument2, String key, ArrayList<String> value, 
      int[] ftAddr, long[] ftAddrKey){
    this.src = src;
    this.srcAddrKey = srcAddrKey;
    this.messageType = messageType;
    this.argument1 = argument1;
    this.argument2 = argument2;
    this.key = key;
    this.value = value;
    this.ftIdxValue = ftIdxValue;
    this.ftAddr = ftAddr;
    this.ftAddrKey = ftAddrKey;
    debugOn = false;
  }
  public boolean isDebugOn() {
    return debugOn;
  }

  public void setDebugOn(boolean debugOn) {
    this.debugOn = debugOn;
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
    
  public long getArgument1() {
    return argument1;
  }
  
  public void setArgument1(long argument1){
    this.argument1 = argument1;
  }
  
  public long getArgument2() {
    return argument2;
  }
  
  public void setArgument2(long argument2){
    this.argument2 = argument2;
  }

}

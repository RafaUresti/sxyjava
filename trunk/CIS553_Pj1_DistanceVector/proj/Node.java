import java.util.ArrayList;
import java.util.Iterator;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

/**
 * <pre>
 * Node -- Class defining the data structures and code for the
 * protocol stack for a node participating in the fishnet
 * 
 * This code is under student control; we provide a baseline
 * &quot;hello world&quot; example just to show how it interfaces with the FishNet classes.
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
public class Node
{
  private final long PingTimeout = 10000; // Timeout pings in 10 seconds
  private int MaxHops = 16; //use MaxHops to prevent count-to-infinity
  private HashMap<String, String> sequenceNumbers;
  private Manager manager;
  private int addr;
  private ArrayList pings; // To store PingRequests.
  private int currentSequence;
  // We need two lists as we poll out neighbors only every 10 seconds
  // Hence if anyone drops out / is added in between we will not have enough
  // data
  // This just seems useful.
  private HashSet currentneighborList;
  private HashSet newneighborList;
  private String polling = "polling neighbors";
  
  private int[] tblDistance = new int[100];
  private int[] tblNextHop = new int[100];

  /**
   * Create a new node
   * 
   * @param manager
   *          The manager that is managing Fishnet
   * @param addr
   *          The address of this node
   */
  public Node(Manager manager, int addr)
  {
    this.manager = manager;
    this.addr = addr;
    this.pings = new ArrayList();
    this.sequenceNumbers = new HashMap<String, String>();
    int currentSequence = 0;
    currentneighborList = new HashSet();
    newneighborList = new HashSet();
  }

  /**
   * Called by the manager to start this node up.
   */
  public void start()
  {
    logOutput("started");
    this.initTablesForDistanceVector();
    this.addTimer(PingTimeout, "pingTimedOut");
    //neighborCheck is the neighbor discovery function
    this.addTimer(PingTimeout, "neighborCheck");
    //this.addTimer(ExchangeDVTimeout, "exchangeDistanceVector");
  }

  /**
   * Called by the manager when a packet has arrived for this node
   * 
   * @param from
   *          The address of the node that has sent this packet
   * @param msg
   *          The serialized form of the packet.
   */
  public void onReceive(Integer from, byte[] msg)
  {
    Packet packet = Packet.unpack(msg);
    // logOutput("received packet from " + from);
    if (packet == null)
    {
      logError("Unable to unpack message: " + Utility.byteArrayToString(msg)
          + " Received from " + from);
      return;
    }

    this.receivePacket(from.intValue(), packet);
  }

  /**
   * Called by the manager when there is a command for this node from the user.
   * Command can be input either from keyboard or file. For now sending messages
   * as ping to a neighbor.
   * 
   * @param command
   *          The command for this node
   */
  public void onCommand(String command)
  {
    if (this.matchDumpCommand(command))
    {// MatchDumpCommand dumps the list of neighbors
      return;
    } else if (this.matchPingCommand(command))
    {
      return;
    }

    logError("Unrecognized command: " + command);
  }

  public boolean matchDumpCommand(String command)
  {
    // This prints the set of neighbors that a given node has.
    // We store two sets. The currentneighborList refers to the set of all
    // neighbors we know existed in the last cycle of polling. The new
    // neighborList is the set of neighbors we have discovered so far in
    // this cycle of polling. This is dynamic and will be updated constantly
    // until the next cycle. At the next cycle the currentneighborList
    // becomes the new neighborList and the newneighborList is reset.
    // This is done to keep track of which nodes disappear.

    command = command.toLowerCase();
    command = command.trim();
    if (command.equals("dump neighbors"))
    {
      // First print neighborlist
      System.out.println("neighbors of " + this.addr
          + " discovered in last poll:");
      Iterator itr = this.currentneighborList.iterator();
      while (itr.hasNext())
      {
        System.out.print(itr.next() + ",");
      }
      System.out.println();

      System.out.println("neighbors of " + this.addr
          + " discovered in current poll");
      itr = this.newneighborList.iterator();
      while (itr.hasNext())
      {
        System.out.print(itr.next() + ",");
      }
      System.out.println();

      return true;
    }else if (command.equals("dump distance vector"))
    {
      System.out.println("Distance vector of " + this.addr + ":");
      System.out.println("(Destination, Distance, NextHop)");
      for(int i=0; i<tblDistance.length; i++)
      {
        if(tblDistance[i] < MaxHops)
        {
        	System.out.println("(" + i + ", " + tblDistance[i] + ", " 
        			+ tblNextHop[i] + ")");
        }
      }
      System.out.println();
      return true;
    }
    return false;
  }

  /**
   * Callback method given to manager to invoke when a timer fires.
   */
  public void pingTimedOut()
  {
    Iterator iter = this.pings.iterator();
    while (iter.hasNext())
    {
      PingRequest pingRequest = (PingRequest) iter.next();
      if ((pingRequest.getTimeSent() + PingTimeout) < this.manager.now())
      {
        try
        {
          logOutput("Timing out ping: " + pingRequest);
          iter.remove();
        } catch (Exception e)
        {
          logError("Exception occured while trying to remove an element from ArrayList pings.  Exception: "
              + e);
          return;
        }
      }
    }
    this.addTimer(PingTimeout, "pingTimedOut");
  }

  private boolean matchPingCommand(String command)
  {
    int index = command.indexOf(" ");
    if (index == -1)
    {
      return false;
    }
    try
    {
      int destAddr = Integer.parseInt(command.substring(0, index));
      String message = command.substring(index + 1);
      Packet packet = new Packet(destAddr, this.addr, Packet.MAX_TTL,
          Protocol.PING_PKT, this.currentSequence, Utility
              .stringToByteArray(message));
      this.currentSequence++;
      this.send(Packet.BROADCAST_ADDRESS, packet);
      this.pings.add(new PingRequest(destAddr, Utility
          .stringToByteArray(message), this.manager.now()));
      return true;
    } catch (Exception e)
    {
      logError("Exception: " + e);
    }

    return false;
  }

  private void receivePacket(int from, Packet packet)
  {

    // Have we seen this packet before?
    // We check source and sequence number in our list
    int thisSource = packet.getSrc();
    int thisSeq = packet.getSeq();
    int lastSeq = -1;
    try
    {
      lastSeq = Integer.parseInt(this.sequenceNumbers.get(thisSource + ""));

    } catch (NumberFormatException nfe)
    {
      // If we get a null or number format exception - we havent seen it
      // This catch is just to have a safe way of extracting seqnum

    }

    // If we have seen it before we ignore it (return from function)
    // Else record that we have seen this packet and process

    if (lastSeq < thisSeq)
    {
      // then the packet is valid and we check if it is meant for us
      // First we update our records to indicate the new latest seqnum
      this.sequenceNumbers.put(thisSource + "", thisSeq + "");
      // System.out.println("Source:"+thisSource+":"+" sequence"+thisSeq);
      // Is this packet meant for us?
      // If it is not -->decrease ttl and flood again
      // else it is meantnt for us and must be processed
      int destination = packet.getDest();
      if (destination == this.addr || destination == Packet.BROADCAST_ADDRESS)
      {
        // it is meant for us and must be processed
        switch (packet.getProtocol())
        {

        case Protocol.PING_PKT:
          this.receivePing(packet);
          break;

        case Protocol.PING_REPLY_PKT:
          this.receivePingReply(packet);
          break;
                  	
        case Protocol.DV_INFO_PKT:
	      	this.receiveDistanceVector(packet);
	      	break;
	      	
        default:
          logError("Packet with unknown protocol received. Protocol: "
              + packet.getProtocol());
        }
      }
      else
      // general packet to flood
      {
        // it must be processed by everyone else
        // decrease time to live and send again
        packet.setTTL(packet.getTTL() - 1);
        
        // if ttl is 0 or there is no link to dest, it shouldnt be sent
        if (packet.getTTL() > 0)
        { 
          if(tblNextHop[packet.getDest()] != -1)
          {
            this.send(tblNextHop[packet.getDest()], packet);
          }else
          {
            this.send(Packet.BROADCAST_ADDRESS, packet);
          }
        }
      }
    } else
    {
      // the packet has invalid seq number or we have seen it. we ignore it
      // If you like we can, log the output, but it clutters up the screen
      // this.logOutput("Invalid sequence number")
      // Uncomment this ^^^ if you like.

      return;
    }

  }

  private void receivePing(Packet packet)
  {
    //logOutput("Received Ping from " + packet.getSrc() + " with message: "
    //    + Utility.byteArrayToString(packet.getPayload()));

    try
    {
      if ((packet.getDest() == Packet.BROADCAST_ADDRESS)
          && (packet.getTTL() == 1))
      {
        // The ping is to discover neighbors as all other broadcast pings
        // have maximimum TTL and are never relayed again (ie their TTL
        // can never become 1, since they are replied to immediately)

        newneighborList.add(packet.getSrc() + "");
        
        //update DV table
        if(tblDistance[packet.getSrc()] != 1)
        {
	        tblDistance[packet.getSrc()] = 1;
	        tblNextHop[packet.getSrc()] = packet.getSrc();
	        exchangeDistanceVector();
        }
        
        Packet reply2 = new Packet(packet.getDest(), this.addr, 1,
            Protocol.PING_REPLY_PKT, this.currentSequence, packet.getPayload());
        this.currentSequence++;
        this.send(Packet.BROADCAST_ADDRESS, reply2);
        return;
      }else if(packet.getDest() == Packet.BROADCAST_ADDRESS)
      {
        return;
      }
      /*
       * We weren't sure whether we were supposed to implement flooding
       * broadcast packets to all nodes, but if we must, then uncomment this
       * code. The problem is that eventually the ttl might become 1, and this
       * would interfere with neighbor discover else
       * if(packet.getDest()==Packet.BROADCAST_ADDRESS) { Packet reply2= new
       * Packet
       * (packet.getDest(),this.addr,packet.getTTL()-1,Protocol.PING_REPLY_PKT
       * ,this.currentSequence,packet.getPayload()); this.currentSequence++;
       * this.send(packet.getDest(),reply2); return; }
       */

      Packet reply = new Packet(packet.getSrc(), this.addr, Packet.MAX_TTL,
          Protocol.PING_REPLY_PKT, this.currentSequence, packet.getPayload());
      this.currentSequence++;
      this.send(Packet.BROADCAST_ADDRESS, reply);
    } catch (IllegalArgumentException e)
    {
      logError("Exception while trying to send a Ping Reply. Exception: " + e);
    }
  }

  // Check that ping reply matches what was sent
  private void receivePingReply(Packet packet)
  {

    if ((packet.getDest() == Packet.BROADCAST_ADDRESS)
        && (packet.getTTL() == 1))
    {

      this.newneighborList.add(packet.getSrc() + "");
      //update DV table
      if(tblDistance[packet.getSrc()] != 1)
      {
        tblDistance[packet.getSrc()] = 1;
        tblNextHop[packet.getSrc()] = packet.getSrc();
        exchangeDistanceVector();
      }
      return;
    }

    Iterator iter = this.pings.iterator();
    String payload = Utility.byteArrayToString(packet.getPayload());
    while (iter.hasNext())
    {
      PingRequest pingRequest = (PingRequest) iter.next();
      if ((pingRequest.getDestAddr() == packet.getSrc())
          && (Utility.byteArrayToString(pingRequest.getMsg()).equals(payload)))
      {

        //logOutput("Got Ping Reply from " + packet.getSrc() + ": " + payload);

        try
        {
          iter.remove();
        } catch (Exception e)
        {
          logError("Exception occured while trying to remove an element from ArrayList pings while processing Ping Reply.  Exception: "
              + e);
        }
        return;
      }
    }
    logError("Unexpected Ping Reply from " + packet.getSrc() + ": " + payload);
  }
  
  private void receiveDistanceVector(Packet packet)
  {
  	//logOutput("Receive Distance Vector from " + packet.getSrc());
    if(packet.getTTL() > 0)
    {
    	boolean flgChange = false;
    	int[] tblDistanceIn = new int[100];
    	int[] tblNextHopIn = new int[100];
    	byteArrayToIntArrays(packet.getPayload(), tblDistanceIn, tblNextHopIn);
      
      for(int i=0; i<tblDistanceIn.length; i++)
      {
      	//System.out.println("(" + i + ", " + tblDistanceIn[i] + ", " 
      	//		+ tblNextHopIn[i] + ")");
      	if(i == packet.getSrc())
      	{
      		if(tblDistance[i] != 1)
      		{
  	    		tblDistance[i] = 1;
  	    		tblNextHop[i] = packet.getSrc();
  	    		flgChange = true;
      		}
      	}else
      	{
      		if(tblDistance[i] > tblDistanceIn[i] + 1 
      				&& tblDistanceIn[i] + 1 < MaxHops
      		    && tblNextHop[i] != this.addr) //split horizon
      		{
      			tblDistance[i] = tblDistanceIn[i] + 1;
      			tblNextHop[i] = packet.getSrc();
      			flgChange = true;
      		}
      	}
      }
      if(flgChange)
      {
      	exchangeDistanceVector();
      }
    }
  }

  private void send(int destAddr, Packet packet)
  {
    try
    {
      this.manager.sendPkt(this.addr, destAddr, packet.pack());
    } catch (IllegalArgumentException e)
    {
      logError("Exception: " + e);
    }
  }

  // Adds a timer, to fire in deltaT milliseconds, with a callback to a public
  // function of this class that takes no parameters
  private void addTimer(long deltaT, String methodName)
  {
    try
    {
      Method method = Callback.getMethod(methodName, this, null);
      Callback cb = new Callback(method, this, null);
      this.manager.addTimer(this.addr, deltaT, cb);
    } catch (Exception e)
    {
      logError("Failed to add timer callback. Method Name: " + methodName
          + "\nException: " + e);
    }
  }

  public void neighborCheck()
  {

    // Must send packets with TTL = 1
    // Packets that recieve it will reply
    // Then we update the list
    // For any changes in the list we sound an alert
    // when we recieve a packet meant for us we need to check that it matches
    // some sequence number that we sent out and if it does we update the list

    // destroy old neighbor list
    // update it with new neighborlist

    // send out next round of scouts to check neighbors

    // we first print out the lists every time there is a difference
    if (!currentneighborList.equals(newneighborList))
    {
      this.matchDumpCommand("dump neighbors");
    }
    this.currentneighborList = this.newneighborList;
    newneighborList = new HashSet();
    this.initTablesForDistanceVector();

    // Send ping packet for neighbor discovery
    // Payload is irrelevant but we need to have some payload
    byte[] payload = (this.polling).getBytes();
    // payload is irrelevant - this will be the only packet broadcast to 255
    // with ttl 1
    Packet pack = new Packet(Packet.BROADCAST_ADDRESS, this.addr, 1,
        Protocol.PING_PKT, this.currentSequence, payload);
    currentSequence++;
    this.send(Packet.BROADCAST_ADDRESS, pack);
      
    this.addTimer(PingTimeout, "neighborCheck");
  }
  
  public void exchangeDistanceVector()
  {
  	//Send DV packet to neighbors
    byte[] payload = intArraysToByteArray(tblDistance, tblNextHop);
    Packet pack = new Packet(Packet.BROADCAST_ADDRESS, this.addr, 1,
        Protocol.DV_INFO_PKT, this.currentSequence, payload);
    currentSequence++;
    this.send(Packet.BROADCAST_ADDRESS, pack);
  }
   
  //initialize distance vector routing table
  private void initTablesForDistanceVector()
  {
  	for(int i=0; i<tblDistance.length; i++)
    {
    	if(i==this.addr)
    	{
    		tblDistance[i] = 0;
    		tblNextHop[i] = this.addr;
    	}else
    	{
    		tblDistance[i] = MaxHops; //use MaxHops to prevent count-to-infinity
    		tblNextHop[i] = -1;
    	}    	
    }
  }
  
  private byte[] intArraysToByteArray(int[] arrDistance, int[] arrNextHop)
  {
  	byte[] btArr = new byte[4*(arrDistance.length + arrNextHop.length)];
  	byte[] btInt = new byte[4];
  	for(int i=0; i<arrDistance.length; i++)
  	{
  		btInt = Packet.intToByteArray(arrDistance[i]);
  		for(int j=0; j<4; j++)
  		{
  			btArr[i*4 + j] = btInt[j];
  		}
  	}
  	
  	for(int i=0; i<arrNextHop.length; i++)
  	{
  		btInt = Packet.intToByteArray(arrNextHop[i]);
  		for(int j=0; j<4; j++)
  		{
  			btArr[4*arrDistance.length + i*4 + j] = btInt[j];
  		}
  	}
  	return btArr;
  }
  
  private void byteArrayToIntArrays(byte[] btArr, int[] arrDistance, int[] arrNextHop)
  {
  	byte[] btSingle = new byte[4];
  	for(int i=0; i<arrDistance.length; i++)
  	{
  		
  		for(int j=0; j<4; j++)
  		{
  			btSingle[j] = btArr[i*4 + j];
  		}
  		arrDistance[i] = Packet.byteArrayToInt(btSingle, 0);
  	}
  	for(int i=0; i<arrNextHop.length; i++)
  	{
  		
  		for(int j=0; j<4; j++)
  		{
  			btSingle[j] = btArr[arrDistance.length * 4 + i*4 + j];
  		}
  		arrNextHop[i] = Packet.byteArrayToInt(btSingle, 0);
  	}
  	
  	return;
  }

  private void logError(String output)
  {
    this.log(output, System.err);
  }

  private void logOutput(String output)
  {
    this.log(output, System.out);
  }

  private void log(String output, PrintStream stream)
  {
    stream.println("Node " + this.addr + ": " + output);
  }
}

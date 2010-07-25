
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;

/**
 * Implements a Link State routing table. Based on Peterson's Forward Search
 * Algorithm, Peterson p.281.
 * 
 */
public class RoutingTable {

    /**
     * The routing table.
     */
    private Hashtable<Integer, RoutingEntry> confirmedTable;
    /**
     * The LSPs received from other nodes.
     */
    private Hashtable<Integer, int[]> linkStatePackets;
    public final static int UNIT_COST = 1;
    public final static int ROUTE_NONE = -1;
    public final static int ROUTE_SELF = -2;
    /**
     * The address of the node this routing table is built for.
     */
    int addr;
    Node node;

    public RoutingTable(Node node) {
	this.node = node;
	this.addr = node.getAddr();
	confirmedTable = new Hashtable<Integer, RoutingEntry>();
	linkStatePackets = new Hashtable<Integer, int[]>();
    }

    /**
     * Clears all LSPs and routing table.
     * 
     */
    public void clear() {
	confirmedTable.clear();
	linkStatePackets.clear();
    }

    /**
     * Adds an LSP (list of neighbors) for a node.
     * 
     * @param node
     * @param neighbors
     * @return LSP replaced (null if none replaced)
     */
    public int[] addLSP(int node, int[] neighbors) {
	int[] previous = linkStatePackets.put(node, neighbors);
	this.build();
	return previous;
    }

    /**
     * Removes an LSP.
     * 
     * @param node
     * @return the LSP removed.
     */
    public int[] removeLSP(int node) {
	int[] previous = linkStatePackets.remove(node);
	this.build();
	return previous;
    }

    /**
     * Get next hop on route to destination.
     * 
     * @param destination
     * @return NO_ROUTE if no route to destination.
     */
    public int getNextHop(int destination) {
	RoutingEntry entry = confirmedTable.get(destination);
	if (entry == null) {
	    return ROUTE_NONE;
	} else {
	    return entry.getNextHop();
	}
    }

    public Integer[] getNodes() {
	return this.confirmedTable.keySet().toArray(new Integer[0]);
    }

    public Hashtable<Integer, int[]> getLinkStatePackets() {
	return linkStatePackets;
    }

    /**
     * Need this method because Hashtable.toString() does not use
     * Arrays.toString() to convert the int[] to String, i.e. no 'deep'
     * toString().
     * 
     * Note:the code below is modified from Hashtable.toString() version 1.116
     * by Arthur van Hoff, Josh Bloch and Neal Gafter.
     */
    public String getLinkStatePacketsAsString() {
	int max = linkStatePackets.size() - 1;
	if (max == -1) {
	    return "{}";
	}
	StringBuilder sb = new StringBuilder();
	Iterator<Map.Entry<Integer, int[]>> it = linkStatePackets.entrySet().iterator();

	sb.append('{');
	for (int i = 0;; i++) {
	    Map.Entry<Integer, int[]> e = it.next();
	    Integer key = e.getKey();
	    int[] value = e.getValue();
	    sb.append(key.toString());
	    sb.append('=');
	    sb.append(Arrays.toString(value));

	    if (i == max) {
		return sb.append('}').toString();
	    }
	    sb.append(", ");
	}
    }

    public String toString() {
	String str = "\n[Destination]    Cost     NextHop\n";
	for(Iterator<Entry<Integer, RoutingEntry>> it 
		= confirmedTable.entrySet().iterator(); it.hasNext();){
	    Entry<Integer, RoutingEntry> entry = it.next();
	    str += "<" + (entry.getValue().destination) + ">  " + entry.getValue().cost + "  " + "<" + entry.getValue().nextHop + ">" + "\n";
	}
	return str;
    }

    public void build() {

        node.logOutput("Building routing table...", Node.DEBUG_LINKSTATE);

	confirmedTable.clear();
	RoutingEntry entry = null;

	// Why do we need a table and a queue for tentative? We need to lookup
	// the cost of entries in tentative, but can't do a lookup out of a
	// queue. Queue is required because it maintains an ordered list. Add
	// and remove operations on these two must be done in pairs.
	PriorityQueue<RoutingEntry> tentativeQueue = new PriorityQueue<RoutingEntry>();
	Hashtable<Integer, RoutingEntry> tentativeTable = new Hashtable<Integer, RoutingEntry>();

	// 1. Initialize confirmed list with entry for self.
	RoutingEntry next = new RoutingEntry(addr, 0, ROUTE_SELF);
	confirmedTable.put(addr, next);

	while (true) {
	    // 2. Select the LSP of node next
	    int[] LSP = linkStatePackets.get(next.getDestination());

	    if (LSP == null) {
		// Don't have the LSP for a neighbor.
		// Assume empty and skip over.
		LSP = new int[0];
	    }

	    // 3. For each neighbor of next, calculate the cost to reach this
	    // neighbor as the sum of the cost from myself to next and from next
	    // to neighbor.
	    for (int neighbor : LSP) {

		if (neighbor == this.addr) {
		    // Ignore routes to self!
		    continue;
		}

		// Test to see if neighbor also advertises neighborhood with
		// next.
		if (!isNeighbor(neighbor, next)) {
		    continue;
		}

		int costFromMyselfToNext = confirmedTable.get(
			next.getDestination()).getCost();
		int costNextToNeighbor = UNIT_COST;
		int cost = costFromMyselfToNext + costNextToNeighbor;

		boolean tentativeContainsNeighbor = tentativeTable.containsKey(neighbor);

		if (!(confirmedTable.containsKey(neighbor) || tentativeContainsNeighbor)) {
		    // (a) If neighbor (of next) is not on either confirmed or
		    // tentativelist, then add (neighbor, cost, nextHop) to
		    // tentative list, where nextHop is the direction I go to
		    // reach next.

		    int nextHop = getNextHop(next.getDestination());
		    if (nextHop == ROUTE_SELF) {
			// If next is self, then nextHop to neighbor is
			// neighbor.
			nextHop = neighbor;
		    }

		    entry = new RoutingEntry(neighbor, cost, nextHop);
		    tentativeQueue.add(entry);
		    tentativeTable.put(neighbor, entry);

		} else if (tentativeContainsNeighbor) {
		    // (b) If neighbor is currently on tentative list, and the
		    // cost is less than the cost on the list, then replace.
		    entry = tentativeTable.get(neighbor);
		    if (cost < entry.cost) {
			// Replace.
			tentativeTable.remove(neighbor);
			tentativeQueue.remove(entry);
			int nextHop = confirmedTable.get(next.getDestination()).getNextHop();
			entry = new RoutingEntry(neighbor, cost, nextHop);
			tentativeTable.put(neighbor, entry);
			tentativeQueue.add(entry);
		    }
		}

	    } // for LSP

	    // 4. If tentative is empty, stop. Otherwise pick entry from
	    // tentative with lowest cost and move it to the confirmed list.
	    // Select next to be node just added to confirmed.
	    // Return to step 2.
	    if (tentativeQueue.isEmpty()) {
		break;
	    }

	    entry = tentativeQueue.remove();
	    tentativeTable.remove(entry.getDestination());
	    confirmedTable.put(entry.getDestination(), entry);
	    next = entry;
	}
        node.logOutput("Building routing table... Done.", Node.DEBUG_LINKSTATE);
    }

    /**
     * Determines if a neighbor of 'next' is also advertised by neighbor as
     * having neighborhood with next. Used to determine that two nodes agree on
     * their neighborhood.
     */
    private boolean isNeighbor(int neighbor, RoutingEntry next) {
	int[] neighbors = this.linkStatePackets.get(neighbor);
	if (neighbors == null) {
	    // No LSP for this neighbor!
	    return false;
	}
	for (int n : neighbors) {
	    if (n == next.getDestination()) {
		return true;
	    }
	}
	return false;
    }

    public class RoutingEntry implements Comparable<RoutingEntry> {

	private int destination;
	private int cost;
	private int nextHop;

	public RoutingEntry(int destination, int cost, int nextHop) {
	    this.destination = destination;
	    this.cost = cost;
	    this.nextHop = nextHop;
	}

	public int getCost() {
	    return cost;
	}

	public int getDestination() {
	    return destination;
	}

	public int getNextHop() {
	    return nextHop;
	}

	public String toString() {
	    StringBuilder string = new StringBuilder();
	    string.append("(");
	    string.append(this.destination);
	    string.append(",");
	    string.append(this.cost);
	    string.append(",");
	    string.append(this.nextHop);
	    string.append(")");
	    return string.toString();
	}

	public int compareTo(RoutingEntry routingEntry) {
	    if (this.cost < routingEntry.getCost()) {
		return -1;
	    } else if ((this.cost > routingEntry.getCost())) {
		return 1;
	    } else {
		return 0;
	    }
	}

	/**
	 * Equality is measered by destination (cost and nextHop excluded). Used
	 * to search for entries in tentativeQueue.
	 */
	public boolean equals(Object obj) {
	    RoutingEntry routingEntry = (RoutingEntry) obj;
	    if (this.destination == routingEntry.getDestination()) {
		return true;
	    } else {
		return false;
	    }
	}
    }
}

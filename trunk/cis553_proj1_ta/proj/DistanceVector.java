
import java.util.Hashtable;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Map.Entry;

public class DistanceVector {

    public static final int MAX_NEIGHBORS = Packet.MAX_PAYLOAD_SIZE; // the address of node which own this distance vector

    /*
     * count to infinity fix
     */
    public static final int MAX_DISTANCE = 16;
    
    /*
     * for handle exception when finding route
     */
    public static final  int ROUTE_NONE = -1;
    
    private Integer mNodeAddr;

    private class DistanceVectorEntry {

	public Integer mDst;
	public Integer mDistance;
	public Integer mNextHop;

	public DistanceVectorEntry(Integer dst, Integer distance, Integer nextHop) {
	    this.mDst = dst;
	    this.mDistance = distance;
	    this.mNextHop = nextHop;
	}
    }

    /*
     * distanceVector (dst(as key), <dst, distance, nextHop>) 
     */
    private Hashtable<Integer, DistanceVectorEntry> mDisVec =
	    new Hashtable<Integer, DistanceVectorEntry>();

    public DistanceVector(Integer nodeAddr) {
	this.mNodeAddr = nodeAddr;
	/*
	 * put myself in, and the distance to myself is 0, nextHop is myself
	 */
	this.mDisVec.put(nodeAddr, new DistanceVectorEntry(nodeAddr, 0, nodeAddr));
    }
    
    /*
     * add a new entry into distance vector
     */
    public void put(Integer dst, Integer distance, Integer nextHop){
	this.mDisVec.put(dst, new DistanceVectorEntry(dst, distance, nextHop));
    }

    /*
     * remove all entries like: 
     * (1) (nodeAddr, *, *)
     * (2) (*, *, nodeAddr)
     */
    public void remove(Integer nodeAddr){
	for(Iterator<Entry<Integer, DistanceVectorEntry>> iter
		= this.mDisVec.entrySet().iterator(); iter.hasNext(); ){
	    Entry<Integer, DistanceVectorEntry> entry = iter.next();
	    if(entry.getValue().mDst == nodeAddr 
		    || entry.getValue().mNextHop == nodeAddr){
		iter.remove();
	    }
	}
    }

    /*
     * get the nextHop to dst
     * @param destination address
     */
    public Integer getNextHop(Integer dst){
	DistanceVectorEntry distanceVectorEntry = this.mDisVec.get(dst);
	// no route for this destination
	if(distanceVectorEntry == null){
	    return this.ROUTE_NONE;
	}
	// route distance is infinity, i.e. route does no exist
	else if(distanceVectorEntry.mDistance == MAX_DISTANCE){
	    return this.ROUTE_NONE;
	}else{
	    return distanceVectorEntry.mNextHop;
	}
    }
    
    public byte[] pack() { /// int => outputStream => byte
	ByteArrayOutputStream stream = new ByteArrayOutputStream(mDisVec.keySet().size() * 3);
	Integer dst, distance, nextHop;
	for (Iterator<Integer> iter = this.mDisVec.keySet().iterator(); iter.hasNext();) {
	    dst = iter.next();
	    distance = this.mDisVec.get(dst).mDistance;
	    nextHop = mNodeAddr; // not this.dv.get(dst).nextHop, since this is sent to neighbor
	    stream.write(dst);
	    stream.write(distance);
	    stream.write(nextHop);
	}
	return stream.toByteArray();
    }

    /*
     * merge current distance vector with the packet received
     * @param byteArray is the info of distance vector
     */
    public int merge(byte[] byteArray) { /// byte => inputStream => int
	if((byteArray.length % 3) != 0) // length must be triple, due to pack()
	    return -1;
	int length = byteArray.length / 3;
	ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);
	Integer dst, distance, nextHop;
	for (int i = 0; i < length; i++) {
	    dst = stream.read();
	    distance = stream.read();
	    nextHop = stream.read(); // nextHop for this node here is the neighbor
	    mergeSingleEntry(new DistanceVectorEntry(dst, distance, nextHop));
	}
	return 0;
    }

    private void mergeSingleEntry(DistanceVectorEntry distanceVectorEntry) {
	Integer dst = distanceVectorEntry.mDst;
	Integer distance = distanceVectorEntry.mDistance;
        Integer nextHop = distanceVectorEntry.mNextHop;
	Integer key;
	boolean isNewRoute = true;
	boolean updateRoute = false;
	
	/* first we suppose this is not a new route by setting "isNewRoute" to be true
	 * and we update old route, if found a better one
	 */
	for (Iterator<Integer> iter = this.mDisVec.keySet().iterator(); iter.hasNext();) {
	    key = iter.next(); // key is the same with mDisVec.get(key).mDst
	    if (key == dst) {
		isNewRoute = false; // indicate it is an old route
		// if find a better route
		if ((distance + 1) < this.mDisVec.get(key).mDistance) {
		    updateRoute = true;
		} // if a former route become worse (longer distance)
		else if (nextHop == this.mDisVec.get(key).mNextHop &&
			(distance + 1) > this.mDisVec.get(key).mDistance) {
		    updateRoute = true;
		}
	    }
	}
	
	//so if it is indeed a new route, be ready to put it into distance vector
	if(isNewRoute){
	    updateRoute = true;
	}

	/**
	 * update distance, with "count to infinity" fixing
	 * if reaching distance limit, set the route to be infinity
	 */
	if(updateRoute){
	    if(distance + 1 > MAX_DISTANCE){
                this.mDisVec.put(dst, new DistanceVectorEntry(dst, MAX_DISTANCE, nextHop));
	    }else{
		this.mDisVec.put(dst, new DistanceVectorEntry(dst, distance + 1, nextHop));
	    }
	}
    }
   
   public String toString(){
       String str = "\n[Destination]    Distance     NextHop\n";
       for(Iterator<Entry<Integer, DistanceVectorEntry>> iter 
	       = mDisVec.entrySet().iterator(); iter.hasNext(); ){
	   Entry<Integer, DistanceVectorEntry> entry = iter.next();
	   str += "<" + (entry.getValue().mDst.toString() + ">  " 
		   + entry.getValue().mDistance.toString() + "  "
		   + "<" + entry.getValue().mNextHop.toString()) + ">" + "\n";
       }
       return str;
   }
}

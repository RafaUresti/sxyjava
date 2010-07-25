import java.util.ArrayList;

/**
 * A synchronized queue. Objects can be <code>put</code>
 * in the queue and retrieved from it by separate Threads;
 * 
 * @author Xiaoyi Sheng
 * @version Mar 6, 2008
 * @param <V> The type of object that may be put into
 *            this Queue.
 */
public class Queue<V> {
    private ArrayList<V> list = new ArrayList<V>();

    /**
     * Adds an object to this Queue.
     * @param obj The object to be added.
     */
    public void put(V obj) {
        synchronized (list) {
            list.add(obj);
            list.notifyAll();
        }
    }

    /**
     * Returns an Object from this Queue. If Queue is empty, returns Null.
     * @return The Object removed from this Queue.
     */
    public V get() {
        if (list.size() > 0) {
            synchronized (list) {
                return list.remove(0);
            }
        } else return null;
    }
}

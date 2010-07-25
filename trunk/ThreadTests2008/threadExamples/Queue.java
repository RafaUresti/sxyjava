package threadExamples;

import java.util.ArrayList;

/**
 * A synchronized queue. Objects can be <code>put</code>
 * in the queue and retrieved from it by separate Threads;
 * a request to <code>get</code> something from this Queue
 * will wait (block) until there is something to get.
 * 
 * @author Dave Matuszek
 * @version Mar 14, 2007
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
            list.notify();
        }
    }

    /**
     * Returns an Object from this Queue, waiting indefinitely
     * if necessary for something to be put in this Queue.
     * @return The Object removed from this Queue.
     */
    public V get() {
        if (list.size() > 0) {
            synchronized (list) {
                return list.remove(0);
            }
        } else {
            try {
                synchronized (list) {
                    while (list.size() == 0) {
                        list.wait(1000);
                    }
                    return get();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("InterruptedException", e);
            }
        }
    }

    /**
     * Returns an Object from this Queue, waiting up to <code>limit</code>
     * milliseconds, if necessary, for something to be put into this Queue.
     * If nothing is put into this Queue within the limit, <code>null</code>
     * is returned.
     * 
     * @param limit The number of milliseconds to wait.
     * @return The Object removed from this Queue (or <code>null</code>,
     *         if the time limit is exceeded).
     */
    public V get(int limit) {
        if (list.size() > 0) {
            synchronized (list) {
                return list.remove(0);
            }
        } else {
            try {
                synchronized (list) {
                    if (list.size() == 0) {
                        list.wait(limit);
                    }
                    if (list.size() > 0) {
                        return list.remove(0);
                    } else {
                        return null;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("InterruptedException", e);
            }
        }
    }
}

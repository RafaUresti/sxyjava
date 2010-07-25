package ThreadTests2008.threadExamples;

/**
 * A pair of Queues, representing a two-way channel
 * between Threads. The Queues are synchronized.
 * <p>
 * Although it isn't required, the intended use of a
 * <code>Channel</code> is asymmetric: One Thread puts requests
 * into the <code>Channel</code>, and another Thread services those
 * requests and returns a response.
 *
 * @author David Matuszek
 * @version January 28, 2008
 * @param <V> The type of object used as a request.
 * @param <W> The type of object used as a response.
 */
public class Channel<V, W> {
    private Queue<V> request;
    private Queue<W> response;

    /**
     * Creates a new Channel (a pair of Queues).
     */
    public Channel() {
        request = new Queue<V>();
        response = new Queue<W>();    }

    /**
     * Puts an Object of type <code>V</code> into the request queue.
     * @param value The object to be added.
     */
    public void putRequest(V value) {
        request.put(value);
    }

    /**
     * Gets an Object of type <code>V</code> from the request queue.
     * @return The Object removed from the request queue.
     */
    public V getRequest() {
        return request.get();
    }
    /**
     * Puts an object of type <code>W</code> into the response queue.
     * @param value The object to be added.
     */
    public void putResponse(W value) {
        response.put(value);
    }
    /**
     * Gets an Object of type <code>W</code> from the response queue.
     * @return The Object removed from the response queue.
     */
    public W getResponse() {
        return response.get();
    }
}

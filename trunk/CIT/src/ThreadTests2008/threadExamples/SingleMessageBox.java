package ThreadTests2008.threadExamples;

/**
 * Implements a message box which can hold only a single
 * message at a time. Behavior is almost identical to that
 * of <code>BatonMessageBox</code>, but the implementation
 * is different: the <code>put</code> and <code>get</code>
 * methods are themselves synchronized, rather than using
 * a separate object on which to synchronize.
 *
 * @author David Matuszek
 * @version January 28, 2008
 * @param <V> The type of "message" that may be put into
 *            this SingleMessageBox.
 */
public class SingleMessageBox<V> {
    private V value;

    /**
     * Creates an empty SingleMessageBox.
     */
    public SingleMessageBox() {
        value = null;
    }

    /**
     * Waits until this SingleMessageBox is empty, then puts
     * the given "message" of type V into it.
     * 
     * @param nextValue The message to be added. Should not be
     * <code>null</code>.
     */
    public synchronized void put(V nextValue) {
        while (value != null) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
        value = nextValue;
        this.notifyAll();
    }

    /**
     * Waits until there is a "message" of type V in this
     * SingleMessageBox, then removes and returns it.
     * 
     * @return The message removed from this Queue.
     */
    public synchronized V get() {
        V temp;
        while (value == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
        temp = value;
        value = null;
        this.notifyAll();
        return temp;
    }
}

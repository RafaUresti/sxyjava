package threadExamples;
/**
 * Implements a message box which can hold only a single
 * message at a time. Behavior is almost identical to that of
 * <code>SimpleMessageBox</code>, but the implementation
 * is different: <code>put</code> and <code>get</code> are
 * not synchronized methods, but instead use an explicit
 * (though otherwise unimportant) object for synchronization.
 *
 * @author David Matuszek
 * @version January 28, 2008
 * @param <V> The type of object that can be put into a BatonMessageBox.
 */
public class BatonMessageBox<V> {
    private Object baton; // The explicit object used for synchronization
    private V value;

    /**
     * Creates an empty BatonMessageBox.
     */
    public BatonMessageBox() {
        baton = new Object();
        value = null;
    }
    /**
     * Waits until this <code>BatonMessageBox</code> is empty, then puts
     * the given object into it.
     * 
     * @param nextValue The object to be added.
     */
    public void put(V nextValue) {
        synchronized (baton) {
            while (value != null) {
                try {
                    baton.wait();
                } catch (InterruptedException e) {
                }
            }
            value = nextValue;
            baton.notifyAll();
        }
    }

    /**
     * Waits until there is something in this
     * <code>BatonMessageBox</code>, then removes and
     * returns it.
     * 
     * @return The object removed from this Queue.
     */
    public V get() {
        V temp;
        synchronized (baton) {
            while (value == null) {
                try {
                    baton.wait();
                } catch (InterruptedException e) {
                }
            }
            temp = value;
            value = null;
            baton.notifyAll();
        }
        return temp;
    }
}

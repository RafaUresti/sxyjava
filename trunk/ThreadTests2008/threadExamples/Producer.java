package threadExamples;

import java.util.Random;

/**
 * Once started, a Producer will unilaterally start producing
 * numbers (using the <code>increment</code> with which it is
 * started). As each number is produced, the <code>run()</code>
 * method attempts to put it in the message box. Hence, this
 * Producer may be as much as two numbers ahead of the consumer
 * (the "consumer" is the Thread removing numbers from the message
 * box), because there may be one number in the message box
 * waiting to be consumed, and a second number waiting to be put
 * into the message box.
 * <p>
 * A Producer will use <i>either</i> a <code>SingleMessageBox</code>
 * <i>or</i> a <code>BatonMessageBox</code>, but not both, depending
 * on which constructor was used.
 * 
 * @author David Matuszek
 * @version February 27, 2008
 */
public class Producer extends Thread {
    private static Random rand = new Random();
    private SingleMessageBox<Integer> simpleBox = null;
    private BatonMessageBox<Integer> batonBox = null;
    private int increment;
    private int count;
    
    /**
     * Constructs a Producer that communicates by means of a
     * SingleMessageBox, and produces values that count up by
     * the given increment.
     * 
     * @param simpleBox The box to use for returning results.
     * @param increment The amount by which to count.
     */
    public Producer(SingleMessageBox<Integer> simpleBox, int increment) {
        setDaemon(true); // Daemon Threads die when main program ends
        this.simpleBox = simpleBox;
        this.increment = increment;
        this.count = 0;
    }
    
    /**
     * Constructs a Producer that communicates by means of a
     * BatonMessageBox, and produces values that count up by
     * the given increment.
     * 
     * @param batonBox The box to use for returning results.
     * @param increment The amount by which to count.
     */
    public Producer(BatonMessageBox<Integer> batonBox, int increment) {
        setDaemon(true); // Daemon Threads die when main program ends
        this.batonBox = batonBox;
        this.increment = increment;
        this.count = 0;
    }
    
    /**
     * Continuously counts up (with a random delay) and puts the
     * next number into a message box, if the message box is
     * available (empty). Pauses when it has to wait for the
     * message box to become available.
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (true) {
            randomDelay();
            count += increment;
            System.out.println("Producing " + count + " from "
                               + (simpleBox != null
                                       ? "SimpleMessageBox"
                                       : "BatonMessageBox"));
            if (simpleBox != null) simpleBox.put(count);
            if (batonBox != null) batonBox.put(count);
        }
    }
    
    /**
     * Pauses for one to ten milliseconds.
     */
    private void randomDelay() {
        try { Thread.sleep(rand.nextInt(10) + 1); }
        catch (InterruptedException e) {}
    }
}

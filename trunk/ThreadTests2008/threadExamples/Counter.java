package threadExamples;

import java.util.Random;

/**
 * This class illustrates the use of a Channel (a pair of
 * Queues) to provide two-way communication between Threads.
 * When a request is received via a Channel, a response
 * (an incremented number) is returned in the same Channel.
 * 
 * @author David Matuszek
 * @version February 27, 2008
 */
public class Counter extends Thread {
    private Channel<?, Integer> c;
    private int count;
    private int increment;
    private static Random rand = new Random();
    
    /**
     * Creates a Counter.
     * 
     * @param c The Channel to be used for communication.
     * @param increment The amount by which to count.
     */
    public Counter(Channel<?, Integer> c, int increment) {
        this.c = c;
        this.increment = increment;
        this.count = 0;
    }
    
    /**
     * When a request is received (via the request queue of a
     * Channel), then after a random delay, a response (an
     * incremented number) is produced and returned (via the
     * response queue of the same Channel).
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (true) {
            Object request = c.getRequest();
            randomDelay(); // for a random amount of time
            if ("quit".equals(request)) break;
            count += increment;
            System.out.println("Counting by " + increment + " gives " + count);
            c.putResponse(count);
        }
        System.out.println("Done counting by " + increment);
    }
    
    /**
     * Pauses a random amount of time.
     */
    public static void randomDelay() {
        try { Thread.sleep(rand.nextInt(50) + 1); }
        catch (InterruptedException e) {}
    }
}

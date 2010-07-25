package threadExamples;

/**
 * This program demonstrates three ways to keep Threads
 * in sync. The first approach uses a Counter class to
 * generate numbers; the second and third approaches each
 * use a customized version of the Producer class to
 * generate numbers. In each case, two Threads are used
 * to produce numbers: One Thread counts from 1 to 10,
 * while the second Thread counts by tens from 10 to 100.
 * <p>
 * Because this is a demonstration program, the Counter
 * and Producer classes each introduce random delays in the
 * time it takes them to provide the next number. (You would
 * not normally do this is a "real" program.) Thus, the
 * order in which numbers are provided is not predictable.
 * <p>
 * In each case, the important thing to notice is that, while
 * the random delays in the Counter and Producer methods mean
 * that one sometimes gets ahead of the other, nonetheless, each
 * of the three methods in this class
 * (<code>useChannels</code>, <code>useSingleMessageBoxes</code>,
 * and <code>useBatonMessageBoxes</code>) succeeds in synchronizing
 * (pairing up) the results correctly. You can see this because
 * the output lines beginning with <tt>"-- Got"</tt> show the correct
 * corresponding values.
 * 
 * @author David Matuszek
 * @version February 27, 2008
 */
public class Main {    
    /**
     * Demonstration program to synchronize two Threads--one counting
     * from 1 to 10, the other counting by tens from 10 to 100--in
     * three different ways.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String line = "\n------------------------ ";
        System.out.println(line + "Using Channels");
        useChannels();
        System.out.println(line + "Using SingleMessageBoxes");
        useSingleMessageBoxes();
        System.out.println(line + "Using BatonMessageBoxes");
        useBatonMessageBoxes();
    }

    /**
     * Demonstrates coordination using Channels. A Channel is a pair
     * of Queues; one Queue is used for sending requests, the other
     * Queue for receiving responses.
     */
    private static void useChannels() {
        // Create two Channels
        Channel<String, Integer> channelOne = new Channel<String, Integer>();
        Channel<String, Integer> channelTwo = new Channel<String, Integer>();
        
        // Create and start two Thread-type objects
        // (OnesCounter and HundredsCounter), each with
        // its own Channel.
        Thread threadOne = new Counter(channelOne, 1);
        Thread threadTwo = new Counter(channelTwo, 10);        
        threadOne.start();
        threadTwo.start();
        
        for (int i = 0; i < 10; i++) {
            // Put a (meaningless) request on each Channel. The
            // corresponding Thread will take a random amount of
            // time to respond.
            channelOne.putRequest("Unused in this example");            
            channelTwo.putRequest("Whatever");
        }
        for (int i = 0; i < 10; i++) {            
            // Wait for _both_ Threads to respond, then print.
            int s1 = channelOne.getResponse();
            int s2 = channelTwo.getResponse();
            System.out.println("-- Got " + s1 + " and " + s2);
        }
        
        // Tell Threads to quit; no response is expected.
        channelOne.putRequest("quit");            
        channelTwo.putRequest("quit");
    }

    /**
     * Demonstrates coordination using SingleMessageBoxes.
     * A SingleMessageBox is a "queue" that can hold at most
     * one item.
     * <p>
     * Other than using a different kind of "message box",
     * this method is identical to useBatonMessageBoxes.
     */
    private static void useSingleMessageBoxes() {
        // Create two message boxes, then associate a
        // Producer Thread with each one.
        SingleMessageBox<Integer> boxOne = new SingleMessageBox<Integer>();
        SingleMessageBox<Integer> boxTwo = new SingleMessageBox<Integer>();
        Producer producerOne = new Producer(boxOne, 1);
        Producer producerTwo = new Producer(boxTwo, 10);
        
        // Start the Producer Threads.
        producerOne.start();
        producerTwo.start();
        
        for (int i = 0; i < 10; i++) {
            // Wait for _both_ Threads to produce, then print.
            Object one = boxOne.get();
            Object two = boxTwo.get();
            System.out.println("-- Got " + one + " and " + two);
        }
    }
    /**
     * Demonstrates coordination using BatonMessageBoxes.
     * A BatonMessageBox is a "queue" that can hold at most
     * one item.
     * <p>
     * Other than using a different kind of "message box",
     * this method is identical to useSingleMessageBox.
     */
    private static void useBatonMessageBoxes() {
        // Create two message boxes, then associate a
        // Producer Thread with each one.
        BatonMessageBox<Integer> boxOne = new BatonMessageBox<Integer>();
        BatonMessageBox<Integer> boxTwo = new BatonMessageBox<Integer>();
        Producer producerOneX = new Producer(boxOne, 1);
        Producer producerTwoX = new Producer(boxTwo, 10);
        
        // Start the Producer Threads.
        producerOneX.start();
        producerTwoX.start();
        for (int i = 0; i < 10; i++) {
            // Wait for _both_ Threads to produce, then print.
            Object one = boxOne.get();
            Object two = boxTwo.get();
            System.out.println("-- Got " + one + " and " + two);
        }
    }
}

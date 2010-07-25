import java.lang.Thread;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedList;

public class MultiplexIO extends Thread {
    
    private PipedInputStream input;
    private PipedOutputStream output;
    private LinkedList data;  // in 1.5, was private LinkedList<Integer> data;

    public MultiplexIO() throws IOException {
	this.input = new PipedInputStream();
	this.output = new PipedOutputStream(this.input);
	this.data = new LinkedList();
    }

    public void run() {
	while(true) {
	    try {
		this.store(this.input.read());
	    }catch(Exception e) {
		System.err.println("Exception occured while waiting for IO channels. Exception: " + e);
	    }
	}
    }

    public synchronized void write(int b) throws IOException {
	this.output.write(b);
    }

    public synchronized int read() {
	if(!this.isEmpty()) {
	    return ((Integer) this.data.removeFirst()).intValue();
	    // in 1.5 was return this.data.poll().intValue();
	}
	return -1;
    }

    public synchronized boolean isEmpty() {
	return this.data.isEmpty();
    }

    private synchronized void store(int b) {
	this.data.add(new Integer(b));
	this.notifyAll();
    }
}

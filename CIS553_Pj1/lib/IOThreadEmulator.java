import java.io.IOException;

public class IOThreadEmulator extends IOThread {
    
    public static final int ID = 2;
    private MultiplexIO multiplexIO;
    
    public IOThreadEmulator(MultiplexIO multiplexIO) {
	super();
	this.multiplexIO = multiplexIO;	
    }

    protected synchronized void addLine(String line) {
	this.inputLines.add(line);
	try {
	    this.multiplexIO.write(ID);
	}catch(IOException e) {
	    System.out.println("IOException occured in IOThreadEmulator while writing to MultiplexIO. Exception: " + e);
	}
    }
}

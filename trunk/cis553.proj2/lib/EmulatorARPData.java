import java.net.InetAddress;

/**
 * <pre>   
 * Structure for storing data contained in the ARP cache of an emulated node
 * </pre>   
 */
public class EmulatorARPData {
    
    private InetAddress ipAddress;
    private int port;

    /**
     * Create a new structure
     * @param ipAddress The IP address of a neighboring node
     * @param port The port used by the neighboring node for communications
     */
    public EmulatorARPData(InetAddress ipAddress, int port) {
	this.ipAddress = ipAddress;
	this.port = port;
    }

    /**
     * Get the IP address
     * @return The IP address
     */
    public InetAddress getIPAddress() {
	return this.ipAddress;
    }

    /**
     * Get the port
     * @return The port
     */
    public int getPort() {
	return this.port;
    }
}

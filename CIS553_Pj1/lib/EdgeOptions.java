/**
 * <pre>   
 * Class to represent the edge options: loss rate, delay and bandwidth
 * </pre>   
 */
public class EdgeOptions {
    double lossRate;
    long delay;
    int bw;

    /**
     * Initializes loss rate to 0. Lossless link by default.
     * Initializes delay to 1 millisecond and bandwidth to 10KB/s
     */
    public EdgeOptions() {
	lossRate = 0.0; 
	delay = 1;
	bw = 10;
    }

    /**
     * Sets the loss rate
     * @param lossRate The new loss rate
     */
    public void setLossRate(double lossRate) {
	this.lossRate = lossRate;
    }

    /**
     * Sets the delay
     * @param delay The new delay, in milliseconds
     */
    public void setDelay(long delay) {
	this.delay = delay;
    }

    /**
     * Sets the bandwidth
     * @param bw The new bandwidth, in KB/s
     */
    public void setBW(int bw) {
	this.bw = bw;
    }

    /**
     * Returns the loss rate
     * @return The loss rate
     */
    public double getLossRate() {
	return lossRate;
    }

    /**
     * Returns the delay
     * @return The delay in milliseconds
     */
    public long getDelay() {
	return delay;
    }

    /**
     * Returns the bandwidth
     * @return The bandwidth in KB/s
     */
    public int getBW() {
	return bw;
    }
}

/**
 * Door Class: A Subclass of Thing
 * Door can be "unlocked" or "locked"
 * Cannot use or pickup a door
 * @author Mai Irie
 * @author Xiaoyi Sheng
 *
 */

public class Door extends Thing {

	private boolean status;
	
	/**
	 * Constructor for Door.
	 * Can un/lock door with key
	 * @param name The name of the key
	 * @param description The description of the key
	 */
	public Door(String name, String description) {
		super(name, description);
		status = false;
	}

	/**
	 * Returns the status of the door.
	 * If the door is "unlocked", returns True, otherwise returns False.
	 * @return The status of the door
	 */
	public boolean getStatus() {  return status; }
	
	/**
	 * Changes the status of the door. 
	 * @param newStatus The new status of the door
	 */
	public void setStatus(boolean newStatus) {  status = newStatus; }
	
	/**
	 * Cannot use a door..
	 */
	@Override
	public boolean useItem(Thing target) {
		return false;
	}

}

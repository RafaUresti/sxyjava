/**
 * Key Class: Subclass of Thing
 * @author Mai Irie
 * @author sxycode
 *
 */
public class Key extends Thing {

	private boolean status; // Whether or not the key is available to pickup

	/**
	 * Constructor for Key.
	 * Can use and pick up Key objects
	 * @param name The name of the key
	 * @param description The description of the key
	 */
	public Key(String name, String description) {
		super(name, description);
		canUse = true;
		canPickUp = true;
		status = false;  // Initially the status is set to false.
	}

	/**
	* Returns the status of the key.
	* @return if the key is available
	*/
	public boolean isAvailable() {  return status; }

	/**
	* make the key available
	*/
	public void setAvailable() {  status = true; }

	/**
	 * uses an item on a target item
	 * @param location the room where the item is used
	 * @param target the target to use on
	 * @return whether the use was successful
	 */
	public boolean useItem(Room location, Object target) {
		if (target instanceof Room) {
			int [] doors1 = location.getDoors();
			int [] doors2 = ((Room)target).getDoors();
			for (int i = 0; i < doors1.length; i ++) 
				if (location.getConnectedRooms()[i] == (Room)target)
					location.openDoor(i);
			for (int j = 0; j< doors2.length; j++)
				if (((Room)target).getConnectedRooms()[j] == location) {
				((Room)target).openDoor(j);
				return true;
				}
		}
		return false;
	}
	
	/**
	 * returns the effect of using the key
	 */
	public String effect() {
		return "The door is unlocked.";
	}
}

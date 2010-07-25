/**
 * Key Class: Subclass of Thing
 * @author Mai Irie
 * @author Xiaoyi Sheng
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

//	/**
//	* Returns the status of the key.
//	* If the key is available for pickup, returns True, otherwise returns False.
//	* @return The status of the key
//	*/
	public boolean isAvailable() {  return status; }

//	/**
//	* Changes the status of the key. 
//	* @param newStatus The new status of the key
//	*/
	public void setAvailable() {  status = true; }

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
	public String effect() {
		return "The door is unlocked.";
	}
}

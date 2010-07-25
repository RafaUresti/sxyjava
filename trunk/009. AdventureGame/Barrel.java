/** 
 * Barrel Class: A Subclass of Thing
 * @author Mai Irie
 * @author sxycode
 */

public class Barrel extends Thing {

	private boolean broken; // False = unbroken; True = broken
	private Key key;
	private boolean hasKey;
	/**
	 * The constructor for a Barrel.
	 * Cannot use or pickup a barrel.
	 * Can break an unbroken barrel. Initial status = unbroken
	 * @param name the name of the barrel
	 * @param description the description of the barrel
	 */
	public Barrel(String name, String description, Key hidden) {
		super(name, description);
		broken = false;
		key = hidden;
		hasKey = true;
	}
	
	/**
	 * The constructor for a broken barrel.
	 * Cannot use or pickup a barrel.
	 * status = broken
	 * @param name the name of the barrel
	 * @param description the description of the barrel
	 */
	public Barrel(String name, String description) {
		super(name, description);
		broken = true;
		hasKey = false;
	}
	
	/**
	 * Returns the status of the barrel.
	 * If the barrel is broken, returns True, otherwise returns False.
	 * @return The status of the barrel
	 */
	public boolean getStatus() {  return broken; }

	/**
	 * Changes the status of the barrel. 
	 * @param newStatus The new status of the barrel
	 */
	public void breakBarrel() {  broken = true; }

	/**
	 * if the key is in the barrel, picks the key from the barrel
	 * and sets the hasKey to false. Otherwise do nothing 
	 * @return the key in the barrel or null if unavailable
	 */
	public Key pickKey() {
		if (hasKey) {
			hasKey = false;
			return key;
		}
		else
			return null;
	}
}

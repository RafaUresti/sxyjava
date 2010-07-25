/** 
 * Barrel Class: A Subclass of Thing
 * @author Mai Irie
 * @author Xiaoyi Sheng
 */

public class Barrel extends Thing {
	
	private boolean broken; // False = unbroken; True = broken
	private Key key;
	private boolean hasKey;
	/**
	 * The constructor for a Barrel.
	 * Cannot use or pickup a barrel.
	 * Can break an unbroken barrel. Initial status = unbroken
	 * @param name
	 * @param description
	 */
	public Barrel(String name, String description, Key hidden) {
		super(name, description);
		broken = false;
		key = hidden;
		hasKey = true;
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
	
	public Key pickKey() {
		if (hasKey) {
			hasKey = false;
			return key;
		}
		else
			return null;
		}
	
//	@Override
//	/**
//	 * Can't use a Barrel on any kind of target. Always returns False.
//	 */
//	public boolean useItem(Object target) {
//		return false;
//	}
}

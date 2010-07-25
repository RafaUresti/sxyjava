package cit009_AdventureGame;
/**
 * Hammer Class: A Subclass of Thing
 * @author Mai Irie
 * @author sxycode
 *
 */
public class Hammer extends Thing {

	/**
	 * Constructor for Hammer.
	 * Can pickup and use a Hammer
	 * @param name The name of the Hammer
	 * @param description The description of the Hammer
	 */
	public Hammer(String name, String description) {
		super(name, description);
		canUse = true;
		canPickUp = true;
	}

	/**
	 * Can only use a Hammer on an unbroken WaterBarrel.
	 * @return True if target was an unbroken WaterBarrel, otherwise returns False.
	 */
	public boolean useItem(Object target) {
		if (!(target instanceof Barrel)) {
			return false;
		} else {
			if (((Barrel)target).getStatus()) {  // Cast target to type Barrel so can call getStatus()
				return false; // Can't use a Hammer on an broken Barrel...It's just stupid!
			} else {
				((Barrel)target).breakBarrel(); // Cast target to type Barrel so can call setStatus()
				return true;
			}
		}	
	}
	public String effect() {
		return "You have broken the water barrel. There is a key inside.";
	}
}

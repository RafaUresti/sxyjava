/**
 * Gold Class: Subclass of Thing
 * @author Mai Irie
 * @author Xiaoyi Sheng
 */

public class Gold extends Thing {

	/**
	 * Constructor for Gold
	 * Can pickup gold, but can't use it
	 * @param name Name of Gold
	 * @param description Description of Gold
	 */
	public Gold(String name, String description) {
		super(name, description); // Call to Super
		canUse= false;
		canPickUp = true;
	}

//	@Override
//	/**
//	 * Can't use Gold, so UseItem(target) always returns False.
//	 */
//	public boolean useItem(Object target) {
//		return false;
//	}

}

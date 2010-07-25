/**
 * Abstract class for different types of items in the room
 * @author Mai Irie
 * @author Xiaoyi Sheng
 */
public abstract class Thing {

	private String name;
	private String description;
	protected boolean canUse;
	protected boolean canPickUp;
	
	/**
	 * Constructor for Thing object
	 * Initial defaults = cannot pick up object,
	 * cannot use object.
	 * @param name The name of this Thing
	 * @param description The description of this Thing
	 */
	public Thing(String name, String description) {
		this.name = name;
		this.description = description;
		canUse = false;
		canPickUp = false;
	}
	
	/**
	 * Returns the name of this Thing object
	 * @return The name of this Thing
	 */
	public String getName() {  return name; }
	
	/**
	 * Returns the description of this Thing object
	 * @return The description of this Thing
	 */
	public String getDescription() {  return description; }
	
	/**
	 * Returns whether or not this Thing can be used.
	 * @return True if this Thing can be used, otherwise return false.
	 */
	public boolean canUse() { return canUse; }
	
	/**
	 * Returns whether or not this Thing can be picked up.
	 * @return True if this Thing can be picked up, otherwise return False.
	 */
	public boolean canPickUp() {  return canPickUp; }
	
	public String effect() {
		return " ";
	}
	/**
	 * Returns whether or not the use of this Thing on the target was successful.
	 * @param target The target Thing that this Thing will be used upon
	 * @return True if the use on target was successful, otherwise returns False.
	 */
//	public abstract boolean useItem(Object target);
//	public abstract boolean useItem(Room location, Object target);
}

import java.util.*;

/**
 * Player class. Defines all the properties and 
 * actions for players.
 */

public class Adventurer {
	private Room location;
	private ArrayList<Thing> inventory;
	private Object thingLookingAt;
	private Barrel brokenBarrel;
	/**
	 * Constructor for the Adventurer Player
	 * @param thisRoom The starting location of the adventurer
	 */
	public Adventurer(Room thisRoom) {
		location = thisRoom;
		inventory = new ArrayList<Thing>();
		thingLookingAt = null;
		brokenBarrel = new Barrel("Broken Barrel", "This barrel is broken.");
	}

	/**
	 * Moves the player to another room
	 * @param otherRoom the room to move to
	 * @return can or cannot move to the room
	 */
	public boolean move(Room otherRoom) { // possibly boolean because sometimes room is locked
		if (location.getDoor(otherRoom) == 0) {
			location = otherRoom;
			thingLookingAt = null;
			return true;
		} else if (location.getDoor(otherRoom) == 1) {
			thingLookingAt = otherRoom;
			return false;
		}
		else return false;
	}

	/**
	 * asks the player to pick up an item
	 * @param item the item to pick up
	 * @return if the player can pick up the item 
	 */
	public boolean pickUp(Thing item) {
		if (item.canPickUp()) {
			Thing itemToRemove = location.removeThingInRoom(item);
			if (itemToRemove!=null) {
				inventory.add(item);
				thingLookingAt = null;
				return true;
			}
		}
		return false;
	}

	/**
	 * asks the player to drop the item
	 * @param item the item to drop
	 */
	public void drop(Thing item) {
		inventory.remove(item);
		thingLookingAt = null;
	}

	/**
	 * asks the player to look at an item
	 * @param item the item to look at
	 * @return the description of the item looked at
	 */
	public String lookAt(Object item) {
		thingLookingAt = item;
		return ((Thing)item).getDescription();
	}

	/**
	 * asks the player to use the item
	 * @param item the item to use
	 * @return if the player can use the item
	 */
	public boolean use(Thing item) {
		if (item.canUse()) {
			if (item instanceof Hammer) {
				boolean result = ((Hammer)item).useItem(thingLookingAt);
				if (result) {
					location.addThingToRoom(((Barrel)(thingLookingAt)).pickKey());
					location.removeThingInRoom((Barrel)(thingLookingAt));
					location.addThingToRoom(brokenBarrel);
				}
				return result;
			}
			else if (item instanceof Key)
				return ((Key)item).useItem(location, thingLookingAt);
		}
		return false;
	}

	/**
	 * getter method for all the items the player carries
	 * @return the list of items the player carries
	 */
	public ArrayList<Thing> getInventory() {
		return inventory;
	}

	/**
	 * getter method for the current location of the player
	 * @return the room where the player is in
	 */
	public Room getLocation () { return location; }
	
	/**
	 * checks if the player is carrying anything
	 * @return if the player carries anything
	 */
	public boolean carryingItems() { return inventory.size() != 0; }


}

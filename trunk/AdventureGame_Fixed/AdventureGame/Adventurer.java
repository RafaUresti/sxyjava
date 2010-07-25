/**
 * Adventurer Class: The player
 * @author Xiaoyi Sheng
 * @author Mai Irie
 */

import java.util.*;

public class Adventurer {
	private Room location;
	private ArrayList<Thing> inventory;
	private Object thingLookingAt;
	/**
	 * Constructor for the Adventurer Player
	 * @param thisRoom The starting location of the adventurer
	 */
	public Adventurer(Room thisRoom) {
		location = thisRoom;
		inventory = new ArrayList<Thing>();
		thingLookingAt = null;
	}
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

	public void drop(Thing item) {
		inventory.remove(item);
		thingLookingAt = null;
//			if (itemToRemove!=null) {
//				inventory.add(item);
//				thingLookingAt = null;
//				return true;
//			}
//		return false;
	}

//	public boolean removeThingFromPlayer(Thing item) {
////		if (inventory.isEmpty()) {
////			return null;
////		}
//
////		Thing toRemove = findThingInRoom(item);
////		if (toRemove != null) {
////			if (item instanceof Key) {				
////				if (((Key) item).isAvailable()) {
////					roomContents.remove(toRemove);
////					return toRemove;
////				}
////			}
//			return inventory.remove(item);
////			return toRemove;
////		} else {
////			return null;
////		}
//	}

	public String lookAt(Object item) {
		thingLookingAt = item;
//		if (item instanceof Room)
//		return ((Room)item).getDescription();
		return ((Thing)item).getDescription();
	}

	public boolean use(Thing item) {
//		if (inventory.contains(item))//XXX
		if (item.canUse()) {
			if (item instanceof Hammer) {
				boolean result = ((Hammer)item).useItem(thingLookingAt);
				if (result) {
					location.addThingToRoom(((Barrel)(thingLookingAt)).pickKey());
				}
				return result;
			}
			else if (item instanceof Key)
					return ((Key)item).useItem(location, thingLookingAt);
		}
		return false;
	}

	public ArrayList<Thing> getInventory() {
		return inventory;
	}
	
	public Room getLocation () { return location; }

	public boolean carryingItems() { return inventory.size() != 0; }

	
}

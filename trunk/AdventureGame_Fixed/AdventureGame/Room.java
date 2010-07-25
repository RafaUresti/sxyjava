/**
 * Room Class
 * @author Mai Irie
 * @author Xiaoyi Sheng
 *
 */

import java.util.ArrayList;

public class Room {

	/* A Room has:
	 * A name
	 * A description, possibly several sentences long
	 * Contents: Things in the room >> There is no limit to 
	 * the number if items in the room
	 * Exits paths to other rooms (North, South, East, West)
	 */

	private String name;
	private String description;
	private ArrayList <Thing> roomContents;
	private Room [ ] connectedRooms;
	private int [ ]doors;
	/**
	 * Constructor for a Room
	 * @param name The name of this room
	 * @param description The description of this room
	 */
	public Room(String name, String description) {
		this.name = name;
		this.description = description;
		roomContents = new ArrayList<Thing>();
		connectedRooms = new Room [4];
		doors = new int[]{-1,-1,-1,-1};  //representing N, E, S, W
	}

	/**
	 * Returns the name of this room
	 * @return The name of this room
	 */
	public String getName() {  return name; }

	/**
	 * Returns the description of this room
	 * @return This room's description
	 */
	public String getDescription() {  
		
		return description; 
	}

	/**
	 * Add an item of type Thing to this room
	 * Does not allow multiple occurrences of the same Thing object 
	 * @param item The item being added to this room
	 * @return True if adding that Thing was successful, otherwise False.
	 */
	public boolean addThingToRoom(Thing item) {  
		if (item != null ) {
			if (findThingInRoom(item) == null) {
				roomContents.add(item); 
				return true;
			}
		}
		return false;
	}
	
	public int getDoor(Room thatRoom) {
		for (int i = 0; i<connectedRooms.length; i++)
			if (thatRoom == connectedRooms[i])
				return doors[i];
		return -1;
	}
	/**
	 * Finds a particular item in this Room.
	 * @param item The Thing object being looked for
	 * @return The first occurrence of the specific Thing object.
	 * 			If there is no such object in the room, returns null.
	 */
	public Thing findThingInRoom(Thing item) {
		if (item == null || roomContents.isEmpty()) {
			return null;
		}

		int index = roomContents.indexOf(item);
		if (index == -1) {
			return null;
		} else {
			return roomContents.get(index);
		}
	}

	/**
	 * Removes a particular item from this Room
	 * @param item The item trying to remove
	 * @return If the remove was successful, returns the removed item. Otherwise, returns null
	 * (There was no such item in the room to remove).
	 */
	public Thing removeThingInRoom(Thing item) {
		if (roomContents.isEmpty()) {
			return null;
		}

		Thing toRemove = findThingInRoom(item);
		if (toRemove != null) {
			if (item instanceof Key) {				
				if (((Key) item).isAvailable()) {
					roomContents.remove(toRemove);
					return toRemove;
				}
			}
			roomContents.remove(toRemove);
			return toRemove;
		} else {
			return null;
		}
	}

	/**
	 * Establishes a two-way connection between rooms
	 * @param thatRoom The room that this room connects to
	 * @param direction The direction that the connecting room
	 * 					is located relative to this room
	 * @param specify whether the door is locked or not
	 */
	public void connectRooms(Room thatRoom, int direction, boolean lockedOrNot) {
		if (connectedRooms[direction] == thatRoom) {
			return;
		} else {
			connectedRooms[direction] = thatRoom;
			int oppositeDirection = (direction + (connectedRooms.length/2)) % connectedRooms.length;

			thatRoom.connectedRooms[oppositeDirection] = this;
			if (lockedOrNot) {
				thatRoom.doors[oppositeDirection] = 1;
				doors[direction] = 1;
			}
			else {
				thatRoom.doors[oppositeDirection] = 0;
				doors[direction] = 0;
			}
		}
	}
	public int[] getDoors() {
		return doors;
	}
	public void openDoor(int i) {
		doors[i] = 0;
	}
	public Room[] getConnectedRooms() {
		return connectedRooms;
	}
	
	public Room getDirectionRoom(int direction) {
		return connectedRooms[direction];
	}
	
	public ArrayList<Thing> getRoomContents() { return roomContents; }

}

package cit009_AdventureGame;
/**
 * Tester Class for the Room Class
 * @author Mai Irie
 * @author sxycode
 */

import junit.framework.TestCase;

public class RoomTest extends TestCase {

	// Variables
	private Room room;
	// TODO add vars for testing other methods
	
	
	protected void setUp() throws Exception {
		super.setUp();
		room = new Room("Library", "This is a library");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTestRoom() { // TODO Finish constructor test
		room = new Room("Library", "This is a library");
		assertTrue("Library".equals(room.getName()));
		assertTrue("This is a library".equals(room.getDescription()));
		
		Thing item = new Gold("Gold", "Some gold");
		room.addThingToRoom(item);
		assertTrue(item == room.removeThingInRoom(item));
		assertTrue(null == room.removeThingInRoom(item));

		// Check for rooms....?
	}

	public void testTestGetName() {
		assertTrue("Library".equals(room.getName()));
		room = new Room("Boo", "Right here");
		assertTrue("Boo".equals(room.getName()));
	}

	public void testTestGetDescription() {
		assertTrue("This is a library".equals(room.getDescription()));
		room = new Room("Boo", "Right here");
		assertTrue("Right here".equals(room.getDescription()));	}

	public void testTestAddThingToRoom() {
		Thing item = null;
		assertFalse(room.addThingToRoom(item));
		assertTrue(room.findThingInRoom(item) == null);
		
		item = new Gold ("Gold", "Some gold");
		assertTrue(room.addThingToRoom(item));
		assertTrue(room.findThingInRoom(item) == item);
		assertFalse(room.addThingToRoom(item));
		
		Thing removedItem = room.removeThingInRoom(item);
		assertTrue( removedItem == item);
		assertTrue(room.findThingInRoom(item) == null);
		removedItem = room.removeThingInRoom(item);
		assertNull(removedItem);
	}

	public void testTestFindThingInRoom() {
		Thing item = null;
		assertNull(room.findThingInRoom(item));

		item = new Gold ("Gold", "Some gold");
		Gold item2 = new Gold("Gold2", "Some more gold");	
		room.addThingToRoom(item);
		room.addThingToRoom(item2);
		
		assertTrue(room.findThingInRoom(item) == item);
		assertTrue(room.findThingInRoom(item2) == item2);
		
		assertTrue(item == room.removeThingInRoom(item));
		assertNull(room.removeThingInRoom(item));
		assertTrue(item2 == room.removeThingInRoom(item2));
		assertNull(room.removeThingInRoom(item2));
	}

	public void testTestRemoveThingInRoom() {
		Thing item = null;
		assertNull(room.removeThingInRoom(item));
		
		item = new Gold ("Gold", "Some gold");
		room.addThingToRoom(item);
		
		assertTrue(item == room.removeThingInRoom(item));
		assertNull(room.removeThingInRoom(item));		
	}

	public void testTestConnectRooms() { // How to TEST THIS? TODO using a model object 
		
	}

}

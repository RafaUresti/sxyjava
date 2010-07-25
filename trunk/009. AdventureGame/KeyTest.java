/**
 * Key Class Test
 * @author Mai Irie
 * @author sxycode
 */

import junit.framework.TestCase;


public class KeyTest extends TestCase {

	private Thing item;
	
	protected void setUp() throws Exception {
		super.setUp();
		item = new Key("Key", "A key");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUseItem() {
		Room room1 = new Room("","");
		Room room2 = new Room("","");
		Room room3 =  new Room("","");
		Barrel barrel = new Barrel("","");
		room1.connectRooms(room2,0, true);
		room1.connectRooms(room3,0,false);
		assertEquals(room2.getDoor(room1), 1);
		System.out.println(room1.getDoor(room2));
		assertEquals(room1.getDoor(room3), 0);
		assertFalse(((Key) item).useItem(room1, barrel));
		assertTrue(((Key) item).useItem(room1, room2));
		assertEquals(room2.getDoor(room1),0);
		System.out.println(room1.getDoor(room2));
		System.out.println(room1.getDoor(room3));

	}

	public void testKey() {
		
		assertTrue("Key".equals(item.getName()));
		assertTrue("A key".equals(item.getDescription()));
		assertTrue(item.canPickUp());
		assertTrue(item.canUse());
		assertFalse(((Key) item).isAvailable());	
		}

	public void testSetAvailable() {
		assertFalse(((Key) item).isAvailable());
		
		((Key) item).setAvailable();
		assertTrue(((Key) item).isAvailable());		
		}
}

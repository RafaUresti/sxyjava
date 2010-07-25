/**
 * Door Class Test
 * @author Mai Irie
 * @author Xiaoyi Sheng
 */

import junit.framework.TestCase;


public class DoorTest extends TestCase {

	private Thing item;
	
	protected void setUp() throws Exception {
		super.setUp();
		item = new Door("Door", "A door");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUseItem() {
		Thing thing2 = null;
		assertFalse(item.useItem(thing2));
		
		thing2 = new Gold("Gold", "some gold");
		assertFalse(item.useItem(thing2));
	}

	public void testDoor() {
		assertTrue("Door".equals(item.getName()));
		assertTrue("A door".equals(item.getDescription()));
		assertFalse(item.canPickUp());
		assertFalse(item.canUse());
		assertFalse(((Door) item).getStatus());
	}

	public void testGetAndSetStatus() {
		assertFalse(((Door) item).getStatus());
		
		((Door) item).setStatus(true);
		assertTrue(((Door) item).getStatus());	
		}
}

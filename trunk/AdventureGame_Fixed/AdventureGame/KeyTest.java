/**
 * Key Class Test
 * @author Mai Irie
 * @author Xiaoyi Sheng
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
		Thing uninitialized = null;
		Thing hammer = new Hammer("Hammer", "Another hammer");
//		Thing door = new Door("Door", "A door");
//		
//		assertFalse(item.useItem(uninitialized));
//		assertFalse(item.useItem(hammer));
//		
//		assertFalse(((Door) door).getStatus());
//		assertFalse(item.useItem(door));
//		
//		((Key) item).setStatus(true);
//		assertTrue(item.useItem(door));
//		assertTrue(((Door) door).getStatus());
//		
//		assertTrue(item.useItem(door));
//		assertFalse(((Door) door).getStatus());
	}

	public void testKey() {
		assertTrue("Key".equals(item.getName()));
		assertTrue("A key".equals(item.getDescription()));
		assertTrue(item.canPickUp());
		assertTrue(item.canUse());
		assertFalse(((Key) item).getStatus());	
		}

	public void testGetAndSetStatus() {
		assertFalse(((Key) item).getStatus());
		
		((Key) item).setStatus(true);
		assertTrue(((Key) item).getStatus());		
		}
}

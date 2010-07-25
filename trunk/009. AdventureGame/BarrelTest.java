/**
 * Barrel Test Class
 * @author Mai Irie
 * @author sxycode
 */

import junit.framework.TestCase;


public class BarrelTest extends TestCase {

	private Thing item;
	Key key;
	
	protected void setUp() throws Exception {
		super.setUp();
		key = new Key("Key", "A key");
		item = new Barrel("Barrel", "A barrel", key);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBarrel() {
		assertTrue("Barrel".equals(item.getName()));
		assertTrue("A barrel".equals(item.getDescription()));
		assertFalse(item.canPickUp());
		assertFalse(item.canUse());
		assertFalse(((Barrel) item).getStatus());
	}

//	public void testUseItem() {
//		Thing thing2 = null;
//		assertFalse(item.useItem(thing2));
//		
//		thing2 = new Gold("Gold", "some gold");
//		assertFalse(item.useItem(thing2));
//	}
	
	public void testGetAndSetStatus() {
		assertFalse(((Barrel) item).getStatus());
		
		((Barrel) item).breakBarrel();
		assertTrue(((Barrel) item).getStatus());
	}
}

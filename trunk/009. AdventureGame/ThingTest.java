/**
 * Tester for Thing Class
 * @author Mai Irie
 * @author sxycode
 */

import junit.framework.TestCase;


public class ThingTest extends TestCase {

	private Thing item;

	protected void setUp() throws Exception {
		super.setUp();
		item = new Gold("Gold", "Some Gold");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetName() {
		assertTrue("Gold".equals(item.getName()));

		item = new Key("Key", "Some key");
		assertTrue("Key".equals(item.getName()));		
	}

	public void testGetDescription() {
		assertTrue("Some Gold".equals(item.getDescription()));

		item = new Key("Key", "Some key");
		assertTrue("Some key".equals(item.getDescription()));		
	}

	public void testCanUse() {
		assertFalse(item.canUse());

		item = new Key("Key", "Some key");
		assertTrue(item.canUse());
	}

	public void testCanPickUp() {
		assertTrue(item.canPickUp());
	}
}

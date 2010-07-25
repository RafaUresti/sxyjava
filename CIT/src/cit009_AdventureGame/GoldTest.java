package cit009_AdventureGame;
/**
 * Gold Class Tester
 * @author Mai Irie
 * @author sxycode
 */

import junit.framework.TestCase;


public class GoldTest extends TestCase {

	private Thing item;
	
	protected void setUp() throws Exception {
		super.setUp();
		item = new Gold("Gold", "Some gold");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

//	public void testUseItem() {
//		Thing thing2 = null;
//		assertFalse(item.useItem(thing2));
//		
//		thing2 = new Gold("Gold", "some gold");
//		assertFalse(item.useItem(thing2));	}

	public void testGold() {
		assertTrue("Gold".equals(item.getName()));
		assertTrue("Some gold".equals(item.getDescription()));
		assertTrue(item.canPickUp());
		assertFalse(item.canUse());
	}
}

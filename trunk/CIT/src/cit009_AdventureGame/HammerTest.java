package cit009_AdventureGame;
/**
 * Hammer Class Tester
 * @author Mai Irie
 * @author sxycode
 */

import junit.framework.TestCase;


public class HammerTest extends TestCase {

	private Thing item;
	
	protected void setUp() throws Exception {
		super.setUp();
		item = new Hammer("Hammer", "A hammer");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUseItem() {
		Thing uninitialized = null;
		Thing hammer = new Hammer("Hammer", "Another hammer");
		Key key = new Key("Key", "A key");
		Thing barrel = new Barrel("Barrel", "A barrel", key);
		
		Hammer item2 = new Hammer("Hammer", "A hammer");
		
		assertFalse(item2.useItem(uninitialized));
		assertFalse(item2.useItem(hammer));
		
		assertFalse(((Barrel) barrel).getStatus());
		assertTrue(item2.useItem(barrel));
		assertTrue(((Barrel) barrel).getStatus());
		
		assertFalse(item2.useItem(barrel));
		assertTrue(((Barrel) barrel).getStatus());
	}

	public void testHammer() {
		assertTrue("Hammer".equals(item.getName()));
		assertTrue("A hammer".equals(item.getDescription()));
		assertTrue(item.canPickUp());
		assertTrue(item.canUse());
	}

}

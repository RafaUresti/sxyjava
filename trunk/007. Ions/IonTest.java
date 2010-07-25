import junit.framework.TestCase;

public class IonTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testIon() {
		Ion ion=new Ion(2.0,2.0);
		assertNotNull(ion);
	}

	public void testSetX() {
		Ion ion=new Ion(2.0,2.0);
		assertEquals(ion.getX(),2.0,0.001);
		ion.setX(3.0);
		assertEquals(ion.getX(),3.0,0.001);
	}

	public void testSetY() {
		Ion ion=new Ion(2.0,2.0);
		assertEquals(ion.getY(),2.0,0.001);
		ion.setY(3.0);
		assertEquals(ion.getY(),3.0,0.001);
	}

	public void testSetXandY() {
		Ion ion=new Ion(2.0,2.0);
		assertEquals(ion.getX(),2.0,0.001);
		assertEquals(ion.getY(),2.0,0.001);
		ion.setXandY(3.0,4.0);
		assertEquals(ion.getX(),3.0,0.001);
		assertEquals(ion.getY(),4.0,0.001);
	}

	public void testGetX() {
		Ion ion=new Ion(2.0,2.0);
		assertEquals(ion.getX(),2.0,0.001);
	}

	public void testGetY() {
		Ion ion=new Ion(2.0,2.0);
		assertEquals(ion.getY(),2.0,0.001);
	}

}

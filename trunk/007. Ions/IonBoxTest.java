import junit.framework.TestCase;


public class IonBoxTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testIonBox() {
		IonBox ib=new IonBox(5,5);
		assertNotNull(ib);
	}

	public void testPutIon() {
		IonBox ib=new IonBox(4,4);
		Ion ion1=new Ion(1,1);
		ib.putIon(ion1);
		assertEquals(1,ib.myIonList.get(0).getX(),0.0001);
		assertEquals(1,ib.myIonList.get(0).getY(),0.0001);
		Ion ion2=new Ion(2,2);
		ib.putIon(ion2);
		assertEquals(0,ib.myIonList.get(0).getX(),0.001);
		assertEquals(0,ib.myIonList.get(0).getY(),0.001);
		assertEquals(4,ib.myIonList.get(1).getX(),0.001);
		assertEquals(4,ib.myIonList.get(1).getY(),0.001);
		Ion ion3=new Ion(1,1);
		ib.putIon(ion3);
		assertEquals(0,ib.myIonList.get(0).getX(),0.001);
		assertEquals(0,ib.myIonList.get(0).getY(),0.001);
		assertEquals(4,ib.myIonList.get(1).getX(),0.001);
		assertEquals(4,ib.myIonList.get(1).getY(),0.001);
		assertEquals(2,ib.myIonList.get(2).getX(),0.001);
		assertEquals(2,ib.myIonList.get(2).getY(),0.001);
	}

	public void testPutFixedIon() {
		IonBox ib=new IonBox(4,4);
		Ion ion1=new Ion(1,1);
		ib.putIon(ion1);
		assertEquals(1,ib.myIonList.get(0).getX(),0.0001);
		assertEquals(1,ib.myIonList.get(0).getY(),0.0001);
		Ion ion2=new Ion(2,2);
		ib.putFixedIon(ion2);
		assertEquals(0,ib.myIonList.get(0).getX(),0.0001);
		assertEquals(0,ib.myIonList.get(0).getY(),0.0001);
		assertEquals(2,ib.myIonList.get(1).getX(),0.0001);
		assertEquals(2,ib.myIonList.get(1).getY(),0.0001);
	}

	public void testRemoveIon() {
		IonBox ib=new IonBox(4,4);
		Ion ion1=new Ion(1,1);
		ib.putIon(ion1);
		Ion ion2=new Ion(2,2);
		ib.putIon(ion2);
		Ion ion3=new Ion(2,2);
		ib.removeIon(ion3);
		assertEquals(0,ib.myIonList.get(0).getX(),0.0001);
		assertEquals(0,ib.myIonList.get(0).getY(),0.0001);
		assertEquals(4,ib.myIonList.get(1).getX(),0.0001);
		assertEquals(4,ib.myIonList.get(1).getY(),0.0001);
		assertEquals(2,ib.myIonList.size());
	}

}

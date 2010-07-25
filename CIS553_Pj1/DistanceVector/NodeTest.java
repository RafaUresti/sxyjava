import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;


public class NodeTest {

	@Test
	public void testBuildRoutingTable() {
		try {
			Node node = new Node(new Simulator(0, null), 0);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

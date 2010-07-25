import junit.framework.TestCase;
import java.util.Arrays;


public class EncoderTest extends TestCase {

	public void testEncoder() {
		Encoder encoder = new Encoder();
		String test = encoder.encode("Ab cd");
		assertNotNull (encoder);
		assertFalse("Ab cd".equals(test));
	}

	public void testEncoderString() {
		Encoder encoder = new Encoder("qwertyuiopasdfghjklzxcvbnm");
		assertNotNull (encoder);
		String test = encoder.encode("Ab cd");
		assertTrue("Qw er".equals(test));
	}

	public void testEncodeStringArray() {
		Encoder encoder = new Encoder("qwertyuiopasdfghjklzxcvbnm");
		String[] testArray = {"ab Cd","Ef'gh-"};
		String[] test = encoder.encode(testArray);
		String[] codedTestArray = {"qw Er", "Ty'ui-"};
		Arrays.equals(codedTestArray, test);
	}

	public void testDecodeString() {
		Encoder encoder = new Encoder("qwertyuiopasdfghjklzxcvbnm");
		String test = encoder.decode("Qw er");
		assertTrue("Ab cd".equals(test));
	}

	public void testDecodeStringArray() {
		Encoder encoder = new Encoder("qwertyuiopasdfghjklzxcvbnm");
		String[] testArray = {"ab Cd","Ef'gh-"};
		String[] test = encoder.decode(testArray);
		String[] decodedTestArray = {"qw Er", "Ty'ui-"};
		Arrays.equals(decodedTestArray, test);
	}

	public void testChange() {
		Encoder encoder = new Encoder("qwertyuiopasdfghjklzxcvbnm");
		encoder.change('a', 'w');
		String test = encoder.encode("Ab cd");
		System.out.print(test);
		assertTrue("Wq er".equals(test));
	}
}

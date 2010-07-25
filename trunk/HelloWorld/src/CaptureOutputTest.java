import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

public class CaptureOutputTest extends TestCase {
    CaptureOutput capture;

    protected void setUp() throws Exception {
        super.setUp();
        capture = new CaptureOutput();
    }

    // test methods go here
    public final void testMyPrint() {
        // Prepare to capture output
        PrintStream originalOut = System.out;        
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        
        // Perform tests
        capture.myPrint("Hello, output!");
        assertEquals("Hello, output!", os.toString());

        // Restore normal operation
        System.setOut(originalOut);
    }


}


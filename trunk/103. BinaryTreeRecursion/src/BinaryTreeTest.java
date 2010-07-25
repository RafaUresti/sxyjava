
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author David Matuszek
 * @author sxycode
 * @version Feb. 7th, 2008
 *
 */
public class BinaryTreeTest {

    BinaryTree<String> leafA, leafB, leafC, nodeAB, rootABC;
    BinaryTree<String> leafB2, rootAB;
    String stringA = "A";
    String stringB = "B";
    String stringB2 = new String("B");
    String stringC = "C";
    String stringAB = "AB";
    String stringAB2 = stringA + stringB;
    String stringABC = "ABC";

    /**
     * Constructor for BinaryTreeTest.
     * @param arg0
     */
    public BinaryTreeTest(String message) {
        System.out.println(message);
    }

    public BinaryTreeTest() {
    }

    /*
     * Constructs the following trees:<pre>
     * 
     *               rootABC:stringABC
     *                 /       \
     *                /         \
     *     nodeAB:stringAB    leafC:stringC         rootAB:stringAB2
     *           /     \                              /        \
     *          /       \                            /          \
     * leafA:stringA  leafB:stringB         leafA:stringA    leafB2:stringB2
     * 
     * </pre>
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        leafA = new BinaryTree<String>(stringA);
        leafB = new BinaryTree<String>(stringB);
        leafC = new BinaryTree<String>(stringC, null, null);
        leafB2 = new BinaryTree<String>(stringB2);
        nodeAB = new BinaryTree<String>(stringAB, leafA, leafB);
        rootABC = new BinaryTree<String>(stringABC, nodeAB, leafC);
        rootAB = new BinaryTree<String>(stringAB2, leafA, leafB2);
        assertEquals("A", leafA.value);
        assertEquals("AB", rootAB.value);
    }

    @Test
    public void testGetLeftChild() {
        BinaryTree t = rootABC.getLeftChild();
        assertEquals(nodeAB, t);
        assertEquals(rootAB, t);
        assertSame(t, nodeAB);
        assertNotSame(t, rootAB);
        assertEquals(null, leafA.getLeftChild());
    }

    @Test
    public void testGetRightChild() {
        assertEquals(leafC, rootABC.getRightChild());
        assertEquals(leafB, rootABC.getLeftChild().getRightChild());
        assertEquals(null, rootABC.getRightChild().getRightChild());
    }

    @Test
    public void testSetLeftChild() {
        BinaryTree leafD = new BinaryTree("D");
        leafA.setLeftChild(leafD);
        assertEquals(leafD, leafA.getLeftChild());
        try {
            leafB2.setLeftChild(rootAB);
            fail("Failed loop test");
        } catch (IllegalArgumentException e) {
        }
        leafB2.setLeftChild(rootABC); // should not throw exception
        assertEquals(leafB2.getLeftChild(), rootABC);
    }

    @Test
    public void testSetRightChild() {
        BinaryTree leafD = new BinaryTree("D");
        leafC.setRightChild(leafD);
        assertEquals(leafD, leafC.getRightChild());
        try {
            leafB.setRightChild(nodeAB);
            fail("Failed loop test");
        } catch (IllegalArgumentException e) {
        }
        leafB2.setRightChild(rootABC); // should not throw exception
        assertEquals(leafB2.getRightChild(), rootABC);
    }

    @Test
    public void testSetAndGetValue() {
        nodeAB.setValue("Test value");
        assertEquals("Test value", nodeAB.getValue());
    }

    @Test
    public void testIsLeaf() {
        assertTrue(leafA.isLeaf());
        assertFalse(nodeAB.isLeaf());
    }

    /*
     * Test for boolean equals(Object)
     */
    @Test
    public void testEqualsObject() {
        assertTrue(nodeAB.equals(rootAB));
        assertTrue(rootAB.equals(nodeAB));
        assertNotSame(rootAB, nodeAB);
        assertNull(leafA.getRightChild());
    }

    @Test
    public void testToString() {
        assertEquals("ABC (AB (A, B), C)", rootABC.toString());
        BinaryTree root = makeTreeWithMissingChildren();
        assertEquals("A (B (D, null), C (null, E))", root.toString());
    }

    private BinaryTree<String> makeTreeWithMissingChildren() {
        BinaryTree<String> root, left, right;
        left = new BinaryTree("D");
        left = new BinaryTree("B", left, null);
        right = new BinaryTree("E");
        right = new BinaryTree("C", null, right);
        root = new BinaryTree("A", left, right);
        return root;
    }

    @Test
    public void testHashCode() {
        BinaryTree first = makeTreeWithMissingChildren();
        BinaryTree second = makeTreeWithMissingChildren();
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void testLeftmostDescendant() {
        assertSame(rootABC.leftmostDescendant(), leafA);
        assertSame(leafC.leftmostDescendant(), leafC);
        assertSame(rootAB.leftmostDescendant(), leafA);
    }

    @Test
    public void testRightmostDescendant() {
        assertSame(rootABC.rightmostDescendant(), leafC);
        assertSame(leafC.rightmostDescendant(), leafC);
        assertSame(rootAB.rightmostDescendant(), leafB2);
        assertSame(nodeAB.rightmostDescendant(), leafB);
    }

    @Test
    public void testNumberOfNodes() {
        assertEquals(rootAB.numberOfNodes(), 3);
        assertEquals(rootABC.numberOfNodes(), 5);
        assertEquals(leafC.numberOfNodes(), 1);
        assertEquals(nodeAB.numberOfNodes(), 3);
    }

    @Test
    public void testDepth() {
        assertEquals(rootAB.depth(), 1);
        assertEquals(rootABC.depth(), 2);
        assertEquals(nodeAB.depth(), 1);
        assertEquals(leafB.depth(), 0);
    }

    @Test
    public void testContainsEqualValue() {
        assertTrue(rootABC.containsEqualValue(leafB2.value));
        assertTrue(nodeAB.containsEqualValue(leafB2.value));
        assertTrue(rootABC.containsEqualValue(rootAB.value));
        assertTrue(rootAB.containsEqualValue(leafA.value));
        assertFalse(rootAB.containsEqualValue(leafC.value));
    }

    @Test
    public void testContainsSameValue() {
        assertTrue(rootABC.containsSameValue(leafA.value));
        assertFalse(rootABC.containsSameValue(leafB2.value));
    }

    @Test
    public void testLeaves() {
        Set<BinaryTree<String>> leafSet = new HashSet<BinaryTree<String>>();
        leafSet.add(leafA);
        leafSet.add(leafB);
        leafSet.add(leafC);
        assertEquals(leafSet, rootABC.leaves());
        leafSet = new HashSet<BinaryTree<String>>();
        leafSet.add(leafA);
        leafSet.add(leafB2);
        assertEquals(leafSet, rootAB.leaves());
    }

    @Test
    public void testValuesOf() {
        Set<String> valueSet = new HashSet<String>();
        valueSet.add(rootABC.value);
        valueSet.add(nodeAB.value);
        valueSet.add(leafA.value);
        valueSet.add(leafB.value);
        valueSet.add(leafC.value);
        assertEquals(valueSet, rootABC.valuesOf());
        valueSet = new HashSet<String>();
        valueSet.add(rootAB.value);
        valueSet.add(leafA.value);
        valueSet.add(leafB2.value);
        assertEquals(valueSet, rootAB.valuesOf());
    }

    @Test
    public void testFringe() {
        List<String> fringeList = new ArrayList<String>();
        fringeList.add(leafA.value);
        fringeList.add(leafB.value);
        fringeList.add(leafC.value);
        assertEquals(fringeList, rootABC.fringe());
        fringeList = new ArrayList<String>();
        fringeList.add(leafA.value);
        fringeList.add(leafB2.value);
        assertEquals(fringeList, rootAB.fringe());
    }

    @Test
    public void testCopy() {
        assertEquals(rootABC, rootABC.copy());
        assertNotSame(rootABC, rootABC.copy());
        assertEquals(rootAB, rootAB.copy());
        assertNotSame(rootAB, rootAB.copy());
    }

    @Test
    public void testReverse() {
        BinaryTree<String> reverseTree = rootABC.reverse();
        assertSame(reverseTree.value, rootABC.value);
        assertEquals(reverseTree.getLeftChild(), leafC);
        assertNotSame(reverseTree.getLeftChild(), leafC);
        assertSame(reverseTree.getRightChild().value, nodeAB.value);
        assertFalse(reverseTree.getRightChild().equals(nodeAB));
        assertEquals(reverseTree.getRightChild().getLeftChild(), leafB);
        assertNotSame(reverseTree.getRightChild().getLeftChild(), leafB);
        assertEquals(reverseTree.getRightChild().getRightChild(), leafA);
        assertNotSame(reverseTree.getRightChild().getRightChild(), leafA);
    }

    @Test
    public void reverseInPlace() {
        rootAB.reverseInPlace();
        assertSame(rootAB.value, stringAB2);
        assertSame(rootAB.getRightChild(), leafA);
        assertSame(rootAB.getLeftChild(), leafB2);
        rootABC.reverseInPlace();
        assertSame(rootABC.value, stringABC);
        assertSame(rootABC.getRightChild(), nodeAB);
        assertSame(rootABC.getLeftChild(), leafC);
        assertSame(rootABC.getRightChild().getLeftChild(), leafB);
        assertSame(rootABC.getRightChild().getRightChild(), leafA);
    }

    /**
     * In general we do not want anything printed out when we run
     * our tests. This class can be run as a JUnit test, in which
     * case this method will not be called, or as an application,
     * in which case this method <i>will</i> be called.
     * <p>
     * The only reason to run this class as an application is to
     * test the print() method in BinaryTree; the results have to
     * be evaluated by hand.
     * <p>
     * Note that we could write a print method that took an output
     * stream as a parameter; this would allow JUnit testing.
     * 
     * @param args Not used.
     */
    public static void main(String[] args) {
        BinaryTreeTest test = new BinaryTreeTest("Method used only for testing print()");
        try {
            test.setUp();
            test.rootABC.print();
            System.out.println("--------------");
            test.makeTreeWithMissingChildren().print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

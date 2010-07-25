package tree;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Tree class.
 * @author Xiaoyi Sheng
 * @version 3-24-2008
 */
public class TreeTest {

	private static final String EXAMPLE_TREE_DESCRIPTION = "one (two three (four five(six seven eight)) )";
	Tree<String> leaf1, leaf2, leaf3, leaf4, leaf5, leaf6, leaf7, leaf8,
	root, node1, node2, node3;
	Tree<String> leaf11, leaf12, root2;
	Tree<String> leaf21, leaf22, root3;
	String l1 = "How", l2 = "are", l3 = "you", l4 = "doing", l5 = "?", 
	l6 = "Wonderful", l7 = "!", r ="Greeting", n1 = "first", n2 = "second", n3 = "third";
	String l11 = "ARE", l12 = "YOU", r2 = "WHO";
	String l21 = l2.toUpperCase(), l22 = l3.toUpperCase(), temp = "W", r3 = temp + "HO";
	/**
	 * Constructs the following trees:<pre>
	 * 
	 *                     root:r                                root2:r2                  root3:r3
	 *                   /    \     \                            /   \                      /    \
	 *                  /      \     \                          /     \                    /      \
	 *           node1:n1  node2:n2  node3: n3            leaf11:l11  12:l12         leaf21:l21  22:l22
	 *         /         / /   \   \    \   \     
	 *        /         / /     \   \    \   \    
	 * leaf1:l1    2:l2 3:l3 4:l4 5:l5 6:l6  7:l7
	 * 
	 * </pre>
	 */

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		leaf1 = new Tree<String>(l1);
		leaf2 = new Tree<String>(l2);
		leaf3 = new Tree<String>(l3);
		leaf4 = new Tree<String>(l4);
		leaf5 = new Tree<String>(l5);
		leaf6 = new Tree<String>(l6);
		leaf7 = new Tree<String>(l7);
		node1 = new Tree<String>(n1, leaf1);
		node2 = new Tree<String>(n2, leaf2, leaf3, leaf4, leaf5);
		node3 = new Tree<String>(n3, leaf6, leaf7);
		root = new Tree<String>(r, node1, node2, node3);

		leaf11 = new Tree<String>(l11);
		leaf12 = new Tree<String>(l12);
		root2 = new Tree<String>(r2, leaf11, leaf12);

		leaf21 = new Tree<String>(l21);
		leaf22 = new Tree<String>(l22);
		root3 = new Tree<String>(r3, leaf21, leaf22);
	}

	
	@Test
	public void testGetValue() {
		assertEquals("How", leaf1.getValue());
		assertEquals("Greeting", root.getValue());
	}

	
	@Test
	public void testSetValue() {
		leaf1.setValue("love");
		assertEquals("love", leaf1.getValue());
		root.setValue("fantacy");
		assertEquals("fantacy", root.getValue());
	}

	
	@Test
	public void testAddChild() {
		assertEquals(4, node2.getNumberOfChildren());
		node2.addChild(2, leaf12);
		assertEquals(5, node2.getNumberOfChildren());
		assertSame(leaf2, node2.getChild(0));
		assertSame(leaf3, node2.getChild(1));
		assertSame(leaf12, node2.getChild(2));
		assertSame(leaf4, node2.getChild(3));
		assertSame(leaf5, node2.getChild(4));
		try{
			leaf7.addChild(leaf7.getNumberOfChildren(), node3);
		}
		catch (IllegalArgumentException e){}
		
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void testAddChildren(){
		assertEquals(4, node2.getNumberOfChildren());
		node2.addChildren(leaf11, leaf12);
		assertEquals(6, node2.getNumberOfChildren());
		assertSame(leaf11, node2.getChild(4));
		assertSame(leaf12, node2.getChild(5));
		try{
			leaf7.addChildren(node3,root2);
		}
		catch (IllegalArgumentException e){}
	}

	
	@Test
	public void testGetNumberOfChildren() {
		assertEquals(3, root.getNumberOfChildren());
		assertEquals(4, node2.getNumberOfChildren());
	}

	
	@Test
	public void testGetChild() {
		assertSame(node1, root.getChild(0));
		assertSame(node2, root.getChild(1));
		assertSame(node3, root.getChild(2));
		assertSame(leaf6, node3.getChild(0));
		try{
			assertSame(node3, root.getChild(3));
			fail();
		}
		catch (IndexOutOfBoundsException e){}
	}
	
	@Test
	public void testIterator() {
		Iterator<Tree<String>> i = root.iterator();
		assertTrue(i.next().equals(node1));
		assertSame(i.next(), node2);
		assertSame(i.next(), node3);
	}

	
	@Test
	public void testContains() {
		assertTrue(root.contains(node1));
		assertTrue(root.contains(leaf2));
		assertTrue(node2.contains(leaf2));
		assertFalse(node3.contains(leaf2));
		assertFalse(node1.contains(root));
	}

	@Test
	public void testParse() {
		Tree<String> tree = Tree.parse(EXAMPLE_TREE_DESCRIPTION );
		Tree<String> rOne = exampleTree();
		assertTrue(tree.equals(rOne));
	}

	
	@Test
	public void testEqualsObject() {
		assertNotSame(root2, root3);
		assertTrue(root2.equals(root3));
	}

	@Test
	public void testToString() {
		Tree<String> tree1 = exampleTree();
		Tree<String> tree2 = Tree.parse(EXAMPLE_TREE_DESCRIPTION);
		assertTrue(tree1.toString().equals(tree2.toString()));
		assertTrue("one  (two three  (four five  (six seven eight )))".equals(tree1.toString()));
	}

	@SuppressWarnings("unchecked")
	private Tree<String> exampleTree() {
		Tree<String> lSix =new Tree<String>("six");
		Tree<String> lSeven =new Tree<String>("seven");
		Tree<String> lEight =new Tree<String>("eight");
		Tree<String> lTwo =new Tree<String>("two");
		Tree<String> lFour =new Tree<String>("four");
		Tree<String> nFive =new Tree<String>("five", lSix, lSeven, lEight);
		Tree<String> nThree	=new Tree<String>("three", lFour, nFive);
		Tree<String> rOne =new Tree<String>("one", lTwo, nThree);
		return rOne;
	}
}

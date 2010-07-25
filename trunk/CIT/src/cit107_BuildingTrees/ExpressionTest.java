package cit107_BuildingTrees;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Expression class
 * @author sxycode
 * @version 3-24-2008
 */
public class ExpressionTest {

	Expression treeExpression, otherExpression, e1, e2, e3, e4;
	String treeString = "+ (5 10 -( *(15 20) 25) 30)";
	String otherString = "+ (5 10 -(40 *(15 20) 25) 30)" ;
	

	@Before
	public void setUp() throws Exception {
		treeExpression = new Expression(treeString);
		e1 = new Expression("+ (5 10 15)");
		e2 = new Expression("* (5 10 15)");
		e3 = new Expression("/ (10 5)");
		e4 = new Expression("- (10 5)");
	}

	@Test
	public void testExpression(){
		try{
			otherExpression = new Expression(otherString);
			fail();
		}
		catch(IllegalArgumentException e){}
	}
	
	@Test
	public void testEvaluate() {
		assertEquals(30, e1.evaluate());
		assertEquals(750, e2.evaluate());
		assertEquals(2, e3.evaluate());
		assertEquals(5, e4.evaluate());
		assertEquals(320, treeExpression.evaluate());
	}

	@Test
	public void testToString() {
		assertEquals(e1.toString(), "(5 + 10 + 15)");
		assertEquals(e2.toString(), "(5 * 10 * 15)");
		assertEquals(e3.toString(), "(10 / 5)");
		assertEquals(e4.toString(), "(10 - 5)");
		assertEquals(treeExpression.toString(), "(5 + 10 + ((15 * 20)  - 25)  + 30)");
	}
	
	@Test
	public void stringToNumber() {
		assertEquals(10, Expression.stringToNumber("10"));
		assertEquals(452, Expression.stringToNumber("452"));
	}
}

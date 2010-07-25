package cit110_BugsInterpreterII;
import java.awt.Color;
import java.util.HashMap;

import javax.lang.model.element.UnknownElementException;

import tree.Tree;
import parser.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit tests for methods in Bug class
 * @author sxycode
 * @version 4-15-2008
 *
 */
public class BugTest {
	Parser parser;
	Bug bug;
	Interpreter interpreter;

	@Before
	public void setUp(){
		interpreter = new Interpreter("Bug Fred{\n color white\n}\n");
		bug = new Bug("Sally", null, interpreter);
	}
	@Test
	public void testStore(){
		//TODO more to test
		bug.store("x", 200);
		bug.store("y", 300);
		bug.store("angle", 100);
		bug.variables.put("abc", 321.0);
		assertTrue(Bug.areEqualDoubles(bug.x, 200));
		assertTrue(Bug.areEqualDoubles(bug.y, 300));
		assertTrue(Bug.areEqualDoubles(bug.angle, 100));
		assertTrue(Bug.areEqualDoubles(bug.variables.get("abc"), 321));
		try{
			bug.store("ab",90.0);
			fail();
		}catch (IllegalArgumentException e){}
		HashMap<String, Double> f0 = new HashMap<String, Double>();
		HashMap<String, Double> f1 = new HashMap<String, Double>();
		HashMap<String, Double> f2 = new HashMap<String, Double>();
		f0.put("a0", 33.0);f0.put("b0", 44.0);
		f1.put("a1", 3.0);f1.put("b1", 4.0);
		f2.put("a2", 3.3);f2.put("b2", 4.4);
		bug.scope.push(f0);bug.scope.push(f1);bug.scope.push(f2);
		bug.store("a0", 33.3);
		assertTrue(Bug.areEqualDoubles(bug.fetch("a0"), 33.3));
		interpreter.variables.put("ax", 99.0);
		assertTrue(Bug.areEqualDoubles(bug.fetch("ax"),99.0));
	}
	
	@Test
	public void testFetch(){
		bug.store("x", 200);
		bug.store("y", 300);
		bug.store("angle", 100);
		bug.variables.put("abc", 321.0);
		assertTrue(Bug.areEqualDoubles(bug.fetch("x"), 200));
		assertTrue(Bug.areEqualDoubles(bug.fetch("y"), 300));
		assertTrue(Bug.areEqualDoubles(bug.fetch("angle"), 100));
		assertTrue(Bug.areEqualDoubles(bug.fetch("abc"), 321));
		interpreter.variables.put("g1", 20.0);
		assertTrue(Bug.areEqualDoubles(bug.fetch("g1"), 20.0));
		HashMap<String, Double> f0 = new HashMap<String, Double>();
		HashMap<String, Double> f1 = new HashMap<String, Double>();
		HashMap<String, Double> f2 = new HashMap<String, Double>();
		f0.put("a0", 33.0);f0.put("b0", 44.0);
		f1.put("a1", 3.0);f1.put("b1", 4.0);
		f2.put("a2", 3.3);f2.put("b2", 4.4);
		bug.scope.push(f0);bug.scope.push(f1);bug.scope.push(f2);
		assertTrue(Bug.areEqualDoubles(bug.fetch("a0"), 33.0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("a1"), 3.0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("a2"), 3.3));
		try{
			bug.fetch("a4");
			fail();
		}catch (IllegalArgumentException e){}
	}
	
	@Test
	public void testEvaluatePlus(){
		use ("34.4+56.8");
		assertEvaluateExpressionEquals(91.2);
		use("+45.5");
		assertEvaluateExpressionEquals(45.5);
		use("-25+35");
		assertEvaluateExpressionEquals(10);
		use("34+-23");
		assertEvaluateExpressionEquals(11);
	}
	
	@Test
	public void testEvaluateMinus(){
		use("25.5-12.2");
		assertEvaluateExpressionEquals(13.3);
		use("-23.3");
		assertEvaluateExpressionEquals(-23.3);
		use("+34-+23");
		assertEvaluateExpressionEquals(11);
		use("-34-11");
		assertEvaluateExpressionEquals(-45);
	}

	@Test
	public void testEvaluateMultiply(){
		use("2.3*2");
		assertEvaluateExpressionEquals(4.6);
		use("+2.3*+2");
		assertEvaluateExpressionEquals(4.6);
		use("-2.3*-2");
		assertEvaluateExpressionEquals(4.6);
		use("-2.3*2");
		assertEvaluateExpressionEquals(-4.6);
	}

	@Test
	public void testEvaluateDivide(){
		use("2.4/2");
		assertEvaluateExpressionEquals(1.2);
		use("+2.4/+2");
		assertEvaluateExpressionEquals(1.2);
		use("-2.4/-2");
		assertEvaluateExpressionEquals(1.2);
		use("-2.4/2");
		assertEvaluateExpressionEquals(-1.2);
	}
	
	@Test
	public void testEvaluateLarger(){
		use("4>2");
		assertEvaluateExpressionEquals(1);
		use("2>4");
		assertEvaluateExpressionEquals(0);
		use("4.3+23>24+0.5");
		assertEvaluateExpressionEquals(1);
		use("23>23.0009");
		assertEvaluateExpressionEquals(0);
		use("23.0009>23.0");
		assertEvaluateExpressionEquals(0);
	}
	
	@Test
	public void testEvaluateLargerEqual(){
		use("4>=2");
		assertEvaluateExpressionEquals(1);
		use("2>=4");
		assertEvaluateExpressionEquals(0);
		use("4.3+23>=24+0.5");
		assertEvaluateExpressionEquals(1);
		use("23>=23.0009");
		assertEvaluateExpressionEquals(1);
		use("23.0009>=23.0");
		assertEvaluateExpressionEquals(1);
		use("23>=23.0011");
		assertEvaluateExpressionEquals(0);
		use("22.9999>=23.0");
		assertEvaluateExpressionEquals(1);
		use("22.99>=23.0");
		assertEvaluateExpressionEquals(0);
	}
	
	@Test
	public void testEvaluateSmaller(){
		use("2<4");
		assertEvaluateExpressionEquals(1);
		use("4<2");
		assertEvaluateExpressionEquals(0);
		use("24+0.5<4.3+23");
		assertEvaluateExpressionEquals(1);
		use("23.0009<23");
		assertEvaluateExpressionEquals(0);
		use("23.0<23.0009");
		assertEvaluateExpressionEquals(0);
	}

	@Test
	public void testEvaluateSmallerEqual(){
		use("2<=4");
		assertEvaluateExpressionEquals(1);
		use("4<=2");
		assertEvaluateExpressionEquals(0);
		use("24+0.5<=4.3+23");
		assertEvaluateExpressionEquals(1);
		use("23.0009<=23");
		assertEvaluateExpressionEquals(1);
		use("23.0<=23.0009");
		assertEvaluateExpressionEquals(1);
		use("23.0011<=23");
		assertEvaluateExpressionEquals(0);
		use("23.0<=22.9999");
		assertEvaluateExpressionEquals(1);
		use("23.0<=22.99");
		assertEvaluateExpressionEquals(0);
	}
	
	@Test
	public void testEvaluateEqual(){
		use("4=4");
		assertEvaluateExpressionEquals(1);
		use("4=2");
		assertEvaluateExpressionEquals(0);
		use("24+0.5=1.5+23");
		assertEvaluateExpressionEquals(1);
		use("23.0009=23");
		assertEvaluateExpressionEquals(1);
		use("23.0=23.0009");
		assertEvaluateExpressionEquals(1);
		use("23.0011=23");
		assertEvaluateExpressionEquals(0);
		use("23.0=22.9999");
		assertEvaluateExpressionEquals(1);
		use("23.0=22.99");
		assertEvaluateExpressionEquals(0);
	}
	
	@Test
	public void testEvaluateUnequal(){
		use("4!=2");
		assertEvaluateExpressionEquals(1);
		use("2!=4");
		assertEvaluateExpressionEquals(1);
		use("4!=4");
		assertEvaluateExpressionEquals(0);
		use("24+0.5!=1.5+23");
		assertEvaluateExpressionEquals(0);
		use("23.0009!=23");
		assertEvaluateExpressionEquals(0);
		use("23.0!=23.0009");
		assertEvaluateExpressionEquals(0);
		use("23.0011!=23");
		assertEvaluateExpressionEquals(1);
		use("23.0!=22.9999");
		assertEvaluateExpressionEquals(0);
		use("23.0!=22.99");
		assertEvaluateExpressionEquals(1);
	}
	
	@Test
	public void testEvaluateExpession(){
		use("x*(25-4)");
		assertTrue(parser.isExpression());
		bug.store("x", 7.0);
		assertEquals(bug.evaluate(getParserTopTree()), 147.0);
		use("angle*(25-4)");
		assertTrue(parser.isExpression());
		bug.store("angle", 500.0);
		assertTrue(Bug.areEqualDoubles(bug.angle, 140.0));
		assertEquals(bug.evaluate(getParserTopTree()), 2940.0);
		use("abc*(25-4)");
		assertTrue(parser.isExpression());
		bug.variables.put("abc", 2.0);
		assertEquals(bug.evaluate(getParserTopTree()), 42.0);
		use("cde*(25-4)");
		assertTrue(parser.isExpression());
		try{
			assertEquals(bug.evaluate(getParserTopTree()), 42.0);
			fail();
		}
		catch (IllegalArgumentException e){}
		use("function*(25-4)");
		assertTrue(parser.isExpression());
		try{
			assertEquals(bug.evaluate(getParserTopTree()), 42.0);
			fail();
		}
		catch (IllegalArgumentException e){}
		
		use("2*3 > 4*5");
		assertEvaluateExpressionEquals(0);
		use("2+3*4+10/5");
		assertEvaluateExpressionEquals(16);
	}
	@Test
	public void testEvaluateNumber(){
		use("234.0");
		assertTrue(parser.isArithmeticExpression());
		assertTrue(Bug.areEqualDoubles(bug.evaluate(getParserTopTree()), 234.0));
		use("-234.123");
		assertTrue(parser.isArithmeticExpression());
		assertTrue(Bug.areEqualDoubles(bug.evaluate(getParserTopTree()), -234.123));
	}
	
	@Test
	public void testEvaluateVariable(){

		bug.variables.put("a", 2.0);
		bug.x = 23.34;
		Token token = new Token("a");
		Tree<Token> tree = new Tree<Token>(token);
		assertTrue(Bug.areEqualDoubles(bug.evaluate(tree), 2.0));
		tree = new Tree<Token>(new Token("x"));
		assertTrue(Bug.areEqualDoubles(bug.evaluate(tree), 23.34));
	}
	
	@Test
	public void testEvaluateCase(){
		use("switch {\n case 23 > 34\n turnto 123\n}\n");
		assertTrue(parser.isSwitchStatement());
		assertTrue(Bug.areEqualDoubles(bug.evaluate(getParserTopTree().getChild(0)), 0.0));
		assertTrue(Bug.areEqualDoubles((bug.angle), 0.0));
		
		use("switch {\n case 34 > 5\n turnto 123\n }\n");
		assertTrue(parser.isSwitchStatement());
		assertTrue(Bug.areEqualDoubles(bug.evaluate(getParserTopTree().getChild(0)), 1.0));
		assertTrue(Bug.areEqualDoubles((bug.angle), 123.0));
	}
	
	@Test
	public void testCall(){
		//TODO more to test
		use("abc (23, b, 89)");
		assertTrue(parser.isFunctionCall());
		try{
			bug.interpret(getParserTopTree());
			fail();
		}
		catch (IllegalArgumentException e){}
		use("do abc (23, b, 89)\n");
		assertTrue(parser.isDoStatement());
		try{
			bug.interpret(getParserTopTree());
			fail();
		}
		catch (IllegalArgumentException e){}
	}

	

	
	@Test
	public void testInterpretColor(){
		use("color yellow\n");
		assertTrue(parser.isColorStatement());
		bug.interpret(getParserTopTree());
		assertEquals(bug.color, Color.yellow);
		use("color darkGray\n");
		assertTrue(parser.isColorStatement());
		bug.interpret(getParserTopTree());
		assertEquals(bug.color, Color.darkGray);
		use("color none\n");//bug with no color
		assertTrue(parser.isColorStatement());
		bug.interpret(getParserTopTree());
		assertEquals(bug.color, null);
		try{
			use("color turn\n");
			assertTrue(parser.isColorStatement());
			bug.interpret(getParserTopTree());
			fail();
		}
		catch (IllegalArgumentException e){}
	}
	
	@Test
	public void testInterpretVar(){
		use("var abc\n");
		assertTrue(parser.isVarDeclaration());
		bug.interpret(getParserTopTree());
		assertTrue(bug.variables.containsKey("abc"));
		assertTrue(bug.fetch("abc") == 0.0);
		assertFalse(bug.variables.containsKey("cde"));
		try{
			parser = new Parser("var abc\n");
			assertTrue(parser.isVarDeclaration());
			bug.interpret(getParserTopTree());
			fail();
		}
		catch (IllegalArgumentException e){}
	}
	
	@Test
	public void testInterpretAssign(){
		use("abc = 123.0\n");
		assertTrue(parser.isAssignmentStatement());
		bug.variables.put("abc", 0.0);
		assertTrue(bug.fetch("abc") == 0.0);
		bug.interpret(getParserTopTree());
		assertTrue(bug.variables.containsKey("abc"));
		assertTrue(bug.fetch("abc") == 123.0);
		
		use("abc = 123.0\n");
		assertTrue(parser.isAssignmentStatement());
		try{
			bug.interpret(getParserTopTree());
			fail();
		}
		catch (IllegalArgumentException e){}
	}
	
	@Test
	public void testInterpretBlock(){
		use("{\n turn -30 \n move 234\n}\n");
		assertTrue(parser.isBlock());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle, 330.0));
		assertTrue(Bug.areEqualDoubles(bug.x, 234.0*Math.sqrt(3.0)/2.0));
		assertTrue(Bug.areEqualDoubles(bug.y, 234.0/2.0));
	}

	@Test
	public void testInterpretInitially(){
		use("initially {\n turn -30\n move 234\n }\n");
		assertTrue(parser.isInitializationBlock());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle, 330.0));
		assertTrue(Bug.areEqualDoubles(bug.x, 234.0*Math.sqrt(3.0)/2.0));
		assertTrue(Bug.areEqualDoubles(bug.y, 234.0/2.0));
	}
	
	@Test
	public void testInterpretFunction(){
		use("define how  using are, you, doing{\n turn 30\n move 234\n }\n");
		assertTrue(parser.isFunctionDefinition());
		bug.interpret(getParserTopTree());
		assertTrue(bug.functions.containsKey("how"));
		assertEquals(bug.functions.get("how"), getParserTopTree());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInterpretList(){
		Tree<Token> v1, v2, v3, tree;

		use("var a\n");
		assertTrue(parser.isVarDeclaration());
		v1 = getParserTopTree();
		use("var abc, cde\n");
		assertTrue(parser.isVarDeclaration());
		v2 = getParserTopTree();
		use("var abc12, cde3, ade45\n");
		assertTrue(parser.isVarDeclaration());
		v3 = getParserTopTree();
		tree = new Tree<Token>(new Token("list"), v1, v2, v3);
		bug.interpret(tree);
		assertTrue(Bug.areEqualDoubles(bug.fetch("a"), 0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("abc"), 0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("cde"), 0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("abc12"), 0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("cde3"), 0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("ade45"), 0));
		try{
			bug.fetch("fas");
			fail();
		}
		catch (IllegalArgumentException e){}
	}
	
	@Test
	public void testInterpretSwitch(){
		use("switch {\n case 23 > 34\n turnto 123\n case 34 > 5\n turnto 45\n case 6>5\n turnto 67\n}\n");
		assertTrue(parser.isSwitchStatement());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle,45.0));
	}
	
	@Test
	public void testInterpretLoop(){
		use ("loop {\n x = x/2.0\n exit if x<1\n y = y/2.0\n }\n");
		assertTrue(parser.isLoopStatement());
		bug.store("x", 1024.0);
		bug.store("y", 1024.0);
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.fetch("x"), 0.5));
		assertTrue(Bug.areEqualDoubles(bug.fetch("y"), 1.0));
		
		use ("loop {\n angle = angle/4.0\n loop {\n exit if y < 2\n x = x/2.0\n " +
				"angle=angle*2.0\n innerCounter = innerCounter+1\n exit if x<1\n y = y/2.0\n }\n " +
				"outerCounter =outerCounter + 1\n exit if angle <1.0\n}\n");
		assertTrue(parser.isLoopStatement());
		bug.store("x", 1024.0);
		bug.store("y", 1024.0);
		bug.store("angle", 0.5);
		bug.variables.put("innerCounter", 0.0);
		bug.variables.put("outerCounter", 0.0);
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 1.0));
		assertTrue(Bug.areEqualDoubles(bug.y, 1.0));
		assertTrue(Bug.areEqualDoubles(bug.angle, 0.5));
		assertTrue(Bug.areEqualDoubles(bug.fetch("innerCounter"), 10.0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("outerCounter"), 5.0));
		
		use("loop{\n" +
				"exit if a > 10\n" +
				"a = a + 1\n" +
				"loop{\n" +
				"     switch {\n" +
				"        case y = 0\n" +
				"           exit if c = 3\n" +
				"           c = 4\n" +
				"     }\n" +
				"     exit if b > 10 \n" +
				"     b = b + 1\n" +
				"     innerCounter = innerCounter + 1\n" +
				"}\n" +
				"outerCounter = outerCounter + 1\n" +
			"}\n");
		assertTrue(parser.isLoopStatement());
		bug.variables.put("a", 1.0);
		bug.variables.put("b", 2.0);
		bug.variables.put("c", 3.0);
		bug.variables.put("innerCounter", 0.0);
		bug.variables.put("outerCounter", 0.0);
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.fetch("a"), 11.0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("b"), 2.0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("c"), 3.0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("innerCounter"), 0.0));
		assertTrue(Bug.areEqualDoubles(bug.fetch("outerCounter"), 10.0));
	}
	
	@Test
	public void testInterpretBug(){
		use("Bug Sally {\n" + 
        		"    var a, b, c\n" + 
        		"    \n" + 
        		"    initially {\n" + 
        		"        x = -50\n" + 
        		"		 a = 1.0\n"+
        		"        b = 2.0\n"+
        		"        c = 3.0\n" +
        		"        color red\n" + 
        		"    }\n" + 
        		"    \n" + 
        		"    y = 2 + 3 * a - b / c\n" + 
        		"    y = ((2+3)*a)-(b/c)\n" + 
        		"    loop{\n" + 
        		"        y = y / 2.0\n" + 
        		"        exit if y<=0.5\n" + 
        		"    }\n" + 
        		"    switch {\n" + 
        		"    }\n" + 
        		"    switch {\n" + 
        		"        case x < y\n" + 
        		"            moveto 3, x+y\n" + 
        		"            turn x-y\n" + 
        		"        case a <= x < y !=a >= b > c\n" + 
        		"            turnto -a + b\n" + 
        		"    }\n" + 
        		"}\n");
		assertTrue(parser.isBugDefinition());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 3.0));
		assertTrue(Bug.areEqualDoubles(bug.y, 3.270833333));
		assertTrue(Bug.areEqualDoubles(bug.angle, 359.7291666));
		assertTrue(Bug.areEqualDoubles(bug.fetch("a"), 1));
		assertTrue(Bug.areEqualDoubles(bug.fetch("b"), 2));
		assertTrue(Bug.areEqualDoubles(bug.fetch("c"), 3));
	}
	@Test
	public void testInterpretMove(){
		//TODO more to test
		use("move 123.1\n");
		assertTrue(parser.isMoveAction());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 123.1));
		assertTrue(Bug.areEqualDoubles(bug.y, 0));
		bug.angle = 270;
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 123.1));
		assertTrue(Bug.areEqualDoubles(bug.y, 123.1));
		bug.angle = 180;
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 0));
		assertTrue(Bug.areEqualDoubles(bug.y, 123.1));
		bug.angle = 90;
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 0));
		assertTrue(Bug.areEqualDoubles(bug.y, 0));
		bug.angle = -45;
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 1/Math.sqrt(2)*123.1));
		assertTrue(Bug.areEqualDoubles(bug.y, 1/Math.sqrt(2)*123.1));
		use ("move 2.5*4\n");
		assertTrue(parser.isMoveAction());
		bug.angle = -45;
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 1/Math.sqrt(2)*10.0));
		assertTrue(Bug.areEqualDoubles(bug.y, 1/Math.sqrt(2)*10.0));
	}
	
	@Test
	public void testInterpretMoveTo(){
		//TODO more to test
		use("moveto 123.4, 567.8\n");
		assertTrue(parser.isMoveToAction());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 123.4));
		assertTrue(Bug.areEqualDoubles(bug.y, 567.8));
		use("moveto 12.3*2, 2*1.3\n");
		assertTrue(parser.isMoveToAction());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.x, 24.6));
		assertTrue(Bug.areEqualDoubles(bug.y, 2.6));
	}
	
	@Test
	public void testInterpretTurn(){
		//TODO more to test
		use("turn 123.0\n");
		assertTrue(parser.isTurnAction());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle, 123.0));
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle, 246.0));
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle, 9.0));
		use("turn -123.0\n");
		assertTrue(parser.isTurnAction());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle, 360.0 - 123.0));
	}
	
	@Test
	public void testInterpretTurnTo(){
		//TODO more to test
		use("turnto -123.0\n");
		assertTrue(parser.isTurnToAction());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle, 360.0 - 123.0));
		bug.angle = 23;
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.angle, 360.0 - 123.0));
	}
	
	@Test
	public void testInterpretReturn(){
		use("return 123.0\n");
		assertTrue(parser.isReturnStatement());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.returnValue, 123.0));
		assertTrue(bug.returningFunction);
	}
	
	@Test
	public void testEvaluateCall(){
		use(
				"Bug FunctionTest{\n" +
				"var a\n" +
				"a = 20\n" +
				"a = changeA(a)\n" +
				"define changeA using b {\n" +
				"	b = b + 10\n" +
				"	return b\n" +
				"}\n" +
				"}\n");
		assertTrue(parser.isBugDefinition());
		bug.interpret(getParserTopTree());
		assertTrue(Bug.areEqualDoubles(bug.returnValue, 30));
		assertTrue(Bug.areEqualDoubles(bug.fetch("a"), 30));		
		assertEquals(bug.scope.size(),1);
	}
	//Helper methods


	private void assertEvaluateExpressionEquals(double result) {
		assertTrue(parser.isExpression());
		assertTrue(Bug.areEqualDoubles(bug.evaluate(getParserTopTree()), result));
	}

	private void use(String bugCode){
		parser = new Parser(bugCode);
		bug = new Bug("Sally", null, interpreter);
	}
	private Tree<Token> getParserTopTree(){
		return parser.stack.peek();
	}


}

package cit108_ParserForBugsLanguage;
import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

import cit107_BuildingTrees.Tree;


public class ParserTest {
    Parser parser;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testParser() {
        parser = new Parser("");
        parser = new Parser("2 + 2");
    }

    @Test
    public void testArithmeticExpression() {
        Tree<Token> expected;
        
        use("250");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createNode("250.0"));
        
        use("hello");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createNode("hello"));

        use("(xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("+", "xyz", "3.0"));

        use("a + b + c");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("+", createTree("+", "a", "b"), "c"));

        use("a * b * c");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("*", createTree("*", "a", "b"), "c"));

        use("3 * 12.5 - 7");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("-", createTree("*", "3.0", "12.5"), createNode("7.0")));

        use("12 * 5 - 3 * 4 / 6 + 8");
        assertTrue(parser.isExpression());
        expected = createTree("+",
                      createTree("-",
                         createTree("*", "12.0", "5.0"),
                         createTree("/",
                            createTree("*", "3.0", "4.0"),
                            "6.0"
                           )
                        ),
                      "8.0"
                     );
        assertStackTopEquals(expected);
                     
        use("12 * ((5 - 3) * 4) / 6 + (8)");
        assertTrue(parser.isExpression());
        expected = createTree("+",
                      createTree("/",
                         createTree("*",
                            "12.0",
                            createTree("*",
                               createTree("-","5.0","3.0"),
                               "4.0")),
                         "6.0"),
                      "8.0");
        assertStackTopEquals(expected);
        
        use("");
        assertFalse(parser.isExpression());
        
        use("#");
        assertFalse(parser.isExpression());

        try {
            use("17 +");
            assertFalse(parser.isExpression());
            fail();
        }
        catch (SyntaxException e) {
        }
        try {
            use("22 *");
            assertFalse(parser.isExpression());
            fail();
        }
        catch (SyntaxException e) {
        }
    }

    @Test
    public void testUnaryOperator() {       
        use("-250");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("-", "250.0"));
        
        use("+250");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("+", "250.0"));
        
        use("- hello");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("-", "hello"));

        use("-(xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("-", createTree("+", "xyz", "3.0")));

        use("(-xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("+", createTree("-", "xyz"), "3.0"));
    }

    @Test
    public void testTerm() {        
        use("12");
        assertTrue(parser.isTerm());
        assertStackTopEquals(createNode("12.0"));
        
        use("12.5");
        assertTrue(parser.isTerm());
        assertStackTopEquals(createNode("12.5"));

        use("3*12");
        assertTrue(parser.isTerm());
        assertStackTopEquals(createTree("*", "3.0", "12.0"));

        use("x * y * z");
        assertTrue(parser.isTerm());
        assertStackTopEquals(createTree("*", createTree("*", "x", "y"), "z"));
        
        use("20 * 3 / 4");
        assertTrue(parser.isTerm());
        assertEquals(createTree("/", createTree("*", "20.0", "3.0"), createNode("4.0")),
                     stackTop());

        use("20 * 3 / 4 + 5");
        assertTrue(parser.isTerm());
        assertEquals(createTree("/", createTree("*", "20.0", "3.0"), "4.0"),
                     stackTop());
        followedBy("+ 5");
        
        use("");
        assertFalse(parser.isTerm());
        followedBy("");
        
        use("#");
        assertFalse(parser.isTerm());followedBy("#");

    }

    @Test
    public void testIsFactor() {
        use("12");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createNode("12.0"));

        use("hello");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createNode("hello"));
        
        use("(xyz + 3)");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createTree("+", "xyz", "3.0"));
        
        use("12 * 5");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createNode("12.0"));
        followedBy("* 5.0");
        
        use("17 +");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createNode("17.0"));
        followedBy("+");

        use("");
        assertFalse(parser.isFactor());
        followedBy("");
        
        use("#");
        assertFalse(parser.isFactor());
        followedBy("#");
    }

    @Test
    public void testIsFactor2() {
        use("hello.world");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createTree(".", "hello", "world"));
        
        use("foo(bar)");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createTree("call", "foo",
                                        createTree("var", "bar")));
        
        use("foo(bar, baz)");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createTree("call", "foo",
                                        createTree("var", "bar", "baz")));
        
        use("foo(2*(3+4))");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createTree("call", "foo",
                                 createTree("var",
                                     createTree("*", "2.0",
                                         createTree("+", "3.0", "4.0")))));
    }

    @Test
    public void testAddOperator() {
        use("+ - + $");
        assertTrue(parser.isAddOperator());
        assertTrue(parser.isAddOperator());
        assertTrue(parser.isAddOperator());
        assertFalse(parser.isAddOperator());
        assertStackTopEquals(createTree("+"));
        parser.stack.pop();
        assertStackTopEquals(createTree("-"));
        followedBy("$");
    }

    @Test
    public void testMultiplyOperator() {
        use("* / $");
        assertTrue(parser.isMultiplyOperator());
        assertTrue(parser.isMultiplyOperator());
        assertFalse(parser.isMultiplyOperator());
        followedBy("$");
    }

    @Test
    public void testNextToken() {
        use("12 12.5 bogus switch + \n");
        assertEquals(new Token(Token.Type.NUMBER, "12.0"), parser.nextToken());
        assertEquals(new Token(Token.Type.NUMBER, "12.5"), parser.nextToken());
        assertEquals(new Token(Token.Type.NAME, "bogus"), parser.nextToken());
        assertEquals(new Token(Token.Type.KEYWORD, "switch"), parser.nextToken());
        assertEquals(new Token(Token.Type.SYMBOL, "+"), parser.nextToken());
        assertEquals(new Token(Token.Type.EOL, "\n"), parser.nextToken());
        assertEquals(new Token(Token.Type.EOF, "EOF"), parser.nextToken());
    }
    
    @Test
    public void testIsEol(){
    	use("\n\n j \n");
    	assertTrue(parser.isEol());
    	followedBy("j");
    	assertTrue(parser.stack.isEmpty());
    }
    @Test
    public void testIsComparator(){
    	use("< = > j =");
    	assertTrue(parser.isComparator());
    	assertStackTopEquals(createTree("<="));
    	assertTrue(parser.isComparator());
    	assertStackTopEquals(createTree(">"));
    	followedBy("j =");
    }
    
	@Test
	public void testIsExpression() {
		use("<=abc");
		assertFalse(parser.isExpression());
		assertTrue(parser.stack.isEmpty());
		use("abc <= cde");
		assertTrue(parser.isExpression());
		assertStackTopEquals(createTree("<=", "abc", "cde"));
		use("456 +a < 123-abc dfe <= 435 def = 34 abc 43!=23 45 >= 32 46>34");
		assertTrue(parser.isExpression());
		assertStackTopEquals(createTree("<",
				createTree("+", "456.0", "a"), createTree("-", "123.0","abc")));
		for (int i = 0; i < 6; i++) {
			assertTrue(parser.isExpression());
		}
		use ("456 +a < 123-abc >= 343*34");
		assertTrue(parser.isExpression());
		assertStackTopEquals(createTree(">=",
				createTree("<", createTree("+", "456.0", "a"), createTree("-", "123.0", "abc")), 
				createTree("*", "343.0", "34.0")));
		//testing with makeTreeOfTokens(String) function
		assertStackTopEquals (makeTreeOfTokens(">= (< (+ (456.0 a) -(123.0 abc)) * (343.0 34.0))"));
	}


	
	@Test
	public void testIsFunctionCall() {
		use("how (are, you)");
		assertTrue(parser.isFunctionCall());
		assertStackTopEquals(makeTreeOfTokens("call(how var(are you))"));
		use("today(is beautiful)"); 
		try {
			assertFalse(parser.isFunctionCall());
			fail();
		}
		catch (SyntaxException e) {
		}
		followedBy("beautiful");
		use("test (123, 345+5, you)");
		assertTrue(parser.isFunctionCall());
		assertStackTopEquals(makeTreeOfTokens("call (test var(123.0 +(345.0 5.0) you))"));
	}
	
	@Test
	public void testIsDoStatement(){
		use("do ab \n&");
		assertTrue(parser.isDoStatement());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("call(ab var)"));
		use("do ab (sdf, 23, 13)\n");
		assertTrue(parser.isDoStatement());
		assertStackTopEquals(makeTreeOfTokens("call(ab var(sdf 23.0 13.0))"));
		use("do \n");
		try{
			assertFalse(parser.isDoStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("\n");
		use("do ab &");
		try{
			assertFalse(parser.isDoStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("&");
		use("exit ab \n");
		assertFalse(parser.isDoStatement());
		followedBy("exit");
	}
	
	@Test//TODO
	public void testIsFunctionDefinition(){
		use("define how using hot { \n}\n*");
		assertTrue(parser.isFunctionDefinition());followedBy("*");
		assertStackTopEquals(makeTreeOfTokens("function (how var(hot) block )"));
		use("define how  using are, you, doing {\n}\n*");
		assertTrue(parser.isFunctionDefinition());
		assertStackTopEquals(makeTreeOfTokens("function (how var (are you doing ) block )"));
		use("define how  {\nturn 234\n move 345\n}\n*");
		assertTrue(parser.isFunctionDefinition());
		use("define using are, you, doing {\n}\n*");
		try{
			assertFalse(parser.isFunctionDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("using are");
		use("define how using are, you, doing");
		try{
			assertFalse(parser.isFunctionDefinition());
			fail();
		}
		catch (SyntaxException e){}
		use("turn how {\n}\n*");
		assertFalse(parser.isFunctionDefinition());
	}
	
	@Test
	public void testIsBlock(){
		use("{\n}\n*");
		assertTrue(parser.isBlock());followedBy("*");
		assertStackTopEquals(makeTreeOfTokens("block"));
		use("{\n do abc \n do cde\n}\n");
		assertTrue(parser.isBlock());
		assertStackTopEquals(makeTreeOfTokens("block (call (abc var) call (cde var))"));
		use("{\n turn 123\n return 234\n move 321\n}\n");
		assertTrue(parser.isBlock());
		assertStackTopEquals(makeTreeOfTokens("block (turn (123.0) return (234.0) move (321.0))"));
		use("{\n turn \n return abc\n}\n");
		try{
			assertFalse(parser.isBlock());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("\n");
		use("\n turn 20 \n return abc\n}\\n");
		assertFalse(parser.isBlock());
		use("{turn 20 \n return abc\n}\n");
		try{
			assertFalse(parser.isBlock());
			fail();
		}
		catch (SyntaxException e){}
		use("{\n turn 20 \n return abc\n}*");
		try{
			assertFalse(parser.isBlock());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("*");
		use("(\n turn 20 \n return abc\n}*");
		assertFalse(parser.isBlock());
	}
	
	@Test
	public void testIsColorStatement(){
		use("color black \n&");
		assertTrue(parser.isColorStatement());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("color (black)"));
		use("color yellow\n");
		assertTrue(parser.isColorStatement());
		assertStackTopEquals(makeTreeOfTokens("color (yellow)"));
		use("color \n");
		try{
			assertFalse(parser.isColorStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("\n");
		use("color ab \n");
		try{
			assertFalse(parser.isColorStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("ab");
		use("exit ab \n");
		assertFalse(parser.isColorStatement());
		followedBy("exit");
	}
	
	@Test
	public void testIsSwitchStatement(){
		use("switch {\n }\n&");
		assertTrue(parser.isSwitchStatement());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("switch"));
		use("switch {\n case abc = 123\n }\n");
		assertTrue(parser.isSwitchStatement());
		assertStackTopEquals(makeTreeOfTokens("switch (case (= (abc 123.0) block))"));
		use("switch {\n case abc = 123\n case cde \n } \n");
		assertTrue(parser.isSwitchStatement());
		assertStackTopEquals(makeTreeOfTokens("switch (case (= (abc 123.0) block) case (cde block))"));
		use("switch {\n case abc = 123 \n turn 123\n move 345\n }\n");
		assertTrue(parser.isSwitchStatement());
		assertStackTopEquals(makeTreeOfTokens("switch (case (= (abc 123.0) block (turn (123.0) move (345.0))))"));
		use("switch {\n case 123 \n turn 123\n move 345\n case 789\n turnto 12\n turn 987\n}\n");
		assertTrue(parser.isSwitchStatement());
		assertStackTopEquals(makeTreeOfTokens("switch (case (123.0 block (turn " +
				"(123.0) move (345.0))) case (789.0 block (turnto (12.0) turn (987.0))))"));
		use("switch \n }\n");
		try{
			assertFalse(parser.isSwitchStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("\n");
		use("switch {\n case $ }\n");
		try{
			assertFalse(parser.isSwitchStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("$ }\n");
		use("exit {\n }\n");
		assertFalse(parser.isSwitchStatement());
		followedBy("exit");
	}

	@Test
	public void testIsMoveAction() {
		use("move 123 \n move 123 move \n");
		assertTrue(parser.isMoveAction());
		assertStackTopEquals(makeTreeOfTokens("move (123.0)"));
		try {
			assertFalse(parser.isMoveAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		try {
			assertFalse(parser.isMoveAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		assertTrue(parser.isEol());
	}
	
	@Test
	public void testIsTurnAction() {
		use("turn 123 \n turn 123 turn \n");
		assertTrue(parser.isTurnAction());
		assertStackTopEquals(makeTreeOfTokens("turn (123.0)"));
		try {
			assertFalse(parser.isTurnAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		try {
			assertFalse(parser.isTurnAction());
			fail();
		}
		catch (SyntaxException e) {
		}
	}
	
	@Test
	public void testIsTurnToAction() {
		use("turnto 123 \n turnto 123 turnto \n turn 123 \n");
		assertTrue(parser.isTurnToAction());
		assertStackTopEquals(makeTreeOfTokens("turnto (123.0)"));
		try {
			assertFalse(parser.isTurnToAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		try {
			assertFalse(parser.isTurnToAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		assertFalse(parser.isTurnToAction());
		assertTrue(parser.isEol());
		assertFalse(parser.isTurnToAction());
		followedBy("turn 123 \n");
	}
	
	@Test
	public void testIsReturnStatement(){
		use("return 23 \n&");
		assertTrue(parser.isReturnStatement());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("return (23.0)"));
		use("return \n");
		try{
			assertFalse(parser.isReturnStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("\n");
		use("return 23 &");
		try{
			assertFalse(parser.isReturnStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("&");
		use("exit 23 \n");
		assertFalse(parser.isReturnStatement());
		followedBy("exit");
	}
	
	@Test
	public void testIsExitIfStatement(){
		use("exit if 23 \n&");
		assertTrue(parser.isExitIfStatement());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("exit (23.0)"));
		use("exit 23\n");
		try{
			assertFalse(parser.isExitIfStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("23");
		use("if exit 23 \n");
		assertFalse(parser.isExitIfStatement());followedBy("if");
		use("exit if \n");
		try{
			assertFalse(parser.isExitIfStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("\n");
		use("exit if 23 &");
		try{
			assertFalse(parser.isExitIfStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("&");
	}
	
	@Test
	public void testIsLoopStatement(){
		use("loop {\n}\n*");
		assertTrue(parser.isLoopStatement());followedBy("*");
		assertStackTopEquals(makeTreeOfTokens("loop (block)"));
		use("loop {\n turn 123 \n move 345\n}\n");
		assertTrue(parser.isLoopStatement());
		assertStackTopEquals(makeTreeOfTokens("loop (block(turn (123.0) move (345.0)))"));
		use("loop *");
		try{
			assertFalse(parser.isLoopStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("*");
		use("lop {\n}\n*");
		assertFalse(parser.isLoopStatement());
		use("do {\n}\n*");
		assertFalse(parser.isLoopStatement());
	}
	
	@Test
	public void testIsMoveToAction() {
		use("moveto 123,456 \n moveto 123 moveto 123, moveto 123,456");
		assertTrue(parser.isMoveToAction());
		assertStackTopEquals(makeTreeOfTokens("moveto (123.0 456.0)"));
		for (int i = 0; i < 3; i++) {
			try {
				assertFalse(parser.isMoveToAction());
				fail();
			}
			catch (SyntaxException e) {
			}
		}
		use("move 123,456\n");
		assertFalse(parser.isMoveToAction());
	}
	
	@Test
	public void testIsLineAction() {
		use("line 3445, 234, 322, 433\n abc");
		assertTrue(parser.isLineAction());
		assertStackTopEquals(makeTreeOfTokens("line (3445.0 234.0 322.0 433.0)"));
		followedBy("abc");
		use("line 234, 432 , , 345 \n");
		try {
			assertFalse(parser.isLineAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		followedBy(", 345 \n");
	}
	
	@Test
	public void testIsAssignmentStatement() {
		use("how = 3445 \n&");
		assertTrue(parser.isAssignmentStatement());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("assign(how 3445.0)"));
		use("how = 345");
		try {
			assertFalse(parser.isAssignmentStatement());
			fail();
		}
		catch (SyntaxException e) {
		}
		use ("how 234 \n");
		try {
			assertFalse(parser.isAssignmentStatement());
			fail();
		}
		catch (SyntaxException e) {
		}
		followedBy("234 \n");
	}

	@Test
	public void testIsInitializationBlock(){
		use("initially {\n move 321\n }\n&");
		assertTrue(parser.isInitializationBlock());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("initially (block (move (321.0)))"));
		use("initially \n move 321\n}\n");
		try{
			assertFalse(parser.isInitializationBlock());
			fail();
		}
		catch (SyntaxException e){}
		use("initial {\n move 321\n }\n");
		assertFalse(parser.isInitializationBlock());
	}
	
	@Test
	public void testIsVarDeclaration(){
		use("var abc\n&");
		assertTrue(parser.isVarDeclaration());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("var (abc)"));
		use("var abc, def, efg\n");
		assertTrue(parser.isVarDeclaration());
		assertStackTopEquals(makeTreeOfTokens("var (abc def efg)"));
		use("var \n");
		try{
			assertFalse(parser.isVarDeclaration());
			fail();
		}
		catch (SyntaxException e){}
	}
	
	@Test
	public void testIsBugDefinition(){
		use("Bug how {\n turn 234 \n}\n&");
		assertTrue(parser.isBugDefinition()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Bug (how list initially (block) block (turn (234.0)) list )"));
		use("Bug how {\n var abc, def \n var cde \n turn 234 \n}\n&");
		assertTrue(parser.isBugDefinition());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Bug (how list (var (abc def) var (cde)) initially(block) block (turn (234.0)) list)"));
		use("Bug how {\n var abc \n var cde \n initially {\n}\n turn 234 \n move 123 \n}\n&");
		assertTrue(parser.isBugDefinition());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Bug (how list (var (abc) var (cde)) initially (block) block(turn (234.0) move (123.0)) list)"));
		use("Bug how {\n var abc \n var cde \n initially {\n}\n turn 234 \n move 123 \n define xyz {\n}\n}\n&");
		assertTrue(parser.isBugDefinition());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Bug (how list(var (abc) var (cde)) initially (block) " +
				"block (turn (234.0) move (123.0)) list(function(xyz var block)))"));
		use("Bug how {\n var abc \n var cde \n initially {\n turn 234\n}\n turn 234 \n " +
				"moveto 123, 345 \n" + " define are {\n}\n define you {\n}\n}\n&");
		assertTrue(parser.isBugDefinition());followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Bug (how list (var (abc) var (cde)) initially" +
				" (block (turn (234.0))) block (turn(234.0) moveto(123.0 345.0)) list(function(are var block) function (you var block)))"));
		use("Bug how {\n }\n");
		try{
			assertFalse(parser.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		use("Bug {\n turn 234 \n}\n");
		try{
			assertFalse(parser.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("{");
		use("Bug how \n turn 234 \n}\n");
		try{
			assertFalse(parser.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("\n");
		use("Bug how { turn 234 \n}\n");
		try{
			assertFalse(parser.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("turn");
		use("Bug how {\n turn 234 \n");
		try{
			assertFalse(parser.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		use("Bug how {\n turn 234 \n}%");
		try{
			assertFalse(parser.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("%");
		use("bug how {\n turn 234 \n}\n");
		assertFalse(parser.isBugDefinition());
	}
	
	@Test
	public void testIsAllbugsCode(){
		use("Allbugs {\n }\n&");
		assertTrue(parser.isAllbugsCode()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Allbugs (list list)"));
		use("Allbugs {\n var abc \n var cde \n }\n&");
		assertTrue(parser.isAllbugsCode()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Allbugs (list (var(abc) var (cde)) list)"));
		use("Allbugs {\n define abc {\n}\n }\n&");
		assertTrue(parser.isAllbugsCode()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Allbugs (list list (function (abc var block)))"));
		use("Allbugs {\n var abc \n var cde \n define abc {\n}\n }\n&");
		assertTrue(parser.isAllbugsCode()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("Allbugs (list (var (abc) var (cde)) list ( function (abc var block)))"));
		use("Allbugs {\n define abc {\n}\n var abc \n var cde \n  }\n&");
		try{
			assertFalse(parser.isAllbugsCode());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("var");
		use("Allbugs \n }\n&");
		try{
			assertFalse(parser.isAllbugsCode());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("\n");
		use("Allbugs { }\n&");
		try{
			assertFalse(parser.isAllbugsCode());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("}");
		use("Allbugs {\n &");
		try{
			assertFalse(parser.isAllbugsCode());
			fail();
		}
		catch (SyntaxException e){}
		followedBy("&");
	}
	
	@Test
	public void testIsProgram(){
		use("Bug how {\n turn 234 \n}\n&");
		assertTrue(parser.isProgram()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("program (Allbugs (list list) list (Bug " +
				"(how list initially (block) block (turn (234.0)) list)))"));
		use("Allbugs {\n }\n Bug how {\n turn 234 \n}\n&");
		assertTrue(parser.isProgram()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("program (Allbugs (list list)" +
				" list (Bug (how list initially (block) block (turn (234.0)) list)))"));
		use("Allbugs {\n }\n Bug how {\n turn 234 \n}\n Bug are {\n turn 456 \n}\n&");
		assertTrue(parser.isProgram()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("program (Allbugs (list list) list (Bug (how list initially (block) block" +
				" (turn (234.0)) list) Bug (are list initially (block) block (turn (456.0)) list)))"));
		use("Allbugs {\n }\n Bug how {\n turn 234 \n}\n Bug are {\n turn 456 \n}\n Bug you {\n turn 789 \n}\n&");
		assertTrue(parser.isProgram()); followedBy("&");
		assertStackTopEquals(makeTreeOfTokens("program (Allbugs (list list) list (Bug (how list initially (block)" +
				" block (turn (234.0)) list) Bug (are list initially (block) block (turn (456.0)) list) " +
				"Bug (you list initially (block) block (turn (789.0)) list)))"));
		use("&");
		assertFalse(parser.isProgram()); followedBy("&");
		use("Allbugs {\n }\n&");
		try{
			assertFalse(parser.isProgram());
			fail();
		}
		catch (SyntaxException e){}
		followedBy( "&");
	}
	
//  ----- "Helper" methods
    
    /**
     * Sets the <code>parser</code> instance to use the given string.
     * 
     * @param s The string to be parsed.
     */
    private void use(String s) {
        parser = new Parser(s);
    }
    
    /**
     * Returns the current top of the stack.
     *
     * @return The top of the stack.
     */
    private Object stackTop() {
        return parser.stack.peek();
    }
    
    /**
     * Tests whether the top element in the stack is correct.
     *
     * @return <code>true</code> if the top element of the stack is as expected.
     */
    private void assertStackTopEquals(Tree<Token> expected) {
        assertEquals(expected, stackTop());
    }
    
    /**
     * This method is given a String containing some or all of the
     * tokens that should yet be returned by the Tokenizer, and tests
     * whether the Tokenizer in fact has those Tokens. To succeed,
     * everything in the given String must still be in the Tokenizer,
     * but there may be additional (untested) Tokens to be returned.
     * This method is primarily to test whether Tokens are pushed
     * back appropriately.
     * 
     * @param expectedTokens The Tokens we expect to get from the Tokenizer.
     */
    private void followedBy(String expectedTokens) {
        int expectedType;
        int actualType;
        StreamTokenizer actual = parser.tokenizer;

        Reader reader = new StringReader(expectedTokens);
        StreamTokenizer expected = Parser.createTokenizer(reader);

        try {
            while (true) {
                expectedType = expected.nextToken();
                if (expectedType == StreamTokenizer.TT_EOF) break;
                actualType = actual.nextToken();
                assertEquals(typeName(expectedType), typeName(actualType));
                if (actualType == StreamTokenizer.TT_WORD) {
                    assertEquals(expected.sval, actual.sval);
                }
                else if (actualType == StreamTokenizer.TT_NUMBER) {
                    assertEquals(expected.nval, actual.nval);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String typeName(int type) {
        switch(type) {
            case StreamTokenizer.TT_EOF: return "EOF";
            case StreamTokenizer.TT_EOL: return "EOL";
            case StreamTokenizer.TT_WORD: return "WORD";
            case StreamTokenizer.TT_NUMBER: return "NUMBER";
            default: return "'" + (char)type + "'";
        }
    }
    
    /**
     * Returns a Tree node consisting of a single leaf; the
     * node will contain a Token with a String as its value. <br>
     * Given a Tree, return the same Tree.<br>
     * Given a Token, return a Tree with the Token as its value.<br>
     * Given a String, make it into a Token, return a Tree
     * with the Token as its value.
     * 
     * @param value A Tree, Token, or String from which to
              construct the Tree node.
     * @return A Tree leaf node containing a Token whose value
     *         is the parameter.
     */
    private Tree<Token> createNode(Object value) {
        if (value instanceof Tree) {
            return (Tree) value;
        }
        if (value instanceof Token) {
            return new Tree<Token>((Token) value);
        }
        else if (value instanceof String) {
            return new Tree<Token>(makeToken((String) value));
        }
        assert false: "Illegal argument: tree(" + value + ")";
        return null; 
    }
    
    /**
     * Builds a Tree that can be compared with the one the
     * Parser produces. Any String or Token arguments will be
     * converted to Tree nodes containing Tokens.
     * 
     * @param op The String value to use in the Token in the root.
     * @param children The objects to be made into children.
     * @return The resultant Tree.
     */
    private Tree<Token> createTree(String op, Object... children) {
        Tree<Token> tree = new Tree<Token>(makeToken(op));
        for (int i = 0; i < children.length; i++) {
            tree.addChild(createNode(children[i]));
        }
        return tree;
    }
    
    /**
     * Quick'n'dirty routine to make a Token from a String. The type
     * (name or keyword, number, or symbol) is inferred from the first
     * character; no error checking is done.
     * 
     * @param s The string to turn into a Token.
     * @return A Token whose value is the given string and whose
     *         type has been inferred from the first character.
     */
    private Token makeToken(String s) {
        char ch = s.charAt(0);
        if (Character.isDigit(ch)) {
            return new Token(Token.Type.NUMBER, s);
        }
        else if (Character.isLetter(ch)) {
            if (Token.isKeyword(s)) {
                return new Token(Token.Type.KEYWORD, s);
            }
            else {
                return new Token(Token.Type.NAME, s);
            }
        }
        else
            return new Token(Token.Type.SYMBOL, s);
    }
    /**
     * Makes a tree of Tokens from tree description
     * @param treeDescription The tree description to make Tree from
     * @return The Tree of tokens
     */
	private Tree<Token> makeTreeOfTokens(String treeDescription) {
		return makeTreeOfTokens(Tree.parse(treeDescription));
	}
	
	/**
	 * Makes a tree of Tokens from a tree of Strings
	 * @param treeOfStrings the tree of Strings to make tree of Tokens from
	 * @return The tree of Tokens
	 */
    private Tree<Token> makeTreeOfTokens(Tree<String> treeOfStrings){
    	Tree<Token> treeOfTokens = new Tree<Token>(null);
    	treeOfTokens.setValue(new Token(treeOfStrings.getValue()));
    	for (int i = 0; i < treeOfStrings.getNumberOfChildren(); i++){
    		treeOfTokens.addChild(makeTreeOfTokens(treeOfStrings.getChild(i)));
    	}
    	return treeOfTokens;
    }
}

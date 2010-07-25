package parser;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import tree.Tree;

public class ParserTestDave {
    
    @Test
    public void testIsProgram() {
        use("Bug sally { \n x = y \n } \n");
        assertTrue(parser.isProgram());
        assertStackTopEqualsOneOf("program(Allbugs(list list)"
                                + " list(Bug(sally list initially(block) block(assign(x y)) list)))",
                                  "program(Allbugs(list list)"
                                + " list(Bug(sally list initially        block(assign(x y)) list)))");

        use("Allbugs { \n  } \n Bug sally { \n x = y \n } \n");
        assertTrue(parser.isProgram());
        assertStackTopEqualsOneOf("program(Allbugs(list list)"
                                + " list(Bug(sally list initially(block) block(assign(x y)) list)))",
                                  "program(Allbugs(list list)"
                                + " list(Bug(sally list initially        block(assign(x y)) list)))");

        use("Allbugs { \n  } \n Bug sally { \n x = y \n } \n Bug Willy { \n x = y \n } \n");
        assertTrue(parser.isProgram());
        assertStackTopEqualsOneOf("program(Allbugs(list list)"
                                + " list(Bug(sally list initially(block) block(assign(x y)) list)"
                                + "      Bug(Willy list initially(block) block(assign(x y)) list)))",
                                  "program(Allbugs(list list)"
                                + " list(Bug(sally list initially        block(assign(x y)) list)"
                                + "      Bug(Willy list initially        block(assign(x y)) list)))");

        use("\n Bug sally { \n x = y \n } \n");
        assertTrue(parser.isProgram());
        assertStackTopEqualsOneOf("program(Allbugs(list list)"
                                + " list(Bug(sally list initially(block) block(assign(x y)) list)))",
                                  "program(Allbugs(list list)"
                                + " list(Bug(sally list initially        block(assign(x y)) list)))");

        use("Allbugs { \n  var a, b \n define foo using a {\n x = a \n } \n \n } \n" +
        		" Bug sally { \n x = y \n } \n");
        assertTrue(parser.isProgram());
        assertStackTopEqualsOneOf("program(Allbugs(list(var(a b)) list(function(foo var(a) block(assign(x a)))))"
                                + " list(Bug(sally list initially(block) block(assign(x y)) list)))",
                                  "program(Allbugs(list(var(a b)) list(function(foo var(a) block(assign(x a)))))"
                                + " list(Bug(sally list initially        block(assign(x y)) list)))");
    }
    
    @Test
    public void testInitiallyNodeAlwaysHasBlockChild() {
        use("Bug sally { \n x = y \n } \n");
        assertTrue(parser.isProgram());
        try {
            assertStackTopEquals("program(Allbugs(list list)"
                               + " list(Bug(sally list initially(block) block(assign(x y)) list)))");
        }
        catch (AssertionError e) {
            throw new AssertionError("\n** The 'initially' node should always have a 'block' child.\n" +
            		             "** This is only a one-point deduction.");
        }
    }
    
    @Test
    public void testIsAllbugsCode() {
        use("Allbugs { \n  } \n");
        assertTrue(parser.isAllbugsCode());
        assertStackTopEquals("Allbugs(list list)");
        
        use("Allbugs { \n var a, b, c \n } \n");
        assertTrue(parser.isAllbugsCode());
        assertStackTopEquals("Allbugs(list(var(a b c)) list)");
        
        use("Allbugs { \n" +
            "   define foo using c { \n" +
            "      move c \n" +
            "   } \n" +
            "} \n");
        assertTrue(parser.isAllbugsCode());
        assertStackTopEquals("Allbugs(list list(function(foo var(c) block(move(c)))))");
        
        use("Allbugs { \n" +
          "   var a, b, c \n" +
          "   define foo using c { \n" +
          "      move c \n" +
          "   } \n" +
          "} \n");
        assertTrue(parser.isAllbugsCode());
        assertStackTopEquals("Allbugs(list(var(a b c)) list(function(foo var(c) block(move(c)))))");
    }
    
    @Test
    public void testIsBugDefinition() {
        use("Bug sally { \n x = y \n } \n");
        assertTrue(parser.isBugDefinition());
        assertStackTopEqualsOneOf("Bug(sally list initially(block) block(assign(x y)) list)",
                                  "Bug(sally list initially        block(assign(x y)) list)");
        
        use("Bug sally { \n" +
            "   var a, b, c \n" +
            "   initially { \n" +
            "      a = 5 \n" +
            "   } \n" +
            "   b = a \n" +
            "   c = b \n" +
            "   define foo using c { \n" +
            "      move c \n" +
            "   } \n" +
            "} \n");
        assertTrue(parser.isBugDefinition());
        assertStackTopEquals("Bug(sally " +
                                " list(var(a b c))" +
                                " initially(block(assign(a 5.0)))" +
                                " block(assign(b a) assign(c b))" +
        		                " list(function(foo var(c) block(move(c))))" +
        		             ")");
    }
    
    @Test
    public void isVarDeclaration() {
        use("var x \n");
        assertTrue(parser.isVarDeclaration());
        assertStackTopEquals("var(x)");
        
        use("var x, y, xyz \n");
        assertTrue(parser.isVarDeclaration());
        assertStackTopEquals("var(x y xyz)");
    }
    
    @Test
    public void testIsInitializationBlock() {
        use("initially { \n } \n");
        assertTrue(parser.isInitializationBlock());
        assertStackTopEquals("initially(block)");
        
        use("initially { \n x = 0 \n y = 0 \n } \n");
        assertTrue(parser.isInitializationBlock());
        assertStackTopEquals("initially(block(assign(x 0.0) assign(y 0.0)))");
        
    }
    
    @Test
    public void testIsCommand() {
        use("x = 5\n move x\n");
        assertTrue(parser.isCommand());
        assertTrue(parser.isCommand());
    }
    
    @Test
    public void testIsStatement() {
        use("x = 5 \n"
            + "loop { \n } \n"
            + "exit if 2 + 2 = 4 \n"
            + "switch {\n case 5 \n move 3 \n }\n"
            + "return 5 \n"
            + "do foo(3, bar) \n"
            + "color orange \n");
        assertTrue(parser.isStatement()); // x = 5 
        assertTrue(parser.isStatement()); // loop { \n } \n
        assertTrue(parser.isStatement()); // exit if 2 + 2 = 4 \n
        assertTrue(parser.isStatement()); // switch {\n case 5 \n move 3 \n }\n
        assertTrue(parser.isStatement()); // return 5 \n
        assertTrue(parser.isStatement()); // do foo(3, bar) \n
        assertTrue(parser.isStatement()); // color orange \n
    }
    
    @Test
    public void testIsAction() {
        use("move 1 \n" +
            "moveto 2, 3 \n" +
            "turn 4 \n" +
            "turnto 5 \n" +
            "line 6, 7, 8, 9 \n");
        for (int i = 0; i < 5; i++) {
            assertTrue(parser.isAction());
        }
    }
    
    @Test
    public void testIsMoveAction() {
        use("move 5 \n");
        assertTrue(parser.isMoveAction());
        assertStackTopEquals("move(5.0)");
        
        use("move 3 * n \n");
        assertTrue(parser.isMoveAction());
        assertStackTopEquals("move(*(3.0 n))");
    }
    
    @Test
    public void testIsMoveToAction() {
        use("moveto 5 , -3\n");
        assertTrue(parser.isMoveToAction());
        assertStackTopEquals("moveto (5.0 -(3.0))");
        
        use("moveto 3 * n , 0\n");
        assertTrue(parser.isMoveToAction());
        assertStackTopEquals("moveto(*(3.0 n) 0.0)");
    }
    
    @Test
    public void testIsTurnAction() {
        use("turn 5 \n");
        assertTrue(parser.isTurnAction());
        assertStackTopEquals("turn (5.0)");
        
        use("turn 3 * n \n");
        assertTrue(parser.isTurnAction());
        assertStackTopEquals("turn(*(3.0 n))");
    }
    
    @Test
    public void testIsTurnToAction() {
        use("turnto 5 \n");
        assertTrue(parser.isTurnToAction());
        assertStackTopEquals("turnto(5.0)");
        
        use("turnto 3 * n \n");
        assertTrue(parser.isTurnToAction());
        assertStackTopEquals("turnto(*(3.0 n))");
    }
    
    private Tree<Token> makeTree(String treeRepresentation) {
        return makeTreeOfTokens(Tree.parse(treeRepresentation));
    }
    
    /**
     * Given a Tree of Strings, creates and returns the corresponding
     * TreeOfTokens;
     *
     */
    private Tree<Token> makeTreeOfTokens(Tree<String> treeOfStrings) {
        Tree<Token> treeOfTokens;
        
        String valueInRoot = treeOfStrings.getValue();
        treeOfTokens = new Tree<Token>(new Token(valueInRoot));
        Iterator<Tree<String>> iter = treeOfStrings.iterator();
        while (iter.hasNext()) {
            treeOfTokens.addChildren(makeTreeOfTokens(iter.next()));
        }
        return treeOfTokens;
    }
    
    @Test
    public void testTree() {
        assertEquals(createTree("turnto", createTree("*", "3.0", "n")),
                     makeTree("turnto(*(3.0 n))")); 
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testTreeTooManyCloseParentheses() {
        makeTree("turnto(*(3.0 n)))"); 
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testTreeTooFewCloseParentheses() {
        makeTree("turnto(*(3.0 n)"); 
    }
    
    @Test
    public void testIsLineAction() {
        use("line 1, 2, -3, 4.0 \n");
        assertTrue(parser.isLineAction());
        assertStackTopEquals("line(1.0 2.0 -(3.0) 4.0)");
    }
    
    @Test
    public void testIsAssignmentStatement() {
        use("x = 5 \n");
        assertTrue(parser.isAssignmentStatement());
        assertStackTopEquals("assign(x 5.0)");
        
        use("abc = 2 + 2 \n");
        assertTrue(parser.isAssignmentStatement());
        assertStackTopEquals("assign(abc +(2.0 2.0))");
    }
    
    @Test
    public void testIsLoopStatement() {
        use("loop { \n }\n");
        assertTrue(parser.isLoopStatement());
        assertStackTopEquals("loop(block)");
        
        use("loop { \n exit if true \n }\n");
        assertTrue(parser.isLoopStatement());
        assertStackTopEquals("loop(block(exit(true)))");
    }
    
    @Test
    public void testIsExitIfStatement() {
        use("exit if 5 \n");
        assertTrue(parser.isExitIfStatement());
        assertStackTopEquals("exit(5.0)");
        
        use("exit if 2 + 2 = 4 \n");
        assertTrue(parser.isExitIfStatement());
        assertStackTopEquals("exit(=(+(2.0 2.0) 4.0))");
    }

    @Test
    public void testIsSwitchStatement() {
        use("switch {\n }\n");
        assertTrue(parser.isSwitchStatement());
        assertStackTopEquals("switch");      
        
        use("switch {\n case x = y \n }\n");
        assertTrue(parser.isSwitchStatement());
        assertStackTopEquals("switch(case(=(x y) block))");      
        
        use("switch {\n case x = y \n x = y \n }\n");
        assertTrue(parser.isSwitchStatement());
        assertStackTopEquals("switch(case(=(x y) block(assign(x y))))");
        
        use("switch {\n case x = y \n x = y \n x = y \n }\n");
        assertTrue(parser.isSwitchStatement());
        assertStackTopEquals("switch(case(=(x y) block(assign(x y)assign(x y))))");
    }
    
    @Test
    public void testIsReturnStatement() {
        use("return 5 \n");
        assertTrue(parser.isReturnStatement());
        assertStackTopEquals("return(5.0)");
        
        use("return 3 * n \n");
        assertTrue(parser.isReturnStatement());
        assertStackTopEquals("return(*(3.0 n))");
    }
    
    @Test
    public void testIsDoStatement() {
        use("do zero() \n");
        assertTrue(parser.isDoStatement());
        assertStackTopEquals("call(zero var)");

        use("do one(1) \n");
        assertTrue(parser.isDoStatement());
        assertStackTopEquals("call(one var(1.0))");

        use("do two(1, 2) \n");
        assertTrue(parser.isDoStatement());
        assertStackTopEquals("call(two var(1.0 2.0))");
    }
    
    @Test
    public void testIsColorStatement() {
        use("color red \n");
        assertTrue(parser.isColorStatement());
        assertStackTopEquals("color(red)");
    }

   @Test(expected=SyntaxException.class)
    public void testIsBadColorStatement() {
        use("color fuschia \n");
        parser.isColorStatement();
    }
    
    @Test
    public void testIsVariable() {
        use("abc123");
        assertTrue(parser.isVariable());
        assertStackTopEquals("abc123");
    }
    
    @Test
    public void testIsBlock() {
        use("{ \n } \n");
        assertTrue(parser.isBlock());
        assertStackTopEquals("block");

        use("{\n x = 5\n move x\n }\n");
        assertTrue(parser.isBlock());
        assertStackTopEquals("block( assign(x 5.0) move(x))");
    }
    
    @Test
    public void testIsFunctionDefinition() {
        use("define foo { \n } \n");
        assertTrue(parser.isFunctionDefinition());
        assertStackTopEquals("function(foo var block)");

        use("define foo using x { \n } \n");
        assertTrue(parser.isFunctionDefinition());
        assertStackTopEquals("function(foo var(x) block)");

        use("define foo { \n x = 0 \n } \n");
        assertTrue(parser.isFunctionDefinition());
        assertStackTopEquals("function(foo var block( assign(x 0.0) ))");

        use("define foo using x, y { \n x = 0 \n y = 0 \n } \n");
        assertTrue(parser.isFunctionDefinition());
        assertStackTopEquals("function(foo var(x y) block(assign(x 0.0) assign(y 0.0)))");
    }
    
    @Test
    public void testIsFunctionCall() {
        use("foo()");
        assertTrue(parser.isFunctionCall());
        assertStackTopEquals("call(foo var)");

        use("foo(x)");
        assertTrue(parser.isFunctionCall());
        assertStackTopEquals("call(foo var(x))");

        use("foo(x, y, z)");
        assertTrue(parser.isFunctionCall());
        assertStackTopEquals("call(foo var(x y z))");
        
        use("foo(2 + 2, bar(x))");
        assertTrue(parser.isFunctionCall());
        assertStackTopEquals("call(foo var(+(2.0 2.0) call(bar var(x))))");
    }
    
    
    @Test
    public void testIsParameterList() {
        use("()");
        assertTrue(parser.isParameterList());
        assertStackTopEquals("var");
        
        use("(5)");
        assertTrue(parser.isParameterList());
        assertStackTopEquals("var(5.0)");
        
        use("(bar, x+3) &");
        assertTrue(parser.isParameterList());
        assertStackTopEquals("var(bar +(x 3.0))");
        
        use("(x > y)");
        assertTrue(parser.isParameterList());
        assertStackTopEquals("var(>(x y))");
    }
    
 // ---------- Below this line is the code given to students ----------
    
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
    public void testIsExpression() {
        use("250");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createNode("250.0"));
        
        use("12 * 5 - 3 * 4 / 6 + 8");
        assertTrue(parser.isExpression());

        use("5 = x");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("=", "5.0", "x"));
        
        use("2 + 3 <= 2 * 3");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("<=", createTree("+", "2.0", "3.0"),
                                        createTree("*", "2.0", "3.0")));
        
        use("x < y < z");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createTree("<", createTree("<", "x", "y"), "z"));
        
    }
    
    @Test
    public void testIsComparator() {
        use("<<==!=>=>");
        assertTrue(parser.isComparator());
        assertStackTopEquals(createNode("<"));
        assertTrue(parser.isComparator());
        assertStackTopEquals(createNode("<="));
        assertTrue(parser.isComparator());
        assertStackTopEquals(createNode("="));
        assertTrue(parser.isComparator());
        assertStackTopEquals(createNode("!="));
        assertTrue(parser.isComparator());
        assertStackTopEquals(createNode(">="));
        assertTrue(parser.isComparator());
        assertStackTopEquals(createNode(">"));
    }

    @Test
    public void testIsArithmeticExpression() {
        Tree<Token> expected;
        
        use("250");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createNode("250.0"));
        
        use("hello");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createNode("hello"));

        use("(xyz + 3)");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("+", "xyz", "3.0"));

        use("a + b + c");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("+", createTree("+", "a", "b"), "c"));

        use("a * b * c");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("*", createTree("*", "a", "b"), "c"));

        use("3 * 12.5 - 7");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("-", createTree("*", "3.0", "12.5"), createNode("7.0")));

        use("12 * 5 - 3 * 4 / 6 + 8");
        assertTrue(parser.isArithmeticExpression());
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
        assertTrue(parser.isArithmeticExpression());
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
        assertFalse(parser.isArithmeticExpression());
        
        use("#");
        assertFalse(parser.isArithmeticExpression());

        try {
            use("17 +");
            assertFalse(parser.isArithmeticExpression());
            fail();
        }
        catch (SyntaxException e) {
        }
        try {
            use("22 *");
            assertFalse(parser.isArithmeticExpression());
            fail();
        }
        catch (SyntaxException e) {
        }
    }

    @Test
    public void testUnaryOperator() {       
        use("-250");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("-", "250.0"));
        
        use("+250");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("+", "250.0"));
        
        use("- hello");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("-", "hello"));

        use("-(xyz + 3)");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("-", createTree("+", "xyz", "3.0")));

        use("(-xyz + 3)");
        assertTrue(parser.isArithmeticExpression());
        assertStackTopEquals(createTree("+", createTree("-", "xyz"), "3.0"));
    }

    @Test
    public void testIsTerm() {        
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
    public void testIsAddOperator() {
        use("+ - + $");
        assertTrue(parser.isAddOperator());
        assertTrue(parser.isAddOperator());
        assertTrue(parser.isAddOperator());
        assertFalse(parser.isAddOperator());
        followedBy("$");
    }

    @Test
    public void testIsMultiplyOperator() {
        use("* / $");
        assertTrue(parser.isMultiplyOperator());
        assertTrue(parser.isMultiplyOperator());
        assertFalse(parser.isMultiplyOperator());
        followedBy("$");
    }

    @Test
    public void testNextToken() {
        use("12 12.5 bogus switch + \n");
        assertEquals(new Token("12.0"), parser.nextToken());
        assertEquals(new Token("12.5"), parser.nextToken());
        assertEquals(new Token("bogus"), parser.nextToken());
        assertEquals(new Token("switch"), parser.nextToken());
        assertEquals(new Token("+"), parser.nextToken());
        assertEquals(new Token("\n"), parser.nextToken());
        assertEquals(new Token("EOF"), parser.nextToken());
    }
    
//    @Test
//    public void testLineNumberCounter() {
//        use("\n\nAllbugs { \n  var a, b \n define foo using a {\n x = a \n } \n \n } \n" +
//        " Bug sally { \n x + y \n } \n");
//        parser.isProgram(); // error on line 11
//    }
    
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
    private Tree<Token> stackTop() {
        return parser.stack.peek();
    }
    
    /**
     * Throws an AssertionException if the top element in the
     * stack is not the expected Tree.
     *
     * @param expected The expected Tree.
     */
    private void assertStackTopEquals(Tree<Token> expected) {
        assertEquals(expected, stackTop());
    }
    
   /**
    * Throws an AssertionException if the top element in the
    * stack is not a leaf containing the expected String.
    *  
    * @param expected The value expected to be in the top Tree.
    */
   private void assertStackTopEquals(String expected) {
       assertEquals(makeTree(expected), stackTop());
   }

   /**
    * Throws an AssertionException if the top element in the
    * stack is not one of the trees whose string representations
    * are in the <code>expecteds</code> array.
    * <p>
    * Note that, because varargs are used, the parameters
    * are not in the usual order.
    * 
    * @param expecteds A list of possible correct values.
    */
   void assertStackTopEqualsOneOf(String... expecteds) {
       Tree<Token> actual = stackTop();
       for (String expected : expecteds) {
           if (actual.equals(makeTree(expected))) return;
       }
       throw new AssertionError(actual + " was not one of these expected values:\n" +
                                Arrays.toString(expecteds));
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
            return new Tree<Token>(new Token((String) value));
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
        Tree<Token> tree = new Tree<Token>( new Token(op));
        for (int i = 0; i < children.length; i++) {
            tree.addChildren(createNode(children[i]));
        }
        return tree;
    }
}

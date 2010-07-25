package cit105_BugLanguageRecognizer;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;




/**
 * This is the test for Recognizer.java
 * @author David Matuszek
 * @author sxycode
 * @version Feb 28, 2008
 */

public class RecognizerTest {

	Recognizer r0, r1, r2, r3, r4, r5, r6, r7, r8;

	/**
	 * Constructor for RecognizerTest.
	 * @param arg0
	 */
	public RecognizerTest() {
		r0 = new Recognizer("2 + 2");
		r1 = new Recognizer("");
	}


	@Before
	public void setUp() throws Exception {
		r0 = new Recognizer("");
		r1 = new Recognizer("250");
		r2 = new Recognizer("hello");
		r3 = new Recognizer("(xyz + 3)");
		r4 = new Recognizer("12 * 5 - 3 * 4 / 6 + 8");
		r5 = new Recognizer("12 * ((5 - 3) * 4) / 6 + (8)");
		r6 = new Recognizer("17 +");
		r7 = new Recognizer("22 *");
		r8 = new Recognizer("#");
	}

	@Test
	public void testRecognizer() {
		r0 = new Recognizer("");
		r1 = new Recognizer("2 + 2");
	}

	@Test
	public void testIsArithmeticExpression() {
		assertTrue(r1.isArithmeticExpression());
		assertTrue(r2.isArithmeticExpression());
		assertTrue(r3.isArithmeticExpression());
		assertTrue(r4.isArithmeticExpression());
		assertTrue(r5.isArithmeticExpression());

		assertFalse(r0.isArithmeticExpression());
		assertFalse(r8.isArithmeticExpression());

		try {
			assertFalse(r6.isArithmeticExpression());
			fail();
		}
		catch (SyntaxException e) {
		}
		try {
			assertFalse(r7.isArithmeticExpression());
			fail();
		}
		catch (SyntaxException e) {
		}
	}

	@Test
	public void testIsTerm() {
		assertFalse(r0.isTerm()); // ""

		assertTrue(r1.isTerm()); // "250"

		assertTrue(r2.isTerm()); // "hello"

		assertTrue(r3.isTerm()); // "(xyz + 3)"
		followedBy(r3, "");

		assertTrue(r4.isTerm());  // "12 * 5 - 3 * 4 / 6 + 8"
		assertEquals(new Token(Token.Type.SYMBOL, "-"), r4.nextToken());
		assertTrue(r4.isTerm());
		followedBy(r4, "+ 8");

		assertTrue(r5.isTerm());  // "12 * ((5 - 3) * 4) / 6 + (8)"
		assertEquals(new Token(Token.Type.SYMBOL, "+"), r5.nextToken());
		assertTrue(r5.isTerm());
		followedBy(r5, "");
	}

	@Test
	public void testIsFactor() {
		assertTrue(r1.isFactor());
		assertTrue(r2.isFactor());
		assertTrue(r3.isFactor());
		assertTrue(r4.isFactor()); followedBy(r4, "* 5 - 3 * 4 / 6 + 8");
		assertTrue(r5.isFactor()); followedBy(r5, "* ((5");
		assertTrue(r6.isFactor()); followedBy(r6, "+");
		assertTrue(r7.isFactor()); followedBy(r7, "*");

		assertFalse(r0.isFactor());
		assertFalse(r8.isFactor()); followedBy(r8, "#");

		Recognizer r = new Recognizer("foo()");
		assertTrue(r.isFactor());
		r = new Recognizer("bar(5, abc, 2+3)+");
		assertTrue(r.isFactor()); followedBy(r, "+");
	}

	@Test
	public void testIsAddOperator() {
		Recognizer r = new Recognizer("+ - $");
		assertTrue(r.isAddOperator());
		assertTrue(r.isAddOperator());
		assertFalse(r.isAddOperator());
		followedBy(r, "$");
	}

	@Test
	public void testIsMultiplyOperator() {
		Recognizer r = new Recognizer("* / $");
		assertTrue(r.isMultiplyOperator());
		assertTrue(r.isMultiplyOperator());
		assertFalse(r.isMultiplyOperator());
		followedBy(r, "$");
	}

	@Test
	public void testIsVariable() {
		Recognizer r = new Recognizer("foo 23 bar +");
		assertTrue(r.isVariable());

		assertFalse(r.isVariable());
		assertTrue(r.isFactor());

		assertTrue(r.isVariable());

		assertFalse(r.isVariable());
		assertTrue(r.isAddOperator());
	}

	@Test
	public void testSymbol() {
		Recognizer r = new Recognizer("++");
		assertEquals(new Token(Token.Type.SYMBOL, "+"), r.nextToken());
	}

	@Test
	public void testNextTokenMatchesType() {
		Recognizer r = new Recognizer("++abc");
		assertTrue(r.nextTokenMatches(Token.Type.SYMBOL));
		assertFalse(r.nextTokenMatches(Token.Type.NAME));
		assertTrue(r.nextTokenMatches(Token.Type.SYMBOL));
		assertTrue(r.nextTokenMatches(Token.Type.NAME));
	}

	@Test
	public void testNextTokenMatchesTypeString() {
		Recognizer r = new Recognizer("+abc+");
		assertTrue(r.nextTokenMatches(Token.Type.SYMBOL, "+"));
		assertTrue(r.nextTokenMatches(Token.Type.NAME, "abc"));
		assertFalse(r.nextTokenMatches(Token.Type.SYMBOL, "*"));
		assertTrue(r.nextTokenMatches(Token.Type.SYMBOL, "+"));
	}

	@Test
	public void testNextToken() {
		// NAME, KEYWORD, NUMBER, SYMBOL, EOL, EOF };
		Recognizer r = new Recognizer("abc move 25 * \n");
		assertEquals(new Token(Token.Type.NAME, "abc"), r.nextToken());
		assertEquals(new Token(Token.Type.KEYWORD, "move"), r.nextToken());
		assertEquals(new Token(Token.Type.NUMBER, "25.0"), r.nextToken());
		assertEquals(new Token(Token.Type.SYMBOL, "*"), r.nextToken());
		assertEquals(new Token(Token.Type.EOL, "\n"), r.nextToken());
		assertEquals(new Token(Token.Type.EOF, "EOF"), r.nextToken());
	}

	@Test
	public void testPushBack() {
		Recognizer r = new Recognizer("abc 25");
		assertEquals(new Token(Token.Type.NAME, "abc"), r.nextToken());
		r.pushBack();
		assertEquals(new Token(Token.Type.NAME, "abc"), r.nextToken());
		assertEquals(new Token(Token.Type.NUMBER, "25.0"), r.nextToken());
	}

	@Test
	public void testIsEol(){
		Recognizer r = new Recognizer("abc\n");
		followedBy(r, "abc");
		assertTrue(r.isEol());
		r = new Recognizer("\n\nabc");
		assertTrue(r.isEol());
		followedBy(r, "abc");
	}

	@Test
	public void testIsComparator(){
		Recognizer r = new Recognizer("<=abc");
		assertTrue(r.isComparator());
		followedBy(r, "abc");
		r = new Recognizer("< abc <= abc = abc != abc >= abc > abc");
		for (int i = 0; i < 6; i++) {
			assertTrue(r.isComparator());
			followedBy(r, "abc");
		}
		assertFalse(r.isComparator());
	}

	@Test
	public void testIsExpression() {
		Recognizer r = new Recognizer("<=abc");
		assertFalse(r.isExpression());
		r = new Recognizer("abc <= abc");
		assertTrue(r.isExpression());
		r = new Recognizer("456 +a < 123-abc dfe <= 435 def = 34 abc 43!=23 45 >= 32 46>34");
		for (int i = 0; i < 7; i++) {
			assertTrue(r.isExpression());
		}
		assertFalse(r.isExpression());
	}
	@Test
	public void testIsFunctionCall() {
		Recognizer r =  new Recognizer("how(here) abc");
		assertTrue(r.isFunctionCall());
		assertEquals(new Token(Token.Type.NAME, "abc"), r.nextToken());
		r = new Recognizer("view(get, now) what(are, you, doing)");
		assertTrue(r.isFunctionCall());
		assertTrue(r.isFunctionCall());
		r = new Recognizer("today(is beautiful)"); 
		try {
			assertFalse(r.isFunctionCall());
			fail();
		}
		catch (SyntaxException e) {
		}
		followedBy(r, "beautiful");
		assertFalse(r.isFunctionCall());
		r = new Recognizer("abc() 123(he)");
		assertTrue(r.isFunctionCall());
		assertFalse(r.isFunctionCall());
		r = new Recognizer("he(134)");
		assertTrue(r.isFunctionCall());
	}

	@Test
	public void testIsMoveAction() {
		Recognizer r =  new Recognizer("move 123 \n move 123 move \n");
		assertTrue(r.isMoveAction());
		try {
			assertFalse(r.isMoveAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		try {
			assertFalse(r.isMoveAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		assertTrue(r.isEol());
	}

	@Test
	public void testIsMoveToAction() {
		Recognizer r =  new Recognizer("moveto 123,456 \n moveto 123 moveto 123, moveto 123,456");
		assertTrue(r.isMoveToAction());
		for (int i = 0; i < 3; i++) {
			try {
				assertFalse(r.isMoveToAction());
				fail();
			}
			catch (SyntaxException e) {
			}
		}
		r = new Recognizer("move 123,456\n");
		assertFalse(r.isMoveToAction());
	}

	@Test
	public void testIsTurnAction() {
		Recognizer r =  new Recognizer("turn 123 \n turn 123 turn \n");
		assertTrue(r.isTurnAction());
		try {
			assertFalse(r.isTurnAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		try {
			assertFalse(r.isTurnAction());
			fail();
		}
		catch (SyntaxException e) {
		}
	}
	@Test
	public void testIsTurnToAction() {
		Recognizer r =  new Recognizer("turnto 123 \n turnto 123 turnto \n turn 123 \n");
		assertTrue(r.isTurnToAction());
		try {
			assertFalse(r.isTurnToAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		try {
			assertFalse(r.isTurnToAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		assertFalse(r.isTurnToAction());
		assertTrue(r.isEol());
		assertFalse(r.isTurnToAction());
		followedBy(r, "turn 123 \n");
	}
	@Test
	public void testIsLineAction() {
		Recognizer r =  new Recognizer("line 3445, 234, 322, 433\n abc");
		assertTrue(r.isLineAction());
		followedBy(r, "abc");
		r = new Recognizer("line 234, 432 , , 345 \n");
		try {
			assertFalse(r.isLineAction());
			fail();
		}
		catch (SyntaxException e) {
		}
		followedBy(r, ", 345 \n");
	}

	@Test
	public void testIsAssignmentStatement() {
		Recognizer r =  new Recognizer("how = 3445 \n&");
		assertTrue(r.isAssignmentStatement());followedBy(r,"&");
		r = new Recognizer("how = 345");
		try {
			assertFalse(r.isAssignmentStatement());
			fail();
		}
		catch (SyntaxException e) {
		}
		r = new Recognizer ("how 234 \n");
		try {
			assertFalse(r.isAssignmentStatement());
			fail();
		}
		catch (SyntaxException e) {
		}
		followedBy(r, "234 \n");
	}

	@Test
	public void testIsBlock(){
		Recognizer r =  new Recognizer("{\n}\n*");
		assertTrue(r.isBlock());followedBy(r, "*");
		r = new Recognizer("{\n turn 20 \n return abc\n}\n");
		assertTrue(r.isBlock());
		r = new Recognizer("{\n turn \n return abc\n}\n");
		try{
			assertFalse(r.isBlock());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "\n");
		r = new Recognizer("\n turn 20 \n return abc\n}\\n");
		assertFalse(r.isBlock());
		r = new Recognizer("{turn 20 \n return abc\n}\n");
		try{
			assertFalse(r.isBlock());
			fail();
		}
		catch (SyntaxException e){}
		r = new Recognizer("{\n turn 20 \n return abc\n}*");
		try{
			assertFalse(r.isBlock());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "*");
		r = new Recognizer("(\n turn 20 \n return abc\n}*");
		assertFalse(r.isBlock());
	}

	@Test
	public void testIsFunctionDefinition(){
		Recognizer r =  new Recognizer("define how {\n}\n*");
		assertTrue(r.isFunctionDefinition());followedBy(r, "*");
		r =  new Recognizer("define how  using are, you, doing {\n}\n*");
		assertTrue(r.isFunctionDefinition());
		r = new Recognizer("define using are, you, doing {\n}\n*");
		try{
			assertFalse(r.isFunctionDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "using are");
		r = new Recognizer("define how using are, you, doing");
		try{
			assertFalse(r.isFunctionDefinition());
			fail();
		}
		catch (SyntaxException e){}
		r =  new Recognizer("turn how {\n}\n*");
		assertFalse(r.isFunctionDefinition());
	}
	@Test
	public void testIsLoopStatement(){
		Recognizer r =  new Recognizer("loop {\n}\n*");
		assertTrue(r.isLoopStatement());followedBy(r, "*");
		r = new Recognizer("loop *");
		try{
			assertFalse(r.isLoopStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "*");
		r = new  Recognizer("lop {\n}\n*");
		assertFalse(r.isLoopStatement());
		r = new  Recognizer("do {\n}\n*");
		assertFalse(r.isLoopStatement());
	}

	@Test
	public void testIsExitIfStatement(){
		Recognizer r =  new Recognizer("exit if 23 \n&");
		assertTrue(r.isExitIfStatement());followedBy(r,"&");
		r = new Recognizer("exit 23\n");
		try{
			assertFalse(r.isExitIfStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "23");
		r = new Recognizer("if exit 23 \n");
		assertFalse(r.isExitIfStatement());followedBy(r, "if");
		r =new Recognizer("exit if \n");
		try{
			assertFalse(r.isExitIfStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "\n");
		r = new Recognizer("exit if 23 &");
		try{
			assertFalse(r.isExitIfStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "&");
	}

	@Test
	public void testIsReturnStatement(){
		Recognizer r =  new Recognizer("return 23 \n&");
		assertTrue(r.isReturnStatement());followedBy(r,"&");
		r = new Recognizer("return \n");
		try{
			assertFalse(r.isReturnStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "\n");
		r = new Recognizer("return 23 &");
		try{
			assertFalse(r.isReturnStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "&");
		r = new Recognizer("exit 23 \n");
		assertFalse(r.isReturnStatement());
		followedBy(r, "exit");
	}

	@Test
	public void testIsDoStatement(){
		Recognizer r =  new Recognizer("do ab \n&");
		assertTrue(r.isDoStatement());followedBy(r,"&");
		r =  new Recognizer("do ab (sdf, 23, 13)\n");
		assertTrue(r.isDoStatement());
		r = new Recognizer("do \n");
		try{
			assertFalse(r.isDoStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "\n");
		r = new Recognizer("do ab &");
		try{
			assertFalse(r.isDoStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "&");
		r = new Recognizer("exit ab \n");
		assertFalse(r.isDoStatement());
		followedBy(r, "exit");
	}

	@Test
	public void testIsColorStatement(){
		Recognizer r =  new Recognizer("color black \n&");
		assertTrue(r.isColorStatement());followedBy(r,"&");
		r =  new Recognizer("color yellow\n");
		assertTrue(r.isColorStatement());
		r = new Recognizer("color \n");
		try{
			assertFalse(r.isColorStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "\n");
		r = new Recognizer("color ab \n");
		try{
			assertFalse(r.isColorStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "ab");
		r = new Recognizer("exit ab \n");
		assertFalse(r.isColorStatement());
		followedBy(r, "exit");
	}

	@Test
	public void testIsSwitchStatement(){
		Recognizer r =  new Recognizer("switch {\n }\n&");
		assertTrue(r.isSwitchStatement());followedBy(r,"&");
		r =  new Recognizer("switch {\n case abc = 123\n }\n");
		assertTrue(r.isSwitchStatement());
		r =  new Recognizer("switch {\n case abc = 123 \n turn 123\n move 345\n }\n");
		assertTrue(r.isSwitchStatement());
		r = new Recognizer("switch \n }\n");
		try{
			assertFalse(r.isSwitchStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "\n");
		r = new Recognizer("switch {\n case $ }\n");
		try{
			assertFalse(r.isSwitchStatement());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "$ }\n");
		r = new Recognizer("exit {\n }\n");
		assertFalse(r.isSwitchStatement());
		followedBy(r, "exit");
	}

	@Test
	public void testIsStatement(){
		Recognizer r =  new Recognizer("abc = 123\n&");
		assertTrue(r.isStatement());followedBy(r,"&");
		r = new Recognizer("loop {\n}\n");
		assertTrue(r.isStatement());
		r = new Recognizer("exit if 45 = 43 \n");
		assertTrue(r.isStatement());
		r = new Recognizer("switch {\n}\n");
		assertTrue(r.isStatement());
		r = new Recognizer("return 45 \n");
		assertTrue(r.isStatement());
		r = new Recognizer("do abc \n");
		assertTrue(r.isStatement());
		r = new Recognizer("color black \n");
		assertTrue(r.isStatement());
		r = new Recognizer("abc {\n}\n");
		try{
			assertFalse(r.isStatement());
			fail();
		}
		catch (SyntaxException e){}
	}

	@Test
	public void testIsParameterList(){
		Recognizer r =  new Recognizer("()&");
		assertTrue(r.isParameterList());followedBy(r,"&");
		r =  new Recognizer("(abc)");
		assertTrue(r.isParameterList());
		r =  new Recognizer("(abc, def, ghi)");
		assertTrue(r.isParameterList());
		r = new Recognizer("(abc$");
		try{
			assertFalse(r.isParameterList());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "$");
		r = new Recognizer("abc)");
		assertFalse(r.isParameterList());
	}

	@Test
	public void testIsAction(){
		Recognizer r =  new Recognizer("move 321\n&");
		assertTrue(r.isAction());followedBy(r,"&");
		r =  new Recognizer("moveto 321, 435\n");
		assertTrue(r.isAction());
		r =  new Recognizer("turn 321\n");
		assertTrue(r.isAction());
		r =  new Recognizer("turnto 321\n");
		assertTrue(r.isAction());
		r =  new Recognizer("line 321, 3212,432,234\n");
		assertTrue(r.isAction());
		r =  new Recognizer("abc \n");
		try{
			assertFalse(r.isCommand());
			fail();
		}
		catch (SyntaxException e){}
	}

	@Test
	public void testIsCommand(){
		Recognizer r =  new Recognizer("turn 321\n&");
		assertTrue(r.isCommand());followedBy(r,"&");
		r =  new Recognizer("exit if ab = 34 \n");
		assertTrue(r.isCommand());
		r = new Recognizer("abc 321 \n");
		try{
			assertFalse(r.isCommand());
			fail();
		}
		catch (SyntaxException e){}
	}

	@Test
	public void testIsInitializationBlock(){
		Recognizer r = new Recognizer("initially {\n move 321\n }\n&");
		assertTrue(r.isInitializationBlock());followedBy(r,"&");
		r =  new Recognizer("initially \n move 321\n}\n");
		try{
			assertFalse(r.isInitializationBlock());
			fail();
		}
		catch (SyntaxException e){}
		r = new Recognizer("initial {\n move 321\n }\n");
		assertFalse(r.isInitializationBlock());
	}

	@Test
	public void testIsVarDeclaration(){
		Recognizer r = new Recognizer("var abc\n&");
		assertTrue(r.isVarDeclaration());followedBy(r,"&");
		r = new Recognizer("var abc, def, efg\n");
		assertTrue(r.isVarDeclaration());
		r = new Recognizer("var \n");
		try{
			assertFalse(r.isVarDeclaration());
			fail();
		}
		catch (SyntaxException e){}
	}

	@Test
	public void testIsBugDefinition(){
		Recognizer r = new Recognizer("Bug how {\n turn 234 \n}\n&");
		assertTrue(r.isBugDefinition()); followedBy(r,"&");
		r = new Recognizer("Bug how {\n var abc \n var cde \n turn 234 \n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n var abc \n var cde \n initially {\n}\n turn 234 \n move 123 \n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n var abc \n var cde \n initially {\n}\n turn 234 \n move 123 \n moveto 123, 345 \n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n var abc \n var cde \n initially {\n}\n turn 234 \n move 123 \n moveto 123, 345 \n" +
		" define are {\n}\n define you {\n}\n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n initially {\n}\n turn 234 \n move 123 \n moveto 123, 345 \n" +
		" define are {\n}\n define you {\n}\n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n var abc \n var cde \n turn 234 \n move 123 \n moveto 123, 345 \n" +
		" define are {\n}\n define you {\n}\n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n var abc \n var cde \n initially {\n}\n turn 234 \n " +
		" define are {\n}\n define you {\n}\n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n var abc \n var cde \n initially {\n}\n turn 234 \n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n turn 234 \n move 123 \n moveto 123, 345 \n" +
		" define are {\n}\n define you {\n}\n}\n&");
		assertTrue(r.isBugDefinition());followedBy(r,"&");
		r = new Recognizer("Bug how {\n }\n");
		try{
			assertFalse(r.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		r = new Recognizer("Bug {\n turn 234 \n}\n");
		try{
			assertFalse(r.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "{");
		r = new Recognizer("Bug how \n turn 234 \n}\n");
		try{
			assertFalse(r.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "\n");
		r = new Recognizer("Bug how { turn 234 \n}\n");
		try{
			assertFalse(r.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "turn");
		r = new Recognizer("Bug how {\n turn 234 \n");
		try{
			assertFalse(r.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		r = new Recognizer("Bug how {\n turn 234 \n}%");
		try{
			assertFalse(r.isBugDefinition());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "%");
		r = new Recognizer("bug how {\n turn 234 \n}\n");
		assertFalse(r.isBugDefinition());
	}

	@Test
	public void testIsAllbugsCode(){
		Recognizer r = new Recognizer("Allbugs {\n }\n&");
		assertTrue(r.isAllbugsCode()); followedBy(r,"&");
		r = new Recognizer("Allbugs {\n var abc \n var cde \n }\n&");
		assertTrue(r.isAllbugsCode()); followedBy(r,"&");
		r = new Recognizer("Allbugs {\n define abc {\n}\n }\n&");
		assertTrue(r.isAllbugsCode()); followedBy(r,"&");
		r = new Recognizer("Allbugs {\n var abc \n var cde \n define abc {\n}\n }\n&");
		assertTrue(r.isAllbugsCode()); followedBy(r,"&");
		r = new Recognizer("Allbugs {\n define abc {\n}\n var abc \n var cde \n  }\n&");
		try{
			assertFalse(r.isAllbugsCode());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "var");
		r = new Recognizer("Allbugs \n }\n&");
		try{
			assertFalse(r.isAllbugsCode());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "\n");
		r = new Recognizer("Allbugs { }\n&");
		try{
			assertFalse(r.isAllbugsCode());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "}");
		r = new Recognizer("Allbugs {\n &");
		try{
			assertFalse(r.isAllbugsCode());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "&");
	}

	@Test
	public void testIsProgram(){
		Recognizer r = new Recognizer("Bug how {\n turn 234 \n}\n&");
		assertTrue(r.isProgram()); followedBy(r,"&");
		r = new Recognizer("Allbugs {\n }\n Bug how {\n turn 234 \n}\n&");
		assertTrue(r.isProgram()); followedBy(r,"&");
		r = new Recognizer("Allbugs {\n }\n Bug how {\n turn 234 \n}\n Bug how {\n turn 234 \n}\n&");
		assertTrue(r.isProgram()); followedBy(r,"&");
		r = new Recognizer("Allbugs {\n }\n Bug how {\n turn 234 \n}\n Bug how {\n turn 234 \n}\nBug how {\n turn 234 \n}\n&");
		assertTrue(r.isProgram()); followedBy(r,"&");
		r = new Recognizer("&");
		assertFalse(r.isProgram()); followedBy(r,"&");
		r = new Recognizer("Allbugs {\n }\n&");
		try{
			assertFalse(r.isProgram());
			fail();
		}
		catch (SyntaxException e){}
		followedBy(r, "&");
		r = new Recognizer("Bug TopLeft {  \n" + 
				"   initially {\n" + 
				"       x=0\n" + 
				"       y=0\n" + 
				"       angle=270\n" + 
				"  }\n" + 
				"  color red\n" + 
				"     loop {\n" + 
				"       turnto direction (BottomLeft)\n" + 
				"       move 2\n" + 
				"       Line x,y,BottomLeft.x,BottomLeft.y\n" + 
				"         exit if distance(BottomLeft)<2\n" + 
				"  }\n" + 
				"}\n" + 
				"\n" + 
				"Bug BottomLeft {  \n" + 
				"   initially {\n" + 
				"       x=0\n" + 
				"       y=100\n" + 
				"       angle=0\n" + 
				"  }\n" + 
				"  color yellow\n" + 
				"     loop {\n" + 
				"       turnto direction (BottomRight)\n" + 
				"       move 2\n" + 
				"       Line x,y,BottomRight.x,BottomRight.y\n" + 
				"         exit if distance(BottomRight)<2\n" + 
				"  }\n" + 
				"}\n" + 
				"\n" + 
				"Bug BottomRight {  \n" + 
				"   initially {\n" + 
				"       x=100\n" + 
				"       y=100\n" + 
				"       angle=90\n" + 
				"  }\n" + 
				"  color green\n" + 
				"     loop {\n" + 
				"       turnto direction (TopRight)\n" + 
				"       move 2\n" + 
				"       Line x,y,TopRight.x,TopRight.y\n" + 
				"         exit if distance(TopRight)<2\n" + 
				"  }\n" + 
				"}\n" + 
				"\n" + 
				"Bug TopRight {  \n" + 
				"   initially {\n" + 
				"       x=100\n" + 
				"       y=0\n" + 
				"       angle=180\n" + 
				"  }\n" + 
				"  color blue\n" + 
				"     loop {\n" + 
				"       turnto direction (TopLeft)\n" + 
				"       move 2\n" + 
				"       Line x,y,TopLeft.x,TopLeft.y\n" + 
				"         exit if distance(TopLeft)<2\n" + 
				"  }\n" + 
				"}\n" + 
				"");
	}

//	----- "Helper" methods

	/**
	 * This method is given a String containing some or all of the
	 * tokens that should yet be returned by the Tokenizer, and tests
	 * whether the Tokenizer in fact has those Tokens. To succeed,
	 * everything in the given String must still be in the Tokenizer,
	 * but there may be additional (untested) Tokens to be returned.
	 * This method is primarily to test whether rejected Tokens are
	 * pushed back appropriately.
	 * 
	 * @param recognizer The Recognizer whose Tokenizer is to be tested.
	 * @param expectedTokens The Tokens we expect to get from the Tokenizer.
	 */
	private void followedBy(Recognizer recognizer, String expectedTokens) {
		int expectedType;
		int actualType;
		StreamTokenizer actual = recognizer.tokenizer;

		Reader reader = new StringReader(expectedTokens);
		StreamTokenizer expected = recognizer.createTokenizer(reader);

		try {
			while (true) {
				expectedType = expected.nextToken();
				if (expectedType == StreamTokenizer.TT_EOF) break;
				actualType = actual.nextToken();
				assertEquals(expectedType, actualType);
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
	@Test
	public void leeKuoTest() {
	    Recognizer r = new Recognizer("color \n $");
	    assertFalse(r.isDoStatement());
	    followedBy(r, "color \n $");
	}
}

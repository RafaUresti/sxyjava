
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * This class consists of a number of methods that "recognize" strings
 * composed of Tokens that follow the indicated grammar rules for each
 * method.
 * <p>Each method may have one of three outcomes:
 * <ul>
 *   <li>The method may succeed, returning <code>true</code> and
 *      consuming the tokens that make up that particular nonterminal.</li>
 *   <li>The method may fail, returning <code>false</code> and not
 *       consuming any tokens.</li>
 *   <li>(Some methods only) The method may determine that an
 *       unrecoverable error has occurred and throw a
 *       <code>SyntaxException</code></li>.
 * </ul>
 * @author David Matuszek
 * @author Xiaoyi Sheng
 * @version Feb 28, 2008
 */
public class Recognizer {

	StreamTokenizer tokenizer = null;

    /**
     * Constructs a Recognizer for the given string.
     * @param text The string to be recognized.
     */
    public Recognizer(String text) {
        Reader reader = new StringReader(text);
        tokenizer = createTokenizer(reader);
    }

    /**
     * Creates a StreamTokenizer for the given <code>reader</code>
     * and sets a bunch of parameters for it.
     * 
     * @param reader The input source to be tokenized.
     * @return The customized tokenizer.
     */
    public StreamTokenizer createTokenizer(Reader reader) {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        tokenizer.eolIsSignificant(true);
        tokenizer.slashStarComments(true);
        tokenizer.slashSlashComments(true);
        tokenizer.lowerCaseMode(false);
        //Includes all special symbols except for the numbers and letters
        tokenizer.ordinaryChars(33, 47);
        tokenizer.ordinaryChars(58, 64);
        tokenizer.ordinaryChars(91, 96);
        tokenizer.ordinaryChars(123, 126);
        tokenizer.quoteChar('\"');
        return tokenizer;
    }


	/**
	 * Tries to recognize an &lt;expression&gt;.
	 * <pre>&lt;expression&gt; ::= &lt;arithmetic expression&gt; { &lt;comparator&gt; &lt;arithmetic expression&gt; }</pre>
	 * A <code>SyntaxException</code> will be thrown if an comparator
	 * is present but not followed by a valid &lt;arithmetic expression&gt;.
	 * @return <code>true</code> if an expression is recognized.
	 */
	public boolean isExpression() {
		if (!isArithmeticExpression())
			return false;
		while (isComparator()) {
			if (!isArithmeticExpression())
				error("No arithmetic expression after comparator");
		}
		return true;
	}

	/**
	 * Tries to recognize an &lt;expression&gt;.
	 * <pre>&lt;expression&gt; ::= &lt;term&gt; { &lt;add_operator&gt; &lt;expression&gt; }</pre>
	 * A <code>SyntaxException</code> will be thrown if the add_operator
	 * is present but not followed by a valid &lt;expression&gt;.
	 * @return <code>true</code> if an expression is recognized.
	 */
	public boolean isArithmeticExpression() {
		if (!isTerm()) {
			return false;
		}
		while (isAddOperator()) {
			if (!isArithmeticExpression()) {
				error("Error in arithmetic expression after '+' or '-'");
			}
		}
		return true;
	}

	/**
	 * Tries to recognize a &lt;term&gt;.
	 * <pre>&lt;term&gt; ::= &lt;factor&gt; { &lt;multiply_operator&gt; &lt;term&gt;}</pre>
	 * A <code>SyntaxException</code> will be thrown if the multiply_operator
	 * is present but not followed by a valid &lt;term&gt;.
	 * @return <code>true</code> if a term is recognized.
	 */
	public boolean isTerm() {
		if (!isFactor()) {
			return false;
		}
		while (isMultiplyOperator()) {
			if (!isTerm()) {
				error("No term after '+' or '-'");
			}
		}
		return true;
	}

	/**
	 * Tries to recognize a &lt;factor&gt;.
	 * <pre>&lt;factor&gt; ::= &lt;name&gt; "." &lt;name&gt;</pre>
	 *           | &lt;name&gt; "(" &lt;parameter list&gt; ")"
	 *           | &lt;name&gt;
	 *           | &lt;number&gt;
	 *           | "(" &lt;expression&gt; ")"</pre>
	 * A <code>SyntaxException</code> will be thrown if the opening
	 * parenthesis is present but not followed by a valid
	 * &lt;expression&gt; and a closing parenthesis.
	 * @return <code>true</code> if a factor is recognized.
	 */
	public boolean isFactor() {
	    boolean startsWithUnary = symbol("+") || symbol("-");
	    if (isVariable()) {
	        if (symbol(".")) {              // reference to another Bug
	            if (name()) return true;
	            else error("Incorrect use of dot notation");
	        }
	        else if (isParameterList()) return true; // function call
	        else return true;                        // just a variable
	    }
	    if (number()) return true;
	    if (symbol("(")) {
	        if (!isExpression()) error("Error in parenthesized expression");
	        if (!symbol(")")) error("Unclosed parenthetical expression");
	        return true;
	   }
	   if (startsWithUnary) error("Error in factor");
	   return false;
	}


	/**
	 * Tries to recognize a &lt;parameter list&gt;.
	 * <pre>&lt;parameter list&gt; ::= "(" [ &lt;expression&gt; { "," &lt;expression&gt; } ] ")"</pre>
	 * A <code>SyntaxException</code> will be thrown if the opening
	 * parenthesis is present but not followed by a closing parenthesis.
	 * @return <code>true</code> if a &lt;parameter list&gt; is recognized.
	 */
	public boolean isParameterList() {
		if (!symbol("(")) {
			return false;
		}
		if (isExpression()) {
			while (symbol(",")) {
				checkExpression();
			}
		}
		if (!symbol(")")) {
			error("Parameter list should end with ')'");
		}
		return true;
	}

	/**
	 * Tries to recognize an &lt;add_operator&gt;.
	 * <pre>&lt;add_operator&gt; ::= "+" | "-"</pre>
	 * @return <code>true</code> if an addop is recognized.
	 */
	public boolean isAddOperator() {
		return symbol("+") || symbol("-");
	}

	/**
	 * Tries to recognize a &lt;multiply_operator&gt;.
	 * <pre>&lt;multiply_operator&gt; ::= "*" | "/"</pre>
	 * @return <code>true</code> if a multiply_operator is recognized.
	 */
	public boolean isMultiplyOperator() {
		return symbol("*") || symbol("/");
	}

	/**
	 * Tries to recognize a &lt;variable&gt;.
	 * <pre>&lt;variable&gt; ::= &lt;NAME&gt;</pre>
	 * @return <code>true</code> if a variable is recognized.
	 */
	public boolean isVariable() {
		return name();
	}

//	----- Private "helper" methods
	/**
	 * Tests whether the next token is a number. If it is, the token
	 * is consumed, otherwise it is not.
	 * 
	 * @return <code>true</code> if the next token is a number.
	 */
	private boolean number() {
		return nextTokenMatches(Token.Type.NUMBER);
	}

	/**
	 * Tests whether the next token is a name. If it is, the token
	 * is consumed, otherwise it is not.
	 * 
	 * @return <code>true</code> if the next token is a name.
	 */
	private boolean name() {
		return nextTokenMatches(Token.Type.NAME);
	}

	/**
	 * Tests whether the next token is the expected name. If it is, the token
	 * is consumed, otherwise it is not.
	 * 
	 * @param expectedName The String value of the expected next token.
	 * @return <code>true</code> if the next token is a name with the expected value.
	 */
	private boolean name(String expectedName) {
		return nextTokenMatches(Token.Type.NAME, expectedName);
	}

	/**
	 * Tests whether the next token is the expected keyword. If it is, the token
	 * is moved to the stack, otherwise it is not.
	 * 
	 * @param expectedKeyword The String value of the expected next token.
	 * @return <code>true</code> if the next token is a keyword with the expected value.
	 */
	private boolean keyword(String expectedKeyword) {
		return nextTokenMatches(Token.Type.KEYWORD, expectedKeyword);
	}

	/**
	 * Tests whether the next token is the expected symbol. If it is,
	 * the token is consumed, otherwise it is not.
	 * 
	 * @param expectedSymbol The String value of the token we expect
	 *    to encounter next.
	 * @return <code>true</code> if the next token is the expected symbol.
	 */
	boolean symbol(String expectedSymbol) {
		return nextTokenMatches(Token.Type.SYMBOL, expectedSymbol);
	}

	/**
	 * Tests whether the next token has the expected type. If it does,
	 * the token is consumed, otherwise it is not. This method would
	 * normally be used only when the token's value is not relevant.
	 * 
	 * @param type The expected type of the next token.
	 * @return <code>true</code> if the next token has the expected type.
	 */
	boolean nextTokenMatches(Token.Type type) {
		Token t = nextToken();
		if (t.type == type) {
			return true;
		} else {
			pushBack();
		}
		return false;
	}

	/**
	 * Tests whether the next token has the expected type and value.
	 * If it does, the token is consumed, otherwise it is not. This
	 * method would normally be used when the token's value is
	 * important.
	 * 
	 * @param type The expected type of the next token.
	 * @param value The expected value of the next token; must
	 *              not be <code>null</code>.
	 * @return <code>true</code> if the next token has the expected type.
	 */
	boolean nextTokenMatches(Token.Type type, String value) {
		Token t = nextToken();
		if (type == t.type && value.equals(t.value)) {
			return true;
		} else {
			pushBack();
		}
		return false;
	}

	/**
	 * Returns the next Token.
	 * @return The next Token.
	 */
	Token nextToken() {
		int code;
		try {
			code = tokenizer.nextToken();
		} catch (IOException e) {
			throw new Error(e);
		} // Should never happen
		switch (code) {
		case StreamTokenizer.TT_WORD:
			if (Token.KEYWORDS.contains(tokenizer.sval)) {
				return new Token(Token.Type.KEYWORD, tokenizer.sval);
			} else {
				return new Token(Token.Type.NAME, tokenizer.sval);
			}
		case StreamTokenizer.TT_NUMBER:
			return new Token(Token.Type.NUMBER, tokenizer.nval + "");
		case StreamTokenizer.TT_EOL:
			return new Token(Token.Type.EOL, "\n");
		case StreamTokenizer.TT_EOF:
			return new Token(Token.Type.EOF, "EOF");
		default:
			return new Token(Token.Type.SYMBOL, ((char) code) + "");
		}
	}

	/**
	 * Returns the most recent Token to the tokenizer.
	 */
	void pushBack() {
		tokenizer.pushBack();
	}

	/**
	 * Utility routine to throw a <code>SyntaxException</code> with the
	 * given message.
	 * @param message The text to put in the <code>SyntaxException</code>.
	 */
	private void error(String message) {
		throw new SyntaxException(message);
	}

	/**
	 * Tries to recognize a &lt;action&gt;.
	 * <pre>&lt;action&gt; ::= &lt;move action&gt;
           | &lt;moveto action&gt;
           | &lt;turn action&gt;
           | &lt;turnto action&gt;
           | &lt;line action&gt;</pre>
	 * @return <code>true</code> if a &lt;action&gt; is recognized.
	 */
	public boolean isAction() {
		return isMoveAction()||isMoveToAction()||isTurnAction()||
		isTurnToAction()||isLineAction();
	}

	/**
	 * Tries to recognize a &lt;allbugs code&gt;.
	 * <pre>&lt;allbugs code&gt; ::= &quot;Allbugs&quot;  &quot;{&quot; &lt;eol&gt;
                       { &lt;var declaration&gt; 
                       { &lt;function definition&gt; 
                   &quot;}&quot; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;allbugs code&gt; is recognized.
	 */
	public boolean isAllbugsCode() {
		if(!keyword("Allbugs")) return false;
		checkLeftBrace();
		checkEol();
		while(isVarDeclaration());
		while(isFunctionDefinition());
		checkRightBrace();
		checkEol();
		return true;
	}

	/**
	 * Tries to recognize a &lt;assignment statement&gt;.
	 * <pre>&lt;assignment statement&gt; ::= &lt;variable&gt; &quot;=&quot; &lt;expression&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if &quot;=&quot is not present
	 * @return <code>true</code> if a &lt;assignment statement&gt; is recognized.
	 */
	public boolean isAssignmentStatement() {
		if(!isVariable()) return false;
		if(!symbol("=")) error("\"=\" missing!");
		checkExpression();
		checkEol();
		return true;
	}

	/**
	 * Tries to recognize a &lt;block&gt;.
	 * <pre>&lt;block&gt; ::= "{" &lt;eol&gt; { &lt;command&gt;   "}" &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;block&gt; is recognized.
	 */
	public boolean isBlock() {
		if(!symbol("{")) return false;
		checkEol();
		while (isCommand());
		checkRightBrace();
		checkEol();
		return true;
	}

	/**
	 * Tries to recognize a &lt;bug definition&gt;.
		<pre>&lt;bug definition&gt; ::= &quot;Bug&quot; &lt;name&gt; &quot;{&quot; &lt;eol&gt;
                         { &lt;var declaration&gt; 
                         [ &lt;initialization block&gt; ]
                         &lt;command&gt;
                         { &lt;command&gt; 
                         { &lt;function definition&gt; 
                    &quot;}&quot; &lt;eol&gt;</pre>
	 * <code>SyntaxException</code> will be thrown if name or command is not present
	 * @return <code>true</code> if a &lt;bug definition&gt; is recognized.
	 */
	public boolean isBugDefinition() {
		if(!keyword("Bug")) return false;
		if(!name()) error("Bug name is missing!");
		checkLeftBrace();
		checkEol();
		while(isVarDeclaration());
		isInitializationBlock();
		if(!isCommand()) error("Command is missing!");
		while(isCommand());
		while(isFunctionDefinition());
		checkRightBrace();
		checkEol();
		return true;
	}

	/**
	 * Tries to recognize a &lt;color statement&gt;.
	 * <pre>&lt;color statement&gt; ::= "color" &lt;KEYWORD&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if a KEYWORD is not present
	 * @return <code>true</code> if a &lt;color statement&gt; is recognized.
	 */
	public boolean isColorStatement() {
		if(!keyword("color")) return false;
		if(!(nextTokenMatches(Token.Type.KEYWORD)))
			error("A keyword is missing!");
		checkEol();
		return true;
	}
	/**
	 * Tries to recognize a &lt;command&gt;.
	 * <pre>&lt;command&gt; ::= &lt;action&gt;
            | &lt;statement&gt; </pre>
	 * @return <code>true</code> if a &lt;command&gt; is recognized.
	 */
	public boolean isCommand() {
		return isAction()||isStatement();
	}

	/**
	 * Tries to recognize a &lt;comparator&gt;.
	 * <pre>&lt;comparator&gt; ::= "&lt;" | "&lt;=" | "=" | "!=" "&gt;=" | "&gt;"</pre>
	 * @return <code>true</code> if a &lt;comparator&gt; is recognized.
	 */
	public boolean isComparator() {
		if (symbol("<")) {
			symbol("=");
			return true;
		}
		if (symbol("=")) return true;
		if (symbol("!")&& symbol("=")) return true;
		if (symbol(">")) {
			symbol("=");
			return true;
		}
		return false;
	}


	/**
	 * Tries to recognize a &lt;do statement&gt;.
	 * <pre>&lt;do statement&gt; ::= "do" &lt;variable&gt; [ &lt;parameter list&gt; ] &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if variable is not present
	 * @return <code>true</code> if a &lt;do statement&gt; is recognized.
	 */
	public boolean isDoStatement() {
		if (!keyword("do")) return false;
		if(!isVariable()) error("Variable is missing!");
		isParameterList();
		checkEol();
		return true;
	}
	/**
	 * Tries to recognize an &lt;eol&gt;
	 * <pre>&lt;eol&gt; ::= &lt;EOL&gt;{&lt;EOL&gt;}</pre>
	 * @return <code>true</code> if EOL is recognized
	 */
	public boolean isEol() {
		if (!(nextTokenMatches(Token.Type.EOL)))
			return false;
		while((nextTokenMatches(Token.Type.EOL))){
		}
		return true;
	}

 	/**
	 * Tries to recognize a &lt;exit if statement&gt;.
	 * <pre>&lt;exit if statement&gt; ::= "exit" "if" &lt;expression&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if &quot;if&quot doesn't follow exit
	 * @return <code>true</code> if a &lt;exit if statement&gt; is recognized.
	 */
	public boolean isExitIfStatement() {
		if(!keyword("exit"))
			return false;
		if (!keyword("if"))
			error("\"if\" is missing");
		checkExpression();
		checkEol();
		return true;
	}
	
 	/**
	 * Tries to recognize a &lt;function call&gt;.
	 * <pre>&lt;function call&gt; ::= &lt;NAME&gt; &lt;parameter list&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if parameter list is not present
	 * @return <code>true</code> if a &lt;function call&gt; is recognized.
	 */
	public boolean isFunctionCall() {
		if(!name()) return false;
		if (!isParameterList())
			error("Parameter list missing!");
		return true;
	}

 	/**
	 * Tries to recognize a &lt;function definition&gt;.
	 * <pre>&lt;function definition&gt; ::= &quot;define&quot; &lt;NAME&gt; [ &quot;using&quot; &lt;variable&gt; { &quot;,&quot; &lt;variable&gt;   ] &lt;block&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if a function name doesn't follow &quot;define&quot; or variable doesn't follow using or &quot;,&quot;
	 * @return <code>true</code> if a &lt;function definition&gt; is recognized.
	 */
	public boolean isFunctionDefinition() {
		if(!keyword("define")) return false;
		if(!name()) error("Function name missing!");
		if(keyword("using")){
			if(!isVariable()) error("Variable missing for \"using\"");
			while(symbol(",")){
				if(!isVariable()) error("Variable missing after \",\"");
			}
		}
		if(!isBlock()) error("Block is missing");
		return true;
	}

 	/**
	 * Tries to recognize a &lt;initialization block&gt;.
	 * <pre>&lt;initialization block&gt; ::= &quot;initially&quot; &lt;block&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown is block doesn't follow initially
	 * @return <code>true</code> if a &lt;initialization block&gt; is recognized.
	 */
	public boolean isInitializationBlock() {
		if(!keyword("initially")) return false;
		if(!isBlock()) error("Initialization block is missing!");
		return true;
	}
 	/**
	 * Tries to recognize a &lt;line action&gt;.
	 * <pre>&lt;line action&gt; ::= "line" &lt;expression&gt; ","&lt;expression&gt; &quot;,&quot;&lt;expression&gt; &quot;,&quot; &lt;expression&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if expression is missing or &quot;,&quot; is not present between expressions
	 * @return <code>true</code> if a &lt;line action&gt; is recognized.
	 */
	public boolean isLineAction() {
		if (!keyword("line"))
			return false;
		for (int i = 0; i<3; i++) {
			checkExpression();
			if (!symbol(","))
				error("\",\" missing");
		}
		checkExpression();
		checkEol();
		return true;
	}


 	/**
	 * Tries to recognize a &lt;loop statement&gt;.
	 * <pre>&lt;loop statement&gt; ::= "loop" &lt;block&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if block is missing
	 * @return <code>true</code> if a &lt;loop statement&gt; is recognized.
	 */
	public boolean isLoopStatement() {
		if (!keyword("loop"))
			return false;
		if (!isBlock())
			error("Block missing!");
		return true;
	}

 	/**
	 * Tries to recognize a &lt;move action&gt;.
	 * <pre>&lt;move action&gt; ::= "move" &lt;expression&gt; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;move action&gt; is recognized.
	 */
	public boolean isMoveAction() {
		if (!keyword("move"))
			return false;
		checkExpression();
		checkEol();
		return true;
	}

 	/**
	 * Tries to recognize a &lt;moveto action&gt;.
	 * <pre>&lt;moveto action&gt; ::= "moveto" &lt;expression&gt; "," &lt;expression&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if &quot;,&quot; is not present between two expressions
	 * @return <code>true</code> if a &lt;moveto action&gt; is recognized.
	 */
	public boolean isMoveToAction() {
		if(!keyword("moveto"))
			return false;
		checkExpression();
		if(!symbol(","))
			error("\",\" missing after  \"moveto <expression>\"");
		checkExpression();
		checkEol();
		return true;
	}
	
 	/**
	 * Tries to recognize a &lt;program&gt;.
	 * <pre>&lt;program&gt; ::= [ &lt;allbugs code&gt; ]
                &lt;bug definition&gt;
              { &lt;bug definition&gt; }</pre>
	 * A <code>SyntaxException</code> will be thrown if bug definition is missing
	 * @return <code>true</code> if a &lt;program&gt; is recognized.
	 */
	public boolean isProgram() {
		while(isEol());
		if(isAllbugsCode()){
			if(!isBugDefinition()) error("bug definition is missing!");
		}
		else if(!isBugDefinition()){
			return false;
		}
		while(isBugDefinition());
		return true;
	}

 	/**
	 * Tries to recognize a &lt;return statement&gt;.
	 * <pre>&lt;return statement&gt; ::= &quot;return&quot; &lt;expression&gt; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;return statement&gt; is recognized.
	 */
	public boolean isReturnStatement() {
		if (!keyword("return")) return false;
		checkExpression();
		checkEol();
		return true;
	}

 	/**
	 * Tries to recognize a &lt;statement&gt;.
	 * <pre>&lt;statement&gt; ::= &lt;assignment statement&gt;
              | &lt;loop statement&gt;
              | &lt;exit if statement&gt;
              | &lt;switch statement&gt;
              | &lt;return statement&gt;
              | &lt;do statement&gt;
              | &lt;color statement&gt;</pre>
	 * @return <code>true</code> if a &lt;statement&gt; is recognized.
	 */
	public boolean isStatement() {
		return isAssignmentStatement()||isLoopStatement()||
		isExitIfStatement()||isSwitchStatement()||isReturnStatement()||
		isDoStatement()||isColorStatement();
	}
 	/**
	 * Tries to recognize a &lt;switch statement&gt;.
	 * <pre>&lt;switch statement&gt; ::= "switch" "{" &lt;eol&gt;
                         { "case" &lt;expression&gt; &lt;eol&gt;
                              { &lt;command&gt;  
                       "}" &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;switch statement&gt; is recognized.
	 */
	public boolean isSwitchStatement() {
		if (!keyword("switch")) return false;
		checkLeftBrace();
		checkEol();
		while (keyword("case")){
			checkExpression();
			checkEol();
			while(isCommand());
		}
		checkRightBrace();
		checkEol();
		return true;
	}
	
 	/**
	 * Tries to recognize a &lt;turn action&gt;.
	 * <pre>&lt;turn action&gt; ::= "turn" &lt;expression&gt; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;turn action&gt; is recognized.
	 */
	public boolean isTurnAction() {
		if (!keyword("turn"))
			return false;
		checkExpression();
		checkEol();
		return true;
	}
 	/**
	 * Tries to recognize a &lt;turnto action&gt;.
	 * <pre>&lt;turnto action&gt; ::= "turnto" &lt;expression&gt; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;turnto action&gt; is recognized.
	 */
	public boolean isTurnToAction() {
		if (!keyword("turnto"))
			return false;
		checkExpression();
		checkEol();
		return true;
	}
 	/**
	 * Tries to recognize a &lt;var declaration&gt;.
	 * <pre>&lt;var declaration&gt; ::= &quot;var&quot; &lt;NAME&gt; { &quot;,&quot; &lt;NAME&gt;}&lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if variable name is missing or &quot;,&quot; is missing between variable names. 
	 * @return <code>true</code> if a &lt;var declaration&gt; is recognized.
	 */
	public boolean isVarDeclaration() {
		if(!keyword("var")) return false;
		if(!name()) error("Var name missing!");
		while(symbol(",")){
			if(!name()) error("Var name missing after \",\"");
		}
		checkEol();
		return true;
	}

//	Helper methods
	private void checkEol() {
		if(!isEol()) error("EOL is missing!");
	}
	private void checkLeftBrace() {
		if(!symbol("{")) error("\"{\" is missing");
	}
	private void checkRightBrace() {
		if(!symbol("}")) error("\"}\" is missing");
	}
	private void checkExpression() {
		if (!isExpression())
			error("Expression missing");
	}
}

package parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.*;
import tree.Tree;
/**
 * This class consists of a number of methods that given a stream of tokens,
 * build trees that represent the structures of the programs represented by the strings.
 * If a string is not recognized by the method, the method will return 
 * <code>false</code> or throw <code>SyntaxException</code>.
 * @author Xiaoyi Sheng
 * @author David Matuszek
 * @version April 10th, 2008
 */
public class Parser {

	StreamTokenizer tokenizer = null;
	public Stack<Tree<Token>> stack = new Stack<Tree<Token>>();
    /**
     * Constructs a Parser for the given string.
     * @param text The string to be recognized.
     */
    public Parser(String text) {
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
    static StreamTokenizer createTokenizer(Reader reader) {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        tokenizer.parseNumbers();
        tokenizer.eolIsSignificant(true);
        tokenizer.slashStarComments(true);
        tokenizer.slashSlashComments(true);
        tokenizer.lowerCaseMode(false);
        tokenizer.ordinaryChars(33, 47);
        tokenizer.ordinaryChars(58, 64);
        tokenizer.ordinaryChars(91, 96);
        tokenizer.ordinaryChars(123, 126);
        tokenizer.quoteChar('\"');
        return tokenizer;
    }


	/**
	 * Tries to build an &lt;expression&gt; on the global stack.
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
			makeTree(2,3,1);
		}
		return true;
	}

	/**
	 * Tries to build an &lt;arithmetic expression&gt; on the global stack.
	 * <pre>&lt;arithmetic expression&gt; ::= &lt;term&gt; { &lt;add_operator&gt; &lt;term&gt; }</pre>
	 * A <code>SyntaxException</code> will be thrown if the add_operator
	 * is present but not followed by a valid &lt;term&gt;.
	 * @return <code>true</code> if an arithmetic expression is recognized.
	 */
	public boolean isArithmeticExpression() {
		if (!isTerm()) {
			return false;
		}
		while (isAddOperator()) {
			if (!isTerm()) {
				error("Error in arithmetic expression after '+' or '-'");
			}
			makeTree(2, 3, 1);
		}
		return true;
	}

	/**
	 * Tries to build a &lt;term&gt; on the global stack.
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
			if (!isFactor()) {
				error("No term after '+' or '-'");
			}
			  makeTree(2, 3, 1);
		}
		return true;
	}

    /**
     * Tries to build a &lt;factor&gt; on the global stack.
     * <pre>&lt;factor&gt; ::= &lt;name&gt;
     *           | &lt;function call&gt;
     *           | &lt;number&gt;
     *           | "(" &lt;expression&gt; ")"</pre>
     * A <code>SyntaxException</code> will be thrown if the opening
     * parenthesis is present but not followed by a valid
     * &lt;expression&gt; and a closing parenthesis.
     * @return <code>true</code> if a factor is recognized.
     */
	public boolean isFactor() {
        boolean startsWithUnary = symbol("+") || symbol("-");
        if (name()) {
            if (symbol(".")) {
                // reference to another Bug
                if (name()) {
                    makeTree(2, 3, 1);
                }
                else error("Incorrect use of dot notation");
            }
            else if (isParameterList()) {
                // function call
                stack.push(makeNode("call", Token.Type.NAME));
                makeTree(1, 3, 2);
            }
            else {
                // just a variable; leave it on the stack
            }
        }
        else if (number()) {
            // leave the number on the stack
        }
        else if (symbol("(")) {
            stack.pop();
            if (!isExpression()) {
                error("Error in parenthesized expression");
            }
            if (!symbol(")")) {
                error("Unclosed parenthetical expression");
            }
            stack.pop();
        }
        else {
            return false;
        }
        if (startsWithUnary) {
           makeTree(2, 1);
       }
       return true;
    }

	/**
	 * Tries to build a &lt;parameter list&gt; and put it on the global stack.
	 * <pre>&lt;parameter list&gt; ::= "(" [ &lt;expression&gt; { "," &lt;expression&gt; } ] ")"</pre>
	 * A <code>SyntaxException</code> will be thrown if the opening
	 * parenthesis is present but not followed by a closing parenthesis.
	 * @return <code>true</code> if a &lt;parameter list&gt; is recognized.
	 */
    public boolean isParameterList() {
        if (!symbol("(")) return false;
        stack.pop(); // remove open paren
        stack.push(makeNode("var", Token.Type.KEYWORD));
        if (isExpression()) {
            makeTree(2, 1);
            while (symbol(",")) {
                stack.pop(); // remove comma
                if (!isExpression()) error("No expression after ','");
                makeTree(2, 1);
            }
        }
        if (!symbol(")")) error("Parameter list doesn't end with ')'");
        stack.pop(); // remove close paren
        return true;
    }

	/**
	 * Tries to build an &lt;add_operator&gt; and put it on the global stack 
	 * <pre>&lt;add_operator&gt; ::= "+" | "-"</pre>
	 * @return <code>true</code> if an add_operator is recognized.
	 */
	public boolean isAddOperator() {
		return symbol("+") || symbol("-");
	}

	/**
	 * Tries to build a &lt;multiply_operator&gt; and put it on the global stack.
	 * <pre>&lt;multiply_operator&gt; ::= "*" | "/"</pre>
	 * @return <code>true</code> if a multiply_operator is recognized.
	 */
	public boolean isMultiplyOperator() {
		return symbol("*") || symbol("/");
	}

	/**
	 * Tries to build a &lt;variable&gt; on the global stack.
	 * <pre>&lt;variable&gt; ::= &lt;NAME&gt;</pre>
	 * @return <code>true</code> if a variable is recognized.
	 */
	public boolean isVariable() {
		return name();
	}

	/**
	 * Tries to build a &lt;action&gt; on the global stack.
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
	 * Tries to build an &lt;allbugs code&gt; on the global stack.
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
		stack.push(makeNode("list"));
		int varDecCounter = 0;
		while(isVarDeclaration()){
			varDecCounter ++;
		}
		int[] varDecArray = getReverseArray(varDecCounter);
		makeTree(varDecCounter +1, varDecArray);
		
		stack.push(makeNode("list"));
		int funcDefCounter = 0;
		while(isFunctionDefinition()){
			funcDefCounter++;
		}
		int[] funcDefArray = getReverseArray(funcDefCounter);
		makeTree(funcDefCounter+1, funcDefArray);
		checkRightBrace();
		checkEol();
		makeTree(3,2,1);
		return true;
	}

	/**
	 * Tries to build an &lt;assignment statement&gt; on the global stack.
	 * <pre>&lt;assignment statement&gt; ::= &lt;variable&gt; &quot;=&quot; &lt;expression&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if &quot;=&quot is not present
	 * @return <code>true</code> if a &lt;assignment statement&gt; is recognized.
	 */
	public boolean isAssignmentStatement() {
		if(!isVariable()) return false;
		if(!symbol("=")) error("\"=\" missing!");
		stack.pop();
		checkExpression();
		checkEol();
		stack.push(makeNode("assign"));
		makeTree(1,3,2);
		return true;
	}

	/**
	 * Tries to build a &lt;block&gt; on the global stack.
	 * <pre>&lt;block&gt; ::= "{" &lt;eol&gt; { &lt;command&gt;   "}" &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;block&gt; is recognized.
	 */
	public boolean isBlock() {
		if(!symbol("{")) return false;
		stack.pop();
		stack.push(makeNode("block"));
		checkEol();
		int counter = 0;
		while (isCommand()){
			counter++;
		}
		checkRightBrace();
		checkEol();
		int[] commandArray = getReverseArray(counter);
		makeTree(counter+1, commandArray);
		return true;
	}

	/**
	 * Tries to build a &lt;bug definition&gt; on the global stack.
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
		stack.push(makeNode("list"));
		int varCount = 0;
		while(isVarDeclaration()){
			varCount++;
		}
		int[] varArray = getReverseArray(varCount);
		makeTree(varCount+1, varArray);
		if (!isInitializationBlock()){
			stack.push(makeNode("initially"));
			stack.push(makeNode("block"));
			makeTree(2,1);
		}
		stack.push(makeNode("block"));
		if(!isCommand()) error("Command is missing!");
		int commandCounter = 1;
		while(isCommand()){ 
			commandCounter++;
		}
		int [] commandArray = getReverseArray(commandCounter);
		makeTree(commandCounter+1, commandArray);
		
		stack.push(makeNode("list"));
		int funcDefCounter = 0;
		while(isFunctionDefinition()){
			funcDefCounter++;
		}
		
		int [] funcDefArray = getReverseArray(funcDefCounter);
		makeTree(funcDefCounter+1, funcDefArray);
		checkRightBrace();
		checkEol();
		
		makeTree(6,5,4,3,2,1);
		return true;
	}

	/**
	 * Tries to build a &lt;color statement&gt; on the global stack.
	 * <pre>&lt;color statement&gt; ::= "color" &lt;KEYWORD&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if a KEYWORD is not present
	 * @return <code>true</code> if a &lt;color statement&gt; is recognized.
	 */
	public boolean isColorStatement() {
		if(!keyword("color")) return false;
		if(!(nextTokenMatches(Token.Type.KEYWORD)))
			error("A keyword is missing!");
		checkEol();
		makeTree(2,1);
		return true;
	}
	/**
	 * Tries to build a &lt;command&gt; on the global stack.
	 * <pre>&lt;command&gt; ::= &lt;action&gt;
            | &lt;statement&gt; </pre>
	 * @return <code>true</code> if a &lt;command&gt; is recognized.
	 */
	public boolean isCommand() {
		return isAction()||isStatement();
	}

	/**
	 * Tries to build a &lt;comparator&gt; on the global stack.
	 * <pre>&lt;comparator&gt; ::= "&lt;" | "&lt;=" | "=" | "!=" "&gt;=" | "&gt;"</pre>
	 * @return <code>true</code> if a &lt;comparator&gt; is recognized.
	 */
	public boolean isComparator() {
		if (symbol("<")) {
			if (symbol("=")){
				stack.pop();
				stack.pop();
				stack.push(makeNode("<=", Token.Type.SYMBOL));
			}
			return true;
		}
		if (symbol("=")) return true;
		if (symbol("!")&& symbol("=")){
			stack.pop();
			stack.pop();
			stack.push(makeNode("!=", Token.Type.SYMBOL));
			return true;
		}
		if (symbol(">")) {
			if (symbol("=")){
				stack.pop();
				stack.pop();
				stack.push(makeNode(">=", Token.Type.SYMBOL));
			}
			return true;
		}
		return false;
	}


	/**
	 * Tries to build a &lt;do statement&gt; on the global stack.
	 * <pre>&lt;do statement&gt; ::= "do" &lt;variable&gt; [ &lt;parameter list&gt; ] &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if variable is not present
	 * @return <code>true</code> if a &lt;do statement&gt; is recognized.
	 */
	public boolean isDoStatement() {
		if (!keyword("do")) return false;
		stack.pop();
		stack.push(makeNode("call"));
		if (!isVariable()) error("Variable is missing!");
		if (!isParameterList())
			stack.push(makeNode("var"));
		checkEol();
		makeTree(3,2,1);
		return true;
	}

 	/**
	 * Tries to build a &lt;exit if statement&gt; on the global stack.
	 * <pre>&lt;exit if statement&gt; ::= "exit" "if" &lt;expression&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if &quot;if&quot doesn't follow exit
	 * @return <code>true</code> if a &lt;exit if statement&gt; is recognized.
	 */
	public boolean isExitIfStatement() {
		if(!keyword("exit"))
			return false;
		if (!keyword("if"))
			error("\"if\" is missing");
		stack.pop();
		checkExpression();
		checkEol();
		makeTree(2,1);
		return true;
	}
	
 	/**
	 * Tries to build a &lt;function call&gt; on the global stack.
	 * <pre>&lt;function call&gt; ::= &lt;NAME&gt; &lt;parameter list&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if parameter list is not present
	 * @return <code>true</code> if a &lt;function call&gt; is recognized.
	 */
	public boolean isFunctionCall() {
		if(!name()) return false;
		stack.push(makeNode("call", Token.Type.NAME));
		if (!isParameterList())
			error("Parameter list missing!");
		makeTree(2,3,1);
		return true;
	}

 	/**
	 * Tries to build a &lt;function definition&gt; on the global stack.
	 * <pre>&lt;function definition&gt; ::= &quot;define&quot; &lt;NAME&gt; [ &quot;using&quot; &lt;variable&gt; { &quot;,&quot; &lt;variable&gt;   ] &lt;block&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if a function name doesn't follow &quot;define&quot; or variable doesn't follow using or &quot;,&quot;
	 * @return <code>true</code> if a &lt;function definition&gt; is recognized.
	 */
	public boolean isFunctionDefinition() {
		if(!keyword("define")) return false;
		stack.pop();
		stack.push(makeNode("function"));
		if(!name()) error("Function name missing!");
		if(keyword("using")){
			stack.pop();
			stack.push(makeNode("var"));
			if(!isVariable()) error("Variable missing for \"using\"");
			int varCounter = 1;
			while(symbol(",")){
				stack.pop();
				if(!isVariable()) error("Variable missing after \",\"");
				varCounter++;
			}
			int[] varList = getReverseArray(varCounter);//reverse the order of variables in the list
			makeTree(varCounter+1, varList);//varCounter+1 is "var"
		}
		else stack.push(makeNode("var"));
		if(!isBlock()) error("Block is missing");
		makeTree(4,3,2,1);
		return true;
	}


 	/**
	 * Tries to build an &lt;initialization block&gt; on the global stack.
	 * <pre>&lt;initialization block&gt; ::= &quot;initially&quot; &lt;block&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown is block doesn't follow initially
	 * @return <code>true</code> if a &lt;initialization block&gt; is recognized.
	 */
	public boolean isInitializationBlock() {
		if(!keyword("initially")) return false;
		if(!isBlock()) error("Initialization block is missing!");
		makeTree(2,1);
		return true;
	}
 	/**
	 * Tries to build a &lt;line action&gt;on the global stack.
	 * <pre>&lt;line action&gt; ::= "line" &lt;expression&gt; ","&lt;expression&gt; 
	 * &quot;,&quot;&lt;expression&gt; &quot;,&quot; &lt;expression&gt; &lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if expression is missing or &quot;,&quot;
	 *  is not present between expressions
	 * @return <code>true</code> if a &lt;line action&gt; is recognized.
	 */
	public boolean isLineAction() {
		if (!keyword("line"))
			return false;
		for (int i = 0; i<3; i++) {
			checkExpression();
			if (!symbol(","))
				error("\",\" missing");
			stack.pop();
		}
		checkExpression();
		checkEol();
		makeTree(5,4,3,2,1);
		return true;
	}
	
	


 	/**
	 * Tries to build a &lt;loop statement&gt; on the global stack.
	 * <pre>&lt;loop statement&gt; ::= "loop" &lt;block&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if block is missing
	 * @return <code>true</code> if a &lt;loop statement&gt; is recognized.
	 */
	public boolean isLoopStatement() {
		if (!keyword("loop"))
			return false;
		if (!isBlock())
			error("Block missing!");
		makeTree(2,1);
		return true;
	}

 	/**
	 * Tries to build a &lt;move action&gt;on the global stack.
	 * <pre>&lt;move action&gt; ::= "move" &lt;expression&gt; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;move action&gt; is recognized.
	 */
	public boolean isMoveAction() {
		if (!keyword("move"))
			return false;
		checkExpression();
		checkEol();
		makeTree(2,1);
		return true;
	}

 	/**
	 * Tries to build a &lt;moveto action&gt;on the global stack.
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
		stack.pop();
		checkExpression();
		checkEol();
		makeTree(3,2,1);
		return true;
	}
	
 	/**
	 * Tries to build a &lt;program&gt;on the global stack.
	 * <pre>&lt;program&gt; ::= [ &lt;allbugs code&gt; ]
                &lt;bug definition&gt;
              { &lt;bug definition&gt; }</pre>
	 * A <code>SyntaxException</code> will be thrown if bug definition is missing
	 * @return <code>true</code> if a &lt;program&gt; is recognized.
	 */
	public boolean isProgram() {
		isEol();
		stack.push(makeNode("program"));
		if(isAllbugsCode()){
			stack.push(makeNode("list"));
			if(!isBugDefinition()) error("bug definition is missing!");
		}
		else {
			stack.push(makeNode("Allbugs"));
			stack.push(makeNode("list"));
			stack.push(makeNode("list"));
			makeTree(3,2,1);
			stack.push(makeNode("list"));
			if(!isBugDefinition()){
				return false;
			}
		}
		int bugCounter = 1;
		while(isBugDefinition()){
			bugCounter++;
		}
		int[] bugArray = getReverseArray(bugCounter);
		makeTree(bugCounter+1, bugArray);
		makeTree(3,2,1);
		return true;
	}

 	/**
	 * Tries to build a &lt;return statement&gt;on the global stack.
	 * <pre>&lt;return statement&gt; ::= &quot;return&quot; &lt;expression&gt; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;return statement&gt; is recognized.
	 */
	public boolean isReturnStatement() {
		if (!keyword("return")) return false;
		checkExpression();
		checkEol();
		makeTree(2,1);
		return true;
	}

 	/**
	 * Tries to build a &lt;statement&gt;on the global stack.
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
	 * Tries to build a &lt;switch statement&gt;on the global stack.
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
		int caseCounter = 0;
		while (keyword("case")){
			caseCounter++;
			checkExpression();
			checkEol();
			stack.push(makeNode("block"));
			int commandCounter = 0;
			while(isCommand()){
				commandCounter++;
			}
			int[] commandArray = getReverseArray(commandCounter);
			makeTree(commandCounter+1, commandArray);//the "block" tree
			makeTree(3,2,1);//the "case" tree
		}
		checkRightBrace();
		checkEol();
		int[] caseArray = getReverseArray(caseCounter);
		makeTree(caseCounter+1, caseArray);
		return true;
	}
	
 	/**
	 * Tries to build a &lt;turn action&gt;on the global stack.
	 * <pre>&lt;turn action&gt; ::= "turn" &lt;expression&gt; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;turn action&gt; is recognized.
	 */
	public boolean isTurnAction() {
		if (!keyword("turn"))
			return false;
		checkExpression();
		checkEol();
		makeTree(2,1);
		return true;
	}
 	/**
	 * Tries to build a &lt;turnto action&gt;on the global stack.
	 * <pre>&lt;turnto action&gt; ::= "turnto" &lt;expression&gt; &lt;eol&gt;</pre>
	 * @return <code>true</code> if a &lt;turnto action&gt; is recognized.
	 */
	public boolean isTurnToAction() {
		if (!keyword("turnto"))
			return false;
		checkExpression();
		checkEol();
		makeTree(2,1);
		return true;
	}
 	/**
	 * Tries to build a &lt;var declaration&gt;on the global stack.
	 * <pre>&lt;var declaration&gt; ::= &quot;var&quot; &lt;NAME&gt; 
	 * { &quot;,&quot; &lt;NAME&gt;}&lt;eol&gt;</pre>
	 * A <code>SyntaxException</code> will be thrown if variable name 
	 * is missing or &quot;,&quot; is missing between variable names. 
	 * @return <code>true</code> if a &lt;var declaration&gt; is recognized.
	 */
	public boolean isVarDeclaration() {
		if(!keyword("var")) return false;
		if(!name()) error("Var name missing!");
		int nameCounter = 1;
		while(symbol(",")){
			stack.pop();
			if(!name()) error("Var name missing after \",\"");
			nameCounter ++;
		}
		checkEol();
		int[] nameArray = getReverseArray(nameCounter);
		makeTree(nameCounter+1, nameArray);	
		return true;
	}

//	private Helper methods
	/**
	 * Tests whether the next token is a number. If it is, the token
	 * is consumed and put on the global stack, otherwise it is not.
	 * 
	 * @return <code>true</code> if the next token is a number.
	 */
	private boolean number() {
		return nextTokenMatches(Token.Type.NUMBER);
	}

	/**
	 * Tests whether the next token is a name. If it is, the token
	 * is consumed and put on the global stack, otherwise it is not.
	 * 
	 * @return <code>true</code> if the next token is a name.
	 */
	private boolean name() {
		return nextTokenMatches(Token.Type.NAME);
	}

	/**
	 * Tests whether the next token is the expected name. If it is, the token
	 * is consumed and put on the global stack, otherwise it is not.
	 * 
	 * @param expectedName The String value of the expected next token.
	 * @return <code>true</code> if the next token is a name with the expected value.
	 */
	private boolean name(String expectedName) {
		return nextTokenMatches(Token.Type.NAME, expectedName);
	}

	/**
	 * Tests whether the next token is the expected keyword. If it is, the token
	 * is moved to the stack and put on the global stack, otherwise it is not.
	 * 
	 * @param expectedKeyword The String value of the expected next token.
	 * @return <code>true</code> if the next token is a keyword with the expected value.
	 */
	private boolean keyword(String expectedKeyword) {
		return nextTokenMatches(Token.Type.KEYWORD, expectedKeyword);
	}

	/**
	 * Tests whether the next token is the expected symbol. If it is,
	 * the token is consumed and put on the global stack, otherwise it is not.
	 * 
	 * @param expectedSymbol The String value of the token we expect
	 *    to encounter next.
	 * @return <code>true</code> if the next token is the expected symbol.
	 */
	boolean symbol(String expectedSymbol) {
		return nextTokenMatches(Token.Type.SYMBOL, expectedSymbol);
	}

	 /**
     * If the next Token has the expected type, it is used as the
     * value of a new (childless) Tree node, and that node
     * is then pushed onto the stack. If the next Token does not
     * have the expected type, this method effectively does nothing.
     * 
     * @param type The expected type of the next token.
     * @return <code>true</code> if the next token has the expected type.
     */
    private boolean nextTokenMatches(Token.Type type) {
        Token t = nextToken();
        if (t.type == type) {
            stack.push(new Tree<Token>(t));
            return true;
        }
        else pushBack();
        return false;
    }

    /**
     * If the next Token has the expected type and value, it is used as
     * the value of a new (childless) Tree node, and that node
     * is then pushed onto the stack; otherwise, this method does
     * nothing.
     * 
     * @param type The expected type of the next token.
     * @param value The expected value of the next token; must
     *              not be <code>null</code>.
     * @return <code>true</code> if the next token has the expected type.
     */
    private boolean nextTokenMatches(Token.Type type, String value) {
        Token t = nextToken();
        if (type == t.type && value.equals(t.value)) {
            stack.push(new Tree<Token>(t));
            return true;
        }
        else pushBack();
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
     * Assembles some number of elements from the top of the global stack
     * into a new Tree, and replaces those elements with the new Tree.<p>
     * <b>Caution:</b> The arguments must be consecutive integers 1..N,
     * in any order, but with no gaps; for example, makeTree(2,4,1,5)
     * would cause problems (3 was omitted).
     * 
     * @param rootIndex Which stack element (counting from 1) to use as
     * the root of the new Tree.
     * @param childIndices Which stack elements to use as the children
     * of the root.
     */    
    private void makeTree(int rootIndex, int... childIndices) {
        // Get root from stack
        Tree<Token> root = getStackItem(rootIndex);
        // Get other trees from stack and add them as children of root
        for (int i = 0; i < childIndices.length; i++) {
            root.addChild(getStackItem(childIndices[i]));
        }
        // Pop root and all children from stack
        for (int i = 0; i <= childIndices.length; i++) {
            stack.pop();
        }
        // Put the root back on the stack
        stack.push(root);
    }

    /**
     * Creates a Tree containing a Token for the given word.
     * 
     * @param word The word to put in the Tree.
     * @param isKeyword <code>true</code> if the Token is to be marked
     *            as a keyword, <code>false</code> if it is to be a name.
     * @return A one-node Tree containing the given word.
     */
    private Tree<Token> makeNode(String word, Token.Type type) {
            return new Tree<Token>(new Token(type, word));
    }
    
    /**
     * Creates a Tree containing a Token for the given word.
     * The Type of the word is determined automatically.
     * @param word The word to put in the Tree.
     * @return A one-node Tree containing the given word.
     */
    private Tree<Token> makeNode(String word) {
    	return new Tree<Token>(new Token(Token.determineTokenType(word), word));
    }
    /**
     * Returns the n-th item from the top of the global stack (counting the
     * top element as 1).
     * 
     * @param n Which stack element to return.
     * @return The n-th element in the global stack.
     */
    private Tree<Token> getStackItem(int n) {
        return stack.get(stack.size() - n);
    }

    /**
     * Debugging method to print the contents of the global stack.
     */
    private void printStack() {
        if (stack.size() == 0) {
            System.out.println("Stack is empty.");
            return;
        }
        System.out.print("[");
        for (int i = 0; i < stack.size() - 1; i++) {
            System.out.print(stack.get(i) + ", ");
        }
        System.out.println(stack.get(stack.size() - 1) + "] <-- top");
    }
    
	/**
	 * Tries to recognize an &lt;eol&gt;
	 * <pre>&lt;eol&gt; ::= &lt;EOL&gt;{&lt;EOL&gt;}</pre>
	 * @return <code>true</code> if EOL is recognized
	 */
	public boolean isEol() {
		if (!(nextTokenMatches(Token.Type.EOL)))
			return false;
		stack.pop();
		while((nextTokenMatches(Token.Type.EOL))){
			stack.pop();
		}
		return true;
	}

	/**
	 * Make an array of reverse ordered integers n, n-1, ..., 1
	 * @param n The range of reverse integers
	 * @return the reverse integers
	 */
	private int[] getReverseArray(int n) {
		int[] varList = new int[n];
		for (int i=0; i< n; i++){
			varList[i] = n-i;
		}
		return varList;
	}

	private void checkEol() {
		if(!isEol()) error("EOL is missing!");
	}
	private void checkLeftBrace() {
		if(!symbol("{")) error("\"{\" is missing");
		stack.pop();
	}
	private void checkRightBrace() {
		if(!symbol("}")) error("\"}\" is missing");
		stack.pop();
	}
	private void checkExpression() {
		if (!isExpression())
			error("Expression missing");
	}
}

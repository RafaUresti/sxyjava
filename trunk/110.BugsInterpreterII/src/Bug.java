import java.util.*;

import parser.*;
import tree.Tree;
import java.awt.Color;

import javax.lang.model.element.UnknownElementException;



/**
 * This class interprets the abstract syntax tree built from the
 * Bugs language by the Parser class.
 * 
 * @author sxycode
 * @version 4-27-2008
 */
public class Bug extends Thread{

	
	double x = 0;
	double y = 0;
	double angle = 0;
	double returnValue;
	Color color = Color.BLACK;
	String name;
	boolean exitingLoop = false;
	boolean returningFunction = false;
	HashMap<String, Double> variables = new HashMap<String, Double>();
	HashMap<String,Tree<Token>> functions = new HashMap<String, Tree<Token>>();
	Stack<HashMap<String, Double>> scope = new Stack<HashMap<String, Double>>();
	private Tree<Token> bugTree;
	private Interpreter interpreter;
	private boolean blocked;
	

	/**
	 * Constructor for the Bug class.
	 * @param name The name of the Bug
	 * @param bugTree The tree representing the Bug
	 * @param interpreter by which this Bug thread is controlled
	 */
	public Bug(String name,Tree<Token> bugTree, Interpreter interpreter){
		this.name = name;
		this.bugTree = bugTree;
		this.interpreter = interpreter;
		scope.push(variables);
	}
	
	/**
	 * Starts the Bug thread
	 */
	@Override
	public void run() {
//		print("I am bug "+name);
		interpret(bugTree);
//		print("   "+name + " is finally at location x = "+x +",y = "+y+",angle = "+angle);
		interpreter.terminateBug(this);
		try {
			join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setBlocked(boolean b) { blocked = b; }

	public boolean isBlocked() { return blocked; }

	/**
	 * Evaluates an expression in Bugs language.
	 * @param tree The expression to evaluate
	 * @return <code>1</code> if result is true and <code>0</code> otherwise
	 */
	public double evaluate(Tree<Token> tree){
		Token rootToken = tree.getValue();
		String rootString = tree.getValue().value;
		if (rootToken.type == Token.Type.NUMBER)
			return Double.parseDouble(rootString);
		if (rootString.equals("+")){
			if (tree.getNumberOfChildren() == 1)
				return evaluate(tree.getChild(0));
			if (tree.getNumberOfChildren() == 2)
				return evaluate(tree.getChild(0)) + evaluate(tree.getChild(1));
		}
		if (rootString.equals("-")) {
			if (tree.getNumberOfChildren() == 1)
				return -evaluate(tree.getChild(0));
			else
				return evaluate(tree.getChild(0)) - evaluate(tree.getChild(1));
		}
		if (rootString.equals("*")){
			return evaluate(tree.getChild(0)) * evaluate(tree.getChild(1));
		}
		if (rootString.equals("/")){
			return evaluate(tree.getChild(0)) / evaluate(tree.getChild(1));
		}
		//Two double numbers are equal if they are within 0.001 of each other
		if (rootString.equals("<")) {
			if (getChildrenDifference(tree) < -0.001) return 1;
			else return 0;
		}
		if (rootString.equals("<=")) {
			if (getChildrenDifference(tree) <= 0.001) return 1;
			else return 0;
		}
		if (rootString.equals("=")){
			if (Math.abs(getChildrenDifference(tree)) <= 0.001) return 1;
			else return 0;
		}
		if (rootString.equals("!=")){
			if (Math.abs(getChildrenDifference(tree)) > 0.001) return 1;
			else return 0;
		}
		if (rootString.equals(">")){
			if (getChildrenDifference(tree) > 0.001) return 1;
			else return 0;
		}
		if (rootString.equals(">=")){
			if (getChildrenDifference(tree) >= -0.001) return 1;
			else return 0;
		}
		if (rootString.equals(".")){
			return evaluateDot(tree);
		}
		if (rootString.equals("case")){
			double condition = evaluate(tree.getChild(0));
			if (!areEqualDoubles(condition,0))
				interpret(tree.getChild(1));
			return condition;
		}
		if (rootString.equals("call")) 
			return evaluateCall(tree);
		if (rootToken.type == Token.Type.NAME)
			return fetch(rootString);
		throw new IllegalArgumentException();
	}

	/**
	 * Evaluates the variable from another Bug
	 * @param tree The tree representing the variable from another Bug.
	 * The root of the tree is "." and the left child the name of another Bug,
	 * right child the name of variable of the other Bug.
	 * @return the value of the variable from another Bug
	 */
	private double evaluateDot(Tree<Token> tree) {
		String otherBugName = tree.getChild(0).getValue().value;
		String otherBugVariable = tree.getChild(1).getValue().value;
		Bug otherBug;
		if(interpreter.bugs.containsKey(otherBugName)){
			otherBug = interpreter.bugs.get(otherBugName);
			if (otherBugVariable.equals("x"))
				return otherBug.x;
			if (otherBugVariable.equals("y"))
				return otherBug.y;
			if (otherBugVariable.equals("angle"))
				return otherBug.angle;
			return otherBug.scope.get(0).get(otherBugVariable);
		}
		else throw new IllegalArgumentException("Bug "+otherBugName+" doesn't exist!");
	}

	/**
	 * Evaluates the function call
	 * @param tree the function call
	 * @return the return value of the function call
	 */
	private double evaluateCall(Tree<Token> tree) {
		// Root value is "call"
		returnValue = 0.0;
		String functionName = tree.getChild(0).getValue().value;
		HashMap<String, Double> functionVariables = new HashMap<String, Double>();
		if (functionName.equals("distance"))
			return evaluateDistance(tree.getChild(1));
		if (functionName.equals("direction"))
			return evaluateDirection(tree.getChild(1));
		if (this.functions.containsKey(functionName) || 
				interpreter.functions.containsKey(functionName)){
			Tree<Token> formalParameters;
			Tree<Token> functionBody;
			if (this.functions.containsKey(functionName)){
				formalParameters = functions.get(functionName).getChild(1);
				functionBody = functions.get(functionName).getChild(2);
			}
			else{
				formalParameters = interpreter.functions.get(functionName).getChild(1);
				functionBody = interpreter.functions.get(functionName).getChild(2);
			}
			Tree<Token> actualParameters = tree.getChild(1);
			 
			for (int i = 0; i < formalParameters.getNumberOfChildren(); i++){
				double parameterValue = evaluate(actualParameters.getChild(i));
				functionVariables.put(formalParameters.getChild(i).getValue().value, parameterValue);
			}
			scope.push(functionVariables);
			interpretFunctionBody(functionBody);
			
		}
		else throw new IllegalArgumentException(functionName + " has not been defined!");
		return returnValue;
	}

	/**
	 * Evaluates the <code>distance</code> function, which calculates
	 * the distance between two bugs.
	 * @param tree the distance function
	 * @return the distance of two bugs
	 */
	private double evaluateDistance(Tree<Token> tree) {
		//root value of tree is "var"
		if (tree.getNumberOfChildren()!= 1)
			throw new IllegalArgumentException("Misuse of direction method!");
		String otherBugName = tree.getChild(0).getValue().value;
		Bug otherBug = interpreter.bugs.get(otherBugName);
		return Math.hypot(x-otherBug.x, y-otherBug.y);
	}

	/**
	 * Evaluates the <code>direction</code> function, which calculates
	 * the direction from this bug to that bug.
	 * @param tree the direction function
	 * @return the direction in degrees
	 */
	private double evaluateDirection(Tree<Token> tree) {
		//root value of tree is "var"
		if (tree.getNumberOfChildren()!= 1)
			throw new IllegalArgumentException("Misuse of distance method!");
		String otherBugName = tree.getChild(0).getValue().value;
		Bug otherBug = interpreter.bugs.get(otherBugName);
		return Math.toDegrees(Math.atan2(y-otherBug.y, otherBug.x-x));
	}

	/**
	 * Interprets the statements in the function body
	 * @param functionBody The block that includes the statements of the function
	 */
	private void interpretFunctionBody(Tree<Token> functionBody) {
		int i = 0;
		while(!returningFunction && i < functionBody.getNumberOfChildren()){
			interpret(functionBody.getChild(i));
			i++;
		}
		returningFunction = false;
		scope.pop();//Function variables are popped from the stack.
					//Don't do it in return as return statement maybe missing
	}

	/**
	 * Computes the difference between two children nodes of a tree
	 * which are evaluated to be doubles. 
	 * @param tree the tree that contains the children to evaluate
	 * @return the difference of the children
	 */
	private double getChildrenDifference(Tree<Token> tree) {
		return evaluate(tree.getChild(0))- evaluate(tree.getChild(1));
	}

	/**
	 * Interprets the Bugs language
	 * @param tree the tree representing a Bugs program
	 */
	public void interpret(Tree<Token> tree){
		String rootString = tree.getValue().value;
		if (exitingLoop||returningFunction) return;
		else if (rootString.equals("move")) interpretMove(tree);
		else if (rootString.equals("moveto")) interpretMoveTo(tree);
		else if (rootString.equals("turn")) interpretTurn(tree);
		else if (rootString.equals("turnto")) interpretTurnTo(tree);
		else if (rootString.equals("color")) interpretColor(tree);
		else if (rootString.equals("var")) interpretVar(tree);
		else if (rootString.equals("assign")) interpretAssign(tree);
		else if (rootString.equals("block")) interpretBlock(tree);
		else if (rootString.equals("initially")) interpretInitially(tree);
		else if (rootString.equals("function")) interpretFunctionDeclaration(tree);
		else if (rootString.equals("list")) interpretList(tree);
		else if (rootString.equals("switch")) interpretSwitch(tree);
		else if (rootString.equals("exit")) interpretExit(tree);
		else if (rootString.equals("loop")) interpretLoop(tree);
		else if (rootString.equals("Bug")) interpretBug(tree);
		else if (rootString.equals("return")) interpretReturn(tree);
		else if (rootString.equals("line")) interpretLine(tree);
		else if (rootString.equals("program")||rootString.equals("Allbugs"))
			throw new UnknownElementException(null, tree);
		else evaluate(tree);	//get all the other conditions to evaluate;
	}


	/**
	 * Interprets a <code>return statement</code>
	 * @param tree the tree representing the <code>return statement</code>
	 */
	private void interpretReturn(Tree<Token> tree) {
		returnValue = evaluate(tree.getChild(0));
		returningFunction = true;
	}

	/**
	 * Interprets a <code>bug definition</code>
	 * @param tree the tree representing the <code>bug definition</code>
	 */
	private void interpretBug(Tree<Token> tree) {
		this.name = tree.getChild(0).getValue().value;
		interpretList(tree.getChild(1));
		interpretList(tree.getChild(4));//functions have to be interpreted before commands
		interpretInitially(tree.getChild(2));
		interpretBlock(tree.getChild(3));
		
	}


	/**
	 * Interprets a <code>loop statement</code>
	 * @param tree the tree representing the <code>loop statement</code>
	 */
	private void interpretLoop(Tree<Token> tree) {
		Tree<Token> blockNode = tree.getChild(0);
		int numberOfCommands = blockNode.getNumberOfChildren();
		int i = 0;
		while(!exitingLoop){
			i %= numberOfCommands;
			interpret(blockNode.getChild(i));
			i++;
		}
		exitingLoop = false;	
	}

	/**
	 * Interprets a <code>exit if statement</code>
	 * @param tree the tree representing the <code>exit if statement</code>
	 */
	private void interpretExit(Tree<Token> tree) {
		double condition = evaluate(tree.getChild(0));
		if (!areEqualDoubles(condition, 0.0))
			exitingLoop = true;;
	}

	/**
	 * Interprets a <code>switch statement</code>
	 * @param tree the tree representing the <code>switch statement</code>
	 */
	private void interpretSwitch(Tree<Token> tree) {
		int numberOfCases = tree.getNumberOfChildren();
		for (int i = 0; i< numberOfCases; i++)
			if (!areEqualDoubles(evaluate(tree.getChild(i)), 0.0)){
				break;
			}
	}

	/**
	 * Interprets a <code>list</code>
	 * @param tree the tree representing the <code>list</code>
	 */
	private void interpretList(Tree<Token> tree) {
		int numberOfChildren = tree.getNumberOfChildren();
		for (int i = 0; i< numberOfChildren; i++){
			interpret(tree.getChild(i));
		}
	}

	/**
	 * Interprets a <code>function definition</code>
	 * @param tree the tree representing the <code>function definition</code>
	 */
	private void interpretFunctionDeclaration(Tree<Token> tree) {
		String name = tree.getChild(0).getValue().value;
		if (!functions.containsKey(name))
			functions.put(name, tree);
		else throw new IllegalArgumentException("function "+name+" has already been defined!");
	}

	/**
	 * Interprets a <code>initialization block</code>
	 * @param tree the tree representing the <code>initialization block</code>
	 */
	private void interpretInitially(Tree<Token> tree) {
		interpret(tree.getChild(0));
	}

	/**
	 * Interprets a <code>block</code>
	 * @param tree the tree representing the <code>block</code>
	 */
	private void interpretBlock(Tree<Token> tree) {
		int numberOfChildren = tree.getNumberOfChildren();
		for (int i = 0; i < numberOfChildren; i++){
			interpret(tree.getChild(i));
		}
	}

	/**
	 * Interprets a <code>assignment statement</code>
	 * @param tree the tree representing the <code>assignment statement</code>
	 */
	private void interpretAssign(Tree<Token> tree) {
		String name = tree.getChild(0).getValue().value;
		double value = evaluate(tree.getChild(1));
		store(name, value);
	}

	/**
	 * Interprets a <code>var declaration</code>
	 * @param tree the tree representing the <code>var declaration</code>
	 */
	private void interpretVar(Tree<Token> tree) {
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			String newVar = tree.getChild(i).getValue().value;
			if (variables.containsKey(newVar)) 
				throw new IllegalArgumentException(newVar + " has already been declared!");
			else if (newVar.equals("x")||newVar.equals("y")||newVar.equals("angle"))
				throw new IllegalArgumentException(newVar + " cannot be declared!"); 
			else variables.put(newVar, 0.0);
		}
	}

	/**
	 * Interprets a <code>color statement</code>
	 * @param tree the tree representing the <code>color statement</code>
	 */
	private void interpretColor(Tree<Token> tree) {
		String newColor = tree.getChild(0).getValue().value;
		if (newColor.equals("black")) color = Color.black;
		else if (newColor.equals("blue")) color = Color.blue;
		else if (newColor.equals("cyan")) color = Color.cyan;
		else if (newColor.equals("darkGray")) color = Color.darkGray;
		else if (newColor.equals("lightGray")) color = Color.lightGray;
		else if (newColor.equals("gray")) color = Color.gray;
		else if (newColor.equals("green")) color = Color.green;
		else if (newColor.equals("magenta")) color = Color.magenta;
		else if (newColor.equals("orange")) color = Color.orange;
		else if (newColor.equals("pink")) color = Color.pink;
		else if (newColor.equals("red")) color = Color.red;
		else if (newColor.equals("white")) color = Color.white;
		else if (newColor.equals("yellow")) color = Color.yellow;
		else if (newColor.equals("brown")) color = new Color(139, 69, 19);
		else if (newColor.equals("purple")) color = new Color(160, 32, 240);
		else if (newColor.equals("none")) color = null;//no color will be drawn
		else throw new IllegalArgumentException("Invalid color name!");
	}

	/**
	 * Interprets a <code>turnto action</code>
	 * @param tree the tree representing the <code>turnto action</code>
	 */
	private void interpretTurnTo(Tree<Token> tree) {
		interpreter.getActionPermit(this);
		setAngle(evaluate(tree.getChild(0)));
		interpreter.completeCurrentAction(this);
	}

	/**
	 * Interprets a <code>turn action</code>
	 * @param tree the tree representing the <code>turn action</code>
	 */
	private void interpretTurn(Tree<Token> tree) {
		interpreter.getActionPermit(this);
		double turn = evaluate(tree.getChild(0));
		setAngle(angle + turn);
		interpreter.completeCurrentAction(this);
	}




	/**
	 * Interprets the <code>move action</code>. Sets the corresponding x and y values
	 * @param tree the tree representing a <code>move action</code> 
	 */
	private void interpretMove(Tree<Token> tree) {
		interpreter.getActionPermit(this);
		double deltaX, deltaY, radian, distance;
		double x1, y1, x2, y2;
		x1 = x; y1 = y;
		radian = Math.toRadians(angle);
		distance = evaluate(tree.getChild(0));
		deltaX = Math.cos(radian)*distance;
		deltaY = Math.sin(-radian)*distance;
		x += deltaX; x2 = x;
		y += deltaY; y2 = y;
		Command command = new Command (color, x1, y1, x2, y2);
		interpreter.addToCommands(command);
		interpreter.completeCurrentAction(this);
	}

	/**
	 * Interprets the <code>moveto action</code>. Sets the corresponding x and y values
	 * @param tree the tree representing a <code>moveto action</code> 
	 */
	private void interpretMoveTo(Tree<Token> tree){
		interpreter.getActionPermit(this);
		double x1, y1, x2, y2;
		x1 = x; y1 = y;
		x = evaluate(tree.getChild(0));
		y = evaluate(tree.getChild(1));
		x2 = x; y2 = y;
		Command command = new Command (color, x1, y1, x2, y2);
		interpreter.addToCommands(command);
		interpreter.completeCurrentAction(this);
	}

	/**
	 * Interprets the <code>line action</code>. Creates a corresponding
	 * command object.
	 * @param tree The line action
	 */
	private void interpretLine(Tree<Token> tree) {
		interpreter.getActionPermit(this);
		double x1, y1, x2, y2;
		x1 = evaluate(tree.getChild(0));
		y1 = evaluate(tree.getChild(1));
		x2 = evaluate(tree.getChild(2));
		y2 = evaluate(tree.getChild(3));
		Command command = new Command(color, x1, y1, x2, y2);
		interpreter.addToCommands(command);
		interpreter.completeCurrentAction(this);
	}
	/**
	 * Stores the value of variable into <code>variables</code>
	 * @param variable the name of the variable
	 * @param value the value of the variable
	 */
	void store(String variable, double value){
		if (variable.equals("x")) x = value;
		else if (variable.equals("y")) y = value;
		else if (variable.equals("angle")) setAngle(value);
		else{ 
			//search for the stack of variable maps
			for (int i = scope.size()-1; i >= 0; i--){
				if (scope.get(i).containsKey(variable)){
					scope.get(i).put(variable, value);
					return;
				}
			}
			//search for Allbug's variable list
			if (interpreter.variables.containsKey(variable))
				interpreter.variables.put(variable, value);
			//can't find it
			else throw new IllegalArgumentException(variable + " hasn't been declared yet!");
		}
	}

	/**
	 * Fetches the value of the variable
	 * @param variable whose value to fetch
	 * @return the value of the variable
	 */
	double fetch(String variable){
		if (variable.equals("x"))
			return x;
		else if (variable.equals("y"))
			return y;
		else if (variable.equals("angle"))
			return angle;
		else {
			for (int i = scope.size()-1; i >= 0; i--){
				if (scope.get(i).containsKey(variable)){
					return scope.get(i).get(variable);
				}
			}
			if (interpreter.variables.containsKey(variable))
				return interpreter.variables.get(variable);
			else throw new IllegalArgumentException("Variable not declared!");
		}
	}

	/**
	 * Sets the instance variable <code>angle</code>. Adjusts the angle
	 * range to be in the range of 0.0 to 360.0
	 * @param newAngle the angle to set
	 */
	private void setAngle(double newAngle) {
		if (newAngle < 0.0) angle = newAngle % 360.0 + 360.0;
		else if (newAngle > 360.0) angle = newAngle % 360.0;
		else angle = newAngle;
	}

	/**
	 * Checks if two numbers are within 0.001 apart.
	 * @param a first number
	 * @param b second number
	 * @return <code>true</code> if they are within 0.001 apart.
	 */
	public static boolean areEqualDoubles(double a, double b){
		return (Math.abs(a-b) <= 0.001);
	}
	
/*-----------Method for testing purpose only------------
	static void print(String string){
		if (false)
			System.out.println(string);
	}
	*/
}

	

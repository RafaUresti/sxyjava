package tree;

import java.util.Iterator;
/**
 * Class for prefix expression evaluation.
 * @author sxycode
 * @version 3-24-2008
 */
public class Expression {
	Tree<String> expressionTree;

	/**
	 * Constructor for Expression
	 * @param treeString the prefix expression to be constructed from
	 * @throws IllegalArgumentException if the treeString is not a valid expression
	 */
	public Expression(String treeString) throws IllegalArgumentException {
		expressionTree = Tree.parse(treeString);
		if (!isValidTree(expressionTree))
			throw new IllegalArgumentException("Expression not valid!");
	}

	/**
	 * Check if a tree is a valid expression tree
	 * @param tree the tree to be checked
	 * @return <code>true</code> if the tree is a valid expression tree
	 */
	private boolean isValidTree(Tree<String> tree){
		if (!(isOperator(tree.getValue())||isNumber(tree.getValue())))
			return false;
		if (isOperator(tree.getValue())){
			if (tree.getValue().equals("+") || tree.getValue().equals("*"))
				if (tree.getNumberOfChildren()<2)
					return false;
			if (tree.getValue().equals("-") || tree.getValue().equals("/"))
				if (tree.getNumberOfChildren()!= 2)
					return false;
		}

		for (Iterator<Tree<String>> i = tree.iterator(); i.hasNext();){
			if (!isValidTree(i.next()))
				return false;
		}
		return true;
	}

	/**
	 * Checks if a value is a mathematical operator (+, -, * or /)
	 * @param value the value to be checked
	 * @return <code>true</code> if the value is an mathematical operator
	 */
	private static boolean isOperator(String value){
		if (value.equals("+") ||value.equals("-")|| value.equals("*")|| value.equals("/"))
			return true;
		else return false;
	}

	/**
	 * Checks if a value is a number
	 * @param value the value to be checked
	 * @return <code>true</code> if the value is a number
	 */
	private static boolean isNumber(String value){
		for (int i = 0; i < value.length(); i++){
			if (value.charAt(i) < '0' || value.charAt(i) > '9')
				return false;
		}
		return true;
	}

	/**
	 * Converts a numerical string to a number
	 * @param value the string to be converted
	 * @return the number in the string
	 * @throws IllegalArgumentException if the string doesn't represent a number
	 */
	static int stringToNumber(String value)throws IllegalArgumentException{
		if (!isNumber(value))
			throw new IllegalArgumentException("This is not a number!");
		int number = 0;
		for (int i = 0; i < value.length(); i++)
			number += (value.charAt(i)-48)* Math.pow(10, value.length()-1-i);
		return number;
	}

	/**
	 * Computes the result from this Expression
	 * @return the result of this Expression
	 */
	public int evaluate(){
		String value = expressionTree.getValue();
		int result = 0;
		if (isNumber(value)){
			return stringToNumber(value);
		}
		if (value.equals("+")){
			result = 0;
			for (Iterator<Tree<String>> i = expressionTree.iterator(); i.hasNext();){
				result += (new Expression(i.next().toString())).evaluate();
			}
		}
		if (value.equals("*")){
			result = 1;
			for (Iterator<Tree<String>> i = expressionTree.iterator(); i.hasNext();){
				result *= (new Expression(i.next().toString())).evaluate();
			}
		}
		if (value.equals("-")){
			result = (new Expression(expressionTree.getChild(0).toString())).evaluate() -
			(new Expression(expressionTree.getChild(1).toString())).evaluate();
		}
		if (value.equals("/")){
			result = (new Expression(expressionTree.getChild(0).toString())).evaluate() /
			(new Expression(expressionTree.getChild(1).toString())).evaluate();
		}
		return result;
	}

	/**
	 * Converts this prefix expression to infix expression
	 * @return the infix version of the prefix expression
	 */
	@Override
	public String toString() {
		String string = "";
		if (expressionTree.getNumberOfChildren()==0)
			return expressionTree.getValue();
		else{
			string += "(";
			for (int i = 0; i < expressionTree.getNumberOfChildren(); i++){
				Tree<String> subtree = expressionTree.getChild(i);
				if (subtree.getNumberOfChildren()==0)
					string += subtree.getValue();
				else
					string += (new Expression(subtree.toString())).toString()+" ";
				if (i < expressionTree.getNumberOfChildren()-1)
					string += " " + expressionTree.getValue()+" ";
			}
			string += ")";
		}
		return string;
	}
}

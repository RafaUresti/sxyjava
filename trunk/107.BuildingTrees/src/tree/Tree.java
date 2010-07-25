package tree;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The tree API for building trees. Constructor and methods for tree building are provided.
 * @author sxycode
 * @version 4-10-2008
 */
public class Tree<V> {
	private V value;
	private ArrayList<Tree<V>> subtreeList;


	/**
	 * Constructor for Tree.
	 * @param value The value to be placed in the root.
	 * @param children The children of the root
	 * @throws IllegalArgumentException if adding the children to the new root node would result in a circular tree.
	 * That is, there must be no path from a node in the Tree back to itself.
	 */
	public Tree(V value, Tree<V>... children) throws IllegalArgumentException{
		setValue(value);
		subtreeList = new ArrayList<Tree<V>>();
		this.addChildren(children);
	}


	/**
	 * Sets the value in this node
	 * @param value The value to be set
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * 	Gets the value in this node
	 * @return the value in this node
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Adds the child as the new index'th child of this Tree;
	 * subsequent nodes are "moved over" as necessary to make room for the new child.
	 * @param index the index'th position to add a new child
	 * @param child the child to add
	 * @throws IllegalArgumentException if the result would be a circular tree 
	 * @throws IndexOutOfBoundsException if index is negative or is greater than the current number of children of this node. 
	 */
	public void addChild(int index, Tree<V> child) throws 
	IllegalArgumentException, IndexOutOfBoundsException {
		if (child.contains(this))
			throw new IllegalArgumentException("Circular Tree Formed!");
		subtreeList.add(index, child);
	}
	
	/**
	 * Adds a new child to this node after any current children.
	 * @param child the new child to add
	 * @throws IllegalArgumentException if result would be a circular tree
	 */
	@SuppressWarnings("unchecked")
	public void addChild(Tree<V> child) throws IllegalArgumentException{
		addChildren(child);
	}

	/**
	 * Adds the new children to this node after any current children.
	 * @param children the new children to add
	 * @throws IllegalArgumentException if result would be a circular tree
	 */
	public void addChildren(Tree<V>... children) throws IllegalArgumentException {
		for (Tree<V> child: children){
			if (child.contains(this))
				throw new IllegalArgumentException("Circular Tree Formed!");
			subtreeList.add(child);
		}
	}

	/**
	 * Gets the number of children that this node has 
	 * @return the number of children that this node has 
	 */
	public int getNumberOfChildren(){
		return subtreeList.size();
	}

	/**
	 * Gets the index'th child of this node
	 * @param index the index of the child to get
	 * @return the index'th child of this node
	 * @throws IndexOutOfBoundsException if index is negative or is greater than
	 *  or equal to the current number of children of this node. 
	 */
	public Tree<V> getChild(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index > subtreeList.size()-1)
			throw new IndexOutOfBoundsException();
		return subtreeList.get(index);
	}

	/**
	 * Returns an iterator for the children of this node
	 * @return an iterator for the children of this node
	 */
	public Iterator<Tree<V>> iterator() {
		return subtreeList.iterator();
	}

	/**
	 * Searches this Tree for a node that is == to node
	 * @param node the node to find
	 * @return <code>true</code> if found. <code>false</code> otherwise
	 */
	boolean contains(Tree<V> node) {
		if (this == node)
			return true;
		for (int i = 0; i < getNumberOfChildren(); i++){
			if (getChild(i).contains(node))
				return true;
		}
		return false;
	}

	/**
	 * Translates a String description of a tree into a Tree<String> object. 
	 * The treeDescription has the form value(child child ... child), 
	 * where a value is any sequence of characters not containing parentheses or whitespace, 
	 * and each child is either just a (String) value or is another treeDescription. 
	 * Whitespace is optional except where needed to separate values.
	 * @param treeDescription the tree description to be parsed
	 * @return the resulting tree
	 */
	@SuppressWarnings("unchecked")
	public static Tree<String> parse(String treeDescription) throws IllegalArgumentException{
		if (!hasTreeParentheses(treeDescription))
			throw new IllegalArgumentException("Improper parentheses!");
		TreeNodeSep treeNodeSep = new TreeNodeSep(treeDescription);
		String value = treeNodeSep.getRootString();
		if (value == null) throw new IllegalArgumentException("Improper Tree description!");
		for (int i = 0; i < value.length(); i++){
			if (isWhiteSpace(value.charAt(i)))
				throw new IllegalArgumentException("Improper Tree description!");
		}
		String subtreeString = treeNodeSep.getSubtreeString();
		ArrayList<String> subtreeStringList = sepSubtreeStrings(subtreeString);
		if (subtreeStringList != null){
			Tree<String>[] trees = new Tree[subtreeStringList.size()];
			for (int i = 0; i < subtreeStringList.size(); i++){
				trees[i] = parse(subtreeStringList.get(i));
			}
			return new Tree<String>(value, trees);
		}
		else return new Tree<String>(value);
	}

	/**
	 * Checks if a character is a white space.
	 * @param c the character to check
	 * @return <code>true</code> if the character is a white space.
	 */
	private static boolean isWhiteSpace(char c) {
		if (c == ' ' || c == '\t' || c == '\r' || c == '\f' || c == '\n')
			return true;
		else return false;
	}


	/**
	 * Checks if a tree description has correct pairing of left and right parentheses
	 * and if parentheses do exist, one pair of parentheses encloses all the other pairs. 
	 * @return <code>true</code> if the tree description meets the description above
	 */
	private static boolean hasTreeParentheses(String treeDescription) {
		int leftCount = 0, rightCount = 0;
		String description = treeDescription.trim();
		for (int i = 0; i < description.length(); i++){
			if (description.charAt(i) == '(')
				leftCount++;
			if (description.charAt(i) == ')')
				rightCount++;
			if (leftCount < rightCount) return false;
			if (leftCount != 0 && i < description.length()-1 && leftCount == rightCount)
				return false;
		}
		if (leftCount != rightCount)
			return false;
		else return true;
		
	}


	/**
	 * Separates the subtree descriptions from the subtrees string
	 * @param subtreeString the string that contains the subtree descriptions
	 * @return a list of subtree descriptions
	 */
	private static ArrayList<String> sepSubtreeStrings(String subtreeString){
		if (subtreeString == null) return null;
		ArrayList<String> subtreeStringList = new ArrayList<String>();
		int subtreeBegin = 0;
		int leftCount = 0, rightCount = 0;//counts the occurrences of '(' and ')', respectively
		for (int i = 0; i < subtreeString.length(); i++){
			if (subtreeString.charAt(i) == '(')
				leftCount++;
			if (subtreeString.charAt(i) == ')')
				rightCount++;
			if (leftCount == rightCount){
				if (leftCount!=0){		
					subtreeStringList.add(subtreeString.substring(subtreeBegin,i+1).trim());
					subtreeBegin = i+1;
					leftCount =0;
					rightCount = 0;
				}
				else if (subtreeString.charAt(i) == ' '){
					int spaceCount =1;
					while ((subtreeString.charAt(++i) == ' ')&& i < subtreeString.length()){
						spaceCount++;
					}//get past extra white space
					i--;
					if (i < subtreeString.length()-1 && subtreeString.charAt(i+1) != '('){//no subtrees
						if (i+1 - subtreeBegin > spaceCount)
							subtreeStringList.add(subtreeString.substring(subtreeBegin,i+1).trim());
						subtreeBegin = i+1;
					}
				}
				else if (i == subtreeString.length()-1){
					subtreeStringList.add(subtreeString.substring(subtreeBegin).trim());
				}
			}
		}
		return subtreeStringList;
	}

	/**
	 * Check if an object is a Tree and has the same shape and contains the same values as this Tree
	 * @param obj the obj to check 
	 * @return <code>true</code> if obj is a Tree and has the same shape and contains the same values as this Tree
	 */
	@SuppressWarnings("unchecked")
	@Override 
	public boolean equals(Object obj){
		if (!(obj instanceof Tree))
			return false;
		Tree<V> tree = (Tree<V>)obj;
		if (!getValue().equals(tree.getValue()) || 
				getNumberOfChildren() != tree.getNumberOfChildren())
			return false;
		for (int i = 0; i < getNumberOfChildren(); i++){
			if (!getChild(i).equals(tree.getChild(i)))
				return false;
		}
		return true;
	}

	/**
	 * Generates a String representing this Tree. The returned String is in the same format as the parse method expects as input. 
	 * @return the String representation of the tree.
	 */
	@Override
	public String toString() {
		String string = "";
		string += value+" ";
		if (getNumberOfChildren()>0){
			string += " (";
			for (Tree<V> tree: subtreeList){
				string += tree.toString();
			}
			string += ")";
		}
		return string;
	}
}

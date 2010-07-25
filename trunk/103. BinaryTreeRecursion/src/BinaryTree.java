
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A BinaryTree consists of "nodes"--each "node" is itself a BinaryTree.
 * Each node has a parent (unless it is the root), may have a left child,
 * and may have a right child. This class implements loop-free binary trees,
 * allowing shared subtrees.
 * 
 * @author David Matuszek
 * @author sxycode
 * @version Feb. 7th, 2008
 */
public class BinaryTree<V> {

	/**
	 * The value (data) in this node of the binary tree; may be of
	 * any object type.
	 */
	public V value;
	private BinaryTree<V> leftChild;
	private BinaryTree<V> rightChild;

	/**
	 * Constructor for BinaryTree.
	 * 
	 * @param value The value to be placed in the root.
	 * @param leftChild The left child of the root (may be null).
	 * @param rightChild The right child of the root (may be null).
	 */
	public BinaryTree(V value, BinaryTree<V> leftChild, BinaryTree<V> rightChild) {
		this.value = value;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	/**
	 * Constructor for a BinaryTree leaf node (that is, with no children).
	 * 
	 * @param value The value to be placed in the root.
	 */
	public BinaryTree(V value) {
		this(value, null, null);
	}

	/**
	 * Getter method for the value in this BinaryTree node.
	 * 
	 * @return The value in this node.
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Getter method for left child of this BinaryTree node.
	 * 
	 * @return The left child (<code>null</code> if no left child).
	 */
	public BinaryTree<V> getLeftChild() {
		return leftChild;
	}

	/**
	 * Getter method for right child of this BinaryTree node.
	 * 
	 * @return The right child (<code>null</code> if no right child).
	 */
	public BinaryTree<V> getRightChild() {
		return rightChild;
	}

	/**
	 * Sets the left child of this BinaryTree node to be the
	 * given subtree. If the node previously had a left child,
	 * it is discarded. Throws an <code>IllegalArgumentException</code>
	 * if the operation would cause a loop in the binary tree.
	 * 
	 * @param subtree The node to be added as the new left child.
	 * @throws IllegalArgumentException If the operation would cause
	 *         a loop in the binary tree.
	 */
	public void setLeftChild(BinaryTree<V> subtree) throws IllegalArgumentException {
		if (contains(subtree, this)) {
			throw new IllegalArgumentException(
					"Subtree " + this + " already contains " + subtree);
		}
		leftChild = subtree;
	}

	/**
	 * Sets the right child of this BinaryTree node to be the
	 * given subtree. If the node previously had a right child,
	 * it is discarded. Throws an <code>IllegalArgumentException</code>
	 * if the operation would cause a loop in the binary tree.
	 * 
	 * @param subtree The node to be added as the new right child.
	 * @throws IllegalArgumentException If the operation would cause
	 *         a loop in the binary tree.
	 */
	public void setRightChild(BinaryTree<V> subtree) throws IllegalArgumentException {
		if (contains(subtree, this)) {
			throw new IllegalArgumentException(
					"Subtree " + this + " already contains " + subtree);
		}
		rightChild = subtree;
	}

	/**
	 * Sets the value in this BinaryTree node.
	 * 
	 * @param value The new value.
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * Tests whether this node is a leaf node.
	 * 
	 * @return <code>true</code> if this BinaryTree node has no children.
	 */
	public boolean isLeaf() {
		return leftChild == null && rightChild == null;
	}

	/**
	 * Tests whether this BinaryTree is equal to the given object.
	 * To be considered equal, the object must be a BinaryTree,
	 * and the two binary trees must have equal values in their
	 * roots, equal left subtrees, and equal right subtrees.
	 * 
	 * @return <code>true</code> if the binary trees are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof BinaryTree)) {
			return false;
		}
		BinaryTree<V> otherTree = (BinaryTree<V>) o;
		return equals(value, otherTree.value) && equals(leftChild, otherTree.getLeftChild()) && equals(rightChild, otherTree.getRightChild());
	}

	/**
	 * Tests whether its two arguments are equal.
	 * This method simply checks for <code>null</code> before
	 * calling <code>equals(Object)</code> so as to avoid possible
	 * <code>NullPointerException</code>s.
	 * 
	 * @param x The first object to be tested.
	 * @param y The second object to be tested.
	 * @return <code>true</code> if the two objects are equal.
	 */
	private boolean equals(Object x, Object y) {
		if (x == null) {
			return y == null;
		}
		return x.equals(y);
	}

	/**
	 * Tests whether the <code>tree</code> argument contains within
	 * itself the <code>targetNode</code> argument.
	 * 
	 * @param tree The root of the binary tree to search.
	 * @param targetNode The node to be searched for.
	 * @return <code>true</code> if the <code>targetNode</code> argument can
	 *        be found within the binary tree rooted at <code>tree</code>.
	 */
	protected boolean contains(BinaryTree<V> tree, BinaryTree<V> targetNode) {
		if (tree == null) {
			return false;
		}
		if (tree == targetNode) {
			return true;
		}
		return contains(targetNode, tree.getLeftChild()) || contains(targetNode, tree.getRightChild());
	}

	/**
	 * Returns a String representation of this BinaryTree.
	 * 
	 * @see java.lang.Object#toString()
	 * @return A String representation of this BinaryTree.
	 */
	public String toString() {
		if (isLeaf()) {
			return value.toString();
		} else {
			String root, left = "null", right = "null";
			root = value.toString();
			if (getLeftChild() != null) {
				left = getLeftChild().toString();
			}
			if (getRightChild() != null) {
				right = getRightChild().toString();
			}
			return root + " (" + left + ", " + right + ")";
		}
	}

	/**
	 * Computes a hash code for the complete binary tree rooted
	 * at this BinaryTree node.
	 * 
	 * @return A hash code for the binary tree with this root.
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = value.hashCode();
		if (leftChild != null) {
			result += 3 * leftChild.hashCode();
		}
		if (rightChild != null) {
			result += 7 * rightChild.hashCode();
		}
		return result;
	}

	/**
	 * Prints the binary tree rooted at this BinaryTree node.
	 */
	public void print() {
		print(this, 0);
	}

	private void print(BinaryTree root, int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print("   ");
		}
		if (root == null) {
			System.out.println("null");
			return;
		}
		System.out.println(root.value);
		if (root.isLeaf()) {
			return;
		}
		print(root.leftChild, indent + 1);
		print(root.rightChild, indent + 1);
	}

	/**
	 * Finds the left most leaf of this binary tree.
	 * @return The left most leaf of the binary tree.
	 */
	public BinaryTree<V> leftmostDescendant() {

		if ((getLeftChild()) == null) {
			return this;
		} else {
			return leftChild.leftmostDescendant();
		}
	}

	/**
	 * Finds the right most leaf of this binary tree.
	 * @return The right most leaf of this binary tree.
	 */
	public BinaryTree<V> rightmostDescendant() {

		if ((getRightChild()) == null) {
			return this;
		} else {
			return rightChild.rightmostDescendant();
		}
	}

	/**
	 * Computes the total number of nodes in this binary tree.
	 * @return The total number of nodes in this binary tree.
	 */
	public int numberOfNodes() {
		int leftNodes, rightNodes;
		if (leftChild == null) {
			leftNodes = 0;
		} else {
			leftNodes = leftChild.numberOfNodes();
		}
		if (rightChild == null) {
			rightNodes = 0;
		} else {
			rightNodes = rightChild.numberOfNodes();
		}
		return 1 + leftNodes + rightNodes;

	}

	/**
	 * Computes the depth of this binary tree. The depth is the length of
	 * the longest path from this node to a leaf.
	 * @return The depth of this binary tree.
	 */
	public int depth() {
		int leftDepth, rightDepth;
		if (leftChild == null) {
			leftDepth = 0;
		} else {
			leftDepth = leftChild.depth() + 1;
		}
		if (rightChild == null) {
			rightDepth = 0;
		} else {
			rightDepth = rightChild.depth() + 1;
		}
		return Math.max(leftDepth, rightDepth);
	}

	/**
	 * Checks if this binary tree contains a node that has an equal value
	 * as that value parameter.
	 * @param value The value to check for equal value node in this binary tree
	 * @return True if this binary tree contains a node with an equal value
	 */
	public boolean containsEqualValue(V value) {
		if (this.value.equals(value)) {
			return true;
		}
		boolean lv, rv;
		if (leftChild == null) {
			lv = false;
		} else {
			lv = leftChild.containsEqualValue(value);
		}
		if (rightChild == null) {
			rv = false;
		} else {
			rv = rightChild.containsEqualValue(value);
		}
		return lv || rv;
	}

	/** 
	 * Checks if this binary tree contains a same value object as the value parameter.
	 * @param value The value object to be found in this binary tree
	 * @return True if this binary tree has the same object
	 */
	public boolean containsSameValue(V value) {
		if (this.value == value) {
			return true;
		}
		boolean lv, rv;
		if (leftChild == null) {
			lv = false;
		} else {
			lv = leftChild.containsSameValue(value);
		}
		if (rightChild == null) {
			rv = false;
		} else {
			rv = rightChild.containsSameValue(value);
		}
		return lv || rv;
	}

	/**
	 * Creates a set of all the leaves in this binary tree.
	 * @return A set of all leaves
	 */
	public Set<BinaryTree<V>> leaves() {
		Set<BinaryTree<V>> leafSet = new HashSet<BinaryTree<V>>();
		if (this != null && isLeaf() && !leafSet.contains(this)) {
			leafSet.add(this);
		}
		if (leftChild != null) {
			leafSet.addAll(leftChild.leaves());
		}
		if (rightChild != null) {
			leafSet.addAll(rightChild.leaves());
		}
		return leafSet;
	}

	/**
	 * Creates a set of all values in this binary tree
	 * @return A set of all values in this binary tree
	 */
	public Set<V> valuesOf() {
		Set<V> valueSet = new HashSet<V>();

		if (!valueSet.contains(this.value)) {
			valueSet.add(this.value);
		}
		if (leftChild != null) {
			valueSet.addAll(leftChild.valuesOf());
		}
		if (rightChild != null) {
			valueSet.addAll(rightChild.valuesOf());
		}

		return valueSet;

	}

	/**
	 * Creates a list of the values in the leaves of this binary tree,
	 * in left-to-right order
	 * @return The list of values in the leaves of this binary tree
	 */
	public List<V> fringe() {
		List<V> fringeList = new ArrayList<V>();
		if (this.leftChild == null && this.rightChild == null) {
			fringeList.add(this.value);
		}
		if (leftChild != null) {
			fringeList.addAll(leftChild.fringe());
		}
		if (rightChild != null) {
			fringeList.addAll(rightChild.fringe());
		}
		return fringeList;
	}

	/**
	 * Creates a new binary tree equal to this binary tree.
	 * @return The new binary tree.
	 */
	public BinaryTree<V> copy() {
		BinaryTree<V> treeCopy = new BinaryTree<V>(null);
		treeCopy.value = this.value;
		if (leftChild != null) {
			treeCopy.leftChild = leftChild.copy();
		}
		if (rightChild != null) {
			treeCopy.rightChild = rightChild.copy();
		}
		return treeCopy;

	}

	/**
	 * Creates a new binary tree which is the mirror image of the binary
	 * tree whose root is at this binary tree.
	 * @return The reversed new binary tree.
	 */
	public BinaryTree<V> reverse() {
		BinaryTree<V> reverseTree;
		reverseTree = this.copy();
		reverseTree.reverseInPlace();
		return reverseTree;
	}

	/**
	 * Rearranges the binary tree rooted at this binary tree to be the 
	 * mirror image of its original structure
	 */
	public void reverseInPlace() {
		if (isLeaf()) {
			return;
		} else {
			BinaryTree<V> temp = leftChild;
			setLeftChild(rightChild);
			setRightChild(temp);
			if (leftChild != null)
				leftChild.reverseInPlace();
			if (rightChild != null)
				rightChild.reverseInPlace();
		}
	}
}     

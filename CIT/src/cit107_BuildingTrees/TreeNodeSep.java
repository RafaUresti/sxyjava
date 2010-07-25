package cit107_BuildingTrees;

/**
 * Separates a tree description string into root description string
 * and subtrees description string
 * @author sxycode
 * @version Mar. 23rd, 2008
 */
public class TreeNodeSep{
	private String rootString = null;
	private String subtreeString = null;

	TreeNodeSep(String treeDescription){
		String treeString = treeDescription.trim();
		sepTree(treeString);
	}
	/**
	 * Separates the tree string into root string and subtrees string
	 * @param treeString the tree string to be separated
	 */
	private void sepTree(String treeString){
		int subtreeBegin;
		boolean hasSubTree = false;
		for (subtreeBegin = 0; subtreeBegin < treeString.length(); subtreeBegin++){
			if (treeString.charAt(subtreeBegin)== '('){
				hasSubTree = true;
				if (subtreeBegin > 0 && treeString.charAt(treeString.length()-1) == ')'){
					rootString = treeString.substring(0, subtreeBegin).trim();
					subtreeString = treeString.substring(++subtreeBegin, treeString.length()-1).trim();
				}
				break;
			}
			if(!hasSubTree && subtreeBegin+1 == treeString.length()){//after final recursion still no subTree found
				rootString = treeString;
			}
		}
	}
	/**
	 * Getter for the root string
	 * @return the root string
	 */
	String getRootString(){
		return rootString;
	}
	
	/**
	 * Setter for the subtrees string
	 * @return the subtrees string
	 */
	String getSubtreeString(){
		return subtreeString;
	}
}

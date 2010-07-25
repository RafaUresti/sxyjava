package cit110_BugsInterpreterII;
import java.io.*;
import java.util.*;

import cit107_BuildingTrees.Tree;
import cit108_ParserForBugsLanguage.SyntaxException;
import cit108_ParserForBugsLanguage.Token;
import cit108_ParserForBugsLanguage.Parser;

/**
 * Interprets the Bugs language and starts Bugs threads
 * @author sxycode
 * @version 4-27-2008
 */
public class Interpreter{


	HashMap<String, Double> variables = new HashMap<String, Double>();
	HashMap<String, Tree<Token>> functions = new HashMap<String, Tree<Token>>();
	HashMap<String, Bug> bugs = new HashMap<String, Bug>();
	
	String programDescription = "";
	Tree<Token> programTree;
	private int speed = 50;
	private boolean blocked = false;//to be released by "Run" button
	List<Bug> deadBugs = new ArrayList<Bug>();//collect Bugs who have finished tasks
	private List<Command> commands = new ArrayList<Command>();

	
	/**
	 * Constructor of the Interpreter class.
	 * @param programDescription The description of the Bugs Program 
	 */
	public Interpreter(String programDescription){
		this.programDescription = programDescription;
	}

	/**
	 * Constructor of the Interpreter class
	 * @param programTree The tree representing the Bugs program
	 */
	public Interpreter (Tree<Token> programTree){
		this.programTree = programTree;
	}
	/**
	 * The main program while not running from a GUI. 
	 * @param args
	 */
	public static void main(String[] args) {
		String description = "";
		File programFile = new File("/bugProgram.txt");
		Scanner scanner;
		try {
			scanner = new Scanner(programFile);
			while (scanner.hasNextLine()){
				description += scanner.nextLine()+"\n";
			}
//			print(description);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		new Interpreter(description).runString();
	}

	/**
	 * Start processing the bugs program from programDescription
	 */
	void runString() {
		Parser parser = new Parser(programDescription);
		if(!parser.isProgram())
			throw new SyntaxException("Not a valid program!");
		programTree = parser.stack.peek();
//		print(programTree.toString());
		run();
	}

	/**
	 * Start processing the bugs program from programTree
	 */
	public void run(){
		processProgram(programTree);
		try{
			for (String bugName:bugs.keySet()){
				Bug bug = bugs.get(bugName);
				bug.start();
			}
		}catch (RuntimeException e){
			System.out.println("Error from Bug!");
		}
/* These statements are for operating without control from GUI
		while(bugs.size()>0){
			unblockAllBugs();	
		}
*/
//		print("Interpreter thread dies");
	}

	/**
	 * Processes the entire Bugs program
	 * @param tree The Bugs program
	 */
	private void processProgram(Tree<Token> tree){
		processAllbugs(tree.getChild(0));
		createBugs(tree.getChild(1));
	}
	/**
	 * Creates Bugs, put them into <code>bugs</code>
	 * and set their state to blocked.
	 * @param tree the list of bug description
	 */
	private void createBugs(Tree<Token> tree) {
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			Tree<Token> bugTree = tree.getChild(i);
			String bugName = bugTree.getChild(0).getValue().value;
			Bug bug = new Bug(bugName, bugTree, this);
			if (bugs.containsKey(bugName)) throw new IllegalArgumentException(bugName + " has already been declared!");
			else bugs.put(bugName, bug);
			bug.setBlocked(true);
		}
//		print(bugs.toString());
	}

	/**
	 * Processes the <code>Allbugs</code> part of the program
	 * @param tree The <code>Allbugs</code> part of the program
	 */
	private void processAllbugs(Tree<Token> tree){
		processVarList(tree.getChild(0));
		processFunctionList(tree.getChild(1));
	}

	/**
	 * Processes the list of variable declarations in the <code>Allbugs</code>
	 * part of the program.
	 * @param tree The list of variable declarations
	 */
	private void processVarList(Tree<Token> tree){
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			processVarDeclaration(tree.getChild(i));
		}
	}

	/**
	 * Process each var declaration
	 * @param tree The var declaration
	 */
	private void processVarDeclaration(Tree<Token> tree) {
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			String newVar = tree.getChild(i).getValue().value;
			if (variables.containsKey(newVar)) throw new IllegalArgumentException(newVar + " has already been declared!");
			else variables.put(newVar, 0.0);
		}
	}

	/**
	 * Process the list of function definitions in the <code>Allbugs</code>
	 * part of the The list of function definitions
	 */
	private void processFunctionList(Tree<Token> tree){
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			Tree<Token> functionTree = tree.getChild(i);
			String name = functionTree.getChild(0).getValue().value;
			if (!functions.containsKey(name))
				functions.put(name, functionTree);
			else throw new IllegalArgumentException("function "+name+" has already been defined!");
		}
	}

	/**
	 * Stops the bug thread execution until its no longer blocked. 
	 * @param bug the bug thread
	 */
	synchronized void getActionPermit(Bug bug){
//		print("    Bug "+ bug.name + " is trying to get a action permit.");
		while (bug.isBlocked()){
			try{
//				print("    Bug "+ bug.name + " is waiting.");
				wait();
			}
			catch (InterruptedException e){
//				print("    Bug "+ bug.name + " has been interrupted.");
			}
		}
	}
	
	/**
	 * Completes the current action of the bug thread by blocking it, 
	 * pausing for a while and notifying other threads.
	 * @param bug the bug whose actions is to be blocked.
	 */
	synchronized void completeCurrentAction(Bug bug) {
		bug.setBlocked(true);
		try {
			Bug.sleep(1000/(speed+1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		print("  Bug " + bug.name + " has done action and is now blocked.");
		notifyAll();
	}

	/**
	 * The speed of the bug thread execution
	 * @param speed
	 */
	public void setSpeed(int speed){
		this.speed = speed;
	}

	/**
	 * Waits for all the bug threads to be blocked, unblocks all
	 * of them and notifies all of them. 
	 */
	synchronized void unblockAllBugs() {
//		print("    Master is trying to reset all.");
		while (blocked){
			try {
//				print("    Master is paused.");
				wait();
			}
			catch (InterruptedException e) {
//				print("    Master has been interrupted.");
			}
		}
		while (countBlockedBugs() < bugs.size()- deadBugs.size()) {
			try {
//				print("    Master is waiting for all workers to be blocked.");
				wait();
			}
			catch (InterruptedException e) {
//				print("    Master has been interrupted.");
			}
		}
//		printResultsSoFar();
		for (String bugName: bugs.keySet()){
			Bug bug = bugs.get(bugName);
			bug.setBlocked(false);
		}
//		print("\nMaster has unblocked all workers.");
		notifyAll();  
	}
	


	/**
	 * Counts the number of bug threads that are blocked
	 * @return the number of blocked bug threads
	 */
	private int countBlockedBugs() {
		int counter = 0;
		for (String bugName: bugs.keySet()){
			Bug bug = bugs.get(bugName);
			if (bug.isBlocked()) counter++;
		}
		return counter;
	}

	/**
	 * Terminates a bug thread by removing it from the 
	 * bugs list and notifies all other threads
	 * @param bug
	 */
	synchronized void terminateBug(Bug bug) {
		deadBugs.add(bug);
//		System.out.println("   Bug " + bug.name + " has terminated. And now bugs size is " + bugs.size());
		notifyAll();
	}

	synchronized void addToCommands(Command command){
		commands.add(command);
	}
	synchronized List<Command> getCommands(){
		return commands;
	}

/*------------Methods for testing purposes only.------------------------
	private void printResultsSoFar() {
		for (String bugName: bugs.keySet()){
			Bug bug = bugs.get(bugName);
			print("Bug "+ bug.name + " is at x=" +bug.x +", y=" +bug.y +" angle ="+ bug.angle);
		}
	}

	static void print(String string){
		if (false)
			System.out.println(string);
	}
*/
}

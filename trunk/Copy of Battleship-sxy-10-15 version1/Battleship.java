/**
 * Assignment 4 Sudoku for CIT591, Fall 2007.
 * @author Dave Matuszek
 * @author Swetha Mandanapu
 * @author Xiaoyi Sheng
 * @version Oct 13, 2007
 */
public class Battleship {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Battleship().run();
	}
	private void run() {
		System.out.println("Welcome to Battleship!");
		Ocean ocean= new Ocean();
		ocean.print();
		
	}
}


/**
 * Assignment 4 Battleship for CIT591, Fall 2007.
 * @author Dave Matuszek
 * @author Xiaoyi Sheng
 * @author Swetha Mandanapu
 * @version Oct 13, 2007
 */
import java.util.Scanner;
public class Battleship {
	/**
	 * Creates a new Ocean object.
	 * Initialize program
	 */
	private int row;
	private int column;
	private Ocean ocean;
	private Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		new Battleship().run();
	}
	/** Creates a new Ocean object, initialize the game by calling the relevant methods, 
	 * displays the score at the end of the game, 
	 * asks the user if he/she wants to play another game 
	 */
	private void run() {
		boolean playAgain=false;
		System.out.println("Welcome to Battleship!\n");
		printGameRules();
		do {
			ocean= new Ocean();
			ocean.placeAllShipsRandomly();
			ocean.print();
			while (!ocean.isGameOver()) {
				playGame();
			}
			System.out.println("Congratulations! You have destroyed the entire enemy fleet!");
			System.out.println("Your score is: "
					+ocean.numberOfShotsFired()+" shots and "
					+ocean.numberOfHits()+ " hits.");
			System.out.println("Do you want to play again?");
			playAgain=(sc.next().equalsIgnoreCase("y"));
		}while (playAgain);
	}
	/**
	 * Ask the user to enter the location where he/she wants to shoot at
	 * Displays if that location is a hit or miss
	 * 
	 */
	private void playGame() {
		boolean hitTarget;
		System.out.println("Please choose the location to shoot at by entering the row and column");
		
		System.out.print("Please choose the row number (0~9):");
		intInput();//verifies that the input is a number, same for column
		row =numberInput();
		sc.nextLine ();//cleans the unneeded extra inputs, same for column

		System.out.print("Please choose the column number (0~9):");
		intInput();
		column = numberInput();
		sc.nextLine ();

		System.out.println();
		hitTarget = ocean.shootAt(row, column);
		ocean.print();
		System.out.println();
		if (hitTarget)
			System.out.println("Good shot!\n");
		else System.out.println("Sorry, you missed!\n");
		System.out.println();
	}
	
	/**
	 * Displays the rules of the game
	 * 
	 */
	private void printGameRules() {
		System.out.println("There is a enemy fleet of one battleship, two cruisers, " +
				"\nthree destroyers and four submarines in the ocean. " +
				"\nA battleship takes 4 locations, a cruiser takes 3 locations, " +
				"\na destroyer takes 2 locations and a submarine takes 1 location." +
				"\nThey can be placed either horizontally or vertically " +
				"\nand none of them are immediately adjacent to each other, " +
				"\neither horizontally, vertically or diagonally." +
				"\nYour mission is to destroy the entire fleet with as few shots as possible." +
				"\nYou can shoot to one location at a time. " +
				"\nIf you hit a ship, an 'X' will be displayed on the location you shot." +
				"\nOtherwise, an 'o' will be displayed instead. " +
				"\nYou have to hit all the locations that a ship occupies to destroy it. " +
				"\nYour score will be the number of shots it took you to destroy all the ships." +
				"\nTry your best, soldier!" +"\n");
	}
	
	/**
	 * verifies if the key entered by the user is an integer
	 * 
	 */
	void intInput() {		
		while (!sc.hasNextInt()){
			sc.nextLine ();
			inputError();
		}
	}
	
	/**
	 * verifies if the number entered by the user is a valid number (0-9)
	 * 
	 * @return the entered number if it is a valid one.
	 */
	int numberInput(){
		int number=sc.nextInt();
		while (number<0 || number>9){	//check if the number is 1,2 or 3
			sc.nextLine ();
			inputError();
			intInput();//checks if the next input is a number
			number = sc.nextInt();
		}
		return number;
	}
	
	/**
	 *  Displays an error message
	 *  
	 */
	void inputError() {
		System.out.print("Error! Please enter a number between 0~9: ");
	}
}

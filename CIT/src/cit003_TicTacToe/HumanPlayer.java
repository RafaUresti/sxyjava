package cit003_TicTacToe;
import java.util.Scanner;

public class HumanPlayer {
	Scanner scanner = new Scanner(System.in);

	/**
	 * 
	 * 
	 * @param TicTacToeBoard board
	 */
	public void makeMove(TicTacToeBoard board) {

		int row = 0;
		int column = 0;
		char ch = 'O'; // human is always 'O'
		boolean repeatInput = false;  //forces human to provide valid input and to select an unoccupied space
		System.out.println();
		System.out.println("Please select an unoccupied square.");				
		do //continues loop until human selects an unoccupied space.
		{	
			System.out.print("Please enter the row (1-3): ");
			intInput();	//check if input is a number
			row = numberInput();
			scanner.nextLine ();
			System.out.print("Please enter the column (1-3): ");
			intInput();
			column = numberInput();
			System.out.println();
			scanner.nextLine ();
			if (board.get(row, column)!= ' '){
				System.out.println("Row " + row + ", column " + column +
				" is already taken.");
				System.out.println("Please select an unoccupied square.");
				repeatInput = true;
			}
			else repeatInput = false;
		}
		while (repeatInput);		
		board.set(row, column, ch);  //sets validated human move.	
	}
	
	/**
	 * Makes sure input is an integer
	 * 	  
	 */
	void intInput() {		
		while (!scanner.hasNextInt()){
			scanner.nextLine ();
			System.out.print("Error! Please enter a number: ");
		}
	}

	/**
	 * Makes sure input is an integer input: 1,2 or 3	 
	 * 	 
	 * @return number integer input of user.  Either row or column.
	 */
	int numberInput(){
		int number=scanner.nextInt();
		while (!(number==1 || number==2 || number==3)){	//check if the number is 1,2 or 3
			scanner.nextLine ();
			System.out.print("Error! Invalid number. Try again: ");
			intInput();//checks if the next input is a number
			number = scanner.nextInt();
		}
		return number;
	}

	/**
	 * Asks human whether to play again.
	 * All scanner objects are created in HumanPlayer class
	 * 
	 * @param question string with an initialize value of "Y"
	 * @return boolean true results in playing the game again
	 */
	public boolean ask(String question) {
		System.out.println("Would you like to play again?"
				+ "\nEnter \"Y\" or \"N\".");
		question = scanner.next();
		return (question.equalsIgnoreCase("y"));

	}
}

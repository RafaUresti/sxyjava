/**
 * Assignment 3 Tic-tac-toe for CIT591, Fall 2007. 
 * @author Dave Matuszek
 * @author Anthony Schreiner
 * @author sxycode
 * @version Sep 27, 2007
 */
public class TicTacToe {

	/**
	 * Creates new TicTacToe object.
	 * Initializes program
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		new TicTacToe().run();
	}
	
	/**
	 * Creates new objects from TicTacToeBoard, HumanPlayer, and ComputerPlayer Class.
	 * Sets computer as player 1 for game 1 and alternates player 1 after game 1.
	 * Continues alternating moves between computer and human player until game over.
	 * Keeps a count of games won, lost, and tied.
	 * Asks human player whether to play again.
	 */
	
	
	
	private void run(){		
		boolean playAgain=true;
		boolean computerFirst=true;
		boolean humanTurn=true;
		int won = 0;
		int lost = 0;
		int tie = 0;
		String question = "Y";
		HumanPlayer human = new HumanPlayer();
		ComputerPlayer computer = new ComputerPlayer();		
		System.out.println("Please play tic tac toe with me.");
		System.out.println("I will go first.");
		System.out.println();
		while(playAgain){

			//Create new board for each game
			TicTacToeBoard board = new TicTacToeBoard();
			if (computerFirst==false){
				board.print(); //only prints blank board when human starts
				human.makeMove(board);
				humanTurn=false;	
			}
			//Computer starts game 1
			//Computer and human alternate first move after game 1
			//tic tac toe board is printed after each move
			if (computerFirst)	{			
				computer.makeMove(board);
				humanTurn=true;
			}
			board.print();
			while (!board.gameIsOver()){
				if (humanTurn) human.makeMove(board);
				else {
					System.out.println("Now it's my turn.\n");
					computer.makeMove(board);
				}				
				humanTurn=!humanTurn; 
				board.print();
				System.out.println();
			}
			//Keeps a count of games won, lost, and tied by the human player
			if(board.humanHasWon()) won++;
			else if(board.computerHasWon()) lost++;
			else tie++;
			System.out.println("\nGames won: " + won + 
							  "\nGames lost: " + lost + 
							  "\nGames tied: " + tie + "\n") ;			
			playAgain=human.ask(question);
			computerFirst=!computerFirst;  //reverses starting order		
			
		}
	}
}

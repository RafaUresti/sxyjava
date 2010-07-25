public class TicTacToeBoard {
	char[][] board;

	/**
	 * Constructs a new tic-tac-toe board, with
	 * all spaces blank.
	 */
	TicTacToeBoard() {
		board = new char[][] {{' ', ' ', ' '},
				{' ', ' ', ' '},
				{' ', ' ', ' '}};
	}

	/**
	 * Puts the designated character into the specified row
	 * and column of this tic-tac-toe board, where the rows and
	 * columns are numbered from 1 to 3. Some error checking
	 * is done.
	 * 
	 * @param row The row number (1..3).
	 * @param column The column number (1..3).
	 * @param ch The character ('X' or 'O') to put there.
	 */
	void set(int row, int column, char ch) {
		if (board[row - 1][column - 1] != ' ') {
			error("Row " + row + ", column " + column +
			" is already taken.");
		}
		else if (ch != 'X' && ch != 'O') {
			error("Illegal character: " + ch);
		}
		else {
			board[row - 1][column - 1] = ch;
		}
	}

	/**
	 * Returns the character ('X', 'O', or space) in the
	 * specified row and column of this tic-tac-toe board.
	 * 
	 * @param row The row number (1..3).
	 * @param column The column number (1..3).
	 * @return The character ('X', 'O', or space) in that location.
	 */
	char get(int row, int column) {
		return board[row - 1][column - 1];
	}

	/**
	 * Returns true if the indicated row and column in this
	 * tic-tac-toe board is available.
	 * 
	 * @param row The row number (1..3).
	 * @param column The column number (1..3).
	 * @return <code>true</code> if the location is available.
	 */
	boolean isEmpty(int row, int column) {
		return board[row - 1][column - 1] == ' ';
	}

	/**
	 * Prints this tic-tac-toe board.
	 */
	void print() {
		printThree(board[0][0], board[0][1], board[0][2]);
		printLine();
		printThree(board[1][0], board[1][1], board[1][2]);
		printLine();
		printThree(board[2][0], board[2][1], board[2][2]);
	}

	/**
	 * Prints three characters ('X', 'O', or space), with
	 * separators, in one row of this tic-tac-toe board.
	 * 
	 * @param ch1 The leftmost character in this row.
	 * @param ch2 The middle character in this row.
	 * @param ch3 The rightmost character in this row.
	 */
	private void printThree(char ch1, char ch2, char ch3) {
		System.out.println(" " + ch1 + " | " + ch2 + " | " + ch3);
	}

	/**
	 * Prints a horizontal line for this tic-tac-toe board.
	 */
	private void printLine() {
		System.out.println("---|---|---");
	}

	/**
	 * Prints the given error message.
	 * 
	 * @param errorMessage The message to print.
	 */
	private void error(String errorMessage) {
		System.out.println(errorMessage);
	}

	/**
	 * Determines if the game is over
	 * 
	 * @return gameOver boolean true ends game
	 */
	boolean gameIsOver() {
		boolean gameOver = false;
		if (computerHasWon() || humanHasWon()) {
			gameOver = true;
			if (computerHasWon()) {
				System.out.println("\nYou have lost!\n");
			}

			if (humanHasWon())
				System.out.println("\nYou have won!\n");
			return gameOver;
		} else {
			for (int i = 1; i <= 3; i++) {
				for (int j = 1; j <= 3; j++)
					if (isEmpty(i,j)){
						gameOver = false;
						return gameOver;
					}
			}
			gameOver = true;
			System.out.println("\nGame Tied!\n");
			return gameOver;
		}

	}

	/**
	 * Returns true if the computer has won
	 * 
	 * @return computerWon boolean true indicating computer has won
	 */
	boolean computerHasWon() {
		boolean computerWon = false;
		if (horizontallyWon('X') || verticallyWon('X') || diagonallyWon('X'))
			computerWon = true;
		return computerWon;
	}

	/**
	 * Returns true if the human has won
	 * 
	 * @return humanWon boolean true indicating human has won
	 */
	boolean humanHasWon() {
		boolean humanWon = false;
		if (horizontallyWon('O') || verticallyWon('O') || diagonallyWon('O'))
			humanWon = true;
		return humanWon;
	}

	/**
	 * Returns true if a row contains three identical char
	 * 
	 * @param ch the char 'X' or 'O'	
	 * @return horizontalWin boolean true if a row contains three identical char
	 */
	boolean horizontallyWon(char ch) {
		boolean horizontalWin = false;
		for (int i = 1; i <= board.length; i++) {
			if (get(i, 1) == get(i, 2) && get(i, 1) == get(i, 3)
					&& get(i, 1) == ch)
				horizontalWin = true;
		}
		return horizontalWin;
	}

	/**
	 * Returns true if a column contains three identical char
	 * 
	 * @param ch the char 'X' or 'O'	 
	 * @return verticalWin boolean true if a column contains 3 identical char	 
	 */
	boolean verticallyWon(char ch) {
		boolean verticalWin = false;
		for (int i = 1; i <= board.length; i++) {
			if (get(1, i) == get(2, i) && get(1, i) == get(3, i)
					&& get(1, i) == ch)
				verticalWin = true;
		}
		return verticalWin;
	}

	/**
	 * Returns true if a diagonal contains three identical char
	 * 
	 * @param ch the char 'X' or 'O'
	 * @return diagonalWin boolean true if a diagonal contains 3 identical char	
	 */
	boolean diagonallyWon(char ch) {
		boolean diagonalWin = false;
		if ((get(1, 1) == get(2, 2) && get(1, 1) == get(3, 3) && get(1, 1) == ch)
				|| (get(1, 3) == get(2, 2) && get(1, 3) == get(3, 1) 
				&& get(1, 3) == ch))
			diagonalWin = true;
		return diagonalWin;
	}
}
package cit003_TicTacToe;
public class ComputerPlayer {

	/**
	 * Controls computer playing logic.
	 * 
	 * Tries first to make center Move, then tries to make winning move,
	 * then tries to block the human player from winning,
	 * then tries a corner move, then edge move, then diagonal move.
	 * 
	 * @param TicTacToeBoard board      
	 */
	void makeMove(TicTacToeBoard board) {

		if (makeCenterMove(board)) {
			board.set(2, 2, 'X');
			return;
		}

		else if (makeWinningMove(board)){
			setWinningOrForcedMove(board, 'X');
			}

		else if (makeForcedMove(board)){
			setWinningOrForcedMove(board, 'O');
		}		

		else if (makeCornerMove(board)) {
			if (board.get(1, 1) == ' ')
				board.set(1, 1, 'X');
			else if (board.get(1, 3) == ' ')
				board.set(1, 3, 'X');
			else if (board.get(3, 1) == ' ')
				board.set(3, 1, 'X');
			else
				board.set(3, 3, 'X');
			return;
		}

		else if (makeEdgeMove(board)) {
			if (board.get(1, 2) == ' ')
				board.set(1, 2, 'X');
			else if (board.get(2, 1) == ' ')
				board.set(2, 1, 'X');
			else if (board.get(2, 3) == ' ')
				board.set(2, 3, 'X');
			else
				board.set(3, 2, 'X');
			return;
		}

	}

	/**
	 * Determines if center is occupied.
	 * 
	 * 
	 * @param TicTacToeBoard board   
	 * @return centerMove boolean true if center is ' '
	 */
	private boolean makeCenterMove(TicTacToeBoard board) {
		boolean centerMove = false;
		if (board.get(2, 2) != ' ')
			centerMove = false;
		else
			centerMove = true;
		return centerMove;

	}

	/**
	 * checks if single move will win game
	 * 
	 * 
	 * @param TicTacToeBoard board
	 * @return winningMove boolean true if single move will win
	 */
	private boolean makeWinningMove(TicTacToeBoard board) {
		boolean winningMove = false;
		if (checkWinningRow(board, 'X') || 
				checkWinningColumn(board,'X') || 
				checkWinningDiagonal(board,'X')){
			winningMove = true;
			return winningMove;
		}
		return winningMove;
	}

	/**
	 * Checks if computer needs to block human from winning.
	 * 
	 * 
	 * @param TicTacToeBoard board
	 * @return forcedMove boolean true if human needs to be blocked from winning
	 */
	private boolean makeForcedMove(TicTacToeBoard board) {
		boolean forcedMove = false;
		if (checkWinningRow(board, 'O') || 
				checkWinningColumn(board,'O') || 
				checkWinningDiagonal(board,'O')){
			forcedMove = true;
			return forcedMove;
		}
		return forcedMove;
	}

	/**
	 * Determines if a corner move is possible
	 * 
	 * 
	 * @param TicTacToeBoard board
	 * @return cornerMove boolean true if move is possible
	 */
	private boolean makeCornerMove(TicTacToeBoard board) {
		boolean cornerMove = false;
		if (board.get(1, 1) == ' ' || board.get(1, 3) == ' '
			|| board.get(3, 1) == ' ' || board.get(3, 3) == ' ')
			cornerMove = true;
		return cornerMove;

	}

	/**
	 * Determines whether an edge move is possible.
	 * 
	 * 
	 * @param TicTacToeBoard board
	 * @return edgeMove boolean true if move is possible
	 */
	private boolean makeEdgeMove(TicTacToeBoard board) {
		boolean edgeMove = false;
		if (board.get(1, 2) == ' ' || board.get(2, 1) == ' '
			|| board.get(2, 3) == ' ' || board.get(3, 2) == ' ')
			edgeMove = true;
		return edgeMove;
	}

	/**
	 * Determines whether a winning row exists.
	 * 
	 * 
	 * @param TicTacToeBoard board
	 * @param ch either 'X' or 'O'
	 * @return rowWinning boolean true if human or computer can win in 1 more move
	 */
	private boolean checkWinningRow(TicTacToeBoard board,char ch) {
		boolean rowWinning = false;		
		for (int i = 1; i <= 3; i++) {
			if (board.get(i, 1)+board.get(i,2)+board.get(i,3)==ch+ch+' '){
				rowWinning=true;
				return rowWinning;
			}
		}
		return rowWinning;
	}

	/**
	 * Determines whether a winning column exists.
	 * 
	 * 
	 * @param TicTacToeBoard board
	 * @param ch either 'X' or 'O'
	 * @return columnWinning boolean true if human or computer can win in 1 more move
	 */
	private boolean checkWinningColumn(TicTacToeBoard board,char ch) {
		boolean columnWinning = false;
		for (int j = 1; j <= 3; j++) {
			if (board.get(1, j)+board.get(2,j)+board.get(3,j)==ch+ch+' '){
				columnWinning=true;
				return columnWinning;
			}
		}
		return columnWinning;
	}

	/**
	 * Determines whether a winning diagonal exists.
	 * 
	 * 
	 * @param TicTacToeBoard board
	 * @param ch either 'X' or 'O'
	 * @return diagonalWinning boolean true if human or computer can win in 1 more move
	 */
	private boolean checkWinningDiagonal(TicTacToeBoard board,char ch) {
		boolean diagonalWinning = false;
		if (board.get(1,1)+board.get(2,2)+board.get(3,3)==ch+ch+' ' ||
				board.get(1,3)+board.get(2,2)+board.get(3,1)==ch+ch+' '){
			diagonalWinning=true;
			return diagonalWinning;
		}
		return diagonalWinning;
	}
	private void setWinningOrForcedMove(TicTacToeBoard board, char ch){
		int winningRow=0;
		int winningColumn=0;
		if (checkWinningRow(board, ch)){
			for (int i = 1; i <= 3; i++) {
				if (board.get(i,1)+board.get(i,2)+board.get(i,3)==ch+ch+' '){
					winningRow = i;
				}
			}
			for (int j = 1; j <= 3; j++){
				if (board.isEmpty(winningRow,j)){
					board.set(winningRow,j,'X');
					return;
				}
			}
		}
		else if(checkWinningColumn(board,ch)){
			for (int j = 1; j <= 3; j++){
				if (board.get(1,j)+board.get(2,j)+board.get(3,j)==ch+ch+' '){
					winningColumn = j;
				}
			}
			for (int i = 1; i <= 3; i++){
				if (board.isEmpty(i,winningColumn)){
					board.set(i,winningColumn,'X');
					return;
				}
			}
		}
			else{ //else must be diagonally winning
				if (board.get(1,1)==ch && board.isEmpty(3,3)) board.set(3,3,'X');
				if (board.get(1,3)==ch && board.isEmpty(3,1)) board.set(3,1,'X');
				if (board.get(3,3)==ch && board.isEmpty(1,1)) board.set(1,1,'X');
				if (board.get(3,1)==ch && board.isEmpty(1,3)) board.set(1,3,'X');
			}
		}

}

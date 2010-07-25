public class Sudoku {

	int[][] sudokuSolution= new int[9][9];
	public Sudoku (int [][] problem) {
		for (int i=0;i<=8;i++)
			for (int j=0;j<=8;j++){
				sudokuSolution[i][j]=problem[i][j];
			}
	}

	public int[][] solve() {
		int digit;//the last digit put into a cell
		do{	
			digit=0;
			for (int i=0;i<9;i++)
				for (int j=0;j<9;j++) {
					if (sudokuSolution[i][j]==0 && getOnlyPossibleDigit(i, j)!=0) {
						sudokuSolution[i][j]=getOnlyPossibleDigit(i, j);
						digit=sudokuSolution[i][j];
					}
				}
		}while(digit!=0);
		return sudokuSolution;
	}

	int[][] getBox(int boxRow, int boxColumn) {
		int[][] box = new int [3][3];
		for (int i= 0; i < 3; i++) {
			for(int j=0; j < 3; j++) {
				box[i][j]= sudokuSolution[boxRow*3+i][boxColumn*3+j];
			}
		}
		return box;
	}

	boolean occursInRow(int digit, int row){

		for (int j=0; j<9; j++){
			if (digit == sudokuSolution[row][j])
				return true;
		}
		return false;
	}
////	Returns true if the given digit (which must be 1..9) occurs in the
//	given row (rows are 0..8),
////	and false otherwise.

	boolean occursInColumn(int digit, int column){
		for (int i=0; i<9; i++){
			if (digit == sudokuSolution[i][column])
				return true;
		}
		return false;
	}
////	Returns true if the given digit (which must be 1..9) occurs in the
//	given column (columns are 0..8),
////	and false otherwise.

	boolean occursInBox(int digit, int row, int column){
		int boxRow=row/3;
		int boxColumn=column/3;
		int[][] box= getBox(boxRow,boxColumn);
		for (int i=0;i<3;i++){
			for (int j=0;j<3;j++){
				if (box[i][j]==digit)
					return true;
			}
		}
		return false;
	}
////	Returns true if the given digit (which must be 1..9)
////	occurs in the box containing the location at the given row and
//	column of the 9x9 array,
////	and false otherwise. Note that this method is given a row and
//	column in the complete 9x9 array,
////	but must search for the given digit in the box containing that
//	(row, column) location.

	boolean occursInBox(int digit, int[][] box){
		for (int i=0; i < 3; i++){
			for (int j=0; j < 3; j++){
				if (digit == box[i][j]){
					return true;
				}
			}
		}
		return false;
	}
////	Returns true if the given digit (which must be 1..9) occurs in the
//	given 3x3 box, and false otherwise.

	boolean isPossibleDigit(int digit, int row, int column){
		if (!(occursInBox(digit,row, column)) && !(occursInColumn(digit,
column)) && !(occursInRow(digit,row)))
			return true;
		else return false;

	}


////	Returns true if the given digit (which must be 1..9) does not occur
//	in the given row,
////	or in the given column, or in the box containing this  row and
//	column, and false otherwise.
////	That is, this digit is a possible candidate for putting in this
//	location; there may be other candidates.

	boolean isOnlyPossibleDigit(int digit, int row, int column){
		if (!isPossibleDigit(digit,row,column))
			return false;
		else {
			for (int altDig=1;altDig<=9;altDig++){
				if (isPossibleDigit(altDig,row,column)&& altDig!=digit)
					return false;
			}
		}
		return true;
	}
////	Returns true if the given digit can be put in the given row and column,
////	and no other digit can be put there; returns false otherwise..

	int getOnlyPossibleDigit(int row, int column){
		for (int digit=1;digit<=9;digit++){
			if (isOnlyPossibleDigit(digit,row,column))
					return digit;
		}
		return 0;
	}
////	If the rules of Sudoku allow only one particular digit to be placed
//	in the given (row, column) location,
////	return that digit, else return zero.

//	}


	boolean isDigit (int digit){
		while (digit < 0 && digit > 9){
			return false;
		} return true;
	}

}

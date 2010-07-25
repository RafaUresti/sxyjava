/**
 * 
 * 
 */
public class Sudoku {

	int[][] sudokuSolution= new int[9][9];

	public Sudoku (int [][] problem) {
		for (int i=0;i<=8;i++)
			for (int j=0;j<=8;j++){
				sudokuSolution[i][j]=problem[i][j];  // copies values in problem array into solution array
			}
	}

	/**Scans entire array for cells with only one possible digit 
	 * as solution and places that number in cell
	 */
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

	/** Divides up the 9x9 array into nine 3x3 arrays
	 * each with unique identifier
	 * @param int boxRow
	 * @param int boxColumn
	 * Return 3x3 box
	 */
	int[][] getBox(int boxRow, int boxColumn) {
		int[][] box = new int [3][3];
		for (int i= 0; i < 3; i++) {
			for(int j=0; j < 3; j++) {
				box[i][j]= sudokuSolution[boxRow*3+i][boxColumn*3+j];
			}
		}
		return box;
	}

	/**Checks to see if digit that is being tested already occurs in given row
	 * Return true if digit occurs in row, return false otherwise
	 * @param int digit
	 * @param int row
	 */
	boolean occursInRow(int digit, int row){

		for (int j=0; j<9; j++){
			if (digit == sudokuSolution[row][j])
				return true;
		}
		return false;
	}

	/**Checks to see if digit that is being tested already occurs in given column
	 * Return true if digit already occurs, Return false otherwise
	 * @param int digit
	 * @param int column
	 */
	boolean occursInColumn(int digit, int column){
		for (int i=0; i<9; i++){
			if (digit == sudokuSolution[i][column])
				return true;
		}
		return false;
	}

	/**Checks if digit that is being tested occurs in the corresponding 3x3 array
	 * @param int digit
	 * @param int row
	 * @param int column
	 * Return true if digit occurs in box, false if otherwise
	 */
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


	/**Checks to see if digit occurs within 3x3 array
	 * @param int digit
	 * @param int[][] box
	 * Return true if occurs in 3x3 array, false if otherwise
	 */
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

	/**Checks to see if digit being tested does not occur in the given box, 
	 * given column, or given row
	 * @param int digit
	 * @param int row
	 * @param int column
	 * Return true if digit does not occur, false if otherwise
	 */
	boolean isPossibleDigit(int digit, int row, int column){
		if (!(occursInBox(digit,row, column)) && !(occursInColumn(digit,
				column)) && !(occursInRow(digit,row)))
			return true;
		else return false;

	}



	/**Checks to see if digit being tested is the only possible digit
	 * and if not then checks to see if any other candidates
	 * @param int digit
	 * @param int row
	 * @param true if given digit can be put in given row and column, 
	 * false if not possible digit
	 */
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


	/**Calculates only possible digit that can be placed in given row and column
	 * @param int row
	 * @param int column
	 * return digit if only digit is found, return 0 if none if found
	 */
	int getOnlyPossibleDigit(int row, int column){
		for (int digit=1;digit<=9;digit++){
			if (isOnlyPossibleDigit(digit,row,column))
				return digit;
		}
		return 0;
	}


	/**Ensures digit is a valid value from ranges 1 to 9
	 * @param int digit
	 * return true if digit is a valid digit, false if otherwise
	 */
	boolean isDigit (int digit){
		while (digit < 0 && digit > 9){
			return false;
		} return true;
	}

}

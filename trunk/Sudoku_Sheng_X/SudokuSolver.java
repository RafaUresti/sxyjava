/**
 * Assignment 4 Sudoku for CIT591, Fall 2007.
 * @author Dave Matuszek
 * @author Brenda Lin
 * @author Xiaoyi Sheng
 * @version October, 2, 2007
 */
public class SudokuSolver {


	/** Initialize a new Sudoku matrix and stores given values of the puzzle into cells
	 * Create a solution matrix to puzzle that will hold answer values
	 * @param int[][] problem (Sudoku matrix)  
	 */
	int[][] problem	= new int[][]{  { 0, 0, 4,   0, 0, 0,   0, 6, 7 },
									{ 3, 0, 0,   4, 7, 0,   0, 0, 5 },
									{ 1, 5, 0,   8, 2, 0,   0, 0, 3 },
									{ 0, 0, 6,   0, 0, 0,   0, 3, 1 },
									{ 8, 0, 2,   1, 0, 5,   6, 0, 4 },
									{ 4, 1, 0,   0, 0, 0,   9, 0, 0 },
									{ 7, 0, 0,   0, 8, 0,   0, 4, 6 },
									{ 6, 0, 0,   0, 1, 2,   0, 0, 0 },
									{ 9, 3, 0,   0, 0, 0,   7, 1, 0 } };
	Sudoku solution=new Sudoku(problem);

	/**Tells program to run print method under SudokuSolver class (i.e. print solution array)
	 * @param args
	 */
	public static void main(String[] args) {
		new SudokuSolver().print();
	}

	/**
	 * Prints the 9X9 array
	 */
	private void print() {
		int[][] sudokuSolution=solution.solve();
		System.out.println("+-------+-------+-------+");
		for (int boxRow=0;boxRow<=2;boxRow++){//there are 3 boxes, each has 3 rows, numbered 0 to 2)
			for (int i=boxRow*3;i<=boxRow*3+2;i++){//3 rows
				for (int boxColumn=0;boxColumn<=2;boxColumn++){
					System.out.print("| ");
					for (int j=boxColumn*3;j<=boxColumn*3+2;j++){
						if(sudokuSolution[i][j]==0)
							System.out.print('.'+" ");
						else System.out.print(sudokuSolution[i][j]+" ");
					}
				}
				System.out.println("|");
			}
			System.out.println("+-------+-------+-------+");
		}
	}


}

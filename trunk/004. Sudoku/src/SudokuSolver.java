/**
 * Assignment 4 Sudoku for CIT591, Fall 2007.
 * @author Dave Matuszek
 * @author Brenda Lin
 * @author sxycode
 * @version Sep 28, 2007
 */
public class SudokuSolver {
	/**
	 * @param args
	 */

	int[][] problem	= new int[][]{  { 1, 6, 3,   0, 4, 0,   9, 0, 0 },
			{ 0, 0, 0,   3, 0, 0,   0, 4, 6 },
			{ 0, 7, 0,   2, 0, 0,   5, 1, 3 },

			{ 0, 0, 0,   9, 8, 0,   1, 0, 0 },
			{ 3, 1, 6,   4, 0, 5,   2, 9, 8 },
			{ 0, 0, 4,   0, 2, 1,   0, 0, 0 },

			{ 2, 8, 5,   0, 0, 4,   0, 7, 0 },
			{ 9, 4, 0,   0, 0, 6,   0, 0, 0 },
			{ 0, 0, 7,   0, 9, 0,   4, 5, 1 } };
	Sudoku solution=new Sudoku(problem);
	
	public static void main(String[] args) {
	new SudokuSolver().print();
	}
	
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

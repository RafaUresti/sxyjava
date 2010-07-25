import java.util.Random;
public class Ocean {
/**
 * Initializes the ocean, places the ships randomly, 
 * updates the status of the ocean after every shot
 * counts the number of shots and hits
 */
	private int numberOfShots;
	private int numberOfHits;
	private char[][] sea = new char[10][10];
	/**
	 * The constructor initializes the sea array and the variables numberOfShots and numberOfHits 
	 */
	public Ocean(){
		for (int i=0; i< sea.length; i++) {
			for (int j=0; j< sea[0].length; j++)
				sea[i][j]=' '; // generate the empty sea
			numberOfShots=0;
			numberOfHits=0;
		}
	}
/**
 * Places 1 battleship, 2 cruisers, 3 destroyers and 4 submarines randomly 
 * under the condition that no two ships are placed adjacent
 * to each other either horizontally or vertically or diagonally. 
 */
	void placeAllShipsRandomly() {
		Random random= new Random();
		int row;
		int column;
		boolean isHorizontal;
		do {
			row = random.nextInt(sea.length);
			column = random.nextInt(sea[0].length);
			isHorizontal = random.nextBoolean();
		}while (!okToPlaceBattleshipAt(row, column, isHorizontal));
		placeBattleshipAt(row, column, isHorizontal);
		
		for (int i=0; i<2; i++) {
			do {
				row = random.nextInt(sea.length);
				column = random.nextInt(sea[0].length);
				isHorizontal = random.nextBoolean();
			}while (!okToPlaceCruiserAt(row, column, isHorizontal));
			placeCruiserAt(row, column, isHorizontal);
		}
		
		for (int i=0; i<3; i++) {
			do {
				row = random.nextInt(sea.length);
				column = random.nextInt(sea[0].length);
				isHorizontal = random.nextBoolean();
			}while (!okToPlaceDestroyerAt(row, column, isHorizontal));
			placeDestroyerAt(row, column, isHorizontal);
		}
		
		for (int i=0; i<4; i++) {
			do {
				row = random.nextInt(sea.length);
				column = random.nextInt(sea[0].length);
			}while (!okToPlaceSubmarineAt(row, column));
			placeSubmarineAt(row, column);
		}
	}

	/**
	 * Checks if a battleship can be placed in the given location
	 * @param row The row number of the leftmost/topmost position of the battleship
	 * @param column The column number of the leftmost/topmost position of the battleship 
	 * @param isHorizontal Is true if the battleship is to be placed horizontally and false otherwise
	 * @return Returns true if a battleship can be placed in the given location and false otherwise
	 */
	boolean okToPlaceBattleshipAt(int row, int column, boolean isHorizontal) {
		return okToPlaceShipAt(row, column, isHorizontal, 4);
	}

	/**
	 * Places the battleship at the given location
	 * @param row The row number at which the battleship should be placed
	 * @param column The column number at which the battleship should be placed
	 * @param isHorizontal Places the battleship horizontally if true and vertically otherwise
	 */
	void placeBattleshipAt(int row, int column, boolean isHorizontal) {
		placeShipAt(row, column, isHorizontal, 4);
	}
	/**
	 * Checks if a cruiser can be placed in the given location
	 * @param row The row number of the leftmost/topmost position of the cruiser
	 * @param column The column number of the leftmost/topmost position of the cruiser
	 * @param isHorizontal Is true if the cruiser is to be placed horizontally and false otherwise
	 * @return Returns true if a cruiser can be placed in the given location and false otherwise
	 */
	boolean okToPlaceCruiserAt(int row, int column, boolean isHorizontal) {
		return okToPlaceShipAt(row, column, isHorizontal, 3);
	}

	/**
	 * Places the cruiser at the given location
	 * @param row The row number at which the cruiser should be placed
	 * @param column The column number at which the cruiser should be placed
	 * @param isHorizontal Places the cruiser horizontally if true and vertically otherwise
	 */
	void placeCruiserAt(int row, int column, boolean isHorizontal) {
		placeShipAt(row, column, isHorizontal, 3);
	}
	/**
	 * Checks if a destroyer can be placed in the given location
	 * @param row The row number of the leftmost/topmost position of the destroyer
	 * @param column The column number of the leftmost/topmost position of the destroyer 
	 * @param isHorizontal Is true if the destroyer is to be placed horizontally and false otherwise
	 * @return Returns true if a destroyer can be placed in the given location and false otherwise
	 */
	boolean okToPlaceDestroyerAt(int row, int column, boolean isHorizontal) {
		return okToPlaceShipAt(row, column, isHorizontal, 2);
	}

	/**
	 * Places the destroyer at the given location
	 * @param row The row number at which the destroyer should be placed
	 * @param column The column number at which the destroyer should be placed
	 * @param isHorizontal Places the destroyer horizontally if true and vertically otherwise
	 */
	void placeDestroyerAt(int row, int column, boolean isHorizontal) {
		placeShipAt(row, column, isHorizontal, 2);
	}
	/**
	 * Checks if a submarine can be placed in the given location
	 * @param row The row number of the submarine
	 * @param column The column number of the submarine 
	 * @return Returns true if a submarine can be placed in the given location and false otherwise
	 */
	boolean okToPlaceSubmarineAt(int row, int column) {
		return sea[row][column]== ' ';
	}

	/**
	 * Places the submarine at the given location
	 * @param row The row number at which the submarine should be placed
	 * @param column The column number at which the submarine should be placed
	 */
	void placeSubmarineAt(int row, int column) {
		sea[row][column]= 'S';
		generateAdjacentSpace();
		//fill the adjacent space with 'O' to prevent 
		//other submarines getting placed adjacently
	}
    
	/**
	 * Checks if a given location is occupied by a ship
	 * @param row The row number of the location
	 * @param column The column number of the location
	 * @return Returns true if the location is occupied by the ship and false otherwise
	 */ 
	boolean isOccupied(int row, int column) {
		return (sea[row][column]=='S');	
	}

	/**
	 * Updates the number of shots and hits 
	 * Changes the status of the location that was shot 
	 * Checks if the shot is a hit
	 * @param row The row number of the location that was shot
	 * @param column The column number of the location that was shot
	 * @return Returns true if its a hit and false otherwise
	 */
	boolean shootAt(int row, int column) {
		++numberOfShots;
		if (isOccupied(row,column) || sea[row][column]== 'X'){
			++numberOfHits;
			sea[row][column]='X';
			return true;
		}
		else {
			sea[row][column]='o';
			return false;
		}
		
	}
  
	/**
	 * 
	 * @return Returns the number of shots fired
	 */
	int numberOfShotsFired() {
		return numberOfShots;
	}
	
	/**
	 * 
	 * @return Returns the number of Hits made
	 */
	int numberOfHits() {
		return numberOfHits;
	}

	/**
	 * Checks if the game is over
	 * @return Returns true if the game is over and false otherwise
	 */
	boolean isGameOver() {
		for (int i=0; i<sea.length; i++){
			for (int j=0; j<sea[0].length; j++){
				if (sea[i][j]=='S')
					return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a ship can be placed in the given location
	 * @param row The leftmost/topmost row number of the ship
	 * @param column The leftmost/topmost column number of the ship 
	 * @param isHorizontal Is true if the ship is to be placed horizontally and false otherwise
	 * @param shipSize Is 1 for submarine, 2 for destroyer, 3 for cruiser, 4 battleship
	 * @return Returns true if a ship can be placed in the given location and false otherwise
	 */
	boolean okToPlaceShipAt(int row, int column, boolean isHorizontal, int shipSize){
		if (isHorizontal) {
			for (int j= column; j< column + shipSize; j++) {
				if (j>sea[0].length-1 || sea[row][j]!=' ')
					return false;
			}
			return true;
		}
		else {
			for (int i= row; i< row + shipSize; i++) {
				if (i>sea.length-1 || sea[i][column]!=' ')
					return false;

			}
			return true;
		}
	}

	/**
	 * Places the ship at the given location
	 * @param row The row number at which the ship should be placed
	 * @param column The column number at which the ship should be placed
	 * @param isHorizontal Places the ship horizontally if true and vertically otherwise
	 * @param shipSize Is 1 for submarine, 2 for destroyer, 3 for cruiser, 4 battleship
	 */
	void placeShipAt(int row, int column, boolean isHorizontal, int shipSize) {
		if (isHorizontal) {
			for (int j = column; j< column + shipSize; j++)
				sea[row][j]= 'S';
		}
		else {
			for (int i = row; i< row + shipSize; i++) 
				sea[i][column]= 'S';
		}
		generateAdjacentSpace();
		//fill the adjacent space with 'O' to prevent 
		//other ships getting placed adjacently
	}
	
	/**
	 * Marks adjacent cells to the ships placed on the ocean
	 */
	private void generateAdjacentSpace() {
		clearAdjacentSpaces(); //clears all previous adjacent space markers
		for (int i = 0; i < sea.length; i++) {
			for (int j = 0; j < sea[0].length; j++) {
				if (sea[i][j] == 'S') {
					makeAdjacentSpace(i - 1, j - 1);
					makeAdjacentSpace(i - 1, j);
					makeAdjacentSpace(i - 1, j + 1);
					makeAdjacentSpace(i, j - 1);
					makeAdjacentSpace(i, j + 1);
					makeAdjacentSpace(i + 1, j - 1);
					makeAdjacentSpace(i + 1, j);
					makeAdjacentSpace(i + 1, j + 1);
				}
			}
		}
	}
	
	/**
	 * Clears all adjacent space markers placed in the ocean.
	 */
	private void clearAdjacentSpaces() {
		for (int i = 0; i < sea.length; i++) {
			for (int j = 0; j < sea[0].length; j++) {
				if (sea[i][j] == 'O') {
					sea[i][j] = ' ';
				}
			}
		}
	}
	
	/**
	 * Marks the given location as adjacent to the ship
	 * @param row The row number of the location
	 * @param column The column number of the location
	 */
	private void makeAdjacentSpace(int row, int column){
		if (row<0 || row >sea.length-1 || column <0 || column > sea[0].length-1)
			return;
		else if (sea[row][column]==' ')
			sea[row][column]='O';
	}
	
	/**
	 * Removes the ship that occupies the given row/column
	 * @param row 
	 * @param column
	 * @return returns a String containing the type of ship that was removed.
	 */
	public String removeShipAt(int row, int column) {

		int shipSize = 0;
		String shipType = "";
		
		
		
		for (int i=row;i>=0;i--) {
			if (isOccupied(i, column)) {
				sea[i][column] = ' ';
				shipSize +=1;
			} else break;
		}
		
		for (int i=row+1;i<10;i++) {
			if (isOccupied(i, column)) {
				sea[i][column] = ' ';
				shipSize +=1;
			} else break;
		}
		
		for (int j=column-1;j>=0;j--) {
			if (isOccupied(row, j)) {
				sea[row][j] = ' ';
				shipSize +=1;
			} else break;
		}
		for (int j=column+1;j<10;j++) {
			if (isOccupied(row, j)) {
				sea[row][j] = ' ';
				shipSize +=1;
			} else break;
		}
		
		switch (shipSize) {
		case 1:
			shipType = "Submarine";
			break;
		case 2:
			shipType = "Destroyer";
			break;
		case 3:
			shipType = "Cruiser";
			break;
		case 4:
			shipType = "Battleship";
			break;
		}	
		
		generateAdjacentSpace();
		return shipType;
	}
}

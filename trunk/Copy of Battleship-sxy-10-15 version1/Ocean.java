import java.util.Random;
public class Ocean {
	private char[][] sea = new char[10][10];
	public Ocean(){
		for (int i=0; i< 10; i++) {
			for (int j=0; j< 10; j++)
				sea[i][j]=' '; // generate the empty sea
		}
	}

	void placeAllShipsRandomly() {
		Random random= new Random();
		int row;
		int column;
		boolean isHorizontal;
		do {
			row = random.nextInt(10);
			column = random.nextInt(10);
			isHorizontal = random.nextBoolean();
		}while (!okToPlaceBattleshipAt(row, column, isHorizontal));
		placeBattleshipAt(row, column, isHorizontal);
		generateAdjacentSpace();
		for (int i=0; i<2; i++) {
			do {
				row = random.nextInt(10);
				column = random.nextInt(10);
				isHorizontal = random.nextBoolean();
			}while (!okToPlaceCruiserAt(row, column, isHorizontal));
			placeCruiserAt(row, column, isHorizontal);
			generateAdjacentSpace();
		}
		for (int i=0; i<3; i++) {
			do {
				row = random.nextInt(10);
				column = random.nextInt(10);
				isHorizontal = random.nextBoolean();
			}while (!okToPlaceDestroyerAt(row, column, isHorizontal));
			placeDestroyerAt(row, column, isHorizontal);
			generateAdjacentSpace();
		}
		for (int i=0; i<4; i++) {
			do {
				row = random.nextInt(10);
				column = random.nextInt(10);
			}while (!okToPlaceSubmarineAt(row, column));
			placeSubmarineAt(row, column);
		}
	}
	boolean okToPlaceBattleshipAt(int row, int column, boolean isHorizontal) {
		return okToPlaceShipAt(row, column, isHorizontal, 4);
	}
	void placeBattleshipAt(int row, int column, boolean isHorizontal) {
		placeShipAt(row, column, isHorizontal, 4);
	}
	boolean okToPlaceCruiserAt(int row, int column, boolean isHorizontal) {
		return okToPlaceShipAt(row, column, isHorizontal, 3);
	}
	void placeCruiserAt(int row, int column, boolean isHorizontal) {
		placeShipAt(row, column, isHorizontal, 3);
	}
	boolean okToPlaceDestroyerAt(int row, int column, boolean isHorizontal) {
		return okToPlaceShipAt(row, column, isHorizontal, 2);
	}
	void placeDestroyerAt(int row, int column, boolean isHorizontal) {
		placeShipAt(row, column, isHorizontal, 2);
	}
	boolean okToPlaceSubmarineAt(int row, int column) {
		return sea[row][column]== ' ';
	}
	void placeSubmarineAt(int row, int column) {
		sea[row][column]= 'S';
	}
	boolean isOccupied(int row, int column) {
		return sea[row][column]=='S';	
	}
	boolean shootAt(int row, int column) {
		return false;
	}
	int numberOfShotsFired() {
		return 0;
	}
	int numberOfHits() {
		return 0;
	}
	boolean isGameOver() {
		return false;
	}
	void print() {
		for (int i = 0; i<10; i++)
			System.out.print("   "+i);//prints the column numbers
		System.out.println();
		printRowDivider();
		for (int k=0; k<10; k++) {
			System.out.print(k +"|");
			printRow();
			printRowDivider();
		}
	}
	void printRowDivider() {
		System.out.print(" +");
		for (int i=0; i<10; i++)
			System.out.print("---+");
		System.out.println();
	}
	void printRow() {
		for (int i=0; i<10; i++) 
			System.out.print("   |");
		System.out.println();
	}

	void generateAdjacentSpace() {
		for (int i = 0; i < 10; i++) {
			for (int j=0; j < 10; j++) {
				if sea
			}
		}
	}
	boolean okToPlaceShipAt(int row, int column, boolean isHorizontal, int shipSize){
		if (isHorizontal) {
			for (int i= row; i< row + shipSize; i++) {
				if (i>9 || sea[i][column]!= ' ')
					return false;
			}
			return true;
		}
		else {
			for (int j= column; j< column + shipSize; j++) {
				if (j>9 || sea[row][j]!= ' ')
					return false;
			}
			return true;
		}
	}
	void placeShipAt(int row, int column, boolean isHorizontal, int shipSize) {
		if (isHorizontal) {
			for (int i = row; i< row + 4; i++) 
				sea[i][column]= 'S';
		}
		else {
			for (int j = column; j< column + 4; j++)
				sea[row][j]= 'S';
		}
	}
}

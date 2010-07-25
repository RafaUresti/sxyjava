import junit.framework.TestCase;


public class OceanTest extends TestCase {
	Ocean ocean = new Ocean();

	public void testOcean() {
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				assertFalse(ocean.isOccupied(i, j));
			}
		}
		assertEquals(0, ocean.numberOfShotsFired());
		assertEquals(0,ocean.numberOfHits());
	}

	public void testPlaceAllShipsRandomly() {
		ocean.placeAllShipsRandomly();
		int count =0;
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				if(ocean.isOccupied(i,j)) ++count;
			}
		}
		assertEquals( 20, count);
	}

	public void testOkToPlaceBattleshipAt() {
		assertFalse(ocean.okToPlaceBattleshipAt(0, 9, true));
		assertTrue(ocean.okToPlaceBattleshipAt(0, 9, false));
		assertTrue(ocean.okToPlaceBattleshipAt(9, 0, true));
		assertFalse(ocean.okToPlaceBattleshipAt(9, 0, false));
		assertFalse(ocean.okToPlaceBattleshipAt(9, 9, true));
		assertFalse(ocean.okToPlaceBattleshipAt(9, 9, false));
		assertTrue(ocean.okToPlaceBattleshipAt(2, 5, true));
		assertTrue(ocean.okToPlaceBattleshipAt(2,5,false));
	}

	public void testPlaceBattleshipAt() {

		for(int i=0; i<10; i++){
			for(int j=0; j<7; j++){
				ocean.placeBattleshipAt(i, j, true);
				for(int m=j; m< j+4; m++)
					assertTrue(ocean.isOccupied(i,m));
			}
		}
		for(int i=0; i<7; i++){
			for(int j=0; j<10; j++){
				ocean.placeBattleshipAt(i, j, false);
				for(int n=i; n< i+4; n++)
					assertTrue(ocean.isOccupied(n,j));
			}
		}

	}

	public void testOkToPlaceCruiserAt() {
		assertFalse(ocean.okToPlaceCruiserAt(0, 9, true));
		assertTrue(ocean.okToPlaceCruiserAt(0, 9, false));
		assertTrue(ocean.okToPlaceCruiserAt(9, 0, true));
		assertFalse(ocean.okToPlaceCruiserAt(9, 0, false));
		assertFalse(ocean.okToPlaceCruiserAt(9, 9, true));
		assertFalse(ocean.okToPlaceCruiserAt(9, 9, false));
		assertTrue(ocean.okToPlaceCruiserAt(2, 5, true));
		assertTrue(ocean.okToPlaceCruiserAt(2,5,false)); 
		ocean.placeBattleshipAt(2,2, true);
		assertFalse(ocean.okToPlaceCruiserAt(2, 3, false));
		assertFalse(ocean.okToPlaceCruiserAt(3, 4, true));
		assertTrue(ocean.okToPlaceCruiserAt(2, 8, false));
		assertTrue(ocean.okToPlaceCruiserAt(2, 7, true));
		ocean.placeCruiserAt(4, 3, false);
		assertFalse(ocean.okToPlaceCruiserAt(4, 3, true));
		assertFalse(ocean.okToPlaceCruiserAt(6, 3, false));
		assertFalse(ocean.okToPlaceCruiserAt(7, 4, true));
		assertFalse(ocean.okToPlaceCruiserAt(6, 2, false));
		assertFalse(ocean.okToPlaceCruiserAt(2, 6, true)); 
		assertTrue(ocean.okToPlaceCruiserAt(2, 8, false));
	}

	public void testPlaceCruiserAt() {
		for(int i=0; i<10; i++){
			for(int j=0; j<8; j++){
				ocean.placeCruiserAt(i, j, true);
				for(int m=j; m< j+3; m++)
					assertTrue(ocean.isOccupied(i,m));
			}
		}
		for(int i=0; i<8; i++){
			for(int j=0; j<10; j++){
				ocean.placeCruiserAt(i, j, false);
				for(int n=i; n< i+3; n++)
					assertTrue(ocean.isOccupied(n,j));
			}
		}
	}

	public void testOkToPlaceDestroyerAt() {
		assertFalse(ocean.okToPlaceDestroyerAt(0, 9, true));
		assertTrue(ocean.okToPlaceDestroyerAt(0, 9, false));
		assertTrue(ocean.okToPlaceDestroyerAt(9, 0, true));
		assertFalse(ocean.okToPlaceDestroyerAt(9, 0, false));
		assertFalse(ocean.okToPlaceDestroyerAt(9, 9, true));
		assertFalse(ocean.okToPlaceDestroyerAt(9, 9, false));
		assertTrue(ocean.okToPlaceDestroyerAt(2, 5, true));
		assertTrue(ocean.okToPlaceDestroyerAt(2,5,false)); 
		ocean.placeBattleshipAt(2,2, true);
		assertFalse(ocean.okToPlaceDestroyerAt(2, 3, false));
		assertFalse(ocean.okToPlaceDestroyerAt(3, 4, true));
		assertTrue(ocean.okToPlaceDestroyerAt(2, 8, false));
		assertTrue(ocean.okToPlaceDestroyerAt(2, 7, true));
		ocean.placeCruiserAt(4, 3, false);
		assertFalse(ocean.okToPlaceDestroyerAt(4, 3, true));
		assertFalse(ocean.okToPlaceDestroyerAt(6, 3, false));
		assertFalse(ocean.okToPlaceDestroyerAt(7, 4, true));
		assertFalse(ocean.okToPlaceDestroyerAt(6, 2, false));
		assertFalse(ocean.okToPlaceDestroyerAt(2, 6, true)); 
		assertTrue(ocean.okToPlaceDestroyerAt(2, 8, false));
		ocean.placeCruiserAt(5, 7, true);
		assertFalse(ocean.okToPlaceDestroyerAt(5, 7, false));
		assertFalse(ocean.okToPlaceDestroyerAt(2, 5, false));
		assertFalse(ocean.okToPlaceDestroyerAt(6, 2, true));
		assertFalse(ocean.okToPlaceDestroyerAt(3, 5, false));
		assertTrue(ocean.okToPlaceDestroyerAt(0, 0, true));
		ocean.placeDestroyerAt(0, 0, true);
		assertFalse(ocean.okToPlaceDestroyerAt(1, 2, true));
		assertFalse(ocean.okToPlaceDestroyerAt(0, 1, false));
		assertTrue(ocean.okToPlaceDestroyerAt(6, 1, false));
		ocean.placeDestroyerAt(6, 1, false);
		assertFalse(ocean.okToPlaceDestroyerAt(7, 1, true));
		assertFalse(ocean.okToPlaceDestroyerAt(8, 2, true));
		assertTrue(ocean.okToPlaceDestroyerAt(8, 4, true));
	}

	public void testPlaceDestroyerAt() {
		for(int i=0; i<10; i++){
			for(int j=0; j<9; j++){
				ocean.placeDestroyerAt(i, j, true);
				for(int m=j; m< j+2; m++)
					assertTrue(ocean.isOccupied(i,m));
			}
		}
		for(int i=0; i<9; i++){
			for(int j=0; j<10; j++){
				ocean.placeDestroyerAt(i, j, false);
				for(int n=i; n< i+2; n++)
					assertTrue(ocean.isOccupied(n,j));
			}
		}
	}

	public void testOkToPlaceSubmarineAt() {
		ocean.placeBattleshipAt(2,2, true);
		ocean.placeCruiserAt(4, 3, false);
		ocean.placeCruiserAt(5, 7, true);
		ocean.placeDestroyerAt(0, 0, true);
		ocean.placeDestroyerAt(6, 1, false);
		ocean.placeSubmarineAt(0, 8);
		assertFalse(ocean.okToPlaceSubmarineAt(1, 7));
		assertTrue(ocean.okToPlaceSubmarineAt(0, 5));
		ocean.placeSubmarineAt(0,5);
		assertFalse(ocean.okToPlaceSubmarineAt(1,5));
		assertFalse(ocean.okToPlaceSubmarineAt(0, 5));
		assertTrue(ocean.okToPlaceSubmarineAt(4, 1));
		ocean.placeSubmarineAt(4, 1);
		assertFalse(ocean.okToPlaceSubmarineAt(5, 0));
		assertTrue(ocean.okToPlaceSubmarineAt(9, 9));  
	}

	public void testPlaceSubmarineAt() {
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				ocean.placeSubmarineAt(i, j);
				for(int m=j; m< j+1; m++)
					assertTrue(ocean.isOccupied(i,m));
			}
		}
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				ocean.placeSubmarineAt(i, j);
				for(int n=i; n< i+1; n++)
					assertTrue(ocean.isOccupied(n,j));
			}
		}
	}

	public void testIsOccupied() {
		ocean.placeBattleshipAt(3, 3, true);
		assertTrue(ocean.isOccupied(3, 3));
		assertTrue(ocean.isOccupied(3, 5));
		assertFalse(ocean.isOccupied(9, 1));
		assertFalse(ocean.isOccupied(3, 7));
	}

	public void testShootAt() {
		ocean.placeBattleshipAt(6, 3, true);
		assertTrue(ocean.shootAt(6, 3));
		assertTrue(ocean.shootAt(6, 6));
		assertFalse(ocean.shootAt(7, 3));
		assertFalse(ocean.shootAt(6,7));
	}

	public void testNumberOfShotsFired() {
		testShootAt();
		assertEquals(4,ocean.numberOfShotsFired());
		testShootAt();
		assertEquals(8,ocean.numberOfShotsFired());
	}

	public void testNumberOfHits() {
		testShootAt();
		assertEquals(2,ocean.numberOfHits());
		testShootAt();
		assertEquals(4,ocean.numberOfHits());
	}

	public void testIsGameOver() {
		ocean.placeAllShipsRandomly();
		assertFalse(ocean.isGameOver());
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				if(ocean.isOccupied(i,j)) ocean.shootAt(i,j);
			}
		}
		assertTrue(ocean.isGameOver());
	}

	public void testOkToPlaceShipAt() {
		testOkToPlaceBattleshipAt();
		testOkToPlaceCruiserAt();
		testPlaceDestroyerAt();
		testPlaceSubmarineAt();
	}

	public void testPlaceShipAt() {
		testPlaceBattleshipAt();
		testPlaceCruiserAt();
		testPlaceDestroyerAt();
		testPlaceSubmarineAt();
	}
	
	public void testRemoveShipAt() {
		ocean.placeBattleshipAt(5, 5, true);
		assertTrue(ocean.isOccupied(5,5));
		assertTrue(ocean.isOccupied(5,8));
		assertFalse(ocean.okToPlaceBattleshipAt(4, 5, true));
		ocean.removeShipAt(5,5);
		assertFalse(ocean.isOccupied(5,5));
		assertFalse(ocean.isOccupied(5,8));
		assertTrue(ocean.okToPlaceBattleshipAt(4, 5, true));
	}
}

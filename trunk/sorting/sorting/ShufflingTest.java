/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sorting;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Xiaoyi Sheng
 */
public class ShufflingTest {
    
    int[] array;
    int[][] counterArray;
    final int SIZE = 20;

    @Before
    public void setUp() {
        
        array = new int[SIZE];
        counterArray = new int[SIZE][SIZE];
    }

    /**
     * Test of initializeArray method, of class Shuffling.
     */
    @Test
    public void initializeArray() {
        System.out.println("initializeArray");
        Shuffling.initializeArray(array);
        assertEquals(array[0], 0);
        assertEquals(array[13], 13);
        assertEquals(array[7], 7);
        assertEquals(array[19], 19);
    }

    /**
     * Test of zeroCounterArray method, of class Shuffling.
     */
    @Test
    public void zeroCounterArray() {
        System.out.println("zeroCounterArray");
        Shuffling.zeroCounterArray(counterArray);
        assertEquals(counterArray[0][0], 0);
        assertEquals(counterArray[3][4], 0);
        assertEquals(counterArray[14][8], 0);
        assertEquals(counterArray[2][15], 0);
        assertEquals(counterArray[19][19], 0);
    }

    /**
     * Test of assessShuffle1 method, of class Shuffling.
     */
    @Test
    public void assessShuffle1() {
        System.out.println("assessShuffle1");
        Shuffling.assessShuffle1(array, counterArray);
        for (int i = 0; i < SIZE; i++)
            for(int j = 0; j < SIZE; j++){
                assertTrue(counterArray[i][j] > Shuffling.NUMBER/SIZE/2);
                assertTrue(counterArray[i][j] < Shuffling.NUMBER/SIZE*1.5);
            }
    }

    /**
     * Test of assessShuffle2 method, of class Shuffling.
     */
    @Test
    public void assessShuffle2() {
        System.out.println("assessShuffle2");
        Shuffling.assessShuffle2(array, counterArray);
        for (int i = 0; i < SIZE; i++)
            for(int j = 0; j < SIZE; j++){
                assertTrue(counterArray[i][j] > Shuffling.NUMBER/SIZE/2);
                assertTrue(counterArray[i][j] < Shuffling.NUMBER/SIZE*1.5);
            }
    }

    /**
     * Test of largestNumber method, of class Shuffling.
     */
    @Test
    public void largestNumber() {
        System.out.println("largestNumber");
        int[][] testArray = {{2,53},{34,766},{42,0},{988,-334},{-34, 98}};
        int expResult = 988;
        int result = Shuffling.largestNumber(testArray);
        assertEquals(expResult, result);
    }

    /**
     * Test of smallestNumber method, of class Shuffling.
     */
    @Test
    public void smallestNumber() {
        System.out.println("smallestNumber");
        int[][] testArray = {{2,53},{34,766},{42,0},{988,-334},{-34, 98}};
        int expResult = -334;
        int result = Shuffling.smallestNumber(testArray);
        assertEquals(expResult, result);
    }

}
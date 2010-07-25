package sorting;

/**
 * Provides and evaluates two array shuffling methods.
 * @author sxycode
 * @author David Matuszek
 * @version 1/28/2008
 */
public class Shuffling {

    static int[] array = new int[10];
    static int[][] counterArray = new int[10][10];
    static final int NUMBER = 10000;

    public static void main(String[] args) {
        assessShuffle1(array, counterArray);
        assessShuffle2(array, counterArray);

    }
/**
 * Initializes an array with element values equal to indices.
 * @param array The array to be initialized
 */
    public static void initializeArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
    }
/**
 * Initializes all elements of an array to value 0.
 * @param array The array to be initialized.
 */
    public static void zeroCounterArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                array[i][j] = 0;
            }
        }
    }
/**
 * Prints out all elements of an array.
 * @param array The array to be printed.
 */
    public static void printCounterArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                // System.out.print(array[i][j]);
                System.out.printf("%7d", array[i][j]);
            }
            System.out.println();
        }
    }
/**
 * Shuffles an array a number of times with shuffle1 method
 * , records and prints the frequency of each element's appearance 
 * in each location of the array.
 */
 /**
  * Shuffles an array a number of times with shuffle1 method
  * , records and prints the frequency of each element's appearance 
  * in each location of the array.
  * @param array The array to be shuffled
  * @param counterArray Where the frequency data are stored
  */
    public static void assessShuffle1(int[] array, int[][] counterArray) {
        initializeArray(array);
        zeroCounterArray(counterArray);
        System.out.println("shuffle1 results:");
        int counter = 0;
        while (counter < NUMBER) {
            counter++;
            Sorting.shuffle1(array);
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array.length; j++) {
                    if (i == array[j]) {
                        counterArray[i][j]++;
                    }
                }
            }
        }
        printCounterArray(counterArray);
        printMaxMinNumbers(counterArray);
    }
/**
 * Shuffles an array a number of times with shuffle2 method, 
 * records and prints the frequency of each element's appearance 
 * in each location of the array.
 * @param array The array to be shuffled
 * @param counterArray Where the frequency data are stored
 */
    public static void assessShuffle2(int[] array, int[][] counterArray) {
        initializeArray(array);
        zeroCounterArray(counterArray);
        System.out.println("shuffle2 results:");
        int counter = 0;
        while (counter < NUMBER) {
            counter++;
            Sorting.shuffle2(array);
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array.length; j++) {
                    if (i == array[j]) {
                        counterArray[i][j]++;
                    }
                }
            }
        }
        printCounterArray(counterArray);
        printMaxMinNumbers(counterArray);
    }
    
/**
 * Prints the largest and smallest elements of an array. 
 * @param array The array to be printed.
 */    
    public static void printMaxMinNumbers(int[][] array){
        System.out.println("Largest number is "+ largestNumber(array));
        System.out.println("Smallest number is " + smallestNumber(array));
        System.out.println();
    }
    
/**
 * Calculates the largets element of an array.
 * @param array The array to be calculated.
 * @return The largest element.
 */
    public static int largestNumber(int[][] array){
        int max;
        max = array[0][0];
        for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    if (array[i][j] > max)
                        max = array[i][j];
                }
        }
        return max;
    }

/**
 * Calculates the smallest element of an array.
 * @param array The array to be calculated.
 * @return The smallest element of the array.
 */
    public static int smallestNumber(int[][] array){
        int min;
        min = array[0][0];
        for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    if (array[i][j] < min)
                        min = array[i][j];
                }
        }
        return min;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sorting;
import java.util.Random;
/**
 * Performs bubble sort, selection sort, and insertion sort
 * on sorted array, reverse array and shuffled array.
 * arrays
 * @author sxycode
 * @author David Matuszek
 * @version 1/28/2008
 */
public class Sorting {

    static Random rand = new Random();
    static int[] array = new int[1000];

    public static void main(String[] args) {
        bubbleSorted(array);
        bubbleReverse(array);
        bubbleShuffled(array);
        selectionSorted(array);
        selectionReverse(array);
        selectionShuffled(array);
        insertionSorted(array);
        insertionReverse(array);
        insertionShuffled(array);
    }
/**
 * Bubble sort an already sorted (ascending) array.
 * @param array The array to be bubble sorted
 */
    public static void bubbleSorted(int[] array){
        initializeSortedArray(array);
        bubbleSort(array);
    }
/**
 * Bubble sort a reverse (descending) ordered array.
 * @param array The array to be bubble sorted
 */
    public static void bubbleReverse(int[] array){
        initializeReverseArray(array);
        bubbleSort(array);
    }

/**
 * Bubble sort a shuffled (random) array.
 * @param array The array to be bubble sorted
 */
    public static void bubbleShuffled(int[] array){
        shuffle1(array);
        bubbleSort(array);
    }
    
/**
 * Selection sort an already sorted (ascending) array.
 * @param array The array to be selection sorted.
 */
    public static void selectionSorted(int[] array){
        initializeSortedArray(array);
        selectionSort(array);
    }
/**
 * Selection sort a reverse (descending) ordered array.
 * @param array The array to be selection sorted.
 */
    public static void selectionReverse(int[] array){
        initializeReverseArray(array);
        selectionSort(array);
    }
/**
 * Selection sort a shuffled (random) array.
 * @param array The array to be selection sorted.
 */
    public static void selectionShuffled(int[] array){
        shuffle1(array);
        selectionSort(array);
    }
/**
 * Insertion sort an already sorted (ascending) array.
 * @param array The array to be insertion sorted.
 */
    public static void insertionSorted(int[] array){
        initializeSortedArray(array);
        insertionSort(array);
    }
/**
 * Insertion sort a reverse (descending) ordered array.
 * @param array The array to be insertion sorted.
 */
    public static void insertionReverse(int[] array){
        initializeReverseArray(array);
        insertionSort(array);
    }
/**
 * Insertion sort a shuffled (random) array.
 * @param array The array to be insertion sorted.
 */
    public static void insertionShuffled(int[] array){
        shuffle1(array);
        insertionSort(array);
    }
/**
 * Bubble sort an array.
 * @param array The array to be bubble sorted.
 */    
    public static void bubbleSort(int[] array) {
        int outer, inner;
        for (outer = array.length - 1; outer > 0; outer--) {  // counting down
            for (inner = 0; inner < outer; inner++) {        // bubbling up
                if (array[inner] > array[inner + 1]) {  // if out of order...
                    int temp = array[inner];          // ...then swap
                    array[inner] = array[inner + 1];
                    array[inner + 1] = temp;
                }
            }
        }
    }
/**
 * Selection sort an array.
 * @param array The array to be selection sorted.
 */
    public static void selectionSort(int[] array) {
        int outer, inner, min;
        for (outer = 0; outer < array.length - 1; outer++) {
            min = outer;
            for (inner = outer + 1; inner < array.length; inner++) {
                if (array[inner] < array[min]) {
                    min = inner;
                }
            }
            // a[min] is least among a[outer]..a[a.length - 1]
            int temp = array[outer];
            array[outer] = array[min];
            array[min] = temp;
        }
    }
/**
 * Insertion sort an array.
 * @param array The array to be insertion sorted.
 */
    public static void insertionSort(int[] array) {
        int in, out;
        for (out = 1; out < array.length; out++) // out is dividing line
        {
            int temp = array[out];    // remove marked item
            in = out;           // start shifts at out
            while (in > 0 && array[in - 1] >= temp) // until one is smaller,
            {
                array[in] = array[in - 1];      // shift item right,
                --in;            // go left one position
            }
            array[in] = temp;         // insert marked item
        } // end for
    } // end insertionSort()
/**
 * Shuffle an array.
 * @param array The array to be shuffled.
 */
    public static void shuffle1(int[] array) {
        int length = array.length;
        for (int i = 0; i < array.length; i++) {
            int index = rand.nextInt(length);
            int temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
    } 
/**
 * Shuffle an array.
 * @param array The array to be shuffled.
 */
    public static void shuffle2(int[] array) {
        int length = array.length;
        for (int i = length; i > 0; i--) {
            int index = rand.nextInt(i);
            int temp = array[i - 1];
            array[i - 1] = array[index];
            array[index] = temp;
        }
    }
    
/**
 * Initialize an sorted array.
 * @param array The array to be initialized.
 */
    public static void initializeSortedArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
    }
/**
 * Initialize a reverse ordered array.
 * @param array The array to be initialized.
 */
    public static void initializeReverseArray(int[] array) {
       for (int i = array.length; i > 0; i--) {
            array[array.length - i] = i - 1;
        }
    }
}

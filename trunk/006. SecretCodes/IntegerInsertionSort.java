import java.util.Arrays;


/*
 * This is file IntegerInsertionSort.java in project Tests, created on Oct 19, 2007
 */

public class IntegerInsertionSort {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int[] array = { 3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5 };
        System.out.println(Arrays.toString(array));
        insertionSort(array);
        System.out.println(Arrays.toString(array));
    }
    
    /**
     * Sorts the array into ascending order.
     * 
     * @param array The array to be sorted.
     */
    public static void insertionSort(int[] array) {
        int inner, outer;
            
        for (outer = 1; outer < array.length; outer++) {
            int temp = array[outer];
            inner = outer;
            while (inner > 0 && array[inner - 1] >= temp) {
                array[inner] = array[inner - 1];
                inner--;
            }
            array[inner] = temp;
             // Invariant: For all i < outer, j < outer, if i < j then a[i] <= a[j]
        }
    }
}

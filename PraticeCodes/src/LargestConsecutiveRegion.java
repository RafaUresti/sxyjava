import java.util.Scanner;
import java.util.ArrayList;
/**
 * This class finds the maximum sum of a consecutive region in an integer array.
 * The class can be executed and input can be done through command line. Also provided
 * a method that takes the integer array as an argument and does the same computation
 * and printing.
 * @author sxycode
 * @date Jan. 7th, 2009
 */
public class LargestConsecutiveRegion {
	public static void main(String[] argv){
		int[] intArray;
		ArrayList<Integer> intList;
		boolean validInput = true;
		Scanner scanner;
		do {
			do{
				intList = new ArrayList<Integer>();
				scanner = new Scanner(System.in);
				System.out.println("This program finds the consecutive region of an integer array and computes the maximum");
				System.out.println("Please input an array of integers, separated by space or new line and terminate by any letter:");
				while (scanner.hasNextInt()){
					intList.add(scanner.nextInt());
				}
				if (intList.size() == 0){
					System.out.println("Incorrect input, please try again");
					validInput = false;
				} else {
					validInput = true;
				}
			}while (!validInput);
			System.out.println("The size of the array is:" + intList.size());
			intArray = new int[intList.size()];
			for (int i = 0; i < intList.size(); i ++){
				intArray[i] = intList.get(i);
			}
			printLargestRegion(intArray);
			System.out.print("Would like to try again? (Y/N):");
			scanner = new Scanner(System.in);
			if (scanner.hasNext()){
				if (!"y".equalsIgnoreCase(scanner.next())){
					System.out.println("Thanks for using. Bye!");
					return;
				}
			}
		} while (true);
	}

	/**
	 * Given an integer array, prints out the consecutive region
	 * with the largest sum.
	 * @param intArray The array to check
	 */
	public static void printLargestRegion(int[] intArray) {
		if (intArray.length == 0){
			System.out.println("The array is empty!");
			return;
		}
		int maxIndex = 0;//Keeps track of the index of the largest element of the array, printed if all elements are negative
		int maxSum = -1;
		int begin = -1; //begin index of the largest region
		int end = -1; // end index of the largest region
		int localSum = -1; //sum of the current region scanned
		int localStart = -1; //begin index of localSum
		for (int i = 0; i < intArray.length; i ++){
			if (intArray[maxIndex] < intArray[i]){
				maxIndex = i;
			}
			if (localSum < 0){
				localSum = 0;
				localStart = i;
			}
			
			localSum += intArray[i];
			if (localSum > maxSum){
				maxSum = localSum;
				begin = localStart;
				end = i;
			}
		}
		System.out.print("The consecutive region of the array with the largest sum is:");
		if (maxSum < 0){//all elements of the array are negative
			System.out.println(intArray[maxIndex]);
			System.out.println("The sum of the region is: "+ intArray[maxIndex]);
		} else{
			for (int i = begin; i <= end; i ++){
				System.out.print(intArray[i]+ " ");
			}
			System.out.println("\nThe sum of the region is: "+ maxSum);
		}
	}
}

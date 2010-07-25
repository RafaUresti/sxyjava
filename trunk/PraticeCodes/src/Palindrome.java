import java.util.Scanner;
public class Palindrome {
	public static void main(String[] args){
		boolean tryAgain = false;
		do {
			System.out.println("Please input the integer to be checked:");
			Scanner scanner = new Scanner(System.in);

			int p;
			if (scanner.hasNextInt()){
				p = scanner.nextInt();
				if (isPalindrome(p)){
					System.out.println("It is palindrome!");
				} else {
					System.out.println("It is not palindrome!");
				}
			} 

			System.out.println("Try again?");
			if (scanner.hasNext()){
				if ("y".equalsIgnoreCase(scanner.next())){
					tryAgain = true;
				}
			}
		}while (tryAgain);
	}

	public static boolean isPalindrome(int p){
		String pString = Integer.toString(p);
		for (int i = 0; i < pString.length()/2; i ++){
			if (!(pString.charAt(i) == pString.charAt(pString.length() - i - 1))){
				return false;
			}
		}
		return true;
	}
}

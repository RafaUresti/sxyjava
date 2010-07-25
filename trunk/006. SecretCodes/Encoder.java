import java.util.Random;

public class Encoder {

	private final static String LETTERS = "abcdefghijklmnopqrstuvwxyz";
	private final static int LETTER_SIZE = 26;
	private final static char[] LETTER_ARRAY = LETTERS.toCharArray();
	private char[] codeArray;
	private char[] plainTextArray;
	private char[] codedTextArray;
	private static char[] randomLetterArray = LETTERS.toCharArray();
	private static char[] randomCodeArray = shuffle (randomLetterArray);
	private static StringBuilder randomCodeText = arrayToStringBuilder(randomCodeArray);
	private final int CASE_CONVERTER = 32; //the ASCII difference between upper and lower case letters
	
	public Encoder() {
		this(randomCodeText.toString());

	}
	public Encoder(String code) {
		codeArray = code.toCharArray();
	}

/**
 * Shuffles the input char array
 * @param array: Input array
 * @return shuffled array
 */
	private static char[] shuffle(char[] array) {
		Random random= new Random();
		for (int i = array.length; i > 1; i--) {
			int j = random.nextInt(i - 1);
			swap(array, i - 1, j);	
		}
		return array;
	}

	/**
	 * Swap two elements of an char array
	 * @param array: the input array that needs swapping
	 * @param i: location of the first element 
	 * @param j: location of the second element
	 */
	private static void swap(char[] array, int i, int j) {
		char temp;
		temp= array[i];
		array[i]=array[j];
		array[j]=temp;
	}
	
	/**
	 * Convert array to StringBuilder
	 * @param array: input array
	 * @return the StringBuilder converted from input array
	 */
	private static StringBuilder arrayToStringBuilder(char[] array) {
		StringBuilder sb = new StringBuilder(LETTER_SIZE);
		for (int i = 0; i < array.length; i++)
			sb.append(array[i]);
		return sb;
	}

/**
 * Encode the input line of text
 * @param plainText the input line of text to be encoded
 * @return coded line of text
 */
	public String encode(String plainText) {
		plainTextArray = plainText.toCharArray();
		codedTextArray = new char[plainTextArray.length];
		StringBuilder codedText = new StringBuilder();
		for (int i = 0; i < plainTextArray.length; i++) {
			for (int j = 0; j < LETTER_SIZE; j++) {
				if (plainTextArray[i] == LETTER_ARRAY[j]) {
					codedTextArray[i] = codeArray[j];
					break;
				}//generate coded text in array form 
				else if (plainTextArray[i] == LETTER_ARRAY[j]-CASE_CONVERTER) { //convert to upper case
					codedTextArray[i] = (char)(codeArray[j]-CASE_CONVERTER);
					break;
				}
				codedTextArray[i]= plainTextArray[i];
				}
			}
		codedText = arrayToStringBuilder(codedTextArray);
		return codedText.toString();
	}
	
	/**
	 * Encode the input multiple lines of text 
	 * @param plainText: input array of text strings to be encoded
	 * @return coded array of text strings
	 */
	public String[] encode(String[] plainText) {
		String[] codedText = new String[plainText.length];
		for (int i = 0; i < plainText.length; i++)
			codedText[i] = encode(plainText[i]);
		return codedText;
	}
	
	/**
	 * Decode the input line of text
	 * @param codedText input line of coded text to be decoded
	 * @return decoded line of plain text
	 */
	public String decode(String codedText) {
		codedTextArray = codedText.toCharArray();
		plainTextArray = new char[codedTextArray.length];
		StringBuilder plainText = new StringBuilder();
		for (int i = 0; i < codedTextArray.length; i++)
			for (int j = 0; j < LETTER_SIZE; j++) {
				if (codedTextArray[i] == codeArray[j]) {
					plainTextArray[i] = LETTER_ARRAY[j]; 
					break;
				}
				else if (codedTextArray[i] == codeArray[j]-CASE_CONVERTER) {
					plainTextArray[i] = (char)(LETTER_ARRAY[j]-CASE_CONVERTER); 
					break;
				}
				plainTextArray[i] = codedTextArray[i];
			}
		plainText = arrayToStringBuilder(plainTextArray);
		return plainText.toString();
	}
	
	/**
	 * Decode the input multiple lines of text
	 * @param codedText input array of coded text strings to be decoded
	 * @return decoded array of text stings
	 */
	public String[] decode(String[] codedText) {
		String[] plainText = new String[codedText.length];
		for (int i = 0; i < codedText.length; i++)
			plainText[i] = decode(codedText[i]);
		return codedText;
	}

	/**
	 * Modifies the secret code so that the code for the plain character 
	 * will be the coded character. To keep the code valid, this operation
	 *  changes two codes. 
	 * @param plainChar the plain character to be changed
	 * @param codedChar the character changed from the plain character
	 */
	public void change(char plainChar, char codedChar) {
		char temp = 0;
		int number = -1;
		for (int i = 0; i < LETTER_SIZE; i++)
			if (LETTER_ARRAY[i] == plainChar) {
				number = i; 
				//store the position of the plainChar in the LETTER_ARRAY
				// in other words, which letter it is
				temp = codeArray[i];//store the original code
				break;
			}
		for (int j = 0; j < LETTER_SIZE; j++)
			if (codeArray[j] == codedChar) {
				codeArray[j] = temp;
				codeArray[number] = codedChar;
				break;
			}
	}
}

import java.util.ArrayList;

public class Lexicon {
	private final int LETTER_SIZE = 26;
	/*private*/ ArrayList<String> wordList;
	/*private*/ ArrayList<Integer> countList;
	private final char[] LETTER_ARRAY = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };
	/*private*/ String[] wordArray;
	/*private*/ Integer[] countArray;

	/**
	 * Creates a empty Lexicon that can hold up to 500 words.
	 *
	 */
	public Lexicon() {
		wordList = new ArrayList<String>();
		countList = new ArrayList<Integer>();
//		String line = " ";
//		countWordsInLine (line);

	}

	/**
	 * Check if the word is already in the lexicon. If so, increment
	 * the count for the word. Otherwise, add the word into the lexicon
	 * set the count of the word to be 1 
	 * @param word the word to be checked
	 */

	public void countWord(String word) {
		if (isInLexicon(word)) {
			for (int i = 0; i < wordList.size(); i++){
				if (word.equalsIgnoreCase(wordList.get(i))){
					int newCount = countList.get(i)+1;
					countList.set(i,newCount);
					return;
				}
			}
		}
		else if (!isFull()) {// record no more than 500 words
			String lowerWord = word.toLowerCase();
			wordList.add(lowerWord);
			countList.add(1);
		}
	}

	/**
	 * Finds if the given word is in lexicon
	 * @param word:  the word to find in lexicon
	 * @return if the word is in lexicon
	 */

	private boolean isInLexicon(String word) {
		if (wordList.size() == 0)
			return false;
		for (int i = 0; i< wordList.size(); i++)
			if (word.equalsIgnoreCase(wordList.get(i)))
				return true;
		return false;
	}
	/**
	 * Check if there is still place in the inputArray for inputing words
	 * @return if the inputArray is already full (containing 500 words)
	 */
	private boolean isFull() {
		boolean full = true;
		if (wordList.size() < 500) {
			full = false;
		}
		return full;
	}

	/**
	 * Break a line of text into a String array of words in the text
	 * @param line the line of text to be converted
	 * @return the array of the words in the line
	 */

	public String[] breakIntoWords(String line) {
		line += " "; //add a none letter to the end of the line
		ArrayList<String> lineWordList = new ArrayList<String>(); //XXX change wordList to lineWordList, local variable
		int begin = 0;
		for (int i=0; i< line.length(); i++){
			if (!isPartOfWord(line.charAt(i))){
				if (begin != i){
					String word = line.substring(begin, i);
					lineWordList.add(word);
				}
				begin = i+1;
			}
		}
		return lineWordList.toArray(new String[0]);
	}

	/**
	 * Determines if a character is part of a string by checking
	 * if it's a letter or apostrophe(') or hyphen(-).
	 * @param ch:A character of the input String 
	 * @return: If the character is part of a word
	 */
	private boolean isPartOfWord(char ch){
		if (ch != '\'' && ch != '-' && (ch < 'A' || ch > 'z'))
			return false;
		else return true;
	}

	/**
	 * Integrate the words in the input line of text into lexicon
	 * @param line the input line of text
	 */

	public void countWordsInLine(String line) {
		String[] wordsInLine = breakIntoWords(line); //XXX changed to wordsInLine as a local variable
		for (int i = 0; i < wordsInLine.length; i++) {
			countWord(wordsInLine[i]);
		}
	}

	/**
	 * Get the count of the given word in lexicon
	 * @param word the input word
	 * @return the count of the input word in lexicon
	 */
	public int getCountFor(String word) {
		for (int i = 0; i < wordList.size(); i++) {
			if (word.equalsIgnoreCase(wordList.get(i))) {
				return countList.get(i);
			}
		}
		return 0;
	}

	/**
	 *Find all the words of a specific length (number of letters) in the lexicon.
	 *Output the words as an array in a descending order of their counts
	 * @param length the length of the words to be found
	 * @return the array of words sorted in descending order of their counts
	 */
	public String[] getWordsOfLength(int length) {
		ArrayList<String> fixedLengthWordList = new ArrayList<String>();
		ArrayList<Integer> fixedLengthCountList = new ArrayList<Integer>();

		for (int i = 0; i < wordList.size(); i++) {
			if (wordList.get(i).length() == length) {
				int count = getCountFor(wordList.get(i));
				fixedLengthWordList.add(wordList.get(i));
				fixedLengthCountList.add(count);
			}
		}

		String[] fixedLengthWordArray = fixedLengthWordList.toArray(new String[0]);
		Integer[] fixedLengthCountArray = fixedLengthCountList.toArray(new Integer[0]);
		int[] sortedCounterArray = new int[fixedLengthCountArray.length];

		// change the fixedLengthCountArray to integer array
		for (int i = 0; i < fixedLengthCountArray.length; i++){
			sortedCounterArray [i] = fixedLengthCountArray [i];
		}

		IntegerInsertionSort.insertionSort(sortedCounterArray);
		reverseOrder(sortedCounterArray);

		String[] wordByFreq = new String[fixedLengthWordArray.length];
		for (int i = 0; i < sortedCounterArray.length; i++) {
			Outerloop:
				for (int j = 0; j < fixedLengthCountArray.length; j++) {
					if (sortedCounterArray[i] == fixedLengthCountArray[j]) {
						for (int k = 0; k < wordByFreq.length; k++)
							if (fixedLengthWordArray[j].equalsIgnoreCase(wordByFreq[k]))//if the word has been sorted already
								continue Outerloop;										// go on find the next one
						wordByFreq[i] = fixedLengthWordArray[j];
					}
				}
		}
		return wordByFreq;

	}

	/**
	 * reverses the order of elements in an array
	 * @param array: the array to be reversed
	 */
	private void reverseOrder(int[] array){
		int temp;
		for (int i = 0; i < array.length/2; i++) {
			temp = array[i];
			array[i] = array[array.length-i-1];
			array[array.length-i-1] = temp;
		}
	}

	/**
	 * Find the frequency of each letter in the lexicon. 
	 * Sort the letters in descending order of their 
	 * frequency in the lexicon.
	 * @return the array of letters in descending order of their frequency
	 */

	public char[] getLettersByFrequency() {
		int[] frequencyArray = new int[LETTER_SIZE];
		char[] letterByFreq = new char[LETTER_SIZE];
		wordArray = wordList.toArray(new String[0]); 
		countArray = countList.toArray(new Integer[0]); 
		//XXX generates the frequency array of all letters
		for (int i = 0; i < wordArray.length; i++){//check each word of lexicon
			for (int j = 0; j < wordArray[i].length(); j++){//check each letter of the word
				for (int k = 0; k < LETTER_SIZE; k++){//go through alphabet
					if(wordArray[i].charAt(j) == LETTER_ARRAY[k])
						frequencyArray[k] += countArray[i];//add to frequency count of the word
				}
			}
		}
		int[] sortedFrequencyArray = new int [LETTER_SIZE];
		for (int i = 0; i < LETTER_SIZE; i++)
			sortedFrequencyArray[i]= frequencyArray[i];
		//generate the sorted array of letter occurences
		IntegerInsertionSort.insertionSort(sortedFrequencyArray);
		reverseOrder(sortedFrequencyArray);

		//generates the sorted letter array
		for (int i = 0; i < LETTER_ARRAY.length; i++) {
			Outerloop:
				for (int j = 0; j < LETTER_SIZE; j++) {
					if (sortedFrequencyArray[i] == frequencyArray[j]) {
						for (int k = 0; k < letterByFreq.length; k++)
							if (LETTER_ARRAY[j] == letterByFreq[k]){//if the letter has been sorted already
								continue Outerloop;                 // go on find the next one
							}
						letterByFreq[i] = LETTER_ARRAY[j];
						break;
					}

				}
		}
		return letterByFreq;
	}

}
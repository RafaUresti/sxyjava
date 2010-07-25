/**
 * Assignment 6 SecretCodes for CIT591, Fall 2007.
 * @author Dave Matuszek
 * @author Xiaoyi Sheng
 * @author Shu-Kai Chang
 * @version Oct 25, 2007
 */

public class Decoder {
	private Lexicon lexicon = new Lexicon();
	private LineReader reader = new LineReader();
	private Encoder encoder=new Encoder();
	private String[] allText;
	private final int LETTER_SIZE = 26;
	private char[] codeArray = new char[LETTER_SIZE];
	private final char[] LETTER_ARRAY = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };
	public static void main(String[] args) {
		System.out.println("\nPlease select input text file");
		new Decoder().run();
	}
	
	/**
	 * Reads the text file from the user, encodes it
	 * with random code, tries to decode it and 
	 * output the decoded text.
	 */
	public void run() {

		allText = reader.readAllLines();
		String [] encodedText=encoder.encode(allText); 		//encode texts from the files
		for (int i=0; i< allText.length; i++) //built a lexicon by counting all the words in the text line by line
			lexicon.countWordsInLine(encodedText[i]);
		String [] result=attemptToDecode(encodedText);
		System.out.println("\nThe decoded text is shown as following:\n\n\n\n");
		for (int i=0; i<result.length;i++){
			System.out.println(result[i]);
		}
	}
	/**
	 * Tries to decode the coded text by building 
	 * lexicon from the it and compare the letter frequency
	 * to general English letter frequency, generating the code.
	 * Then use the code to decode the coded text.
	 * @param lines the input coded text as an array of strings
	 * @return decoded text in array of strings 
	 */
	public String[] attemptToDecode(String[] lines){
		char[] letterFrequency = lexicon.getLettersByFrequency();
		char[] englishLetterFrequency = {'e','t', 'a', 'o', 'i', 'n', 's',
				'r', 'h', 'l', 'd', 'c', 'u', 'm', 'f', 'p', 'g', 'w', 'y',
				'b', 'v', 'k', 'x', 'j', 'q', 'z'};
		for (int i=0; i< LETTER_SIZE; i++)
			for (int j=0; j< LETTER_SIZE; j++){
				if (englishLetterFrequency[i] == LETTER_ARRAY[j])
					codeArray[j] = letterFrequency[i];
			}

		StringBuilder[] textBuilder = new StringBuilder[lines.length];
		for (int i=0; i<lines.length; i++)
			textBuilder[i]= new StringBuilder();
		for (int i=0; i< lines.length; i++) //"i" is the line number of the input text
			for (int j=0; j< lines[i].length(); j++)// "j" is the location of the letter in a line
				textBuilder[i].append(lines[i].charAt(j));
		for (int i=0; i< textBuilder.length; i++)
			for (int j=0; j< textBuilder[i].length(); j++)
				for (int k=0; k< LETTER_SIZE; k++){
					if (textBuilder[i].charAt(j)== LETTER_ARRAY[k])
					textBuilder[i].setCharAt(j,codeArray[k]);
				}

		String[] textArray =  new String[textBuilder.length];
		for (int i=0; i< textBuilder.length; i++)
			textArray[i]=textBuilder[i].toString();
		return textArray;
	}
}


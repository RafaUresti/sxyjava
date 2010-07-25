package cit006_SecretCodes;
import junit.framework.TestCase;
import java.util.Arrays;

public class LexiconTest extends TestCase {
	private String[] text = {" Hello World! ","How are you?", "I'm fine!",
			"Hello! Who are you?", "The birds are beautiful!", "the the the"};
	public void testLexicon() {
		Lexicon lexicon = new Lexicon();
		assertNotNull(lexicon);
		inputTextIntoLexicon(lexicon, text);
		int helloCount = lexicon.getCountFor("hello");
		assertEquals(helloCount, 2);
	}

	/**inputs text to Lexicon to generate a lexicon
	 * @param lexicon: the lexicon to build
	 */
	private void inputTextIntoLexicon(Lexicon lexicon, String[] text) {
		for (int i=0; i< text.length; i++) 
			lexicon.countWordsInLine(text[i]);
	}

	public void testCountWord() {
		Lexicon lexicon = new Lexicon();
		inputTextIntoLexicon(lexicon, text);
		lexicon.countWord("hello"); //adds 1 to "hello" count
		int helloCount = lexicon.getCountFor("hello");
		assertEquals(helloCount, 3);
		int morningCount = lexicon.getCountFor("morning");
		assertEquals(morningCount, 0);
		lexicon.countWord("morning");
		morningCount = lexicon.getCountFor("morning");
		assertEquals(morningCount, 1);
		lexicon.countWord("morning");
		morningCount = lexicon.getCountFor("morning");
		assertEquals(morningCount, 2);
	}

	public void testBreakIntoWords() {
		Lexicon lexicon = new Lexicon();
		String line = "hello  world!";
		String[] words = {"hello", "world"};
		String[] lineWordArray = lexicon.breakIntoWords (line);
		Arrays.equals(lineWordArray, words);
	}

	public void testCountWordsInLine() {
		Lexicon lexicon = new Lexicon();
		lexicon.countWordsInLine("Hello, mom. Mom says hello hello!");
		int momCount = lexicon.getCountFor("mom");
		assertEquals(momCount, 2);
		int helloCount = lexicon.getCountFor("hello");
		assertEquals(helloCount, 3);
	}

	public void testGetCountFor() {
		Lexicon lexicon = new Lexicon();
		inputTextIntoLexicon(lexicon, text);
		int theCount = lexicon.getCountFor("the");
		assertEquals(theCount, 4);
	}

	public void testGetWordsOfLength() {
		Lexicon lexicon = new Lexicon();
		inputTextIntoLexicon(lexicon, text);
		String[]threeLetterWordArray = lexicon.getWordsOfLength(3);
		String[] array3 = {"the", "are", "you"};
		Arrays.equals(array3, threeLetterWordArray);
		String[]fiveLetterWordArray = lexicon.getWordsOfLength(5);
		String[] array5 = {"hello", "world", "birds"};
		Arrays.equals(array5, fiveLetterWordArray);
	}

	public void testGetLettersByFrequency() {
		Lexicon lexicon = new Lexicon();
		lexicon.countWordsInLine("Hello, mom. Mom says hello hello!");
		char[] letterFrequency = lexicon.getLettersByFrequency();
		char[] expectedFrequency = {'l', 'o', 'm', 'e', 'h', 's', 'a', 'y', 'b', 'c', 
				'd', 'f', 'g', 'i', 'j', 'k', 'n', 'p', 'q', 'r', 't', 'u', 'v', 'w', 
				'x', 'z'};
		Arrays.equals(letterFrequency, expectedFrequency);
		Lexicon lexicon2 = new Lexicon();
		inputTextIntoLexicon(lexicon2, text);
		char[] letterFrequency2 = lexicon2.getLettersByFrequency();
		char[] expectedFrequency2 = {'e', 'h', 'o', 'l', 'r', 't', 'a', 'i', 'u',
				'w', 'b', 'd', 'f', 'y', 'm', 'n', 's', 'c', 'g', 'j', 'k', 'p', 
				'q', 'v', 'x', 'z'};
		Arrays.equals(letterFrequency2, expectedFrequency2);
	}

}

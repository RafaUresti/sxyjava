import java.util.*;

/** 
 * The Eliza takes the patternFile and user sentence input and generates the answer. 
 * The pattern file is organized so that the each pattern recognition key words is lead with
 * a "Q:" and is followed by a substitution (lead by "S") or a direct answer (lead by "S").
 * The key words and substitutions/answers are laid out according to their priority in the pattern file,
 * With the higher priority (more likely to replace with) on top and lower priority at bottom.
 * The pattern file also includes some general answers when no other pattern matches are available.
 * They don't have any special leading characters for identification. These general answers are returned
 * randomly.
 * 
 * @author sxycode
 * @author Youjun Wang
 * @version 12-05-2007
 */
public class Eliza {
	private String question;
	private String answer;
	private ArrayList<String> patternFile;

	private ArrayList<String> patternQLineWords;//list words of each line in patternFile
	private ArrayList<ArrayList<String>> patternQLines;//list of lists of words of each line in patternFile
	private ArrayList<String> patternSALines;//the list of substitutions for key words in question
	private ArrayList<Boolean> substitutionMode = new ArrayList<Boolean>();//keeps track whether the replacement is a word
	//substitution or sentence replacement. True for word substitution. False for sentence replacement.
	private ArrayList<String> questionWords;
	private ArrayList<String> noMatchAnswer;
	private ArrayList<ReplacementUnit> replacementList;
	/**
	 * Constructor for the Eliza class.
	 * @param pattern File takes in a pattern text file in the form of an ArrayList
	 * @param question the input of the user.
	 */
	public Eliza(ArrayList<String> patternFile, String question) {
		this.question = question;
		this.patternFile = patternFile;
	}
	/**
	 * Generates the answer according to the input of the user.
	 * @return the answer to the input question
	 */
	public String generateAnswer() {
		answer = "";
		breakPatternFile();
		parseQuestion();
		generateReplacementList();
		replaceOriginalQuestion();
		if (replacementList.size() != 0) {
			return answer;
		}
		else {
			Random random = new Random();
			int randomIndex = random.nextInt(noMatchAnswer.size());
			return noMatchAnswer.get(randomIndex);
		}
	}
	
	/**
	 * Creates ReplacementUnit objects to replace the keywords in the sentence input of the user
	 * and stores them as an ArrayList of the ReplacementUnit objects.
	 */
	private void generateReplacementList(){
		replacementList = new ArrayList<ReplacementUnit>();
		ReplacementUnit replacementUnit; 
		for (int i = 0; i < patternQLines.size(); i++) {
			int j = 0, k =0;
			int begin = -1, end = -1;
			OuterLoop:
				for (j = 0; j < questionWords.size(); j++) {
					replacementUnit = new ReplacementUnit();
					for (ReplacementUnit unit: replacementList) {
						if (j >= unit.getBeginPosition() && j <= unit.getEndPosition())
							continue OuterLoop;
					}
					if (patternQLines.get(i).size() > questionWords.size() - j) {
						break;
					}
					if (patternQLines.get(i).get(0).equalsIgnoreCase(questionWords.get(j))) {
						begin = j;
						replacementUnit.setBeginPosition(begin);
					}
					for (k = 1; k < patternQLines.get(i).size(); k++) {
						if (++j >= questionWords.size()) break;
						if (!patternQLines.get(i).get(k).equalsIgnoreCase(questionWords.get(j))) {
							break;
						}
					}
					if (j+k-2 < begin) end = j+k -1;
					else end = j+k-2;
					replacementUnit.setEndPosition(end);
					if (k == patternQLines.get(i).size() && 
							replacementUnit.getBeginPosition()>= 0 && 
							replacementUnit.getEndPosition()>= 0) {
						replacementUnit.setReplacement(patternSALines.get(i));
						replacementUnit.setSubstitutionMode(substitutionMode.get(i));
						replacementList.add(replacementUnit);
						}	
					}
				}
		}
		
	/**
	 * Generates answer according to the replacement list.
	 */
	private void replaceOriginalQuestion() {
		if (replacementList.size() != 0) {
			for (int i = replacementList.size()-1; i >= 0; i--) {
				for (int j = replacementList.get(i).getEndPosition(); 
				j>= replacementList.get(i).getBeginPosition(); j--) {
					if (!replacementList.get(i).getSubstitutionMode()) {
						answer = replacementList.get(i).getReplacement();
						return;
					}
					questionWords.remove(j);
				}
				questionWords.add(replacementList.get(i).getBeginPosition(), replacementList.get(i).getReplacement());
			}
			for (int i = 0; i < questionWords.size()-1; i++)
				answer += questionWords.get(i)+" ";
			answer += questionWords.get(questionWords.size()-1)+".";
		}
	}
	
	/**
	 * Analyzes the input of the user. Break the input into an ArrayList of Strings.
	 */
	private void parseQuestion() {
		String token;
		String word;
		StringTokenizer tokenizer= new StringTokenizer(question);
		questionWords = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) { 
			token = tokenizer.nextToken();
			word = extractWord(token);
			questionWords.add(word);
		}
	}

	/**
	 * Breaks the patternFile into a list of lists of words of each
	 * sentence in the pattern file.
	 */
	private void breakPatternFile() {
		String token;
		patternSALines = new ArrayList<String>();
		patternQLines = new ArrayList<ArrayList<String>>();
		ArrayList<String> answerLine;
		String substitution;
		noMatchAnswer = new ArrayList<String>();
		for (int i = 0; i < patternFile.size(); i++) {
			patternQLineWords = new ArrayList<String>();
			StringTokenizer tokenizerText = new StringTokenizer(patternFile.get(i));
			if (tokenizerText.hasMoreTokens()) {
				token = tokenizerText.nextToken();
				if (token.equals("Q:")) {//builds the key word list
					while(tokenizerText.hasMoreTokens()) {
						String word = extractWord(tokenizerText.nextToken());
						patternQLineWords.add(word);
					}
					patternQLines.add(patternQLineWords);
				}
				else if (token.equals("S:") || token.equals("A:")){//builds substitution line and mode lists
					if (token.equals("S:"))
						substitutionMode.add(true);
					if (token.equals("A:"))
						substitutionMode.add(false);
					{
						answerLine = new ArrayList<String>();
						while(tokenizerText.hasMoreTokens()) {
							answerLine.add(tokenizerText.nextToken());
						}
						substitution = "";
						for (int j = 0; j < answerLine.size()-1; j++)
							substitution += answerLine.get(j)+" ";
						substitution += answerLine.get(answerLine.size()-1);
						patternSALines.add(substitution);
					}
				}
				else{
					noMatchAnswer.add(patternFile.get(i));
				}
			}
		}
	}
	/**
	 * Converts a token into a word by removing any punctuation
	 * behind it.
	 * @param token the token to be converted
	 * @return the array of the words in the line
	 */
	private String extractWord(String token) {
		token += " "; //add a none letter to the end of the line
		int begin = 0;
		String word = null;
		for (int i=0; i< token.length(); i++){
			if (!isPartOfWord(token.charAt(i))){
				word = token.substring(begin, i);
				break;
			}
		}
		return word;
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
}



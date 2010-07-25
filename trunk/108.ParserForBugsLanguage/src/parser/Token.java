package parser;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Token {
    public static enum Type { KEYWORD, NAME, NUMBER, SYMBOL, ERROR, EOL, EOF };

    private static final String[] KEYWORD_LIST = new String[] {
        "Allbugs", "Bug", "move", "moveto", "turn", "turnto", "line",
        "loop", "exit", "if", "switch", "case", "return", "do", "color",
        "define", "using", "var", "initially", "background",
        "black", "blue", "cyan", "darkGray", "gray", "green", "lightGray",
        "magenta", "orange", "pink", "red", "white", "yellow", "brown",
        "purple", "none" };
        
        
    static final Set KEYWORDS = new HashSet(Arrays.asList(KEYWORD_LIST));

    public Type type;
    public String value;

    /**
     * Constructor for Tokens.
     * 
     * @param type The type of the token, chosen from the above list.
     * @param value The characters making up the token.
     */
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }
    
    /**
     * Constructor for Tokens
     * @param value the characters making up the token.
     */
    public Token(String value){
    	this.value = value;
    	this.type = determineTokenType(value);
    }

    public static Type determineTokenType(String string){
    	char firstCharacter = string.charAt(0);
        
        if (firstCharacter == '\n') return Type.EOL;
        
        else if (Character.isLetter(firstCharacter)) {
            if (string.equals("EOF")) return Type.EOF;
            else if (isKeyword(string)) return Type.KEYWORD;
            else return Type.NAME;
        }
        
        else if (Character.isDigit(firstCharacter)) return Type.NUMBER;
        
        else if (firstCharacter == '.') {
            if (string.length() == 1) return Type.SYMBOL;
            else return Type.NUMBER;
        }
        
        else return Type.SYMBOL;

    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Token) {
            Token that = (Token) o;
            return this.type == that.type && this.value.equals(that.value);
        }
        else return false;
    }

    @Override
    public String toString() {
        return type + ":" + value;
    }

    /**
     * Returns <code>true</code> if the argument is a recognized keyword,
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> if the argument is a keyword.
     */
    public static boolean isKeyword(String s) {
        return KEYWORDS.contains(s);
    }
}

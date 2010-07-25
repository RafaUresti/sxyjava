import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Token {
    static enum Type { KEYWORD, NAME, NUMBER, SYMBOL, ERROR, EOL, EOF };

    private static final String[] KEYWORD_LIST = new String[] {
        "Allbugs", "Bug", "move", "moveto", "turn", "turnto", "line",
        "loop", "exit", "if", "switch", "case", "return", "do", "color",
        "define", "using", "var", "initially",
        "black", "blue", "cyan", "darkGray", "gray", "green", "lightGray",
        "magenta", "orange", "pink", "red", "white", "yellow", "brown",
        "purple", "none" };


    public static final Set KEYWORDS = new HashSet(Arrays.asList(KEYWORD_LIST));

    Type type;
    String value;

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
}


public class Token {
    private final String value;
    private final TokenType type;

    public enum TokenType {
        NUMBER, OPERATOR, FUNCTION, LEFT_PAREN, RIGHT_PAREN
    }

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() { return value; }
    public TokenType getType() { return type; }

    @Override
    public String toString() {
        return value + " [" + type + "]";
    }
}
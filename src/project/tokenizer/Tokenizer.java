package project.tokenizer;
import java.util.ArrayList;
import java.util.List;
public class Tokenizer {
    private final String source;
    private int pos;
    private int line;

    public Tokenizer(String source) {
        this.source = source;
        this.pos    = 0;
        this.line   = 1;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < source.length()) {
            skipSpaces();              // skip spaces/tabs but NOT newlines
            if (pos >= source.length()) break;

            char c = source.charAt(pos);

            // --- Newline ---
            if (c == '\n') {
                tokens.add(new Token(TokenType.NEWLINE, "\\n", line));
                line++;
                pos++;
                continue;
            }

            // --- Skip carriage return ---
            if (c == '\r') { pos++; continue; }

            // --- Number ---
            if (Character.isDigit(c)) {
                tokens.add(readNumber());
                continue;
            }

            // --- String literal ---
            if (c == '"') {
                tokens.add(readString());
                continue;
            }

            // --- Operator symbols ---
            if (c == '+') { tokens.add(new Token(TokenType.PLUS,  "+", line)); pos++; continue; }
            if (c == '-') { tokens.add(new Token(TokenType.MINUS, "-", line)); pos++; continue; }
            if (c == '*') { tokens.add(new Token(TokenType.STAR,  "*", line)); pos++; continue; }
            if (c == '/') { tokens.add(new Token(TokenType.SLASH, "/", line)); pos++; continue; }
            if (c == '>') { tokens.add(new Token(TokenType.LESS_THAN, ">", line)); pos++; continue; }
            if (c == '<') { tokens.add(new Token(TokenType.LESS_THAN, "<", line)); pos++; continue; }

            // --- Word (keyword or identifier) ---
            if (Character.isLetter(c)) {
                tokens.add(readWord());
                continue;
            }

            // --- Unknown character: skip with a warning ---
            System.err.println("Warning: unknown character '" + c + "' at line " + line);
            pos++;
        }

        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    // ----------------------------------------------------------------
    // Helper: skip spaces and tabs (not newlines)
    // ----------------------------------------------------------------
    private void skipSpaces() {
        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (c == ' ' || c == '\t') pos++;
            else break;
        }
    }

    // ----------------------------------------------------------------
    // Helper: read a number like 10 or 3.14
    // ----------------------------------------------------------------
    private Token readNumber() {
        int start = pos;
        while (pos < source.length() && (Character.isDigit(source.charAt(pos)) || source.charAt(pos) == '.')) {
            pos++;
        }
        return new Token(TokenType.NUMBER, source.substring(start, pos), line);
    }

    // ----------------------------------------------------------------
    // Helper: read a quoted string like "hello world"
    // ----------------------------------------------------------------
    private Token readString() {
        pos++; // skip opening "
        int start = pos;
        while (pos < source.length() && source.charAt(pos) != '"') {
            pos++;
        }
        String value = source.substring(start, pos);
        pos++; // skip closing "
        return new Token(TokenType.STRING, value, line);
    }

    // ----------------------------------------------------------------
    // Helper: read a word and decide if it is a keyword or identifier.
    // SPEEK has multi-word keywords like "is greater than" and "repeat N times".
    // We handle those here by peeking ahead after reading the first word.
    // ----------------------------------------------------------------
    private Token readWord() {
        int start = pos;
        while (pos < source.length() && Character.isLetter(source.charAt(pos))) {
            pos++;
        }
        String word = source.substring(start, pos);

        switch (word) {
            case "let":     return new Token(TokenType.LET,     word, line);
            case "be":      return new Token(TokenType.BE,      word, line);
            case "say":     return new Token(TokenType.SAY,     word, line);
            case "then":    return new Token(TokenType.THEN,    word, line);
            case "repeat":  return new Token(TokenType.REPEAT,  word, line);
            case "times":   return new Token(TokenType.TIMES,   word, line);
            case "if":      return new Token(TokenType.IF,      word, line);
            case "is":      return new Token(TokenType.IS,      word, line);
            case "greater": return new Token(TokenType.GREATER, word, line);
            case "than":    return new Token(TokenType.THAN,    word, line);
            default:        return new Token(TokenType.IDENTIFIER, word, line);
        }
    }
}

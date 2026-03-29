package Aoopproject;

public enum TokenType {
    // Keywords
    LET, BE, SAY, IF, IS, GREATER, THAN, THEN, REPEAT, TIMES,

    // Literals
    NUMBER,       // e.g. 10, 3.14
    STRING,       // e.g. "hello"
    IDENTIFIER,   // e.g. x, score, result

    // Arithmetic operators
    PLUS, MINUS, STAR, SLASH,

    // Comparison operators
    GREATER_THAN,   // we'll build this as a compound token "is greater than"
    LESS_THAN,

    // Structure
    NEWLINE,
    EOF
}

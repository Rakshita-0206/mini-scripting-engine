package project.parser;

import project.interpreter.Environment;
import project.interpreter.Instruction;
import project.interpreter.AssignInstruction;
import project.interpreter.PrintInstruction;
import project.interpreter.IfInstruction;
import project.interpreter.RepeatInstruction;
import project.tokenizer.Token;
import project.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public List<Instruction> parse() {
        List<Instruction> instructions = new ArrayList<>();
        while (!isAtEnd()) {
            skipNewlines();
            if (isAtEnd()) break;
            instructions.add(parseInstruction());
        }
        return instructions;
    }

    private Instruction parseInstruction() {
        Token current = peek();
        if (current.getType() == TokenType.LET)    return parseAssign();
        if (current.getType() == TokenType.SAY)    return parsePrint();
        if (current.getType() == TokenType.IF)     return parseIf();
        if (current.getType() == TokenType.REPEAT) return parseRepeat();
        throw new RuntimeException("Unexpected token at line "
                + current.getLine() + ": \"" + current.getValue() + "\"");
    }

    // let <name> be <expression>
    private Instruction parseAssign() {
        consume(TokenType.LET);
        Token name = consume(TokenType.IDENTIFIER);
        consume(TokenType.BE);
        Expression expr = parseExpression();
        consumeNewlineOrEOF();
        return new AssignInstruction(name.getValue(), expr);
    }

    // say <expression>
    private Instruction parsePrint() {
        consume(TokenType.SAY);
        Expression expr = parseExpression();
        consumeNewlineOrEOF();
        return new PrintInstruction(expr);
    }

    // if <expr> is greater than <expr> then
    private Instruction parseIf() {
        consume(TokenType.IF);
        Expression left = parseExpression();
        consume(TokenType.IS);
        consume(TokenType.GREATER);
        consume(TokenType.THAN);
        Expression right = parseExpression();
        Expression condition = new BinaryOpNode(left, ">", right);
        consume(TokenType.THEN);
        consumeNewlineOrEOF();
        List<Instruction> body = parseBlock();
        return new IfInstruction(condition, body);
    }

    // repeat <number> times
    private Instruction parseRepeat() {
        consume(TokenType.REPEAT);
        Token countToken = consume(TokenType.NUMBER);
        int count = (int) Double.parseDouble(countToken.getValue());
        consume(TokenType.TIMES);
        consumeNewlineOrEOF();
        List<Instruction> body = parseBlock();
        return new RepeatInstruction(count, body);
    }

    // Read all instructions that form the body of an if/repeat block
    private List<Instruction> parseBlock() {
        List<Instruction> body = new ArrayList<>();
        skipNewlines();
        while (!isAtEnd()) {
            Token t = peek();
            if (t.getType() == TokenType.LET
                    || t.getType() == TokenType.SAY
                    || t.getType() == TokenType.IF
                    || t.getType() == TokenType.REPEAT) {
                body.add(parseInstruction());
                skipNewlines();
            } else {
                break;
            }
        }
        return body;
    }

    // Handles + and - (lowest precedence)
    private Expression parseExpression() {
        Expression left = parseTerm();
        while (peek().getType() == TokenType.PLUS
                || peek().getType() == TokenType.MINUS) {
            String op = advance().getValue();
            Expression right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }
        return left;
    }

    // Handles * and / (higher precedence)
    private Expression parseTerm() {
        Expression left = parsePrimary();
        while (peek().getType() == TokenType.STAR
                || peek().getType() == TokenType.SLASH) {
            String op = advance().getValue();
            Expression right = parsePrimary();
            left = new BinaryOpNode(left, op, right);
        }
        return left;
    }

    // Handles a single value: number, string, or variable
    private Expression parsePrimary() {
        Token t = peek();
        if (t.getType() == TokenType.NUMBER) {
            advance();
            return new NumberNode(Double.parseDouble(t.getValue()));
        }
        if (t.getType() == TokenType.STRING) {
            advance();
            return new StringNode(t.getValue());
        }
        if (t.getType() == TokenType.IDENTIFIER) {
            advance();
            return new VariableNode(t.getValue());
        }
        throw new RuntimeException("Expected number, string, or variable at line "
                + t.getLine() + " but got: \"" + t.getValue() + "\"");
    }

    private Token peek() { return tokens.get(pos); }
    private Token advance() { return tokens.get(pos++); }

    private Token consume(TokenType expected) {
        Token t = peek();
        if (t.getType() != expected) {
            throw new RuntimeException("Expected " + expected
                    + " but got " + t.getType()
                    + " (\"" + t.getValue() + "\") at line " + t.getLine());
        }
        return advance();
    }

    private void consumeNewlineOrEOF() {
        if (peek().getType() == TokenType.NEWLINE) advance();
    }

    private void skipNewlines() {
        while (peek().getType() == TokenType.NEWLINE) advance();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
}

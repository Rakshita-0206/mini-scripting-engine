package project.parser;
import project.interpreter.Environment;

public class BinaryOpNode implements Expression {
    private final Expression left;
    private final String     operator;
    private final Expression right;

    public BinaryOpNode(Expression left, String operator, Expression right) {
        this.left     = left;
        this.operator = operator;
        this.right    = right;
    }

    @Override
    public Object evaluate(Environment env) {
        Object leftVal  = left.evaluate(env);
        Object rightVal = right.evaluate(env);

        // Both sides must be numbers for arithmetic / comparison
        double l = toDouble(leftVal);
        double r = toDouble(rightVal);

        switch (operator) {
            case "+": return l + r;
            case "-": return l - r;
            case "*": return l * r;
            case "/": return l / r;
            case ">": return l > r;
            case "<": return l < r;
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    private double toDouble(Object val) {
        if (val instanceof Double) return (Double) val;
        throw new RuntimeException("Expected a number but got: " + val);
    }
}

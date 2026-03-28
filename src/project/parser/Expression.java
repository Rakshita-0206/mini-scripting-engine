package project.parser;

import project.interpreter.Environment;

public interface Expression {
    // Returns Double for numbers, String for text, Boolean for comparisons
    Object evaluate(Environment env);
}


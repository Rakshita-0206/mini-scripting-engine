package project.parser;

import project.interpreter.Environment;

public class VariableNode implements Expression {
    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public Object evaluate(Environment env) {
        return env.get(name);  // throws if not defined
    }
}

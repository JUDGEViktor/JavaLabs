package ru.spbstu.pipeline;

public abstract class BaseGrammar {

    private final String[] tokens;
    private static final String delimiter = "=";

    protected BaseGrammar(String[] tokens) {
        this.tokens = tokens;
    }

    public final int numberTokens() {
        if (tokens == null) {
            return 0;
        }
        return tokens.length;
    }

    public final String token(int index) {
        if (tokens != null && index >= 0 && index < tokens.length) {
            return tokens[index];
        }
        return null;
    }

    public String delimiter() {
        return delimiter;
    }
}

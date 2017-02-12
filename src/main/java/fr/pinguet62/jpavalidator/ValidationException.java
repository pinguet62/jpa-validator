package fr.pinguet62.jpavalidator;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1;

    private final List<String> errors = new ArrayList<>();

    public ValidationException() {}

    public ValidationException(String message) {
        super(message);
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (getMessage() != null) {
            builder.append(getMessage());
            builder.append(lineSeparator());
        }
        String strErrors = errors.stream().map(e -> "* " + e).collect(joining(lineSeparator()));
        builder.append(strErrors);
        return builder.toString().replaceFirst(lineSeparator() + "$", "");
    }

}
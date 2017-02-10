package fr.pinguet62.jpavalidator;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1;

    private final List<String> errors = new ArrayList<>();

    public List<String> getErrors() {
        return errors;
    }

}
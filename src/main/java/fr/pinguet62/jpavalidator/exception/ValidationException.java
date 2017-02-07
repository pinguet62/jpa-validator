package fr.pinguet62.jpavalidator.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1;

    private final List<ValidationException> errors = new ArrayList<>();

    public List<ValidationException> getErrors() {
        return errors;
    }

}
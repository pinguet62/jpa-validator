package fr.pinguet62.jpavalidator.exception;

import static java.lang.String.format;

import java.lang.reflect.Field;

public class FieldException extends VException {

    private static final long serialVersionUID = 1;

    private final Field field;

    public FieldException(Field field, String message) {
        super(message);
        this.field = field;
    }

    @Override
    public String toString() {
        return format("%s: %s", field, getMessage());
    }

}
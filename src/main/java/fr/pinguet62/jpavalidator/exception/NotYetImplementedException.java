package fr.pinguet62.jpavalidator.exception;

public class NotYetImplementedException extends RuntimeException {

    private static final long serialVersionUID = 1;

    public NotYetImplementedException() {}

    public NotYetImplementedException(String message) {
        super(message);
    }

}
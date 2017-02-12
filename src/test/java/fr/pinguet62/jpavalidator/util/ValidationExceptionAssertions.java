package fr.pinguet62.jpavalidator.util;

import fr.pinguet62.jpavalidator.ValidationException;

public class ValidationExceptionAssertions {

    /** Check that at least 1 of {@link ValidationException#getErrors()} contains the {@code message}. */
    public static void assertContainsMessage(ValidationException exception, String message) {
        for (String error : exception.getErrors())
            if (error.contains(message))
                return;
        throw new AssertionError("Message \"" + message + "\"not found into " + ValidationException.class.getSimpleName());
    }

}
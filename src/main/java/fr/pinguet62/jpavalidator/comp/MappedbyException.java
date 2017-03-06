package fr.pinguet62.jpavalidator.comp;

import static java.lang.String.format;

public class MappedbyException extends VException {

    private static final long serialVersionUID = 1;

    private final String mappedBy;

    public MappedbyException(String mappedBy, String message) {
        super(message);
        this.mappedBy = mappedBy;
    }

    @Override
    public String toString() {
        return format("%s: %s", mappedBy, getMessage());
    }

}
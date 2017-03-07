package fr.pinguet62.jpavalidator.exception;

import static java.lang.String.format;

public class ColumnException extends VException {

    private static final long serialVersionUID = 1;

    private final String columnName;

    private final String tableName;

    public ColumnException(String tableName, String columnName, String message) {
        super(message);
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return format("%s.%s: %s", tableName, columnName, getMessage());
    }

}
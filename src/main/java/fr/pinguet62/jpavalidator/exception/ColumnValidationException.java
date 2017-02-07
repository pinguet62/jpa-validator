package fr.pinguet62.jpavalidator.exception;

public class ColumnValidationException {

    private final String columnName;

    private final String message;

    private final String tableName;

    public ColumnValidationException(String tableName, String columnName, String message) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("Table=%s, Column=%s: %s", tableName, columnName, message);
    }

}
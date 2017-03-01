package fr.pinguet62.jpavalidator.comp;

public class ColumnException extends VException {

    private static final long serialVersionUID = 1;

    public ColumnException(String tableName, String columnName, String message) {
        super(message);
    }

}
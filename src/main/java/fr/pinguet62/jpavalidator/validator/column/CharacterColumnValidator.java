package fr.pinguet62.jpavalidator.validator.column;

import java.lang.reflect.Field;

import javax.persistence.Column;

import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;

public class CharacterColumnValidator extends AbstractColumnValidator {

    public CharacterColumnValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        if (!JdbcMetadataChecker.INSTANCE.checkCharacter(tableName, columnName, column.length()))
            throw new ColumnException(tableName, columnName, "invalid character length");
    }

    @Override
    public boolean support(Field field) {
        return field.getType().equals(String.class);
    }

}
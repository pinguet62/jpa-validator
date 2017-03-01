package fr.pinguet62.jpavalidator.comp.column;

import java.lang.reflect.Field;

import javax.persistence.Column;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;

public class CharacterColumnValidator extends AbstractColumnValidator {

    public CharacterColumnValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void process(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        if (!JdbcMetadataChecker.INSTANCE.checkCharacter(tableName, columnName, column.length()))
            throw new ColumnException(tableName, columnName, "invalid character length");

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.getType().equals(String.class);
    }

}
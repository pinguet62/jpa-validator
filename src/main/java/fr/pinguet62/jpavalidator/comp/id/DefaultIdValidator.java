package fr.pinguet62.jpavalidator.comp.id;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;
import fr.pinguet62.jpavalidator.comp.Validator;

public class DefaultIdValidator extends Validator {

    public DefaultIdValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void process(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        // PK
        if (!JdbcMetadataChecker.INSTANCE.checkPrimaryKey(tableName, columnName))
            throw new ColumnException(tableName, columnName, "column is not an PK");

        // Not auto-increment
        if (JdbcMetadataChecker.INSTANCE.checkAutoIncrement(tableName, columnName, true))
            throw new ColumnException(tableName, columnName, "column is 'auto-increment'");

        // TODO processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return !field.isAnnotationPresent(GeneratedValue.class);
    }

}
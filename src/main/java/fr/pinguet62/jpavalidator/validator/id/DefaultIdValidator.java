package fr.pinguet62.jpavalidator.validator.id;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.validator.Validator;

public class DefaultIdValidator extends Validator {

    public DefaultIdValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        // PK
        if (!JdbcMetadataChecker.INSTANCE.checkPrimaryKey(tableName, columnName))
            throw new ColumnException(tableName, columnName, "column is not an PK");

        // Not auto-increment
        if (JdbcMetadataChecker.INSTANCE.checkAutoIncrement(tableName, columnName, true))
            throw new ColumnException(tableName, columnName, "column is 'auto-increment'");
    }

    @Override
    public boolean support(Field field) {
        return !field.isAnnotationPresent(GeneratedValue.class);
    }

}
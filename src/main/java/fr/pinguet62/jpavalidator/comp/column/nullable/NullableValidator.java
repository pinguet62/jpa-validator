package fr.pinguet62.jpavalidator.comp.column.nullable;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;
import fr.pinguet62.jpavalidator.comp.column.AbstractColumnValidator;

public class NullableValidator extends AbstractColumnValidator {

    public NullableValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void process(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        boolean nullable = column.nullable();
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(tableName, columnName, nullable))
            throw new ColumnException(tableName, columnName, "invalid nullable constraint");

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return !field.isAnnotationPresent(GeneratedValue.class);
    }

}
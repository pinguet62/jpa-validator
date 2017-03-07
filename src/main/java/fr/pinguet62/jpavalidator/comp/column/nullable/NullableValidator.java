package fr.pinguet62.jpavalidator.comp.column.nullable;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.column.AbstractColumnValidator;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.exception.VException;

public class NullableValidator extends AbstractColumnValidator {

    public NullableValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        boolean nullable = column.nullable();

        // Database constraint
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(tableName, columnName, nullable))
            throw new ColumnException(tableName, columnName, "invalid nullable constraint");

        // Field type
        if (nullable && field.getType().isPrimitive())
            throw new VException("field " + field + " must be an " + Object.class.getSimpleName());
    }

    @Override
    public boolean support(Field field) {
        return !field.isAnnotationPresent(GeneratedValue.class);
    }

}
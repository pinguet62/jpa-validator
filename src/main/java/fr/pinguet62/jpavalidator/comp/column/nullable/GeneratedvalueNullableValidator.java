package fr.pinguet62.jpavalidator.comp.column.nullable;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import fr.pinguet62.jpavalidator.comp.ColumnException;
import fr.pinguet62.jpavalidator.comp.column.AbstractColumnValidator;

/** {@link GeneratedValue} require {@link Column#nullable() nullable=true}, because value is managed by JPA. */
public class GeneratedvalueNullableValidator extends AbstractColumnValidator {

    public GeneratedvalueNullableValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void process(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        if (column.nullable() == false)
            throw new ColumnException(tableName, columnName, "using @" + GeneratedValue.class.getSimpleName() + " the @"
                    + Column.class.getSimpleName() + "(nullable) must be true");

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(GeneratedValue.class);
    }

}
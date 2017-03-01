package fr.pinguet62.jpavalidator.comp.column.nullable;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;
import fr.pinguet62.jpavalidator.comp.Validator;
import fr.pinguet62.jpavalidator.comp.column.AbstractColumnValidator;
import fr.pinguet62.jpavalidator.comp.column.NumericColumnValidator;

public class BaseNullableValidator extends AbstractColumnValidator {

    public BaseNullableValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected List<Validator> getAvailableNextValidators() {
        return asList(new GeneratedvalueNullableValidator(tableName, column), new NumericColumnValidator(tableName, column));
    }

    @Override
    protected void process(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        boolean nullable = column.nullable();
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(tableName, columnName, nullable))
            throw new ColumnException(tableName, columnName, "invalid nullable");

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return !field.isAnnotationPresent(GeneratedValue.class);
    }

}
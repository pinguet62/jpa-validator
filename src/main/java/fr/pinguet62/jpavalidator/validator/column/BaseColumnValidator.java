package fr.pinguet62.jpavalidator.validator.column;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import javax.persistence.Column;

import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.MultipleProcessor;
import fr.pinguet62.jpavalidator.validator.Validator;
import fr.pinguet62.jpavalidator.validator.column.enumeration.BaseEnumerationValidator;
import fr.pinguet62.jpavalidator.validator.column.nullable.BaseNullableValidator;

public class BaseColumnValidator extends Validator {

    private Column column;

    public BaseColumnValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void doProcess(Field field) {
        column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        if (!JdbcMetadataChecker.INSTANCE.checkColumnExists(tableName, columnName))
            throw new ColumnException(tableName, columnName, "doesn't exists");
    }

    @Override
    protected AbstractProcessor getProcessor() {
        return new MultipleProcessor(asList(new TypeColumnValidator(tableName, column),
                new BaseNullableValidator(tableName, column), new CharacterColumnValidator(tableName, column),
                new NumericColumnValidator(tableName, column), new BaseEnumerationValidator(tableName)));
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(Column.class);
    }

}
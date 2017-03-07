package fr.pinguet62.jpavalidator.validator.id;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Id;

import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.OnlyOneProcessor;
import fr.pinguet62.jpavalidator.validator.Validator;
import fr.pinguet62.jpavalidator.validator.id.generatedvalue.BaseGeneratedvalueValidator;

public class BaseIdValidator extends Validator {

    public BaseIdValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        if (!JdbcMetadataChecker.INSTANCE.checkPrimaryKey(tableName, columnName))
            throw new ColumnException(tableName, columnName, "column is not a PK");
    }

    @Override
    protected AbstractProcessor getProcessor() {
        return new OnlyOneProcessor(asList(new DefaultIdValidator(tableName), new BaseGeneratedvalueValidator(tableName)));
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

}
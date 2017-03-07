package fr.pinguet62.jpavalidator.comp.column.nullable;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import javax.persistence.Column;

import fr.pinguet62.jpavalidator.comp.column.AbstractColumnValidator;
import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.OnlyOneProcessor;

public class BaseNullableValidator extends AbstractColumnValidator {

    public BaseNullableValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected AbstractProcessor getProcessor() {
        return new OnlyOneProcessor(
                asList(new GeneratedvalueNullableValidator(tableName, column), new NullableValidator(tableName, column)));
    }

    @Override
    public boolean support(Field field) {
        return true;
    }

}
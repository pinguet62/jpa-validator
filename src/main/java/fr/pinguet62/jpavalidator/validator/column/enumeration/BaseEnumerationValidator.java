package fr.pinguet62.jpavalidator.validator.column.enumeration;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.OnlyOneProcessor;
import fr.pinguet62.jpavalidator.validator.Validator;
import fr.pinguet62.jpavalidator.validator.column.TypeColumnValidator;

public class BaseEnumerationValidator extends Validator {

    public BaseEnumerationValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected AbstractProcessor getProcessor() {
        return new OnlyOneProcessor(asList(new TypeColumnValidator(tableName, null)));
    }

    @Override
    public boolean support(Field field) {
        return Enum.class.isAssignableFrom(field.getType());
    }

}
package fr.pinguet62.jpavalidator.comp.column.enumeration;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import fr.pinguet62.jpavalidator.comp.Validator;
import fr.pinguet62.jpavalidator.comp.column.TypeColumnValidator;
import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.OnlyOneProcessor;

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
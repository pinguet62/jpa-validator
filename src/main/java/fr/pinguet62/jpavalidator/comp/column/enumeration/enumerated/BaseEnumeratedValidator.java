package fr.pinguet62.jpavalidator.comp.column.enumeration.enumerated;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import fr.pinguet62.jpavalidator.comp.Validator;
import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.OnlyOneProcessor;

public class BaseEnumeratedValidator extends Validator {

    public BaseEnumeratedValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected AbstractProcessor getProcessor() {
        return new OnlyOneProcessor(
                asList(new OrdinalEnumeratedValidator(tableName), new StringEnumeratedValidator(tableName)));
    }

    @Override
    public boolean support(Field field) {
        return Enum.class.isAssignableFrom(field.getType());
    }

}
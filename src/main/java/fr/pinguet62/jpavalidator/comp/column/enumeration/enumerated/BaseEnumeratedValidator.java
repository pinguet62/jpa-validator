package fr.pinguet62.jpavalidator.comp.column.enumeration.enumerated;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.List;

import fr.pinguet62.jpavalidator.comp.Validator;

public class BaseEnumeratedValidator extends Validator {

    public BaseEnumeratedValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected List<Validator> getAvailableNextValidators() {
        return asList(new OrdinalEnumeratedValidator(tableName), new StringEnumeratedValidator(tableName));
    }

    @Override
    protected void process(Field field) {
        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return Enum.class.isAssignableFrom(field.getType());
    }

}
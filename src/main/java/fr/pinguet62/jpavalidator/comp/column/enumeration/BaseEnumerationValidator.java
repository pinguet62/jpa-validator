package fr.pinguet62.jpavalidator.comp.column.enumeration;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.List;

import fr.pinguet62.jpavalidator.comp.Validator;
import fr.pinguet62.jpavalidator.comp.column.TypeColumnValidator;

public class BaseEnumerationValidator extends Validator {

    public BaseEnumerationValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected List<Validator> getAvailableNextValidators() {
        return asList(new TypeColumnValidator(tableName, null));
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
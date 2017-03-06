package fr.pinguet62.jpavalidator.comp;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.List;

import fr.pinguet62.jpavalidator.comp.column.BaseColumnValidator;
import fr.pinguet62.jpavalidator.comp.id.BaseIdValidator;
import fr.pinguet62.jpavalidator.comp.onetoone.BaseOnetooneValidator;

public class FieldValidator extends Validator {

    public FieldValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected List<Validator> getAvailableNextValidators() {
        return asList(new BaseColumnValidator(tableName), new BaseIdValidator(tableName), new BaseOnetooneValidator(tableName));
    }

    @Override
    protected void process(Field field) {}

    @Override
    protected boolean support(Field field) {
        return true;
    }

}
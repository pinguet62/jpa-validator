package fr.pinguet62.jpavalidator.validator;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.MultipleProcessor;
import fr.pinguet62.jpavalidator.validator.column.BaseColumnValidator;
import fr.pinguet62.jpavalidator.validator.id.BaseIdValidator;
import fr.pinguet62.jpavalidator.validator.manytomany.BaseManytomanyValidator;
import fr.pinguet62.jpavalidator.validator.manytoone.ManytooneValidator;
import fr.pinguet62.jpavalidator.validator.onetomany.OnetomanyValidator;
import fr.pinguet62.jpavalidator.validator.onetoone.BaseOnetooneValidator;

public class FieldValidator extends Validator {

    public FieldValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected AbstractProcessor getProcessor() {
        return new MultipleProcessor(asList(new BaseColumnValidator(tableName), new BaseIdValidator(tableName),
                new BaseManytomanyValidator(tableName), new ManytooneValidator(tableName), new OnetomanyValidator(tableName),
                new BaseOnetooneValidator(tableName)));
    }

    @Override
    public boolean support(Field field) {
        return true;
    }

}
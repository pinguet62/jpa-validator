package fr.pinguet62.jpavalidator.comp;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import fr.pinguet62.jpavalidator.comp.column.BaseColumnValidator;
import fr.pinguet62.jpavalidator.comp.id.BaseIdValidator;
import fr.pinguet62.jpavalidator.comp.manytomany.BaseManytomanyValidator;
import fr.pinguet62.jpavalidator.comp.manytoone.ManytooneValidator;
import fr.pinguet62.jpavalidator.comp.onetomany.OnetomanyValidator;
import fr.pinguet62.jpavalidator.comp.onetoone.BaseOnetooneValidator;
import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.MultipleProcessor;

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
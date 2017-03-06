package fr.pinguet62.jpavalidator.comp.id.generatedvalue;

import javax.persistence.GeneratedValue;

import fr.pinguet62.jpavalidator.comp.Validator;

public abstract class AbstractGeneratedvalueValidator extends Validator {

    protected final GeneratedValue generatedValue;

    public AbstractGeneratedvalueValidator(String tableName, GeneratedValue generatedValue) {
        super(tableName);
        this.generatedValue = generatedValue;
    }

}
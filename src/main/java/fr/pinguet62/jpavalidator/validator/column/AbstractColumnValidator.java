package fr.pinguet62.jpavalidator.validator.column;

import javax.persistence.Column;

import fr.pinguet62.jpavalidator.validator.Validator;

public abstract class AbstractColumnValidator extends Validator {

    protected final Column column;

    protected AbstractColumnValidator(String tableName, Column column) {
        super(tableName);
        this.column = column;
    }

}
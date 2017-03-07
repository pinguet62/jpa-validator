package fr.pinguet62.jpavalidator.validator.onetoone;

import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.validator.Validator;

public abstract class AbstractOnetooneValidator extends Validator {

    protected final OneToOne oneToOne;

    protected AbstractOnetooneValidator(String tableName, OneToOne oneToOne) {
        super(tableName);
        this.oneToOne = oneToOne;
    }

}
package fr.pinguet62.jpavalidator.comp.onetoone;

import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.comp.Validator;

public abstract class AbstractOnetooneValidator extends Validator {

    protected final OneToOne oneToOne;

    protected AbstractOnetooneValidator(String tableName, OneToOne oneToOne) {
        super(tableName);
        this.oneToOne = oneToOne;
    }

}
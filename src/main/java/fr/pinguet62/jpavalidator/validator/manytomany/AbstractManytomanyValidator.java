package fr.pinguet62.jpavalidator.validator.manytomany;

import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.validator.Validator;

public abstract class AbstractManytomanyValidator extends Validator {

    protected final ManyToMany manyToMany;

    protected AbstractManytomanyValidator(String tableName, ManyToMany manyToMany) {
        super(tableName);
        this.manyToMany = manyToMany;
    }

}
package fr.pinguet62.jpavalidator.processor;

import java.lang.reflect.Field;
import java.util.Collection;

import fr.pinguet62.jpavalidator.validator.Validator;

/**
 * <b>Store</b> {@link Validator} and <b>define strategy</b> to get the next available to process.
 * <p>
 * The {@link #validators} must be <b>ordered</b> because are evaluated in same order.
 */
public abstract class AbstractProcessor {

    protected final Collection<Validator> validators;

    protected AbstractProcessor(Collection<Validator> validators) {
        this.validators = validators;
    }

    public abstract Collection<Validator> getValidators(Field field);

}
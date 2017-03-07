package fr.pinguet62.jpavalidator.processor;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.Collection;

import fr.pinguet62.jpavalidator.validator.Validator;

public class OnlyOneProcessor extends AbstractProcessor {

    public OnlyOneProcessor(Collection<Validator> validators) {
        super(validators);
    }

    @Override
    public Collection<Validator> getValidators(Field field) {
        Validator result = null;
        for (Validator validator : validators)
            if (validator.support(field)) {
                if (result != null)
                    throw new UnsupportedOperationException("Too many validators found");
                result = validator;
            }
        if (result == null)
            throw new UnsupportedOperationException("Validator not found");
        return asList(result);
    }

}
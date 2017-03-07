package fr.pinguet62.jpavalidator.processor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.pinguet62.jpavalidator.comp.Validator;

public class MultipleProcessor extends AbstractProcessor {

    public MultipleProcessor(Collection<Validator> validators) {
        super(validators);
    }

    @Override
    public Collection<Validator> getValidators(Field field) {
        List<Validator> results = new ArrayList<>();
        for (Validator validator : validators)
            if (validator.support(field))
                results.add(validator);
        return results;
    }

}
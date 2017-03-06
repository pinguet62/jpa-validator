package fr.pinguet62.jpavalidator.comp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Validator {

    protected final String tableName;

    public Validator(String tableName) {
        this.tableName = tableName;
    }

    /** The values must be <b>ordered</b> because are evaluated in order. */
    protected List<Validator> getAvailableNextValidators() {
        return new ArrayList<>();
    }

    protected abstract void process(Field field);

    public void processNext(Field field) {
        Validator next = null;
        for (Validator validator : getAvailableNextValidators())
            if (validator.support(field)) {
                if (next != null)
                    throw new RuntimeException("Only 1 can be called");
                next = validator;
            }
        if (next == null)
            throw new RuntimeException("Not found");
        next.process(field);
    }

    protected abstract boolean support(Field field);

}
package fr.pinguet62.jpavalidator.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;

import fr.pinguet62.jpavalidator.Counter;
import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.MultipleProcessor;

public abstract class Validator {

    protected final String tableName;

    public Validator(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Implemented by each {@link Validator} to check the JPA rules.
     * <p>
     * Empty by default: define a <b>tree-separation</b> (TODO translate) whithout action.
     */
    protected void doProcess(Field field) {}

    /**
     * Define next {@link Validator}s and the strategy to get the {@link Validator}(s) to process.
     * <p>
     * Empty by default: define a <b>end-tree</b> {@link Validator} without next to process.
     */
    protected AbstractProcessor getProcessor() {
        return new MultipleProcessor(new ArrayList<>());
    }

    /**
     * Internal method who:
     * <ol>
     * <li>get next {@link Validator#support(Field) supported validators} using {@link #getProcessor()}</li>
     * <li>{@link #doProcess(Field) process validation}</li>
     * <li>{@link #process(Field) process next sub-validators}</li>
     * </ol>
     */
    public void process(Field field) {
        for (Validator validator : getProcessor().getValidators(field)) {
            Counter.logValidator(validator);
            Counter.N++;
            validator.doProcess(field);
            validator.process(field);
            Counter.N--;
        }
    }

    /**
     * Method used by <b>responsability chaining</b> pattern to check if the {@link Field} can be
     * {@link #doProcess(Field) processed} by the {@link Validator}.
     */
    public abstract boolean support(Field field);

}
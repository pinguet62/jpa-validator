package fr.pinguet62.jpavalidator.validator.onetoone;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.OnlyOneProcessor;
import fr.pinguet62.jpavalidator.validator.Validator;

public class BaseOnetooneValidator extends Validator {

    private OneToOne oneToOne;

    public BaseOnetooneValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void doProcess(Field field) {
        oneToOne = field.getDeclaredAnnotation(OneToOne.class);
    }

    @Override
    protected AbstractProcessor getProcessor() {
        return new OnlyOneProcessor(
                asList(new DirectOnetooneValidator(tableName, oneToOne), new MappedbyOnetooneValidator(tableName, oneToOne)));
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(OneToOne.class);
    }

}
package fr.pinguet62.jpavalidator.comp.onetoone;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.comp.Validator;

public class BaseOnetooneValidator extends Validator {

    private OneToOne oneToOne;

    public BaseOnetooneValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected List<Validator> getAvailableNextValidators() {
        return asList(new DirectOnetooneValidator(tableName, oneToOne), new MappedbyOnetooneValidator(tableName, oneToOne));
    }

    @Override
    protected void process(Field field) {
        oneToOne = field.getDeclaredAnnotation(OneToOne.class);

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.isAnnotationPresent(OneToOne.class);
    }

}
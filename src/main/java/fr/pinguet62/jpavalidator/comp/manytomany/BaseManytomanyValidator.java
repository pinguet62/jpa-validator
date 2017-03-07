package fr.pinguet62.jpavalidator.comp.manytomany;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;

import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.comp.Validator;
import fr.pinguet62.jpavalidator.processor.AbstractProcessor;
import fr.pinguet62.jpavalidator.processor.OnlyOneProcessor;

public class BaseManytomanyValidator extends Validator {

    private ManyToMany manyToMany;

    public BaseManytomanyValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void doProcess(Field field) {
        manyToMany = field.getDeclaredAnnotation(ManyToMany.class);
    }

    @Override
    protected AbstractProcessor getProcessor() {
        return new OnlyOneProcessor(asList(new DirectManytomanyValidator(tableName, manyToMany),
                new MappedbyManytomanyValidator(tableName, manyToMany)));
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ManyToMany.class);
    }

}
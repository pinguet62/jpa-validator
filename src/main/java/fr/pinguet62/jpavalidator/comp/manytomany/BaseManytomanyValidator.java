package fr.pinguet62.jpavalidator.comp.manytomany;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.comp.Validator;

public class BaseManytomanyValidator extends Validator {

    private ManyToMany manyToMany;

    public BaseManytomanyValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected List<Validator> getAvailableNextValidators() {
        return asList(new DirectManytomanyValidator(tableName, manyToMany),
                new MappedbyManytomanyValidator(tableName, manyToMany));
    }

    @Override
    protected void process(Field field) {
        manyToMany = field.getDeclaredAnnotation(ManyToMany.class);

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.isAnnotationPresent(ManyToMany.class);
    }

}
package fr.pinguet62.jpavalidator.comp.onetoone;

import java.lang.reflect.Field;

import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.comp.VException;

public class MappedbyOnetooneValidator extends AbstractOnetooneValidator {

    protected MappedbyOnetooneValidator(String tableName, OneToOne oneToOne) {
        super(tableName, oneToOne);
    }

    @Override
    protected void process(Field field) {
        // Target property
        Class<?> tgtEntity = field.getType();
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, oneToOne.mappedBy());

        // exists
        if (mappedbyField == null)
            throw new VException("mappedBy target property not found");
        // same type
        if (!mappedbyField.getType().equals(field.getDeclaringClass()))
            throw new VException("mappedBy target property is not of same type");

        // Will be processed by direct validator
        // - use @OneToOne
        OneToOne mappedOnetoone = mappedbyField.getDeclaredAnnotation(OneToOne.class);
        if (mappedOnetoone == null)
            throw new VException("mappedBy target property is not annotated with @" + OneToOne.class.getSimpleName());
        // - doesn't use "mappedBy"
        if (!mappedOnetoone.mappedBy().equals(""))
            throw new VException(
                    "mappedBy target property cannot use @" + OneToOne.class.getSimpleName() + "(mappedBy) either");

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return !field.getDeclaredAnnotation(OneToOne.class).mappedBy().equals("");
    }

}
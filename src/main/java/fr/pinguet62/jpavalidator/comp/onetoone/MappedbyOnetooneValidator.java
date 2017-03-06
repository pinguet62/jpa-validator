package fr.pinguet62.jpavalidator.comp.onetoone;

import java.lang.reflect.Field;

import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.comp.MappedbyException;

public class MappedbyOnetooneValidator extends AbstractOnetooneValidator {

    protected MappedbyOnetooneValidator(String tableName, OneToOne oneToOne) {
        super(tableName, oneToOne);
    }

    @Override
    protected void process(Field field) {
        String mappedBy = oneToOne.mappedBy();

        // Target property
        Class<?> tgtEntity = field.getType();
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, mappedBy);

        // exists
        if (mappedbyField == null)
            throw new MappedbyException(mappedBy, "mappedBy target property not found");
        // same type
        if (!mappedbyField.getType().equals(field.getDeclaringClass()))
            throw new MappedbyException(mappedBy, "mappedBy target property is not of same type");

        // Will be processed by direct validator
        // - use @OneToOne
        OneToOne mappedOnetoone = mappedbyField.getDeclaredAnnotation(OneToOne.class);
        if (mappedOnetoone == null)
            throw new MappedbyException(mappedBy,
                    "mappedBy target property is not annotated with @" + OneToOne.class.getSimpleName());
        // - doesn't use "mappedBy"
        if (!mappedOnetoone.mappedBy().equals(""))
            throw new MappedbyException(mappedBy,
                    "mappedBy target property cannot use @" + OneToOne.class.getSimpleName() + "(mappedBy) either");

        // TODO processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return !field.getDeclaredAnnotation(OneToOne.class).mappedBy().equals("");
    }

}
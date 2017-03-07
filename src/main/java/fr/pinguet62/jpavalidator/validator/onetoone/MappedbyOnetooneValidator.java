package fr.pinguet62.jpavalidator.validator.onetoone;

import java.lang.reflect.Field;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.exception.FieldException;
import fr.pinguet62.jpavalidator.exception.MappedbyException;

public class MappedbyOnetooneValidator extends AbstractOnetooneValidator {

    protected MappedbyOnetooneValidator(String tableName, OneToOne oneToOne) {
        super(tableName, oneToOne);
    }

    @Override
    protected void doProcess(Field field) {
        String mappedBy = oneToOne.mappedBy();

        // Target class
        Class<?> tgtEntity = field.getType();
        // - @Entity
        if (!tgtEntity.isAnnotationPresent(Entity.class))
            throw new FieldException(field,
                    "target type " + tgtEntity.getSimpleName() + " must be an @" + Entity.class.getSimpleName());

        // Target property
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, mappedBy);
        // - exists
        if (mappedbyField == null)
            throw new MappedbyException(mappedBy, "mappedBy target property not found");
        // - same type
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
    }

    @Override
    public boolean support(Field field) {
        return !field.getDeclaredAnnotation(OneToOne.class).mappedBy().equals("");
    }

}
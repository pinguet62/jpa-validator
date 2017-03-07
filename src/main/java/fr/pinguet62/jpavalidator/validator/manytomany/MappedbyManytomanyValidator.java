package fr.pinguet62.jpavalidator.validator.manytomany;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.exception.FieldException;
import fr.pinguet62.jpavalidator.exception.MappedbyException;

public class MappedbyManytomanyValidator extends AbstractManytomanyValidator {

    protected MappedbyManytomanyValidator(String tableName, ManyToMany manyToMany) {
        super(tableName, manyToMany);
    }

    @Override
    protected void doProcess(Field field) {
        String mappedBy = manyToMany.mappedBy();

        // Target class
        // - Collection<>
        if (!(Collection.class.isAssignableFrom(field.getType())))
            throw new FieldException(field, "must be a " + Collection.class.getSimpleName());
        Class<?> tgtEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
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
        if (!JpaUtils.getFirstArgumentType(mappedbyField.getGenericType()).equals(field.getDeclaringClass()))
            throw new MappedbyException(mappedBy, "mappedBy target property is not of same type");

        // Will be processed by direct validator
        // - use @ManyToMany
        ManyToMany mappedOnetomany = mappedbyField.getDeclaredAnnotation(ManyToMany.class);
        if (mappedOnetomany == null)
            throw new MappedbyException(mappedBy,
                    "mappedBy target property is not annotated with @" + ManyToMany.class.getSimpleName());
        // - doesn't use "mappedBy"
        if (!mappedOnetomany.mappedBy().equals(""))
            throw new MappedbyException(mappedBy,
                    "mappedBy target property cannot use @" + ManyToMany.class.getSimpleName() + "(mappedBy) either");
    }

    @Override
    public boolean support(Field field) {
        return !field.getDeclaredAnnotation(ManyToMany.class).mappedBy().equals("");
    }

}
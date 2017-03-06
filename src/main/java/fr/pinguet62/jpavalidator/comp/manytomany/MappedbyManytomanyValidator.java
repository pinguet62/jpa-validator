package fr.pinguet62.jpavalidator.comp.manytomany;

import java.lang.reflect.Field;

import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.comp.VException;

public class MappedbyManytomanyValidator extends AbstractManytomanyValidator {

    protected MappedbyManytomanyValidator(String tableName, ManyToMany manyToMany) {
        super(tableName, manyToMany);
    }

    @Override
    protected void process(Field field) {
        // Target property: exists
        Class<?> tgtEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, manyToMany.mappedBy());
        if (mappedbyField == null)
            throw new VException("mappedBy target property not found");

        // Target property: same type
        if (!JpaUtils.getFirstArgumentType(mappedbyField.getGenericType()).equals(field.getDeclaringClass()))
            throw new VException("mappedBy target property is not of same type");

        // Will be processed by direct validator
        // - use @ManyToMany
        ManyToMany mappedOnetomany = mappedbyField.getDeclaredAnnotation(ManyToMany.class);
        if (mappedOnetomany == null)
            throw new VException("mappedBy target property is not annotated with @" + ManyToMany.class.getSimpleName());
        // - doesn't use "mappedBy"
        if (!mappedOnetomany.mappedBy().equals(""))
            throw new VException(
                    "mappedBy target property cannot use @" + ManyToMany.class.getSimpleName() + "(mappedBy) either");

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return !field.getDeclaredAnnotation(ManyToMany.class).mappedBy().equals("");
    }

}
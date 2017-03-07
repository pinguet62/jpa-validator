package fr.pinguet62.jpavalidator.validator.manytomany;

import java.lang.reflect.Field;

import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.exception.MappedbyException;

public class MappedbyManytomanyValidator extends AbstractManytomanyValidator {

    protected MappedbyManytomanyValidator(String tableName, ManyToMany manyToMany) {
        super(tableName, manyToMany);
    }

    @Override
    protected void doProcess(Field field) {
        String mappedBy = manyToMany.mappedBy();

        // Target property: exists
        Class<?> tgtEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, mappedBy);
        if (mappedbyField == null)
            throw new MappedbyException(mappedBy, "mappedBy target property not found");

        // Target property: same type
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
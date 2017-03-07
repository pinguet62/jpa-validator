package fr.pinguet62.jpavalidator.validator.onetomany;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.exception.FieldException;
import fr.pinguet62.jpavalidator.exception.MappedbyException;
import fr.pinguet62.jpavalidator.validator.Validator;

public class OnetomanyValidator extends Validator {

    public OnetomanyValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void doProcess(Field field) {
        OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
        String mappedBy = oneToMany.mappedBy();

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
        if (!mappedbyField.getType().equals(field.getDeclaringClass()))
            throw new MappedbyException(mappedBy, "mappedBy target property is not of same type");

        // Will be processed by direct validator
        // - use @ManyToOne
        if (!mappedbyField.isAnnotationPresent(ManyToOne.class))
            throw new MappedbyException(mappedBy, "mappedBy target property must use @" + ManyToOne.class.getSimpleName());
        // - use @JoinColumn
        if (!mappedbyField.isAnnotationPresent(JoinColumn.class))
            throw new MappedbyException(mappedBy, "mappedBy target property must use @" + JoinColumn.class.getSimpleName());
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

}
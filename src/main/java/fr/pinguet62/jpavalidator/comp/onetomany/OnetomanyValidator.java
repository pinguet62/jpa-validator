package fr.pinguet62.jpavalidator.comp.onetomany;

import java.lang.reflect.Field;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.comp.VException;
import fr.pinguet62.jpavalidator.comp.Validator;

public class OnetomanyValidator extends Validator {

    protected OnetomanyValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void process(Field field) {
        OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
        String mappedBy = oneToMany.mappedBy();

        // Target property: exists
        Class<?> tgtEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, mappedBy);
        if (mappedbyField == null)
            throw new VException("mappedBy target property not found: " + mappedBy);

        // Target property: same type
        if (!mappedbyField.getType().equals(field.getDeclaringClass()))
            throw new VException("mappedBy target property is not of same type");

        // Will be processed by direct validator
        // - use @ManyToOne
        if (!mappedbyField.isAnnotationPresent(ManyToOne.class))
            throw new VException("mappedBy target property must use @" + ManyToOne.class.getSimpleName());
        // - use @JoinColumn
        if (!mappedbyField.isAnnotationPresent(JoinColumn.class))
            throw new VException("mappedBy target property must use @" + JoinColumn.class.getSimpleName());

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

}
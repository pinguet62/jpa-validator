package fr.pinguet62.jpavalidator.validator.manytomany;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class MappedbyManytomanyValidator extends AbstractValidator {

    public MappedbyManytomanyValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        ManyToMany manyToMany = field.getDeclaredAnnotation(ManyToMany.class);

        // Target property: exists
        Class<?> tgtEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, manyToMany.mappedBy());
        if (mappedbyField == null) {
            throwError("mappedBy target property not found");
            return false;
        }

        // Target property: same type
        if (!JpaUtils.getFirstArgumentType(mappedbyField.getGenericType()).equals(entity)) {
            throwError("mappedBy target property is not of same type");
            return false;
        }

        return true;
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotations() {
        return asList(ManyToMany.class);
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ManyToMany.class)
                && !field.getDeclaredAnnotation(ManyToMany.class).mappedBy().equals("");
    }

}
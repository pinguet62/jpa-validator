package fr.pinguet62.jpavalidator.validator.onetoone;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class MappedbyOnetooneValidator extends AbstractValidator {

    public MappedbyOnetooneValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        OneToOne oneToOne = field.getDeclaredAnnotation(OneToOne.class);

        // Target property: exists
        Class<?> tgtEntity = field.getType();
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, oneToOne.mappedBy());
        if (mappedbyField == null) {
            throwError("mappedBy target property not found");
            return false;
        }

        // Target property: same type
        if (!mappedbyField.getType().equals(entity)) {
            throwError("mappedBy target property is not of same type");
            return false;
        }

        return true;
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotations() {
        return asList(OneToOne.class);
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(OneToOne.class) && !field.getDeclaredAnnotation(OneToOne.class).mappedBy().equals("");
    }

}
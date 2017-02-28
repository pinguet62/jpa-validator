package fr.pinguet62.jpavalidator.validator.onetomany;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class OnetomanyValidator extends AbstractValidator {

    public OnetomanyValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
        String mappedBy = oneToMany.mappedBy();

        // Target property: exists
        Class<?> tgtEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, mappedBy);
        if (mappedbyField == null) {
            throwError("mappedBy target property not found: " + mappedBy);
            return false;
        }

        // Target property: same type
        if (!mappedbyField.getType().equals(entity)) {
            throwError("mappedBy target property is not of same type");
            return false;
        }

        // Target property: will be scanned
        if (!mappedbyField.isAnnotationPresent(ManyToOne.class)) {
            throwError("mappedBy target property must use @" + ManyToOne.class.getSimpleName());
            return false;
        }
        if (!mappedbyField.isAnnotationPresent(JoinColumn.class)) {
            throwError("mappedBy target property must use @" + JoinColumn.class.getSimpleName());
            return false;
        }

        return true;
    }

    @Override
    public Collection<Class<? extends Annotation>> getNotYetSupportedAnnotations() {
        return asList(JoinTable.class);
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotations() {
        return asList(OneToMany.class);
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(OneToMany.class) && !field.isAnnotationPresent(JoinColumn.class);
    }

}
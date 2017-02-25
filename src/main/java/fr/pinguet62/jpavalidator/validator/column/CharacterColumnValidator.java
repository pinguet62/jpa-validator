package fr.pinguet62.jpavalidator.validator.column;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class CharacterColumnValidator extends AbstractValidator {

    public CharacterColumnValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        if (!JdbcMetadataChecker.INSTANCE.checkCharacter(tableName, column.name(), column.length())) {
            throwError(format("column has invalid character length: %s.%s", tableName, column.name()));
            return false;
        }
        return true;
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotations() {
        return asList(Column.class, Id.class, GeneratedValue.class, SequenceGenerator.class);
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(Column.class) && field.getType().equals(String.class);
    }

}
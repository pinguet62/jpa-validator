package fr.pinguet62.jpavalidator.validator.id;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class IdValidator extends AbstractValidator {

    public IdValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        // PK
        if (!JdbcMetadataChecker.INSTANCE.checkPrimaryKey(tableName, columnName)) {
            throwError(format("column is not an PK: %s.%s", tableName, columnName));
            return false;
        }

        // Auto-increment
        if (JdbcMetadataChecker.INSTANCE.checkAutoIncrement(tableName, columnName, true)) {
            throwError(format("column is 'auto-increment': %s.%s", tableName, columnName));
            return false;
        }

        return true;
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotations() {
        return asList(Id.class, Column.class);
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(Id.class) && !field.isAnnotationPresent(GeneratedValue.class);
    }

}
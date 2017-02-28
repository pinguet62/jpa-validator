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

public class ColumnValidator extends AbstractValidator {

    public ColumnValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);

        Boolean nullable = column.nullable();
        if (field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(GeneratedValue.class))
            nullable = false; // because set to false for JPA

        // Nullable: field type
        if (nullable && field.getType().isPrimitive()) {
            throwError("@" + Column.class.getSimpleName() + "(nullable=true) but field is primitve");
            return false;
        }

        // Column & Nullable: database constraint
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(tableName, column.name(), nullable)) {
            throwError(format("column doesn't exists or has invalid nullable: %s.%s", tableName, column.name()));
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
        return field.isAnnotationPresent(Column.class);
    }

}
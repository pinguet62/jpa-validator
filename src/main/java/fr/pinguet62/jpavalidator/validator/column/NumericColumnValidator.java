package fr.pinguet62.jpavalidator.validator.column;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class NumericColumnValidator extends AbstractValidator {

    private static final Collection<Class<?>> NUMERIC_TYPES = asList(Float.class, Float.TYPE, BigDecimal.class);

    public NumericColumnValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        // Field type
        if (!NUMERIC_TYPES.contains(field.getType())) {
            throwError(format("invalid field type: %s.%s", tableName, columnName));
            return false;
        }

        // Database constraint
        if (!JdbcMetadataChecker.INSTANCE.checkNumeric(tableName, columnName, column.precision(), column.scale())) {
            throwError(format("column has invalid numeric precision/scale: %s.%s not null", tableName, columnName));
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
        if (!field.isAnnotationPresent(Column.class))
            return false;

        // Field type require precision
        if (NUMERIC_TYPES.contains(field.getType()))
            return true;

        // Precision declarated into @COlumn
        Column column = field.getDeclaredAnnotation(Column.class);
        if (column.precision() != 0 && column.scale() != 0)
            return true;

        return false;
    }

}
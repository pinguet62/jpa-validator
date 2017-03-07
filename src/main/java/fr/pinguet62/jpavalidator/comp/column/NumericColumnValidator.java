package fr.pinguet62.jpavalidator.comp.column;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.Column;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.exception.VException;

public class NumericColumnValidator extends AbstractColumnValidator {

    private static final Collection<Class<?>> NUMERIC_TYPES = asList(Float.class, Float.TYPE, BigDecimal.class);

    public NumericColumnValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        // Field type
        if (!NUMERIC_TYPES.contains(field.getType()))
            throw new VException(Column.class.getSimpleName() + "(precision, scale) can only be used with field of type "
                    + NUMERIC_TYPES.stream().map(Class::getSimpleName).collect(joining(",", "[", "]")));

        // Database constraint
        if (!JdbcMetadataChecker.INSTANCE.checkNumeric(tableName, columnName, column.precision(), column.scale()))
            throw new ColumnException(tableName, columnName, "invalid numeric precision/scale");
    }

    @Override
    public boolean support(Field field) {
        // Field type require precision
        if (NUMERIC_TYPES.contains(field.getType()))
            return true;

        // Precision declarated into @Column
        Column column = field.getDeclaredAnnotation(Column.class);
        if (column.precision() != 0 && column.scale() != 0)
            return true;

        return false;
    }

}
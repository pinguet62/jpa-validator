package fr.pinguet62.jpavalidator.comp.column;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.Column;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;

public class NumericColumnValidator extends AbstractColumnValidator {

    private static final Collection<Class<?>> NUMERIC_TYPES = asList(Float.class, Float.TYPE, BigDecimal.class);

    public NumericColumnValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void process(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        if (!JdbcMetadataChecker.INSTANCE.checkNumeric(tableName, columnName, column.precision(), column.scale()))
            throw new ColumnException(tableName, columnName, "invalid numeric precision/scale");

        // TODO processNext(field);
    }

    @Override
    protected boolean support(Field field) {
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
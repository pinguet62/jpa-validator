package fr.pinguet62.jpavalidator.validator.column;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;

public class TypeColumnValidator extends AbstractColumnValidator {

    private static final Map<Class<?>, String> javaToDb = new HashMap<>();

    static {
        javaToDb.put(Boolean.TYPE, "BIT");
        javaToDb.put(Boolean.class, "BIT");
        javaToDb.put(Byte.TYPE, "");
        javaToDb.put(Byte.class, "");
        javaToDb.put(Short.TYPE, "SMALLINT");
        javaToDb.put(Short.class, "SMALLINT");
        javaToDb.put(Integer.TYPE, "INT");
        javaToDb.put(Integer.class, "INT");
        javaToDb.put(Long.TYPE, "LONG");
        javaToDb.put(Long.class, "LONG");
        javaToDb.put(Float.TYPE, "FLOAT");
        javaToDb.put(Float.class, "FLOAT");
        javaToDb.put(Double.TYPE, "DOUBLE");
        javaToDb.put(Double.class, "DOUBLE");
        javaToDb.put(String.class, "VARCHAR");
        javaToDb.put(java.util.Date.class, "TIMESTAMP");
        javaToDb.put(java.sql.Date.class, "TIMESTAMP");
        javaToDb.put(java.sql.Time.class, "TIMESTAMP");
        javaToDb.put(java.sql.Timestamp.class, "DATE");
        javaToDb.put(java.util.Calendar.class, "DATE");
    }

    public TypeColumnValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        column.name();

        Class<?> propertyType = field.getType();
        String expectedDatabaseType = javaToDb.get(propertyType);
        System.err.println("TODO: check " + propertyType.getSimpleName() + "/" + expectedDatabaseType);
    }

    @Override
    public boolean support(Field field) {
        return true;
    }

}
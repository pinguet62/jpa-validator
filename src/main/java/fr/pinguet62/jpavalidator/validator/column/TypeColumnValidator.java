package fr.pinguet62.jpavalidator.validator.column;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.Struct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;

// https://jcp.org/aboutJava/communityprocess/final/jsr221/index.html
/** @see org.hibernate.type.descriptor.sql.JdbcTypeFamilyInformation */
public class TypeColumnValidator extends AbstractColumnValidator {

    private static final Map<Class<?>, List<String>> javaToDb = new HashMap<>();

    static {
        // JSR-221 JDBC 4.0
        javaToDb.put(String.class, asList("CHAR", "VARCHAR", "LONGVARCHAR", "NCHAR", "NVARCHAR", "LONGNVARCHAR"));
        javaToDb.put(BigDecimal.class, asList("NUMERIC", "DECIMAL"));
        javaToDb.put(Boolean.TYPE, asList("BIT", "BOOLEAN"));
        javaToDb.put(Boolean.class, asList("BIT", "BOOLEAN"));
        javaToDb.put(Byte.class, asList("TINYINT"));
        javaToDb.put(byte.class, asList("TINYINT"));
        javaToDb.put(Short.TYPE, asList("SMALLINT"));
        javaToDb.put(Short.class, asList("SMALLINT"));
        javaToDb.put(Integer.TYPE, asList("INTEGER"));
        javaToDb.put(Integer.class, asList("INTEGER"));
        javaToDb.put(Long.TYPE, asList("BIGINT"));
        javaToDb.put(Long.class, asList("BIGINT"));
        javaToDb.put(Float.TYPE, asList("REAL"));
        javaToDb.put(Float.class, asList("REAL"));
        javaToDb.put(Double.class, asList("FLOAT", "DOUBLE"));
        javaToDb.put(Double.TYPE, asList("FLOAT", "DOUBLE"));
        javaToDb.put(Byte[].class, asList("BINARY", "VARBINARY", "LONGVARBINARY"));
        javaToDb.put(byte[].class, asList("BINARY", "VARBINARY", "LONGVARBINARY"));
        javaToDb.put(java.sql.Date.class, asList("DATE"));
        javaToDb.put(java.sql.Time.class, asList("TIME"));
        javaToDb.put(java.sql.Timestamp.class, asList("TIMESTAMP"));
        javaToDb.put(Clob.class, asList("CLOB"));
        javaToDb.put(Blob.class, asList("BLOB"));
        javaToDb.put(Array.class, asList("ARRAY"));
        // javaToDb.put(.class, "DISTINCT");
        javaToDb.put(Struct.class, asList("STRUCT"));
        javaToDb.put(Ref.class, asList("REF"));
        javaToDb.put(URL.class, asList("DATALINK"));
        // javaToDb.put(.class, "JAVA_OBJECT");
        javaToDb.put(RowId.class, asList("ROWID"));
        javaToDb.put(NClob.class, asList("NCLOB"));
        javaToDb.put(SQLXML.class, asList("SQLXML"));
    }

    public TypeColumnValidator(String tableName, Column column) {
        super(tableName, column);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        Class<?> propertyType = field.getType();
        List<String> databaseTypes = javaToDb.get(propertyType);

        if (!databaseTypes.stream()
                .anyMatch(databaseType -> JdbcMetadataChecker.INSTANCE.checkType(tableName, columnName, databaseType)))
            throw new ColumnException(tableName, columnName, "invalid column type");
    }

    @Override
    public boolean support(Field field) {
        return true;
    }

}
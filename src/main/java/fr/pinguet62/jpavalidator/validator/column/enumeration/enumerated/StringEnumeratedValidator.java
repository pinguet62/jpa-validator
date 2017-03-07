package fr.pinguet62.jpavalidator.validator.column.enumeration.enumerated;

import static javax.persistence.EnumType.STRING;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.validator.Validator;

public class StringEnumeratedValidator extends Validator {

    private static final EnumType enumType = STRING;

    public StringEnumeratedValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        String dbType = "VARCHAR";
        if (!JdbcMetadataChecker.INSTANCE.checkType(tableName, columnName, dbType))
            throw new ColumnException(tableName, columnName,
                    "@" + Enumerated.class.getSimpleName() + "(" + enumType.name() + ") require " + dbType + " database type");
    }

    @Override
    public boolean support(Field field) {
        return field.getDeclaredAnnotation(Enumerated.class).value().equals(enumType);
    }

}
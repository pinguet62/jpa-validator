package fr.pinguet62.jpavalidator.comp.column.enumeration.enumerated;

import static javax.persistence.EnumType.ORDINAL;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;
import fr.pinguet62.jpavalidator.comp.Validator;

public class OrdinalEnumeratedValidator extends Validator {

    private static final EnumType enumType = ORDINAL;

    public OrdinalEnumeratedValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void process(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        String dbType = "INT";
        if (!JdbcMetadataChecker.INSTANCE.checkType(tableName, columnName, dbType))
            throw new ColumnException(tableName, columnName,
                    "@" + Enumerated.class.getSimpleName() + "(" + enumType.name() + ") require " + dbType + " database type");

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.getDeclaredAnnotation(Enumerated.class).value().equals(enumType);
    }

}
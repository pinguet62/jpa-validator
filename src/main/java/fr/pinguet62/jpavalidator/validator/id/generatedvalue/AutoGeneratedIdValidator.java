package fr.pinguet62.jpavalidator.validator.id.generatedvalue;

import static javax.persistence.GenerationType.AUTO;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;

public class AutoGeneratedIdValidator extends AbstractGeneratedvalueValidator {

    public AutoGeneratedIdValidator(String tableName, GeneratedValue generatedValue) {
        super(tableName, generatedValue);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        // Managed by JPA, not by database
        if (JdbcMetadataChecker.INSTANCE.checkAutoIncrement(tableName, columnName, true))
            throw new ColumnException(tableName, columnName, "is 'auto-increment'");
    }

    @Override
    public boolean support(Field field) {
        return field.getDeclaredAnnotation(GeneratedValue.class).strategy().equals(AUTO);
    }

}
package fr.pinguet62.jpavalidator.validator.id.generatedvalue;

import static javax.persistence.GenerationType.SEQUENCE;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;

import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.exception.VException;

public class SequenceGeneratedIdValidator extends AbstractGeneratedvalueValidator {

    public SequenceGeneratedIdValidator(String tableName, GeneratedValue generatedValue) {
        super(tableName, generatedValue);
    }

    @Override
    protected void doProcess(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        // Auto-increment
        if (!JdbcMetadataChecker.INSTANCE.checkAutoIncrement(tableName, columnName, true))
            throw new ColumnException(tableName, columnName, "is not 'auto-increment'");

        String generator = generatedValue.generator();
        SequenceGenerator sequenceGenerator = field.getDeclaredAnnotation(SequenceGenerator.class);
        String sequenceGeneratorName = sequenceGenerator.name();
        // Generator: same key
        if (!generator.equals(sequenceGeneratorName))
            throw new VException(
                    "@" + GeneratedValue.class.getSimpleName() + "(generator=" + generator + ") doesn't match with @"
                            + SequenceGenerator.class.getSimpleName() + "(name=" + sequenceGeneratorName + ")");

        String sequenceName = sequenceGenerator.sequenceName();

        // Sequence: exists
        if (!JdbcMetadataChecker.INSTANCE.checkSequence(sequenceName))
            throw new VException("sequence doesn't exists: " + sequenceName);

        // Generated by: sequence
        if (!JdbcMetadataChecker.INSTANCE.checkAutoIncrementBySequence(tableName, columnName, sequenceName))
            throw new ColumnException(tableName, columnName, "is not 'generated as sequence " + sequenceName + "'");
        if (!JdbcMetadataChecker.INSTANCE.checkAutoIncrementByIdentity(tableName, columnName))
            throw new ColumnException(tableName, columnName, "is not 'generated as identity' by sequenceName");
    }

    @Override
    public boolean support(Field field) {
        return field.getDeclaredAnnotation(GeneratedValue.class).strategy().equals(SEQUENCE);
    }

}
package fr.pinguet62.jpavalidator.comp.id;

import static javax.persistence.GenerationType.SEQUENCE;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;

import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;
import fr.pinguet62.jpavalidator.comp.VException;
import fr.pinguet62.jpavalidator.comp.Validator;

public class SequenceGeneratedIdValidator extends Validator {

    public SequenceGeneratedIdValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void process(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        String columnName = column.name();

        // Auto-increment
        if (!JdbcMetadataChecker.INSTANCE.checkAutoIncrement(tableName, columnName, true))
            throw new ColumnException(tableName, columnName, "is not 'auto-increment'");

        String generator = field.getDeclaredAnnotation(GeneratedValue.class).generator();
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

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.isAnnotationPresent(GeneratedValue.class)
                && field.getDeclaredAnnotation(GeneratedValue.class).strategy().equals(SEQUENCE)
                && field.isAnnotationPresent(SequenceGenerator.class);
    }

}
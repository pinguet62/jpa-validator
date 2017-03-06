package fr.pinguet62.jpavalidator.comp.manytoone;

import java.lang.reflect.Field;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;
import fr.pinguet62.jpavalidator.comp.Validator;

public class ManytooneValidator extends Validator {

    protected ManytooneValidator(String tableName) {
        super(tableName);
    }

    @Override
    protected void process(Field field) {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        String srcColumnName = joinColumn.name();

        Class<?> tgtEntity = field.getType();
        String tgtTableName = JpaUtils.getTableName(tgtEntity);

        // Column & Nullable: database constraint
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(tableName, srcColumnName, joinColumn.nullable()))
            throw new ColumnException(tableName, srcColumnName, "invalid nullable constraint");

        // FK
        if (!JdbcMetadataChecker.INSTANCE.checkForeignKey(tableName, srcColumnName, tgtTableName))
            throw new ColumnException(tableName, srcColumnName, "no FK from to " + tgtTableName);

        processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

}
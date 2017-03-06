package fr.pinguet62.jpavalidator.comp.onetoone;

import java.lang.reflect.Field;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;

public class DirectOnetooneValidator extends AbstractOnetooneValidator {

    protected DirectOnetooneValidator(String tableName, OneToOne oneToOne) {
        super(tableName, oneToOne);
    }

    @Override
    protected void process(Field field) {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        String srcColumnName = joinColumn.name();

        // Column: exists
        if (!JdbcMetadataChecker.INSTANCE.checkColumnExists(tableName, srcColumnName))
            throw new ColumnException(tableName, srcColumnName, "column doesn't exists");
        // Nullable: constraint
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(tableName, srcColumnName, joinColumn.nullable()))
            throw new ColumnException(tableName, srcColumnName, "invalid nullable constraint");

        // Target
        Class<?> tgtEntity = field.getType();
        String tgtTableName = JpaUtils.getTableName(tgtEntity);

        // FK
        if (JdbcMetadataChecker.INSTANCE.checkForeignKey(tableName, srcColumnName, tgtTableName) == false)
            throw new ColumnException(tableName, srcColumnName, "no FK to " + tgtTableName);

        // TODO processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.getDeclaredAnnotation(OneToOne.class).mappedBy().equals("");
    }

}
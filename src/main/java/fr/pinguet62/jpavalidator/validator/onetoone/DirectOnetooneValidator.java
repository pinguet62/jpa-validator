package fr.pinguet62.jpavalidator.validator.onetoone;

import java.lang.reflect.Field;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.exception.FieldException;

public class DirectOnetooneValidator extends AbstractOnetooneValidator {

    protected DirectOnetooneValidator(String tableName, OneToOne oneToOne) {
        super(tableName, oneToOne);
    }

    @Override
    protected void doProcess(Field field) {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn == null)
            throw new FieldException(field, "must be annotated with @" + JoinColumn.class.getSimpleName());

        String srcColumnName = joinColumn.name();

        // Target class
        Class<?> tgtEntity = field.getType();
        // - @Entity
        if (!tgtEntity.isAnnotationPresent(Entity.class))
            throw new FieldException(field,
                    "target type " + tgtEntity.getSimpleName() + " must be an @" + Entity.class.getSimpleName());

        // Column: exists
        if (!JdbcMetadataChecker.INSTANCE.checkColumnExists(tableName, srcColumnName))
            throw new ColumnException(tableName, srcColumnName, "column doesn't exists");
        // Nullable: constraint
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(tableName, srcColumnName, joinColumn.nullable()))
            throw new ColumnException(tableName, srcColumnName, "invalid nullable constraint");

        String tgtTableName = JpaUtils.getTableName(tgtEntity);

        // FK
        if (JdbcMetadataChecker.INSTANCE.checkForeignKey(tableName, srcColumnName, tgtTableName) == false)
            throw new ColumnException(tableName, srcColumnName, "no FK to " + tgtTableName);
    }

    @Override
    public boolean support(Field field) {
        return field.getDeclaredAnnotation(OneToOne.class).mappedBy().equals("");
    }

}
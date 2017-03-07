package fr.pinguet62.jpavalidator.validator.manytoone;

import java.lang.reflect.Field;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.exception.FieldException;
import fr.pinguet62.jpavalidator.validator.Validator;

public class ManytooneValidator extends Validator {

    public ManytooneValidator(String tableName) {
        super(tableName);
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

        String tgtTableName = JpaUtils.getTableName(tgtEntity);

        // FK
        if (!JdbcMetadataChecker.INSTANCE.checkForeignKey(tableName, srcColumnName, tgtTableName))
            throw new ColumnException(tableName, srcColumnName, "no FK from to " + tgtTableName);

        // Column & Nullable: database constraint
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(tableName, srcColumnName, joinColumn.nullable()))
            throw new ColumnException(tableName, srcColumnName, "invalid nullable constraint");
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

}
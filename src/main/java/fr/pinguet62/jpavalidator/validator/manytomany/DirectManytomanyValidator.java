package fr.pinguet62.jpavalidator.validator.manytomany;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ColumnException;
import fr.pinguet62.jpavalidator.exception.FieldException;
import fr.pinguet62.jpavalidator.exception.NotYetImplementedException;

public class DirectManytomanyValidator extends AbstractManytomanyValidator {

    protected DirectManytomanyValidator(String tableName, ManyToMany manyToMany) {
        super(tableName, manyToMany);
    }

    @Override
    protected void doProcess(Field field) {
        JoinTable joinTable = field.getDeclaredAnnotation(JoinTable.class);
        if (joinTable == null)
            throw new FieldException(field, "must be annotated with @" + JoinTable.class.getSimpleName());

        String linkTableName = joinTable.name();

        // Target class
        // - Collection<>
        if (!(Collection.class.isAssignableFrom(field.getType())))
            throw new FieldException(field, "must be a " + Collection.class.getSimpleName());
        Class<?> tgtEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
        // - @Entity
        if (!tgtEntity.isAnnotationPresent(Entity.class))
            throw new FieldException(field,
                    "target type " + tgtEntity.getSimpleName() + " must be an @" + Entity.class.getSimpleName());

        // Check 2 FKs
        {
            // Direct
            if (joinTable.joinColumns().length != 1)
                throw new NotYetImplementedException(field.toString() + ": @" + JoinTable.class + "(joinColumns.length > 1)");
            JoinColumn joinColumn = joinTable.joinColumns()[0];

            if (JdbcMetadataChecker.INSTANCE.checkForeignKey(linkTableName, joinColumn.name(), tableName) == false)
                throw new ColumnException(linkTableName, joinColumn.name(), "no FK to " + tableName);
        }
        {
            // Reverse
            if (joinTable.inverseJoinColumns().length != 1)
                throw new NotYetImplementedException(
                        field.toString() + ": @" + JoinTable.class + "(inverseJoinColumns.length > 1)");
            JoinColumn joinColumn = joinTable.inverseJoinColumns()[0];
            Class<?> targetEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
            String targetTableName = JpaUtils.getTableName(targetEntity);

            if (JdbcMetadataChecker.INSTANCE.checkForeignKey(linkTableName, joinColumn.name(), targetTableName) == false)
                throw new ColumnException(linkTableName, joinColumn.name(), "no FK to " + targetTableName);
        }
    }

    @Override
    public boolean support(Field field) {
        return field.getDeclaredAnnotation(ManyToMany.class).mappedBy().equals("");
    }

}
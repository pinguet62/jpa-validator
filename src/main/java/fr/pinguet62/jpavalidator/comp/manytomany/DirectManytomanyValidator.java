package fr.pinguet62.jpavalidator.comp.manytomany;

import java.lang.reflect.Field;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.NotYetImplemented;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.comp.ColumnException;

public class DirectManytomanyValidator extends AbstractManytomanyValidator {

    protected DirectManytomanyValidator(String tableName, ManyToMany manyToMany) {
        super(tableName, manyToMany);
    }

    @Override
    protected void process(Field field) {
        JoinTable joinTable = field.getDeclaredAnnotation(JoinTable.class);
        String linkTableName = joinTable.name();

        // Check 2 FKs
        {
            // Direct
            if (joinTable.joinColumns().length != 1)
                throw new NotYetImplemented(field.toString() + ": @" + JoinTable.class + "(joinColumns.length > 1)");
            JoinColumn joinColumn = joinTable.joinColumns()[0];

            if (JdbcMetadataChecker.INSTANCE.checkForeignKey(linkTableName, joinColumn.name(), tableName) == false)
                throw new ColumnException(linkTableName, joinColumn.name(), "no FK to " + tableName);
        }
        {
            // Reverse
            if (joinTable.inverseJoinColumns().length != 1)
                throw new NotYetImplemented(field.toString() + ": @" + JoinTable.class + "(inverseJoinColumns.length > 1)");
            JoinColumn joinColumn = joinTable.inverseJoinColumns()[0];
            Class<?> targetEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
            String targetTableName = JpaUtils.getTableName(targetEntity);

            if (JdbcMetadataChecker.INSTANCE.checkForeignKey(linkTableName, joinColumn.name(), targetTableName) == false)
                throw new ColumnException(linkTableName, joinColumn.name(), "no FK to " + targetTableName);
        }

        // TODO processNext(field);
    }

    @Override
    protected boolean support(Field field) {
        return field.getDeclaredAnnotation(ManyToMany.class).mappedBy().equals("");
    }

}
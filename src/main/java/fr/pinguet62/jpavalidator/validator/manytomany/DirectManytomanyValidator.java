package fr.pinguet62.jpavalidator.validator.manytomany;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumns;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.NotYetImplemented;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class DirectManytomanyValidator extends AbstractValidator {

    public DirectManytomanyValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        JoinTable joinTable = field.getDeclaredAnnotation(JoinTable.class);
        String linkTableName = joinTable.name();

        // Check 2 FKs
        {
            // Direct
            if (joinTable.joinColumns().length != 1)
                throw new NotYetImplemented(field.toString() + ": @" + JoinTable.class + "(joinColumns.length > 1)");
            JoinColumn joinColumn = joinTable.joinColumns()[0];

            if (JdbcMetadataChecker.INSTANCE.checkForeignKey(linkTableName, joinColumn.name(), tableName) == false) {
                throwError(format("no FK from %s.%s to %s", linkTableName, joinColumn.name(), tableName));
                return false;
            }
        }
        {
            // Reverse
            if (joinTable.inverseJoinColumns().length != 1)
                throw new NotYetImplemented(field.toString() + ": @" + JoinTable.class + "(inverseJoinColumns.length > 1)");
            JoinColumn joinColumn = joinTable.inverseJoinColumns()[0];
            Class<?> targetEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
            String targetTableName = JpaUtils.getTableName(targetEntity);

            if (JdbcMetadataChecker.INSTANCE.checkForeignKey(linkTableName, joinColumn.name(), targetTableName) == false) {
                throwError(format("no FK from %s.%s to %s", linkTableName, joinColumn.name(), targetTableName));
                return false;
            }
        }

        return true;
    }

    @Override
    public Collection<Class<? extends Annotation>> getNotYetSupportedAnnotations() {
        return asList(PrimaryKeyJoinColumns.class, ManyToOne.class);
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotations() {
        return asList(ManyToMany.class, JoinTable.class);
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ManyToMany.class)
                && field.getDeclaredAnnotation(ManyToMany.class).mappedBy().equals("");
    }

}
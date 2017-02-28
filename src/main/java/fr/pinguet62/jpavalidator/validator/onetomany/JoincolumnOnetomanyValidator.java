package fr.pinguet62.jpavalidator.validator.onetomany;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

/** Only supported by {@code JPA 2.x} version. */
public class JoincolumnOnetomanyValidator extends AbstractValidator {

    public JoincolumnOnetomanyValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
        String mappedBy = oneToMany.mappedBy();

        // Target property: exists
        Class<?> tgtEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
        Field mappedbyField = JpaUtils.getTargetField(tgtEntity, mappedBy);
        if (mappedbyField == null) {
            throwError("mappedBy target property not found: " + mappedBy);
            return false;
        }

        // Target property: same type
        if (!mappedbyField.getType().equals(entity)) {
            throwError("mappedBy target property is not of same type");
            return false;
        }

        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        String srcTableName = JpaUtils.getTableName(tgtEntity);
        String srcColumnName = joinColumn.name();
        String tgtTableName = tableName; // TODO joinColumn.referencedColumnName();

        // Column & Nullable: database constraint
        if (!JdbcMetadataChecker.INSTANCE.checkColumn(srcTableName, srcColumnName, joinColumn.nullable())) {
            throwError(format("column doesn't exists or has invalid nullable: %s.%s", tableName, srcColumnName));
            return false;
        }

        // FK
        if (!JdbcMetadataChecker.INSTANCE.checkForeignKey(srcTableName, srcColumnName, tgtTableName)) {
            throwError(format("no FK from %s.%s to %s", srcTableName, srcColumnName, tgtTableName));
            return false;
        }

        return true;
    }

    @Override
    public Collection<Class<? extends Annotation>> getNotYetSupportedAnnotations() {
        return asList(JoinTable.class);
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotations() {
        return asList(OneToMany.class, JoinColumn.class);
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(OneToMany.class) && field.isAnnotationPresent(JoinColumn.class);
    }

}
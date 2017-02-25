package fr.pinguet62.jpavalidator.validator.manytoone;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class ManytooneValidator extends AbstractValidator {

    public ManytooneValidator(Class<?> entity, String tableName) {
        super(entity, tableName);
    }

    @Override
    protected boolean doProcess(Field field) {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        String srcColumnName = joinColumn.name();

        Class<?> tgtEntity = field.getType();
        String tgtTableName = JpaUtils.getTableName(tgtEntity);

        if (JdbcMetadataChecker.INSTANCE.checkForeignKey(tableName, srcColumnName, tgtTableName) == false) {
            throwError(format("no FK from %s.%s to %s", tableName, srcColumnName, tgtTableName));
            return false;
        }

        return true;
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotations() {
        return asList(ManyToOne.class, JoinColumn.class);
    }

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

}
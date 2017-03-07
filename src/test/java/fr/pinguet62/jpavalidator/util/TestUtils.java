package fr.pinguet62.jpavalidator.util;

import static fr.pinguet62.jpavalidator.util.runner.SchemaRunner.DATABASE;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.sql.SQLException;

import fr.pinguet62.jpavalidator.Counter;
import fr.pinguet62.jpavalidator.JpaUtils;
import fr.pinguet62.jpavalidator.database.HsqldbMetadataChecker;
import fr.pinguet62.jpavalidator.database.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.VException;
import fr.pinguet62.jpavalidator.exception.ValidationException;
import fr.pinguet62.jpavalidator.validator.FieldValidator;

public class TestUtils {

    // public static void runCheck(Class<?>... entities) throws ValidationException {
    // HsqldbMetadataChecker checker;
    // try {
    // checker = new HsqldbMetadataChecker(DATABASE);
    // } catch (SQLException e) {
    // throw new RuntimeException(e);
    // }
    // new Processor(checker).accept(asList(entities));
    // }

    // public static void runCheck(Class<?>... entities) throws ValidationException {
    // try {
    // JdbcMetadataChecker.INSTANCE = new HsqldbMetadataChecker(DATABASE);
    // } catch (SQLException e) {
    // throw new RuntimeException(e);
    // }
    // new Processor().accept(asList(entities));
    // }

    public static void assertErrorWithColumn(ValidationException exception, Class<?> entity, String message) {
        assertTrue(exception.getErrors().get(0), exception.getErrors().get(0).contains(message));
    }

    public static void assertErrorWithColumn(ValidationException exception, Class<?> entity, String tableName,
            String columnName) {
        assertTrue(exception.getErrors().get(0), exception.getErrors().get(0).contains(tableName));
        assertTrue(exception.getErrors().get(0), exception.getErrors().get(0).contains(columnName));
    }

    public static void runCheck(Class<?>... entities) throws ValidationException {
        Counter.N = 0;

        try {
            JdbcMetadataChecker.INSTANCE = new HsqldbMetadataChecker(DATABASE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (Class<?> entity : entities)
            for (Field field : JpaUtils.getAnnotatedFields(entity)) {
                String tableName = JpaUtils.getTableName(entity);
                try {
                    new FieldValidator(tableName).process(field);
                } catch (VException e) {
                    ValidationException validationException = new ValidationException();
                    validationException.getErrors().add(e.toString());
                    throw validationException;
                }
            }
    }

}
package fr.pinguet62.jpavalidator.util;

import static fr.pinguet62.jpavalidator.util.runner.SchemaRunner.DATABASE;
import static java.util.Arrays.asList;

import java.sql.SQLException;

import fr.pinguet62.jpavalidator.Processor;
import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.checker.HsqldbMetadataChecker;

public class TestUtils {

    public static void runCheck(Class<?>... entities) throws ValidationException {
        HsqldbMetadataChecker checker;
        try {
            checker = new HsqldbMetadataChecker(DATABASE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        new Processor(checker).accept(asList(entities));
    }

}
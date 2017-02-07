package fr.pinguet62.jpavalidator;

import static fr.pinguet62.jpavalidator.util.SchemaRunner.DATABASE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.pinguet62.jpavalidator.checker.Checker;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.exception.ValidationException;
import fr.pinguet62.jpavalidator.model.column.ColumnLength;
import fr.pinguet62.jpavalidator.model.column.ColumnNullable;
import fr.pinguet62.jpavalidator.model.column.ColumnNumeric;
import fr.pinguet62.jpavalidator.model.ok.OkModel;
import fr.pinguet62.jpavalidator.util.Script;

public class FullITTest {

    private final Checker checker;

    public FullITTest() throws SQLException {
        checker = new JdbcMetadataChecker(DATABASE);
    }

    private void runCheck(List<Class<?>> entitites) throws ValidationException {
        new Processor(checker).accept(asList(ColumnLength.class));
    }

    @Test
    @Script("/column_length.sql")
    public void test_column_length() {
        try {
            runCheck(asList(ColumnLength.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors());
        }
    }

    @Test
    @Script("/column_nullable.sql")
    public void test_column_nullable() {
        try {
            runCheck(asList(ColumnNullable.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(2, e.getErrors());
        }
    }

    @Test
    @Script("/column_numeric.sql")
    public void test_column_numeric() {
        try {
            runCheck(asList(ColumnNumeric.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors());
        }
    }

    @Test
    @Ignore
    public void todo() {
        runCheck(new EntityScanner(OkModel.class.getPackage().getName()).get());
    }

}
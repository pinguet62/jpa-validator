package fr.pinguet62.jpavalidator;

import static fr.pinguet62.jpavalidator.util.SchemaRunner.DATABASE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.checker.Checker;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.model.IdTable;
import fr.pinguet62.jpavalidator.model.column.ColumnLength;
import fr.pinguet62.jpavalidator.model.column.ColumnNullable;
import fr.pinguet62.jpavalidator.model.column.ColumnNumeric;
import fr.pinguet62.jpavalidator.model.manytomany.Employee;
import fr.pinguet62.jpavalidator.model.manytomany.Project;
import fr.pinguet62.jpavalidator.util.SchemaRunner;
import fr.pinguet62.jpavalidator.util.Script;

@RunWith(SchemaRunner.class)
public class FullITTest {

    private final Checker checker;

    public FullITTest() throws SQLException {
        checker = new JdbcMetadataChecker(DATABASE);
    }

    private void runCheck(List<Class<?>> entitites) throws ValidationException {
        new Processor(checker).accept(entitites);
    }

    @Test
    @Script("/column_length.sql")
    public void test_column_length() {
        try {
            runCheck(asList(ColumnLength.class));
            fail();
        } catch (ValidationException e) {
            // print(e);
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script("/column_nullable.sql")
    public void test_column_nullable() {
        try {
            runCheck(asList(ColumnNullable.class));
            fail();
        } catch (ValidationException e) {
            // print(e);
            assertEquals(2, e.getErrors().size());
        }
    }

    @Test
    @Script("/column_numeric.sql")
    public void test_column_numeric() {
        try {
            runCheck(asList(ColumnNumeric.class));
            fail();
        } catch (ValidationException e) {
            // print(e);
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script("/id.sql")
    public void test_id() {
        try {
            runCheck(asList(IdTable.class));
            fail();
        } catch (ValidationException e) {
            // print(e);
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script("/manytomany.sql")
    public void test_manytomany() {
        try {
            runCheck(asList(Employee.class, Project.class));
            // TODO Bad mapping
        } catch (ValidationException e) {
            // print(e);
            fail();
        }
    }

    @Test
    @Script("/manytoone.sql")
    public void test_manytoone() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.manytoone.Person.class,
                    fr.pinguet62.jpavalidator.model.manytoone.Car.class));
            fail();
        } catch (ValidationException e) {
            // print(e);
            assertEquals(2, e.getErrors().size());
        }
    }

    @Test
    @Script("/onetoone.sql")
    public void test_onetoone() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.onetoone.Person.class,
                    fr.pinguet62.jpavalidator.model.onetoone.Address.class));
            // TODO Bad mapping
        } catch (ValidationException e) {
            // print(e);
            fail();
        }
    }

}
package fr.pinguet62.jpavalidator;

import static fr.pinguet62.jpavalidator.util.ValidationExceptionAssertions.assertContainsMessage;
import static fr.pinguet62.jpavalidator.util.runner.SchemaRunner.DATABASE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.model.manytomany.Employee;
import fr.pinguet62.jpavalidator.model.manytomany.Project;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

/**
 * Each {@link Test} has {@link Entity} model mapping into separated package.<br>
 * The naming rule of method is corresponding to the package.
 */
@RunWith(SchemaRunner.class)
public class FullITTest {

    private final HsqldbMetadataChecker checker;

    public FullITTest() throws SQLException {
        checker = new HsqldbMetadataChecker(DATABASE);
    }

    private void runCheck(List<Class<?>> entitites) throws ValidationException {
        new Processor(checker).accept(entitites);
    }

    @Test
    @Script("/column_length.sql")
    public void test_column_length_invalid() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.column.length.invalid.ColumnLength.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "invalid");
        }
    }

    @Test
    @Script("/column_length.sql")
    public void test_column_length_ok() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.column.length.ok.ColumnLength.class));
    }

    @Test
    @Script("/column_nullable.sql")
    public void test_column_nullable_invalid() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.column.nullable.invalid.ColumnNullable.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(2, e.getErrors().size());
            assertContainsMessage(e, "mandatory_error");
            assertContainsMessage(e, "optional_error");
        }
    }

    @Test
    @Script("/column_nullable.sql")
    public void test_column_nullable_ok() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.column.nullable.ok.ColumnNullable.class));
    }

    @Test
    @Script("/column_numeric.sql")
    public void test_column_numeric_invalid() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.column.numeric.invalid.ColumnNumeric.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "invalid");
        }
    }

    @Test
    @Script("/column_numeric.sql")
    public void test_column_numeric_ok() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.column.numeric.ok.ColumnNumeric.class));
    }

    @Test
    @Script("/id_generated_identity.sql")
    public void test_id_generated_identity_invalid() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.id.generated.identity.invalid.IdIdentity.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "invalid");
        }
    }

    @Test
    @Script("/id_generated_identity.sql")
    public void test_id_generated_identity_ok() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.id.generated.identity.ok.IdIdentity.class));
    }

    @Test
    @Script("/id_generated_sequence.sql")
    public void test_id_generated_sequence_invalid() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.id.generated.sequence.invalid.IdSequence.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "invalid");
        }
    }

    @Test
    @Script("/id_generated_sequence.sql")
    public void test_id_generated_sequence_ok() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.id.generated.sequence.ok.IdSequence.class));
    }

    @Test
    @Script("/id_simple.sql")
    public void test_id_simple_invalid() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.id.simple.invalid.IdSample.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "invalid");
        }
    }

    @Test
    @Script("/id_simple.sql")
    public void test_id_simple_ok() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.id.simple.ok.IdSample.class));
    }

    @Test
    @Script("/manytomany.sql")
    public void test_manytomany_invalid() {
        throw new NotYetImplemented();
    }

    @Test
    @Script("/manytomany.sql")
    public void test_manytomany_ok() {
        runCheck(asList(Employee.class, Project.class));
    }

    @Test
    @Script("/manytoone.sql")
    public void test_manytoone_invalidTarget() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.manytoone.invalidTarget.Car.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "invalid");
        }
    }

    @Test
    @Script("/manytoone.sql")
    public void test_manytoone_invalidType() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.manytoone.invalidType.Car.class));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "invalid");
        }
    }

    @Test
    @Script("/manytoone.sql")
    public void test_manytoone_ok() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.manytoone.ok.Car.class));
    }

    /**
     * The reverse {@link OneToMany#mappedBy()} target {@link Field} is not of the same type as the current
     * {@link Class}.
     */
    @Test
    @Script("/onetomany.sql")
    public void test_onetomany_badReverseTargetMappedbyField() {
        try {
            runCheck(asList(fr.pinguet62.jpavalidator.model.onetomany.badReverseTargetMappedbyField.Person.class));
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script("/onetomany.sql")
    public void test_onetomany_ok() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.onetomany.ok.Person.class));
    }

    @Test
    @Script("/onetoone.sql")
    public void test_onetoone() {
        runCheck(asList(fr.pinguet62.jpavalidator.model.onetoone.Person.class,
                fr.pinguet62.jpavalidator.model.onetoone.Address.class));
    }

}
package fr.pinguet62.jpavalidator.cases.column;

import static fr.pinguet62.jpavalidator.util.TestUtils.assertErrorWithColumn;
import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static org.junit.Assert.fail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.exception.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class NumericColumnTest {

    @Entity
    @Table(name = "SAMPLE")
    static class Sample {
        @Column(name = "COL", precision = 5, scale = 2)
        Float field;
    }

    @Test
    @Script("create table SAMPLE ( COL numeric(5,2) );")
    public void test() {
        runCheck(Sample.class);
    }

    @Test
    @Script("create table SAMPLE ( COL numeric(99,42) );")
    public void test_constraint_invalid() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Sample.class, "SAMPLE", "COL");
        }
    }

    @Test
    @Script("create table SAMPLE ( COL numeric(5,2) );")
    public void test_property_type_invalid() {
        @Entity
        @Table(name = "SAMPLE")
        class InvalidFieldType {
            @Column(name = "COL", precision = 5, scale = 2)
            Object field;
        }

        try {
            runCheck(InvalidFieldType.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, InvalidFieldType.class, "field");
        }
    }

}
package fr.pinguet62.jpavalidator.model.column;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static fr.pinguet62.jpavalidator.util.ValidationExceptionAssertions.assertContainsMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class ColumnLengthTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Column(name = "COL", length = 42)
        String field;
    }

    @Test
    @Script("create table SAMPLE ( COL varchar(99) );")
    public void test_error() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "COL");
        }
    }

    @Test
    @Script("create table SAMPLE ( COL varchar(42) );")
    public void test_ok() {
        runCheck(Sample.class);
    }

}
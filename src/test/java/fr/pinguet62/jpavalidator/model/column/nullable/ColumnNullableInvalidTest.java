package fr.pinguet62.jpavalidator.model.column.nullable;

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
public class ColumnNullableInvalidTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Column(name = "MANDATORY", nullable = true)
        Integer mandatory;

        @Column(name = "OPTIONAL", nullable = false)
        Integer optional;
    }

    @Test
    @Script("create table SAMPLE ( \n" + //
            "    MANDATORY int not null, \n" + //
            "    OPTIONAL int null \n" + //
            ");")
    public void test() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(2, e.getErrors().size());
            assertContainsMessage(e, "MANDATORY");
            assertContainsMessage(e, "OPTIONAL");
        }
    }

}
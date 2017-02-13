package fr.pinguet62.jpavalidator.model.id.simple;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static fr.pinguet62.jpavalidator.util.ValidationExceptionAssertions.assertContainsMessage;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class IdSampleInvalidTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Id
        @Column(name = "INVALID", nullable = false)
        Integer id;
    }

    @Test
    @Script("create table SAMPLE ( ID int /* not null*/ primary key );")
    public void test() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertTrue(e.getErrors().size() >= 1);
            assertContainsMessage(e, "INVALID");
        }
    }

}
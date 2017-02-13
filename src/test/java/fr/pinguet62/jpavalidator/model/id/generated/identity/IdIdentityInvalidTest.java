package fr.pinguet62.jpavalidator.model.id.generated.identity;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static fr.pinguet62.jpavalidator.util.ValidationExceptionAssertions.assertContainsMessage;
import static javax.persistence.GenerationType.IDENTITY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class IdIdentityInvalidTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Id
        @GeneratedValue(strategy = IDENTITY)
        @Column(name = "ID", nullable = true)
        Integer id;
    }

    @Test
    @Script("create table SAMPLE ( ID int /* not null*/ primary key );")
    public void test() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "ID");
        }
    }

}
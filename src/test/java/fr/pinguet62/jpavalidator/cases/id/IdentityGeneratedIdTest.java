package fr.pinguet62.jpavalidator.cases.id;

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
public class IdentityGeneratedIdTest {

    @Entity
    @Table(name = "SAMPLE")
    static class Sample {
        @Id
        @GeneratedValue(strategy = IDENTITY)
        @Column(name = "PK", nullable = true)
        Integer id;
    }

    @Test
    @Script("create table SAMPLE ( PK int generated by default as identity primary key );")
    public void test() {
        runCheck(Sample.class);
    }

    @Test
    @Script("create table SAMPLE ( PK int primary key );")
    public void test_generatedAsInvalid_not() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "PK");
        }
    }

    @Test
    @Script({ "create sequence SAMPLE_SEQUENCE;", //
            "create table SAMPLE ( PK int generated by default as sequence SAMPLE_SEQUENCE primary key );" })
    public void test_generatedAsInvalid_sequence() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "PK");
        }
    }

}
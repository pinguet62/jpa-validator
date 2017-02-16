package fr.pinguet62.jpavalidator.model.id;

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
public class IdTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Id
        @Column(name = "ID", nullable = false /* primary key: NOT NULL by default */)
        Integer id;
    }

    @Test
    @Script("create table SAMPLE ( INVALID int primary key );")
    public void test_error_badColumnMapping() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertTrue(e.getErrors().size() >= 1);
            assertContainsMessage(e, "id");
            assertContainsMessage(e, "ID");
        }
    }

    @Test
    @Script("create table SAMPLE ( ID int generated by default as identity primary key );")
    public void test_error_generatedAsIdentity() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertTrue(e.getErrors().size() >= 1);
            assertContainsMessage(e, "id");
        }
    }

    @Test
    @Script({ "create sequence SAMPLE_SEQUENCE;",
            "create table SAMPLE ( ID int generated by default as sequence SAMPLE_SEQUENCE primary key );" })
    public void test_error_generatedAsSequence() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertTrue(e.getErrors().size() >= 1);
            assertContainsMessage(e, "id");
        }
    }

    @Test
    @Script("create table SAMPLE ( ID int primary key );")
    public void test_ok() {
        runCheck(Sample.class);
    }

}
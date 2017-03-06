package fr.pinguet62.jpavalidator.cases.id;

import static fr.pinguet62.jpavalidator.util.TestUtils.assertErrorWithColumn;
import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static javax.persistence.GenerationType.SEQUENCE;
import static org.junit.Assert.fail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class SequenceGeneratedIdTest {

    @Entity
    @Table(name = "SAMPLE")
    static class Sample {
        @Id
        @GeneratedValue(strategy = SEQUENCE, generator = "key")
        @SequenceGenerator(name = "key", sequenceName = "SAMPLE_SEQUENCE")
        @Column(name = "PK", nullable = true)
        int id;
    }

    @Test
    @Script({ "create sequence SAMPLE_SEQUENCE;", //
            "create table SAMPLE ( PK int generated by default as sequence SAMPLE_SEQUENCE primary key );" })
    public void test() {
        runCheck(Sample.class);
    }

    @Test
    @Script({ "create sequence SAMPLE_SEQUENCE;", //
            "create table SAMPLE ( PK int generated by default as identity primary key );" })
    public void test_generatedAs_identity_invalid() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Sample.class, "SAMPLE", "PK");
        }
    }

    @Test
    @Script({ "create sequence SAMPLE_SEQUENCE;", //
            "create table SAMPLE ( PK int primary key );" })
    public void test_generatedAs_not_invalid() {
        try {
            runCheck(Sample.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Sample.class, "SAMPLE", "PK");
        }
    }

    @Test
    @Script({ "create sequence SAMPLE_SEQUENCE;", //
            "create table SAMPLE ( PK int generated by default as identity primary key );" })
    public void test_generatorName_invalid() {
        @Entity
        @Table(name = "SAMPLE")
        class BadGeneratorName {
            @Id
            @GeneratedValue(strategy = SEQUENCE, generator = "key")
            @SequenceGenerator(name = "invalid", sequenceName = "SAMPLE_SEQUENCE")
            @Column(name = "PK", nullable = true)
            int id;
        }

        try {
            runCheck(BadGeneratorName.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Sample.class, "invalid");
        }
    }

}
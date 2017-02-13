package fr.pinguet62.jpavalidator.model.manytoone;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static fr.pinguet62.jpavalidator.util.ValidationExceptionAssertions.assertContainsMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class ManytooneTypeInvalidTest {

    @Entity
    @Table(name = "CAR")
    public static class Car {
        @ManyToOne
        @JoinColumn(name = "FK")
        Person person;
    }

    @Entity
    @Table(name = "PERSON")
    public static class Person {}

    @Test
    @Script({ "create table PERSON ( PK character varying(99) /*not null*/ primary key );",
            "create table CAR ( FK character varying(42) references PERSON (PK) );" })
    public void test() {
        try {
            runCheck(Car.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "FK");
        }
    }

}
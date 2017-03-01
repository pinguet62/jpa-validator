package fr.pinguet62.jpavalidator.cases.onetomany;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static fr.pinguet62.jpavalidator.util.ValidationExceptionAssertions.assertContainsMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class OnetomanyTest {

    @Entity
    @Table(name = "CAR")
    static class Car {
        @ManyToOne
        @JoinColumn(name = "FK")
        Person person;
    }

    @Entity
    @Table(name = "PERSON")
    static class Person {
        @OneToMany(mappedBy = "person")
        List<Car> cars;
    }

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( FK int references PERSON (PK) );" })
    public void test() {
        runCheck(Person.class);
    }

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( FK int references PERSON (PK) );" })
    public void test_mappedby_property_type_invalid() {
        @Entity
        @Table(name = "CAR")
        class InvalidMappedbyTypeCar {
            @ManyToOne
            @JoinColumn(name = "FK")
            String person;
        }

        @Entity
        @Table(name = "PERSON")
        class InvalidMappedbyTypePerson {
            @OneToMany(mappedBy = "person")
            List<InvalidMappedbyTypeCar> cars;
        }

        try {
            runCheck(InvalidMappedbyTypePerson.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( FK int references PERSON (PK) );" })
    public void test_mappedby_property_unknown() {
        @Entity
        @Table(name = "PERSON")
        class UnknownMappedbyProperty {
            @OneToMany(mappedBy = "person")
            List<Car> cars;
        }

        try {
            runCheck(UnknownMappedbyProperty.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "cars");
        }
    }

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( FK int references PERSON (PK) );" })
    public void test_property_type_invalid() {
        @Entity
        @Table(name = "PERSON")
        class InvalidType {
            @OneToMany(mappedBy = "person")
            List<String> cars;
        }

        try {
            runCheck(InvalidType.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertContainsMessage(e, "cars");
        }
    }

}
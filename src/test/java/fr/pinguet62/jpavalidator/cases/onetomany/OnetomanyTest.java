package fr.pinguet62.jpavalidator.cases.onetomany;

import static fr.pinguet62.jpavalidator.util.TestUtils.assertErrorWithColumn;
import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.exception.ValidationException;
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
    public void test_mappedby() {
        runCheck(Person.class);
    }

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( FK int references PERSON (PK) );" })
    public void test_mappedby_property_type_invalid() {
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
            // assertErrorWithColumn(e, UnknownMappedbyProperty.class, "cars");
            assertErrorWithColumn(e, UnknownMappedbyProperty.class, "person");
        }
    }

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( FK int references PERSON (PK) );" })
    public void test_mappedby_property_unknown() {
        @Entity
        @Table(name = "PERSON")
        class UnknownMappedbyProperty {
            @OneToMany(mappedBy = "unknown")
            List<Car> cars;
        }

        try {
            runCheck(UnknownMappedbyProperty.class);
            fail();
        } catch (ValidationException e) {
            // assertErrorWithColumn(e, UnknownMappedbyProperty.class, "cars");
            assertErrorWithColumn(e, UnknownMappedbyProperty.class, "unknown");
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
            assertErrorWithColumn(e, InvalidType.class, "cars");
        }
    }

}
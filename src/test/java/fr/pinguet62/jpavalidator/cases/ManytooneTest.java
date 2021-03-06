package fr.pinguet62.jpavalidator.cases;

import static fr.pinguet62.jpavalidator.util.TestUtils.assertErrorWithColumn;
import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static org.junit.Assert.fail;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.exception.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class ManytooneTest {

    @Entity
    @Table(name = "CAR")
    static class Car {
        @ManyToOne
        @JoinColumn(name = "FK")
        Person person;
    }

    @Entity
    @Table(name = "PERSON")
    static class Person {}

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( FK int references PERSON (PK) );" })
    public void test() {
        runCheck(Car.class);
    }

    @Test
    @Script({ "create table PERSON ( ID_PERSON character varying(99) primary key );", //
            "create table BAD ( ID_BAD character varying(99) primary key );", //
            "create table CAR ( FK character varying(99) references BAD (ID_BAD) );" })
    public void test_fk_invalid() {
        try {
            runCheck(Car.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Car.class, "CAR", "FK");
        }
    }

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( INVALID int references PERSON (PK) );" })
    public void test_joinColumn_name_invalid() {
        try {
            runCheck(Car.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Car.class, "CAR", "FK");
        }
    }

    @Test
    @Script({ "create table PERSON ( PK int primary key );", //
            "create table CAR ( FK int not null references PERSON (PK) );" }) // bad constraint
    public void test_joinColumn_nullable_invalid() {
        try {
            runCheck(Car.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Car.class, "CAR", "FK");
        }
    }

    @Test
    @Script({ "create table PERSON ( PK character varying(99) primary key );", //
            "create table CAR ( FK character varying(42) references PERSON (PK) );" })
    public void test_propertyType_invalid() {
        @Entity
        @Table(name = "CAR")
        class InvalidType {
            @ManyToOne
            @JoinColumn(name = "FK")
            String person;
        }

        try {
            runCheck(InvalidType.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, InvalidType.class, "person");
        }
    }

}
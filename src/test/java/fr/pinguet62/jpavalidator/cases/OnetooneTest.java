package fr.pinguet62.jpavalidator.cases;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class OnetooneTest {

    @Entity
    @Table(name = "ADDRESS")
    public static class Address {
        @OneToOne(mappedBy = "address")
        Person person;
    }

    @Entity
    @Table(name = "ADDRESS")
    public static class MappedbyInvalidType {
        @OneToOne(mappedBy = "address")
        String person;
    }

    @Entity
    @Table(name = "ADDRESS")
    public static class MappedbyUnknownProperty {
        @OneToOne(mappedBy = "unknown")
        String person;
    }

    @Entity
    @Table(name = "PERSON")
    public static class Person {
        @OneToOne
        @JoinColumn(name = "ADDRESS_ID")
        Address address;
    }

    @Test
    @Script({ "create table ADDRESS ( ID_ADDRESS int primary key, );", //
            "create table PERSON ( \n" + //
                    "    ID_PERSON int primary key, \n" + //
                    "    ADDRESS_ID int references ADDRESS (ID_ADDRESS) \n" + //
                    ");" })
    public void test_joinColumn() {
        runCheck(Person.class);
    }

    @Test
    @Script({ "create table ADDRESS ( ID_ADDRESS int primary key, );", //
            "create table PERSON ( \n" + //
                    "    ID_PERSON int primary key, \n" + //
                    "    ADDRESS_ID int /*references ADDRESS (ID_ADDRESS)*/ \n" + //
                    ");" })
    public void test_joinColumn_noFk() {
        try {
            runCheck(Person.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script({ "create table ADDRESS ( ID_ADDRESS int primary key, );", //
            "create table PERSON ( \n" + //
                    "    ID_PERSON int primary key, \n" + //
                    "    ADDRESS_ID int references ADDRESS (ID_ADDRESS) \n" + //
                    ");" })
    public void test_mappedby() {
        runCheck(Address.class);
    }

    @Test
    @Script({ "create table ADDRESS ( ID_ADDRESS int primary key, );", //
            "create table PERSON ( \n" + //
                    "    ID_PERSON int primary key, \n" + //
                    "    ADDRESS_ID int references ADDRESS (ID_ADDRESS) \n" + //
                    ");" })
    public void test_mappedby_invalidType() {
        try {
            runCheck(MappedbyInvalidType.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script({ "create table ADDRESS ( ID_ADDRESS int primary key, );", //
            "create table PERSON ( \n" + //
                    "    ID_PERSON int primary key, \n" + //
                    "    ADDRESS_ID int references ADDRESS (ID_ADDRESS) \n" + //
                    ");" })
    public void test_mappedby_unknownProperty() {
        try {
            runCheck(MappedbyUnknownProperty.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

}
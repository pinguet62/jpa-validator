package fr.pinguet62.jpavalidator.cases;

import static fr.pinguet62.jpavalidator.util.TestUtils.assertErrorWithColumn;
import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static org.junit.Assert.fail;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.exception.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class OnetooneTest {

    @Entity
    @Table(name = "ADDRESS")
    static class Address {
        @OneToOne(mappedBy = "address")
        Person person;
    }

    @Entity
    @Table(name = "PERSON")
    static class Person {
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
                    "    ADDRESS_ID int \n" + //
                    ");" })
    public void test_joinColumn_fk_invalid() {
        try {
            runCheck(Person.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Person.class, "PERSON", "ADDRESS_ID");
        }
    }

    @Test
    @Script({ "create table ADDRESS ( ID_ADDRESS int primary key, );", //
            "create table PERSON ( \n" + //
                    "    ID_PERSON int primary key, \n" + //
                    "    INVALID int references ADDRESS (ID_ADDRESS) \n" + //
                    ");" })
    public void test_joinColumn_name_invalid() {
        try {
            runCheck(Person.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Person.class, "PERSON", "ADDRESS_ID");
        }
    }

    @Test
    @Script({ "create table ADDRESS ( ID_ADDRESS int primary key, );", //
            "create table PERSON ( \n" + //
                    "    ID_PERSON int primary key, \n" + //
                    "    ADDRESS_ID int not null references ADDRESS (ID_ADDRESS) \n" + //
                    ");" })
    public void test_joinColumn_nullable_invalid() {
        try {
            runCheck(Person.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Person.class, "PERSON", "ADDRESS_ID");
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
    public void test_mappedby_property_type_invalid() {
        @Entity
        @Table(name = "PERSON")
        class PersonInvalidMappedbyType {
            @OneToOne
            @JoinColumn(name = "ADDRESS_ID")
            String address;
        }

        @Entity
        @Table(name = "ADDRESS")
        class AddressInvalidMappedbyType {
            @OneToOne(mappedBy = "address")
            PersonInvalidMappedbyType person;
        }

        try {
            runCheck(AddressInvalidMappedbyType.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, AddressInvalidMappedbyType.class, "mappedBy");
            assertErrorWithColumn(e, AddressInvalidMappedbyType.class, "address");
        }
    }

    @Test
    @Script({ "create table ADDRESS ( ID_ADDRESS int primary key, );", //
            "create table PERSON ( \n" + //
                    "    ID_PERSON int primary key, \n" + //
                    "    ADDRESS_ID int references ADDRESS (ID_ADDRESS) \n" + //
                    ");" })
    public void test_mappedby_property_unknown() {
        @Entity
        @Table(name = "ADDRESS")
        class UnknownMappedbyProperty {
            @OneToOne(mappedBy = "unknown")
            Person person;
        }

        try {
            runCheck(UnknownMappedbyProperty.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, UnknownMappedbyProperty.class, "unknown");
        }
    }

}
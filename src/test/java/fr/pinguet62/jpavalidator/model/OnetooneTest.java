package fr.pinguet62.jpavalidator.model;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

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
    @Table(name = "PERSON")
    public static class Person {
        @OneToOne
        @JoinColumn(name = "ADDRESS_ID")
        Address address;
    }

    @Test
    @Script({
            "create table PERSON ( \n" + //
                    "    ID_PERSON int /*not null*/ primary key, \n" + //
                    "    ADDRESS_ID int /*references ADDRESS (ID_ADDRESS)*/ \n" + //
                    ");",
            "create table ADDRESS ( \n" + //
                    "    ID_ADDRESS int /*not null*/ primary key, \n" + //
                    "    PERSON_ID int /*references PERSON (ID_PERSON)*/ \n" + //
                    ");",
            "alter table PERSON add foreign key (ADDRESS_ID) references ADDRESS (ID_ADDRESS);",
            "alter table ADDRESS add foreign key (PERSON_ID) references PERSON (ID_PERSON);" })
    public void test() {
        runCheck(Person.class, Address.class);
    }

}
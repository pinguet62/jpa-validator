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

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class JoincolumnOnetomanyTest {

    @Entity
    @Table(name = "CAR")
    static class Car {
        @ManyToOne
        Person person;
    }

    @Entity
    @Table(name = "PERSON")
    static class Person {
        @OneToMany(mappedBy = "person")
        @JoinColumn(name = "FK", referencedColumnName = "PK")
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
            "create table CAR ( FK int );" })
    public void test_fk_invalid() {
        try {
            runCheck(Person.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Person.class, "SAMPLE", "PK");
        }
    }

}
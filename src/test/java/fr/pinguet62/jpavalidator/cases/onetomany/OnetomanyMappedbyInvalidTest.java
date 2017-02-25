package fr.pinguet62.jpavalidator.cases.onetomany;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static org.junit.Assert.assertEquals;

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

/**
 * The reverse {@link OneToMany#mappedBy()} target {@link Field} is not of the same type as the current {@link Class}.
 */
@RunWith(SchemaRunner.class)
public class OnetomanyMappedbyInvalidTest {

    @Entity
    @Table(name = "CAR")
    public static class Car {
        @ManyToOne
        @JoinColumn(name = "FK")
        String person;
    }

    @Entity
    @Table(name = "PERSON")
    public static class Person {
        @OneToMany(mappedBy = "person")
        List<Car> cars;
    }

    @Test
    @Script({ "create table PERSON ( PK int /*not null*/ primary key );",
            "create table CAR ( FK int references PERSON (PK) );" })
    public void test() {
        try {
            runCheck(Person.class);
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

}
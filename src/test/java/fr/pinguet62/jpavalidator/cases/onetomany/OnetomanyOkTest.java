package fr.pinguet62.jpavalidator.cases.onetomany;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class OnetomanyOkTest {

    @Entity
    @Table(name = "CAR")
    public static class Car {
        @ManyToOne
        @JoinColumn(name = "FK")
        Person person;
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
        runCheck(Person.class);
    }

}

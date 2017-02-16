package fr.pinguet62.jpavalidator.model.id.generated.identity;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class IdIdentityOkTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Id
        @GeneratedValue(strategy = IDENTITY)
        @Column(name = "ID", nullable = true)
        Integer id;
    }

    @Test
    @Script("create table SAMPLE ( ID int /* not null*/ generated by default as identity primary key );")
    public void test() {
        runCheck(Sample.class);
    }

}
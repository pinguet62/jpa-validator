package fr.pinguet62.jpavalidator.cases.column;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.exception.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class TypeColumnTest {

    @Entity
    @Table(name = "SAMPLE")
    static class Sample {
        @Column(name = "TYPE_NUMERIC", precision = 128/* HSQLDB default constraint */)
        BigDecimal typeBigDecimal;

        @Column(name = "TYPE_DOUBLE", precision = 128/* HSQLDB default constraint */)
        Double typeFloat;

        @Column(name = "TYPE_VARCHAR")
        String typeString;
    }

    @Test
    @Script("create table SAMPLE ( \n" + //
            "    TYPE_NUMERIC numeric, \n" + //
            "    TYPE_DOUBLE double, \n" + //
            "    TYPE_VARCHAR varchar(255) \n" + //
            ");")
    public void test() {
        runCheck(Sample.class);
    }

    @Test(expected = ValidationException.class)
    @Script("create table SAMPLE ( \n" + //
            "    TYPE_NUMERIC boolean, \n" + //
            "    TYPE_DOUBLE boolean, \n" + //
            "    TYPE_VARCHAR boolean \n" + //
            ");")
    public void test_invalid() {
        runCheck(Sample.class);
    }

}
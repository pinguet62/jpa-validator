package fr.pinguet62.jpavalidator.model.column.nullable;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class ColumnNullableOkTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Column(name = "MANDATORY", nullable = false)
        Integer mandatory;

        @Column(name = "OPTIONAL", nullable = true)
        Integer optional;
    }

    @Test
    @Script("create table SAMPLE ( \n" + //
            "    MANDATORY int not null, \n" + //
            "    OPTIONAL int null \n" + //
            ");")
    public void test() {
        runCheck(Sample.class);
    }

}
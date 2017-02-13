package fr.pinguet62.jpavalidator.model.column.numeric;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class ColumnNumericOkTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Column(name = "COL", precision = 5, scale = 2)
        float field;
    }

    @Test
    @Script("create table SAMPLE ( COL numeric(5,2) );")
    public void test() {
        runCheck(Sample.class);
    }

}
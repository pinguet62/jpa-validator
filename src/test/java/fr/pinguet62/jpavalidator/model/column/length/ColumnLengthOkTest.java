package fr.pinguet62.jpavalidator.model.column.length;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class ColumnLengthOkTest {

    @Entity
    @Table(name = "SAMPLE")
    public static class Sample {
        @Column(name = "COL", length = 99)
        String field;
    }

    @Test
    @Script("create table SAMPLE ( COL varchar(99) );")
    public void test() {
        runCheck(Sample.class);
    }

}
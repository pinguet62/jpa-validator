package fr.pinguet62.jpavalidator;

import java.util.List;

import org.junit.Test;

import fr.pinguet62.jpavalidator.checker.Checker;
import fr.pinguet62.jpavalidator.checker.JdbcMetadataChecker;
import fr.pinguet62.jpavalidator.model.ModelConfig;

public class FullITTest {

    @Test
    public void test() throws Exception {
        String database = "";
        Checker checker = new JdbcMetadataChecker(database);

        String basePackage = ModelConfig.class.getPackage().getName();
        List<Class<?>> entities = new EntityScanner(basePackage).get();

        new Processor(checker).accept(entities);
    }

}
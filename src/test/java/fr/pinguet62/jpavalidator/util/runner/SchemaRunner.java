package fr.pinguet62.jpavalidator.util.runner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/** JUnit {@link Runner} who get {@link Script} and execute the SQL file before each {@link Test} method. */
public class SchemaRunner extends BlockJUnit4ClassRunner {

    public static final String DATABASE = "jdbc:hsqldb:mem:jpavalidator";

    private final Connection connection;

    public SchemaRunner(Class<?> clas) throws InitializationError, SQLException {
        super(clas);
        connection = DriverManager.getConnection(DATABASE);
    }

    /**
     * Execute {@link #resetDatabase(String)} before test method if {@link Script} is present.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        try {
            Script script = method.getAnnotation(Script.class);
            if (script != null)
                resetDatabase(script.value());
        } catch (Throwable e) {
            return new Fail(e);
        }

        return super.methodBlock(method);
    }

    /**
     * Reset the database and execute the SQL file to initialize the new schema.
     *
     * @param sqlScripts {@link Script#value()}
     * @throws SQLException Error executing SQL.
     */
    private void resetDatabase(String[] sqlScripts) throws SQLException, IOException {
        connection.createStatement().execute("DROP SCHEMA PUBLIC CASCADE;");
        for (String sqlScript : sqlScripts)
            connection.createStatement().executeQuery(sqlScript);
    }

}
package fr.pinguet62.jpavalidator.util;

import static java.nio.charset.Charset.defaultCharset;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class SchemaRunner extends BlockJUnit4ClassRunner {

    public static final String DATABASE = "jdbc:hsqldb:mem:jpavalidator";

    private final Connection connection;

    public SchemaRunner(Class<?> clas) throws InitializationError, SQLException {
        super(clas);
        connection = DriverManager.getConnection(DATABASE);
    }

    /**
     * Reset the database and execute the SQL file to initialize the new schema.
     *
     * @param path {@link Script#value()}
     */
    private void resetDatabase(String path) {
        try {
            connection.createStatement().execute("DROP SCHEMA PUBLIC CASCADE;");

            String sql;
            try (InputStream inputStream = getClass().getResourceAsStream(path)) {
                sql = IOUtils.toString(inputStream, defaultCharset());
            }

            // Workaroud to execute command 1 by 1
            // TODO Split with external secured lib
            for (String s : sql.split(";")) {
                s = s.replaceAll("^(\r?\n)*", "");
                if (s.isEmpty())
                    continue;
                s += ";";
                System.out.println(s);
                connection.createStatement().executeQuery(s);
                System.out.println("--------------------------");
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        Script script = method.getAnnotation(Script.class);
        if (script == null)
            throw new UnsupportedOperationException(
                    "Method " + method + " must be annotated with " + Script.class.getSimpleName());
        String path = script.value();
        resetDatabase(path);

        super.runChild(method, notifier);
    }

}
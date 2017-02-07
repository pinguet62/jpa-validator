package fr.pinguet62.jpavalidator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Test;

public class MetadataTest {

    public static void print(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCols = rsmd.getColumnCount();
        for (int i = 1; i <= numCols; i++) {
            if (i > 1)
                System.out.print(" - ");
            System.out.print(rsmd.getColumnLabel(i));
        }
        System.out.println();

        while (rs.next()) {
            for (int i = 1; i <= numCols; i++) {
                if (i > 1)
                    System.out.print(" - ");
                System.out.print(rs.getString(i));
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("==================================================");
        System.out.println();
    }

    @Test
    public void test() throws SQLException {
        String database = "jdbc:postgresql://ec2-184-73-165-195.compute-1.amazonaws.com:5432/d54utom7qlqe15?user=lveshvmnnklrze&password=v0hrZTKbqmS4PvwRKAQd9Q_L5f&currentSchema=lib&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        Connection connection = DriverManager.getConnection(database);
        DatabaseMetaData metadata = connection.getMetaData();

        print(metadata.getTables(connection.getCatalog(), connection.getSchema(), "address", null));
        // print(metadata.getColumns(connection.getCatalog(), connection.getSchema(), "%", null));
        // print(metadata.getPrimaryKeys(connection.getCatalog(), connection.getSchema(), "address"));
        // print(metadata.getImportedKeys(connection.getCatalog(), connection.getSchema(), null));
    }

}
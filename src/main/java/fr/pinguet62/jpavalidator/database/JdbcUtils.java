package fr.pinguet62.jpavalidator.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class JdbcUtils {

    public static void executeAndPrint(Connection connection, String sql, String... args) throws SQLException {
        PreparedStatement query = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++)
            query.setString(i + 1, args[i]);
        ResultSet rs = query.executeQuery();
        print(rs);
    }

    /** Find all <b>system table</b> who any column matches to {@link Predicate}. */
    public static List<String> findSchemaTable(Connection connection, Predicate<Object> predicate) {
        List<String> tables = new ArrayList<>();
        try {
            ResultSet rsTable = connection.createStatement().executeQuery("select * from INFORMATION_SCHEMA.SYSTEM_TABLES");
            while (rsTable.next()) {
                String table = rsTable.getString("TABLE_NAME");
                ResultSet resultSet = connection.createStatement().executeQuery("select * from INFORMATION_SCHEMA." + table);
                ResultSetMetaData md = resultSet.getMetaData();
                while (resultSet.next())
                    for (int i = 1; i <= md.getColumnCount(); i++)
                        if (predicate.test(resultSet.getObject(i)))
                            tables.add(table);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return tables;
    }

    public static void print(ResultSet resultSet) throws SQLException {
        System.out.println("==================================================");
        String sep = " - ";
        ResultSetMetaData md = resultSet.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); ++i)
            System.out.print(md.getColumnName(i) + sep);
        System.out.println();
        while (resultSet.next()) {
            for (int i = 1; i <= md.getColumnCount(); ++i)
                System.out.print(resultSet.getObject(i) + sep);
            System.out.println();
        }
        System.out.println("==================================================");
    }

    public static void test(Connection connection) throws SQLException {
        List<String> tables = JdbcUtils.findSchemaTable(connection,
                col -> col != null && col.toString().contains("SAMPLE_SEQUENCE"));
        for (String table : tables) {
            System.out.println("----- " + table);
            executeAndPrint(connection, "select * from information_schema." + table);
            System.out.println();
        }
    }

}
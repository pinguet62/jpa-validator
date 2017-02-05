package fr.pinguet62.jpavalidator.checker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementation of {@link Checker} for PostreSQL. */
public class PostgresChecker extends Checker {

    static List<Map<String, Object>> resultSetToMap(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData md = resultSet.getMetaData();
        while (resultSet.next()) {
            Map<String, Object> columns = new HashMap<>();
            for (int i = 1; i <= md.getColumnCount(); ++i)
                columns.put(md.getColumnName(i), resultSet.getObject(i));
            rows.add(columns);
        }
        return rows;
    }

    private final List<Map<String, Object>> metaColumns;

    private final List<Map<String, Object>> metaFk;

    public PostgresChecker(String databaseUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(databaseUrl);
        metaColumns = resultSetToMap(connection.createStatement().executeQuery(//
                "select * " //
                        + "from information_schema.columns " //
                        + "where table_schema = '" + connection.getSchema() + "';"));
        metaFk = resultSetToMap(connection.createStatement().executeQuery(//
                "select " + //
                        "    information_schema.key_column_usage.table_name \"src_table\", " + //
                        "    information_schema.key_column_usage.column_name \"src_column\", " + //
                        "    information_schema.constraint_column_usage.table_name \"tgt_table\", " + //
                        "    information_schema.constraint_column_usage.column_name \"tgt_column\", " + //
                        "    information_schema.table_constraints.constraint_type " + //
                        "from information_schema.key_column_usage " + //
                        "join information_schema.constraint_column_usage " + //
                        "  on information_schema.constraint_column_usage.constraint_name = information_schema.key_column_usage.constraint_name "
                        + //
                        "join information_schema.table_constraints " + //
                        "  on information_schema.table_constraints.constraint_name = information_schema.key_column_usage.constraint_name "
                        + //
                        "where information_schema.constraint_column_usage.table_schema = '" + connection.getSchema() + "';"));
    }

    @Override
    public boolean checkCharacter(String tableName, String columnName, int length) {
        Map<String, Object> columnDefinition = metaColumns.stream()
                .filter(row -> row.get("table_name").equals(tableName.toLowerCase()))
                .filter(row -> row.get("column_name").equals(columnName.toLowerCase())).findFirst().get();
        return ((int) columnDefinition.get("character_maximum_length")) == 42;
    }

    @Override
    public boolean checkColumn(String tableName, String columnName, boolean nullable) {
        Map<String, Object> columnDefinition = metaColumns.stream()
                .filter(row -> row.get("table_name").equals(tableName.toLowerCase()))
                .filter(row -> row.get("column_name").equals(columnName.toLowerCase())).findFirst().get();
        return ((String) columnDefinition.get("is_nullable")).equals(nullable ? "YES" : "NO");
    }

    @Override
    public boolean checkForeignKey(String tableName, String columnName, String tgtTableName) {
        return metaFk.stream().filter(row -> row.get("src_table").equals(tableName.toLowerCase()))
                .filter(row -> row.get("src_column").equals(columnName.toLowerCase()))
                .filter(row -> row.get("tgt_table").equals(tgtTableName.toLowerCase()))
                .filter(row -> row.get("constraint_type").equals("FOREIGN KEY")).findFirst().isPresent();
    }

    @Override
    public boolean checkId(String tableName, String columnName) {
        return metaFk.stream().filter(row -> row.get("src_table").equals(tableName.toLowerCase()))
                .filter(row -> row.get("src_column").equals(columnName.toLowerCase()))
                .filter(row -> row.get("constraint_type").equals("PRIMARY KEY")).findFirst().isPresent();
    }

    @Override
    public boolean checkNumeric(String tableName, String columnName, int precision, int scale) {
        Map<String, Object> columnDefinition = metaColumns.stream()
                .filter(row -> row.get("table_name").equals(tableName.toLowerCase()))
                .filter(row -> row.get("column_name").equals(columnName.toLowerCase())).findFirst().get();
        return columnDefinition.get("numeric_precision").equals(precision)
                && columnDefinition.get("numeric_scale").equals(scale);
    }

    @Override
    public boolean checkTable(String tableName) {
        return metaColumns.stream().anyMatch(row -> row.get("table_name").equals(tableName.toLowerCase()));
    }

}
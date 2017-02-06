package fr.pinguet62.jpavalidator.checker;

import static java.sql.DatabaseMetaData.columnNoNulls;
import static java.sql.DatabaseMetaData.columnNullable;
import static java.sql.DatabaseMetaData.columnNullableUnknown;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementation of {@link Checker} for PostreSQL. */
public class JdbcMetadataChecker extends Checker {

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

    private final String catalog;

    private final DatabaseMetaData metadata;

    private final String schema;

    public JdbcMetadataChecker(String databaseUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(databaseUrl);
        catalog = connection.getCatalog();
        schema = connection.getSchema();
        metadata = connection.getMetaData();
    }

    @Override
    public boolean checkCharacter(String tableName, String columnName, int length) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toLowerCase(), columnName.toLowerCase());
            return resultSet.next() && resultSet.getInt("COLUMN_SIZE") == length;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkColumn(String tableName, String columnName, boolean nullable) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toLowerCase(), columnName.toLowerCase());
            if (!resultSet.next())
                return false;
            if (resultSet.getInt("NULLABLE") == columnNullableUnknown)
                throw new RuntimeException("???");
            return resultSet.getInt("NULLABLE") == (nullable ? columnNullable : columnNoNulls);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkForeignKey(String tableName, String columnName, String tgtTableName) {
        try {
            ResultSet resultSet = metadata.getImportedKeys(catalog, schema, tableName.toLowerCase());
            while (resultSet.next())
                if (resultSet.getString("FKTABLE_NAME").toLowerCase().equals(tableName.toLowerCase())
                        && resultSet.getString("FKCOLUMN_NAME").toLowerCase().equals(columnName.toLowerCase())
                        && resultSet.getString("PKTABLE_NAME").toLowerCase().equals(tgtTableName.toLowerCase()))
                    return true;
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkId(String tableName, String columnName) {
        try {
            ResultSet resultSet = metadata.getPrimaryKeys(catalog, schema, tableName.toLowerCase());
            while (resultSet.next())
                if (resultSet.getString("COLUMN_NAME").toLowerCase().equals(columnName.toLowerCase()))
                    return true;
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkNumeric(String tableName, String columnName, int precision, int scale) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toLowerCase(), columnName.toLowerCase());
            return resultSet.next() && resultSet.getInt("COLUMN_SIZE") == precision
                    && resultSet.getInt("DECIMAL_DIGITS") == scale;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkTable(String tableName) {
        try {
            return metadata.getTables(catalog, schema, tableName.toLowerCase(), null).next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
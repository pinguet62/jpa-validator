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

/** Implementation of {@link Checker} for PostreSQL. */
public class JdbcMetadataChecker implements Checker {

    static void print(ResultSet resultSet) throws SQLException {
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
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toUpperCase(), columnName.toUpperCase());
            return resultSet.next() && resultSet.getInt("COLUMN_SIZE") == length;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkColumn(String tableName, String columnName, boolean nullable) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toUpperCase(), columnName.toUpperCase());
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
            ResultSet fkResultSet = metadata.getImportedKeys(catalog, schema, tableName.toUpperCase());
            while (fkResultSet.next())
                // FK exists
                if (fkResultSet.getString("FKCOLUMN_NAME").equals(columnName.toUpperCase())
                        && fkResultSet.getString("PKTABLE_NAME").equals(tgtTableName.toUpperCase())) {
                    // Target PK column name
                    ResultSet targetPkResultSet = metadata.getPrimaryKeys(catalog, schema, tgtTableName.toUpperCase());
                    targetPkResultSet.next();
                    String targetColumnName = targetPkResultSet.getString("COLUMN_NAME");
                    // Check type
                    ResultSet type1 = metadata.getColumns(catalog, schema, tableName.toUpperCase(), columnName.toUpperCase());
                    ResultSet type2 = metadata.getColumns(catalog, schema, tgtTableName.toUpperCase(), targetColumnName);
                    return
                    // exist
                    (type1.next() && type2.next())
                            // type
                            && (type1.getInt("DATA_TYPE") == type2.getInt("DATA_TYPE"))
                            // constraints
                            && (type1.getInt("COLUMN_SIZE") == type2.getInt("COLUMN_SIZE"));
                }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkId(String tableName, String columnName) {
        try {
            ResultSet resultSet = metadata.getPrimaryKeys(catalog, schema, tableName.toUpperCase());
            while (resultSet.next())
                if (resultSet.getString("COLUMN_NAME").equals(columnName.toUpperCase()))
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
            return metadata.getTables(catalog, schema, tableName.toUpperCase(), null).next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
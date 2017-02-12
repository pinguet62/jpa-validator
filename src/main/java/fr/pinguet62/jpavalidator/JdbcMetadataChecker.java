package fr.pinguet62.jpavalidator;

import static java.sql.DatabaseMetaData.columnNoNulls;
import static java.sql.DatabaseMetaData.columnNullable;
import static java.sql.DatabaseMetaData.columnNullableUnknown;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Visitor pattern used to check all types of JPA constraints: PK, FK, length, numeric, ...
 * <p>
 * Return {@code true} if is valid, {@code false} otherwise.<br>
 * Throw {@link NotImplementedException} if not implemented.
 */
public abstract class JdbcMetadataChecker {

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

    protected final String catalog;

    protected final Connection connection;

    private final DatabaseMetaData metadata;

    protected final String schema;

    public JdbcMetadataChecker(String databaseUrl) throws SQLException {
        connection = DriverManager.getConnection(databaseUrl);
        catalog = connection.getCatalog();
        schema = connection.getSchema();
        metadata = connection.getMetaData();
    }

    /** @see GeneratedValue */
    public boolean checkAutoIncrement(String tableName, String columnName, boolean autoIncrement) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toUpperCase(), columnName.toUpperCase());
            return resultSet.next() && resultSet.getString("IS_AUTOINCREMENT").equals(convertBoolean(autoIncrement));
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /** @param length {@link Column#length()} */
    public boolean checkCharacter(String tableName, String columnName, int length) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toUpperCase(), columnName.toUpperCase());
            return resultSet.next() && resultSet.getInt("COLUMN_SIZE") == length;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * @param columnName {@link Column#name()}
     * @param nullable {@link Column#nullable()}
     */
    public boolean checkColumn(String tableName, String columnName, boolean nullable) {
        try {
            print(metadata.getPrimaryKeys(catalog, schema, tableName.toUpperCase()));

            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toUpperCase(), columnName.toUpperCase());
            if (!resultSet.next())
                return false;
            if (resultSet.getInt("NULLABLE") == columnNullableUnknown)
                throw new RuntimeException("???");
            return resultSet.getInt("NULLABLE") == (nullable ? columnNullable : columnNoNulls);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * @param srcTableName Source {@link Table#name()}
     * @param columnName Source column name, defined by {@link JoinColumn#name()}
     * @param tgtTableName Target {@link Table#name()}
     * @see ManyToOne
     * @see OneToOne
     */
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
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * @param precision {@link Column#precision()}
     * @param scale {@link Column#scale()}
     */
    public boolean checkNumeric(String tableName, String columnName, int precision, int scale) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName.toUpperCase(), columnName.toUpperCase());
            return resultSet.next() && resultSet.getInt("COLUMN_SIZE") == precision
                    && resultSet.getInt("DECIMAL_DIGITS") == scale;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * @see Id
     * @see Generated
     */
    public boolean checkPrimaryKey(String tableName, String columnName) {
        try {
            ResultSet resultSet = metadata.getPrimaryKeys(catalog, schema, tableName.toUpperCase());
            while (resultSet.next())
                if (resultSet.getString("COLUMN_NAME").equals(columnName.toUpperCase()))
                    return true;
            return false;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * @see GenerationType#SEQUENCE
     * @see SequenceGenerator
     */
    public abstract boolean checkSequence(String sequence);

    /** @see Table#name() */
    public boolean checkTable(String tableName) {
        try {
            return metadata.getTables(catalog, schema, tableName.toUpperCase(), null).next();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private String convertBoolean(boolean value) {
        return value ? "YES" : "NO";
    }

}
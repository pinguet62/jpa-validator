package fr.pinguet62.jpavalidator.database;

import static java.sql.DatabaseMetaData.columnNoNulls;
import static java.sql.DatabaseMetaData.columnNullable;
import static java.sql.DatabaseMetaData.columnNullableUnknown;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

import fr.pinguet62.jpavalidator.exception.NotYetImplementedException;

/**
 * Visitor pattern used to check all types of JPA constraints: PK, FK, length, numeric, ...
 * <p>
 * Return {@code true} if is valid, {@code false} otherwise.<br>
 * Throw {@link NotImplementedException} if not implemented.
 */
public abstract class JdbcMetadataChecker {

    public static JdbcMetadataChecker INSTANCE;

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
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName, columnName);
            return resultSet.next() && resultSet.getString("IS_AUTOINCREMENT").equals(convertBoolean(autoIncrement));
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * @see GeneratedValue
     * @see GenerationType#IDENTITY
     * @see SequenceGenerator
     */
    public abstract boolean checkAutoIncrementByIdentity(String tableName, String columnName);

    /**
     * @see GeneratedValue
     * @see GenerationType#SEQUENCE
     * @see SequenceGenerator
     */
    public abstract boolean checkAutoIncrementBySequence(String tableName, String columnName, String sequence);

    /** @param length {@link Column#length()} */
    public boolean checkCharacter(String tableName, String columnName, int length) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName, columnName);
            return resultSet.next() && resultSet.getInt("COLUMN_SIZE") == length;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /** @param nullable {@link Column#nullable()} */
    public boolean checkColumn(String tableName, String columnName, boolean nullable) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName, columnName);
            if (!resultSet.next())
                return false;
            if (resultSet.getInt("NULLABLE") == columnNullableUnknown)
                throw new RuntimeException("???");
            return resultSet.getInt("NULLABLE") == (nullable ? columnNullable : columnNoNulls);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /** @param columnName {@link Column#name()} */
    public boolean checkColumnExists(String tableName, String columnName) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName, columnName);
            return resultSet.next();
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
            ResultSet fkResultSet = metadata.getImportedKeys(catalog, schema, tableName);
            while (fkResultSet.next())
                // FK exists
                if (fkResultSet.getString("FKCOLUMN_NAME").equals(columnName)
                        && fkResultSet.getString("PKTABLE_NAME").equals(tgtTableName)) {
                    // Target PK column name
                    ResultSet targetPkResultSet = metadata.getPrimaryKeys(catalog, schema, tgtTableName);
                    targetPkResultSet.next();
                    String targetColumnName = targetPkResultSet.getString("COLUMN_NAME");
                    // Check type
                    ResultSet type1 = metadata.getColumns(catalog, schema, tableName, columnName);
                    ResultSet type2 = metadata.getColumns(catalog, schema, tgtTableName, targetColumnName);
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

    public abstract boolean checkIsAutoIncrementByAnySequence(String tableName, String columnName);

    /**
     * @param precision {@link Column#precision()}
     * @param scale {@link Column#scale()}
     */
    public boolean checkNumeric(String tableName, String columnName, int precision, int scale) {
        try {
            ResultSet resultSet = metadata.getColumns(catalog, schema, tableName, columnName);
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
            ResultSet resultSet = metadata.getPrimaryKeys(catalog, schema, tableName);
            while (resultSet.next())
                if (resultSet.getString("COLUMN_NAME").equals(columnName))
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
            return metadata.getTables(catalog, schema, tableName, null).next();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public boolean checkType(String tableName, String columnName, String type) {
        throw new NotYetImplementedException();
    }

    private String convertBoolean(boolean value) {
        return value ? "YES" : "NO";
    }

}
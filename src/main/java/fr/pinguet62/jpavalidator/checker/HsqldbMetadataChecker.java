package fr.pinguet62.jpavalidator.checker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.pinguet62.jpavalidator.SQLRuntimeException;

/** Extension for <b>HSQLDB</b> SGBD. */
public class HsqldbMetadataChecker extends JdbcMetadataChecker {

    public HsqldbMetadataChecker(String databaseUrl) throws SQLException {
        super(databaseUrl);
    }

    @Override
    public boolean checkAutoIncrementByIdentity(String tableName, String columnName) {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM information_schema.COLUMNS where TABLE_CATALOG=? and TABLE_SCHEMA=? and TABLE_NAME=? and COLUMN_NAME=?");
            int param = 1;
            query.setString(param++, catalog);
            query.setString(param++, schema);
            query.setString(param++, tableName);
            query.setString(param++, columnName);
            ResultSet rs = query.executeQuery();
            return rs.next() && rs.getString("IS_IDENTITY").equals("YES");
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public boolean checkAutoIncrementBySequence(String tableName, String columnName, String sequence) {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM information_schema.SYSTEM_COLUMN_SEQUENCE_USAGE where TABLE_CATALOG=? and TABLE_SCHEMA=? and TABLE_NAME=? and COLUMN_NAME=? and SEQUENCE_NAME=?");
            int param = 1;
            query.setString(param++, catalog);
            query.setString(param++, schema);
            query.setString(param++, tableName);
            query.setString(param++, columnName);
            query.setString(param++, sequence);
            ResultSet rs = query.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public boolean checkIsAutoIncrementByAnySequence(String tableName, String columnName) {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM information_schema.SYSTEM_COLUMN_SEQUENCE_USAGE where TABLE_CATALOG=? and TABLE_SCHEMA=? and TABLE_NAME=? and COLUMN_NAME=?");
            int param = 1;
            query.setString(param++, catalog);
            query.setString(param++, schema);
            query.setString(param++, tableName);
            query.setString(param++, columnName);
            ResultSet rs = query.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public boolean checkSequence(String sequenceName) {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM information_schema.system_sequences where SEQUENCE_CATALOG=? and SEQUENCE_SCHEMA=? and SEQUENCE_NAME=?");
            int param = 1;
            query.setString(param++, catalog);
            query.setString(param++, schema);
            query.setString(param++, sequenceName);
            ResultSet rs = query.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

}
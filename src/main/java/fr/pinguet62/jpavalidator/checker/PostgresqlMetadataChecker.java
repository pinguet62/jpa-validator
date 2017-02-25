package fr.pinguet62.jpavalidator.checker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.pinguet62.jpavalidator.SQLRuntimeException;

/** Extension for <b>PostgreSQL</b> SGBD. */
public class PostgresqlMetadataChecker /*extends JdbcMetadataChecker*/ {
/*
    public PostgresqlMetadataChecker(String databaseUrl) throws SQLException {
        super(databaseUrl);
    }

    @Override
    public boolean checkSequence(String sequenceName) {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM information_schema.sequences where SEQUENCE_CATALOG and SEQUENCE_SCHEMA and SEQUENCE_NAME");
            query.setString(1, catalog);
            query.setString(2, schema);
            query.setString(3, sequenceName);
            ResultSet rs = query.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
*/
}
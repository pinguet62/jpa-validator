package fr.pinguet62.jpavalidator.checker;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Visitor pattern used to check all types of JPA constraints: PK, FK, length, numeric, ...
 * <p>
 * Return {@code true} if is valid, {@code false} otherwise.<br>
 * Throw {@link NotImplementedException} if not implemented.
 */
public abstract class Checker {

    public static class NotImplementedException extends RuntimeException {
        private static final long serialVersionUID = 1;
    }

    /** @param length {@link Column#length()} */
    public boolean checkCharacter(String tableName, String columnName, int length) {
        throw new NotImplementedException();
    }

    /**
     * @param columnName {@link Column#name()}
     * @param nullable {@link Column#nullable()}
     */
    public boolean checkColumn(String tableName, String columnName, boolean nullable) {
        throw new NotImplementedException();
    }

    /**
     * @param srcTableName Source {@link Table#name()}
     * @param columnName Source column name, defined by {@link JoinColumn#name()}
     * @param tgtTableName Target {@link Table#name()}
     * @see ManyToOne
     * @see OneToOne
     */
    public boolean checkForeignKey(String tableName, String columnName, String tgtTableName) {
        throw new NotImplementedException();
    }

    /**
     * @see Id
     * @see Generated
     */
    public boolean checkId(String tableName, String columnName) {
        throw new NotImplementedException();
    }

    /**
     * @param precision {@link Column#precision()}
     * @param scale {@link Column#scale()}
     */
    public boolean checkNumeric(String tableName, String columnName, int precision, int scale) {
        throw new NotImplementedException();
    }

    /** @see Table#name() */
    public boolean checkTable(String name) {
        throw new NotImplementedException();
    }

}
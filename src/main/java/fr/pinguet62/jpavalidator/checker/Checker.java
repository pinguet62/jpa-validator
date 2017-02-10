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
public interface Checker {

    /** @param length {@link Column#length()} */
    boolean checkCharacter(String tableName, String columnName, int length);

    /**
     * @param columnName {@link Column#name()}
     * @param nullable {@link Column#nullable()}
     */
    boolean checkColumn(String tableName, String columnName, boolean nullable);

    /**
     * @param srcTableName Source {@link Table#name()}
     * @param columnName Source column name, defined by {@link JoinColumn#name()}
     * @param tgtTableName Target {@link Table#name()}
     * @see ManyToOne
     * @see OneToOne
     */
    boolean checkForeignKey(String tableName, String columnName, String tgtTableName);

    /**
     * @see Id
     * @see Generated
     */
    boolean checkId(String tableName, String columnName);

    /**
     * @param precision {@link Column#precision()}
     * @param scale {@link Column#scale()}
     */
    boolean checkNumeric(String tableName, String columnName, int precision, int scale);

    /** @see Table#name() */
    boolean checkTable(String name);

}
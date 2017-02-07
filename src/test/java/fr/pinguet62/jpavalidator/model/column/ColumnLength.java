package fr.pinguet62.jpavalidator.model.column;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_LENGTH")
public class ColumnLength {

    @Column(name = "invalid", length = 1)
    private String invalid;

    @Column(name = "ok", length = 99)
    private String ok;

}
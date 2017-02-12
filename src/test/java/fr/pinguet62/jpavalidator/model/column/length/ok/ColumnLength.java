package fr.pinguet62.jpavalidator.model.column.length.ok;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_LENGTH")
public class ColumnLength {

    @Column(name = "ok", length = 99)
    String ok;

}
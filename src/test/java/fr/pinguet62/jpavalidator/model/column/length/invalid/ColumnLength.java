package fr.pinguet62.jpavalidator.model.column.length.invalid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_LENGTH")
public class ColumnLength {

    @Column(name = "invalid", length = 1)
    String invalid;

}
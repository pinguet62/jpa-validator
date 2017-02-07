package fr.pinguet62.jpavalidator.model.column;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_LENGTH")
public class ColumnNumeric {

    @Column(name = "invalid", precision = 99, scale = 44)
    private float invalid;

    @Column(name = "decimal", precision = 5, scale = 2)
    private float ok;

}
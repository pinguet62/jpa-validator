package fr.pinguet62.jpavalidator.model.column.numeric.invalid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_NUMERIC")
public class ColumnNumeric {

    @Column(name = "invalid", precision = 99, scale = 44)
    float invalid;

}
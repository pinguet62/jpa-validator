package fr.pinguet62.jpavalidator.model.column.numeric.ok;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_NUMERIC")
public class ColumnNumeric {

    @Column(name = "ok", precision = 5, scale = 2)
    float ok;

}
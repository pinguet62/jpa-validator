package fr.pinguet62.jpavalidator.model.column.nullable.invalid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_NULLABLE")
public class ColumnNullable {

    @Column(name = "mandatory_error", nullable = true)
    Integer mandatoryError;

    @Column(name = "optional_error", nullable = false)
    Integer optionalError;

}
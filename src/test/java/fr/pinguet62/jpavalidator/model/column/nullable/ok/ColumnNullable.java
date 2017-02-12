package fr.pinguet62.jpavalidator.model.column.nullable.ok;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_NULLABLE")
public class ColumnNullable {

    @Column(name = "mandatory_ok", nullable = false)
    Integer mandatoryOk;

    @Column(name = "optional_ok", nullable = true)
    Integer optionalOk;

}
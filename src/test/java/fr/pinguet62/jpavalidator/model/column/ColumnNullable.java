package fr.pinguet62.jpavalidator.model.column;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COLUMN_NULLABLE")
public class ColumnNullable {

    @Column(name = "mandatory_error", nullable = true)
    private Integer mandatoryError;

    @Column(name = "mandatory_ok", nullable = false)
    private Integer mandatoryOk;

    @Column(name = "optional_error", nullable = false)
    private Integer optionalError;

    @Column(name = "optional_ok", nullable = true)
    private Integer optionalOk;

}
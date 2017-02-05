package fr.pinguet62.jpavalidator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DATA_TYPE")
public class DataType {

    @Column(name = "DECIMAL", precision = 5, scale = 2)
    private Long decimal;

    @Column(name = "MANDATORY", nullable = false)
    private int mandatory;

    @Column(name = "OPTIONAL", nullable = true)
    private int optional;

    @Column(name = "STRING", length = 42)
    private String str;

}
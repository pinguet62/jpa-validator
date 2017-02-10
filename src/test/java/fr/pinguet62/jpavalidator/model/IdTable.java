package fr.pinguet62.jpavalidator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ID_TABLE")
public class IdTable {

    @Id
    @Column(name = "not_pk", nullable = false)
    private int notPk;

    @Id
    @Column(name = "pk", nullable = false)
    private int pk;

}
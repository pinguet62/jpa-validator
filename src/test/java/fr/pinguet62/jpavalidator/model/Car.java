package fr.pinguet62.jpavalidator.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class Car {

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;

    @Id
    @Column(name = "ID_C", nullable = false)
    private Integer id;

}
package fr.pinguet62.jpavalidator.model.manytoone.ok;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MANYTOONE_CAR")
public class Car {

    @ManyToOne
    @JoinColumn(name = "ok")
    Person person;

}
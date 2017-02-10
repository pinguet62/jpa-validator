package fr.pinguet62.jpavalidator.model.manytoone;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MANYTOONE_CAR")
public class Car {

    @ManyToOne
    @JoinColumn(name = "invalid_target")
    private Person invalidTarget;

    @ManyToOne
    @JoinColumn(name = "invalid_type")
    private Person invalidType;

    @ManyToOne
    @JoinColumn(name = "ok")
    private Person ok;

}
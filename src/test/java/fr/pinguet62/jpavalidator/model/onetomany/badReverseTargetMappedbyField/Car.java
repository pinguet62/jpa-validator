package fr.pinguet62.jpavalidator.model.onetomany.badReverseTargetMappedbyField;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ONETOMANY_CAR")
public class Car {

    @ManyToOne
    @JoinColumn(name = "person_id")
    String person;

}